package com.berd.dev.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ScheduledFuture;

/**
 * Composant Spring qui exécute des backups PostgreSQL périodiques sans bloquer
 * le thread principal.
 * Les paramètres sont lus depuis `application.properties` :
 * - project.backup.enabled (true|false)
 * - project.backup.interval (ms)
 * - project.backup.path
 * - project.backup.keep (nombre de fichiers à garder)
 *
 * et les credentials depuis spring.datasource.*
 */
@Component
@ConditionalOnProperty(prefix = "project.backup", name = "enabled", havingValue = "true", matchIfMissing = false)
public class PostgresBackupManager implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(PostgresBackupManager.class);

    @Value("${project.backup.interval:3600000}")
    private long intervalMs;

    @Value("${project.backup.path:./backup}")
    private String backupPath;

    @Value("${project.backup.keep:10}")
    private int keep;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:postgres}")
    private String dbUser;

    @Value("${spring.datasource.password:}")
    private String dbPass;

    private ThreadPoolTaskScheduler scheduler;
    private ScheduledFuture<?> scheduledFuture;

    @Override
    public void afterPropertiesSet() {
        // Prépare le dossier
        File dir = new File(backupPath);
        if (!dir.exists()) {
            boolean ok = dir.mkdirs();
            if (!ok)
                log.warn("Impossible de créer le dossier de backup : {}", backupPath);
        }

        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("backup-scheduler-");
        scheduler.initialize();

        // Schedule at fixed rate (interval en ms)
        scheduledFuture = scheduler.scheduleAtFixedRate(this::runBackup, intervalMs);
        log.info("Backup automatique activé : interval={}ms, path={}, keep={}", intervalMs, backupPath, keep);
    }

    private void runBackup() {
        try {
            log.info("Démarrage d'un backup PostgreSQL");
            ParsedJdbc parsed = parseJdbcUrl(datasourceUrl);
            String result = executeBackup(parsed.host, parsed.port, dbUser, dbPass, parsed.database, backupPath);
            if (result != null) {
                cleanOldBackups(backupPath, keep);
            }
        } catch (Exception e) {
            log.error("Erreur pendant le backup : {}", e.getMessage(), e);
        }
    }

    private static class ParsedJdbc {
        String host;
        String port;
        String database;
    }

    private ParsedJdbc parseJdbcUrl(String jdbcUrl) {
        ParsedJdbc p = new ParsedJdbc();
        try {
            // Exemple : jdbc:postgresql://host:5432/dbname?params
            String withoutPrefix = jdbcUrl.replaceFirst("^jdbc:postgresql:", "");
            URI uri = new URI(withoutPrefix);
            p.host = uri.getHost() == null ? "localhost" : uri.getHost();
            p.port = (uri.getPort() == -1) ? "5432" : Integer.toString(uri.getPort());
            String path = uri.getPath();
            if (path != null && path.length() > 1)
                p.database = path.substring(1);
            else
                p.database = "postgres";
        } catch (Exception ex) {
            log.warn("Impossible de parser spring.datasource.url='{}'. Utilisation des valeurs par défaut.", jdbcUrl);
            p.host = "localhost";
            p.port = "5432";
            p.database = "postgres";
        }
        return p;
    }

    private String executeBackup(String host, String port, String user, String pass, String db, String dir) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File file = new File(dir, "backup_" + db + "_" + ts + ".dump");

        String cmd = String.format("pg_dump -h %s -p %s -U %s -F c -f %s %s", host, port, user, file.getAbsolutePath(),
                db);

        ProcessBuilder pb;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd.exe", "/c", cmd);
        } else {
            pb = new ProcessBuilder("bash", "-c", cmd);
        }

        if (pass != null)
            pb.environment().put("PGPASSWORD", pass);

        try {
            Process p = pb.start();
            int code = p.waitFor();
            if (code == 0) {
                log.info("Backup créé : {}", file.getName());
                return file.getAbsolutePath();
            } else {
                log.error("pg_dump retourné code={} pour la commande: {}", code, cmd);
            }
        } catch (Exception e) {
            log.error("Échec backup : {}", e.getMessage(), e);
        }
        return null;
    }

    private void cleanOldBackups(String dirPath, int maxToKeep) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dump"));

        if (files != null && files.length > maxToKeep) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            int toDelete = files.length - maxToKeep;
            for (int i = 0; i < toDelete; i++) {
                if (files[i].delete()) {
                    log.info("Ancien backup supprimé : {}", files[i].getName());
                } else {
                    log.warn("Impossible de supprimer l'ancien backup : {}", files[i].getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void destroy() {
        if (scheduledFuture != null)
            scheduledFuture.cancel(true);
        if (scheduler != null)
            scheduler.shutdown();
        log.info("Scheduler de backup arrêté");
    }
}

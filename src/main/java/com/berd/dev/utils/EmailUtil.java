package com.berd.dev.utils;

public class EmailUtil {
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email; // Ou lever une exception selon votre besoin
        }

        int atIndex = email.lastIndexOf("@");
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        // 1. Masquage de la partie locale
        String maskedLocal;
        if (localPart.length() <= 1) {
            maskedLocal = "*";
        } else if (localPart.length() == 2) {
            maskedLocal = localPart.charAt(0) + "*";
        } else {
            // Affiche le premier et le dernier caractère, 3 étoiles au milieu
            maskedLocal = localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1);
        }

        // 2. Masquage du domaine (optionnel, mais plus robuste pour la vie privée)
        int lastDotIndex = domainPart.lastIndexOf(".");
        String maskedDomain;
        
        if (lastDotIndex > 1) {
            String domainName = domainPart.substring(0, lastDotIndex);
            String extension = domainPart.substring(lastDotIndex); // ex: .com ou .co.uk
            
            // Masque le nom du domaine mais garde l'extension
            maskedDomain = domainName.charAt(0) + "***" + domainName.charAt(domainName.length() - 1) + extension;
        } else {
            // Si le domaine est bizarre (ex: localhost), on ne masque rien ou tout
            maskedDomain = domainPart;
        }

        return maskedLocal + "@" + maskedDomain;
    }
}

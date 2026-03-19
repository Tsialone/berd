package com.berd.dev.services;
import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void envoyerEmail(String vers, String sujet, String contenu) {
        String url = "https://api.brevo.com/v3/smtp/email";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // JSON simple pour Brevo
            String jsonBody = String.format(
                "{\"sender\":{\"email\":\"tsialone1902@gmail.com\"},\"to\":[{\"email\":\"%s\"}],\"subject\":\"%s\",\"htmlContent\":\"%s\"}",
                vers, sujet, contenu
            );

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            restTemplate.postForEntity(url, entity, String.class);
            
            System.out.println("Email envoyé via API Brevo !");
        } catch (Exception e) {
            System.err.println("Erreur Render/Brevo : " + e.getMessage());
        }
    }
}



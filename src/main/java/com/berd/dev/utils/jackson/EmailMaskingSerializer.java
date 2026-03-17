package com.berd.dev.utils.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EmailMaskingSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(maskEmail(value));
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (localPart.isEmpty()) {
            return "*****" + domainPart;
        }

        if (localPart.length() == 1) {
            return localPart.charAt(0) + "*****" + domainPart;
        }

        return localPart.charAt(0) + "*****" + localPart.charAt(localPart.length() - 1) + domainPart;
    }
}

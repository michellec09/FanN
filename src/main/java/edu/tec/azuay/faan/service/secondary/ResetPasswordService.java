package edu.tec.azuay.faan.service.secondary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class ResetPasswordService {

    @Value("${mail.recovery.token-length}")
    private int TOKEN_LENGTH;

    @Value("${mail.recovery.token-expiration-time-milis}")
    private long EXPIRATION_TIME_MILLIS;

    public String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public Date calculateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME_MILLIS);
    }
}

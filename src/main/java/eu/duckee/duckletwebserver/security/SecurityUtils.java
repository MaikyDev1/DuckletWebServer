package eu.duckee.duckletwebserver.security;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtils {

    private static final SecureRandom random = new SecureRandom();

    static public String generateSession() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}

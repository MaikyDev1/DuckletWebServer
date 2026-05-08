package eu.duckee.duckletwebserver.exchange;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public record Cookie(
        String name,
        String value,
        String domain,
        String path,
        Instant expiresAt,
        boolean secure,
        boolean httpOnly
) {

    public static Cookie fromHeader(String header) {
        String[] parts = header.split(";");
        String[] nameValue = parts[0].trim().split("=", 2);

        String name = nameValue[0].trim();
        String value = nameValue.length > 1 ? nameValue[1].trim() : "";

        Map<String, String> attrs = new HashMap<>();
        boolean secure = false;
        boolean httpOnly = false;

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim();

            if (part.equalsIgnoreCase("Secure")) {
                secure = true;
            } else if (part.equalsIgnoreCase("HttpOnly")) {
                httpOnly = true;
            } else if (part.contains("=")) {
                String[] kv = part.split("=", 2);
                attrs.put(kv[0].trim().toLowerCase(), kv[1].trim());
            }
        }

        return new Cookie(
                name,
                value,
                attrs.get("domain"),
                attrs.get("path"),
                null,
                secure,
                httpOnly
        );
    }

    public String toHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);

        if (domain != null)
            sb.append("; Domain=").append(domain);

        if (path != null)
            sb.append("; Path=").append(path);

        if (expiresAt != null) {
            String httpDate = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(expiresAt);
            sb.append("; Expires=").append(httpDate);
        }

        if (secure)
            sb.append("; Secure");

        if (httpOnly)
            sb.append("; HttpOnly");

        return sb.toString();
    }

}

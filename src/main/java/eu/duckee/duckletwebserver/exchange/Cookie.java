package eu.duckee.duckletwebserver.exchange;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record Cookie(
        String name,
        String value,
        String domain,
        String path,
        Instant expiresAt,
        boolean secure,
        boolean httpOnly,
        String sameSite
) {

    public static List<Cookie> fromCookieHeader(String header) {

        List<Cookie> cookies = new ArrayList<>();

        for (String part : header.split(";")) {
            part = part.trim();

            if (part.isEmpty()) continue;

            String[] kv = part.split("=", 2);

            String name = kv[0].trim();
            String value = kv.length > 1 ? kv[1].trim() : "";

            cookies.add(new Cookie(name, value, null, null, null, false, false, null));
        }

        return cookies;
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

        if (sameSite != null)
            sb.append("; SameSite=").append(sameSite);

        return sb.toString();
    }

}

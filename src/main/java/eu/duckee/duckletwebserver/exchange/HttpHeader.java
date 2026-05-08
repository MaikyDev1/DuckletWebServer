package eu.duckee.duckletwebserver.exchange;

public record HttpHeader(String name, String value) {

    public HttpHeader {
        name = name.trim();
        value = value.trim();
    }

    public String toHeader() {
        return name + ": " + value;
    }

    public static HttpHeader from(String raw) {
        String[] parts = raw.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid header: " + raw);
        }

        return new HttpHeader(parts[0], parts[1]);
    }
}

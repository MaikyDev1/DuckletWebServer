package eu.duckee.duckletwebserver.exchange;

import lombok.Getter;

@Getter
public enum ResponseType {
    JSON("application/json"),
    TEXT("text/plain"),
    FILE("application/octet-stream");

    private final String contentType;

    ResponseType(String contentType) {
        this.contentType = contentType;
    }

}

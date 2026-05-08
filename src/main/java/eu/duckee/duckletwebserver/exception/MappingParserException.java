package eu.duckee.duckletwebserver.exception;

public class MappingParserException extends RuntimeException {
    public MappingParserException(String message) {
        super(message + " | Please use this url format: \"/api/endpoing\"");
    }
}

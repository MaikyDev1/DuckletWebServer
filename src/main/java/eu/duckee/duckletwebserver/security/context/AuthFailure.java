package eu.duckee.duckletwebserver.security.context;

public record AuthFailure(String reason) implements AuthResult {}

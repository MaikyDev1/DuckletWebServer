package eu.duckee.duckletwebserver.security.context;

public record AuthSuccess (AuthContext context) implements AuthResult {}

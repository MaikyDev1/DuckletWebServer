package eu.duckee.duckletwebserver.security.context;

public record AuthSuccess<T> (AuthContext<T> context) implements AuthResult {}

package eu.duckee.duckletwebserver.security.context;

public sealed interface AuthResult permits AuthSuccess, AuthFailure {}
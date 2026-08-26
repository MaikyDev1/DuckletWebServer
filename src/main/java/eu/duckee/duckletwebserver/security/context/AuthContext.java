package eu.duckee.duckletwebserver.security.context;

import eu.duckee.duckletwebserver.security.AuthType;

public record AuthContext(
        Object identity,
        AuthType type
) {}

package eu.duckee.duckletwebserver.security.context;

import eu.duckee.duckletwebserver.security.AuthType;

public record AuthContext<T>(
        String userId,
        AuthType type,
        T identity
) {}

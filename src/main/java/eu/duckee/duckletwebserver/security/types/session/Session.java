package eu.duckee.duckletwebserver.security.types.session;

import java.time.Instant;

public record Session(
        String session,
        Object identity,
        Instant createdAt,
        Instant expiresAt
        ) {
}

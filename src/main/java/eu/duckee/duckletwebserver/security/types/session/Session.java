package eu.duckee.duckletwebserver.security.types.session;

import java.time.Instant;

public record Session(
        String session,
        String user,
        Instant createdAt,
        Instant expiresAt
        ) {
}

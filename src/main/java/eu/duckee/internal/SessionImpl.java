package eu.duckee.internal;

import eu.duckee.duckletwebserver.security.SecurityUtils;
import eu.duckee.duckletwebserver.security.types.session.Session;
import eu.duckee.duckletwebserver.security.types.session.SessionImplementation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionImpl implements SessionImplementation {

    ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Optional<Session> authenticate(String session) {
        Session s = sessions.get(session);
        if (s == null || s.session() == null)
            return Optional.empty();
        // check times
        if (s.expiresAt().isBefore(Instant.now())) {
            sessions.remove(session);
            return Optional.empty();
        }
        return Optional.of(s);
    }

    @Override
    public Optional<Session> login(String username, String password) {
        if (username.equalsIgnoreCase("admin") &&
            password.equalsIgnoreCase("password")) {
            Session session = new Session(
                    SecurityUtils.generateSession(),
                    username,
                    Instant.now(),
                    Instant.now().plus(30, ChronoUnit.DAYS)
            );
            sessions.put(session.session(), session);
            return Optional.of(session);
        }
        return Optional.empty();
    }
}

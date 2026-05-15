package eu.duckee.duckletwebserver.security.types.session;

import java.util.Optional;

public interface SessionImplementation {

    Optional<Session> authenticate(String session);
    Optional<Session> login(String username, String password);

}

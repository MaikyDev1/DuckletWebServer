package eu.duckee.duckletwebserver.annotations.auth.flows;

import net.maikydev.nestserver.ducket.annotations.auth.AuthClient;

import java.util.Optional;

public interface TokenAuthentification {

    Optional<AuthClient> getClient(String token);

}

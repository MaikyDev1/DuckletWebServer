package eu.duckee.duckletwebserver.security;

import eu.duckee.duckletwebserver.exchange.DuckletRequest;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import eu.duckee.duckletwebserver.security.context.AuthResult;
import eu.duckee.duckletwebserver.security.login_details.LoginDetails;

public interface SecurityTrail {

    AuthResult authenticate(DuckletRequest request);

    /**
     * This function will process a response. And if the user implementation will
     * provide a valid session, the response will add the desired cookie.
     * @param response the response
     * @param details some details provided by the backend
     * @return
     */
    DuckletResponse login(DuckletResponse response, LoginDetails details);
    AuthResult unAuthenticate(DuckletRequest request);

}

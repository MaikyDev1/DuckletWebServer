package eu.duckee.duckletwebserver.security;

import eu.duckee.duckletwebserver.exchange.DuckletRequest;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import eu.duckee.duckletwebserver.security.context.AuthResult;
import eu.duckee.duckletwebserver.security.login_details.LoginDetails;

public interface SecurityTrail {

    AuthResult authenticate(DuckletRequest request);

    /**
     * This function will process a response. And if the user implementation will
     * provide a valid session, the response will be modified based on the auth type
     * @param response the response
     * @param details the login details provided by the user
     * @return null if the login failed or a Response if the login is a success
     * YOU MUST return the login result to the route return.
     */
    DuckletResponse login(DuckletResponse response, LoginDetails details);
    void unAuthenticate(DuckletRequest request);

}

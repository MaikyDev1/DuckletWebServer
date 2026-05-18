package eu.duckee.duckletwebserver.security.types.session;

import eu.duckee.duckletwebserver.exchange.Cookie;
import eu.duckee.duckletwebserver.exchange.DuckletRequest;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import eu.duckee.duckletwebserver.security.AuthType;
import eu.duckee.duckletwebserver.security.SecurityTrail;
import eu.duckee.duckletwebserver.security.context.AuthContext;
import eu.duckee.duckletwebserver.security.context.AuthFailure;
import eu.duckee.duckletwebserver.security.context.AuthResult;
import eu.duckee.duckletwebserver.security.context.AuthSuccess;
import eu.duckee.duckletwebserver.security.login_details.LoginDetails;
import eu.duckee.duckletwebserver.security.login_details.SessionLoginDetails;

import java.util.Optional;

public class SessionAuth implements SecurityTrail {

    private final SessionImplementation sessionImplementation;

    public SessionAuth(SessionImplementation sessionImplementation) {
        this.sessionImplementation = sessionImplementation;
    }

    @Override
    public AuthResult authenticate(DuckletRequest request) {
        Optional<Cookie> cookie = request.getCookie("session");
        if (cookie.isEmpty())
            return new AuthFailure("No cookie");
        Optional<Session> optionalSession = sessionImplementation.authenticate(cookie.get().value());
        if (optionalSession.isEmpty()) {
            return new AuthFailure("Auth failed!");
        }
        Session session = optionalSession.get();
        return new AuthSuccess<>(
                new AuthContext<String>(session.user(), AuthType.SESSION, null)
        );
    }

    @Override
    public DuckletResponse login(DuckletResponse response, LoginDetails details) {
        if (!(details instanceof SessionLoginDetails(String username, String password)))
            return null;
        Optional<Session> session = sessionImplementation.login(username, password);
        if (session.isEmpty())
            return null;
        response.addCookie(new Cookie(
                "session", session.get().session(),
                null, null, session.get().expiresAt(),
                false, true, "Lax"
        ));
        return response;
    }

    @Override
    public AuthResult unAuthenticate(DuckletRequest request) {
        return null;
    }

}

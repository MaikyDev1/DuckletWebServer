package eu.duckee.internal;

import eu.duckee.duckletwebserver.annotations.auth.Authentication;
import eu.duckee.duckletwebserver.annotations.auth.InjectSecurity;
import eu.duckee.duckletwebserver.annotations.http_types.GetRequest;
import eu.duckee.duckletwebserver.annotations.http_types.PostRequest;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.annotations.request.RequestParam;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import eu.duckee.duckletwebserver.security.SecurityTrail;
import eu.duckee.duckletwebserver.security.context.AuthContext;
import eu.duckee.duckletwebserver.security.login_details.SessionLoginDetails;
import eu.duckee.duckletwebserver.security.types.session.Session;

@RequestMapping("/account")
public class AuthRoute {

    @InjectSecurity
    SecurityTrail authentification;

    @PostRequest
    @RequestMapping("/register")
    public DuckletResponse register() {
        return DuckletResponse.ok();
    }

    @PostRequest
    @RequestMapping("/login")
    public DuckletResponse login(@RequestParam("username") String username, @RequestParam("password") String password) {
        DuckletResponse authResponse = authentification.login(DuckletResponse.ok(), new SessionLoginDetails(
                username, password
        ));
        if (authResponse == null)
            return DuckletResponse.notFound().sendText("User or password not found.");
        return authResponse;
    }

    @GetRequest
    @RequestMapping("/")
    public DuckletResponse account(@Authentication AuthContext<Session> user) {
        if (user == null) {
            return DuckletResponse.badRequest().sendText("Not Logged in");
        }
        System.out.println(user.userId());
        return DuckletResponse.ok().sendText(user.userId());
    }

}

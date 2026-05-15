package eu.duckee;

import eu.duckee.duckletwebserver.DuckletController;
import eu.duckee.duckletwebserver.security.SecurityTrail;
import eu.duckee.duckletwebserver.security.types.session.SessionAuth;
import eu.duckee.internal.AuthRoute;
import eu.duckee.internal.ExampleRoute;
import eu.duckee.internal.SessionImpl;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        DuckletController controller = DuckletController.createController(8080, 10);
        SecurityTrail trail = new SessionAuth(new SessionImpl());
        controller.useAuthentification(trail);
        controller.addRoute(new AuthRoute());
        controller.addRoute(new ExampleRoute());
        controller.startController();
    }
}

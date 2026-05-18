package eu.duckee.duckletwebserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.exception.DuckletHandlerException;
import eu.duckee.duckletwebserver.security.SecurityTrail;
import eu.duckee.duckletwebserver.security.types.session.SessionAuth;
import eu.duckee.duckletwebserver.security.types.session.SessionConfig;
import eu.duckee.duckletwebserver.security.types.session.SessionImplementation;
import eu.duckee.duckletwebserver.utils.SimpleLogger;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class DuckletController implements HttpHandler {

    public static String VERSION = "ALPHA-0.1V";

    private HashMap<String, DuckletHandler> routes;

    public DuckletConfig config = new DuckletConfig() {};

    @Getter
    private SecurityTrail securityTrail;

    private HttpServer server;

    private DuckletController() {};

    /**
     * Create a new DuckletController
     * @param port a port in where it will run
     * @param threads how many threads you need
     * @return a new ducklet controller
     */
    public static DuckletController createController(int port, int threads) {
        SimpleLogger.info("Starting Ducklet Lightweight Web Server. [" + VERSION + "]");
        DuckletController duckletController = new DuckletController();
        duckletController.routes = new HashMap<>();
        try {
            duckletController.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (Exception e) {
            SimpleLogger.error(e.getMessage());
            return null;
        }
        duckletController.server.setExecutor(Executors.newFixedThreadPool(threads));
        duckletController.server.createContext("/", duckletController);
        return duckletController;
    }

    /**
     * Once you made a mapping, the route will be added using this method.
     * @param ducklet an instance of the route
     */
    public void addRoute(Object ducklet) {
        Class<?> clazz = ducklet.getClass();
        if (!clazz.isAnnotationPresent(RequestMapping.class)) {
            SimpleLogger.error("You are trying to map " + clazz.getName() + ", but it has no @RequestMapping. Skipping!");
            return;
            }
        try {
            String mapping = clazz.getAnnotation(RequestMapping.class).value();
            DuckletHandler handler = DuckletHandler.wrapFromRoute(this, ducklet);
            routes.put(mapping, handler);
        } catch (DuckletHandlerException e) {
            SimpleLogger.error(e.getMessage());
        }
    }

    public void useSessionAuthentication(SessionImplementation implementation) {
        this.securityTrail = new SessionAuth(implementation, null);
    }

    public void useSessionAuthentication(SessionImplementation implementation, SessionConfig config) {
        this.securityTrail = new SessionAuth(implementation, config);
    }

    /**
     * When all the web routes are ready you can start the controller.
     * You can start the controller after adding routes or anytime you want.
     */
    public void startController() {
        server.start();
        SimpleLogger.info("Server started on " + server.getAddress() + " with " + routes.size() + " routes.");
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String reqUrl = exchange.getRequestURI().getPath();
            for (String mapping : routes.keySet()) {
                String hasNext = matchWithMapping(mapping, reqUrl);
                if (hasNext == null)
                    continue;
                routes.get(mapping).tryAndHandle(exchange, hasNext);
                return;
            }
            config.notFound().respond(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal function that will match a request url with a mapping
     * @return A string that is the rest after the match or null;
     */
    private String matchWithMapping(String mapping, String request) {
        String requestUrl = request + "/";
        for (int i = 0; i < mapping.length(); i++) {
            if (requestUrl.charAt(i) != mapping.charAt(i))
                return null;
        }
        return requestUrl.substring(mapping.length(), requestUrl.length() - 1);
    }

}

package eu.duckee.duckletwebserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.duckee.duckletwebserver.annotations.RequestMapping;
import eu.duckee.duckletwebserver.exception.DuckletHandlerException;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import eu.duckee.duckletwebserver.utils.SimpleLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class DuckletController implements HttpHandler {

    public static String VERSION = "ALPHA-0.1V";

    private HashMap<String, DuckletHandler> routes;

    private DuckletResponse notFound;
    private DuckletResponse internalServerError;
    private DuckletResponse notAuthenticated;

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
        duckletController.notFound = DuckletResponse.notFound().sendText("Not found");
        duckletController.internalServerError = DuckletResponse.internalServerError().sendText("Internal server error");
        duckletController.notAuthenticated = DuckletResponse.badRequest().sendText("Bad request");
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
            DuckletHandler handler = DuckletHandler.wrapFromRoute(ducklet);
            routes.put(mapping, handler);
        } catch (DuckletHandlerException e) {
            SimpleLogger.error(e.getMessage());
        }
    }

    public void useAuthentification() {

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
    public void handle(HttpExchange exchange) throws IOException {
        String reqUrl = exchange.getRequestURI().getPath();
        for (String mapping : routes.keySet()) {
            String hasNext = hasNext(mapping, reqUrl);
            if (hasNext != null) {
                try {
                    if (!routes.get(mapping).handle(exchange, hasNext)) {
                        notFound.respond(exchange);
                    }
                    return;
                } catch (IOException | DuckletHandlerException e) {
                    SimpleLogger.error(e.getMessage());
                }
                internalServerError.respond(exchange);
                return;
            }
        }
        notFound.respond(exchange);
        return;
    }

    private String hasNext(String mapping, String request) {
        String requestUrl = request + "/";
        for (int i = 0; i < mapping.length(); i++) {
            if (requestUrl.charAt(i) != mapping.charAt(i))
                return null;
        }
        return requestUrl.substring(mapping.length(), requestUrl.length() - 1);
    }

}

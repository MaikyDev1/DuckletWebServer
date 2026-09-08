package eu.duckee.duckletwebserver;

import com.sun.net.httpserver.HttpExchange;
import eu.duckee.duckletwebserver.annotations.auth.InjectSecurity;
import eu.duckee.duckletwebserver.annotations.auth.AuthenticatedOnly;
import eu.duckee.duckletwebserver.annotations.auth.UnauthenticatedOnly;
import eu.duckee.duckletwebserver.annotations.http_types.HttpMethod;
import eu.duckee.duckletwebserver.annotations.request.RequestMapping;
import eu.duckee.duckletwebserver.exception.DuckletHandlerException;
import eu.duckee.duckletwebserver.exchange.DuckletRequest;
import eu.duckee.duckletwebserver.utils.HttpTools;
import eu.duckee.duckletwebserver.utils.Mapping;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DuckletHandler {

    private final DuckletController controller;

    public DuckletHandler(DuckletController controller) {
        this.controller = controller;
    }

    private boolean requireAuthentification;
    //private String mapping;
    private final List<DuckletEndpoint> methods = new ArrayList<>();

    /**
     * Internal process for creating routes from instances
     * @param object The ducklet instance
     * @return The wrapped handler of the instance
     * @throws DuckletHandlerException Any bad implementation will result in a failer!
     */
    protected DuckletHandler addDucklet(Object object) throws DuckletHandlerException {
        Class<?> clazz = object.getClass();
//        if (!clazz.isAnnotationPresent(RequestMapping.class))
//            throw new DuckletHandlerException("An object with no RequestMapping was provided!");
//        dh.mapping = clazz.getAnnotation(RequestMapping.class).value();
        requireAuthentification = clazz.isAnnotationPresent(AuthenticatedOnly.class);
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectSecurity.class)) {
                field.setAccessible(true);
                try {
                    field.set(object, controller.getSecurityTrail());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (Method method : clazz.getDeclaredMethods())
            addMethod(method, object);
        return this;
    }

    private void addMethod(Method method, Object parent) throws DuckletHandlerException {
        if (!method.isAnnotationPresent(RequestMapping.class))
            return;
        DuckletEndpoint endpoint = new DuckletEndpoint(controller, parent);
        endpoint.setMethod(method);

        if (method.isAnnotationPresent(AuthenticatedOnly.class)) {
            endpoint.setAccessType(AccessType.AUTHENTICATED_ONLY);
        } else if (method.isAnnotationPresent(UnauthenticatedOnly.class)) {
            endpoint.setAccessType(AccessType.UNAUTHENTICATED_ONLY);
        } else {
            endpoint.setAccessType(AccessType.PERMIT_ALL);
        }

        endpoint.setMapping(Mapping.wrapFromString(method.getAnnotation(RequestMapping.class).value()));

        // Method testing
        for (Annotation annotation : method.getAnnotations()) {
            HttpMethod httpMethod = HttpMethod.getMethod(annotation);
            if (httpMethod == null)
                continue;
            endpoint.setHttpMethod(httpMethod);
        }
        if (endpoint.getHttpMethod() == null)
            throw new DuckletHandlerException("Method: " + method.getName() + " from class " + method.getClass() + " has a @RequestMapping but no HttpMethod (Like @GetRequest)!");
        // Why this code? We are adding dinamic routes to the end of the array so we can make custom routes beside dynamic ones!
        if (endpoint.getMapping().isDynamic())
            methods.addLast(endpoint);
        else
            methods.addFirst(endpoint);
    }

    /**
     * We are trying to match a url to this route handler.
     * This function is called only from the controller. We must respond here or later to the controller
     * <b>DON'T</b> let this function finish without a HttpExchange response!
     *
     * @param exchange The http exchange from HttpServer
     * @param url The url from HttpServer
     */
    protected boolean tryAndHandle(HttpExchange exchange, String url) throws IOException, DuckletHandlerException {
        boolean methodNotAllowed = false;
        for (DuckletEndpoint endpoint : methods) {
            Optional<Map<String, String>> mappingOptional = endpoint.getMapping().matchAndExtract(url);
            /* Not a match */
            if (mappingOptional.isEmpty())
                continue;
            /* Matched */
            if (!exchange.getRequestMethod().equalsIgnoreCase(endpoint.getHttpMethod().toString())) {
                methodNotAllowed = true;
                continue;
            }
            return resolveEndpoint(endpoint, mappingOptional.get(), exchange);
        }
        if (methodNotAllowed)
            controller.config.methodNotAllowed().respond(exchange);
        else
            controller.config.notFound().respond(exchange);
        return false;
    }

    private boolean resolveEndpoint(DuckletEndpoint endpoint, Map<String, String> tagLines, HttpExchange exchange) throws IOException {
        DuckletRequest request = DuckletRequest.wrapFromExchange(exchange);
        /* Parse body */
        String body = null;
        InputStream is = exchange.getRequestBody();
        if (is != null) {
            byte[] bytes = is.readAllBytes();
            if (bytes.length > 0) {
                body = new String(bytes, StandardCharsets.UTF_8);
            }
        }
        Map<String, String> query = HttpTools.computeQueryParams(exchange.getRequestURI().getRawQuery());
        request.addTagLines(tagLines).addHttpParams(query).setHttpBody(body);
        endpoint.execute(request).respond(exchange);
        return true;
    }

}

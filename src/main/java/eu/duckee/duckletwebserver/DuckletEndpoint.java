package eu.duckee.duckletwebserver;

import eu.duckee.duckletwebserver.annotations.AnnotationLibrary;
import eu.duckee.duckletwebserver.annotations.http_types.HttpMethod;
import eu.duckee.duckletwebserver.annotations.request.RequestParam;
import eu.duckee.duckletwebserver.annotations.request.RequestUrlParam;
import eu.duckee.duckletwebserver.exception.DuckletHandlerException;
import eu.duckee.duckletwebserver.exchange.DuckletRequest;
import eu.duckee.duckletwebserver.exchange.DuckletResponse;
import eu.duckee.duckletwebserver.security.context.AuthResult;
import eu.duckee.duckletwebserver.security.context.AuthSuccess;
import eu.duckee.duckletwebserver.utils.Mapping;
import lombok.Getter;
import lombok.Setter;
import net.maikydev.duckycore.data.json.DuckyJson;
import net.maikydev.duckycore.data.json.objects.JsonEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

@Getter
@Setter
public class DuckletEndpoint {

    private HttpMethod httpMethod;
    private Mapping mapping;
    private Method method;
    private boolean requireAuthentification;

    private DuckletController controller;

    public DuckletEndpoint(DuckletController controller) {
        this.controller = controller;
    }

    protected DuckletResponse execute(Object parent, DuckletRequest request) throws DuckletHandlerException {
        Parameter[] params = method.getParameters();
        Object[] processedParams = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Annotation annotation = Arrays.stream(param.getAnnotations()).findFirst().orElse(null);
            switch (AnnotationLibrary.getFromAnnotation(annotation)) {
                case AUTHENTIFICATION -> {
                    System.out.println("Authentification req");
                    System.out.println(request.getCookies());
                    System.out.println(request.getCookies().get("session").toHeader());
                    AuthResult authResult = controller.getSecurityTrail().authenticate(request);
                    System.out.println("Session result: " + authResult);
                    if (authResult instanceof AuthSuccess<?>) {
                        AuthSuccess success = ((AuthSuccess) authResult);
                        if (success.context().getClass().getName().equalsIgnoreCase(param.getType().getName())) {
                            processedParams[i] = success.context();
                        } else {
                            processedParams[i] = null;
                        }
                    } else {
                        processedParams[i] = null;
                    }
                }
                case REQUEST_PARAM -> {
                    if (request.getHttpParams() == null) {
                        processedParams[i] = null;
                    } else {
                        String paramValue = param.getAnnotation(RequestParam.class).value();
                        processedParams[i] = request.getHttpParams().getOrDefault(paramValue, null);
                    }
                }
                case REQUEST_BODY -> {
                    if (request.getHttpBody() != null && param.getType() == JsonEntity.class) {
                        try {
                            processedParams[i] = DuckyJson.serialization(request.getHttpBody());
                        } catch (Exception e) {
                            processedParams[i] = null;
                        }
                    } else {
                        processedParams[i] = null;
                    }
                }
                case REQUEST_URL_PARAM -> {
                    if (request.getTagLines() == null) {
                        processedParams[i] = null;
                    } else {
                        String tagLineName = param.getAnnotation(RequestUrlParam.class).value();
                        processedParams[i] = request.getTagLines().getOrDefault(tagLineName, null);
                    }
                }
                case null, default -> {
                    throw new DuckletHandlerException("Under " + method.getName() + " we found some unsupported parameters! Cancelling!");
                }
            }
        }
        try {
            return (DuckletResponse) method.invoke(parent, processedParams);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new DuckletHandlerException("Under " + method.getName() + " we could not invoke the method. " + e.getMessage() + "! Cancelling!");
        }

    }

}


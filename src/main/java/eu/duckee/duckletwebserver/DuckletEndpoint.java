package eu.duckee.duckletwebserver;

import eu.duckee.duckletwebserver.annotations.AnnotationMeta;
import eu.duckee.duckletwebserver.annotations.ParameterAnnotations;
import eu.duckee.duckletwebserver.annotations.http_types.HttpMethod;
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
public class DuckletEndpoint {

    @Setter
    private HttpMethod httpMethod;
    @Setter
    private Mapping mapping;
    private Method method;
    @Setter
    private boolean requireAuthentification;

    private AnnotationMeta cachedParams[];

    private DuckletController controller;

    public DuckletEndpoint(DuckletController controller) {
        this.controller = controller;
    }

    public void setMethod(Method method) {
        this.method = method;
        cacheParameters();
    }

    /**
     * Internal function that will be runed at the start of the object creation
     */
    private void cacheParameters() {
        if (method == null || method.getParameters() == null) return;
        Parameter[] params = method.getParameters();
        cachedParams = new AnnotationMeta[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Annotation annotation = Arrays.stream(param.getAnnotations()).findFirst().orElse(null);
            ParameterAnnotations paramsAnnotation = ParameterAnnotations.getFromAnnotation(annotation);
            if (paramsAnnotation == null)
                throw new DuckletHandlerException("Under " + method.getName() + " we found some unsupported parameters! Cancelling!");
            cachedParams[i] = new AnnotationMeta(
                    paramsAnnotation, paramsAnnotation.extractValue(annotation), param.getType()
            );
        }
    }

    protected DuckletResponse execute(Object parent, DuckletRequest request) throws DuckletHandlerException {
        Object[] processedParams = new Object[cachedParams.length];
        for (int i = 0; i < cachedParams.length; i++) {
            AnnotationMeta meta = cachedParams[i];
            switch (meta.type()) {
                case AUTHENTIFICATION -> {
                    AuthResult authResult = controller.getSecurityTrail().authenticate(request);
                    System.out.println("Session result: " + authResult);
                    if (authResult instanceof AuthSuccess<?>) {
                        AuthSuccess success = ((AuthSuccess) authResult);
                        if (success.context().getClass().getName().equalsIgnoreCase(meta.paramType().getName())) {
                            processedParams[i] = success.context();
                            break;
                        }
                    }
                    processedParams[i] = null;
                }
                case REQUEST_BODY -> {
                    if (request.getHttpBody() == null) {
                        processedParams[i] = null;
                        break;
                    }
                    if (meta.paramType() == JsonEntity.class) {
                        processedParams[i] = DuckyJson.serialization(request.getHttpBody());
                    } else if (meta.paramType() == String.class) {
                        processedParams[i] = request.getHttpBody();
                    } else {
                        processedParams[i] = null;
                    }
                }
                case REQUEST_PARAM -> {
                    if (request.getHttpParams() == null) {
                        processedParams[i] = null;
                    } else {
                        processedParams[i] = request.getHttpParams().getOrDefault(meta.value(), null);
                    }
                }
                case REQUEST_URL_PARAM -> {
                    if (request.getTagLines() == null) {
                        processedParams[i] = null;
                    } else {
                        processedParams[i] = request.getTagLines().getOrDefault(meta.value(), null);
                    }
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


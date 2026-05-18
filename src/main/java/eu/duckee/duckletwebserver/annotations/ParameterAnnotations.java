package eu.duckee.duckletwebserver.annotations;

import eu.duckee.duckletwebserver.annotations.auth.Authentication;
import eu.duckee.duckletwebserver.annotations.request.RequestBody;
import eu.duckee.duckletwebserver.annotations.request.RequestParam;
import eu.duckee.duckletwebserver.annotations.request.RequestUrlParam;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum ParameterAnnotations {

    AUTHENTIFICATION(Authentication.class, a -> null),

    REQUEST_BODY(RequestBody.class, a -> null),

    REQUEST_PARAM(RequestParam.class, a -> ((RequestParam) a).value()),

    REQUEST_URL_PARAM(RequestUrlParam.class, a -> ((RequestUrlParam) a).value());

    private final Class<?> annotationClass;
    private final Function<Annotation, String> extractor;

    ParameterAnnotations(Class<?> clazz, Function<Annotation, String> extractor) {
        this.annotationClass = clazz;
        this.extractor = extractor;
    }

    private static final Map<Class<?>, ParameterAnnotations> LOOKUP = new HashMap<>();

    static {
        for (ParameterAnnotations p : values()) {
            LOOKUP.put(p.annotationClass, p);
        }
    }

    public static ParameterAnnotations getFromAnnotation(Annotation annotation) {
        if (annotation == null) return null;
        return LOOKUP.get(annotation.annotationType());
    }

    public String extractValue(Annotation annotation) {
        return extractor.apply(annotation);
    }
}

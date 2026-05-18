package eu.duckee.duckletwebserver.annotations.http_types;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET(GetRequest.class),
    DELETE(DeleteRequest.class),
    PUT(PutRequest.class),
    PATCH(PutRequest.class),
    POST(PostRequest.class);

    private final Class<?> requestClass;
    HttpMethod(Class<?> clazz) {
        this.requestClass = clazz;
    }

    private static final Map<Class<?>, HttpMethod> LOOKUP = new HashMap<>();

    static {
        for (HttpMethod http : values()) {
            LOOKUP.put(http.requestClass, http);
        }
    }

    public static HttpMethod getMethod(Annotation annotation) {
        if (annotation == null) return null;
        return LOOKUP.get(annotation.annotationType());
    }
}

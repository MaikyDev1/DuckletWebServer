package eu.duckee.duckletwebserver.annotations.http_types;

import java.lang.annotation.Annotation;

public enum HttpMethod {
    GET(GetRequest.class),
    DELETE(DeleteRequest.class),
    POST(PostRequest.class);

    private final Class<?> requestClass;
    HttpMethod(Class<?> clazz) {
        this.requestClass = clazz;
    }

    public static HttpMethod getMethod(Annotation annotation) {
        if (annotation == null)
            return null;
        Class<?> annotationType = annotation.annotationType();
        for (HttpMethod method : values()) {
            if (method.requestClass.equals(annotationType)) {
                return method;
            }
        }
        return null;
    }
}

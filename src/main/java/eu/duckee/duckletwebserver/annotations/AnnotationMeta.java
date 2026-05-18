package eu.duckee.duckletwebserver.annotations;

public record AnnotationMeta (
        ParameterAnnotations type, String value, Class<?> paramType
) {}

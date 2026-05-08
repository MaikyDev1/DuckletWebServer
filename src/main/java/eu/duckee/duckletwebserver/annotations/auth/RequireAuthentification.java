package eu.duckee.duckletwebserver.annotations.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequireAuthentification {

}

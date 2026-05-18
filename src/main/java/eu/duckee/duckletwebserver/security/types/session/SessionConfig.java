package eu.duckee.duckletwebserver.security.types.session;

public interface SessionConfig {

    default boolean secure() {
        return true;
    }

    default boolean httpOnly() {
        return true;
    }

    default String sessionName() {
        return "DSESSION";
    }

    default String sameSite() {
        return "Lax";
    }

    default String domain() {
        return null;
    }

    default String path() {
        return "/";
    }

}

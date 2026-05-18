package eu.duckee.duckletwebserver;

import eu.duckee.duckletwebserver.exchange.DuckletResponse;

public interface DuckletConfig {

    default DuckletResponse notFound() {
        return DuckletResponse.notFound();
    }

    default DuckletResponse internalServerError() {
        return DuckletResponse.internalServerError();
    }

    default DuckletResponse unauthorized() {
        return DuckletResponse.unauthorized();
    }

    default DuckletResponse methodNotAllowed() {
        return DuckletResponse.methodNotAllowed();
    }

    default DuckletResponse forbidden() {
        return DuckletResponse.forbidden();
    }
    
}

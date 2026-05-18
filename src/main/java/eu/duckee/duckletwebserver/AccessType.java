package eu.duckee.duckletwebserver;

import eu.duckee.duckletwebserver.security.context.AuthFailure;
import eu.duckee.duckletwebserver.security.context.AuthResult;
import eu.duckee.duckletwebserver.security.context.AuthSuccess;

public enum AccessType {
    AUTHENTICATED_ONLY,
    UNAUTHENTICATED_ONLY,
    PERMIT_ALL;


    public boolean canAccess(AuthResult result) {
        return switch (this) {
            case AUTHENTICATED_ONLY -> result instanceof AuthSuccess;
            case UNAUTHENTICATED_ONLY -> result instanceof AuthFailure;
            case PERMIT_ALL -> true;
        };
    }

}

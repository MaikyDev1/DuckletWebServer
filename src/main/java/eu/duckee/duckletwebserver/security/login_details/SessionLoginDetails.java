package eu.duckee.duckletwebserver.security.login_details;

public record SessionLoginDetails(
        String username,
        String password
) implements LoginDetails {

}

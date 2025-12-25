package uk.ac.sheffield.com2008_team_27.exceptions;

public class RoleDoesNotExistException extends RuntimeException {
    public RoleDoesNotExistException(String message) {
        super(message);
    }
}

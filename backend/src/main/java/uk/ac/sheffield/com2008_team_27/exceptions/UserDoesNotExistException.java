package uk.ac.sheffield.com2008_team_27.exceptions;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String message) { super(message); }
}

package uk.ac.sheffield.com2008_team_27.exceptions;

public class ModuleDoesNotExistException extends RuntimeException {
    public ModuleDoesNotExistException(String message) {
        super(message);
    }
}

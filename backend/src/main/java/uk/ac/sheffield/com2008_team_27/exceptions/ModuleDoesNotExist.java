package uk.ac.sheffield.com2008_team_27.exceptions;

public class ModuleDoesNotExist extends RuntimeException{
    public ModuleDoesNotExist(String message) {
        super(message);
    }
}

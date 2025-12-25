package uk.ac.sheffield.com2008_team_27.exceptions;

import org.springframework.web.server.ResponseStatusException;

public class NotExternalExaminerException extends RuntimeException {
    public NotExternalExaminerException(String message) {
        super(message);
    }
}

package uk.ac.sheffield.com2008_team_27.dto;

import java.util.List;
import java.util.Map;

public class ApiErrorDTO {

    private int status;
    private String message;
    private Map<String, List<String>> errors;

    public ApiErrorDTO(int status, String error) {
        this.status = status;
        this.message = error;
    }

    public ApiErrorDTO(int status, String error, Map<String, List<String>> errors) {
        this.status = status;
        this.message = error;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}

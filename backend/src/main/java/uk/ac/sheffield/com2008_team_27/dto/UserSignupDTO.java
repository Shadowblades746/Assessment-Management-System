package uk.ac.sheffield.com2008_team_27.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserSignupDTO {

    @NotNull(message = "cannot be null")
    @Size(min = 5, max = 30, message = "must be between 5 and 30 characters in length")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@sheffield\\.ac\\.uk$", message = "only valid Sheffield emails allowed")
    private String email;

    @NotNull(message = "cannot be null")
    @Size(min = 8, message = "must be at least 8 characters in length")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter, one special character, and no spaces"
    )
    private String password;

    private String forename;

    private String surname;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHash() {
        return password;
    }

    public void setHash(String password) {
        this.password = password;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}

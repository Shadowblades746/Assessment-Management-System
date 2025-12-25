package uk.ac.sheffield.com2008_team_27.dto;

import uk.ac.sheffield.com2008_team_27.domain.User;

public class UserDTO {

    private String email;

    private String forename;

    private String surname;

    private Integer id;

    public UserDTO() { // no-arg constructor
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User toEntity() {
        User entity = new User();
        entity.setForename(this.forename);
        entity.setSurname(this.surname);
        entity.setEmail(this.email);
        if (this.id != null) {
            entity.setId(this.id);
        }
        return entity;
    }
}

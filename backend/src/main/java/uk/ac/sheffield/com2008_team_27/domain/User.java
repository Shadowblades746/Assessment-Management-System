package uk.ac.sheffield.com2008_team_27.domain;
import jakarta.persistence.*;
import uk.ac.sheffield.com2008_team_27.config.Authorities;
import uk.ac.sheffield.com2008_team_27.dto.UserDTO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users") // H2 already has a database table called "user"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String forename;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hash;

    @Column(nullable = false)
    private String role = Authorities.ROLE_USER; // default role

    @Column(nullable = false)
    private boolean active = true;

    // ---------- Constructors ----------
    public User() {} // Required by JPA

    // Main constructor used when creating users with password + roles
    public User(String email, String forename, String surname, String hash, String roles) {
        this.email = email;
        this.forename = forename;
        this.surname = surname;
        this.hash = hash;
        this.role = roles;
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

    public String getRole() { return role; }
    public void setRole(String roles) { this.role = roles; }

    public boolean hasRole(String role) {
        return this.role.equalsIgnoreCase(role);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String username) { this.email = username; }

    public String getHash() { return hash; }
    public void setHash(String password) { this.hash = password; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public UserDTO toDto() {
        UserDTO dto = new UserDTO();
        dto.setId(this.id);
        dto.setEmail(this.email);
        dto.setForename(this.forename);
        dto.setSurname(this.surname);

        return dto;
    }
}

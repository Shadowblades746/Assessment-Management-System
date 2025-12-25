package uk.ac.sheffield.com2008_team_27.dto;

public class TokenDTO {

    private String token;

    private UserDTO user;

    private String roles;

    public TokenDTO(String token, UserDTO userDTO,  String roles) {
        this.token = token;
        this.user = userDTO;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getRoles() { return roles; }

    public void setRoles(String roles) { this.roles = roles; }
}

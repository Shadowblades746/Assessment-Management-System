package uk.ac.sheffield.com2008_team_27.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "module_roles")
public class ModuleRole {

    @EmbeddedId
    private ModuleRoleId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("moduleCode")
    @JoinColumn(name = "module_code")
    private Module module;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        STAFF,
        LEAD,
        MODERATOR
    }

    public ModuleRole() {}

    public ModuleRole(User user, Module module, Role role) {
        this.user = user;
        this.module = module;
        this.role = role;
        this.id = new ModuleRoleId(user.getId(), module.getModuleCode());
    }

    // getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
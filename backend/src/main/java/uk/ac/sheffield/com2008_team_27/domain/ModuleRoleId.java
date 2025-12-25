package uk.ac.sheffield.com2008_team_27.domain;

import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.Objects;

public class ModuleRoleId implements Serializable {

    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "module_code")
    private String moduleCode;

    public ModuleRoleId() {}

    public ModuleRoleId(Integer userId, String moduleCode) {
        this.userId = userId;
        this.moduleCode = moduleCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleRoleId that = (ModuleRoleId) o;
        return userId.equals(that.userId) && moduleCode.equals(that.moduleCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, moduleCode);
    }
}

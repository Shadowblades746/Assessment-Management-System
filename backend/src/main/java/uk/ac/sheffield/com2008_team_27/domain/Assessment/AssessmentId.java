package uk.ac.sheffield.com2008_team_27.domain.Assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AssessmentId implements Serializable {

    private String title;

    @Column(name = "module_code")
    private String moduleCode;

    public AssessmentId() {}

    public AssessmentId(String title, String moduleCode) {
        this.title = title;
        this.moduleCode = moduleCode;
    }

    public String getTitle() {
        return title;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentId that = (AssessmentId) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(moduleCode, that.moduleCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, moduleCode);
    }
}

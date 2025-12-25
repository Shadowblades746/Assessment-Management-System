package uk.ac.sheffield.com2008_team_27.dto;

import jakarta.persistence.*;
import uk.ac.sheffield.com2008_team_27.domain.Module;

import javax.print.attribute.IntegerSyntax;

public class ModuleDTO {

    private String moduleCode;
    private String moduleName;
    private String school;

    private Boolean canEdit;
    private Boolean canDelete;

    private Module.DegreeLevel degreeLevel;

    public ModuleDTO() {}

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Boolean getCanEdit() { return canEdit; }
    public void setCanEdit(Boolean canEdit) { this.canEdit = canEdit; }

    public Boolean getCanDelete() { return canDelete; }
    public void setCanDelete(Boolean canDelete) { this.canDelete = canDelete; }

    public Module.DegreeLevel getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(Module.DegreeLevel degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public Module toEntity(){
        Module module = new Module();
        if (moduleCode == null || moduleCode.isBlank()) {
            throw new IllegalArgumentException("Module Has to Have a Code");
        }
        if (moduleName == null || moduleName.isBlank()) {
            throw new IllegalArgumentException("Module Has to Have a Name");
        }
        if (school == null || school.isBlank()) {
            throw new IllegalArgumentException("School is in the Incorrect  Format");
        }
        if (degreeLevel == null) {
            throw new IllegalArgumentException("Level Of Degree is a required Field");
        }
        module.setModuleCode(moduleCode);
        module.setModuleName(moduleName);
        module.setSchool(school);
        module.setDegreeLevel(degreeLevel);
        return module;
    }
}

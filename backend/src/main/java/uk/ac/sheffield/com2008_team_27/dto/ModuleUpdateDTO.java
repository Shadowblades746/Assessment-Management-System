package uk.ac.sheffield.com2008_team_27.dto;

import uk.ac.sheffield.com2008_team_27.domain.Module;

public class ModuleUpdateDTO {
    private String moduleCode;
    private String moduleName;
    private String school;
    private Module.DegreeLevel degreeLevel;
    private Integer moduleLeadID;
    private Integer moderatorID;

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

    public Module.DegreeLevel getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(Module.DegreeLevel degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public Integer getModuleLeadId() {
        return moduleLeadID;
    }

    public void setModuleLeadId(Integer moduleLeadId) {
        this.moduleLeadID = moduleLeadId;
    }

    public Integer getModeratorId() {
        return moderatorID;
    }

    public void setModeratorId(Integer moderatorId) {
        this.moderatorID = moderatorId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

}

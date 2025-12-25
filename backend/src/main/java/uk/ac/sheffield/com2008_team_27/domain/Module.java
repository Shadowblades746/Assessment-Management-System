package uk.ac.sheffield.com2008_team_27.domain;

import jakarta.persistence.*;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.dto.ModuleDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules")
public class Module {

    @Id
    private String moduleCode;


    @Column(nullable = false)
    private String moduleName;

    @Column(nullable = false)
    private String school;

    public enum DegreeLevel{
        UNDERGRADUATE,
        POSTGRADUATE,
    }

    @Column(nullable = false)
    private DegreeLevel degreeLevel;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assessment> assessments = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleRole> moduleRoles = new ArrayList<>();

    public Module(String moduleCode, String moduleName, String school, DegreeLevel levelOfDegree) {
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.degreeLevel = levelOfDegree;
        this.school = school;
    }

    public Module(){}

    public String getModuleCode() {return moduleCode;}

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

    public DegreeLevel getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(DegreeLevel levelOfDegree) {
        this.degreeLevel = levelOfDegree;
    }

    public ModuleDTO toDto(){
        ModuleDTO moduledto = new ModuleDTO();
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
        moduledto.setModuleCode(moduleCode);
        moduledto.setModuleName(moduleName);
        moduledto.setSchool(school);
        moduledto.setDegreeLevel(degreeLevel);
        return moduledto;
    }
}

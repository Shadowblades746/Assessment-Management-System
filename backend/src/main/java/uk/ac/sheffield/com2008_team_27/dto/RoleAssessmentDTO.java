package uk.ac.sheffield.com2008_team_27.dto;

import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;

public class RoleAssessmentDTO {
    private String role;
    private Assessment assessment;

    public RoleAssessmentDTO(String role, Assessment assessment) {
        this.role = role;
        this.assessment = assessment;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}

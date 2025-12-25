package uk.ac.sheffield.com2008_team_27.dto;

import uk.ac.sheffield.com2008_team_27.domain.Assessment.*;
import uk.ac.sheffield.com2008_team_27.domain.Module;

import java.time.LocalDateTime;
import java.util.Date;

public class AssessmentDTO {
    //non-specific
    private String title;
    private Assessment.Type type;
    private Integer setterID;
    private Integer checkerID;
    private String moduleCode;
    private LocalDateTime setDate;
    private Assessment.AssessmentStatus status;
    private LocalDateTime lastUpdated;

    // specific to formal exam
    private Integer externalExaminerID;
//    private String externalExaminerFeedback;
//    private String setterResponse;
    private String previousFeedback;

    // specific to in semester
    private Boolean autograded;

    // specific to coursework
    private LocalDateTime dueDate;

    public Assessment toEntity(Module module) {
        Assessment returnedAssessment = null;
        if (this.type == Assessment.Type.IN_SEMESTER) {
            returnedAssessment = new InSemester(this.title, this.type, this.setterID, this.checkerID, module, this.setDate, this.previousFeedback, this.autograded);
        }
        if (this.type == Assessment.Type.COURSE_WORK) {
            returnedAssessment = new Coursework(this.title, this.type, this.setterID, this.checkerID, module, this.setDate, this.previousFeedback, this.dueDate);
        }
        if (this.type == Assessment.Type.FORMAL_EXAM) {
            returnedAssessment = new FormalExam(this.title, this.type, this.setterID, this.checkerID, module, this.setDate, this.externalExaminerID, this.previousFeedback);
                    //this.previousFeedback); //this.externalExaminerFeedback, this.setterResponse);
        }

        return returnedAssessment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Assessment.Type getType() {
        return type;
    }

    public void setType(Assessment.Type type) {
        this.type = type;
    }

    public Integer getSetterID() {
        return setterID;
    }

    public void setSetterID(Integer setterID) {
        this.setterID = setterID;
    }

    public Integer getCheckerID() {
        return checkerID;
    }

    public void setCheckerID(Integer checkerID) {
        this.checkerID = checkerID;
    }

    public LocalDateTime getSetDate() {
        return setDate;
    }

    public void setSetDate(LocalDateTime setDate) {
        this.setDate = setDate;
    }

    public Assessment.AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(Assessment.AssessmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getExternalExaminerID() {
        return externalExaminerID;
    }

    public void setExternalExaminerID(Integer externalExaminerID) {
        this.externalExaminerID = externalExaminerID;
    }

    public String getPreviousFeedback() {
        return previousFeedback;
    }
    public void setPreviousFeedback(String previousFeedback) {
        this.previousFeedback = previousFeedback;
    }

    public Boolean isAutograded() {
        return autograded;
    }
    public void setAutograded(Boolean autograded) {
        this.autograded = autograded;
    }

    public String getModuleCode() {
        return moduleCode;
    }
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}

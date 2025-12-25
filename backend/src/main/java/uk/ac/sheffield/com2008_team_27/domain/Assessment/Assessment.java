package uk.ac.sheffield.com2008_team_27.domain.Assessment;

import jakarta.persistence.*;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import jakarta.persistence.Transient;
import uk.ac.sheffield.com2008_team_27.service.AssessmentService;

import java.time.*;
import java.util.List;

@Entity
@Table(name="Assessments")
public abstract class Assessment {
    public enum Type{
        COURSE_WORK,
        IN_SEMESTER,
        FORMAL_EXAM
    }

    public enum AssessmentStatus {
        DRAFT, // creation or edit of an assessment
        NEEDS_CHECKING,
        NEEDS_EO_CHECKING,
        NEEDS_EE_FEEDBACK,
        NEEDS_SETTER_FEEDBACK,
        SPECIFICATION_RELEASE,
        MARKING_STANDARDISATION,
        NEEDS_MARKING,
        NEEDS_ADMIN_CHECK,
        NEEDS_MODERATION,
        RETURNS_FEEDBACK,
        //NEEDS_APPROVAL, this step does not need to be tracke
        COMPLETED
    }


    /*
        gets required assessment process from relevant class instead of switch case
     */
    @Transient
    public abstract List<AssessmentStatus> getProcess();

    private int currentProcessIndex; // tracks position in the workflow

    public enum ProgressDirection{
        FORWARD,
        BACKWARD
    }

    @EmbeddedId
    private AssessmentId id;

    @ManyToOne
    @MapsId("moduleCode")
    @JoinColumn(name = "module_code")
    private Module module;

    private Type type;
    private Integer setterID;
    private Integer checkerID;
    private LocalDateTime setDate;
    private AssessmentStatus status;
    private LocalDateTime lastUpdated;

    private String previousFeedback;

    public Assessment(){
    }
    public Assessment(String title, Type type, Integer setterID, Integer checkerID, Module module, LocalDateTime setDate, String previousFeedback){
        this.module = module;
        this.id = new AssessmentId(title, this.module.getModuleCode());

        this.type = type;
        this.setterID = setterID;
        this.checkerID = checkerID;
        this.setDate = setDate;
        this.status = AssessmentStatus.DRAFT;
        this.lastUpdated = LocalDateTime.now();
        this.previousFeedback = previousFeedback;
    }

    public abstract AssessmentDTO toDTO();

    protected AssessmentDTO populateBaseDTO(AssessmentDTO dto) {
        dto.setTitle(this.getTitle());
        dto.setType(this.type);
        dto.setSetterID(this.setterID);
        dto.setCheckerID(this.checkerID);
        dto.setModuleCode(this.getModuleCode());
        dto.setSetDate(this.setDate);
        dto.setStatus(this.status);
        dto.setLastUpdated(this.lastUpdated);
        dto.setPreviousFeedback(this.previousFeedback);
        return dto;
    }

    public String getTitle() {
        return id != null ? id.getTitle() : null;
    }

    public Type getType() {
        return type;
    }

    public Integer getSetterID() {
        return setterID;
    }

    public Integer getCheckerID() {
        return checkerID;
    }

    public String getModuleCode() {
        return id  != null ? id.getModuleCode() : null;

    }
    public LocalDateTime getSetDate() {
        return setDate;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setType(Type type) {
        this.type = type;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setSetterID(Integer setterID) {
        this.setterID = setterID;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setCheckerID(Integer checkerID) {
        this.checkerID = checkerID;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setModuleCode(String moduleCode) {
        this.id.setModuleCode(moduleCode);
        this.lastUpdated = LocalDateTime.now();
    }

    public void setSetDate(LocalDateTime setDate) {
        this.setDate = setDate;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public int getCurrentProcessIndex() {
        return currentProcessIndex;
    }

    public void setCurrentProcessIndex(int currentProcessIndex) {
        this.currentProcessIndex = currentProcessIndex;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getPreviousFeedback() {
        return previousFeedback;
    }
    public void setPreviousFeedback(String previousFeedback) {
        this.previousFeedback = previousFeedback;
    }
}

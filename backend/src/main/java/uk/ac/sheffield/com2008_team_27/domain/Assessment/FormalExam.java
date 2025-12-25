package uk.ac.sheffield.com2008_team_27.domain.Assessment;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import uk.ac.sheffield.com2008_team_27.domain.Module;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class FormalExam extends Assessment {

    private Integer externalExaminerId;

//    private String externalExaminerFeedback;
//
//    private String setterResponse;

    private String previousFeedback;

    private boolean hasSeenNeedsChecking;

    public FormalExam(String title, Assessment.Type type, Integer setterID, Integer checkerID, Module module,
                      LocalDateTime setDate, Integer externalExaminerId, String previousFeedback) {
        super(title, type, setterID, checkerID, module, setDate, previousFeedback);
        this.externalExaminerId = externalExaminerId;
    }

    public FormalExam() {

    }

    @Override
    @Transient
    public List<AssessmentStatus> getProcess() {
        return List.of(
            AssessmentStatus.DRAFT,
            AssessmentStatus.NEEDS_CHECKING,          // this stage is skipped after the first pass - question 86
            AssessmentStatus.NEEDS_EO_CHECKING,       // exams officer checks
            AssessmentStatus.NEEDS_EE_FEEDBACK,       // external examiner feedback
            AssessmentStatus.NEEDS_SETTER_FEEDBACK,   // written response + changes
            AssessmentStatus.NEEDS_EO_CHECKING,       // final EO check then send to printing
            // exam takes place automatically progressed?
            AssessmentStatus.MARKING_STANDARDISATION,
            AssessmentStatus.NEEDS_MARKING,
            AssessmentStatus.NEEDS_ADMIN_CHECK,       // admin team mark-checking
            AssessmentStatus.NEEDS_MODERATION,
            AssessmentStatus.COMPLETED
        );
    }

    @Override
    public void setStatus(AssessmentStatus status) {
        super.setStatus(status);

        if (status == AssessmentStatus.NEEDS_CHECKING) {
            this.hasSeenNeedsChecking = true;
        }
    }

    @Override
    public AssessmentDTO toDTO() {
        AssessmentDTO dto = new AssessmentDTO();
        this.populateBaseDTO(dto);
        dto.setExternalExaminerID(this.externalExaminerId);
        return dto;
    }

    public Integer getExternalExaminerId() {
        return externalExaminerId;
    }

    public void setExternalExaminerId(Integer externalExaminerId) {
        this.externalExaminerId = externalExaminerId;
    }

//    public String getExternalExaminerFeedback() {
//        return externalExaminerFeedback;
//    }
//
//    public void setExternalExaminerFeedback(String externalExaminerFeedback) {
//        this.externalExaminerFeedback = externalExaminerFeedback;
//    }
//
//    public String getSetterResponse() {
//        return setterResponse;
//    }
//
//    public void setSetterResponse(String setterResponse) {
//        this.setterResponse = setterResponse;
//    }

    public boolean hasSeenNeedsChecking() {
        return hasSeenNeedsChecking;
    }

    public void setHasSeenNeedsChecking(boolean hasSeenNeedsChecking) {
        this.hasSeenNeedsChecking = hasSeenNeedsChecking;
    }

    /*
        testing purposes -> to reset a formal exam so NEEDS_CHECKING can be iterated over again
     */
    public void resetNeedsCheckingFlag() {
        this.hasSeenNeedsChecking = false;
    }

}

package uk.ac.sheffield.com2008_team_27.domain.Assessment;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import uk.ac.sheffield.com2008_team_27.domain.Module;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class InSemester extends Assessment {
    private Boolean autograded;

    public InSemester(String title, Assessment.Type type, Integer setterID, Integer checkerID, Module module,
                      LocalDateTime setDate, String previousFeedback, Boolean autograded) {
        super(title, type, setterID, checkerID, module, setDate, previousFeedback);
        this.autograded = autograded;
    }

    public InSemester() {

    }

    @Override
    public AssessmentDTO toDTO() {
        AssessmentDTO dto = new AssessmentDTO();
        this.populateBaseDTO(dto);
        dto.setAutograded(this.autograded);
        return dto;
    }

    @Override
    @Transient
    public List<AssessmentStatus> getProcess() {
        if (isAutograded()) {
            // autograded
            return List.of(
                    AssessmentStatus.DRAFT,
                    AssessmentStatus.NEEDS_CHECKING,
                    // test takes place automatically progressed?
                    AssessmentStatus.MARKING_STANDARDISATION,
                    AssessmentStatus.RETURNS_FEEDBACK,
                    AssessmentStatus.COMPLETED
            );
        } else {
            // not autograded
            return List.of(
                    AssessmentStatus.DRAFT,
                    AssessmentStatus.NEEDS_CHECKING,
                    // test takes place automatically progressed?
                    AssessmentStatus.MARKING_STANDARDISATION,
                    AssessmentStatus.NEEDS_MARKING,
                    AssessmentStatus.NEEDS_MODERATION,
                    AssessmentStatus.RETURNS_FEEDBACK,
                    AssessmentStatus.COMPLETED
            );
        }
    }


    public Boolean isAutograded() {
        return autograded;
    }

    public void setAutograded(Boolean autograded) {
        this.autograded = autograded;
    }
}

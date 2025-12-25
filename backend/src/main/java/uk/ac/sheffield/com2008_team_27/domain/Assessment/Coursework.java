package uk.ac.sheffield.com2008_team_27.domain.Assessment;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import uk.ac.sheffield.com2008_team_27.domain.Module;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
public class Coursework extends Assessment {

    private LocalDateTime dueDate;

    public Coursework(String title, Type type, Integer setterID, Integer checkerID, Module module,
                      LocalDateTime setDate, String previousFeedback, LocalDateTime dueDate){
        super(title, type, setterID, checkerID, module, setDate, previousFeedback);
        this.dueDate = dueDate;
    }

    public Coursework() {

    }

    @Override
    @Transient
    public List<AssessmentStatus> getProcess() {
        return List.of(
            AssessmentStatus.DRAFT,
            AssessmentStatus.NEEDS_CHECKING,          // checked by another staff member
            AssessmentStatus.SPECIFICATION_RELEASE,
            // submission deadline automatically progressed?
            AssessmentStatus.MARKING_STANDARDISATION, // (if team-marked)
            AssessmentStatus.NEEDS_MARKING,
            AssessmentStatus.NEEDS_MODERATION,
            AssessmentStatus.RETURNS_FEEDBACK,
            AssessmentStatus.COMPLETED
        );
    }

    @Override
    public AssessmentDTO toDTO() {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setDueDate(this.dueDate);
        return this.populateBaseDTO(dto);
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}

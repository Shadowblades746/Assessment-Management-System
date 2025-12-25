package uk.ac.sheffield.com2008_team_27.domain.Assessment;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssessmentTest {

    private static class TestAssessment extends Assessment {
        private final List<AssessmentStatus> process;

        public TestAssessment(String title, Type type, Integer setterID, Integer checkerID,
                              Module module, LocalDateTime setDate, String previousFeedback,
                              List<AssessmentStatus> process) {
            super(title, type, setterID, checkerID, module, setDate, previousFeedback);
            this.process = process;
        }

        @Override
        public List<AssessmentStatus> getProcess() {
            return process;
        }

        @Override
        public AssessmentDTO toDTO() {
            return populateBaseDTO(new AssessmentDTO());
        }
    }

    @Test
    void constructor_initializesFieldsCorrectly() {
        Module module = Mockito.mock(Module.class);
        when(module.getModuleCode()).thenReturn("CS101");
        LocalDateTime setDate = LocalDateTime.of(2025, 3, 15, 10, 0);
        List<Assessment.AssessmentStatus> process = Arrays.asList(Assessment.AssessmentStatus.DRAFT, Assessment.AssessmentStatus.COMPLETED);

        TestAssessment assessment = new TestAssessment(
                "Test Assessment",
                Assessment.Type.COURSE_WORK,
                1,
                2,
                module,
                setDate,
                "Previous feedback",
                process
        );

        assertEquals("Test Assessment", assessment.getTitle());
        assertEquals(Assessment.Type.COURSE_WORK, assessment.getType());
        assertEquals(1, assessment.getSetterID());
        assertEquals(2, assessment.getCheckerID());
        assertEquals("CS101", assessment.getModuleCode());
        assertEquals(setDate, assessment.getSetDate());
        assertEquals(Assessment.AssessmentStatus.DRAFT, assessment.getStatus());
        assertEquals("Previous feedback", assessment.getPreviousFeedback());
        assertNotNull(assessment.getLastUpdated());
    }

    @Test
    void populateBaseDTO_mapsAllFields() {
        Module module = Mockito.mock(Module.class);
        when(module.getModuleCode()).thenReturn("CS202");
        LocalDateTime setDate = LocalDateTime.of(2025, 4, 20, 14, 30);
        List<Assessment.AssessmentStatus> process = List.of(Assessment.AssessmentStatus.DRAFT);

        TestAssessment assessment = new TestAssessment(
                "Base DTO Test",
                Assessment.Type.IN_SEMESTER,
                5,
                6,
                module,
                setDate,
                "Feedback text",
                process
        );

        AssessmentDTO dto = assessment.toDTO();

        assertEquals("Base DTO Test", dto.getTitle());
        assertEquals(Assessment.Type.IN_SEMESTER, dto.getType());
        assertEquals(5, dto.getSetterID());
        assertEquals(6, dto.getCheckerID());
        assertEquals("CS202", dto.getModuleCode());
        assertEquals(setDate, dto.getSetDate());
        assertEquals(Assessment.AssessmentStatus.DRAFT, dto.getStatus());
        assertEquals("Feedback text", dto.getPreviousFeedback());
        assertNotNull(dto.getLastUpdated());
    }

    @Test
    void setters_updateLastUpdated() throws InterruptedException {
        Module module = Mockito.mock(Module.class);
        when(module.getModuleCode()).thenReturn("CS303");
        LocalDateTime setDate = LocalDateTime.of(2025, 5, 1, 9, 0);
        List<Assessment.AssessmentStatus> process = List.of(Assessment.AssessmentStatus.DRAFT);

        TestAssessment assessment = new TestAssessment(
                "Setter Test",
                Assessment.Type.FORMAL_EXAM,
                10,
                11,
                module,
                setDate,
                null,
                process
        );

        LocalDateTime initialUpdate = assessment.getLastUpdated();
        Thread.sleep(10);

        assessment.setType(Assessment.Type.COURSE_WORK);
        assertTrue(assessment.getLastUpdated().isAfter(initialUpdate));

        LocalDateTime afterTypeUpdate = assessment.getLastUpdated();
        Thread.sleep(10);

        assessment.setStatus(Assessment.AssessmentStatus.COMPLETED);
        assertTrue(assessment.getLastUpdated().isAfter(afterTypeUpdate));
    }
}

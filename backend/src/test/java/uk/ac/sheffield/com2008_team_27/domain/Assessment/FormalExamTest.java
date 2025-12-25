package uk.ac.sheffield.com2008_team_27.domain.Assessment;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormalExamTest {

    @Test
    void getProcess_returnsCorrectSteps() {
        FormalExam exam = new FormalExam();
        List<Assessment.AssessmentStatus> process = exam.getProcess();

        assertEquals(11, process.size());
        assertEquals(Assessment.AssessmentStatus.DRAFT, process.get(0));
        assertEquals(Assessment.AssessmentStatus.NEEDS_CHECKING, process.get(1));
        assertEquals(Assessment.AssessmentStatus.NEEDS_EO_CHECKING, process.get(2));
        assertEquals(Assessment.AssessmentStatus.NEEDS_EE_FEEDBACK, process.get(3));
        assertEquals(Assessment.AssessmentStatus.NEEDS_SETTER_FEEDBACK, process.get(4));
        assertEquals(Assessment.AssessmentStatus.NEEDS_EO_CHECKING, process.get(5));
        assertEquals(Assessment.AssessmentStatus.MARKING_STANDARDISATION, process.get(6));
        assertEquals(Assessment.AssessmentStatus.NEEDS_MARKING, process.get(7));
        assertEquals(Assessment.AssessmentStatus.NEEDS_ADMIN_CHECK, process.get(8));
        assertEquals(Assessment.AssessmentStatus.NEEDS_MODERATION, process.get(9));
        assertEquals(Assessment.AssessmentStatus.COMPLETED, process.get(10));
    }

    @Test
    void toDTO_mapsAllFields() {
        Module module = Mockito.mock(Module.class);
        when(module.getModuleCode()).thenReturn("Com2008");
        LocalDateTime setDate = LocalDateTime.of(2025, 6, 1, 10, 0);

        FormalExam exam = new FormalExam("Spring Exam", Assessment.Type.FORMAL_EXAM, 12, 13, module, setDate, 99, "Exam feedback");

        try (MockedConstruction<AssessmentDTO> mocked = Mockito.mockConstruction(AssessmentDTO.class)) {
            AssessmentDTO result = exam.toDTO();

            AssessmentDTO mockDto = mocked.constructed().get(0);
            assertSame(mockDto, result);

            verify(mockDto).setTitle("Spring Exam");
            verify(mockDto).setType(Assessment.Type.FORMAL_EXAM);
            verify(mockDto).setSetterID(12);
            verify(mockDto).setCheckerID(13);
            verify(mockDto).setModuleCode("Com2008");
            verify(mockDto).setSetDate(setDate);
            verify(mockDto).setExternalExaminerID(99);
            verify(mockDto).setPreviousFeedback("Exam feedback");

            when(mockDto.getType()).thenReturn(Assessment.Type.FORMAL_EXAM);
            assertEquals(Assessment.Type.FORMAL_EXAM, mockDto.getType());
        }
    }
}

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

class InSemesterTest {

    @Test
    void getProcess_autograded_returnsCorrectSteps() {
        InSemester inSem = new InSemester();
        inSem.setAutograded(true);
        List<Assessment.AssessmentStatus> process = inSem.getProcess();

        assertEquals(5, process.size());
        assertEquals(Assessment.AssessmentStatus.DRAFT, process.get(0));
        assertEquals(Assessment.AssessmentStatus.NEEDS_CHECKING, process.get(1));
        assertEquals(Assessment.AssessmentStatus.MARKING_STANDARDISATION, process.get(2));
        assertEquals(Assessment.AssessmentStatus.RETURNS_FEEDBACK, process.get(3));
        assertEquals(Assessment.AssessmentStatus.COMPLETED, process.get(4));
    }

    @Test
    void getProcess_notAutograded_returnsCorrectSteps() {
        InSemester inSem = new InSemester();
        inSem.setAutograded(false);
        List<Assessment.AssessmentStatus> process = inSem.getProcess();

        assertEquals(7, process.size());
        assertEquals(Assessment.AssessmentStatus.DRAFT, process.get(0));
        assertEquals(Assessment.AssessmentStatus.NEEDS_CHECKING, process.get(1));
        assertEquals(Assessment.AssessmentStatus.MARKING_STANDARDISATION, process.get(2));
        assertEquals(Assessment.AssessmentStatus.NEEDS_MARKING, process.get(3));
        assertEquals(Assessment.AssessmentStatus.NEEDS_MODERATION, process.get(4));
        assertEquals(Assessment.AssessmentStatus.RETURNS_FEEDBACK, process.get(5));
        assertEquals(Assessment.AssessmentStatus.COMPLETED, process.get(6));
    }

    @Test
    void toDTO_mapsAllFields() {
        Module module = Mockito.mock(Module.class);
        when(module.getModuleCode()).thenReturn("Com2008");
        LocalDateTime setDate = LocalDateTime.of(2025, 2, 1, 9, 0);

        InSemester inSem = new InSemester("Quiz", Assessment.Type.IN_SEMESTER, 5, 6, module, setDate, "Feedback text", true);

        try (MockedConstruction<AssessmentDTO> mocked = Mockito.mockConstruction(AssessmentDTO.class)) {
            AssessmentDTO result = inSem.toDTO();

            AssessmentDTO mockDto = mocked.constructed().get(0);
            assertSame(mockDto, result);

            verify(mockDto).setTitle("Quiz");
            verify(mockDto).setType(Assessment.Type.IN_SEMESTER);
            verify(mockDto).setSetterID(5);
            verify(mockDto).setCheckerID(6);
            verify(mockDto).setModuleCode("Com2008");
            verify(mockDto).setSetDate(setDate);
            verify(mockDto).setAutograded(true);
            verify(mockDto).setPreviousFeedback("Feedback text");

            when(mockDto.isAutograded()).thenReturn(true);
            assertTrue(mockDto.isAutograded());
        }
    }
}

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

class CourseworkTest {

    @Test
    void getProcess_returnsCorrectSteps() {
        Coursework cw = new Coursework();
        List<Assessment.AssessmentStatus> process = cw.getProcess();

        assertEquals(8, process.size());
        assertEquals(Assessment.AssessmentStatus.DRAFT, process.get(0));
        assertEquals(Assessment.AssessmentStatus.NEEDS_CHECKING, process.get(1));
        assertEquals(Assessment.AssessmentStatus.SPECIFICATION_RELEASE, process.get(2));
        assertEquals(Assessment.AssessmentStatus.MARKING_STANDARDISATION, process.get(3));
        assertEquals(Assessment.AssessmentStatus.NEEDS_MARKING, process.get(4));
        assertEquals(Assessment.AssessmentStatus.NEEDS_MODERATION, process.get(5));
        assertEquals(Assessment.AssessmentStatus.RETURNS_FEEDBACK, process.get(6));
        assertEquals(Assessment.AssessmentStatus.COMPLETED, process.get(7));
    }

    @Test
    void toDTO_mapsAllFields() {
        Module module = Mockito.mock(Module.class);
        when(module.getModuleCode()).thenReturn("CS202");
        LocalDateTime setDate = LocalDateTime.of(2025, 4, 5, 11, 0);

        Coursework cw = new Coursework("Essay", Assessment.Type.COURSE_WORK, 7, 8, module, setDate, "Feedback here", null);

        try (MockedConstruction<AssessmentDTO> mocked = Mockito.mockConstruction(AssessmentDTO.class)) {
            AssessmentDTO result = cw.toDTO();

            AssessmentDTO mockDto = mocked.constructed().get(0);
            assertSame(mockDto, result);

            verify(mockDto).setTitle("Essay");
            verify(mockDto).setType(Assessment.Type.COURSE_WORK);
            verify(mockDto).setSetterID(7);
            verify(mockDto).setCheckerID(8);
            verify(mockDto).setModuleCode("CS202");
            verify(mockDto).setSetDate(setDate);
            verify(mockDto).setPreviousFeedback("Feedback here");

            when(mockDto.getType()).thenReturn(Assessment.Type.COURSE_WORK);
            assertEquals(Assessment.Type.COURSE_WORK, mockDto.getType());
        }
    }
}

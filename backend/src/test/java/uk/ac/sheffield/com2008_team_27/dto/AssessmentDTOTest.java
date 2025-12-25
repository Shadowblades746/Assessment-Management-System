package uk.ac.sheffield.com2008_team_27.dto;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Coursework;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.FormalExam;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.InSemester;
import uk.ac.sheffield.com2008_team_27.domain.Module;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentDTOTest {

    @Test
    void toEntity_createsInSemester() {
        Module module = Mockito.mock(Module.class);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setTitle("InSem Test");
        dto.setType(Assessment.Type.IN_SEMESTER);
        dto.setSetterID(10);
        dto.setCheckerID(20);
        LocalDateTime dt = LocalDateTime.of(2025, 1, 2, 3, 4);
        dto.setSetDate(dt);
        dto.setAutograded(true);

        Assessment result = dto.toEntity(module);
        assertNotNull(result);
        assertInstanceOf(InSemester.class, result);

        InSemester inSem = (InSemester) result;
        assertEquals("InSem Test", inSem.getTitle());
        assertEquals(Assessment.Type.IN_SEMESTER, inSem.getType());
        assertEquals(10, inSem.getSetterID());
        assertEquals(20, inSem.getCheckerID());
        assertEquals(dt, inSem.getSetDate());
        assertTrue(inSem.isAutograded());
    }

    @Test
    void toEntity_createsCoursework() {
        Module module = Mockito.mock(Module.class);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setTitle("CW Test");
        dto.setType(Assessment.Type.COURSE_WORK);
        dto.setSetterID(1);
        dto.setCheckerID(2);
        LocalDateTime dt = LocalDateTime.of(2025, 6, 7, 8, 9);
        dto.setSetDate(dt);

        Assessment result = dto.toEntity(module);
        assertNotNull(result);
        assertInstanceOf(Coursework.class, result);

        Coursework cw = (Coursework) result;
        assertEquals("CW Test", cw.getTitle());
        assertEquals(Assessment.Type.COURSE_WORK, cw.getType());
        assertEquals(1, cw.getSetterID());
        assertEquals(2, cw.getCheckerID());
        assertEquals(dt, cw.getSetDate());
    }

    @Test
    void toEntity_createsFormalExam() {
        Module module = Mockito.mock(Module.class);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setTitle("Exam Test");
        dto.setType(Assessment.Type.FORMAL_EXAM);
        dto.setSetterID(5);
        dto.setCheckerID(6);
        LocalDateTime dt = LocalDateTime.of(2025, 12, 1, 10, 11);
        dto.setSetDate(dt);

        Assessment result = dto.toEntity(module);
        assertNotNull(result);
        assertInstanceOf(FormalExam.class, result);

        FormalExam fe = (FormalExam) result;
        assertEquals("Exam Test", fe.getTitle());
        assertEquals(Assessment.Type.FORMAL_EXAM, fe.getType());
        assertEquals(5, fe.getSetterID());
        assertEquals(6, fe.getCheckerID());
        assertEquals(dt, fe.getSetDate());
    }
}

package uk.ac.sheffield.com2008_team_27.domain;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import uk.ac.sheffield.com2008_team_27.dto.ModuleDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModuleTest {

    @Test
    void constructor_initializesFieldsCorrectly() {
        Module module = new Module("com2008", "Intro to CS", "Engineering", Module.DegreeLevel.UNDERGRADUATE);

        assertEquals("com2008", module.getModuleCode());
        assertEquals("Intro to CS", module.getModuleName());
        assertEquals("Engineering", module.getSchool());
        assertEquals(Module.DegreeLevel.UNDERGRADUATE, module.getDegreeLevel());
    }

    @Test
    void toDto_mapsAllFields() {
        Module module = new Module("com2002", "Advanced Programming", "Computer Science", Module.DegreeLevel.POSTGRADUATE);

        try (MockedConstruction<ModuleDTO> mocked = Mockito.mockConstruction(ModuleDTO.class)) {
            ModuleDTO result = module.toDto();

            ModuleDTO mockDto = mocked.constructed().get(0);
            assertSame(mockDto, result);

            verify(mockDto).setModuleCode("com2002");
            verify(mockDto).setModuleName("Advanced Programming");
            verify(mockDto).setSchool("Computer Science");
            verify(mockDto).setDegreeLevel(Module.DegreeLevel.POSTGRADUATE);
        }
    }

    @Test
    void toDto_missingModuleCode_throwsException() {
        Module module = new Module(null, "Test Module", "Engineering", Module.DegreeLevel.UNDERGRADUATE);

        assertThrows(IllegalArgumentException.class, module::toDto);
    }

    @Test
    void toDto_blankModuleCode_throwsException() {
        Module module = new Module("  ", "Test Module", "Engineering", Module.DegreeLevel.UNDERGRADUATE);

        assertThrows(IllegalArgumentException.class, module::toDto);
    }

    @Test
    void toDto_missingModuleName_throwsException() {
        Module module = new Module("com2008", null, "Engineering", Module.DegreeLevel.UNDERGRADUATE);

        assertThrows(IllegalArgumentException.class, module::toDto);
    }

    @Test
    void toDto_blankSchool_throwsException() {
        Module module = new Module("com2008", "Test Module", "  ", Module.DegreeLevel.UNDERGRADUATE);

        assertThrows(IllegalArgumentException.class, module::toDto);
    }

    @Test
    void toDto_missingDegreeLevel_throwsException() {
        Module module = new Module("com2008", "Test Module", "Engineering", null);

        assertThrows(IllegalArgumentException.class, module::toDto);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        Module module = new Module("com2008", "Intro to CS", "Engineering", Module.DegreeLevel.UNDERGRADUATE);

        module.setModuleCode("com2002");
        module.setModuleName("Advanced CS");
        module.setSchool("Computer Science");
        module.setDegreeLevel(Module.DegreeLevel.POSTGRADUATE);

        assertEquals("com2002", module.getModuleCode());
        assertEquals("Advanced CS", module.getModuleName());
        assertEquals("Computer Science", module.getSchool());
        assertEquals(Module.DegreeLevel.POSTGRADUATE, module.getDegreeLevel());
    }
}

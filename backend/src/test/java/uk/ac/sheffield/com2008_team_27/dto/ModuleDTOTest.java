package uk.ac.sheffield.com2008_team_27.dto;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import uk.ac.sheffield.com2008_team_27.domain.Module;

import static org.junit.jupiter.api.Assertions.*;

class ModuleDTOTest {

    private ModuleDTO createValidDTO() {
        ModuleDTO dto = new ModuleDTO();
        dto.setModuleCode("Com2008");
        dto.setModuleName("Intro to CS");
        dto.setSchool("Engineering");
        dto.setDegreeLevel(Module.DegreeLevel.values()[0]);
        return dto;
    }

    @Test
    void toEntity_success_returnsConstructedModule_mocked() {
        ModuleDTO dto = createValidDTO();

        try (MockedConstruction<Module> mocked = Mockito.mockConstruction(Module.class)) {
            Module result = dto.toEntity();

            assertNotNull(result);
            assertInstanceOf(Module.class, result);
            assertSame(mocked.constructed().get(0), result);
        }
    }

    @Test
    void toEntity_missingModuleCode_throwsIllegalArgumentException() {
        ModuleDTO dto = createValidDTO();
        dto.setModuleCode(null);

        try (MockedConstruction<Module> ignored = Mockito.mockConstruction(Module.class)) {
            assertThrows(IllegalArgumentException.class, dto::toEntity);
        }
    }

    @Test
    void toEntity_blankModuleCode_throwsIllegalArgumentException() {
        ModuleDTO dto = createValidDTO();
        dto.setModuleCode("  ");

        try (MockedConstruction<Module> ignored = Mockito.mockConstruction(Module.class)) {
            assertThrows(IllegalArgumentException.class, dto::toEntity);
        }
    }

    @Test
    void toEntity_missingModuleName_throwsIllegalArgumentException() {
        ModuleDTO dto = createValidDTO();
        dto.setModuleName(null);

        try (MockedConstruction<Module> ignored = Mockito.mockConstruction(Module.class)) {
            assertThrows(IllegalArgumentException.class, dto::toEntity);
        }
    }

    @Test
    void toEntity_missingSchool_throwsIllegalArgumentException() {
        ModuleDTO dto = createValidDTO();
        dto.setSchool(" ");

        try (MockedConstruction<Module> ignored = Mockito.mockConstruction(Module.class)) {
            assertThrows(IllegalArgumentException.class, dto::toEntity);
        }
    }

    @Test
    void toEntity_missingDegreeLevel_throwsIllegalArgumentException() {
        ModuleDTO dto = createValidDTO();
        dto.setDegreeLevel(null);

        try (MockedConstruction<Module> ignored = Mockito.mockConstruction(Module.class)) {
            assertThrows(IllegalArgumentException.class, dto::toEntity);
        }
    }
}

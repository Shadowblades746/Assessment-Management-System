package uk.ac.sheffield.com2008_team_27.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.when;

import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRoleRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;
import uk.ac.sheffield.com2008_team_27.service.RoleService;
import uk.ac.sheffield.com2008_team_27.service.ModuleService;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {
    @Mock private ModuleRepository moduleRepository;
    @Mock private UserRepository userRepository;
    @Mock private AssessmentRepository assessmentRepository;
    @Mock private RoleService roleService;
    @Mock private ModuleRoleRepository moduleRoleRepository;

    @InjectMocks
    private ModuleService classUnderTest;

    @Test
    public void createModule_Succeeds(){
        Module toCreate = new Module("COM2008", "Software Engineering", "Computer Science", Module.DegreeLevel.UNDERGRADUATE);
        when(moduleRepository.save(ArgumentMatchers.any(Module.class))).thenReturn(toCreate);
        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.findById(2)).thenReturn(java.util.Optional.of(new User()));
        Module savedModule = classUnderTest.createModule(toCreate, 1, 2);
        assertThat(savedModule).isEqualTo(toCreate);
    }

    @Test
    public void updateModule_Succeeds(){
        Module existingModule = new Module("COM2008", "Software Engineering", "Computer Science", Module.DegreeLevel.UNDERGRADUATE);
        when(moduleRepository.findByModuleCode("COM2008")).thenReturn(java.util.Optional.of(existingModule));
        when(moduleRepository.save(ArgumentMatchers.any(Module.class))).thenReturn(existingModule);

        Module toUpdate = new Module("COM2008", "Advanced Software Engineering", "Computer Science", Module.DegreeLevel.UNDERGRADUATE);
        Module updatedModule = classUnderTest.updateModule("COM2008", toUpdate, null, null, null);

        assertThat(updatedModule.getModuleName()).isEqualTo("Advanced Software Engineering");
    }

}

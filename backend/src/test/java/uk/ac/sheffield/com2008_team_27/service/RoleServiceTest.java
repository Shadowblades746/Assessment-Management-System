package uk.ac.sheffield.com2008_team_27.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.when;

import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.exceptions.NotAcademicStaffException;
import uk.ac.sheffield.com2008_team_27.exceptions.NotExamsOfficerException;
import uk.ac.sheffield.com2008_team_27.exceptions.UserDeletedException;
import uk.ac.sheffield.com2008_team_27.exceptions.UserDoesNotExistException;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRoleRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;
import uk.ac.sheffield.com2008_team_27.service.RoleService;
import uk.ac.sheffield.com2008_team_27.service.ModuleService;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private ModuleRoleRepository moduleRoleRepository;
    @Mock private ModuleRepository moduleRepository;

    @InjectMocks
    private RoleService classUnderTest;

    @Test
    public void promoteToExamsOfficer_FailsNotAcademicStaff(){
        int userId = 1;
        var user = Mockito.mock(uk.ac.sheffield.com2008_team_27.domain.User.class);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(user.hasRole("ROLE_ACADEMIC_STAFF")).thenReturn(false);

        NotAcademicStaffException exception = Assertions.assertThrows(NotAcademicStaffException.class, () -> {
            classUnderTest.promoteToExamsOfficer(userId);
        });
    }

    @Test
    public void demoteToExamsOfficer_CantDemoteSelf(){
        int userId = 1;
        int actingUserId = 1;

        NotExamsOfficerException exception = Assertions.assertThrows(NotExamsOfficerException.class, () -> {
            classUnderTest.demoteToExamsOfficer(userId, actingUserId);
        });
        assertThat(exception.getMessage()).isEqualTo("Exams officer: " + userId + " can't demote themselves");
    }

    @Test
    public void demoteToExamsOfficer_FailsNotExamsOfficer(){
        int userId = 1;
        int actingUserId = 2;
        var user = Mockito.mock(uk.ac.sheffield.com2008_team_27.domain.User.class);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(user.hasRole("ROLE_EXAMS_OFFICER")).thenReturn(false);
        when(user.isActive()).thenReturn(true);

        NotExamsOfficerException exception = Assertions.assertThrows(NotExamsOfficerException.class, () -> {
            classUnderTest.demoteToExamsOfficer(userId, actingUserId);
        });
    }

    @Test
    public void updateUserRole_UserNotFound(){
        int userId = 1;
        String newRole = "ROLE_EXAMS_OFFICER";
        //when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        UserDoesNotExistException exception = Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            classUnderTest.updateUserRole(userId, newRole);
        });
    }
}

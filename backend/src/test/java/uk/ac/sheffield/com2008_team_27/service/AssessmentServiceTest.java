package uk.ac.sheffield.com2008_team_27.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.config.Authorities;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.*;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import uk.ac.sheffield.com2008_team_27.exceptions.*;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRoleRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;
    @Mock
    private ModuleRoleRepository moduleRoleRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private AssessmentService classUnderTest;

    private Module makeModule(String code) {
        Module m = new Module();
        m.setModuleCode(code);
        m.setModuleName("sześć siedem");
        m.setSchool("kapka");
        m.setDegreeLevel(Module.DegreeLevel.UNDERGRADUATE);
        return m;
    }

    private User makeUser(int id) {
        User u = new User();
        u.setId(id);
        u.setActive(true);
        u.setRole(Authorities.ROLE_ACADEMIC_STAFF);
        return u;
    }

    private Authentication mockAuthWithRole(String role) {
        Authentication auth = mock(Authentication.class);
        Mockito.lenient().when(auth.getAuthorities()).thenAnswer(invocation -> new ArrayList<SimpleGrantedAuthority>() {{
            add(new SimpleGrantedAuthority(role));
        }});
        return auth;
    }

    private Authentication mockAuthWithEmail(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        return auth;
    }

    @Test
    public void createAssessment_allowsTeachingSupport() {
        Authentication auth = mockAuthWithRole(Authorities.SCOPED_TEACHING_SUPPORT);

        Module module = makeModule("COM2068");
        User checker = makeUser(10);
        User setter = makeUser(20);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setTitle("CW6");
        dto.setType(Assessment.Type.COURSE_WORK);
        dto.setCheckerID(checker.getId());
        dto.setSetterID(setter.getId());

        Assessment toCreate = dto.toEntity(module);

        when(userRepository.findById(checker.getId())).thenReturn(Optional.of(checker));
        when(userRepository.findById(setter.getId())).thenReturn(Optional.of(setter));
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(toCreate);

        Assessment saved = classUnderTest.createAssessment(dto, module, auth);

        assertThat(saved).isEqualTo(toCreate);
        verify(assessmentRepository).save(argThat(a ->
                a.getTitle().equals("CW6") &&
                        a.getModuleCode().equals("COM2068") &&
                        a.getCheckerID().equals(checker.getId()) &&
                        a.getSetterID().equals(setter.getId())
        ));
    }

    @Test
    public void createAssessment_deniesIfNoValidRole() {
        Authentication auth = mockAuthWithRole("ROLE_USER");

        Module module = makeModule("COM2066");
        AssessmentDTO dto = new AssessmentDTO();
        dto.setTitle("CW66");
        dto.setType(Assessment.Type.COURSE_WORK);
        dto.setCheckerID(10);
        dto.setSetterID(20);

        assertThatThrownBy(() -> classUnderTest.createAssessment(dto, module, auth))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verifyNoInteractions(assessmentRepository);
    }

    @Test
    public void createAssessment_throwsIfSetterDoesNotExist() {
        Authentication auth = mockAuthWithRole(Authorities.SCOPED_TEACHING_SUPPORT);

        Module module = makeModule("COM2420");
        User checker = makeUser(2);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setTitle("CW4");
        dto.setType(Assessment.Type.COURSE_WORK);
        dto.setCheckerID(2);
        dto.setSetterID(1);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classUnderTest.createAssessment(dto, module, auth))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining("Setter with id: 1 does not exist");
    }

    @Test
    public void createAssessment_rejectsNullAssessment() {
        Authentication auth = mock(Authentication.class);

        assertThatThrownBy(() -> classUnderTest.createAssessment(null, makeModule("COM2008"), auth))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void updateAssessment_mutatesFieldsAndSaves() {
        Module module = makeModule("BLOB2008");
        User oldChecker = makeUser(10);
        User oldSetter = makeUser(20);
        User newChecker = makeUser(30);
        User newSetter = makeUser(40);

        Coursework stored = new Coursework(
                "have fun", Assessment.Type.COURSE_WORK, oldChecker.getId(), oldSetter.getId(), module,
                LocalDateTime.now(), null, null
        );

        when(assessmentRepository.findById_ModuleCodeAndId_Title(module.getModuleCode(), "have fun"))
                .thenReturn(Optional.of(stored));

        when(userRepository.findById(newChecker.getId())).thenReturn(Optional.of(newChecker));
        when(userRepository.findById(newSetter.getId())).thenReturn(Optional.of(newSetter));

        Authentication auth = mock(Authentication.class);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setCheckerID(newChecker.getId());
        dto.setSetterID(newSetter.getId());
        dto.setStatus(Assessment.AssessmentStatus.MARKING_STANDARDISATION);
        dto.setType(Assessment.Type.COURSE_WORK);
        dto.setSetDate(LocalDateTime.now().plusDays(1));

        when(assessmentRepository.save(any(Assessment.class))).thenAnswer(inv -> inv.getArgument(0));

        Assessment updated = classUnderTest.updateAssessment(module.getModuleCode(), "have fun", dto, auth);

        assertThat(updated.getCheckerID()).isEqualTo(newChecker.getId());
        assertThat(updated.getSetterID()).isEqualTo(newSetter.getId());
        assertThat(updated.getStatus()).isEqualTo(Assessment.AssessmentStatus.MARKING_STANDARDISATION);
        verify(assessmentRepository).save(updated);
    }

    @Test
    public void updateAssessment_throwsIfAssessmentNotFound() {
        when(assessmentRepository.findById_ModuleCodeAndId_Title("XOXOXO", "YAYAYA"))
                .thenReturn(Optional.empty());

        Authentication auth = mock(Authentication.class);
        AssessmentDTO dto = new AssessmentDTO();

        assertThatThrownBy(() -> classUnderTest.updateAssessment("XOXOXO", "YAYAYA", dto, auth))
                .isInstanceOf(AssessmentDoesNotExistException.class);
    }

    @Test
    public void updateAssessment_throwsIfCheckerDoesNotExist() {
        Module module = makeModule("COM1005");
        User setter = makeUser(20);

        Coursework stored = new Coursework(
                "EF1", Assessment.Type.COURSE_WORK, 10, setter.getId(), module,
                LocalDateTime.now(), null, null
        );

        when(assessmentRepository.findById_ModuleCodeAndId_Title(module.getModuleCode(), "EF1"))
                .thenReturn(Optional.of(stored));
        when(userRepository.findById(10)).thenReturn(Optional.empty());

        Authentication auth = mock(Authentication.class);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setCheckerID(10);

        assertThatThrownBy(() -> classUnderTest.updateAssessment(module.getModuleCode(), "EF1", dto, auth))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Checker with id: 10 does not exist");
    }

    @Test
    public void updateAssessment_throwsIfSetterDoesNotExist() {
        Module module = makeModule("COT180");
        User checker = makeUser(10);

        Coursework stored = new Coursework(
                "ERA7", Assessment.Type.COURSE_WORK, checker.getId(), 20, module,
                LocalDateTime.now(), null, null
        );

        when(assessmentRepository.findById_ModuleCodeAndId_Title(module.getModuleCode(), "ERA7"))
                .thenReturn(Optional.of(stored));
        when(userRepository.findById(20)).thenReturn(Optional.empty());

        Authentication auth = mock(Authentication.class);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setSetterID(20);

        assertThatThrownBy(() -> classUnderTest.updateAssessment(module.getModuleCode(), "ERA7", dto, auth))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Setter with id: 20 does not exist");
    }

    @Test
    public void updateAssessment_throwsIfCheckerIsModuleLead() {
        Module module = makeModule("COS360");
        User checker = makeUser(10);
        User setter = makeUser(20);

        Coursework stored = new Coursework(
                "BJN3", Assessment.Type.COURSE_WORK, 0, setter.getId(), module,
                LocalDateTime.now(), null, null
        );

        when(assessmentRepository.findById_ModuleCodeAndId_Title(module.getModuleCode(), "BJN3"))
                .thenReturn(Optional.of(stored));

        Authentication auth = mock(Authentication.class);

        AssessmentDTO dto = new AssessmentDTO();
        dto.setCheckerID(checker.getId());

        assertThatThrownBy(() -> classUnderTest.updateAssessment(module.getModuleCode(), "BJN3", dto, auth))
                .isInstanceOf(UserDoesNotExistException.class);

    }

    @Test
    public void deleteAssessment_Succeeds() {
        Module module = makeModule("SIN2090");

        Coursework stored = new Coursework(
                "CE5", Assessment.Type.COURSE_WORK, 1, 2, module,
                LocalDateTime.now(), null, null
        );

        when(assessmentRepository.findById_ModuleCodeAndId_Title(module.getModuleCode(), "CE5"))
                .thenReturn(Optional.of(stored));

        Authentication auth = mock(Authentication.class);

        classUnderTest.deleteAssessment(module.getModuleCode(), "CE5", auth);

        verify(assessmentRepository).delete(stored);
    }

    @Test
    public void submitFeedback_updatesFeedback() {
        Module module = makeModule("TEMBONCE");
        FormalExam exam = new FormalExam(
                "Exam1", Assessment.Type.FORMAL_EXAM, 10, 20, module, LocalDateTime.now(), 99, null
        );
        exam.setStatus(Assessment.AssessmentStatus.DRAFT);
        exam.setCurrentProcessIndex(0);

        when(assessmentRepository.findById_ModuleCodeAndId_Title("TEMBONCE", "Exam1"))
                .thenReturn(Optional.of(exam));
        when(assessmentRepository.save(any(FormalExam.class))).thenAnswer(inv -> inv.getArgument(0));

        Assessment returned = classUnderTest.submitFeedback("TEMBONCE", "Exam1", 42, "Did Good ♥", true, true);

        assertThat(returned.getPreviousFeedback()).isEqualTo("Did Good ♥");
        verify(assessmentRepository).save(exam);
    }

    @Test
    public void submitFeedback_throwsIfAssessmentMissing() {
        when(assessmentRepository.findById_ModuleCodeAndId_Title("A", "B"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> classUnderTest.submitFeedback("A", "B", 1, "x", true, true))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void advanceStatus_Succeeds() {
        Module module = makeModule("XMAS");

        Assessment assessment = new Coursework(
                "Merry Christmas",
                Assessment.Type.COURSE_WORK,
                10,
                20,
                module,
                LocalDateTime.now(),
                null,
                null
        );

        List<Assessment.AssessmentStatus> process = assessment.getProcess();

        assessment.setCurrentProcessIndex(0);
        assessment.setStatus(process.get(0));

        classUnderTest.advanceStatus(assessment, Assessment.ProgressDirection.FORWARD, true);

        assertThat(assessment.getCurrentProcessIndex()).isEqualTo(1);
        assertThat(assessment.getStatus()).isEqualTo(process.get(1));
    }

    @Test
    public void advanceStatus_throwsIfCantMoveBackwards() {
        Module module = makeModule("NEWYEAR");

        Assessment assessment = new Coursework(
                "Happy New Year",
                Assessment.Type.COURSE_WORK,
                10,
                20,
                module,
                LocalDateTime.now(),
                null,
                null
        );

        List<Assessment.AssessmentStatus> process = assessment.getProcess();

        assessment.setCurrentProcessIndex(0);
        assessment.setStatus(process.get(0));

        assertThatThrownBy(() ->
                classUnderTest.advanceStatus(assessment, Assessment.ProgressDirection.BACKWARD, true)
        )
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);

        assertThat(assessment.getCurrentProcessIndex()).isEqualTo(0);
        assertThat(assessment.getStatus()).isEqualTo(process.get(0));
    }

    @Test
    public void checkPermission_allowsOverrideRoles() {
        Authentication auth = mockAuthWithRole("ROLE_ADMIN");
        User user = mock(User.class);

        Module module = makeModule("COM2008");
        Assessment assessment = new Coursework("CW1", Assessment.Type.COURSE_WORK, null, null,
                module, LocalDateTime.now(), null, null);

        when(userService.getUserByEmail(any())).thenReturn(user);
        when(user.hasRole(Authorities.ROLE_TEACHING_SUPPORT)).thenReturn(true);
        when(moduleRepository.findByModuleCode("COM2008")).thenReturn(Optional.of(module));

        // Should NOT throw
        classUnderTest.checkAssessmentProgressPermission(assessment, auth);
    }


    @Test
    public void checkPermission_allowsSetterAtDraft() {
        Authentication auth = mockAuthWithEmail("setter@uni.com");
        User user = makeUser(10);

        Module module = makeModule("COM2008");
        Assessment assessment = new Coursework("CW1", Assessment.Type.COURSE_WORK,
                user.getId(), 50, module, LocalDateTime.now(), null, null);
        assessment.setStatus(Assessment.AssessmentStatus.DRAFT);

        when(userService.getUserByEmail("setter@uni.com")).thenReturn(user);
        when(moduleRepository.findByModuleCode("COM2008")).thenReturn(Optional.of(module));
        when(moduleRoleRepository.findByModule_ModuleCode("COM2008")).thenReturn(List.of());

        classUnderTest.checkAssessmentProgressPermission(assessment, auth);
    }


    @Test
    public void checkPermission_deniesSetterAtNeedsChecking() {
        Authentication auth = mockAuthWithEmail("setter@uni.com");
        User user = makeUser(10);

        Module module = makeModule("COM2008");
        Assessment assessment = new Coursework("CW1", Assessment.Type.COURSE_WORK,
                user.getId(), null, module, LocalDateTime.now(), null, null);
        assessment.setStatus(Assessment.AssessmentStatus.NEEDS_CHECKING);

        when(userService.getUserByEmail("setter@uni.com")).thenReturn(user);
        when(moduleRepository.findByModuleCode("COM2008")).thenReturn(Optional.of(module));
        when(moduleRoleRepository.findByModule_ModuleCode("COM2008")).thenReturn(List.of());

        assertThatThrownBy(() ->
                classUnderTest.checkAssessmentProgressPermission(assessment, auth)
        )
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void checkPermission_allowsModuleLeadAtDraft() {
        Authentication auth = mockAuthWithEmail("lead@uni.com");
        User user = makeUser(50);

        Module module = makeModule("COM2008");
        Assessment assessment = new Coursework("CW1", Assessment.Type.COURSE_WORK,
                null, null, module, LocalDateTime.now(), null, null);
        assessment.setStatus(Assessment.AssessmentStatus.DRAFT);

        ModuleRole leadRole = new ModuleRole(user, module, ModuleRole.Role.LEAD);

        when(userService.getUserByEmail("lead@uni.com")).thenReturn(user);
        when(moduleRepository.findByModuleCode("COM2008")).thenReturn(Optional.of(module));
        when(moduleRoleRepository.findByModule_ModuleCode("COM2008"))
                .thenReturn(List.of(leadRole));

        // Should NOT throw
        classUnderTest.checkAssessmentProgressPermission(assessment, auth);
    }


    @Test
    public void checkPermission_throwsIfModuleDoesNotExist() {
        Authentication auth = mockAuthWithEmail("user@uni.com");
        User user = makeUser(10);

        Assessment assessment = mock(Assessment.class);
        when(assessment.getModuleCode()).thenReturn("MISSING");

        when(userService.getUserByEmail("user@uni.com")).thenReturn(user);
        when(moduleRepository.findByModuleCode("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                classUnderTest.checkAssessmentProgressPermission(assessment, auth)
        )
                .isInstanceOf(ModuleDoesNotExistException.class)
                .hasMessageContaining("Module: MISSING does not exist");
    }

    @Test
    public void getNextIndex_forwardIncrementsIndex() {
        Module module = makeModule("COM2008");
        Coursework cw = new Coursework("CW1", Assessment.Type.COURSE_WORK, 1, 2,
                module, LocalDateTime.now(), null, null);
        cw.setCurrentProcessIndex(0);


        int next = classUnderTest.getNextIndex(cw, Assessment.ProgressDirection.FORWARD, true);

        assertThat(next).isEqualTo(1);
    }


}

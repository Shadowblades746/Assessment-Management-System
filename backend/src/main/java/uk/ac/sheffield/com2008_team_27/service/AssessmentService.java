package uk.ac.sheffield.com2008_team_27.service;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.config.Authorities;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Coursework;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.FormalExam;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.InSemester;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.exceptions.*;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRoleRepository;
import uk.ac.sheffield.com2008_team_27.service.ModuleService;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import uk.ac.sheffield.com2008_team_27.dto.ModuleDTO;
import uk.ac.sheffield.com2008_team_27.dto.RoleAssessmentDTO;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/*

Look into if this needs dto

 */
@Service
@Transactional
public class AssessmentService {

    private final UserService userService;
    private final ModuleRoleRepository moduleRoleRepository;
    private final UserRepository userRepository;
    private  AssessmentRepository assessmentRepository;
    private ModuleRepository moduleRepository;

    private static final String NOT_AUTHED_TO_DELETE = "Not authorised to delete assessment";

    @Autowired
    public AssessmentService(AssessmentRepository assessmentRepository, ModuleRepository moduleRepository, UserService userService, ModuleRoleRepository moduleRoleRepository, UserRepository userRepository) {
        this.assessmentRepository = assessmentRepository;
        this.moduleRepository = moduleRepository;
        this.userService = userService;
        this.moduleRoleRepository = moduleRoleRepository;
        this.userRepository = userRepository;
    }

    private static boolean isTeachingSupportStaff(Authentication auth) {
        return auth
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(Authorities.SCOPED_TEACHING_SUPPORT));
    }

    private static boolean isAcademicStaffMember(Authentication auth) {
        return auth
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(Authorities.SCOPED_ACADEMIC_STAFF));
    }

    // Create, Update, Delete, Get Assessments (all, name, etc..)
    @Transactional
    public Assessment createAssessment(AssessmentDTO  assessmentDTO, Module module, Authentication auth) {
        Assessment assessment = assessmentDTO.toEntity(module);
        // need to verify that module exists
        if (!isTeachingSupportStaff(auth) &&  !isAcademicStaffMember(auth)) {
            // Make new exception
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create assessment");
        }

        Integer setterId = assessment.getSetterID();
        if (setterId != null) {
            User setter = userRepository.findById(setterId)
                    .orElseThrow(() -> new UserDoesNotExistException("Setter with id: " + setterId + " does not exist"));

            if (setter.getRole() != Authorities.ROLE_ACADEMIC_STAFF &&  setter.getRole() != Authorities.ROLE_EXAMS_OFFICER) {
                throw new NotAcademicStaffException("Setter: " + setter.getEmail() + " is not an academics staff member of exams officer");
            }

            if (!setter.isActive()) {
                throw new UserDeletedException("Setter with id: " + setterId + " is already deleted");
            }
        }

        Integer checkerId = assessment.getCheckerID();
        if (checkerId != null) {
            User checker = userRepository.findById(checkerId)
                    .orElseThrow(() -> new UserDoesNotExistException("Checker with id: " + checkerId + " does not exist"));

            if (checkerId == setterId) {
                throw new ForbidCheckingException("Checker: " + checker.getEmail() + " is already a setter");
            }

            if (checker.getRole() != Authorities.ROLE_ACADEMIC_STAFF &&  checker.getRole() != Authorities.ROLE_EXAMS_OFFICER) {
                throw new NotAcademicStaffException("Checker: " + checker.getEmail() + " is not an academic staff member of exams officer");
            }

            if (isCheckerStaffOrModuleLead(checkerId, assessment.getModuleCode())) {
                throw new ForbidCheckingException("User with id: " + checker.getEmail() + " cannot be a checker" +
                        " as they are either a staff or lead for the module");
            }

            if (!checker.isActive()) {
                throw new UserDeletedException("Checker with id: " + checkerId + " is already deleted");
            }
        }


        Integer externalExaminerId = assessmentDTO.getExternalExaminerID();
        if (externalExaminerId != null) {
            User externalExaminer = userRepository.findById(externalExaminerId)
                    .orElseThrow(() -> new UserDoesNotExistException("External examiner with id: " + externalExaminerId + " does not exist"));

            if (!externalExaminer.isActive()) {
                throw new UserDeletedException("External  examiner with id: " + externalExaminerId + " is already deleted");
            }

            if (externalExaminer.getRole() != Authorities.ROLE_EXTERNAL_EXAMINER) {
                throw new NotExternalExaminerException("User: " + externalExaminer.getEmail() + " is not an external examiner");
            }
        }

        // Do we need to add a check here for the checker, make sure there not involved in delivery so module lead or module staff for module
        try {
            Assessment savedAssessment = assessmentRepository.save(assessment);
            assessmentRepository.flush();
            return savedAssessment;
        } catch (DataIntegrityViolationException e) {
            throw new AssessmentExistsException("Assessment with title: " + assessment.getTitle() + " and module code: " + assessment.getModuleCode() + " exists");
        }
    }

    private boolean isCheckerStaffOrModuleLead(Integer userId, String moduleCode) {
        ModuleRole moduleRole = moduleRoleRepository.findByModule_ModuleCodeAndUser_Id(moduleCode, userId)
                .orElse(null);

        if  (moduleRole == null) {
            return false;
        }
        ModuleRole.Role role = moduleRole.getRole();
        return role == ModuleRole.Role.STAFF || role == ModuleRole.Role.LEAD;
    }

    public Assessment updateAssessment(String moduleCode,
                                       String title,
                                       AssessmentDTO updatedAssessment,
                                       Authentication auth) {
        Assessment storedAssessment = getAssessment(moduleCode, title);
        checkAssessmentOwnership(storedAssessment, auth);

        Integer setterId = updatedAssessment.getSetterID();
        if (setterId!= null) {
            User setter = userRepository.findById(updatedAssessment.getSetterID())
                    .orElseThrow(() -> new UserDoesNotExistException("Setter with id: " +  updatedAssessment.getSetterID() + " does not exist"));

            if (!setter.isActive()) {
                throw new UserDeletedException("The setter with id: " + updatedAssessment.getSetterID() + " is not active");
            }

            if (setter.getRole() != Authorities.ROLE_ACADEMIC_STAFF &&  setter.getRole() != Authorities.ROLE_EXAMS_OFFICER)
                throw new NotAcademicStaffException("Setter: " + setter.getEmail() + " is not an academic staff member of exams officer");
            storedAssessment.setSetterID(updatedAssessment.getSetterID());
        }

        Integer checkerId = updatedAssessment.getCheckerID();
        if (checkerId!= null) {

            User checker = userRepository.findById(updatedAssessment.getCheckerID())
                            .orElseThrow(() -> new UserDoesNotExistException("Checker with id: " +  updatedAssessment.getCheckerID() + " does not exist"));

            if (checkerId == setterId) {
                throw new ForbidCheckingException("Checker: " + checker.getEmail() + " is already assigned as a setter");
            }

            if (isCheckerStaffOrModuleLead(updatedAssessment.getCheckerID(), updatedAssessment.getModuleCode())) {
                throw new ForbidCheckingException("User with id: " + checker.getEmail() + " cannot be a checker" +
                        " as they are either a staff or lead for the module");
            }

            if (checker.getRole() != Authorities.ROLE_ACADEMIC_STAFF &&  checker.getRole() != Authorities.ROLE_EXAMS_OFFICER) {
                throw new NotAcademicStaffException("Checker: " + checker.getEmail() + " is not an academics staff member of exams officer");
            }
            if (!checker.isActive()) {
                throw new UserDeletedException("The checker with id: " + updatedAssessment.getCheckerID() + " is not active");
            }
            storedAssessment.setCheckerID(updatedAssessment.getCheckerID());
        }

        if (updatedAssessment.getStatus() != null) {
            storedAssessment.setStatus(updatedAssessment.getStatus());
        }
        if (updatedAssessment.getType() != null) {
            storedAssessment.setType(updatedAssessment.getType());
        }
        if (updatedAssessment.getSetDate() != null) {
            storedAssessment.setSetDate(updatedAssessment.getSetDate());
        }


        if (updatedAssessment.getDueDate() != null && storedAssessment instanceof Coursework coursework) {
            coursework.setDueDate(updatedAssessment.getDueDate());
        }
        if (updatedAssessment.isAutograded() != null && storedAssessment instanceof InSemester inSemester) {
            inSemester.setAutograded(updatedAssessment.isAutograded());
        }

        Integer externalExaminerId = updatedAssessment.getExternalExaminerID();
        if (externalExaminerId != null) {
            User externalExaminer = userRepository.findById(externalExaminerId)
                    .orElseThrow(() -> new  UserDoesNotExistException("External examiner with id: " + externalExaminerId + " does not exist"));

            if (!externalExaminer.isActive()) {
                throw new UserDeletedException("External  examiner with id: " + externalExaminerId + " is already deleted");
            }

            if (externalExaminer.getRole() != Authorities.ROLE_EXTERNAL_EXAMINER) {
                throw new NotExternalExaminerException("User: " + externalExaminer.getEmail() + " is not an external examiner");
            }

            if (storedAssessment instanceof FormalExam formalExam) {
                formalExam.setExternalExaminerId(externalExaminerId);
            }
        }
        return assessmentRepository.save(storedAssessment);
    }

    public Assessment submitFeedback(String moduleCode,
                                     String title,
                                     int userId,
                                     String response,
                                     boolean isForward,
                                     boolean override) {

        Assessment assessment = assessmentRepository
                .findById_ModuleCodeAndId_Title(moduleCode, title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such assessment found"));

        // simply overwrite previous feedback
        assessment.setPreviousFeedback(response);

        // move to next stage
        if (isForward) {
            advanceStatus(assessment, Assessment.ProgressDirection.FORWARD, override);
        }
        else {
            advanceStatus(assessment, Assessment.ProgressDirection.BACKWARD, override);
        }


        return assessmentRepository.save(assessment);
    }

    public boolean isFeedbackRequired(Assessment.AssessmentStatus status) {
        return status == Assessment.AssessmentStatus.NEEDS_EE_FEEDBACK
                || status == Assessment.AssessmentStatus.NEEDS_SETTER_FEEDBACK;
    }




    public void deleteAssessment(String moduleCode, String title, Authentication auth) {
        Assessment assessmentToDelete = getAssessment(moduleCode, title);
        checkAssessmentOwnership(assessmentToDelete, auth);
        assessmentRepository.delete(assessmentToDelete);
    }

    public Assessment getAssessment(String moduleCode, String title) {
        return assessmentRepository.findById_ModuleCodeAndId_Title(moduleCode, title)
                .orElseThrow(() -> new AssessmentDoesNotExistException("Assessment inside: " + moduleCode + " does not exist"));
    }

    public List<RoleAssessmentDTO> getAssessmentRolesInModule(String moduleCode, Integer userId, int count)
    {
        List<RoleAssessmentDTO> assessments = new ArrayList<>();

        List<Assessment> setterAssessments = moduleCode.equals("all") ? assessmentRepository.findAllBySetterID(userId)
                : assessmentRepository.findAllById_ModuleCodeAndSetterID(moduleCode, userId);

        List<Assessment> checkerAssessments = moduleCode.equals("all") ? assessmentRepository.findAllByCheckerID(userId)
                : assessmentRepository.findAllById_ModuleCodeAndCheckerID(moduleCode, userId);

        // First get the setter ID roles
        for  (Assessment assessment : setterAssessments) {
            assessments.add(new RoleAssessmentDTO("setter", assessment));
        }

        // Second get checker ID roles
        for (Assessment assessment : checkerAssessments) {
            assessments.add(new RoleAssessmentDTO("checker", assessment));
        }

        if (assessments.size() < count) {
            return assessments;
        } else {
            return assessments.subList(0, count);
        }
    }

    public List<Map<String, Object>> getAssessmentsWithRolesForUser(String moduleCode, Integer userId, int count) {
        List<RoleAssessmentDTO> rawRoles = new ArrayList<>();

        // setter/checker roles
        List<Assessment> setterAssessments = moduleCode.equals("all") ?
                assessmentRepository.findAllBySetterID(userId) :
                assessmentRepository.findAllById_ModuleCodeAndSetterID(moduleCode, userId);

        List<Assessment> checkerAssessments = moduleCode.equals("all") ?
                assessmentRepository.findAllByCheckerID(userId) :
                assessmentRepository.findAllById_ModuleCodeAndCheckerID(moduleCode, userId);

        for (Assessment a : setterAssessments) {
            rawRoles.add(new RoleAssessmentDTO("SETTER", a));
        }
        for (Assessment a : checkerAssessments) {
            rawRoles.add(new RoleAssessmentDTO("CHECKER", a));
        }

        // module-level roles (LEAD, MODERATOR, STAFF)
        List<ModuleRole> userModuleRoles = moduleRoleRepository.findByUserId(userId);
        for (ModuleRole mr : userModuleRoles) {
            List<Assessment> moduleAssessments = assessmentRepository.findAllById_ModuleCode(mr.getModule().getModuleCode());
            for (Assessment a : moduleAssessments) {
                rawRoles.add(new RoleAssessmentDTO(mr.getRole().name(), a));
            }
        }

        // exams officer role
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isExamsOfficer = user.hasRole(Authorities.ROLE_EXAMS_OFFICER);

        if (isExamsOfficer) {
            List<Assessment> allAssessments = moduleCode.equals("all")
                    ? assessmentRepository.findAll()
                    : assessmentRepository.findAllById_ModuleCode(moduleCode);

            for (Assessment a : allAssessments) {
                rawRoles.add(new RoleAssessmentDTO("EXAMS_OFFICER", a));
            }
        }


        // external examiner role - only for FormalExam
        for (Assessment a : assessmentRepository.findAll()) {  // or findAllById_ModuleCode(moduleCode)
            if (a instanceof FormalExam formalExam) {
                if (formalExam.getExternalExaminerId() != null && formalExam.getExternalExaminerId().equals(userId)) {
                    rawRoles.add(new RoleAssessmentDTO("EXTERNAL_EXAMINER", a));
                }
            }
        }

        List<Assessment> formalExams = assessmentRepository.findAllById_ModuleCode(moduleCode)
                .stream()
                .filter(a -> a instanceof FormalExam)
                .collect(Collectors.toList());

        // filter only FormalExam assessments
        for (Assessment a : formalExams) {
            if (a instanceof FormalExam formalExam) {
                if (formalExam.getExternalExaminerId() != null && formalExam.getExternalExaminerId().equals(userId)) {
                    rawRoles.add(new RoleAssessmentDTO("EXTERNAL_EXAMINER", formalExam));
                }
            }
        }

        // group by assessment and build Map
        Map<Assessment, List<String>> grouped = rawRoles.stream()
                .collect(Collectors.groupingBy(
                        RoleAssessmentDTO::getAssessment,
                        Collectors.mapping(RoleAssessmentDTO::getRole, Collectors.toList())
                ));

        List<Map<String, Object>> result = grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("assessment", e.getKey().toDTO());
                    map.put("roles", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        // apply count limit if needed
        if (count > 0 && result.size() > count) {
            return result.subList(0, count);
        }
        return result;
    }


    public List<Assessment> getAssessmentsForModule(String moduleCode) {
        return assessmentRepository.findAllById_ModuleCode(moduleCode);


    }

    public void advanceStatus(Assessment assessment, Assessment.ProgressDirection direction, boolean override) {
        int nextIndex = calculateNextIndex(assessment, direction, override);

        // if backward is blocked, throw an exception
        if (direction == Assessment.ProgressDirection.BACKWARD && nextIndex == assessment.getCurrentProcessIndex()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot move backward from current stage: " + assessment.getStatus());
        }


        assessment.setCurrentProcessIndex(nextIndex);
        assessment.setStatus(assessment.getProcess().get(nextIndex));
    }

    public boolean checkIfExamsOrSupport(Authentication auth) {
        // overrides to progress at any stage
        User user = userService.getUserByEmail(auth.getName());
        return user.hasRole(Authorities.ROLE_TEACHING_SUPPORT) ||
                user.hasRole(Authorities.ROLE_EXAMS_OFFICER); // full override
    }



    public int getNextIndex(Assessment assessment, Assessment.ProgressDirection direction, boolean override) {
        // calls same helper but doesnt update status
        return calculateNextIndex(assessment, direction, override);
    }

    /**
     * Core logic for calculating the next index for forward/backward.
     * Does not modify the assessment.
     */
    private int calculateNextIndex(Assessment assessment, Assessment.ProgressDirection direction, boolean override) {
        List<Assessment.AssessmentStatus> process = assessment.getProcess();
        int index = assessment.getCurrentProcessIndex();
        int nextIndex = index;
        boolean isFormalExam = assessment.getType() == Assessment.Type.FORMAL_EXAM;

        if (direction == Assessment.ProgressDirection.FORWARD) {
            // FormalExam special: skip NEEDS_CHECKING after first pass
            if (isFormalExam &&
                    process.get(index) == Assessment.AssessmentStatus.DRAFT &&
                    ((FormalExam) assessment).hasSeenNeedsChecking()) {
                nextIndex += 2; // DRAFT -> NEEDS_EO_CHECKING
            } else {
                nextIndex = Math.min(index + 1, process.size() - 1);
            }

            // skip MARKING_STANDARDISATION if not team-marked (applies to any assessment)
            if (process.get(nextIndex) == Assessment.AssessmentStatus.MARKING_STANDARDISATION
                    && !isTeamMarked(assessment)) {
                nextIndex++;
            }

        } else { // BACKWARD
            Assessment.AssessmentStatus currentStatus = process.get(index);

            // Block backward for any assessment if stage forbids it
            if (!override) {
                if ((currentStatus == Assessment.AssessmentStatus.MARKING_STANDARDISATION && !isTeamMarked(assessment)) ||
                        currentStatus == Assessment.AssessmentStatus.NEEDS_MARKING) {
                    return index; // cannot go backward
                }
            }

            // FormalExam special: NEEDS_EO_CHECKING -> DRAFT
            if (isFormalExam && index == 2) {
                nextIndex = 0;
            } else {
                nextIndex = Math.max(index - 1, 0);
            }

            // skip MARKING_STANDARDISATION backwards
            if (process.get(nextIndex) == Assessment.AssessmentStatus.MARKING_STANDARDISATION
                    && !isTeamMarked(assessment)) {
                nextIndex--;
            }
        }

        return nextIndex;
    }




    /*
        testing purposes -> to reset a formal exam so NEEDS_CHECKING can be iterated over again
     */
    public void restartFormalExam(Assessment assessment) {
        if (!(assessment instanceof FormalExam)) {
            throw new NotFormalExamException("Only Formal Exams can be reset. Assessment type is: " +  assessment.getType());
        }

        FormalExam exam = (FormalExam) assessment;

        exam.resetNeedsCheckingFlag();
        exam.setCurrentProcessIndex(0);
        exam.setStatus(Assessment.AssessmentStatus.DRAFT);

        assessmentRepository.save(exam);
    }

    public boolean isTeamMarked(Assessment assessment) {
        Module module = moduleRepository.findByModuleCode(assessment.getModuleCode())
                .orElseThrow(() -> new ModuleDoesNotExistException("Module: " +  assessment.getModuleCode() + " does not exist"));

        // get all staff who have a role for this module - if module markers mark all assessments
        List<ModuleRole> roles = moduleRoleRepository.findByModule_ModuleCode(module.getModuleCode());

        // count academic staff
        long academicStaffCount = roles.stream()
                .filter(r -> r.getUser().hasRole(String.valueOf(ModuleRole.Role.STAFF)))
                .count();

        return academicStaffCount > 1; // true if team-marked

    }


    private void checkAssessmentOwnership(Assessment assessment, Authentication auth) {
        // academic staff -> shouldnt update, should progress
        // teaching support staff -> should update, should progress

        // to:do
        // figure out who has assessment ownership and add
    }

    public void checkAssessmentProgressPermission(Assessment assessment, Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        Module module = moduleRepository
                .findByModuleCode(assessment.getModuleCode())
                .orElseThrow(() -> new ModuleDoesNotExistException("Module: " +  assessment.getModuleCode() + " does not exist"));

        // overrides to progress at any stage
        if (checkIfExamsOrSupport(auth)) {
            return; // full override
        }

        List<ModuleRole> roles = moduleRoleRepository.findByModule_ModuleCode(module.getModuleCode());

        boolean isModuleLead = roles.stream()
                .anyMatch(role -> role.getRole() == ModuleRole.Role.LEAD && role.getUser().equals(user));

        boolean isModerator = roles.stream()
                .anyMatch(role -> role.getRole() == ModuleRole.Role.MODERATOR && role.getUser().equals(user));

        // determine all roles for this module/assessment
        boolean isSetter      = assessment.getSetterID() == user.getId();
        boolean isChecker     = assessment.getCheckerID() == user.getId();

        boolean isModuleStaff = moduleRoleRepository
                .findByModule_ModuleCode(module.getModuleCode())
                .stream()
                .anyMatch(r -> r.getUser().equals(user));

        boolean isExternalExaminer = user.hasRole(Authorities.ROLE_EXTERNAL_EXAMINER);

        Assessment.AssessmentStatus stage = assessment.getStatus();

        switch (stage) {

            case DRAFT:
                if (isSetter || isModuleLead) return;
                break;

            case NEEDS_CHECKING:
                if (isChecker) return;
                break;

            case NEEDS_SETTER_FEEDBACK:
                if (isSetter || isModuleLead) return;
                break;

            case NEEDS_EO_CHECKING:
                if (user.hasRole(Authorities.ROLE_EXAMS_OFFICER)) return;
                break;

            case NEEDS_EE_FEEDBACK:
                if (isExternalExaminer) return;
                break;

            case SPECIFICATION_RELEASE:
            case MARKING_STANDARDISATION:
            case NEEDS_MARKING:
            case RETURNS_FEEDBACK:
                if (isModuleStaff || isModuleLead) return;
                break;

            case NEEDS_MODERATION:
                if (isModerator) return;
                break;

            case NEEDS_ADMIN_CHECK:
                if (user.hasRole(Authorities.ROLE_TEACHING_SUPPORT)) return;
                break;

            case COMPLETED:
                // only override roles (already handled at top)
                break;
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "You do not have permission to progress this assessment"
        );
    }

    public Set<String> getRequiredRolesForProgress(Assessment assessment) {

        Assessment.AssessmentStatus stage = assessment.getStatus();
        Set<String> roles = new HashSet<>();

        switch (stage) {
            case DRAFT:
                roles.add("SETTER");
                roles.add("MODULE_LEAD");
                break;

            case NEEDS_CHECKING:
                roles.add("CHECKER");
                break;

            case NEEDS_SETTER_FEEDBACK:
                roles.add("SETTER");
                roles.add("MODULE_LEAD");
                break;

            case NEEDS_EO_CHECKING:
                roles.add("EXAMS_OFFICER");
                break;

            case NEEDS_EE_FEEDBACK:
                roles.add("EXTERNAL_EXAMINER");
                break;

            case SPECIFICATION_RELEASE:
            case MARKING_STANDARDISATION:
            case NEEDS_MARKING:
            case RETURNS_FEEDBACK:
                roles.add("MODULE_STAFF");
                roles.add("MODULE_LEAD");
                break;

            case NEEDS_MODERATION:
                roles.add("MODERATOR");
                break;

            case NEEDS_ADMIN_CHECK:
                roles.add("TEACHING_SUPPORT");
                break;

            case COMPLETED:
                // no one can progress it
                break;
        }

        return roles;
    }


    public Assessment createAssessmentFromCSV(Assessment.Type type,
                                              String title,
                                              Module module)
    {
        Integer setterId = null;
        Integer checkerId = null;
        LocalDateTime date = LocalDateTime.now();

        return switch (type) {
            case IN_SEMESTER -> new InSemester(
                    title,
                    type,
                    setterId,
                    checkerId,
                    module,
                    date,
                    null,
                    false
            );

            case COURSE_WORK -> new Coursework (
                    title,
                    type,
                    setterId,
                    checkerId,
                    module,
                    date,
                    null,
                    null
            );

            case FORMAL_EXAM ->  new FormalExam (
                    title,
                    type,
                    setterId,
                    checkerId,
                    module,
                    date,
                    0,
                    null
            );
        };
    }
}

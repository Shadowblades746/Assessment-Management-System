package uk.ac.sheffield.com2008_team_27.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import uk.ac.sheffield.com2008_team_27.dto.ModuleDTO;
import uk.ac.sheffield.com2008_team_27.dto.RoleAssessmentDTO;
import uk.ac.sheffield.com2008_team_27.exceptions.ModuleDoesNotExistException;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;
import uk.ac.sheffield.com2008_team_27.service.AssessmentService;
import uk.ac.sheffield.com2008_team_27.service.ModuleService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/modules/{moduleCode}/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final ModuleService moduleService;
    private final ModuleRepository moduleRepository;
    private final AssessmentRepository assessmentRepository;

    @Autowired
    public AssessmentController(AssessmentService assessmentService, ModuleService moduleService, AssessmentRepository assessmentRepository, ModuleRepository moduleRepository) {
        this.assessmentService = assessmentService;
        this.moduleService = moduleService;
        this.assessmentRepository = assessmentRepository;
        this.moduleRepository = moduleRepository;
    }


    @GetMapping("/{title}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable String moduleCode,
                                                    @PathVariable String title) {
        if (!moduleService.doesModuleExist(moduleCode)) {
            throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
        }

        return ResponseEntity.status(HttpStatus.OK).body(assessmentService.getAssessment(moduleCode, title));
    }
    @GetMapping({"", "/"})
    public ResponseEntity<List<AssessmentDTO>> getAssessments(@PathVariable("moduleCode") String moduleCode) {
        if (!moduleService.doesModuleExist(moduleCode)) {
            throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
        }

        return ResponseEntity.status(HttpStatus.OK).body(assessmentService
                .getAssessmentsForModule(moduleCode)
                .stream()
                .map(Assessment::toDTO)
                .toList());
    }

    // Should this take in a User or just the userId?
    /*
        Output Format JSON:
            Setter:
                Assessment
            Checker:
                Assessment
            ...
     */
    @GetMapping("/role")
    public ResponseEntity<?> getRoles(@PathVariable("moduleCode") String moduleCode,
                                      @RequestParam Integer userId,
                                      @RequestParam(required = false, defaultValue = "0") Integer count) {

        if (!moduleCode.equalsIgnoreCase("all")) {
            if (!moduleService.doesModuleExist(moduleCode)) {
                throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
            }
        }

        List<RoleAssessmentDTO> assessmentRoles = assessmentService.getAssessmentRolesInModule(moduleCode, userId, count);

        return ResponseEntity.status(HttpStatus.OK).body(assessmentRoles);
    }

    @GetMapping("/expand")
    public ResponseEntity<List<Map<String, Object>>> assessmentsUserAssociated(@PathVariable("moduleCode") String moduleCode,
                                                                                            @RequestParam Integer userId,
                                                                                            @RequestParam(required = false, defaultValue = "0") Integer count) {
        // returns a list of (assessment, List<Roles>) that a user is associated with
        return ResponseEntity.ok(
                assessmentService.getAssessmentsWithRolesForUser(moduleCode, userId, count)
        );
    }


    @GetMapping("/expand/filter")
    public ResponseEntity<List<Map<String, Object>>> assessmentsActionRequired(
            @RequestParam Integer userId,
            @RequestParam(required = false, defaultValue = "0") Integer count) {

        List<Map<String, Object>> assessmentsWithRoles =
                assessmentService.getAssessmentsWithRolesForUser("all", userId, count);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Map<String, Object> entry : assessmentsWithRoles) {

            AssessmentDTO dto = (AssessmentDTO) entry.get("assessment");
            List<String> userRoles = (List<String>) entry.get("roles");

            Assessment entity = assessmentService.getAssessment(
                    dto.getModuleCode(),
                    dto.getTitle()
            );

            Set<String> requiredRoles = assessmentService.getRequiredRolesForProgress(entity);

            // compares user roles with the required roles to progress
            boolean userCanProgress = userRoles.stream().anyMatch(requiredRoles::contains);
            if (!userCanProgress) continue;

            List<Assessment.AssessmentStatus> process = entity.getProcess();
            int index = entity.getCurrentProcessIndex();

            boolean canForward = index < process.size() - 1;
            boolean canBackward = index > 0;

            String action;
            if (canForward) {
                action = "FORWARD";
            } else if (canBackward) {
                action = "BACKWARD";
            } else {
                action = "NONE";
            }

            Map<String, Object> result = new HashMap<>();
            result.put("assessment", dto);
            result.put("roles", userRoles);
            result.put("action", action); // if the user can progress

            response.add(result);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<AssessmentDTO> createAssessment(@PathVariable("moduleCode") String moduleCode,
                                                       @RequestBody AssessmentDTO assessmentDTO,
                                                       Authentication authentication) {
        if (!moduleService.doesModuleExist(moduleCode)) {
            throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
        }
        Module module = moduleRepository.getReferenceById(moduleCode);

        assessmentDTO.setModuleCode(moduleCode);
        AssessmentDTO createdAssessment = assessmentService.createAssessment(assessmentDTO, module, authentication).toDTO();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssessment);
    }

//    @PostMapping("/{title}/external-feedback")
//    public ResponseEntity<String> submitExternalFeedback(
//            @PathVariable String moduleCode,
//            @PathVariable String title,
//            @RequestParam int examinerId,
//            @RequestBody String feedback) {
//     if (!moduleService.doesModuleExist(moduleCode)) {
//         throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
//     }
//
//     assessmentService.submitExternalExaminerFeedback(moduleCode, title, examinerId, feedback);
//     return  ResponseEntity.status(HttpStatus.OK).body("External Feedback Submitted");
//
//     // make feedback one string called previousFeedback
//    }

//    @PostMapping("/{title}/setter-response")
//    public ResponseEntity<String> submitSetterResponse(
//            @PathVariable String moduleCode,
//            @PathVariable String title,
//            @RequestParam int setterId,
//            @RequestBody String response) {
//        if (!moduleService.doesModuleExist(moduleCode)) {
//            throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
//        }
//
//        assessmentService.submitSetterResponse(moduleCode, title, setterId, response);
//        return  ResponseEntity.status(HttpStatus.OK).body("Setter Response Submitted");
//
//        // make feedback one string called previousFeedback
//    }

    @PutMapping("/{title}")
    public ResponseEntity<AssessmentDTO> updateAssessment(Authentication authentication,
                                                          @PathVariable String moduleCode,
                                                          @PathVariable String title,
                                                          @RequestBody AssessmentDTO updatedAssessment) {
        if (!moduleService.doesModuleExist(moduleCode)) {
            throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
        }
        Module module = moduleRepository.getReferenceById(moduleCode);

        return ResponseEntity.ok(assessmentService.updateAssessment(moduleCode, title, updatedAssessment, authentication)
                .toDTO());
    }

    @PostMapping("/{title}/feedback")
    public ResponseEntity<String> submitFeedback(
            @PathVariable String moduleCode,
            @PathVariable String title,
            @RequestParam int userId,
            @RequestParam boolean isForward,
            @RequestBody String feedback,
            Authentication auth) {                   // boolean isForward -

        Assessment assessment = assessmentService.getAssessment(moduleCode, title);

        assessmentService.checkAssessmentProgressPermission(assessment, auth);

        boolean override = assessmentService.checkIfExamsOrSupport(auth);
        assessmentService.submitFeedback(moduleCode, title, userId, feedback, isForward, override);

        return ResponseEntity.ok("Feedback received");
    }


    @GetMapping("/{title}/forward")
    public ResponseEntity<Map<String, Object>> canGoForward(
            @PathVariable String moduleCode,
            @PathVariable String title,
            Authentication auth) {

        // returns if going forward is possible
        // returns if feedback is required

        Assessment assessment = assessmentService.getAssessment(moduleCode, title);

        // check permissions before calculating logic
        assessmentService.checkAssessmentProgressPermission(assessment, auth);

        boolean override = assessmentService.checkIfExamsOrSupport(auth);
        int nextIndex = assessmentService.getNextIndex(assessment, Assessment.ProgressDirection.FORWARD, override);
        Assessment.AssessmentStatus nextStatus = assessment.getProcess().get(nextIndex);

        Map<String, Object> body = new HashMap<>();

        body.put("currentStatus", assessment.getStatus());
        body.put("nextStatus", nextStatus);

        boolean isPossible = nextIndex != assessment.getCurrentProcessIndex(); //change is more restrictions needed
        body.put("isPossible", isPossible);

        String previousFeedback = assessment.getPreviousFeedback();
        body.put("previousFeedback", previousFeedback);

        boolean feedbackRequired = assessmentService.isFeedbackRequired(nextStatus);
        body.put("feedbackRequired", feedbackRequired);

        return ResponseEntity.ok(body);
    }


    @GetMapping("/{title}/backward")
    public ResponseEntity<Map<String, Object>> canGoBackward(
            @PathVariable String moduleCode,
            @PathVariable String title,
            Authentication auth) {

        Assessment assessment = assessmentService.getAssessment(moduleCode, title);

        // check if user has permission to move backward
        assessmentService.checkAssessmentProgressPermission(assessment, auth);

        int currentIndex = assessment.getCurrentProcessIndex();
        boolean override = assessmentService.checkIfExamsOrSupport(auth);
        int nextIndex = assessmentService.getNextIndex(assessment, Assessment.ProgressDirection.BACKWARD, override);
        Assessment.AssessmentStatus nextStatus = assessment.getProcess().get(nextIndex);

        Map<String, Object> body = new HashMap<>();

        boolean isPossible = true;

        // special rules for formal exams
        List<Assessment.AssessmentStatus> process = assessment.getProcess();
        Assessment.AssessmentStatus currentStatus = process.get(currentIndex);

        // can't go backward from MARKING_STANDARDISATION
        if (assessmentService.checkIfExamsOrSupport(auth)) {
            isPossible = true;
        }
        else if (currentStatus == Assessment.AssessmentStatus.MARKING_STANDARDISATION ||
            currentStatus == Assessment.AssessmentStatus.DRAFT) {
            isPossible = false;
        }


        // can't go backward from NEEDS_MARKING if not team-marked
        if (assessmentService.checkIfExamsOrSupport(auth)) {
            isPossible = true;
        }
        else if (currentStatus == Assessment.AssessmentStatus.NEEDS_MARKING
                && !assessmentService.isTeamMarked(assessment)) {
            isPossible = false;
        }


        body.put("currentStatus", assessment.getStatus());
        body.put("nextStatus", nextStatus);
        body.put("isPossible", isPossible);

        // backward never requires feedback for now
        body.put("feedbackRequired", false);

        String previousFeedback = assessment.getPreviousFeedback();
        body.put("previousFeedback", previousFeedback);

        return ResponseEntity.ok(body);
    }


    @PostMapping("/{title}")
    public ResponseEntity<?> updateStatus(Authentication authentication,
                                          @PathVariable String moduleCode,
                                          @PathVariable String title,
                                          @RequestParam Assessment.ProgressDirection direction) {
        Assessment assessment = assessmentService.getAssessment(moduleCode, title);
        assessmentService.checkAssessmentProgressPermission(assessment, authentication);
        boolean override = assessmentService.checkIfExamsOrSupport(authentication);
        assessmentService.advanceStatus(assessment, direction, override);
        assessmentRepository.save(assessment);
        return ResponseEntity.status(HttpStatus.OK).body(assessment.toDTO());
    }

    @GetMapping("/{title}/required-role")
    public ResponseEntity<Map<String, Object>> getRequiredProgressRole(
            @PathVariable String moduleCode,
            @PathVariable String title
    ) {
        Assessment assessment = assessmentService.getAssessment(moduleCode, title);

        Set<String> requiredRoles = assessmentService.getRequiredRolesForProgress(assessment);

        Map<String, Object> body = new HashMap<>();
        body.put("currentStatus", assessment.getStatus());
        body.put("requiredRoles", requiredRoles);

        return ResponseEntity.ok(body);
    }


    // return role needed to progress the assessment progress

    /*
        testing purposes -> to reset a formal exam so NEEDS_CHECKING can be iterated over again
     */
    @PostMapping("/{title}/reset-formal-exam")
    public ResponseEntity<Void> resetFormalExam(@PathVariable String moduleCode,
                                                @PathVariable String title) {

        Assessment assessment = assessmentService.getAssessment(moduleCode, title);

        assessmentService.restartFormalExam(assessment);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteAssessment(Authentication authentication,
                                                 @PathVariable("moduleCode") String moduleCode,
                                                 @PathVariable("title") String title) {
        if (!moduleService.doesModuleExist(moduleCode)) {
            throw new ModuleDoesNotExistException(moduleCode + " doesn't exist");
        }

        assessmentService.deleteAssessment(moduleCode, title, authentication);
        return ResponseEntity.noContent().build();
    }


}

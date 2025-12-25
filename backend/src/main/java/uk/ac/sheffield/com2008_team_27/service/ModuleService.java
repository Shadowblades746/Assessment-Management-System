package uk.ac.sheffield.com2008_team_27.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.dto.AssessmentDTO;
import uk.ac.sheffield.com2008_team_27.dto.ModuleDTO;
import uk.ac.sheffield.com2008_team_27.dto.UserDTO;
import uk.ac.sheffield.com2008_team_27.exceptions.*;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRoleRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModuleService {

    private static final String MODULE_NOT_FOUND = "Module does not exist";
    private static final String ASSESSMENT_NOT_FOUND = "Assessment does not exist";
    private static final String LEAD_NOT_FOUND = "Module Lead does not exist";
    private static final String MODERATOR_NOT_FOUND = "Moderator does not exist";
    private static final String USER_DELETED = "User is deleted/deactivated";

    private final ModuleRepository moduleRepository;
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;
    private final ModuleRoleRepository moduleRoleRepository;
    private final RoleService roleService;
    private final AssessmentService assessmentService;

    // constructor for module repo
    public ModuleService(ModuleRepository moduleRepository, AssessmentRepository assessmentRepository, UserRepository userRepository,
                         AssessmentService assessmentService, ModuleRoleRepository moduleRoleRepository, RoleService roleService) {
        this.moduleRepository = moduleRepository;
        this.assessmentRepository = assessmentRepository;
        this.userRepository = userRepository;
        this.moduleRoleRepository = moduleRoleRepository;
        this.assessmentService = assessmentService;
        this.roleService = roleService;
    }

    public boolean doesModuleExist(String moduleCode) {
        return moduleRepository.findByModuleCode(moduleCode).isPresent();
    }

    // creating a module
    public Module createModule(Module module, Integer moduleLeadId, Integer moderatorId) {
        // Save module
        if (moduleRepository.findByModuleCode(module.getModuleCode()).isPresent()) {
            throw new ModuleExistsException("Module with code: " + module.getModuleCode() + " already exists");
        }
        Module savedModule = moduleRepository.save(module);

        // Assign the module lead
        User moduleLead = userRepository.findById(moduleLeadId).orElseThrow(() -> new UserDoesNotExistException(LEAD_NOT_FOUND));
        if (!moduleLead.isActive()) throw new UserDeletedException("User: " + moduleLead.getEmail()  + " has been deleted");

        roleService.assignModuleRole(moduleLeadId, module.getModuleCode(), ModuleRole.Role.LEAD);

        // Assign optional moderator
        if (moderatorId != null) {
            User moderator = userRepository.findById(moderatorId).orElseThrow(() -> new UserDoesNotExistException(MODERATOR_NOT_FOUND));
            if (!moderator.isActive())  throw new UserDeletedException("User: " + moderator.getEmail() + " has been deleted");
            roleService.assignModuleRole(moderatorId, module.getModuleCode(), ModuleRole.Role.MODERATOR);
        }

        return savedModule;
    }

    // get all modules
    public List<Module> getModules() {
        return moduleRepository.findAll();
    }
    // get module by name
    public Module getModuleByName(String moduleName) {
        return moduleRepository.findByModuleName(moduleName)
                .orElseThrow(() -> new ModuleDoesNotExist(MODULE_NOT_FOUND));
    }
    // get module by code
    public Module getModuleByCode(String moduleCode) {
        return moduleRepository.findByModuleCode(moduleCode)
                .orElseThrow(() -> new ModuleDoesNotExist(MODULE_NOT_FOUND));
    }
    // get module by module lead
    public Module getModuleByModuleLead(int moduleLeadId) {
        ModuleRole leadRole = moduleRoleRepository
                .findByUserIdAndRole(moduleLeadId, ModuleRole.Role.LEAD)
                .orElseThrow(() -> new UserDoesNotExistException(LEAD_NOT_FOUND));
        return leadRole.getModule();
    }
    // get module by moderator
    public Module getModuleByModerator(int moduleModeratorId) {
        ModuleRole leadRole = moduleRoleRepository
                .findByUserIdAndRole(moduleModeratorId, ModuleRole.Role.MODERATOR)
                .orElseThrow(() -> new UserDoesNotExistException(MODERATOR_NOT_FOUND));
        return leadRole.getModule();
    }

    /*
        Check with caspar/marek if everytime a update is made the whole module information is sent or
        they need individual endpoints for specific columns
     */
    public Module updateModule(String moduleCode,
                               Module updatedModule,
                               Integer moduleLeadId,
                               Integer moderatorId,
                               Authentication authentication) {

        Module storedModule = getModuleByCode(moduleCode);
        checkModuleOwnership(storedModule, authentication);

        if (updatedModule.getModuleName() != null) {
            storedModule.setModuleName(updatedModule.getModuleName());
        }

        if (updatedModule.getSchool() != null) {
            storedModule.setSchool(updatedModule.getSchool());
        }
        if (updatedModule.getDegreeLevel() != null) {
            storedModule.setDegreeLevel(updatedModule.getDegreeLevel());
        }
        // Update lead if changed
        if (moduleLeadId != null) {
            User moduleLead = userRepository.findById(moduleLeadId).orElseThrow(() -> new UserDoesNotExistException(LEAD_NOT_FOUND));
            if (!moduleLead.isActive()) throw new UserDeletedException("User: " + moduleLead.getEmail() + " has been deleted");
            roleService.assignModuleRole(moduleLeadId, storedModule.getModuleCode(), ModuleRole.Role.LEAD);
        }

        // Update moderator
        if (moderatorId != null) {
            User moderator = userRepository.findById(moderatorId).orElseThrow(() -> new UserDoesNotExistException(MODERATOR_NOT_FOUND));
            if (!moderator.isActive()) throw new UserDeletedException("User: " + moderator.getEmail() + " has been deleted");
            roleService.assignModuleRole(moderatorId, storedModule.getModuleCode(), ModuleRole.Role.MODERATOR);
        }
        return moduleRepository.save(storedModule);
    }

    // delete module
    public void deleteModule(String moduleCode, Authentication authentication) {
        Module module = getModuleByCode(moduleCode);
        checkModuleOwnership(module, authentication);

        moduleRoleRepository.deleteByModule_ModuleCode(module.getModuleCode());
        moduleRepository.delete(module);
    }

    public boolean userCanEditOrDelete(User user, String moduleCode) {
        if (!user.isActive()) return false;
        return moduleRoleRepository.findByUserIdAndModule_ModuleCodeAndRole(
                user.getId(),
                moduleCode,
                ModuleRole.Role.STAFF) != null; // change staff if its someone else who can edit/delete
    }

    // gets count user roles
    public List<ModuleRole> getUserRoles(Integer userId, Integer count) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!user.isActive()) throw new UserDeletedException("User: " + user.getEmail() + " has been deleted");

        List<ModuleRole> roles = moduleRoleRepository.findByUserId(userId);
        if (count > 0 && count <= roles.size()) {
            return roles.subList(0, count);
        }
        return roles;
    }

    private void checkModuleOwnership(Module module, Authentication authentication) {
//        User user = (User) authentication.getPrincipal();
//        if (doesModuleExist(module.getModuleCode())) {
//
//        }
    }

    // remove assessment
    public void removeAssessmentFromModule(String moduleCode, String assessmentTitle) {
        getModuleByName(moduleCode);

        // finds by module code and the assessment title
        Assessment assessment = assessmentRepository
                .findById_ModuleCodeAndId_Title(moduleCode, assessmentTitle)
                .orElseThrow(() -> new AssessmentDoesNotExistException(ASSESSMENT_NOT_FOUND));

        assessmentRepository.delete(assessment);
    }

    /*
        importing module from CSV
     */

    public Module importModuleFromCSV(String [] csvRow) {
        String moduleCode = csvRow[0];
        String moduleName = csvRow[1];
        String leadName =  csvRow[2];
        String staffNames = csvRow[3].replace("\"", "");

        // Find the lead must exist or we abort
        User leadUser = findUserByName(leadName);
        if (leadUser == null || !leadUser.isActive()) {
            throw new UserDoesNotExistException(LEAD_NOT_FOUND);
        }

        // Create the module
        Module module = new Module();
        module.setModuleCode(moduleCode);
        module.setModuleName(moduleName);
        module.setSchool("Computer Science"); // do we need this?
        module.setDegreeLevel(Module.DegreeLevel.UNDERGRADUATE); // do we need this?

        // Save the module
        Module savedModule = createModule(module, leadUser.getId(), null);

        // Try to add staff members
        if (staffNames != null && !staffNames.isBlank()) {
            String[] staffArray = staffNames.split(",");
            for (String staffName : staffArray) {
                staffName = staffName.trim();
                if (!staffName.isEmpty()) {
                    User staffUser = findUserByName(staffName);
                    if (staffUser != null && staffUser.isActive()) {
                        try {
                            roleService.assignModuleRole(staffUser.getId(), moduleCode, ModuleRole.Role.STAFF);
                        } catch (Exception e) {
                            // Ignore - staff member couldn't be added
                        }
                    }
                }
            }
        }

        List<Assessment> assessments = new ArrayList<>();

        // In the csv it goes type,title,type,title...
        for (int i = 4; i < csvRow.length; i += 2) {
            if (i + 1 >= csvRow.length) break; // not correct

            String typeStr = csvRow[i].replace("\"", "").trim().toUpperCase();
            String title = csvRow[i + 1].replace("\"", "").trim();

            Assessment.Type type = mapType(typeStr);
            if (type == null) {
                continue;
            }

            Assessment assessment = assessmentService.createAssessmentFromCSV(type, title, savedModule);
            assessmentRepository.save(assessment);
        }
        return savedModule;
    }

    public User findUserByName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }

        fullName = fullName.trim();
        String[] parts = fullName.split(" ", 2);

        if (parts.length == 2) {
            String forename =  parts[0];
            String surname =  parts[1];

            Optional<User> user = userRepository.findByForenameIgnoreCaseAndSurnameIgnoreCase(forename, surname);
            return user.orElse(null);
        }
        return null;
    }

    private Assessment.Type mapType(String t) {
        return switch (t.toUpperCase()) {
            case "CW" -> Assessment.Type.COURSE_WORK;
            case "IN", "IN-SEMESTER" -> Assessment.Type.IN_SEMESTER;
            case "EXAM" -> Assessment.Type.FORMAL_EXAM;
            default -> null;
        };
    }


}

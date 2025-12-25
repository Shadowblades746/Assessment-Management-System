package uk.ac.sheffield.com2008_team_27.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.config.Authorities;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.exceptions.*;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRoleRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class RoleService {

    private static final String USER_NOT_FOUND = "User does not exist";

    private final UserRepository userRepository;

    private final ModuleRoleRepository moduleRoleRepository;

    private final ModuleRepository moduleRepository;

    public RoleService(UserRepository userRepository, ModuleRoleRepository moduleRoleRepository, ModuleRepository moduleRepository) {
        this.userRepository = userRepository;
        this.moduleRoleRepository = moduleRoleRepository;
        this.moduleRepository = moduleRepository;
    }

    /*
        Improve the error messages and exceptions! They're just temporary for now
     */
    public User promoteToExamsOfficer(int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!user.hasRole(Authorities.ROLE_ACADEMIC_STAFF))
            throw new NotAcademicStaffException("User: " + user.getEmail()  + " has to be an academic staff member");

        if (!user.isActive())
            throw new UserDeletedException("User: " + user.getEmail() + " has been deleted");

        user.setRole(Authorities.ROLE_EXAMS_OFFICER);
        userRepository.save(user);
        return user;
    }

    public User demoteToExamsOfficer(int userId, int actingUserId) {
        if (userId == actingUserId) {
            throw new NotExamsOfficerException("Exams officer: " + userId + " can't demote themselves");
        }

        // Get rid of orElseThrow and throwing with better exceptions
        User user = userRepository.findById(userId).orElseThrow();
        if (!user.isActive())
            throw new UserDeletedException("User: " + user.getEmail() + " has been deleted");

        if (!user.hasRole(Authorities.ROLE_EXAMS_OFFICER))
            throw new NotExamsOfficerException("User: " + user.getEmail() + " has to be an exams officer");

        user.setRole(Authorities.ROLE_ACADEMIC_STAFF);
        userRepository.save(user);
        return user;
    }

    public User updateUserRole(int id, String role) {
        if (!userRepository.existsById(id)) {
            throw new UserDoesNotExistException(USER_NOT_FOUND);
        }

        User storedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        if (!storedUser.isActive())
            throw new UserDeletedException("User: " + storedUser.getEmail() + " has been deleted");

        role = role.toUpperCase();

        if (storedUser.getRole().equals(Authorities.ROLE_EXAMS_OFFICER)) {
            demoteToExamsOfficer(id, storedUser.getId());
        }

        List<String> allowed = List.of(
                Authorities.ROLE_ACADEMIC_STAFF,
                Authorities.ROLE_TEACHING_SUPPORT,
                Authorities.ROLE_EXTERNAL_EXAMINER
        );

        if (!allowed.contains(role)) {
            throw new RoleDoesNotExistException(role + " doesn't exist");
        }
        storedUser.setRole(role);
        return userRepository.save(storedUser);
    }

    // -------------------------------- MODULE ROLES ----------------------------------------------------------

    public ModuleRole assignModuleRole(int userId, String moduleCode, ModuleRole.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User with id: " + userId + " doesn't exist"));

        if (!user.isActive())
            throw new UserDeletedException("User: " + user.getEmail() + " has been deleted");

        // Checks for if a user is being assigned module lead/moderator when they are already the lead/moderator
        // LEAD
        if (role == ModuleRole.Role.LEAD) {
            ModuleRole currentModuleModerator = moduleRoleRepository.findFirstByModule_ModuleCodeAndRole(moduleCode, ModuleRole.Role.MODERATOR)
                    .orElse(null);
            // Module lead cannot be moderator in the same module
            if (currentModuleModerator != null && userId == currentModuleModerator.getUser().getId()) {
                throw new ModuleLeadForModuleException("Lead: " + currentModuleModerator.getUser().getEmail()+ " " +
                        " cannot be a moderator in the same module");
            }
        }
        // MODERATOR
        else if (role == ModuleRole.Role.MODERATOR) {
            ModuleRole currentModuleLead = moduleRoleRepository.findFirstByModule_ModuleCodeAndRole(moduleCode, ModuleRole.Role.LEAD)
                    .orElse(null);
            // Module moderator cannot be lead in the same module
            if (currentModuleLead != null && userId == currentModuleLead.getUser().getId()) {
                throw new ModuleLeadForModuleException("Moderator: " + currentModuleLead.getUser().getEmail()+ " " +
                        " cannot be a lead in the same module");
            }

            // Make sure moderator is not in the module staff / involved in module delivery
            ModuleRole currentModuleRole = moduleRoleRepository.findByUserIdAndModule_ModuleCodeAndRole(userId, moduleCode, ModuleRole.Role.STAFF);

            if (currentModuleRole != null && currentModuleRole.getRole() == ModuleRole.Role.STAFF) {
                throw new ForbiddenToModerateException("User: " + currentModuleRole.getUser().getEmail() +
                        " is involved in delivering the module (STAFF) so cannot moderate");
            }
        }

        if (!user.hasRole(Authorities.ROLE_ACADEMIC_STAFF) && !user.hasRole(Authorities.ROLE_EXAMS_OFFICER)) {
            throw new NotAcademicStaffException("User: " + user.getEmail() + " has to be an academic staff member");
        }
        
        Module module = moduleRepository.findByModuleCode(moduleCode)
                .orElseThrow(() -> new ModuleDoesNotExist("Module: " + moduleCode + " does not exist"));

        // Prevent downgrading a user from lead to staff (only lead can be updated, needs to present at all times)
        if (role == ModuleRole.Role.STAFF) {
            moduleRoleRepository.findByModule_ModuleCodeAndUser_Id(moduleCode, userId)
                    .ifPresent(existingRole -> {
                            if (existingRole.getRole() == ModuleRole.Role.LEAD) {
                throw new ForbidModuleLeadDowngradeException("User: " + userId + " has to be a lead role");
            }});
        }
        // Ensure that only one LEAD and Moderator exist in one module
        if (role == ModuleRole.Role.LEAD || role == ModuleRole.Role.MODERATOR) {
            moduleRoleRepository.findFirstByModule_ModuleCodeAndRole(moduleCode, role).ifPresent(moduleRoleRepository::delete);
        }

        ModuleRole moduleRole = new  ModuleRole(user, module, role);
        return moduleRoleRepository.save(moduleRole);
    }

    public void removeModuleRole(int userId, String moduleCode, ModuleRole.Role role) {
        ModuleRole moduleRole = moduleRoleRepository.findByUserIdAndModule_ModuleCodeAndRole(userId, moduleCode, role);

        if (moduleRole != null) {
            // For lead, prevent removal
            if (role == ModuleRole.Role.LEAD) {
                throw new ForbidModuleLeadDowngradeException("Cannot remove module lead: " + moduleRole.getUser().getEmail() + " . Assign a new role");
            }
            moduleRoleRepository.delete(moduleRole);
        }
    }

    public ModuleRole getRoleInModule(String moduleCode, ModuleRole.Role role) {
        return moduleRoleRepository
                .findFirstByModule_ModuleCodeAndRole(moduleCode, role)
                .orElse(null);
    }

    public List<ModuleRole> getRolesInModule(String moduleCode, ModuleRole.Role role) {
        if (role != ModuleRole.Role.STAFF) {
            throw new ModuleLeadForModuleException("There are can only be multiple roles for staff, not: " + role);
        }

        return moduleRoleRepository.findByModule_ModuleCodeAndRole(moduleCode, role);
    }

    // Security (Role checking)
    public boolean hasRoleInModule(String email, String moduleCode, ModuleRole.Role role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        return moduleRoleRepository
                .findByUserIdAndModule_ModuleCodeAndRole(user.getId(), moduleCode, role) != null;
    }

    public boolean hasRoleInModule(int userId, String moduleCode, ModuleRole.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        return moduleRoleRepository
                .findByUserIdAndModule_ModuleCodeAndRole(user.getId(), moduleCode, role) != null;
    }
}

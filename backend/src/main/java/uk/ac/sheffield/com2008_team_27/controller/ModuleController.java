package uk.ac.sheffield.com2008_team_27.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.dto.*;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.service.ModuleService;
import uk.ac.sheffield.com2008_team_27.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/modules")
public class ModuleController {

    private final ModuleService moduleService;
    private final ModuleRepository moduleRepository;
    private final RoleService roleService;

    public ModuleController(ModuleService moduleService, ModuleRepository moduleRepository, RoleService roleService) {
        this.moduleService = moduleService;
        this.moduleRepository = moduleRepository;
        this.roleService = roleService;
    }

//    @GetMapping({"", "/"})
//    public ResponseEntity<List<ModuleDTO>> getModules() {
//        return ResponseEntity.status(HttpStatus.OK).body(moduleService.getModules()
//                .stream()
//                .map(Module::toDto)
//                .toList());
//    }

    /*
        This is a basic implementation that needs refactoring/moving
        Using for testing of the moduleRole class for now
     */
    @PostMapping("/staff")
    public ResponseEntity<ModuleRole> addUserToModule(@RequestParam Integer userId,
                                                      @RequestParam String moduleCode,
                                                      @RequestParam String role, Authentication authentication) {

        return ResponseEntity.status(HttpStatus.OK).body(roleService.assignModuleRole(userId, moduleCode, ModuleRole.Role.valueOf(role)));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<ModuleDTO> createModule(
                                                  @RequestBody ModuleCreateDTO moduleDTO,
                                                  Authentication authentication) {
        Module module = new Module(moduleDTO.getModuleCode(), moduleDTO.getModuleName(),
                moduleDTO.getSchool(), moduleDTO.getDegreeLevel());

        Module createdModule = moduleService.createModule(module, moduleDTO.getModuleLeadId(), moduleDTO.getModeratorId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdModule.toDto());
    }

    @PostMapping("/import/csv")
    public ResponseEntity<?> importModuleFromCSV(@RequestBody String csvContent) {
        List<ModuleDTO> importedModules = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        String[] lines = csvContent.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) continue;

            try {
                // The regex split by commas, but ignores commas nested inside
                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Module module = moduleService.importModuleFromCSV(row);
                importedModules.add(module.toDto());
            } catch (ResponseStatusException e) {
                errors.add(e.getMessage());
            } catch (Exception e) {
                errors.add(e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(importedModules);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<?> getModulesOrUserRoles(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false, defaultValue = "0") Integer count) {
        // if user isnt defined, returns all modules
        if (userId == null) {
            // returns all modules
            List<ModuleDTO> modules = moduleService.getModules()
                .stream()
                .map(Module::toDto)
                .toList();
            return ResponseEntity.status(HttpStatus.OK).body(modules);
        }
        // if user is defined returns users roles
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.getUserRoles(userId, count));
    }

    // get module by code
    @GetMapping("/{moduleCode}")
    public ResponseEntity<?> getModule(@PathVariable String moduleCode,
                                       @RequestParam(required = false) User user) {

        // module info
        Module module = moduleService.getModuleByCode(moduleCode);
        ModuleDTO moduleDTO = module.toDto();

        // if user defined return module info and if the user can edit/delete
        if (user != null) {
            boolean canEdit = moduleService.userCanEditOrDelete(user, moduleCode);
            boolean canDelete = canEdit; // same as canEdit

            moduleDTO.setCanEdit(canEdit);
            moduleDTO.setCanDelete(canDelete);
        }

        return ResponseEntity.status(HttpStatus.OK).body(moduleDTO);
    }

    @GetMapping("/{moduleCode}/lead")
    public ResponseEntity<?> getModuleLead(@PathVariable String moduleCode) {
        Module module = moduleService.getModuleByCode(moduleCode);
        ModuleRole leadRole = roleService.getRoleInModule(moduleCode, ModuleRole.Role.LEAD);

        if (leadRole == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module lead not assigned");
        }
        return ResponseEntity.ok(leadRole.getUser().toDto());
    }

    @GetMapping("/{moduleCode}/moderator")
    public ResponseEntity<?> getModuleModerator(@PathVariable String moduleCode) {
        Module module = moduleService.getModuleByCode(moduleCode);
        ModuleRole moderatorRole = roleService.getRoleInModule(moduleCode, ModuleRole.Role.MODERATOR);

        if (moderatorRole == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module moderator not assigned");
        }
        return ResponseEntity.ok(moderatorRole.getUser().toDto());
    }

    @GetMapping("/{moduleCode}/staff")
    public ResponseEntity<?> getModuleStaff(@PathVariable String moduleCode) {
        Module module = moduleService.getModuleByCode(moduleCode);
        List<ModuleRole> staffRoles = roleService.getRolesInModule(moduleCode, ModuleRole.Role.STAFF);

        List<User> staffUsers = staffRoles.stream().map(ModuleRole::getUser).toList();
        return ResponseEntity.status(HttpStatus.OK).body(staffUsers);
    }


    @PutMapping("/{moduleCode}/lead")
    public ResponseEntity<?> updateModuleLead(@PathVariable String moduleCode,
                                              @RequestParam Integer newLeadId,
                                              Authentication authentication) {
        ModuleRole updatedLead = roleService.assignModuleRole(newLeadId, moduleCode, ModuleRole.Role.LEAD);
        return ResponseEntity.ok(updatedLead.getUser().toDto());
    }

    @PutMapping("/{moduleCode}/moderator")
    public ResponseEntity<?> updateModuleModerator(@PathVariable String moduleCode,
                                              @RequestParam Integer newModeratorId,
                                              Authentication authentication) {
        ModuleRole updatedModerator = roleService.assignModuleRole(newModeratorId, moduleCode, ModuleRole.Role.LEAD);
        return ResponseEntity.ok(updatedModerator.getUser().toDto());
    }

    @PutMapping("/{moduleCode}/staff")
    public ResponseEntity<?> updateModuleStaff(@PathVariable String moduleCode,
                                                   @RequestParam Integer newStaffId,
                                                   Authentication authentication) {
        ModuleRole updatedModerator = roleService.assignModuleRole(newStaffId, moduleCode, ModuleRole.Role.STAFF);
        return ResponseEntity.ok(updatedModerator.getUser().toDto());
    }

    @DeleteMapping("/{moduleCode}/staff")
    public ResponseEntity<?> removeModuleStaff(@PathVariable String moduleCode,
                                               @RequestParam Integer staffId,
                                               Authentication authentication) {
        roleService.removeModuleRole(staffId, moduleCode, ModuleRole.Role.STAFF);
//        ModuleRole updatedModerator = roleService.assignModuleRole(newStaffId, moduleCode, ModuleRole.Role.STAFF);
        return ResponseEntity.ok().body("User with id: " + staffId + " has been removed from staff");
    }



    // PutMapping to update module
    @PutMapping("/{moduleCode}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable String moduleCode,
                                                  @RequestBody ModuleUpdateDTO moduleDTO,
                                                  Authentication authentication) {
        Module updatedModule = new Module();
        updatedModule.setModuleName(moduleDTO.getModuleName());
        updatedModule.setSchool(moduleDTO.getSchool());
        updatedModule.setDegreeLevel(moduleDTO.getDegreeLevel());

        Module module = moduleService.updateModule(moduleCode, updatedModule, moduleDTO.getModuleLeadId(),
                moduleDTO.getModeratorId(), authentication);

        return ResponseEntity.ok(module.toDto());
    }

    // delete a module which only admin (?) can do
    @PreAuthorize("hasAnyAuthority(T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_TEACHING_SUPPORT)")
    @DeleteMapping("/{moduleCode}")
    public ResponseEntity<Void> deleteModule(@PathVariable String moduleCode,
                                             Authentication authentication) {
        moduleService.deleteModule(moduleCode, authentication);
        return ResponseEntity.noContent().build();
    }


}

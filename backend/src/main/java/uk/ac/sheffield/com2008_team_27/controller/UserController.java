package uk.ac.sheffield.com2008_team_27.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.ac.sheffield.com2008_team_27.config.Authorities;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.domain.ModuleRole;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.domain.Module;
import uk.ac.sheffield.com2008_team_27.dto.UserDTO;
import uk.ac.sheffield.com2008_team_27.service.RoleService;
import uk.ac.sheffield.com2008_team_27.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PatchMapping("/{id}/promote-exams-officer")
    @PreAuthorize("hasAnyAuthority(T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_EXAMS_OFFICER)")
    public ResponseEntity<String> promoteExamsOfficer(@PathVariable("id") Integer userId, Authentication authentication) {
        User updatedUser = roleService.promoteToExamsOfficer(userId);
        return ResponseEntity.ok("User " + updatedUser.getEmail() + " promoted to exams officer");
    }

    @PatchMapping("/{id}/demote-exams-officer")
    @PreAuthorize("hasAnyAuthority(T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_EXAMS_OFFICER)")
    public ResponseEntity<String> demoteExamsOfficer(@PathVariable("id") Integer userId, Authentication authentication) {
        User actingUser = userService.getUserByEmail(authentication.getName());
        User updatedUser = roleService.demoteToExamsOfficer(userId, actingUser.getId());
        return ResponseEntity.ok("User " + updatedUser.getEmail()  + " demoted to exams officer");
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority(T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_TEACHING_SUPPORT," +
                    "T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_EXAMS_OFFICER)")
    public ResponseEntity<String> updateUserRole(@PathVariable Integer id, @RequestParam String role, Authentication authentication)
    {
        if (role.equals(Authorities.ROLE_EXAMS_OFFICER.toString())) {
            User updatedUser = roleService.promoteToExamsOfficer(id);
            return ResponseEntity.ok("User " + updatedUser.getEmail() + " promoted to exams officer");
        } else {
            User updatedUser = roleService.updateUserRole(id, role);
            return ResponseEntity.ok("User role updated to: " + updatedUser.getRole());
        }
    }

    // Add a create account

    @GetMapping({"", "/"})
        public ResponseEntity<List<UserDTO>> getUsers(@RequestParam(required = false, defaultValue = "0") Integer count) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(count)
                .stream()
                .map(User::toDto)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id).toDto());
    }

    @GetMapping("/id/{email}")
    public ResponseEntity<Integer> getUserIdFromEmail(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserByEmail(email).getId());
    }

    @PreAuthorize("hasAnyAuthority(T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_TEACHING_SUPPORT," +
            "T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_EXAMS_OFFICER)")
    @GetMapping("/roles/{id}")
    public ResponseEntity<String> getRoles(@PathVariable int id) {
        User user = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(user.getRole());
    }

    @GetMapping("/roles/verifyAdmin/")
    public ResponseEntity<String> verifyAdmin(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = jwt.getClaimAsStringList("scope");

        if (roles != null && (roles.contains(Authorities.ROLE_TEACHING_SUPPORT))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/roles/verifyExamsOfficer/")
    public ResponseEntity<String> verifyExamsOfficer(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = jwt.getClaimAsStringList("scope");

        if (roles != null && (roles.contains(Authorities.ROLE_EXAMS_OFFICER))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("hasAnyAuthority(T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_TEACHING_SUPPORT," +
            "T(uk.ac.sheffield.com2008_team_27.config.Authorities).SCOPED_EXAMS_OFFICER)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }



    /*
        Add a get email from user id
        ADd a get user id from email
     */
}

package uk.ac.sheffield.com2008_team_27.service;

import uk.ac.sheffield.com2008_team_27.config.Authorities;
import org.springframework.security.core.userdetails.UserDetails;
import uk.ac.sheffield.com2008_team_27.domain.Assessment.Assessment;
import uk.ac.sheffield.com2008_team_27.domain.SecurityUser;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.dto.TokenDTO;
import uk.ac.sheffield.com2008_team_27.dto.UserSignupDTO;
import uk.ac.sheffield.com2008_team_27.exceptions.EmailExistsException;
import uk.ac.sheffield.com2008_team_27.exceptions.RoleDoesNotExistException;
import uk.ac.sheffield.com2008_team_27.exceptions.UserDeletedException;
import uk.ac.sheffield.com2008_team_27.repository.AssessmentRepository;
import uk.ac.sheffield.com2008_team_27.repository.ModuleRepository;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UserService {

    private static final String USER_NOT_FOUND = "User does not exist";
    private static final String USER_DELETED = "User is deleted";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailService detailsService;
    private final TokenService tokenService;
    private final ModuleRepository moduleRepository;
    private final AssessmentRepository assessmentRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JpaUserDetailService jpaUserDetailsService, TokenService tokenService, ModuleRepository moduleRepository, AssessmentRepository assessmentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.detailsService = jpaUserDetailsService;
        this.tokenService = tokenService;
        this.moduleRepository = moduleRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public static final String ROLE_USER = "ROLE_USER";

    public TokenDTO signupNewUser(UserSignupDTO userSignupDTO) {
        if (userRepository.existsByEmail(userSignupDTO.getEmail())) {
            throw new EmailExistsException("Username already exists, be original!");
        }
        User newUser = new User(
                userSignupDTO.getEmail(),
                userSignupDTO.getForename(),
                userSignupDTO.getSurname(),
                passwordEncoder.encode(userSignupDTO.getHash()),
                ROLE_USER
        );
        User savedUser = userRepository.save(newUser);
        SecurityUser securityUser = (SecurityUser) detailsService.loadUserByUsername(newUser.getEmail());
        return new TokenDTO(tokenService.generateToken(securityUser.getAuthorities(), newUser.getEmail()), savedUser.toDto(), savedUser.getRole());
    }

    public TokenDTO loginUser(Authentication authentication) {
        String email = authentication.getName();
        User authedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!authedUser.isActive()) {
            throw new UserDeletedException("User: " + email + " is deleted!");
        }
        return new TokenDTO(tokenService.generateToken(authentication.getAuthorities(), email), authedUser.toDto(), authedUser.getRole());
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsers(int count) {
        List<User> users = userRepository.findAll();
        if (count == 0)
            return users;

        if (count >  users.size()) {
            int maxIndex = users.size();
            return users.subList(0, maxIndex);
        }

        return users.subList(0, count);
    }

    public User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    }

    public User getUserByForename(String forename) {
        return userRepository.findByForename(forename)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    }

    public User getUserBySurname(String surname) {
        return userRepository.findBySurname(surname)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        user.setActive(false);
        userRepository.save(user);
//        userRepository.deleteById(id);
    }

    public User updateUser(User updatedUser, int id) {
        User storedUser = getUser(id);
        storedUser.setEmail(updatedUser.getEmail());
        return userRepository.save(storedUser);
    }

    public List<Assessment> getAssessmentChecker(int id) {
        return assessmentRepository.findAllByCheckerID(id);
    }

    public List<Assessment> getAssessmentSetter(int id) {
        return assessmentRepository.findAllBySetterID(id);
    }

    public User getActiveUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        if (!user.isActive()) {
            throw new UserDeletedException("User has been deleted");
        }
        return user;
    }

    public User getActiveUserByAuth(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        if (!user.isActive()) {
            throw new UserDeletedException("User has been deleted");
        }
        return user;
    }
}
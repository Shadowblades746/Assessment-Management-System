package uk.ac.sheffield.com2008_team_27.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.sheffield.com2008_team_27.domain.SecurityUser;
import uk.ac.sheffield.com2008_team_27.domain.User;
import uk.ac.sheffield.com2008_team_27.dto.TokenDTO;
import uk.ac.sheffield.com2008_team_27.dto.UserSignupDTO;
import uk.ac.sheffield.com2008_team_27.exceptions.EmailExistsException;
import uk.ac.sheffield.com2008_team_27.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JpaUserDetailService detailsService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService classUnderTest;

    private User exampleUser;

    @BeforeEach
    public void setup() {

        exampleUser = new User(
                "bruno@sheffield.ac.uk",
                "Bruno",
                "Emad",
                "Password",
                UserService.ROLE_USER
        );
        exampleUser.setId(1);
    }

    @Test
    public void signupNewUser_Succeeds() {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setEmail(exampleUser.getEmail());
        dto.setHash("plainPassword");
        dto.setForename(exampleUser.getForename());
        dto.setSurname(exampleUser.getSurname());

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getHash())).thenReturn(exampleUser.getHash());
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(exampleUser);

        when(detailsService.loadUserByUsername(exampleUser.getEmail())).thenReturn(new SecurityUser(exampleUser));
        when(tokenService.generateToken(ArgumentMatchers.any(), ArgumentMatchers.eq(exampleUser.getEmail()))).thenReturn("generated-token");

        TokenDTO result = classUnderTest.signupNewUser(dto);

        assertThat(result.getToken()).isEqualTo("generated-token");
        assertThat(result.getUser().getEmail()).isEqualTo(exampleUser.getEmail());
        assertThat(result.getRoles()).isEqualTo(exampleUser.getRole());
    }

    @Test
    public void signupNewUser_ThrowsIfEmailExists() {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setEmail(exampleUser.getEmail());
        dto.setHash("plainPassword");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> classUnderTest.signupNewUser(dto))
                .isInstanceOf(EmailExistsException.class);

        verify(userRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    public void loginUser_Succeeds() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(exampleUser.getEmail());

        when(userRepository.findByEmail(exampleUser.getEmail())).thenReturn(Optional.of(exampleUser));
        when(tokenService.generateToken(auth.getAuthorities(), exampleUser.getEmail())).thenReturn("login-token");

        TokenDTO tokenDTO = classUnderTest.loginUser(auth);

        assertThat(tokenDTO.getToken()).isEqualTo("login-token");
        assertThat(tokenDTO.getUser().getEmail()).isEqualTo(exampleUser.getEmail());
    }

    @Test
    public void loginUser_ThrowsIfNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("notexists@sheffield.ac.uk");

        when(userRepository.findByEmail("notexists@sheffield.ac.uk")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classUnderTest.loginUser(auth))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    public void getUsers_ReturnsAll() {
        User u2 = new User("a@sheffield.ac.uk", "A", "B", "h", "ROLE_USER");
        u2.setId(2);
        List<User> all = List.of(exampleUser, u2);
        when(userRepository.findAll()).thenReturn(all);

        List<User> result = classUnderTest.getUsers();

        assertThat(result).isEqualTo(all);
    }

    @Test
    public void getUsers_WithCountZeroReturnsAll() {
        List<User> all = List.of(exampleUser);
        when(userRepository.findAll()).thenReturn(all);

        List<User> result = classUnderTest.getUsers(0);

        assertThat(result).isEqualTo(all);
    }

    @Test
    public void getUsers_WithCountLessThanSizeReturnsSublist() {
        User u2 = new User("blob@sheffield.ac.uk", "blob", "man", "drowssap", "ROLE_USER");
        u2.setId(2);
        List<User> all = List.of(exampleUser, u2);
        when(userRepository.findAll()).thenReturn(all);

        List<User> result = classUnderTest.getUsers(1);

        assertThat(result).isEqualTo(all.subList(0, 1));
    }

    @Test
    public void getUsers_WithCountGreaterThanSizeReturnsAll() {
        List<User> all = List.of(exampleUser);
        when(userRepository.findAll()).thenReturn(all);

        List<User> result = classUnderTest.getUsers(5);

        assertThat(result).isEqualTo(all);
    }

    @Test
    public void getUserById_Succeeds() {
        when(userRepository.findById(1)).thenReturn(Optional.of(exampleUser));

        User found = classUnderTest.getUser(1);

        assertThat(found).isEqualTo(exampleUser);
    }

    @Test
    public void getUserById_NotFoundThrows() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classUnderTest.getUser(2))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserByEmail_Succeeds() {
        when(userRepository.findByEmail(exampleUser.getEmail())).thenReturn(Optional.of(exampleUser));

        User found = classUnderTest.getUserByEmail(exampleUser.getEmail());

        assertThat(found).isEqualTo(exampleUser);
    }

    @Test
    public void getUserByEmail_NotFoundThrows() {
        when(userRepository.findByEmail("error@sheffield.ac.uk")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classUnderTest.getUserByEmail("error@sheffield.ac.uk"))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserByForename_Succeeds() {
        when(userRepository.findByForename(exampleUser.getForename())).thenReturn(Optional.of(exampleUser));

        User found = classUnderTest.getUserByForename(exampleUser.getForename());

        assertThat(found).isEqualTo(exampleUser);
    }

    @Test
    public void getUserByForename_NotFoundThrows() {
        when(userRepository.findByForename("NoName")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classUnderTest.getUserByForename("NoName"))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserBySurname_Succeeds() {
        when(userRepository.findBySurname(exampleUser.getSurname())).thenReturn(Optional.of(exampleUser));

        User found = classUnderTest.getUserBySurname(exampleUser.getSurname());

        assertThat(found).isEqualTo(exampleUser);
    }

    @Test
    public void getUserBySurname_NotFoundThrows() {
        when(userRepository.findBySurname("NoName")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classUnderTest.getUserBySurname("NoName"))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteUserSucceedsIfExists() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(exampleUser));

        classUnderTest.deleteUser(1);

        assertThat(exampleUser.isActive()).isFalse();
        verify(userRepository).save(exampleUser);
    }

    @Test
    public void deleteUser_ThrowsIfNotExists() {
        when(userRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> classUnderTest.deleteUser(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

        verify(userRepository, never()).deleteById(99);
    }

    @Test
    public void updateUser_Succeeds() {
        User stored = new User("Nikiot@sheffield.ac.uk", "♀☻", "Name", "qwertyuiop", UserService.ROLE_USER);
        stored.setId(5);
        User updated = new User("Ellita@sheffield.ac.uk", "♀☻", "Name", "qwertyuiop", UserService.ROLE_USER);
        updated.setId(5);

        when(userRepository.findById(5)).thenReturn(Optional.of(stored));
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(updated);

        User result = classUnderTest.updateUser(updated, 5);

        assertThat(result.getEmail()).isEqualTo("Ellita@sheffield.ac.uk");
    }
}

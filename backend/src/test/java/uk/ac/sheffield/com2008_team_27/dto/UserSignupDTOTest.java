package uk.ac.sheffield.com2008_team_27.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSignupDTOTest {

    private static final String TEST_EMAIL = "student@sheffield.ac.uk";
    private static final String TEST_PASSWORD = "Valid1!";
    private static final String BAD_EMAIL = "bad@example.com";
    private static final String BAD_PASSWORD = "weak";

    @Mock
    private Validator validator;

    @Mock
    private ConstraintViolation<UserSignupDTO> violation;

    private UserSignupDTO validDto() {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setEmail(TEST_EMAIL);
        dto.setHash(TEST_PASSWORD);
        dto.setForename("Alice");
        dto.setSurname("Smith");
        return dto;
    }

    @Test
    void whenValidatorReturnsEmptySet_thenNoViolations() {
        UserSignupDTO dto = validDto();

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        Set<ConstraintViolation<UserSignupDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        verify(validator, times(1)).validate(dto);
        verifyNoMoreInteractions(validator);
    }

    @Test
    void whenValidatorReturnsViolationSet_thenViolationsPresent() {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setEmail(BAD_EMAIL);
        dto.setHash(BAD_PASSWORD);

        when(validator.validate(dto)).thenReturn(Collections.singleton((ConstraintViolation<UserSignupDTO>) violation));

        Set<ConstraintViolation<UserSignupDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        verify(validator, times(1)).validate(dto);
        verifyNoMoreInteractions(validator);
    }
}

package uk.ac.sheffield.com2008_team_27.dto;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import uk.ac.sheffield.com2008_team_27.domain.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserDTOTest {

    private static final String TEST_FORENAME = "Alice";
    private static final String TEST_SURNAME = "Smith";
    private static final String TEST_EMAIL = "alice@example.com";
    private static final Integer TEST_ID = 7;

    private UserDTO createValidDTO() {
        UserDTO dto = new UserDTO();
        dto.setForename(TEST_FORENAME);
        dto.setSurname(TEST_SURNAME);
        dto.setEmail(TEST_EMAIL);
        dto.setId(TEST_ID);
        return dto;
    }

    @Test
    void toEntity_setsAllFields_andReturnsConstructedMock() {
        UserDTO dto = createValidDTO();

        try (MockedConstruction<User> mocked = Mockito.mockConstruction(User.class)) {
            User result = dto.toEntity();

            assertNotNull(result);
            assertEquals(1, mocked.constructed().size());
            User mockUser = mocked.constructed().get(0);
            assertSame(mockUser, result);

            verify(mockUser).setForename(TEST_FORENAME);
            verify(mockUser).setSurname(TEST_SURNAME);
            verify(mockUser).setEmail(TEST_EMAIL);
            verify(mockUser).setId(TEST_ID);
            verifyNoMoreInteractions(mockUser);
        }
    }

    @Test
    void toEntity_withNullId_doesNotCallSetId() {
        UserDTO dto = createValidDTO();
        dto.setId(null);

        try (MockedConstruction<User> mocked = Mockito.mockConstruction(User.class)) {
            User result = dto.toEntity();

            assertNotNull(result);
            User mockUser = mocked.constructed().get(0);
            assertSame(mockUser, result);

            verify(mockUser).setForename(TEST_FORENAME);
            verify(mockUser).setSurname(TEST_SURNAME);
            verify(mockUser).setEmail(TEST_EMAIL);
            verify(mockUser, never()).setId(any());
            verifyNoMoreInteractions(mockUser);
        }
    }

    @Test
    void toEntity_withNullNamesAndEmail_callsSettersWithNull() {
        UserDTO dto = createValidDTO();
        dto.setForename(null);
        dto.setSurname(null);
        dto.setEmail(null);

        try (MockedConstruction<User> mocked = Mockito.mockConstruction(User.class)) {
            User result = dto.toEntity();

            assertNotNull(result);
            User mockUser = mocked.constructed().get(0);
            assertSame(mockUser, result);

            verify(mockUser).setForename((String) null);
            verify(mockUser).setSurname((String) null);
            verify(mockUser).setEmail((String) null);
            verify(mockUser).setId(TEST_ID);
            verifyNoMoreInteractions(mockUser);
        }
    }
}

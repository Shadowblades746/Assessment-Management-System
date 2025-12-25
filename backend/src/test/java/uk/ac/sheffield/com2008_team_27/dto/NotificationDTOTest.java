package uk.ac.sheffield.com2008_team_27.dto;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import uk.ac.sheffield.com2008_team_27.domain.Notification;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDTOTest {

    private NotificationDTO createValidDTO() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(10);
        dto.setContent("Welcome");
        dto.setRead(true);
        return dto;
    }

    @Test
    void toEntity_success_returnsConstructedNotification_mocked() {
        NotificationDTO dto = createValidDTO();

        try (MockedConstruction<Notification> mocked = Mockito.mockConstruction(Notification.class)) {
            Notification result = dto.toEntity();

            assertNotNull(result);
            assertInstanceOf(Notification.class, result);
            // ensure the returned instance is the mocked one
            assertSame(mocked.constructed().get(0), result);
        }
    }

    @Test
    void toEntity_allowsNullUserId_noException() {
        NotificationDTO dto = createValidDTO();
        dto.setUserId(null);

        try (MockedConstruction<Notification> ignored = Mockito.mockConstruction(Notification.class)) {
            assertDoesNotThrow(dto::toEntity);
        }
    }

    @Test
    void toEntity_allowsNullContent_noException() {
        NotificationDTO dto = createValidDTO();
        dto.setContent(null);

        try (MockedConstruction<Notification> ignored = Mockito.mockConstruction(Notification.class)) {
            assertDoesNotThrow(dto::toEntity);
        }
    }

    @Test
    void toEntity_preservesReadFlag_falseAndTrue_noException() {
        NotificationDTO dto = createValidDTO();
        dto.setRead(false);

        try (MockedConstruction<Notification> ignored = Mockito.mockConstruction(Notification.class)) {
            assertDoesNotThrow(dto::toEntity);
        }

        dto.setRead(true);
        try (MockedConstruction<Notification> ignored = Mockito.mockConstruction(Notification.class)) {
            assertDoesNotThrow(dto::toEntity);
        }
    }
}

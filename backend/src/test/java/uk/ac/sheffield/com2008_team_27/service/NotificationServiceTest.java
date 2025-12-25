package uk.ac.sheffield.com2008_team_27.service;

import org.junit.jupiter.api.Test;
import uk.ac.sheffield.com2008_team_27.domain.Notification;
import uk.ac.sheffield.com2008_team_27.dto.NotificationDTO;
import uk.ac.sheffield.com2008_team_27.repository.NotificationRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService classUnderTest;

    @Test
    void createNewNotification_succeeds(){
        Integer expectedId = 1;
        when(notificationRepository.save(Mockito.any(Notification.class)))
                .thenReturn(new Notification(expectedId,1234, "test notification"));

        Integer actualId = classUnderTest.sendNotification(1234, "test notification").getId();

        assertThat(actualId).isEqualTo(expectedId);
        Mockito.verify(notificationRepository).save(Mockito.any(Notification.class));
    }
}

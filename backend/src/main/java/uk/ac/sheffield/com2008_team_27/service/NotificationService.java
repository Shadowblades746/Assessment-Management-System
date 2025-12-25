package uk.ac.sheffield.com2008_team_27.service;

import uk.ac.sheffield.com2008_team_27.domain.Notification;
import uk.ac.sheffield.com2008_team_27.repository.NotificationRepository;

public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
    }

    public Notification sendNotification(Integer userId, String content){
        Notification newNotif = new Notification(userId, content);
        return notificationRepository.save(newNotif);
    }
}

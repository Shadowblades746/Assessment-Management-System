package uk.ac.sheffield.com2008_team_27.dto;

import uk.ac.sheffield.com2008_team_27.domain.Notification;

public class NotificationDTO {
    private Integer id;
    private Integer userId;
    private String content;
    private boolean read;

    public NotificationDTO(){}

    public Notification toEntity(){
        Notification notif = new Notification();
        notif.setUserId(this.userId);
        notif.setContent(this.content);
        notif.setRead(this.read);
        return notif;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}

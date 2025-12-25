package uk.ac.sheffield.com2008_team_27.domain;

import jakarta.persistence.*;
import org.hibernate.type.internal.ImmutableNamedBasicTypeImpl;
import uk.ac.sheffield.com2008_team_27.dto.NotificationDTO;

@Entity
@Table
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer userId;
    private String content;
    private boolean read;

    public Notification(){} //empty constructor for jpa

    public Notification(Integer userId, String content){
        this.userId = userId;
        this.content = content;
        this.read = false;
    }

    //separate constructor for testing
    public Notification(Integer id, Integer userId, String content) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.read = false;
    }

    public NotificationDTO toDTO(){
        NotificationDTO dto = new NotificationDTO();
        dto.setId(this.id);
        dto.setUserId(this.userId);
        dto.setContent(this.content);
        dto.setRead(this.read);
        return dto;
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

    public Integer getId(){
        return id;
    }
}


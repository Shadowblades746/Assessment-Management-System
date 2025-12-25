package uk.ac.sheffield.com2008_team_27.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

public class AssessmentFeedback {

    @Id
    private int assessmentId;
    
    @Id
    private int userId;

    @Column
    private String assessmentFeedback;

    @Column
    private LocalDateTime feedbackDate;

    public AssessmentFeedback(int assessmentId, int userId, String assessmentFeedback, LocalDateTime feedbackDate) {
        this.assessmentId = assessmentId;
        this.userId = userId;
        this.assessmentFeedback = assessmentFeedback;
        this.feedbackDate = feedbackDate;
    }

    public AssessmentFeedback() {}

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAssessmentFeedback() {
        return assessmentFeedback;
    }

    public void setAssessmentFeedback(String assessmentFeedback) {
        this.assessmentFeedback = assessmentFeedback;
    }

    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
}

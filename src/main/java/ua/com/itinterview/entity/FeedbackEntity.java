package ua.com.itinterview.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ua.com.itinterview.web.command.FeedbackCommand;

@Entity
@Table(name = "feedbacks")
@SequenceGenerator(name = "sequence", sequenceName = "feedbacks_id", allocationSize = 1)
public class FeedbackEntity extends EntityWithId {

    private String feedbackText;
    private Date createTime;
    private boolean checked;

    @ManyToOne
    @JoinColumn(name = "interview_id")
    private InterviewEntity interview;

    public FeedbackEntity(FeedbackCommand commandMock) {
	feedbackText = commandMock.getFeedbackText();
	createTime = commandMock.getCreateTime();
	checked = commandMock.isChecked();
    }

    public FeedbackEntity() {

    }

    public Date getCreateTime() {
	return createTime;
    }

    public void setCreateTime(Date createTime) {
	this.createTime = createTime;
    }

    public String getFeedbackText() {
	return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
	this.feedbackText = feedbackText;
    }

    public boolean isChecked() {
	return checked;
    }

    public void setChecked(boolean checked) {
	this.checked = checked;
    }

    public InterviewEntity getInterview() {
	return interview;
    }

    public void setInterview(InterviewEntity interview) {
	this.interview = interview;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + (checked ? 1231 : 1237);
	result = prime * result
		+ ((createTime == null) ? 0 : createTime.hashCode());
	result = prime * result
		+ ((feedbackText == null) ? 0 : feedbackText.hashCode());
	result = prime * result
		+ ((interview == null) ? 0 : interview.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	FeedbackEntity other = (FeedbackEntity) obj;
	if (checked != other.checked)
	    return false;
	if (createTime == null) {
	    if (other.createTime != null)
		return false;
	} else if (!createTime.equals(other.createTime))
	    return false;
	if (feedbackText == null) {
	    if (other.feedbackText != null)
		return false;
	} else if (!feedbackText.equals(other.feedbackText))
	    return false;
	if (interview == null) {
	    if (other.interview != null)
		return false;
	} else if (!interview.equals(other.interview))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "FeedbackEntity [feedbackText=" + feedbackText + ", createTime="
		+ createTime + ", checked=" + checked + ", interview="
		+ interview + "]";
    }

}

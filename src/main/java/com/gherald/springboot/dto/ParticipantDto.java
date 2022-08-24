package com.gherald.springboot.dto;

import com.gherald.springboot.model.Change;
import com.gherald.springboot.model.ChangeReview;

import java.util.List;

public class ParticipantDto {
    private String id;
    private String tool;

    private String project;

    private Integer taskATime;

    private Boolean completed;
    private List<ChangeReviewDto> changeReviews;

    public ParticipantDto(String id, String tool, String project, Integer taskATime, Boolean completed, List<ChangeReviewDto> changeReviews) {
        this.id = id;
        this.tool = tool;
        this.project = project;
        this.taskATime = taskATime;
        this.completed = completed;
        this.changeReviews = changeReviews;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Integer getTaskATime() {
        return taskATime;
    }

    public void setTaskATime(Integer taskATime) {
        this.taskATime = taskATime;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<ChangeReviewDto> getChangeReviews() {
        return changeReviews;
    }

    public void setChangeReviews(List<ChangeReviewDto> changeReviews) {
        this.changeReviews = changeReviews;
    }
}

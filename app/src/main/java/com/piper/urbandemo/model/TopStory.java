package com.piper.urbandemo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by developers on 12/12/17.
 */

public class TopStory {

    @SerializedName("id")
    private long id;

    @SerializedName("by")
    private String userName;

    @SerializedName("descendants")
    private int totalCommentCount;

    @SerializedName("title")
    private String title;

    @SerializedName("score")
    private int score;

    @SerializedName("type")
    private String type;

    @SerializedName("url")
    private String url;

    @SerializedName("time")
    private long timeStamp;

    @SerializedName("kids")
    private ArrayList<Long> commentIds;


    /************************************************
     * Getter Setter
     ************************************************/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTotalCommentCount() {
        return totalCommentCount;
    }

    public void setTotalCommentCount(int totalCommentCount) {
        this.totalCommentCount = totalCommentCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ArrayList<Long> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(ArrayList<Long> commentIds) {
        this.commentIds = commentIds;
    }
}

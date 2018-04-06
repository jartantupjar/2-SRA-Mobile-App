package com.example.micha.srafarmer.Entity;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    private int id, postID, score;
    private String farmersName, message;
    private char recommended;
    private Date date;
    private boolean liked;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the postID
     */
    public int getPostID() {
        return postID;
    }

    /**
     * @param postID the postID to set
     */
    public void setPostID(int postID) {
        this.postID = postID;
    }

    /**
     * @return the farmersName
     */
    public String getFarmersName() {
        return farmersName;
    }

    /**
     * @param farmersName the farmersName to set
     */
    public void setFarmersName(String farmersName) {
        this.farmersName = farmersName;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the recommended
     */
    public char getRecommended() {
        return recommended;
    }

    /**
     * @param recommended the recommended to set
     */
    public void setRecommended(char recommended) {
        this.recommended = recommended;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the liked
     */
    public boolean isLiked() {
        return liked;
    }

    /**
     * @param liked the liked to set
     */
    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}

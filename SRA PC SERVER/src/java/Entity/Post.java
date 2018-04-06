package Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Serializable{
    private int id, fieldID, problemID, recommendationID, notificationID;
    private String title, message, status, farmersName, message2;
    private Date datePosted, dateStarted, dateEnded;
    private ArrayList<Comment> comments;

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
     * @return the fieldID
     */
    public int getFieldID() {
        return fieldID;
    }

    /**
     * @param fieldID the fieldID to set
     */
    public void setFieldID(int fieldID) {
        this.fieldID = fieldID;
    }

    /**
     * @return the problemID
     */
    public int getProblemID() {
        return problemID;
    }

    /**
     * @param problemID the problemID to set
     */
    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }

    /**
     * @return the recommendationID
     */
    public int getRecommendationID() {
        return recommendationID;
    }

    /**
     * @param recommendationID the recommendationID to set
     */
    public void setRecommendationID(int recommendationID) {
        this.recommendationID = recommendationID;
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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the datePosted
     */
    public Date getDatePosted() {
        return datePosted;
    }

    /**
     * @param datePosted the datePosted to set
     */
    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    /**
     * @return the dateStarted
     */
    public Date getDateStarted() {
        return dateStarted;
    }

    /**
     * @param dateStarted the dateStarted to set
     */
    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    /**
     * @return the dateEnded
     */
    public Date getDateEnded() {
        return dateEnded;
    }

    /**
     * @param dateEnded the dateEnded to set
     */
    public void setDateEnded(Date dateEnded) {
        this.dateEnded = dateEnded;
    }

    /**
     * @return the comments
     */
    public ArrayList<Comment> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the message2
     */
    public String getMessage2() {
        return message2;
    }

    /**
     * @param message2 the message2 to set
     */
    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    /**
     * @return the notificationID
     */
    public int getNotificationID() {
        return notificationID;
    }

    /**
     * @param notificationID the notificationID to set
     */
    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }
}

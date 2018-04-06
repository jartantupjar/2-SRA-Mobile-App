package Entity;

import java.io.Serializable;
import java.util.Date;

public class Problem implements Serializable{
    private String name, description, type, status, phase, message;
    private int FieldsID, problemsID, id, notificationID, disasterAlertID;
    private double damage;
    private Date date;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the phase
     */
    public String getPhase() {
        return phase;
    }

    /**
     * @param phase the phase to set
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * @return the FieldsID
     */
    public int getFieldsID() {
        return FieldsID;
    }

    /**
     * @param FieldsID the FieldsID to set
     */
    public void setFieldsID(int FieldsID) {
        this.FieldsID = FieldsID;
    }

    /**
     * @return the problemsID
     */
    public int getProblemsID() {
        return problemsID;
    }

    /**
     * @param problemsID the problemsID to set
     */
    public void setProblemsID(int problemsID) {
        this.problemsID = problemsID;
    }

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
     * @return the damage
     */
    public double getDamage() {
        return damage;
    }

    /**
     * @param damage the damage to set
     */
    public void setDamage(double damage) {
        this.damage = damage;
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

    /**
     * @return the disasterAlertID
     */
    public int getDisasterAlertID() {
        return disasterAlertID;
    }

    /**
     * @param disasterAlertID the disasterAlertID to set
     */
    public void setDisasterAlertID(int disasterAlertID) {
        this.disasterAlertID = disasterAlertID;
    }
}

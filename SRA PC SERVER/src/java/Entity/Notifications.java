package Entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

public class Notifications implements Serializable{
    private ArrayList<Problem> problems, disasters;
    private ArrayList<Recommendation> recommendations, reminders;
    private ArrayList<Post> posts;
    private boolean empty;
    private ArrayList<Phase> phases;
    private int year;
    private Date date;

    public Notifications(){
        this.empty = true;
    }

    public ArrayList<Problem> getProblems() {
        return problems;
    }

    public void setProblems(ArrayList<Problem> problems) {
        this.problems = problems;
    }

    public ArrayList<Problem> getDisasters() {
        return disasters;
    }

    public void setDisasters(ArrayList<Problem> disasters) {
        this.disasters = disasters;
    }

    public ArrayList<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(ArrayList<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public ArrayList<Phase> getPhases() {
        return phases;
    }

    public void setPhases(ArrayList<Phase> phases) {
        this.phases = phases;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Recommendation> getReminders() {
        return reminders;
    }

    public void setReminders(ArrayList<Recommendation> reminders) {
        this.reminders = reminders;
    }

}

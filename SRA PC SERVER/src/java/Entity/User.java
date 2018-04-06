package Entity;

import java.util.ArrayList;


public class User {
    private String username, password, role, district, name;
    private long date;
    private int year;
    private ArrayList<String> varieties;
    private ArrayList<String> cropClass;
    private ArrayList<String> textures;
    private ArrayList<String> topography;
    private ArrayList<String> fertilizers;
    private ArrayList<String> farmingSystems;
    private ArrayList<Phase> phases;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(String district) {
        this.district = district;
    }

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getVarieties() {
        return varieties;
    }

    public void setVarieties(ArrayList<String> varieties) {
        this.varieties = varieties;
    }

    public ArrayList<String> getCropClass() {
        return cropClass;
    }

    public void setCropClass(ArrayList<String> cropClass) {
        this.cropClass = cropClass;
    }

    public ArrayList<String> getTextures() {
        return textures;
    }

    public void setTextures(ArrayList<String> textures) {
        this.textures = textures;
    }

    public ArrayList<String> getTopography() {
        return topography;
    }

    public void setTopography(ArrayList<String> topography) {
        this.topography = topography;
    }

    public ArrayList<String> getFertilizers() {
        return fertilizers;
    }

    public void setFertilizers(ArrayList<String> fertilizers) {
        this.fertilizers = fertilizers;
    }

    public ArrayList<String> getFarmingSystems() {
        return farmingSystems;
    }

    public void setFarmingSystems(ArrayList<String> farmingSystems) {
        this.farmingSystems = farmingSystems;
    }

    public ArrayList<Phase> getPhases() {
        return phases;
    }

    public void setPhases(ArrayList<Phase> phases) {
        this.phases = phases;
    }
}

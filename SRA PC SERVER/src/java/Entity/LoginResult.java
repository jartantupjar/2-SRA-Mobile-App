package Entity;

import java.util.ArrayList;
import java.util.Date;

public class LoginResult {
    private Farmer farmer;
    private ArrayList<String> varieties;
    private ArrayList<String> cropClass;
    private ArrayList<String> textures;
    private ArrayList<String> topography;
    private ArrayList<String> fertilizers;
    private ArrayList<String> farmingSystems;
    private ArrayList<Production> productions;
    private ArrayList<CVS> cvs;
    private ArrayList<Fertilizer> fertilizersSubmitted;
    private ArrayList<Tiller> tillers;
    private ArrayList<Problem> problems;
    private ArrayList<Recommendation> recommendations;
    private ArrayList<Phase> phases;
    private int year;
    private Date date;

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
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

    public ArrayList<Production> getProductions() {
        return productions;
    }

    public void setProductions(ArrayList<Production> productions) {
        this.productions = productions;
    }

    /**
     * @return the cvs
     */
    public ArrayList<CVS> getCvs() {
        return cvs;
    }

    /**
     * @param cvs the cvs to set
     */
    public void setCvs(ArrayList<CVS> cvs) {
        this.cvs = cvs;
    }

    /**
     * @return the fertilizersSubmitted
     */
    public ArrayList<Fertilizer> getFertilizersSubmitted() {
        return fertilizersSubmitted;
    }

    /**
     * @param fertilizersSubmitted the fertilizersSubmitted to set
     */
    public void setFertilizersSubmitted(ArrayList<Fertilizer> fertilizersSubmitted) {
        this.fertilizersSubmitted = fertilizersSubmitted;
    }

    /**
     * @return the tillers
     */
    public ArrayList<Tiller> getTillers() {
        return tillers;
    }

    /**
     * @param tillers the tillers to set
     */
    public void setTillers(ArrayList<Tiller> tillers) {
        this.tillers = tillers;
    }

    /**
     * @return the problems
     */
    public ArrayList<Problem> getProblems() {
        return problems;
    }

    /**
     * @param problems the problems to set
     */
    public void setProblems(ArrayList<Problem> problems) {
        this.problems = problems;
    }

    /**
     * @return the recommendations
     */
    public ArrayList<Recommendation> getRecommendations() {
        return recommendations;
    }

    /**
     * @param recommendations the recommendations to set
     */
    public void setRecommendations(ArrayList<Recommendation> recommendations) {
        this.recommendations = recommendations;
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
     * @return the phases
     */
    public ArrayList<Phase> getPhases() {
        return phases;
    }

    /**
     * @param phases the phases to set
     */
    public void setPhases(ArrayList<Phase> phases) {
        this.phases = phases;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }
}

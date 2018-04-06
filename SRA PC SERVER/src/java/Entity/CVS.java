package Entity;

import java.util.Date;

public class CVS {
    
    private int year;
    private int field;
    private int numMillable;
    private String variety;
    private String cropClass;
    private String texture;
    private String farmingSystem;
    private String topography;
    private double furrowDistance;
    private double plantingDensity;
    private double avgMillableStool;
    private double brix;
    private double stalkLength;
    private double diameter;
    private double weight;
    private Date harvestDate;
    private Date dateMillable;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public int getNumMillable() {
        return numMillable;
    }

    public void setNumMillable(int numMillable) {
        this.numMillable = numMillable;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getCropClass() {
        return cropClass;
    }

    public void setCropClass(String cropClass) {
        this.cropClass = cropClass;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getFarmingSystem() {
        return farmingSystem;
    }

    public void setFarmingSystem(String farmingSystem) {
        this.farmingSystem = farmingSystem;
    }

    public String getTopography() {
        return topography;
    }

    public void setTopography(String topography) {
        this.topography = topography;
    }

    public double getFurrowDistance() {
        return furrowDistance;
    }

    public void setFurrowDistance(double furrowDistance) {
        this.furrowDistance = furrowDistance;
    }

    public double getPlantingDensity() {
        return plantingDensity;
    }

    public void setPlantingDensity(double plantingDensity) {
        this.plantingDensity = plantingDensity;
    }

    public double getAvgMillableStool() {
        return avgMillableStool;
    }

    public void setAvgMillableStool(double avgMillableStool) {
        this.avgMillableStool = avgMillableStool;
    }

    public double getBrix() {
        return brix;
    }

    public void setBrix(double brix) {
        this.brix = brix;
    }

    public double getStalkLength() {
        return stalkLength;
    }

    public void setStalkLength(double stalkLength) {
        this.stalkLength = stalkLength;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(Date harvestDate) {
        this.harvestDate = harvestDate;
    }

    public Date getDateMillable() {
        return dateMillable;
    }

    public void setDateMillable(Date dateMillable) {
        this.dateMillable = dateMillable;
    }
}

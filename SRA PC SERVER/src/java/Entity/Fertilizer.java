package Entity;

public class Fertilizer {
    
    private int year;
    private int fieldID;
    private String fertilizer;
    private double firstDose;
    private double secondDose;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getFieldID() {
        return fieldID;
    }

    public void setFieldID(int fieldID) {
        this.fieldID = fieldID;
    }

    public String getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(String fertilizer) {
        this.fertilizer = fertilizer;
    }

    public double getFirstDose() {
        return firstDose;
    }

    public void setFirstDose(double firstDose) {
        this.firstDose = firstDose;
    }

    public double getSecondDose() {
        return secondDose;
    }

    public void setSecondDose(double secondDose) {
        this.secondDose = secondDose;
    }
}

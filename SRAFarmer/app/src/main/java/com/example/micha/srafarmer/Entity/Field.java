package com.example.micha.srafarmer.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Field implements Serializable {
    private String coordsString;
    private ArrayList<LatLng> coords;
    private double organicMatter;
    private double phosphorus;
    private double phLevel;
    private double potassium;
    private double area;
    private String managementType;
    private String municipality;
    private String barangay;
    private String district;
    private String farmer;
    private int id;
    private double damage;
    private CVS cvs;

    public Field(){}
    
    public Field (ArrayList<LatLng> coords, double area){
        this.setCoords(coords);
        this.setCoordsString(BoundaryToString(coords));
        this.setArea(area);
    }

    public double getOrganicMatter() {
        return organicMatter;
    }

    public void setOrganicMatter(double organicMatter) {
        this.organicMatter = organicMatter;
    }

    public double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public double getPhLevel() {
        return phLevel;
    }

    public void setPhLevel(double phLevel) {
        this.phLevel = phLevel;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public String getManagementType() {
        return managementType;
    }

    public void setManagementType(String managementType) {
        this.managementType = managementType;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }
    private List<LatLng> StringToBoundary(String boundary) {
        if (boundary == null) {
            return new ArrayList<LatLng>();
        }
        StringTokenizer tokens = new StringTokenizer(boundary, ",");
        List<LatLng> points = new ArrayList<LatLng>();
        while (tokens.countTokens() > 1) {
            String lat = tokens.nextToken();
            String lng = tokens.nextToken();
            points.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
        }
        return points;
    }

    private  String BoundaryToString(List<LatLng> boundary) {
        String strNewBoundary = "";
        if (boundary != null && boundary.isEmpty() == false) {
            // Generate boundary
            StringBuilder newBoundary = new StringBuilder(boundary.size() * 20);
            for (int i = 0; i < boundary.size(); i++) {
                newBoundary.append(boundary.get(i).getLat());
                newBoundary.append(",");
                newBoundary.append(boundary.get(i).getLng());
                newBoundary.append(",");
            }
            newBoundary.deleteCharAt(newBoundary.length() - 1);
            strNewBoundary = newBoundary.toString();
        }
        return strNewBoundary;
    }

    public String getCoordsString() {
        return coordsString;
    }

    public void setCoordsString(String coordsString) {
        this.coordsString = coordsString;
    }

    public ArrayList<LatLng> getCoords() {
        return (ArrayList<LatLng>) StringToBoundary(coordsString);
    }

    public void setCoords(ArrayList<LatLng> coords) {
        this.coords = coords;
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

    public CVS getCvs() {
        return cvs;
    }

    public void setCvs(CVS cvs) {
        this.cvs = cvs;
    }

    public String getFarmer() {
        return farmer;
    }

    public void setFarmer(String farmer) {
        this.farmer = farmer;
    }
}

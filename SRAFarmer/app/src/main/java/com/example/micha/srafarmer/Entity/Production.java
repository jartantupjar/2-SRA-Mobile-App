package com.example.micha.srafarmer.Entity;

import java.io.Serializable;
import java.util.Date;

public class Production implements Serializable{
    private int id;
    private int year;
    private int fieldsID;
    private double areaHarvested;
    private double tonsCane;
    private double lkg;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getFieldsID() {
        return fieldsID;
    }

    public void setFieldsID(int fieldsID) {
        this.fieldsID = fieldsID;
    }

    public double getAreaHarvested() {
        return areaHarvested;
    }

    public void setAreaHarvested(double areaHarvested) {
        this.areaHarvested = areaHarvested;
    }

    public double getTonsCane() {
        return tonsCane;
    }

    public void setTonsCane(double tonsCane) {
        this.tonsCane = tonsCane;
    }

    public double getLkg() {
        return lkg;
    }

    public void setLkg(double lkg) {
        this.lkg = lkg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

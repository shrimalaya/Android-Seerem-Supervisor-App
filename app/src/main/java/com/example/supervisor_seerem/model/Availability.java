package com.example.supervisor_seerem.model;

public class Availability {
    private String id;
    private String total_hrs;
    private String overtime;
    private String mon;
    private String tue;
    private String wed;
    private String thu;
    private String fri;
    private String sat;
    private String sun;

    public Availability(String id, String mon, String tue, String wed, String thu, String fri, String sat, String sun) {
        this.id = id;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    public String getId() {
        return id;
    }

    public String getTotal_hrs() {
        return total_hrs;
    }

    public String getOvertime() {
        return overtime;
    }

    public String getMon() {
        return mon;
    }

    public String getTue() {
        return tue;
    }

    public String getWed() {
        return wed;
    }

    public String getThu() {
        return thu;
    }

    public String getFri() {
        return fri;
    }

    public String getSat() {
        return sat;
    }

    public String getSun() {
        return sun;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public void setTotal_hrs(String total_hrs) {
        this.total_hrs = total_hrs;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }
}

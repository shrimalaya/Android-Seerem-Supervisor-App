package com.example.supervisor_seerem.model;

public class DayLeave {

    private String id;
    private String date;
    private String leaveDuration;
    private String explanation;

    public DayLeave(String id, String date, String leaveDuration, String explanation){
        this.id = id;
        this.date = date;
        this.leaveDuration = leaveDuration;
        this.explanation = explanation;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getLeaveDuration() {
        return leaveDuration;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLeaveDuration(String leaveDuration) {
        this.leaveDuration = leaveDuration;
    }


    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return "Overtime{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", leaveDuration='" + leaveDuration + '\'' +
                ", explanation='" + explanation + '\'' +
                '}';
    }

}

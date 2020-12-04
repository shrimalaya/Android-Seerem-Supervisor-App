package com.example.supervisor_seerem.model;

public class Overtime {
    private String id;
    private String dayOfOvertime;
    private String overtimeHours;
    private String explanation;

    public Overtime(String id, String dayOfOvertime, String overtimeHours, String explanation){
        this.id = id;
        this.dayOfOvertime = dayOfOvertime;
        this.overtimeHours = overtimeHours;
        this.explanation = explanation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDayOfOvertime() {
        return dayOfOvertime;
    }

    public void setDayOfOvertime(String dayOfOvertime) {
        this.dayOfOvertime = dayOfOvertime;
    }

    public String getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(String overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return "Overtime{" +
                "id='" + id + '\'' +
                ", dayOfOvertime='" + dayOfOvertime + '\'' +
                ", overtimeHours='" + overtimeHours + '\'' +
                ", explanation='" + explanation + '\'' +
                '}';
    }

}

package com.example.supervisor_seerem.model;

public class Emergency {
    private String id;
    private String blood_grp;
    private String medical_conditions;
    private String emergency_name;
    private String emergency_number;
    private String relationship;

    public Emergency(String id, String blood_grp, String medical_conditions, String emergency_name, String emergency_number, String relationship) {
        this.id = id;
        this.blood_grp = blood_grp;
        this.medical_conditions = medical_conditions;
        this.emergency_name = emergency_name;
        this.emergency_number = emergency_number;
        this.relationship = relationship;
    }

    public String getId() {
        return id;
    }

    public String getBlood_grp() {
        return blood_grp;
    }

    public String getMedical_conditions() {
        return medical_conditions;
    }

    public String getEmergency_name() {
        return emergency_name;
    }

    public String getEmergency_number() {
        return emergency_number;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setBlood_grp(String blood_grp) {
        this.blood_grp = blood_grp;
    }

    public void setMedical_conditions(String medical_conditions) {
        this.medical_conditions = medical_conditions;
    }

    public void setEmergency_name(String emergency_name) {
        this.emergency_name = emergency_name;
    }

    public void setEmergency_number(String emergency_number) {
        this.emergency_number = emergency_number;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}

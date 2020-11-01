package com.example.supervisor_seerem.model;

/**
 *  Supervisor (only for the user of the application)
 *  Class for defining data realted to the Supervisor object,
 *  which is meant to relate to a Supervisor's data
 * @Author Michael Mora
 */
public class Supervisor {
    private String savedUsername;
    private String savedPassword;
    private String id;
    private String medicalConsiderations;
    private String emergencyContactName;
    private int emergencyContantNumber;


    // When creating a new user, their username and password will be the first things saved; everything else will be added manually later on
    private Supervisor(String username, String password){
        savedUsername = username;
        savedPassword = password;
    }

    private Supervisor(){}

    private static Supervisor instance;

    // Theoretically since there should only be one instance of a
    // Supervisor stored in data at a time (
    public static Supervisor getInstance(String username, String password){
        if(instance == null){
            instance = new Supervisor(username, password);
        }
        return instance;
    }

    // Could be called when the user logs out, so make the reference null so a new instance of
    // Supervisor can be instantiated when someone logs in again.
    public void removeSupervisor(){
        if(instance != null){
            instance = null;
        }
    }

    public String getSavedUsername() {
        return savedUsername;
    }

    public void setSavedUsername(String savedUsername) {
        this.savedUsername = savedUsername;
    }

    public String getSavedPassword() {
        return savedPassword;
    }

    public void setSavedPassword(String savedPassword) {
        this.savedPassword = savedPassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicalConsiderations() {
        return medicalConsiderations;
    }

    public void setMedicalConsiderations(String medicalConsiderations) {
        this.medicalConsiderations = medicalConsiderations;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public int getEmergencyContantNumber() {
        return emergencyContantNumber;
    }

    public void setEmergencyContantNumber(int emergencyContantNumber) {
        this.emergencyContantNumber = emergencyContantNumber;
    }
}

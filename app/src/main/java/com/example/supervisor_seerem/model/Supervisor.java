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
    private int emergencyContactNumber;
    private String firstName;
    private String lastName;
    private String company_id;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setWorksite_id(String worksite_id) {
        this.worksite_id = worksite_id;
    }

    private String worksite_id;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getWorksite_id() {
        return worksite_id;
    }

    public static Supervisor getInstance() {
        return instance;
    }

    // When creating a new user, their username and password will be the first things saved; everything else will be added manually later on
    private Supervisor(String username, String password){
        savedUsername = username;
        savedPassword = password;
    }

    public Supervisor(String id, String firstName, String lastName, String company_id) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company_id = company_id;
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

    // Since Personal Information should be able to be altered by user, create setters to allow this.
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

    public int getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(int emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }
}

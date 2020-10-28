package com.example.supervisor_seerem.model;

/**
 * Class name: Site
 *
 * Description: A class that stores information about one work site object.
 *
 */
public class Site {
    private String projectID;
    private String location;
    private String emergencyResponse;
    private String masterpoint;
    private String hse;
    private String operationHour;

    // Parameterized Constructor
    public Site(String projectID, String location, String emergencyResponse, String masterpoint,
                String hse, String operationHour) {
        this.projectID = projectID;
        this.location = location;
        this.emergencyResponse = emergencyResponse;
        this.masterpoint = masterpoint;
        this.hse = hse;
        this.operationHour = operationHour;
    }

    // Getters
    public String getProjectID() {
        return projectID;
    }

    public String getLocation() {
        return location;
    }

    public String getEmergencyResponse() {
        return emergencyResponse;
    }

    public String getMasterpoint() {
        return masterpoint;
    }

    public String getHse() {
        return hse;
    }

    public String getOperationHour() {
        return operationHour;
    }

    @Override
    public String toString() {
        return "Site{" +
                "projectID='" + projectID + '\'' +
                ", location='" + location + '\'' +
                ", emergencyInfo='" + emergencyResponse + '\'' +
                ", masterpoint='" + masterpoint + '\'' +
                ", hse='" + hse + '\'' +
                ", operationHour='" + operationHour + '\'' +
                '}';
    }
}

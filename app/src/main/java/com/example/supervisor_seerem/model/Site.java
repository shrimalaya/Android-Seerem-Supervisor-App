package com.example.supervisor_seerem.model;

import com.google.type.LatLng;

/**
 * Class name: Site
 *
 * Description: A class that stores information about one work site object.
 *
 */
public class Site {
    private String projectID;
    private String ID;
    private LatLng location;
    private String emergencyResponse;
    private LatLng masterpoint;
    private String hseLink;
    private String operationHour;

    // Parameterized Constructor
    public Site(String ID, String projectID, LatLng location, LatLng masterpoint, String hseLink, String operationHour) {
        this.projectID = projectID;
        this.ID = ID;
        this.location = location;
        this.masterpoint = masterpoint;
        this.hseLink = hseLink;
        this.operationHour = operationHour;
    }

    // Getters
    public String getID() { return ID; }

    public String getHseLink() { return hseLink; }

    public String getProjectID() {
        return projectID;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getEmergencyResponse() {
        return emergencyResponse;
    }

    public LatLng getMasterpoint() {
        return masterpoint;
    }

    public String getHse() {
        return hseLink;
    }

    public String getOperationHour() {
        return operationHour;
    }

    @Override
    public String toString() {
        return "Site{" +
                "projectID='" + projectID + '\'' +
                ", location='" + location + '\'' +
                ", masterpoint='" + masterpoint + '\'' +
                ", hse='" + hseLink + '\'' +
                ", operationHour='" + operationHour + '\'' +
                '}';
    }
}

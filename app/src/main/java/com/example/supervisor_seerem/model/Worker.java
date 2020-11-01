package com.example.supervisor_seerem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class name: Worker
 *
 * Description: A class that stores information about one worker.
 */
public class Worker {
    private String firstName;
    private String lastName;
    private String employeeID;
    private String supervisorID;
    private String worksiteID;
    private List<String> skills = new ArrayList<>();

    // Parameterized Constructor
    public Worker(String firstName, String lastName, String employeeID, String supervisorID,
                  String worksiteID, List<String> skills) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeID = employeeID;
        this.supervisorID = supervisorID;
        this.worksiteID = worksiteID;
        this.skills = skills;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getSupervisorID() {
        return supervisorID;
    }

    public String getWorksiteID() {
        return worksiteID;
    }

    public List<String> getSkills() {
        return skills;
    }

    public boolean hasRequiredSkill(String requiredSkill) {
        for (String skill : this.skills) {
            if (skill.equals(requiredSkill)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employeeID='" + employeeID + '\'' +
                ", supervisorID='" + supervisorID + '\'' +
                ", worksiteID='" + worksiteID + '\'' +
                ", skills=" + skills +
                '}';
    }
}

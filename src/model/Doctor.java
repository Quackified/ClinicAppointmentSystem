package clinicapp.src.model;

import java.util.ArrayList; // TODO
import java.util.List; // TODO

public class Doctor {
    private static int nextId = 1;
    private final int id;
    
    private String name; // No age cause it is Irrelevant, Redundant and for Privacy Purposes (Personal Data)
    private String specialization;
    private String phoneNumber;
    private String email;
    private List<String> availableDays; // List of available days "Monday", "Tuesday", etc
    private String startTime;   
    private String endTime;
    private boolean isAvailable;



    // Constructor for Doctor - Contains Essential Details
    public Doctor(String name, String specialization, String phoneNumber, String email,
            List<String> availableDays, String startTime, String endTime, boolean isAvailable) {
        this.id = nextId++; // not in the parameters since it is assigned independtly when instantiated.

        this.name = name;
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.availableDays = availableDays; // Todo - requires integration of the lists here
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
    }



    // Getter Methods - Doctor
    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
    
    /*  TODO - WILL ADD LATER
    public List<String< getAvailableDays() {
        return ???
    }
    */

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public boolean getIsAvailable() {
        return isAvailable;
    }



    // Setter Methods - Doctor
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*  TODO - WILL ADD LATER
    public List<void< setAvailableDays() {
        this. ahh code here
    }
    */

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }


    // TODO - Implement Formatter for Local Date and such...
}

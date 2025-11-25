package clinicapp.model;

import java.util.ArrayList;
import java.util.List;

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
            List<String> availableDays, String startTime, String endTime) {
        this.id = nextId++; // not in the parameters since it is assigned independtly when instantiated.

        this.name = name;
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.availableDays = availableDays != null ? new ArrayList<>(availableDays) : new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;
    }


    // Getter Methods - Doctor
    public int getId() {
        return id;
    }

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
    
    public List<String> getAvailableDays() {
        return new ArrayList<>(availableDays); // Basically means, create a copy of what is inside the variable
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public boolean isAvailable() {
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

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays != null ? new ArrayList<>(availableDays) : new ArrayList<>(); // True = Copies the current container | False = Makes a new List, uses that instead | By default = Variable has no container #NullPointerException
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }


    public String getDetailedInfo() {
        String status = isAvailable ? "Available" : "Not Available";
        return "Doctor ID: " + id +
               "\n Name: " + name +
               "\n Specialization: " + specialization +
               "\n Phone Number: " + phoneNumber +
               "\n Email: " + (email != null ? email : "N/A") +
               "\n Status: " + status +
               "\n Working Hours: " + startTime + " – " + endTime +
               "\n Available Days: " + String.join(", ", availableDays); 
    }
}

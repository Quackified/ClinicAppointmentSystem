package clinicapp.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;  // TODO - Assign this cause why not :D

public class Patient {
    // When creating an object, assigns the Patient by the nextId, then increments by 1
    private static int nextId = 1;
    private final int id;

    // Essential Details of a Patient
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String bloodType;
    private String allergies;

    // Constructor for Patient
    public Patient(String name, LocalDate dateOfBirth, String gender, String phoneNumber, String email, String address, String bloodType, String allergies) {
        // Increments the id and assigns it
        this.id = nextId++;

        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.bloodType = bloodType;
        this.allergies = allergies;
    }


    // Getter Methods - Patient
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    // Setter Methods - Patient
    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    // TODO - Age Variable - Was recommended to try and calculate based on the Date of Birth instead (Uses LocalDate)
    // TODO - Implement Formatter for Local Date and such...
}

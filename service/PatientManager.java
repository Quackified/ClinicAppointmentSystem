package clinicapp.service;

import clinicapp.model.Patient;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class PatientManager {

    // Initialize Hashmap (Actually called Dictionary in Python <Key, Value>)
    // Basically saying, make a container that takes <Patient ID, Patient's Details> and name it as "patients"
    // Map then to Hashmap for verstaility
    private final Map<Integer, Patient> patients;

    // Constructor
    public PatientManager() {
        this.patients = new HashMap<>();
    }

    // Methods
    public Patient addPatient(String name, LocalDate dateOfBirth, String gender, String phoneNumber, String email, String address,String bloodType, String allergies) {
        Patient patient = new Patient(name, dateOfBirth, gender, phoneNumber, email, address, bloodType, allergies);
        patients.put(patient.getId(), patient);
        return patient;
    }
    
    public Patient getPatientById(int id) {
        return patients.get(id);
    }

    // .values() spits out every value it has
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients.values());
    }

    // Inputs name, checks if it contains that name. Compiles and returns the result.
    public List<Patient> searchPatientByName(String name) {
        List<Patient> results = new ArrayList<>();
        String searchTerm = name.toLowerCase();

        // reusing "patient" here as "i"
        for (Patient patient : patients.values()) {
            if (patient.getName().toLowerCase().contains(searchTerm)) {
                results.add(patient);
            }
        }

        return results;
    }

    // Inputs new Patient Details, checks if it exists, if it does then overwrite every details.
    public Boolean updatePatient(int id, String name, LocalDate dateOfBirth, String gender, String phoneNumber, String email, String address,String bloodType, String allergies) {

        Patient patient =  patients.get(id);
        if (patient == null) {
            System.out.println("Patient does not exist");
            return false;
        }

        if (name != null) patient.setName(name);
        if (dateOfBirth != null) patient.setDateOfBirth(dateOfBirth);
        if (gender != null) patient.setGender(gender);
        if (phoneNumber != null) patient.setPhoneNumber(phoneNumber);
        if (email != null) patient.setEmail(email);
        if (address != null) patient.setAddress(address);
        if (bloodType != null) patient.setBloodType(bloodType);
        if (allergies != null) patient.setAllergies(allergies);
        
        return true;
    }

    // Removes then check if it exists
    public Boolean deletePatient(int id) {
        return patients.remove(id) != null;
    }

    // Gets the size
    public int getPatientCount() {
        return patients.size();
    }

    // Checks if the id has a patient
    public Boolean patientExists(int id) {
        return patients.containsKey(id);
    }

    // Checks gender in every Patient, returns the patient if it matches.
    public List<Patient> searchPatientByGender(String gender) {
        List<Patient> results = new ArrayList<>();
        String searchGender = gender.toLowerCase();

        // reusing "patient" here as "i"
        for (Patient patient : patients.values()) {
            if (patient.getGender().toLowerCase().contains(searchGender)) {
                results.add(patient);
            }
        }

        return results;
    }

    // Inputs two age values, checks if it's between. Compiles and returns the result.
    public List<Patient> getPatientsByAge(int minAge, int maxAge) {
        List<Patient> results = new ArrayList<>();

        for (Patient patient : patients.values()) {
            int age = patient.getAge();

            if (age >= minAge && age <= maxAge) {
                results.add(patient);
            }
        }

        return results;
    }
}
package clinicapp.service;

import clinicapp.model.Doctor;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DoctorManager {

    // Initialize Hashmap for doctors
    private final Map<Integer, Doctor> doctors;

    // Constructor
    public DoctorManager() {
        this.doctors = new HashMap<>();
    }

    // Methods

    // Add a Doctor with these credentials
    public Doctor addDoctor(String name, String specialization, String phoneNumber, String email,
            List<String> availableDays, String startTime, String endTime) {
        Doctor doctor = new Doctor(name, specialization, phoneNumber, email, availableDays, startTime, endTime);
        doctors.put(doctor.getId(), doctor);
        return doctor;
    }

    // Grabs a doctor with the associated ID
    public Doctor getDoctorById(int id) {
        return doctors.get(id);
    }

    // Grabs all doctors in the arraylist
    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors.values());
    }

    // Grabs all available doctors
    public List<Doctor> getAvailableDoctors() {
        List<Doctor> available = new ArrayList<>();
        for(Doctor doctor : doctors.values()) {
            if (doctor.isAvailable()) {
                available.add(doctor);
            }
        }

        return available;
    }

    // Search for a doctor by name (with Partial search)
    public List<Doctor> searchDoctorByName(String name) {
        List<Doctor> results = new ArrayList<>();
        String searchTerm = name.toLowerCase();

        for(Doctor doctor : doctors.values()) {
            if (doctor.getName().toLowerCase().contains(searchTerm)) {
                results.add(doctor);
            }
        }

        return results;
    }

    // Search for a doctor by specialization (with Partial search)
    public List<Doctor> searchDoctorBySpecialization(String specialization) {
        List<Doctor> results = new ArrayList<>();
        String searchTerm = specialization.toLowerCase();

        for (Doctor doctor : doctors.values()) {
            if (doctor.getSpecialization().toLowerCase().contains(searchTerm)) {
                results.add(doctor);
            }
        }

        return results;
    }

    // Update a doctor with the associated ID
    public Boolean updateDoctor(int id, String name, String specialization, String phoneNumber, String email,
            List<String> availableDays, String startTime, String endTime) {
        Doctor doctor = doctors.get(id);
        if (doctor == null) {
            return false;
        }

        if (name != null) doctor.setName(name);
        if (specialization != null) doctor.setSpecialization(specialization);
        if (phoneNumber != null) doctor.setPhoneNumber(phoneNumber);
        if (email!= null) doctor.setEmail(email);
        if (availableDays != null) doctor.setAvailableDays(availableDays);
        if (startTime != null) doctor.setStartTime(startTime);
        if (endTime != null) doctor.setEndTime(endTime);
        
        return true;
    }



    // Delete a doctor with the associated ID
    public Boolean deleteDoctor(int id) {
        return doctors.remove(id) != null;
    }

    // Get the number of doctors in the Hashmap
    public int getDoctorCount() {
        return doctors.size();
    }
    
    // Check if a doctor exists with the associated ID
    public Boolean doctorExists(int id) {
        return doctors.containsKey(id);
    }

}

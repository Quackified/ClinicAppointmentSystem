package clinicapp.service;

import clinicapp.model.Doctor;
import java.util.List;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DoctorManager {
    private final Map<Integer, Doctor> doctors;

    public DoctorManager() {
        this.doctors = new HashMap<>();
    }

    public Doctor addDoctor(String name, String specialization, String phoneNumber, String email,
            List<String> availableDays, LocalTime startTime, LocalTime endTime) {
        Doctor doctor = new Doctor(name, specialization, phoneNumber, email, availableDays, startTime, endTime);
        doctors.put(doctor.getId(), doctor);
        return doctor;
    }

    // Methods
    public Doctor getDoctor(int id) {
        return doctors.get(id);
    }

    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors.values());
    }

    public List<Doctor> getAvailableDoctors() {
        List<Doctor> available = new ArrayList<>();
        for(Doctor doctor : doctors.values()) {
            if (doctor.isAvailable()) {
                available.add(doctor);
            }
        }

        return available;
    }

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

    public Boolean updateDoctor(int id, String name, String specialization, String phoneNumber, String email,
            List<String> availableDays, LocalTime startTime, LocalTime endTime) {
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

    public Boolean setDoctorAvailability(int id, boolean available) {
        Doctor doctor = doctors.get(id);
        if (doctor == null) {
            return false;
        }
        doctor.setIsAvailable(available);
        return true;
    }

    public Boolean deleteDoctor(int id) {
        return doctors.remove(id) != null;
    }

    public int getDoctorCount() {
        return doctors.size();
    }

    public Boolean doctorExists(int id) {
        return doctors.containsKey(id);
    }

    // Using Set instead of List to only contain Unique specializations
    public Set<String> getAllSpecializations() {
        Set<String> specializations = new HashSet<>();
        for (Doctor doctor : doctors.values()) {
            specializations.add(doctor.getSpecialization());
        }

        return specializations;
    }
}

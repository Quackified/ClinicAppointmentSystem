package clinicapp.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import clinicapp.util.DateUtils;

/**
 * Appointment model representing a scheduled appointment in the clinic system.
 * Links patients with doctors at specific times and tracks appointment status.
 */
public class Appointment {
    private static int nextId = 1;
    
    private final int id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime appointmentDateTime;
    private String reason;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    
    public enum AppointmentStatus {
        SCHEDULED,   // Appointment is scheduled and waiting
        CONFIRMED,   // Appointment has been confirmed
        IN_PROGRESS, // Patient is currently being seen
        COMPLETED,   // Appointment is completed
        CANCELLED,   // Appointment has been cancelled
        NO_SHOW      // Patient did not show up
    }
    
    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentDateTime, String reason) {
        this.id = nextId++;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
        this.status = AppointmentStatus.SCHEDULED;
        this.notes = "";
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter Methods - Appointment
    public int getId() {
        return id;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
    
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    // Getter Methods - Appointment
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDetailedInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
        return "Appointment ID: " + id +
               "\n Date & Time: " + DateUtils.formatDateTime(appointmentDateTime) +
               "\n Patient: " + patient.getName() +
               "\n Patient ID: " + patient.getId() +
               "\n Doctor: " + doctor.getName() +
               "\n Doctor ID: " + doctor.getId() +
               "\n Specialization: " + doctor.getSpecialization() +
               "\n Reason: " + reason +
               "\n Status: " + status + 
               "\n Notes: " + (notes != null && !notes.isEmpty() ? notes : "None") +
               "\n Created At: " + DateUtils.formatDateTime(createdAt);
    }
}
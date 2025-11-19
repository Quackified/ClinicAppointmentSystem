package clinicapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment {
    private static int nextId = 1;
    
    private final int id;
    private Patient patient;
    private Doctor doctor;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
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
    
    public Appointment(Patient patient, Doctor doctor, LocalDate appointmentDate, 
                      LocalTime startTime, LocalTime endTime, String reason) {
        this.id = nextId++;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
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
    
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getAppointmentDateTime() {
        return LocalDateTime.of(appointmentDate, startTime);
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
    
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        if (appointmentDateTime != null) {
            this.appointmentDate = appointmentDateTime.toLocalDate();
            this.startTime = appointmentDateTime.toLocalTime();
        }
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
        return "Appointment ID: " + id +
               "\n Date: " + appointmentDate +
               "\n Start Time: " + startTime +
               "\n End Time: " + endTime +
               "\n Patient: " + patient.getName() +
               "\n Patient ID: " + patient.getId() +
               "\n Doctor: " + doctor.getName() +
               "\n Doctor ID: " + doctor.getId() +
               "\n Specialization: " + doctor.getSpecialization() +
               "\n Reason: " + reason +
               "\n Status: " + status +
               "\n Notes: " + (notes != null && !notes.isEmpty() ? notes : "None") +
               "\n Created At: " + createdAt;
    }

}
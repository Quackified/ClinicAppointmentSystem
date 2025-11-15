package clinicapp.model;

import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter; TODO - Needs to be integrated

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
    
    /**
     * Enum representing the status of an appointment.
     */
    public enum AppointmentStatus {
        SCHEDULED,   // Appointment is scheduled and waiting
        CONFIRMED,   // Appointment has been confirmed
        IN_PROGRESS, // Patient is currently being seen
        COMPLETED,   // Appointment is completed
        CANCELLED,   // Appointment has been cancelled
        NO_SHOW      // Patient did not show up
    }
    
    /**
     * Constructor for creating a new appointment with auto-generated ID.
     */
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
    
    /* Get formatted display string for appointment information.
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("ID: %d | %s | Patient: %s | Dr. %s | %s | Status: %s",
        id, appointmentDateTime.format(formatter), patient.getName(), 
        doctor.getName(), reason, status);
    } */
    
    /*
    public String getDetailedInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    APPOINTMENT DETAILS                         ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Appointment ID : %-45d ║\n", id));
        sb.append(String.format("║ Date & Time    : %-45s ║\n", appointmentDateTime.format(formatter)));
        sb.append(String.format("║ Patient        : %-45s ║\n", patient.getName()));
        sb.append(String.format("║ Patient ID     : %-45d ║\n", patient.getId()));
        sb.append(String.format("║ Doctor         : Dr. %-41s ║\n", doctor.getName()));
        sb.append(String.format("║ Doctor ID      : %-45d ║\n", doctor.getId()));
        sb.append(String.format("║ Specialization : %-45s ║\n", doctor.getSpecialization()));
        sb.append(String.format("║ Reason         : %-45s ║\n", reason));
        sb.append(String.format("║ Status         : %-45s ║\n", status));
        if (notes != null && !notes.isEmpty()) {
            sb.append(String.format("║ Notes          : %-45s ║\n", notes));
        }
        sb.append(String.format("║ Created At     : %-45s ║\n", createdAt.format(formatter)));
        sb.append("╚════════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    } */

    public String getDetailedInfo() {
        return "Appointment ID: " + id +
               "\n Date & Time: " + null + // TODO - Formatter
               "\n Patient: " + patient.getName() +
               "\n Patient ID: " + patient.getId() +
               "\n Doctor: " + doctor.getName() +
               "\n Doctor ID: " + doctor.getId() +
               "\n Specialization: " + doctor.getSpecialization() +
               "\n Reason: " + reason +
               "\n Status: " + status + 
               "\n Notes: " + (notes != null && !notes.isEmpty() ? notes : "") + // TODO - check if it's alright
               "\n Created At: " + createdAt; // TODO - Formaatter
    }
}
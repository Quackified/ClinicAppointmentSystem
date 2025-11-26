package clinicapp.io;

import clinicapp.model.Appointment;
import clinicapp.model.Doctor;
import clinicapp.model.Patient;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Limitation: The exports folder must be in  the root, will do an export window where the user can select the folder.
// Not done: Persistence Memory not implemented | Timestamps also not implemented  | Exporter to CSV for Appointmnets (UNUSED)

public class CsvExporter {
    private static final String EXPORT_DIRECTORY = "exports/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // Export Patients to CSV
    public static String exportPatients(List<Patient> patients) throws IOException {
        String fileName = EXPORT_DIRECTORY + "patients_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "_" + System.currentTimeMillis() + ".csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {
                    "ID", "Name", "Date of Birth", "Age", "Gender",
                    "Phone Number", "Email", "Address", "Blood Type", "Allergies"
            };
            writer.writeNext(header);

            for (Patient patient : patients) {
                String[] data = {
                        String.valueOf(patient.getId()),
                        patient.getName(),
                        patient.getDateOfBirth().format(DATE_FORMATTER),
                        String.valueOf(patient.getAge()),
                        patient.getGender(),
                        patient.getPhoneNumber(),
                        patient.getEmail() != null ? patient.getEmail() : "",
                        patient.getAddress(),
                        patient.getBloodType() != null ? patient.getBloodType() : "",
                        patient.getAllergies() != null ? patient.getAllergies() : ""
                };
                writer.writeNext(data);
            }
        }

        return fileName;
    }

    // Export Doctors to CSV
    public static String exportDoctors(List<Doctor> doctors) throws IOException {
        String fileName = EXPORT_DIRECTORY + "doctors_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "_" + System.currentTimeMillis() + ".csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {
                    "ID", "Name", "Specialization", "Phone Number",
                    "Email", "Available Days", "Start Time", "End Time", "Available"
            };
            writer.writeNext(header);

            for (Doctor doctor : doctors) {
                String availableDays = doctor.getAvailableDays() != null ? String.join(";", doctor.getAvailableDays())
                        : "";
                String[] data = {
                        String.valueOf(doctor.getId()),
                        doctor.getName(),
                        doctor.getSpecialization(),
                        doctor.getPhoneNumber(),
                        doctor.getEmail() != null ? doctor.getEmail() : "",
                        availableDays,
                        doctor.getStartTime() != null ? doctor.getStartTime() : "",
                        doctor.getEndTime() != null ? doctor.getEndTime() : "",
                        String.valueOf(doctor.isAvailable())
                };
                writer.writeNext(data);
            }
        }

        return fileName;
    }

    // Export Appointments to CSV (UNUSED)
    public static String exportAppointments(List<Appointment> appointments) throws IOException {
        String fileName = EXPORT_DIRECTORY + "appointments_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "_" + System.currentTimeMillis() + ".csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {
                    "ID", "Date", "Start Time", "End Time", "Patient ID", "Patient Name",
                    "Doctor ID", "Doctor Name", "Reason", "Status", "Notes", "Created At"
            };
            writer.writeNext(header);

            for (Appointment appointment : appointments) {
                String[] data = {
                        String.valueOf(appointment.getId()),
                        appointment.getAppointmentDate().format(DATE_FORMATTER),
                        appointment.getStartTime().format(TIME_FORMATTER),
                        appointment.getEndTime().format(TIME_FORMATTER),
                        String.valueOf(appointment.getPatient().getId()),
                        appointment.getPatient().getName(),
                        String.valueOf(appointment.getDoctor().getId()),
                        appointment.getDoctor().getName(),
                        appointment.getReason(),
                        appointment.getStatus().toString(),
                        appointment.getNotes() != null ? appointment.getNotes() : "",
                        appointment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                };
                writer.writeNext(data);
            }
        }

        return fileName;
    }

    // Export Appointments to CSV by Date (UNUSED)
    public static String exportAppointmentsByDate(List<Appointment> appointments, LocalDate date) throws IOException {
        String fileName = EXPORT_DIRECTORY + "appointments_" +
                date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "_" + System.currentTimeMillis() + ".csv";

        List<Appointment> filteredAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentDate().equals(date)) {
                filteredAppointments.add(appointment);
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {
                    "ID", "Date", "Start Time", "End Time", "Patient ID", "Patient Name",
                    "Doctor ID", "Doctor Name", "Reason", "Status", "Notes"
            };
            writer.writeNext(header);

            for (Appointment appointment : filteredAppointments) {
                String[] data = {
                        String.valueOf(appointment.getId()),
                        appointment.getAppointmentDate().format(DATE_FORMATTER),
                        appointment.getStartTime().format(TIME_FORMATTER),
                        appointment.getEndTime().format(TIME_FORMATTER),
                        String.valueOf(appointment.getPatient().getId()),
                        appointment.getPatient().getName(),
                        String.valueOf(appointment.getDoctor().getId()),
                        appointment.getDoctor().getName(),
                        appointment.getReason(),
                        appointment.getStatus().toString(),
                        appointment.getNotes() != null ? appointment.getNotes() : ""
                };
                writer.writeNext(data);
            }
        }

        return fileName;
    }
}
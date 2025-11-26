package clinicapp.io;
 
import clinicapp.model.Appointment;
import clinicapp.model.Appointment.AppointmentStatus;
import clinicapp.model.Doctor;
import clinicapp.model.Patient;
import clinicapp.service.AppointmentManager;
import clinicapp.service.DoctorManager;
import clinicapp.service.PatientManager;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
 
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Not done: Implementation of CSV Importer feature (Code exists but UNUSED)
 
public class CsvImporter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public static class ImportResult {
        public int successCount;
        public int errorCount;
        public List<String> errors;
        
        public ImportResult() {
            this.successCount = 0;
            this.errorCount = 0;
            this.errors = new ArrayList<>();
        }
    }
    
    // Import patients from a CSV file.
    public static ImportResult importPatients(String filePath, PatientManager patientManager) {
        ImportResult result = new ImportResult();
        
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            
            if (records.isEmpty()) {
                result.errors.add("CSV file is empty");
                return result;
            }
            
            boolean firstRow = true;
            for (String[] record : records) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                
                try {
                    if (record.length < 6) {
                        result.errors.add("Invalid record: insufficient columns");
                        result.errorCount++;
                        continue;
                    }
                    
                    String name = record[1];
                    LocalDate dob = LocalDate.parse(record[2], DATE_FORMATTER);
                    String gender = record[4];
                    String phone = record[5];
                    String email = record.length > 6 && !record[6].isEmpty() ? record[6] : null;
                    String address = record.length > 7 ? record[7] : "";
                    String bloodType = record.length > 8 && !record[8].isEmpty() ? record[8] : null;
                    String allergies = record.length > 9 && !record[9].isEmpty() ? record[9] : null;
                    
                    patientManager.addPatient(name, dob, gender, phone, email, address, bloodType, allergies);
                    result.successCount++;
                } catch (Exception e) {
                    result.errors.add("Error importing patient: " + e.getMessage());
                    result.errorCount++;
                }
            }
        } catch (IOException | CsvException e) {
            result.errors.add("Error reading CSV file: " + e.getMessage());
        }
        
        return result;
    }
    
    // Import doctors from a CSV file.
    public static ImportResult importDoctors(String filePath, DoctorManager doctorManager) {
        ImportResult result = new ImportResult();
        
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            
            if (records.isEmpty()) {
                result.errors.add("CSV file is empty");
                return result;
            }
            
            boolean firstRow = true;
            for (String[] record : records) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                
                try {
                    if (record.length < 4) {
                        result.errors.add("Invalid record: insufficient columns");
                        result.errorCount++;
                        continue;
                    }
                    
                    String name = record[1];
                    String specialization = record[2];
                    String phone = record[3];
                    String email = record.length > 4 && !record[4].isEmpty() ? record[4] : null;
                    
                    List<String> availableDays = null;
                    if (record.length > 5 && !record[5].isEmpty()) {
                        availableDays = Arrays.asList(record[5].split(";"));
                    }
                    
                    String startTime = record.length > 6 && !record[6].isEmpty() ? record[6] : null;
                    String endTime = record.length > 7 && !record[7].isEmpty() ? record[7] : null;
                    
                    doctorManager.addDoctor(name, specialization, phone, email, availableDays, startTime, endTime);
                    result.successCount++;
                } catch (Exception e) {
                    result.errors.add("Error importing doctor: " + e.getMessage());
                    result.errorCount++;
                }
            }
        } catch (IOException | CsvException e) {
            result.errors.add("Error reading CSV file: " + e.getMessage());
        }
        
        return result;
    }
    
    // Import appointments from a CSV file (UNUSED)
    public static ImportResult importAppointments(String filePath, AppointmentManager appointmentManager,
                                                  PatientManager patientManager, DoctorManager doctorManager) {
        ImportResult result = new ImportResult();
        
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            
            if (records.isEmpty()) {
                result.errors.add("CSV file is empty");
                return result;
            }
            
            boolean firstRow = true;
            for (String[] record : records) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                
                try {
                    if (record.length < 9) {
                        result.errors.add("Invalid record: insufficient columns");
                        result.errorCount++;
                        continue;
                    }
                    
                    LocalDate date = LocalDate.parse(record[1], DATE_FORMATTER);
                    LocalTime startTime = LocalTime.parse(record[2], TIME_FORMATTER);
                    LocalTime endTime = LocalTime.parse(record[3], TIME_FORMATTER);
                    int patientId = Integer.parseInt(record[4]);
                    int doctorId = Integer.parseInt(record[6]);
                    String reason = record[8];
                    
                    Patient patient = patientManager.getPatientById(patientId);
                    Doctor doctor = doctorManager.getDoctorById(doctorId);
                    
                    if (patient == null) {
                        result.errors.add("Patient with ID " + patientId + " not found");
                        result.errorCount++;
                        continue;
                    }
                    
                    if (doctor == null) {
                        result.errors.add("Doctor with ID " + doctorId + " not found");
                        result.errorCount++;
                        continue;
                    }
                    
                    Appointment appointment = appointmentManager.scheduleAppointment(
                        patient, doctor, date, startTime, endTime, reason
                    );
                    
                    if (appointment != null) {
                        if (record.length > 9 && !record[9].isEmpty()) {
                            try {
                                AppointmentStatus status = AppointmentStatus.valueOf(record[9]);
                                appointment.setStatus(status);
                            } catch (IllegalArgumentException e) {
                                // Keep default status
                            }
                        }
                        
                        if (record.length > 10 && !record[10].isEmpty()) {
                            appointment.setNotes(record[10]);
                        }
                        
                        result.successCount++;
                    } else {
                        result.errors.add("Failed to create appointment (possible conflict)");
                        result.errorCount++;
                    }
                } catch (Exception e) {
                    result.errors.add("Error importing appointment: " + e.getMessage());
                    result.errorCount++;
                }
            }
        } catch (IOException | CsvException e) {
            result.errors.add("Error reading CSV file: " + e.getMessage());
        }
        
        return result;
    }
}
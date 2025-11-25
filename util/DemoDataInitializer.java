package clinicapp.util;

import clinicapp.model.Patient;
import clinicapp.model.Doctor;
import clinicapp.service.PatientManager;
import clinicapp.service.DoctorManager;
import clinicapp.service.AppointmentManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * Initializes demo data for the clinic management system
 * Creates actual objects instead of just display rows
 */
public class DemoDataInitializer {

        public static void initializeDemoData(PatientManager patientManager,
                        DoctorManager doctorManager,
                        AppointmentManager appointmentManager) {
                // Create demo patients
                Patient patient1 = patientManager.addPatient(
                                "John Doe",
                                LocalDate.of(1992, 12, 14),
                                "Male",
                                "1234567890",
                                "john.doe@example.com",
                                "123 Main St",
                                "O+",
                                "None");

                Patient patient2 = patientManager.addPatient(
                                "Jane Smith",
                                LocalDate.of(1985, 5, 20),
                                "Female",
                                "0987654321",
                                "jane.smith@example.com",
                                "456 Oak Ave",
                                "A+",
                                "Penicillin");

                Patient patient3 = patientManager.addPatient(
                                "Bob Johnson",
                                LocalDate.of(1978, 8, 15),
                                "Male",
                                "5551234567",
                                "bob.j@example.com",
                                "789 Pine Rd",
                                "B+",
                                "None");

                // Create demo doctors
                Doctor doctor1 = doctorManager.addDoctor(
                                "Sarah Lee",
                                "Cardiologist",
                                "1112223333",
                                "dr.lee@clinic.com",
                                Arrays.asList("Monday", "Wednesday", "Friday"),
                                "09:00",
                                "17:00");

                Doctor doctor2 = doctorManager.addDoctor(
                                "Michael Kim",
                                "Dermatologist",
                                "4445556666",
                                "dr.kim@clinic.com",
                                Arrays.asList("Tuesday", "Thursday", "Saturday"),
                                "10:00",
                                "18:00");

                Doctor doctor3 = doctorManager.addDoctor(
                                "Emily Chen",
                                "Pediatrician",
                                "7778889999",
                                "dr.chen@clinic.com",
                                Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
                                "08:00",
                                "16:00");

                // Create demo appointments (all for today)
                LocalDate today = LocalDate.now();

                if (patient1 != null && doctor1 != null) {
                        appointmentManager.scheduleAppointment(
                                        patient1,
                                        doctor1,
                                        today,
                                        LocalTime.of(9, 0),
                                        LocalTime.of(10, 0),
                                        "Regular checkup");
                }

                if (patient2 != null && doctor2 != null) {
                        appointmentManager.scheduleAppointment(
                                        patient2,
                                        doctor2,
                                        today,
                                        LocalTime.of(10, 30),
                                        LocalTime.of(11, 30),
                                        "Skin consultation");
                }

                if (patient3 != null && doctor3 != null) {
                        appointmentManager.scheduleAppointment(
                                        patient3,
                                        doctor3,
                                        today,
                                        LocalTime.of(14, 0),
                                        LocalTime.of(15, 0),
                                        "Follow-up visit");
                }

                System.out.println("Demo data initialized successfully!");
                System.out.println("Patients: " + patientManager.getPatientCount());
                System.out.println("Doctors: " + doctorManager.getDoctorCount());
                System.out.println("Appointments: " + appointmentManager.getAllAppointments().size());
        }
}

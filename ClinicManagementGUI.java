package clinicapp;

import clinicapp.gui.LoginFrame;
import clinicapp.gui.MainFrame;
import clinicapp.service.*;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Main entry point for Clinic Management System
 * Now with modern FlatLaf theme and enhanced UI
 */
public class ClinicManagementGUI {
    public static void main(String[] args) {
        // Set FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme. Using default.");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Initialize managers
            PatientManager patientManager = new PatientManager();
            DoctorManager doctorManager = new DoctorManager();
            AppointmentManager appointmentManager = new AppointmentManager(patientManager, doctorManager);

            // Initialize demo data
            clinicapp.util.DemoDataInitializer.initializeDemoData(
                    patientManager, doctorManager, appointmentManager);

            // Show login frame
            LoginFrame loginFrame = new LoginFrame((username) -> {
                // On successful login, show main frame
                MainFrame mainFrame = new MainFrame(patientManager, doctorManager, appointmentManager, username);
                mainFrame.setVisible(true);
            });
            loginFrame.setVisible(true);
        });
    }
}
package clinicapp.gui;

import clinicapp.service.AppointmentManager;
import clinicapp.service.DoctorManager;
import clinicapp.service.PatientManager;

import javax.swing.*;

// Main window - displays the main application window 
public class MainWindow extends JFrame {
    private PatientManager patientManager;
    private DoctorManager doctorManager;
    private AppointmentManager appointmentManager;

    private JTabbedPane tabbedPane;
    private PatientPanel patientPanel;
    private DoctorPanel doctorPanel;
    private AppointmentPanel appointmentPanel;

    public MainWindow() {
        initializeManagers();
        initializeUI();
    }

    private void initializeManagers() {
        patientManager = new PatientManager();
        doctorManager = new DoctorManager();
        appointmentManager = new AppointmentManager(patientManager, doctorManager);
    }

    private void initializeUI() {
        setTitle("Clinic Appointment Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Use enhanced panels with tabbed interface
        patientPanel = new PatientPanel(patientManager);
        doctorPanel = new DoctorPanel(doctorManager);
        appointmentPanel = new AppointmentPanel(appointmentManager, patientManager, doctorManager);

        tabbedPane.addTab("Patients", new ImageIcon(), patientPanel, "Manage Patients");
        tabbedPane.addTab("Doctors", new ImageIcon(), doctorPanel, "Manage Doctors");
        tabbedPane.addTab("Appointments", new ImageIcon(), appointmentPanel, "Manage Appointments");

        add(tabbedPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
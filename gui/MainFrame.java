package clinicapp.gui;

import clinicapp.service.AppointmentManager;
import clinicapp.service.DoctorManager;
import clinicapp.service.PatientManager;
import javax.swing.*;
import java.awt.*;

/**
 * Main application frame with sidebar navigation
 * Replaces the old tabbed interface with a modern sidebar layout
 */
public class MainFrame extends JFrame {
    private PatientManager patientManager;
    private DoctorManager doctorManager;
    private AppointmentManager appointmentManager;
    private String currentUser;
    private SidebarPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    // Panel instances
    private DashboardPanel dashboardPanel;
    private PatientPanel patientPanel;
    private DoctorPanel doctorPanel;
    private AppointmentPanel appointmentPanel;
    private SchedulePanel schedulePanel;
    private WalkInQueuePanel walkInQueuePanel;

    public MainFrame(PatientManager patientManager, DoctorManager doctorManager,
            AppointmentManager appointmentManager, String username) {
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        this.appointmentManager = appointmentManager;
        this.currentUser = username;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        // Set app icon for window and taskbar
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/clinicapp/resources/icons/clinic-logo.png"));
        setIconImage(appIcon.getImage());
        
        // Main layout: Sidebar on left, content on right
        setLayout(new BorderLayout());
        // Initialize sidebar with navigation callback
        sidebarPanel = new SidebarPanel("Admin", currentUser, new SidebarPanel.NavigationCallback() {
            @Override
            public void onNavigate(String page) {
                switchToPage(page);
            }

            @Override
            public void onLogout() {
                handleLogout();
            }
        });
        // Initialize content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.GRAY_50);
        // Initialize all panels
        dashboardPanel = new DashboardPanel(new DashboardPanel.NavigationCallback() {
            @Override
            public void onNavigate(String page) {
                switchToPage(page);
            }
        }, appointmentManager, currentUser);

        patientPanel = new PatientPanel(patientManager);
        doctorPanel = new DoctorPanel(doctorManager);
        appointmentPanel = new AppointmentPanel(appointmentManager, patientManager, doctorManager);
        schedulePanel = new SchedulePanel(appointmentManager);
        walkInQueuePanel = new WalkInQueuePanel(appointmentManager, patientManager, doctorManager);

        // Update dashboard stats
        dashboardPanel.updateStats(
                patientManager.getAllPatients().size(),
                doctorManager.getAllDoctors().size(),
                appointmentManager.getAllAppointments().size());
        // Add panels to card layout
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(patientPanel, "patients");
        contentPanel.add(doctorPanel, "doctors");
        contentPanel.add(appointmentPanel, "appointments");
        contentPanel.add(schedulePanel, "schedule");
        contentPanel.add(walkInQueuePanel, "walkinqueue");
        // Add components to frame
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        // Show dashboard by default
        cardLayout.show(contentPanel, "dashboard");
    }

    private void switchToPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            // Close main frame
            dispose();
            // Show login frame again
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame((username) -> {
                    MainFrame mainFrame = new MainFrame(patientManager, doctorManager, appointmentManager, username);
                    mainFrame.setVisible(true);
                });
                loginFrame.setVisible(true);
            });
        }
    }

    /**
     * Navigate to a specific page programmatically
     */
    public void navigateTo(String page) {
        sidebarPanel.setCurrentPage(page);
        switchToPage(page);
    }
}
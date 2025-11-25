package clinicapp.gui;

import clinicapp.gui.components.*;
import clinicapp.model.Appointment;
import clinicapp.service.AppointmentManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard panel with statistics cards, horizontal quick actions, and
 * full-width appointment queue
 */
public class DashboardPanel extends JPanel {

    private int totalPatients = 0;
    private int totalDoctors = 0;
    private int todayAppointments = 0;
    private NavigationCallback navCallback;
    private AppointmentManager appointmentManager;
    private JPanel appointmentsContainer;
    private String userName;

    // Icon resources
    private static ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(SidebarPanel.class.getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    ImageIcon clockIcon = scaleIcon("/clinicapp/resources/icons/clock-16-1.png", 12, 12);
    ImageIcon dateIcon = scaleIcon("/clinicapp/resources/icons/date-16-1.png", 12, 12);

    public interface NavigationCallback {
        void onNavigate(String page);
    }

    public DashboardPanel(NavigationCallback callback, AppointmentManager appointmentManager, String userName) {
        this.navCallback = callback;
        this.appointmentManager = appointmentManager;
        this.userName = userName;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.GRAY_50);

        // Title Section - Full width, no padding
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(UIConstants.createCardBorder());

        JLabel titleLabel = StyledLabel.createHeading("Dashboard");
        JLabel subtitleLabel = StyledLabel
                .createSmall("Welcome Back, " + userName);

        JPanel titleContent = new JPanel();
        titleContent.setLayout(new BoxLayout(titleContent, BoxLayout.Y_AXIS));
        titleContent.setBackground(Color.WHITE);
        titleContent.add(titleLabel);
        titleContent.add(Box.createVerticalStrut(5));
        titleContent.add(subtitleLabel);

        titlePanel.add(titleContent, BorderLayout.CENTER);

        // Content wrapper with padding
        JPanel contentWrapper = new JPanel(new BorderLayout(20, 20));
        contentWrapper.setBackground(UIConstants.GRAY_50);
        contentWrapper.setBorder(UIConstants.createPaddingBorder(20));

        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(UIConstants.GRAY_50);

        JPanel patientsCard = createStatsCard("Total Patients", String.valueOf(totalPatients),
                UIConstants.PRIMARY_BLUE);
        JPanel doctorsCard = createStatsCard("Active Doctors", String.valueOf(totalDoctors), UIConstants.CYAN_500);
        JPanel appointmentsCard = createStatsCard("Today's Appointments", String.valueOf(todayAppointments),
                UIConstants.SUCCESS_GREEN);
        JPanel queueCard = createStatsCard("Queue Status", String.valueOf(getQueueCount()), UIConstants.PURPLE_600);

        statsPanel.add(patientsCard);
        statsPanel.add(doctorsCard);
        statsPanel.add(appointmentsCard);
        statsPanel.add(queueCard);



        // Appointment Queue - Full width
        JPanel queuePanel = createAppointmentQueuePanel();

        // Main content layout
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(UIConstants.GRAY_50);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(queuePanel, BorderLayout.SOUTH);

        contentWrapper.add(contentPanel, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);
    }

    private JPanel createAppointmentQueuePanel() {
        JPanel queuePanel = new JPanel(new BorderLayout(10, 10));
        queuePanel.setBackground(Color.WHITE);
        queuePanel.setBorder(UIConstants.createCardBorder());

        // Toolbar with title and buttons
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);

        JLabel queueTitle = StyledLabel.createSubheading("Appointment Queue (Today)");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        StyledButton refreshButton = StyledButton.createSuccess("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 30));
        refreshButton.addActionListener(e -> refreshQueue());

        StyledButton appointmentsBtn = StyledButton.createSuccess("View Schedule");
        appointmentsBtn.setPreferredSize(new Dimension(150, 30));
        appointmentsBtn.addActionListener(e -> {
            if (navCallback != null)
                navCallback.onNavigate("schedule");
        });

        buttonPanel.add(appointmentsBtn);
        buttonPanel.add(refreshButton);

        toolbar.add(queueTitle, BorderLayout.WEST);
        toolbar.add(buttonPanel, BorderLayout.EAST);

        // Appointments container (no scroll - only show recent)
        appointmentsContainer = new JPanel();
        appointmentsContainer.setLayout(new BoxLayout(appointmentsContainer, BoxLayout.Y_AXIS));
        appointmentsContainer.setBackground(Color.WHITE);

        queuePanel.add(toolbar, BorderLayout.NORTH);
        queuePanel.add(appointmentsContainer, BorderLayout.CENTER);

        // Initial load
        refreshQueue();

        return queuePanel;
    }

    private void refreshQueue() {
        appointmentsContainer.removeAll();

        if (appointmentManager == null) {
            showNoAppointmentsMessage("No appointment manager available");
            appointmentsContainer.revalidate();
            appointmentsContainer.repaint();
            return;
        }

        LocalDate today = LocalDate.now();
        List<Appointment> todayAppointments = appointmentManager.getAppointmentsByDate(today);

        if (todayAppointments.isEmpty()) {
            showNoAppointmentsMessage("No appointments scheduled for today");
        } else {
            // Only show the latest and recent upcoming appointments (limit to 3)
            int maxCards = Math.min(3, todayAppointments.size());
            for (int i = 0; i < maxCards; i++) {
                JPanel card = createAppointmentCard(todayAppointments.get(i));
                appointmentsContainer.add(card);
                if (i < maxCards - 1) {
                    appointmentsContainer.add(Box.createVerticalStrut(15));
                }
            }
        }

        appointmentsContainer.revalidate();
        appointmentsContainer.repaint();
    }

    private JPanel createAppointmentCard(Appointment appointment) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);

        // Get status color for left border
        Color statusColor = getStatusColor(appointment.getStatus().toString());

        // Create colored left border based on status
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, statusColor),
                BorderFactory.createCompoundBorder(
                        UIConstants.createCardBorder(),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20))));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Left section: Doctor info with date/time icons
        JPanel leftSection = createLeftSection(appointment);

        // Center section: Reason only
        JPanel centerSection = createCenterSection(appointment);

        // Right section: Action buttons
        JPanel rightSection = createRightSection(appointment);

        card.add(leftSection, BorderLayout.WEST);
        card.add(centerSection, BorderLayout.CENTER);
        card.add(rightSection, BorderLayout.EAST);

        return card;
    }

    private JPanel createLeftSection(Appointment appointment) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        String startTimeText = appointment.getStartTime().format(timeFormatter);
        String dateText = appointment.getAppointmentDate().format(dateFormatter);

        JLabel doctorLabel = new JLabel("Dr. " + appointment.getDoctor().getName());
        doctorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        doctorLabel.setForeground(UIConstants.GRAY_800);
        doctorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel specializationLabel = StyledLabel.createSmall(appointment.getDoctor().getSpecialization());
        specializationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Combined date and time panel (horizontal layout)
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateTimePanel.setBackground(Color.WHITE);
        dateTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Date with icon
        dateTimePanel.add(new JLabel(dateIcon));
        dateTimePanel.add(Box.createHorizontalStrut(4));
        JLabel dateLabelText = StyledLabel.createSmall(dateText);
        dateTimePanel.add(dateLabelText);
        dateTimePanel.add(Box.createHorizontalStrut(10));

        // Time with icon (right beside date)
        dateTimePanel.add(new JLabel(clockIcon));
        dateTimePanel.add(Box.createHorizontalStrut(4));
        JLabel timeLabelText = StyledLabel.createSmall(startTimeText);
        dateTimePanel.add(timeLabelText);

        leftPanel.add(doctorLabel);
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(specializationLabel);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(dateTimePanel);

        return leftPanel;
    }

    private JPanel createCenterSection(Appointment appointment) {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        JLabel reasonLabel = StyledLabel.createSmall("Reason: " + appointment.getReason());
        centerPanel.add(reasonLabel);

        return centerPanel;
    }

    private JPanel createRightSection(Appointment appointment) {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setBackground(Color.WHITE);

        // Status label with colored background
        String statusText = getStatusText(appointment.getStatus().toString());
        
        JLabel statusLabel = StyledLabel.createSmall(statusText);


        StyledButton viewButton = StyledButton.createPrimary("View Details");
        viewButton.setPreferredSize(new Dimension(120, 35));
        viewButton.addActionListener(e -> showAppointmentDetails(appointment));

        rightPanel.add(statusLabel);
        rightPanel.add(viewButton);

        return rightPanel;
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "SCHEDULED":
                return new Color(59, 130, 246);
            case "CONFIRMED":
                return UIConstants.SUCCESS_GREEN;
            case "IN_PROGRESS":
                return new Color(245, 158, 11);
            case "COMPLETED":
                return UIConstants.GRAY_400;
            case "CANCELLED":
                return UIConstants.DANGER_RED;
            case "NO_SHOW":
                return new Color(239, 68, 68);
            default:
                return UIConstants.GRAY_300;
        }
    }

    private String getStatusText(String status) {
        switch (status) {
            case "SCHEDULED":
                return "Scheduled";
            case "CONFIRMED":
                return "Confirmed";
            case "IN_PROGRESS":
                return "In Progress";
            case "COMPLETED":
                return "Completed";
            case "CANCELLED":
                return "Cancelled";
            case "NO_SHOW":
                return "No Show";
            default:
                return status;
        }
    }

    private void showNoAppointmentsMessage(String message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(UIConstants.FONT_LABEL);
        messageLabel.setForeground(UIConstants.GRAY_600);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messagePanel.add(Box.createVerticalStrut(30));
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(30));

        appointmentsContainer.add(messagePanel);
    }

    private void showAppointmentDetails(Appointment appointment) {
        String details = appointment.getDetailedInfo();
        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        textArea.setFont(UIConstants.FONT_BODY);
        textArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Appointment Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private int getQueueCount() {
        if (appointmentManager == null)
            return 0;
        LocalDate today = LocalDate.now();
        return appointmentManager.getAppointmentsByDate(today).size();
    }

    private JPanel createStatsCard(String title, String value, Color accentColor) {
        JPanel card = StyledPanel.createWithAccent(new BorderLayout(10, 10), accentColor);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(UIConstants.GRAY_800);

        JLabel titleLabel = StyledLabel.createSmall(title);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    public void updateStats(int patients, int doctors, int appointments) {
        this.totalPatients = patients;
        this.totalDoctors = doctors;
        this.todayAppointments = appointments;
        // Refresh the UI
        removeAll();
        initializeUI();
        revalidate();
        repaint();
    }
}

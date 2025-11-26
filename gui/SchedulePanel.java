package clinicapp.gui;

import clinicapp.gui.components.*;
import clinicapp.model.Appointment;
import clinicapp.service.AppointmentManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Dedicated Schedule Panel for viewing appointment schedules
public class SchedulePanel extends JPanel {

    private AppointmentManager appointmentManager;
    private LocalDate selectedDate;
    private JPanel appointmentsContainer;
    private JLabel dateLabel;
    private JButton todayButton;
    private JButton prevDayButton;
    private JButton nextDayButton;
    private JButton refreshButton;

    // Scales an icon to the specified dimensions
    private static ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(SidebarPanel.class.getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    ImageIcon sadIcon = new ImageIcon(getClass().getResource("/clinicapp/resources/icons/sad-2.png"));
    ImageIcon clockIcon = scaleIcon("/clinicapp/resources/icons/clock-16-1.png", 12, 12);
    ImageIcon dateIcon = scaleIcon("/clinicapp/resources/icons/date-16-1.png", 12, 12);
    
    // Constructor
    public SchedulePanel(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
        this.selectedDate = LocalDate.now();
        initializeUI();
        loadAppointments();
    }

    // Initializes the UI components
    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.GRAY_50);
        setBorder(UIConstants.createPaddingBorder(20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Date Navigation Panel
        JPanel dateNavPanel = createDateNavigationPanel();

        // Appointments Container (scrollable)
        appointmentsContainer = new JPanel();
        appointmentsContainer.setLayout(new BoxLayout(appointmentsContainer, BoxLayout.Y_AXIS));
        appointmentsContainer.setBackground(UIConstants.GRAY_50);

        JScrollPane scrollPane = new JScrollPane(appointmentsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Top section with header and date nav
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(UIConstants.GRAY_50);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(dateNavPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Creates the header panel
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(UIConstants.createCardBorder());

        JPanel titleContent = new JPanel();
        titleContent.setLayout(new BoxLayout(titleContent, BoxLayout.Y_AXIS));
        titleContent.setBackground(Color.WHITE);

        JLabel titleLabel = StyledLabel.createHeading("Appointment Schedule");
        JLabel subtitleLabel = StyledLabel.createSmall("View and manage daily appointment schedules");

        titleContent.add(titleLabel);
        titleContent.add(Box.createVerticalStrut(5));
        titleContent.add(subtitleLabel);

        headerPanel.add(titleContent, BorderLayout.CENTER);

        return headerPanel;
    }

    // Creates the date navigation panel
    private JPanel createDateNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout(15, 0));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(UIConstants.createCardBorder());

        // Date display
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dateLabel.setForeground(UIConstants.GRAY_800);
        updateDateLabel();

        // Navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        prevDayButton = StyledButton.createOutline("<- Previous Day");
        prevDayButton.setPreferredSize(new Dimension(140, 35)); 
        prevDayButton.addActionListener(e -> navigateDay(-1));

        todayButton = StyledButton.createPrimary("Today");
        todayButton.setPreferredSize(new Dimension(100, 35));
        todayButton.addActionListener(e -> navigateToToday());

        nextDayButton = StyledButton.createOutline("Next Day ->");
        nextDayButton.setPreferredSize(new Dimension(140, 35));
        nextDayButton.addActionListener(e -> navigateDay(1));

        refreshButton = StyledButton.createSuccess("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadAppointments());

        buttonPanel.add(prevDayButton);
        buttonPanel.add(todayButton);
        buttonPanel.add(nextDayButton);
        buttonPanel.add(refreshButton);

        JPanel dateLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        dateLabelPanel.setBackground(Color.WHITE);
        dateLabelPanel.add(dateLabel);

        navPanel.add(dateLabelPanel, BorderLayout.WEST);
        navPanel.add(buttonPanel, BorderLayout.EAST);

        return navPanel;
    }

    // Updates the date label
    private void updateDateLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String dateText = selectedDate.format(formatter);

        if (selectedDate.equals(LocalDate.now())) {
            dateLabel.setText(dateText + " (Today)");
        } else {
            dateLabel.setText(dateText);
        }
    }

    // Navigates to the previous or next day
    private void navigateDay(int days) {
        selectedDate = selectedDate.plusDays(days);
        updateDateLabel();
        loadAppointments();
    }

    // Navigates to today
    private void navigateToToday() {
        selectedDate = LocalDate.now();
        updateDateLabel();
        loadAppointments();
    }

    // Loads appointments for the selected date
    private void loadAppointments() {
        appointmentsContainer.removeAll();

        if (appointmentManager == null) {
            showNoAppointmentsMessage("No appointment manager available");
            return;
        }

        List<Appointment> appointments = appointmentManager.getAppointmentsByDate(selectedDate);

        if (appointments.isEmpty()) {
            showNoAppointmentsMessage("No appointments scheduled for this date");
        } else {
            for (Appointment apt : appointments) {
                JPanel card = createAppointmentCard(apt);
                appointmentsContainer.add(card);
                appointmentsContainer.add(Box.createVerticalStrut(15));
            }
        }

        appointmentsContainer.revalidate();
        appointmentsContainer.repaint();
    }

    // Creates an appointment card
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

    // Creates the left section of the appointment card
    private JPanel createLeftSection(Appointment appointment) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure alignment

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

    // Creates the center section of the appointment card
    private JPanel createCenterSection(Appointment appointment) {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Reason only (patient name removed)
        JLabel reasonLabel = StyledLabel.createSmall("Reason: " + appointment.getReason());

        centerPanel.add(reasonLabel);

        return centerPanel;
    }

    // Creates the right section of the appointment card
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

    // Returns the color based on the appointment status
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

    // Returns the text based on the appointment status
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

    // Shows a message when there are no appointments
    private void showNoAppointmentsMessage(String message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(UIConstants.createCardBorder());
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel iconLabel = new JLabel(sadIcon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(UIConstants.FONT_LABEL);
        messageLabel.setForeground(UIConstants.GRAY_600);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messagePanel.add(Box.createVerticalStrut(40));
        messagePanel.add(iconLabel);
        messagePanel.add(Box.createVerticalStrut(15));
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(40));

        appointmentsContainer.add(messagePanel);
    }

    // Shows appointment details in a dialog
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

    // Refreshes the data
    public void refreshData() {
        loadAppointments();
    }
}

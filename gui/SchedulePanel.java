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
 * Dedicated Schedule Panel for viewing appointment schedules
 * Features horizontal appointment cards inspired by modern UI design
 */
public class SchedulePanel extends JPanel {

    private AppointmentManager appointmentManager;
    private LocalDate selectedDate;
    private JPanel appointmentsContainer;
    private JLabel dateLabel;
    private JButton todayButton;
    private JButton prevDayButton;
    private JButton nextDayButton;
    private JButton refreshButton;

    public SchedulePanel(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
        this.selectedDate = LocalDate.now();
        initializeUI();
        loadAppointments();
    }

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

        prevDayButton = StyledButton.createOutline("← Previous Day");
        prevDayButton.setPreferredSize(new Dimension(140, 35));
        prevDayButton.addActionListener(e -> navigateDay(-1));

        todayButton = StyledButton.createPrimary("Today");
        todayButton.setPreferredSize(new Dimension(100, 35));
        todayButton.addActionListener(e -> navigateToToday());

        nextDayButton = StyledButton.createOutline("Next Day →");
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

    private void updateDateLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String dateText = selectedDate.format(formatter);

        if (selectedDate.equals(LocalDate.now())) {
            dateLabel.setText(dateText + " (Today)");
        } else {
            dateLabel.setText(dateText);
        }
    }

    private void navigateDay(int days) {
        selectedDate = selectedDate.plusDays(days);
        updateDateLabel();
        loadAppointments();
    }

    private void navigateToToday() {
        selectedDate = LocalDate.now();
        updateDateLabel();
        loadAppointments();
    }

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

    private JPanel createAppointmentCard(Appointment appointment) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                UIConstants.createCardBorder(),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Left section: Time and status indicator
        JPanel leftSection = createLeftSection(appointment);

        // Center section: Patient and doctor info
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
        leftPanel.setPreferredSize(new Dimension(120, 80));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeText = appointment.getStartTime().format(timeFormatter);

        JLabel timeLabel = new JLabel(timeText);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timeLabel.setForeground(UIConstants.PRIMARY_BLUE);

        JLabel durationLabel = StyledLabel.createSmall(
                appointment.getStartTime().format(timeFormatter) + " - " +
                        appointment.getEndTime().format(timeFormatter));

        // Status badge
        JLabel statusBadge = createStatusBadge(appointment.getStatus().toString());

        leftPanel.add(timeLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(durationLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(statusBadge);

        return leftPanel;
    }

    private JPanel createCenterSection(Appointment appointment) {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Doctor name
        JLabel doctorLabel = new JLabel("Dr. " + appointment.getDoctor().getName());
        doctorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        doctorLabel.setForeground(UIConstants.GRAY_800);

        // Specialization
        JLabel specializationLabel = StyledLabel.createSmall(appointment.getDoctor().getSpecialization());

        // Patient name
        JLabel patientLabel = new JLabel("Patient: " + appointment.getPatient().getName());
        patientLabel.setFont(UIConstants.FONT_BODY);
        patientLabel.setForeground(UIConstants.GRAY_700);

        // Reason
        JLabel reasonLabel = StyledLabel.createSmall("Reason: " + appointment.getReason());

        centerPanel.add(doctorLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(specializationLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(patientLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(reasonLabel);

        return centerPanel;
    }

    private JPanel createRightSection(Appointment appointment) {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setBackground(Color.WHITE);

        StyledButton viewButton = StyledButton.createPrimary("View Details");
        viewButton.setPreferredSize(new Dimension(120, 35));
        viewButton.addActionListener(e -> showAppointmentDetails(appointment));

        rightPanel.add(viewButton);

        return rightPanel;
    }

    private JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(status);
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        switch (status) {
            case "SCHEDULED":
                badge.setBackground(new Color(59, 130, 246));
                badge.setForeground(Color.WHITE);
                break;
            case "CONFIRMED":
                badge.setBackground(UIConstants.SUCCESS_GREEN);
                badge.setForeground(Color.WHITE);
                break;
            case "IN_PROGRESS":
                badge.setBackground(new Color(245, 158, 11));
                badge.setForeground(Color.WHITE);
                break;
            case "COMPLETED":
                badge.setBackground(UIConstants.GRAY_400);
                badge.setForeground(Color.WHITE);
                break;
            case "CANCELLED":
                badge.setBackground(UIConstants.DANGER_RED);
                badge.setForeground(Color.WHITE);
                break;
            case "NO_SHOW":
                badge.setBackground(new Color(239, 68, 68));
                badge.setForeground(Color.WHITE);
                break;
            default:
                badge.setBackground(UIConstants.GRAY_300);
                badge.setForeground(UIConstants.GRAY_700);
        }

        return badge;
    }

    private void showNoAppointmentsMessage(String message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(UIConstants.createCardBorder());
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel iconLabel = new JLabel("📅");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
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

    public void refreshData() {
        loadAppointments();
    }
}

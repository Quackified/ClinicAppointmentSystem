package clinicapp.gui;

import clinicapp.gui.components.*;
import clinicapp.model.Appointment;
import clinicapp.service.AppointmentManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private JTable queueTable;
    private DefaultTableModel queueTableModel;

    public interface NavigationCallback {
        void onNavigate(String page);
    }

    public DashboardPanel(NavigationCallback callback, AppointmentManager appointmentManager) {
        this.navCallback = callback;
        this.appointmentManager = appointmentManager;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.GRAY_50);
        setBorder(UIConstants.createPaddingBorder(20));

        // Title Section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(UIConstants.createCardBorder());

        JLabel titleLabel = StyledLabel.createHeading("Dashboard");
        JLabel subtitleLabel = StyledLabel
                .createSmall("Welcome to Pinnacle Medical Services - Clinic Management System");

        JPanel titleContent = new JPanel();
        titleContent.setLayout(new BoxLayout(titleContent, BoxLayout.Y_AXIS));
        titleContent.setBackground(Color.WHITE);
        titleContent.add(titleLabel);
        titleContent.add(Box.createVerticalStrut(5));
        titleContent.add(subtitleLabel);

        titlePanel.add(titleContent, BorderLayout.CENTER);

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

        // Quick Actions - Horizontal layout
        JPanel quickActionsPanel = createQuickActionsPanel();

        // Appointment Queue - Full width
        JPanel queuePanel = createAppointmentQueuePanel();

        // Main content layout
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(UIConstants.GRAY_50);
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        JPanel middleBottomPanel = new JPanel(new BorderLayout(0, 20));
        middleBottomPanel.setBackground(UIConstants.GRAY_50);
        middleBottomPanel.add(quickActionsPanel, BorderLayout.NORTH);
        middleBottomPanel.add(queuePanel, BorderLayout.CENTER);

        contentPanel.add(middleBottomPanel, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createQuickActionsPanel() {
        JPanel actionsPanel = new JPanel(new BorderLayout(10, 10));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(UIConstants.createCardBorder());

        JLabel actionsTitle = StyledLabel.createSubheading("Quick Actions");

        // Horizontal button layout
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        StyledButton patientsBtn = StyledButton.createPrimary("Manage Patients");
        patientsBtn.setPreferredSize(new Dimension(150, 40));
        patientsBtn.addActionListener(e -> {
            if (navCallback != null)
                navCallback.onNavigate("patients");
        });

        StyledButton doctorsBtn = new StyledButton("Manage Doctors", StyledButton.ButtonVariant.PRIMARY);
        doctorsBtn.setBackground(UIConstants.CYAN_500);
        doctorsBtn.setPreferredSize(new Dimension(150, 40));
        doctorsBtn.addActionListener(e -> {
            if (navCallback != null)
                navCallback.onNavigate("doctors");
        });

        StyledButton appointmentsBtn = StyledButton.createSuccess("View Schedule");
        appointmentsBtn.setPreferredSize(new Dimension(150, 40));
        appointmentsBtn.addActionListener(e -> {
            if (navCallback != null)
                navCallback.onNavigate("schedule");
        });

        buttonsPanel.add(patientsBtn);
        buttonsPanel.add(doctorsBtn);
        buttonsPanel.add(appointmentsBtn);

        JPanel actionsContent = new JPanel(new BorderLayout(10, 10));
        actionsContent.setBackground(Color.WHITE);
        actionsContent.add(actionsTitle, BorderLayout.NORTH);
        actionsContent.add(buttonsPanel, BorderLayout.CENTER);

        actionsPanel.add(actionsContent);

        return actionsPanel;
    }

    private JPanel createAppointmentQueuePanel() {
        JPanel queuePanel = new JPanel(new BorderLayout(10, 10));
        queuePanel.setBackground(Color.WHITE);
        queuePanel.setBorder(UIConstants.createCardBorder());

        // Toolbar with title and refresh button
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);

        JLabel queueTitle = StyledLabel.createSubheading("Appointment Queue (Today)");

        StyledButton refreshButton = StyledButton.createSuccess("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 30));
        refreshButton.addActionListener(e -> refreshQueue());

        toolbar.add(queueTitle, BorderLayout.WEST);
        toolbar.add(refreshButton, BorderLayout.EAST);

        // Queue table
        String[] columnNames = { "Queue#", "Time", "Doctor", "Status" };
        queueTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        queueTable = new JTable(queueTableModel);
        queueTable.setRowHeight(25);
        queueTable.setFont(UIConstants.FONT_BODY);
        queueTable.getTableHeader().setFont(UIConstants.FONT_LABEL);

        JScrollPane scrollPane = new JScrollPane(queueTable);

        queuePanel.add(toolbar, BorderLayout.NORTH);
        queuePanel.add(scrollPane, BorderLayout.CENTER);

        // Initial load
        refreshQueue();

        return queuePanel;
    }

    private void refreshQueue() {
        queueTableModel.setRowCount(0);

        if (appointmentManager == null)
            return;

        LocalDate today = LocalDate.now();
        List<Appointment> todayAppointments = appointmentManager.getAppointmentsByDate(today);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        int queueNumber = 1;
        for (Appointment apt : todayAppointments) {
            String timeSlot = apt.getStartTime().format(timeFormatter);
            String doctor = "Dr. " + apt.getDoctor().getName();
            String status = apt.getStatus().toString();

            Object[] row = {
                    "#" + queueNumber,
                    timeSlot,
                    doctor,
                    status
            };
            queueTableModel.addRow(row);
            queueNumber++;
        }
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

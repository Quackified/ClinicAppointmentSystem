package clinicapp.gui;

import clinicapp.gui.components.*;
import clinicapp.model.Patient;
import clinicapp.model.Appointment;
import clinicapp.service.PatientManager;
import clinicapp.service.AppointmentManager;
import clinicapp.service.DoctorManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

// GUI Panel for managing walk-in patient queue
// Uses existing AppointmentManager queue for waitlist management
public class WalkInQueuePanel extends JPanel {
    private AppointmentManager appointmentManager;
    private PatientManager patientManager;
    private DoctorManager doctorManager;
    private JTable queueTable;
    private DefaultTableModel tableModel;
    private JLabel queueSizeLabel;
    private JLabel statsLabel;
    private JComboBox<String> patientCombo;
    private JComboBox<String> doctorCombo;
    private JTextField reasonField;
    
    // Constructor
    public WalkInQueuePanel(AppointmentManager appointmentManager, PatientManager patientManager, DoctorManager doctorManager) {
        this.appointmentManager = appointmentManager;
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        initializeUI();
    }
    
    // Initialize UI components
    private void initializeUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIConstants.GRAY_50);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(UIConstants.GRAY_50);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Top stats panel
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Center: Split panel (left: add patient form, right: queue table)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerLocation(450);
        
        JPanel leftPanel = createAddPatientPanel();
        JPanel rightPanel = createQueueTablePanel();
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        // Bottom: Action buttons
        JPanel bottomPanel = createBottomPanel();
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Initial load
        refreshQueue();
    }
    
    // Create stats panel
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(UIConstants.GRAY_50);
        
        // Queue size card
        JPanel sizeCard = new JPanel(new BorderLayout(10, 10));
        sizeCard.setBackground(Color.WHITE);
        sizeCard.setBorder(BorderFactory.createCompoundBorder(
            UIConstants.createCardBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel sizeTitle = new JLabel("Patients Waiting");
        sizeTitle.setFont(UIConstants.FONT_LABEL);
        sizeTitle.setForeground(UIConstants.GRAY_600);
        
        queueSizeLabel = new JLabel("0");
        queueSizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        queueSizeLabel.setForeground(UIConstants.PRIMARY_BLUE);
        
        sizeCard.add(sizeTitle, BorderLayout.NORTH);
        sizeCard.add(queueSizeLabel, BorderLayout.CENTER);
        
        // Stats card
        JPanel statsCard = new JPanel(new BorderLayout(10, 10));
        statsCard.setBackground(Color.WHITE);
        statsCard.setBorder(BorderFactory.createCompoundBorder(
            UIConstants.createCardBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel statsTitle = new JLabel("Queue Statistics");
        statsTitle.setFont(UIConstants.FONT_LABEL);
        statsTitle.setForeground(UIConstants.GRAY_600);
        
        statsLabel = new JLabel("No patients in queue");
        statsLabel.setFont(UIConstants.FONT_BODY);
        statsLabel.setForeground(UIConstants.GRAY_700);
        
        statsCard.add(statsTitle, BorderLayout.NORTH);
        statsCard.add(statsLabel, BorderLayout.CENTER);
        
        panel.add(sizeCard);
        panel.add(statsCard);
        
        return panel;
    }
    
    // Create add patient panel
    private JPanel createAddPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(UIConstants.createCardBorder());
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Add Walk-in Patient");
        titleLabel.setFont(UIConstants.FONT_HEADING);
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Patient selection
        JLabel patientLabel = new JLabel("Select Patient: *");
        patientLabel.setFont(UIConstants.FONT_LABEL);
        
        patientCombo = new JComboBox<>();
        patientCombo.setPreferredSize(new Dimension(300, 35));
        loadPatients();
        
        // Doctor selection
        JLabel doctorLabel = new JLabel("Assign Doctor: *");
        doctorLabel.setFont(UIConstants.FONT_LABEL);
        
        doctorCombo = new JComboBox<>();
        doctorCombo.setPreferredSize(new Dimension(300, 35));
        loadDoctors();
        
        // Reason field
        JLabel reasonLabel = new JLabel("Reason for Visit: *");
        reasonLabel.setFont(UIConstants.FONT_LABEL);
        
        reasonField = new JTextField();
        reasonField.setPreferredSize(new Dimension(300, 35));
        reasonField.setFont(UIConstants.FONT_BODY);
        
        // Add to queue button
        StyledButton addButton = StyledButton.createPrimary("Add to Queue");
        addButton.setPreferredSize(new Dimension(300, 40));
        addButton.addActionListener(e -> addPatientToQueue());
        
        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(patientLabel, gbc);
        
        gbc.gridy = 1;
        formPanel.add(patientCombo, gbc);
        
        gbc.gridy = 2;
        formPanel.add(doctorLabel, gbc);
        
        gbc.gridy = 3;
        formPanel.add(doctorCombo, gbc);
        
        gbc.gridy = 4;
        formPanel.add(reasonLabel, gbc);
        
        gbc.gridy = 5;
        formPanel.add(reasonField, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(addButton, gbc);
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Info label
        JLabel infoLabel = new JLabel("<html><b>Queue System:</b> First-In-First-Out (FIFO)<br>" +
                                     "Patients are served in order of arrival.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(infoLabel);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 8, 8, 8);
        formPanel.add(infoPanel, gbc);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Create queue table panel
    private JPanel createQueueTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(UIConstants.createCardBorder());
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Current Queue");
        titleLabel.setFont(UIConstants.FONT_HEADING);
        titlePanel.add(titleLabel);
        
        // Table
        String[] columnNames = {"Position", "Queue #", "Patient Name", "Arrival Time", "Wait Time", "Reason"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        queueTable = new JTable(tableModel);
        queueTable.setRowHeight(35);
        queueTable.setFont(UIConstants.FONT_BODY);
        queueTable.getTableHeader().setFont(UIConstants.FONT_LABEL);
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        queueTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Position
        queueTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Queue #
        queueTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Name
        queueTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Arrival
        queueTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Wait Time
        queueTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Reason
        
        JScrollPane scrollPane = new JScrollPane(queueTable);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Create bottom panel
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(UIConstants.GRAY_50);
        
        StyledButton callNextButton = StyledButton.createSuccess("Call Next Patient");
        callNextButton.setPreferredSize(new Dimension(150, 35));
        callNextButton.addActionListener(e -> callNextPatient());
        
        JButton removeButton = new JButton("Remove from Queue");
        removeButton.setPreferredSize(new Dimension(150, 35));
        removeButton.addActionListener(e -> removeFromQueue());
        
        StyledButton refreshButton = StyledButton.createPrimary("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> refreshQueue());
        
        panel.add(callNextButton);
        panel.add(removeButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    // Load patients into combo box
    private void loadPatients() {
        patientCombo.removeAllItems();
        List<Patient> patients = patientManager.getAllPatients();
        
        for (Patient patient : patients) {
            patientCombo.addItem(patient.getId() + " - " + patient.getName());
        }
    }
    
    // Load doctors into combo box
    private void loadDoctors() {
        doctorCombo.removeAllItems();
        List<clinicapp.model.Doctor> doctors = doctorManager.getAvailableDoctors();
        
        for (clinicapp.model.Doctor doctor : doctors) {
            doctorCombo.addItem(doctor.getId() + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        }
    }
    
    // Add patient to queue
    private void addPatientToQueue() {
        if (patientCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patient", 
                "No Patient Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get reason for visit
        String reason = reasonField.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a reason for visit", 
                "Missing Information", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Extract patient ID
        String selected = (String) patientCombo.getSelectedItem();
        int patientId = Integer.parseInt(selected.split(" - ")[0]);
        Patient patient = patientManager.getPatientById(patientId);
        
        if (patient == null) {
            JOptionPane.showMessageDialog(this, 
                "Patient not found", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get selected doctor
        if (doctorCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a doctor", 
                "No Doctor Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedDoctor = (String) doctorCombo.getSelectedItem();
        int doctorId = Integer.parseInt(selectedDoctor.split(" - ")[0]);
        clinicapp.model.Doctor doctor = doctorManager.getDoctorById(doctorId);
        
        if (doctor == null) {
            JOptionPane.showMessageDialog(this, 
                "Doctor not found", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Schedule walk-in appointment for today using selected doctor
        Appointment appointment = appointmentManager.scheduleAppointment(
            patient, 
            doctor,
            java.time.LocalDate.now(),
            java.time.LocalTime.now(),
            java.time.LocalTime.now().plusMinutes(30),
            reason,
            true  // Mark as walk-in appointment
        );
        
        if (appointment != null) {
            // Add to walk-in queue instead of regular appointment queue
            appointmentManager.addToWalkInQueue(appointment);
            
            JOptionPane.showMessageDialog(this, 
                String.format("Patient added to walk-in queue!\nAppointment ID: %d\nQueue Position: %d", 
                    appointment.getId(), appointmentManager.getWalkInQueueSize()),
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            reasonField.setText("");
            
            // Refresh display
            refreshQueue();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to add patient to queue", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Call next patient
    private void callNextPatient() {
        if (appointmentManager.getWalkInQueueSize() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Queue is empty. No patients waiting.", 
                "Empty Queue", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Call next walk-in patient (FIFO - Queue operation)
        Appointment nextAppointment = appointmentManager.processNextWalkIn();
        
        if (nextAppointment != null) {
            String message = String.format(
                "Calling Next Patient:\n\n" +
                "Appointment ID: %d\n" +
                "Patient: %s\n" +
                "Reason: %s\n\n" +
                "Remaining in queue: %d",
                nextAppointment.getId(),
                nextAppointment.getPatient().getName(),
                nextAppointment.getReason(),
                appointmentManager.getWalkInQueueSize()
            );
            
            JOptionPane.showMessageDialog(this, 
                message, 
                "Patient Called", 
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshQueue();
        }
    }
    
    // Remove patient from queue
    private void removeFromQueue() {
        int selectedRow = queueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patient from the queue to remove", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove this patient from the queue?",
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentManager.cancelAppointment(appointmentId)) {
                JOptionPane.showMessageDialog(this, 
                    "Patient removed from queue successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshQueue();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to remove patient from queue", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Refresh queue
    private void refreshQueue() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all walk-in queue entries
        List<Appointment> queueEntries = appointmentManager.viewWalkInQueue();
        
        // Update stats
        queueSizeLabel.setText(String.valueOf(appointmentManager.getWalkInQueueSize()));
        if (appointmentManager.getWalkInQueueSize() == 0) {
            statsLabel.setText("No patients in queue");
        } else {
            statsLabel.setText(String.format("Queue Size: %d patients waiting", 
                appointmentManager.getWalkInQueueSize()));
        }
        
        // Populate table
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        int position = 1;
        
        for (Appointment appointment : queueEntries) {
            Object[] row = {
                position++,
                appointment.getId(),
                appointment.getPatient().getName(),
                appointment.getStartTime().format(timeFormatter),
                "N/A", // Wait time - would need to calculate
                appointment.getReason()
            };
            tableModel.addRow(row);
        }
    }
    
    // Public method to refresh the queue display
    // Can be called from other panels
    public void updateQueue() {
        refreshQueue();
    }
}

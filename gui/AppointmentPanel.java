package clinicapp.gui;

import clinicapp.gui.components.*;
import clinicapp.model.Appointment;
import clinicapp.model.Doctor;
import clinicapp.model.Patient;
import clinicapp.service.AppointmentManager;
import clinicapp.service.DoctorManager;
import clinicapp.service.PatientManager;
import clinicapp.util.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentPanel extends JPanel {
    private AppointmentManager appointmentManager;
    private PatientManager patientManager;
    private DoctorManager doctorManager;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> sortCombo;

    public AppointmentPanel(AppointmentManager appointmentManager,
            PatientManager patientManager,
            DoctorManager doctorManager) {
        this.appointmentManager = appointmentManager;
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIConstants.GRAY_50);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(UIConstants.GRAY_50);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top toolbar
        JPanel toolbarPanel = createToolbar();
        contentPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Center table
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = createBottomPanel();
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Initial load
        refreshTable();
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                UIConstants.createCardBorder(),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        // Left side - Sort
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sortPanel.setBackground(Color.WHITE);

        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(UIConstants.FONT_LABEL);

        sortCombo = new JComboBox<>(new String[] { "Time", "Doctor", "Patient" });
        sortCombo.setPreferredSize(new Dimension(120, 35));
        sortCombo.addActionListener(e -> refreshTable());

        sortPanel.add(sortLabel);
        sortPanel.add(sortCombo);

        // Right side - Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);

        StyledButton scheduleButton = StyledButton.createPrimary("Add Schedule");
        scheduleButton.setPreferredSize(new Dimension(120, 35));
        scheduleButton.addActionListener(e -> showScheduleDialog());

        StyledButton refreshButton = StyledButton.createSuccess("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> refreshTable());

        StyledButton reportButton = StyledButton.createPrimary("Daily Report");
        reportButton.setPreferredSize(new Dimension(120, 35));
        reportButton.addActionListener(e -> showDailyReportDialog());

        actionsPanel.add(scheduleButton);
        actionsPanel.add(refreshButton);
        actionsPanel.add(reportButton);

        toolbar.add(sortPanel, BorderLayout.WEST);
        toolbar.add(actionsPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(UIConstants.createCardBorder());

        // Table
        String[] columnNames = { "ID", "Date", "Time", "Patient", "Doctor", "Reason", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setRowHeight(30);
        appointmentTable.setFont(UIConstants.FONT_BODY);
        appointmentTable.getTableHeader().setFont(UIConstants.FONT_LABEL);

        // Add double-click to view details
        appointmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showAppointmentDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIConstants.GRAY_50);

        // Left side - Reschedule/Delete buttons
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftButtons.setBackground(UIConstants.GRAY_50);

        JButton rescheduleButton = new JButton("Reschedule");
        rescheduleButton.setPreferredSize(new Dimension(120, 35));
        rescheduleButton.addActionListener(e -> showUpdateDialog());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deleteAppointment());

        // Undo button - uses Stack
        JButton undoButton = new JButton("Undo Last");
        undoButton.setPreferredSize(new Dimension(100, 35));
        undoButton.addActionListener(e -> undoLastAction());

        // View History button
        JButton historyButton = new JButton("View History");
        historyButton.setPreferredSize(new Dimension(120, 35));
        historyButton.addActionListener(e -> showAppointmentHistory());

        leftButtons.add(rescheduleButton);
        leftButtons.add(deleteButton);
        leftButtons.add(undoButton);
        leftButtons.add(historyButton);

        // Right side - Status transition buttons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
        rightButtons.setBackground(UIConstants.GRAY_50);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setPreferredSize(new Dimension(90, 35));
        confirmButton.addActionListener(e -> confirmAppointment());

        JButton inProgressButton = new JButton("In Progress");
        inProgressButton.setPreferredSize(new Dimension(110, 35));
        inProgressButton.addActionListener(e -> markInProgress());

        JButton completeButton = new JButton("Complete");
        completeButton.setPreferredSize(new Dimension(100, 35));
        completeButton.addActionListener(e -> markCompleted());

        JButton noShowButton = new JButton("No Show");
        noShowButton.setPreferredSize(new Dimension(90, 35));
        noShowButton.addActionListener(e -> markNoShow());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.addActionListener(e -> cancelAppointment());

        rightButtons.add(confirmButton);
        rightButtons.add(inProgressButton);
        rightButtons.add(completeButton);
        rightButtons.add(noShowButton);
        rightButtons.add(cancelButton);
        panel.add(leftButtons, BorderLayout.WEST);
        panel.add(rightButtons, BorderLayout.EAST);

        return panel;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Appointment> appointments = appointmentManager.getAllAppointments();

        // Sort appointments based on selected criteria
        String sortBy = sortCombo != null ? (String) sortCombo.getSelectedItem() : "Time";
        sortAppointments(appointments, sortBy);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Appointment apt : appointments) {
            String timeSlot = apt.getStartTime().format(timeFormatter) + " - " +
                    apt.getEndTime().format(timeFormatter);

            Object[] row = {
                    apt.getId(),
                    apt.getAppointmentDate(),
                    timeSlot,
                    apt.getPatient().getName(),
                    "Dr. " + apt.getDoctor().getName(),
                    apt.getReason(),
                    apt.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void sortAppointments(List<Appointment> appointments, String sortBy) {
        switch (sortBy) {
            case "Time":
                appointments.sort((a1, a2) -> {
                    int dateCompare = a1.getAppointmentDate().compareTo(a2.getAppointmentDate());
                    if (dateCompare != 0)
                        return dateCompare;
                    return a1.getStartTime().compareTo(a2.getStartTime());
                });
                break;
            case "Doctor":
                appointments.sort((a1, a2) -> {
                    int doctorCompare = a1.getDoctor().getName().compareToIgnoreCase(a2.getDoctor().getName());
                    if (doctorCompare != 0)
                        return doctorCompare;
                    int dateCompare = a1.getAppointmentDate().compareTo(a2.getAppointmentDate());
                    if (dateCompare != 0)
                        return dateCompare;
                    return a1.getStartTime().compareTo(a2.getStartTime());
                });
                break;
            case "Patient":
                appointments.sort((a1, a2) -> {
                    int patientCompare = a1.getPatient().getName().compareToIgnoreCase(a2.getPatient().getName());
                    if (patientCompare != 0)
                        return patientCompare;
                    int dateCompare = a1.getAppointmentDate().compareTo(a2.getAppointmentDate());
                    if (dateCompare != 0)
                        return dateCompare;
                    return a1.getStartTime().compareTo(a2.getStartTime());
                });
                break;
        }
    }

    private void showScheduleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule Appointment", true);
        dialog.setSize(500, 450);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient selection
        JComboBox<String> patientCombo = new JComboBox<>();
        List<Patient> patients = patientManager.getAllPatients();
        for (Patient patient : patients) {
            patientCombo.addItem(patient.getId() + " - " + patient.getName());
        }

        // Doctor selection
        JComboBox<String> doctorCombo = new JComboBox<>();
        List<Doctor> doctors = doctorManager.getAvailableDoctors();
        for (Doctor doctor : doctors) {
            doctorCombo
                    .addItem(doctor.getId() + " - Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        }

        DatePicker datePicker = new DatePicker();
        TimePicker startTimePicker = new TimePicker();
        TimePicker endTimePicker = new TimePicker();
        JTextField reasonField = new JTextField(20);

        int row = 0;
        addFormField(formPanel, gbc, row++, "Patient: *", patientCombo);
        addFormField(formPanel, gbc, row++, "Doctor: *", doctorCombo);
        addFormField(formPanel, gbc, row++, "Date: *", datePicker);
        addFormField(formPanel, gbc, row++, "Start Time: *", startTimePicker);
        addFormField(formPanel, gbc, row++, "End Time: *", endTimePicker);
        addFormField(formPanel, gbc, row++, "Reason: *", reasonField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton saveButton = StyledButton.createPrimary("Create");
        saveButton.setPreferredSize(new Dimension(90, 30));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(90, 30));

        saveButton.addActionListener(e -> {
            if (patientCombo.getSelectedItem() == null || doctorCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Please select patient and doctor",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String dateStr = datePicker.getDateString();
            String startTimeStr = startTimePicker.getTimeString();
            String endTimeStr = endTimePicker.getTimeString();
            String reason = reasonField.getText().trim();

            LocalDate date = InputValidator.parseAndValidateDate(dateStr);
            if (date == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalTime startTime = InputValidator.parseAndValidateTime(startTimeStr);
            if (startTime == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid start time format. Use HH:mm", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalTime endTime = InputValidator.parseAndValidateTime(endTimeStr);
            if (endTime == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid end time format. Use HH:mm", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!endTime.isAfter(startTime)) {
                JOptionPane.showMessageDialog(dialog, "End time must be after start time", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int patientId = Integer.parseInt(((String) patientCombo.getSelectedItem()).split(" - ")[0]);
            int doctorId = Integer.parseInt(((String) doctorCombo.getSelectedItem()).split(" - ")[0]);

            Patient patient = patientManager.getPatientById(patientId);
            Doctor doctor = doctorManager.getDoctorById(doctorId);

            Appointment appointment = appointmentManager.scheduleAppointment(
                    patient, doctor, date, startTime, endTime, reason);

            if (appointment != null) {
                JOptionPane.showMessageDialog(dialog, "Appointment scheduled successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to schedule appointment. Doctor or patient may have a scheduling conflict.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void showAppointmentDetails() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to view",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Appointment appointment = appointmentManager.getAppointmentById(appointmentId);

        if (appointment != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String details = String.format(
                    "Appointment ID: %d\n" +
                            "Patient: %s\n" +
                            "Doctor: Dr. %s\n" +
                            "Date: %s\n" +
                            "Time: %s - %s\n" +
                            "Reason: %s\n" +
                            "Status: %s",
                    appointment.getId(),
                    appointment.getPatient().getName(),
                    appointment.getDoctor().getName(),
                    appointment.getAppointmentDate(),
                    appointment.getStartTime().format(timeFormatter),
                    appointment.getEndTime().format(timeFormatter),
                    appointment.getReason(),
                    appointment.getStatus());

            JOptionPane.showMessageDialog(this, details, "Appointment Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showUpdateDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
        if (appointment == null)
            return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reschedule Appointment", true);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        DatePicker datePicker = new DatePicker();
        datePicker.setDate(appointment.getAppointmentDate().toString());
        TimePicker startTimePicker = new TimePicker();
        startTimePicker.setTime(appointment.getStartTime().toString());
        TimePicker endTimePicker = new TimePicker();
        endTimePicker.setTime(appointment.getEndTime().toString());

        int row = 0;
        addFormField(formPanel, gbc, row++, "New Date: *", datePicker);
        addFormField(formPanel, gbc, row++, "New Start Time: *", startTimePicker);
        addFormField(formPanel, gbc, row++, "New End Time: *", endTimePicker);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton saveButton = StyledButton.createSuccess("Update");
        JButton cancelBtn = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String dateStr = datePicker.getDateString();
            String startTimeStr = startTimePicker.getTimeString();
            String endTimeStr = endTimePicker.getTimeString();

            LocalDate newDate = InputValidator.parseAndValidateDate(dateStr);
            LocalTime newStartTime = InputValidator.parseAndValidateTime(startTimeStr);
            LocalTime newEndTime = InputValidator.parseAndValidateTime(endTimeStr);

            if (newDate == null || newStartTime == null || newEndTime == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (appointmentManager.updateAppointment(appointmentId, newDate, newStartTime, newEndTime, null, null)) {
                JOptionPane.showMessageDialog(dialog, "Appointment updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update appointment",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void confirmAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to confirm",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        if (appointmentManager.confirmAppointment(appointmentId)) {
            JOptionPane.showMessageDialog(this, "Appointment confirmed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to confirm appointment",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markInProgress() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        if (appointmentManager.markInProgress(appointmentId)) {
            JOptionPane.showMessageDialog(this, "Appointment marked as IN_PROGRESS!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update appointment status",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markCompleted() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        if (appointmentManager.markCompleted(appointmentId)) {
            JOptionPane.showMessageDialog(this, "Appointment marked as COMPLETED!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update appointment status",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markNoShow() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        if (appointmentManager.markNoShow(appointmentId)) {
            JOptionPane.showMessageDialog(this, "Appointment marked as NO_SHOW!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update appointment status",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this appointment?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentManager.cancelAppointment(appointmentId)) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this appointment?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentManager.deleteAppointment(appointmentId)) {
                JOptionPane.showMessageDialog(this, "Appointment deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete appointment",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Undo last appointment action using Stack
     */
    private void undoLastAction() {
        if (!appointmentManager.canUndo()) {
            JOptionPane.showMessageDialog(this, "There's no history to undo", "Undo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to undo the last action?",
                "Confirm Undo", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentManager.undoLastAction()) {
                JOptionPane.showMessageDialog(this, "Last action undone successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }

    /**
     * Show appointment history dialog
     */
    private void showAppointmentHistory() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Appointment History", true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Date range
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        datePanel.add(new JLabel("From:"));
        DatePicker startPicker = new DatePicker();
        startPicker.setDate(LocalDate.now().minusMonths(1).toString());
        datePanel.add(startPicker);
        datePanel.add(new JLabel("To:"));
        DatePicker endPicker = new DatePicker();
        endPicker.setDate(LocalDate.now().toString());
        datePanel.add(endPicker);
        mainPanel.add(datePanel, BorderLayout.NORTH);

        // Table
        String[] cols = { "ID", "Date", "Time", "Patient", "Doctor", "Status", "Reason" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(UIConstants.FONT_BODY);
        table.getTableHeader().setFont(UIConstants.FONT_LABEL);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data
        List<Appointment> history = appointmentManager.getAppointmentHistory(LocalDate.now().minusMonths(1),
                LocalDate.now());
        
        // Filter for historical statuses only
        history = history.stream()
            .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.COMPLETED ||
                apt.getStatus() == Appointment.AppointmentStatus.CANCELLED ||
                apt.getStatus() == Appointment.AppointmentStatus.NO_SHOW)
            .collect(java.util.stream.Collectors.toList());
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        for (Appointment apt : history) {
            model.addRow(new Object[] { apt.getId(), apt.getAppointmentDate(),
                    apt.getStartTime().format(fmt) + "-" + apt.getEndTime().format(fmt),
                    apt.getPatient().getName(), "Dr. " + apt.getDoctor().getName(),
                    apt.getStatus(), apt.getReason() });
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * Generate and display daily report of appointments
     */
    private void showDailyReportDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Daily Appointment Report", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Date selection
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        datePanel.add(new JLabel("Select Date:"));
        DatePicker datePicker = new DatePicker();
        datePicker.setDate(LocalDate.now().toString());
        datePanel.add(datePicker);

        StyledButton generateButton = StyledButton.createPrimary("Generate Report");
        generateButton.addActionListener(e -> {
            LocalDate selectedDate = InputValidator.parseAndValidateDate(datePicker.getDateString());
            if (selectedDate != null) {
                updateDailyReport(dialog, selectedDate);
            }
        });
        datePanel.add(generateButton);

        mainPanel.add(datePanel, BorderLayout.NORTH);

        // Report content area
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Generate initial report for today
        generateDailyReport(reportArea, LocalDate.now());

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton exportButton = new JButton("Export to File");
        exportButton.addActionListener(e -> {
            exportDailyReport(reportArea.getText(), datePicker.getDateString());
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void updateDailyReport(JDialog dialog, LocalDate date) {
        // Find the text area in the dialog
        JTextArea reportArea = findTextAreaInContainer(dialog.getContentPane());
        if (reportArea != null) {
            generateDailyReport(reportArea, date);
        }
    }

    private void generateDailyReport(JTextArea reportArea, LocalDate date) {
        List<Appointment> appointments = appointmentManager.getAppointmentsByDate(date);

        StringBuilder report = new StringBuilder();
        report.append("DAILY APPOINTMENT REPORT\n");
        report.append("Date: ").append(date).append("\n");
        report.append("Generated: ").append(LocalDate.now()).append(" at ")
                .append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n\n");

        if (appointments.isEmpty()) {
            report.append("No appointments scheduled for this date.\n");
        } else {
            report.append("Total Appointments: ").append(appointments.size()).append("\n\n");

            // Group by status
            var statusCount = appointments.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Appointment::getStatus, 
                    java.util.stream.Collectors.counting()));

            report.append("Summary by Status:\n");
            statusCount.forEach((status, count) -> report.append(String.format("  %s: %d\n", status, count)));
            report.append("\n");

            // Detailed appointments
            report.append("Detailed Appointments:\n\n");

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            for (int i = 0; i < appointments.size(); i++) {
                Appointment apt = appointments.get(i);
                report.append(String.format("%d. Appointment ID: %d\n", i + 1, apt.getId()));
                report.append(String.format("   Time: %s - %s\n",
                        apt.getStartTime().format(timeFormatter),
                        apt.getEndTime().format(timeFormatter)));
                report.append(String.format("   Patient: %s (ID: %d)\n",
                        apt.getPatient().getName(), apt.getPatient().getId()));
                report.append(String.format("   Doctor: Dr. %s (%s)\n",
                        apt.getDoctor().getName(), apt.getDoctor().getSpecialization()));
                report.append(String.format("   Reason: %s\n", apt.getReason()));
                report.append(String.format("   Status: %s\n\n", apt.getStatus()));
            }

            report.append("END OF REPORT\n");
        }

        reportArea.setText(report.toString());
        reportArea.setCaretPosition(0);
    }

    private void exportDailyReport(String reportContent, String dateStr) {
        try {
            String filename = "daily_report_" + dateStr + ".txt";
            java.nio.file.Path exportPath = java.nio.file.Paths.get("exports", filename);

            // Create exports directory if it doesn't exist
            java.nio.file.Files.createDirectories(exportPath.getParent());

            // Write report to file
            java.nio.file.Files.writeString(exportPath, reportContent);

            JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + exportPath.toAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to export report: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextArea findTextAreaInContainer(java.awt.Container container) {
        for (java.awt.Component comp : container.getComponents()) {
            if (comp instanceof JScrollPane) {
                java.awt.Component view = ((JScrollPane) comp).getViewport().getView();
                if (view instanceof JTextArea) {
                    return (JTextArea) view;
                }
            } else if (comp instanceof java.awt.Container) {
                JTextArea textArea = findTextAreaInContainer((java.awt.Container) comp);
                if (textArea != null)
                    return textArea;
            }
        }
        return null;
    }
}
package clinicapp.gui;

import clinicapp.io.CsvExporter;
import clinicapp.io.CsvImporter;
import clinicapp.model.Appointment;
import clinicapp.model.Appointment.AppointmentStatus;
import clinicapp.model.Doctor;
import clinicapp.model.Patient;
import clinicapp.service.AppointmentManager;
import clinicapp.service.DoctorManager;
import clinicapp.service.PatientManager;
import clinicapp.util.InputValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
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
    
    public AppointmentPanel(AppointmentManager appointmentManager, 
                          PatientManager patientManager, 
                          DoctorManager doctorManager) {
        this.appointmentManager = appointmentManager;
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        initializeUI();
        refreshTable();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        String[] columnNames = {"ID", "Date", "Start Time", "End Time", "Patient", "Doctor", "Reason", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton scheduleButton = new JButton("Schedule Appointment");
        JButton viewButton = new JButton("View Details");
        JButton updateButton = new JButton("Update");
        JButton confirmButton = new JButton("Confirm");
        JButton completeButton = new JButton("Complete");
        JButton cancelButton = new JButton("Cancel Appointment");
        JButton exportButton = new JButton("Export to CSV");
        JButton importButton = new JButton("Import from CSV");
        JButton refreshButton = new JButton("Refresh");
        
        scheduleButton.addActionListener(e -> showScheduleDialog());
        viewButton.addActionListener(e -> showAppointmentDetails());
        updateButton.addActionListener(e -> showUpdateDialog());
        confirmButton.addActionListener(e -> confirmAppointment());
        completeButton.addActionListener(e -> completeAppointment());
        cancelButton.addActionListener(e -> cancelAppointment());
        exportButton.addActionListener(e -> exportAppointments());
        importButton.addActionListener(e -> importAppointments());
        refreshButton.addActionListener(e -> refreshTable());
        
        buttonPanel.add(scheduleButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(refreshButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Appointment> appointments = appointmentManager.getAllAppointments();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Appointment apt : appointments) {
            Object[] row = {
                apt.getId(),
                apt.getAppointmentDate(),
                apt.getStartTime().format(timeFormatter),
                apt.getEndTime().format(timeFormatter),
                apt.getPatient().getName(),
                "Dr. " + apt.getDoctor().getName(),
                apt.getReason(),
                apt.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showScheduleDialog() {
        List<Patient> patients = patientManager.getAllPatients();
        List<Doctor> doctors = doctorManager.getAvailableDoctors();
        
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients available. Please add patients first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (doctors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available doctors. Please add doctors first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule Appointment", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        
        JComboBox<String> patientCombo = new JComboBox<>();
        for (Patient p : patients) {
            patientCombo.addItem(p.getId() + " - " + p.getName());
        }
        
        JComboBox<String> doctorCombo = new JComboBox<>();
        for (Doctor d : doctors) {
            doctorCombo.addItem(d.getId() + " - Dr. " + d.getName() + " (" + d.getSpecialization() + ")");
        }
        
        JTextField dateField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField endTimeField = new JTextField();
        JTextField reasonField = new JTextField();
        
        dialog.add(new JLabel("Patient:"));
        dialog.add(patientCombo);
        dialog.add(new JLabel("Doctor:"));
        dialog.add(doctorCombo);
        dialog.add(new JLabel("Date (yyyy-MM-dd):"));
        dialog.add(dateField);
        dialog.add(new JLabel("Start Time (HH:mm):"));
        dialog.add(startTimeField);
        dialog.add(new JLabel("End Time (HH:mm):"));
        dialog.add(endTimeField);
        dialog.add(new JLabel("Reason:"));
        dialog.add(reasonField);
        
        JButton saveButton = new JButton("Schedule");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String patientSelection = (String) patientCombo.getSelectedItem();
            String doctorSelection = (String) doctorCombo.getSelectedItem();
            String dateStr = dateField.getText().trim();
            String startTimeStr = startTimeField.getText().trim();
            String endTimeStr = endTimeField.getText().trim();
            String reason = reasonField.getText().trim();
            
            if (!InputValidator.isValidString(reason)) {
                JOptionPane.showMessageDialog(dialog, "Reason is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate date = InputValidator.parseAndValidateDate(dateStr);
            if (date == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalTime startTime = InputValidator.parseAndValidateTime(startTimeStr);
            if (startTime == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid start time format. Use HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalTime endTime = InputValidator.parseAndValidateTime(endTimeStr);
            if (endTime == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid end time format. Use HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!endTime.isAfter(startTime)) {
                JOptionPane.showMessageDialog(dialog, "End time must be after start time", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int patientId = Integer.parseInt(patientSelection.split(" - ")[0]);
            int doctorId = Integer.parseInt(doctorSelection.split(" - ")[0]);
            
            Patient patient = patientManager.getPatientById(patientId);
            Doctor doctor = doctorManager.getDoctorById(doctorId);
            
            Appointment appointment = appointmentManager.scheduleAppointment(
                patient, doctor, date, startTime, endTime, reason
            );
            
            if (appointment != null) {
                JOptionPane.showMessageDialog(dialog, "Appointment scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to schedule appointment. Doctor may have a conflict.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }
    
    private void showAppointmentDetails() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
        
        if (appointment != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String details = String.format(
                "ID: %d\nDate: %s\nTime: %s - %s\nPatient: %s (ID: %d)\nDoctor: Dr. %s (ID: %d)\nSpecialization: %s\nReason: %s\nStatus: %s\nNotes: %s",
                appointment.getId(),
                appointment.getAppointmentDate(),
                appointment.getStartTime().format(timeFormatter),
                appointment.getEndTime().format(timeFormatter),
                appointment.getPatient().getName(),
                appointment.getPatient().getId(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getSpecialization(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getNotes() != null && !appointment.getNotes().isEmpty() ? appointment.getNotes() : "N/A"
            );
            JOptionPane.showMessageDialog(this, details, "Appointment Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showUpdateDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
        
        if (appointment == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Appointment", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        JTextField dateField = new JTextField(appointment.getAppointmentDate().toString());
        JTextField startTimeField = new JTextField(appointment.getStartTime().format(timeFormatter));
        JTextField endTimeField = new JTextField(appointment.getEndTime().format(timeFormatter));
        JTextField reasonField = new JTextField(appointment.getReason());
        JTextArea notesArea = new JTextArea(appointment.getNotes() != null ? appointment.getNotes() : "");
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        
        dialog.add(new JLabel("Date (yyyy-MM-dd):"));
        dialog.add(dateField);
        dialog.add(new JLabel("Start Time (HH:mm):"));
        dialog.add(startTimeField);
        dialog.add(new JLabel("End Time (HH:mm):"));
        dialog.add(endTimeField);
        dialog.add(new JLabel("Reason:"));
        dialog.add(reasonField);
        dialog.add(new JLabel("Notes:"));
        dialog.add(notesScroll);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String dateStr = dateField.getText().trim();
            String startTimeStr = startTimeField.getText().trim();
            String endTimeStr = endTimeField.getText().trim();
            String reason = reasonField.getText().trim();
            String notes = notesArea.getText().trim();
            
            LocalDate date = InputValidator.parseAndValidateDate(dateStr);
            if (date == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalTime startTime = InputValidator.parseAndValidateTime(startTimeStr);
            if (startTime == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid start time format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalTime endTime = InputValidator.parseAndValidateTime(endTimeStr);
            if (endTime == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid end time format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!endTime.isAfter(startTime)) {
                JOptionPane.showMessageDialog(dialog, "End time must be after start time", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (appointmentManager.updateAppointment(appointmentId, date, startTime, endTime, reason, notes)) {
                JOptionPane.showMessageDialog(dialog, "Appointment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update appointment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }
    
    private void confirmAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        
        if (appointmentManager.confirmAppointment(appointmentId)) {
            JOptionPane.showMessageDialog(this, "Appointment confirmed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to confirm appointment", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completeAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        
        String notes = JOptionPane.showInputDialog(this, "Enter completion notes (optional):");
        
        if (appointmentManager.completeAppointment(appointmentId, notes)) {
            JOptionPane.showMessageDialog(this, "Appointment completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to complete appointment", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this appointment?", 
            "Confirm Cancel", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentManager.cancelAppointment(appointmentId)) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportAppointments() {
        List<Appointment> appointments = appointmentManager.getAllAppointments();
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments to export", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            String fileName = CsvExporter.exportAppointments(appointments);
            JOptionPane.showMessageDialog(this, 
                "Appointments exported successfully!\nFile: " + fileName, 
                "Export Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to export appointments: " + e.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void importAppointments() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV file to import");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            CsvImporter.ImportResult importResult = CsvImporter.importAppointments(
                selectedFile.getAbsolutePath(), appointmentManager, patientManager, doctorManager
            );
            
            StringBuilder message = new StringBuilder();
            message.append("Import completed!\n");
            message.append("Success: ").append(importResult.successCount).append("\n");
            message.append("Errors: ").append(importResult.errorCount).append("\n");
            
            if (!importResult.errors.isEmpty()) {
                message.append("\nError details:\n");
                for (String error : importResult.errors) {
                    message.append("- ").append(error).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(this, message.toString(), 
                "Import Results", 
                importResult.errorCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
            
            refreshTable();
        }
    }
}
package clinicapp.gui;

import clinicapp.io.CsvExporter;
import clinicapp.io.CsvImporter;
import clinicapp.model.Doctor;
import clinicapp.service.DoctorManager;
import clinicapp.util.InputValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DoctorPanel extends JPanel {
    private DoctorManager doctorManager;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    
    public DoctorPanel(DoctorManager doctorManager) {
        this.doctorManager = doctorManager;
        initializeUI();
        refreshTable();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        String[] columnNames = {"ID", "Name", "Specialization", "Phone", "Email", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Doctor");
        JButton viewButton = new JButton("View Details");
        JButton updateButton = new JButton("Update Doctor");
        JButton deleteButton = new JButton("Delete Doctor");
        JButton toggleAvailButton = new JButton("Toggle Availability");
        JButton exportButton = new JButton("Export to CSV");
        JButton importButton = new JButton("Import from CSV");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(e -> showAddDoctorDialog());
        viewButton.addActionListener(e -> showDoctorDetails());
        updateButton.addActionListener(e -> showUpdateDoctorDialog());
        deleteButton.addActionListener(e -> deleteDoctor());
        toggleAvailButton.addActionListener(e -> toggleAvailability());
        exportButton.addActionListener(e -> exportDoctors());
        importButton.addActionListener(e -> importDoctors());
        refreshButton.addActionListener(e -> refreshTable());
        
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(toggleAvailButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(refreshButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Doctor> doctors = doctorManager.getAllDoctors();
        for (Doctor doctor : doctors) {
            Object[] row = {
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getPhoneNumber(),
                doctor.getEmail() != null ? doctor.getEmail() : "",
                doctor.isAvailable() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Doctor", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        
        JTextField nameField = new JTextField();
        JTextField specializationField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Specialization:"));
        dialog.add(specializationField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email (optional):"));
        dialog.add(emailField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String specialization = specializationField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            
            if (!InputValidator.isValidString(name)) {
                JOptionPane.showMessageDialog(dialog, "Name is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!InputValidator.isValidString(specialization)) {
                JOptionPane.showMessageDialog(dialog, "Specialization is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!InputValidator.isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(dialog, "Invalid phone number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!email.isEmpty() && !InputValidator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Doctor doctor = doctorManager.addDoctor(
                name, 
                specialization, 
                phone, 
                email.isEmpty() ? null : email,
                null,
                null,
                null
            );
            
            if (doctor != null) {
                JOptionPane.showMessageDialog(dialog, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add doctor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }
    
    private void showDoctorDetails() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctor = doctorManager.getDoctorById(doctorId);
        
        if (doctor != null) {
            String details = String.format(
                "ID: %d\nName: Dr. %s\nSpecialization: %s\nPhone: %s\nEmail: %s\nAvailable: %s",
                doctor.getId(), doctor.getName(), doctor.getSpecialization(), 
                doctor.getPhoneNumber(),
                doctor.getEmail() != null ? doctor.getEmail() : "N/A",
                doctor.isAvailable() ? "Yes" : "No"
            );
            JOptionPane.showMessageDialog(this, details, "Doctor Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showUpdateDoctorDialog() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctor = doctorManager.getDoctorById(doctorId);
        
        if (doctor == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Doctor", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);
        
        JTextField specializationField = new JTextField(doctor.getSpecialization());
        JTextField phoneField = new JTextField(doctor.getPhoneNumber());
        JTextField emailField = new JTextField(doctor.getEmail() != null ? doctor.getEmail() : "");
        
        dialog.add(new JLabel("Specialization:"));
        dialog.add(specializationField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email (optional):"));
        dialog.add(emailField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String specialization = specializationField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            
            if (!InputValidator.isValidString(specialization)) {
                JOptionPane.showMessageDialog(dialog, "Specialization is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!InputValidator.isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(dialog, "Invalid phone number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!email.isEmpty() && !InputValidator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (doctorManager.updateDoctor(doctorId, null, specialization, phone, email.isEmpty() ? null : email, null, null, null)) {
                JOptionPane.showMessageDialog(dialog, "Doctor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update doctor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }
    
    private void deleteDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this doctor?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorManager.deleteDoctor(doctorId)) {
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete doctor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void toggleAvailability() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctor = doctorManager.getDoctorById(doctorId);
        
        if (doctor != null) {
            doctorManager.setDoctorAvailability(doctorId, !doctor.isAvailable());
            JOptionPane.showMessageDialog(this, 
                "Doctor availability updated to: " + (doctor.isAvailable() ? "Available" : "Unavailable"), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }
    
    private void exportDoctors() {
        List<Doctor> doctors = doctorManager.getAllDoctors();
        if (doctors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No doctors to export", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            String fileName = CsvExporter.exportDoctors(doctors);
            JOptionPane.showMessageDialog(this, 
                "Doctors exported successfully!\nFile: " + fileName, 
                "Export Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to export doctors: " + e.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void importDoctors() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV file to import");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            CsvImporter.ImportResult importResult = CsvImporter.importDoctors(
                selectedFile.getAbsolutePath(), doctorManager
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
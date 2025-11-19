package clinicapp.gui;

import clinicapp.io.CsvExporter;
import clinicapp.io.CsvImporter;
import clinicapp.model.Patient;
import clinicapp.service.PatientManager;
import clinicapp.util.InputValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class PatientPanel extends JPanel {
    private PatientManager patientManager;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    
    public PatientPanel(PatientManager patientManager) {
        this.patientManager = patientManager;
        initializeUI();
        refreshTable();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        String[] columnNames = {"ID", "Name", "DOB", "Age", "Gender", "Phone", "Email", "Blood Type"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(patientTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Patient");
        JButton viewButton = new JButton("View Details");
        JButton updateButton = new JButton("Update Patient");
        JButton deleteButton = new JButton("Delete Patient");
        JButton exportButton = new JButton("Export to CSV");
        JButton importButton = new JButton("Import from CSV");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(e -> showAddPatientDialog());
        viewButton.addActionListener(e -> showPatientDetails());
        updateButton.addActionListener(e -> showUpdatePatientDialog());
        deleteButton.addActionListener(e -> deletePatient());
        exportButton.addActionListener(e -> exportPatients());
        importButton.addActionListener(e -> importPatients());
        refreshButton.addActionListener(e -> refreshTable());
        
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(refreshButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Patient> patients = patientManager.getAllPatients();
        for (Patient patient : patients) {
            Object[] row = {
                patient.getId(),
                patient.getName(),
                patient.getDateOfBirth(),
                patient.getAge(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getEmail() != null ? patient.getEmail() : "",
                patient.getBloodType() != null ? patient.getBloodType() : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddPatientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Patient", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JTextField nameField = new JTextField();
        JTextField dobField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField bloodTypeField = new JTextField();
        JTextField allergiesField = new JTextField();
        
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Date of Birth (yyyy-MM-dd):"));
        dialog.add(dobField);
        dialog.add(new JLabel("Gender:"));
        dialog.add(genderCombo);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email (optional):"));
        dialog.add(emailField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);
        dialog.add(new JLabel("Blood Type (optional):"));
        dialog.add(bloodTypeField);
        dialog.add(new JLabel("Allergies (optional):"));
        dialog.add(allergiesField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dobStr = dobField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String bloodType = bloodTypeField.getText().trim();
            String allergies = allergiesField.getText().trim();
            
            if (!InputValidator.isValidString(name)) {
                JOptionPane.showMessageDialog(dialog, "Name is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate dob = InputValidator.parseAndValidateDate(dobStr);
            if (dob == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd", "Error", JOptionPane.ERROR_MESSAGE);
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
            
            if (!bloodType.isEmpty() && !InputValidator.isValidBloodType(bloodType)) {
                JOptionPane.showMessageDialog(dialog, "Invalid blood type", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = patientManager.addPatient(
                name, dob, gender, phone,
                email.isEmpty() ? null : email,
                address,
                bloodType.isEmpty() ? null : InputValidator.normalizeBloodType(bloodType),
                allergies.isEmpty() ? null : allergies
            );
            
            if (patient != null) {
                JOptionPane.showMessageDialog(dialog, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add patient", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }
    
    private void showPatientDetails() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        Patient patient = patientManager.getPatientById(patientId);
        
        if (patient != null) {
            String details = String.format(
                "ID: %d\nName: %s\nDOB: %s\nAge: %d\nGender: %s\nPhone: %s\nEmail: %s\nAddress: %s\nBlood Type: %s\nAllergies: %s",
                patient.getId(), patient.getName(), patient.getDateOfBirth(), patient.getAge(),
                patient.getGender(), patient.getPhoneNumber(),
                patient.getEmail() != null ? patient.getEmail() : "N/A",
                patient.getAddress(), 
                patient.getBloodType() != null ? patient.getBloodType() : "N/A",
                patient.getAllergies() != null ? patient.getAllergies() : "None"
            );
            JOptionPane.showMessageDialog(this, details, "Patient Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showUpdatePatientDialog() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        Patient patient = patientManager.getPatientById(patientId);
        
        if (patient == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Patient", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        
        JTextField phoneField = new JTextField(patient.getPhoneNumber());
        JTextField emailField = new JTextField(patient.getEmail() != null ? patient.getEmail() : "");
        JTextField addressField = new JTextField(patient.getAddress());
        JTextField bloodTypeField = new JTextField(patient.getBloodType() != null ? patient.getBloodType() : "");
        JTextField allergiesField = new JTextField(patient.getAllergies() != null ? patient.getAllergies() : "");
        
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email (optional):"));
        dialog.add(emailField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);
        dialog.add(new JLabel("Blood Type (optional):"));
        dialog.add(bloodTypeField);
        dialog.add(new JLabel("Allergies (optional):"));
        dialog.add(allergiesField);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String bloodType = bloodTypeField.getText().trim();
            String allergies = allergiesField.getText().trim();
            
            if (!InputValidator.isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(dialog, "Invalid phone number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!email.isEmpty() && !InputValidator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!bloodType.isEmpty() && !InputValidator.isValidBloodType(bloodType)) {
                JOptionPane.showMessageDialog(dialog, "Invalid blood type", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (patientManager.updatePatient(patientId, null, null, null, phone, 
                                            email.isEmpty() ? null : email, 
                                            address, 
                                            bloodType.isEmpty() ? null : InputValidator.normalizeBloodType(bloodType),
                                            allergies.isEmpty() ? null : allergies)) {
                JOptionPane.showMessageDialog(dialog, "Patient updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update patient", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }
    
    private void deletePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this patient?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (patientManager.deletePatient(patientId)) {
                JOptionPane.showMessageDialog(this, "Patient deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete patient", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportPatients() {
        List<Patient> patients = patientManager.getAllPatients();
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients to export", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            String fileName = CsvExporter.exportPatients(patients);
            JOptionPane.showMessageDialog(this, 
                "Patients exported successfully!\nFile: " + fileName, 
                "Export Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to export patients: " + e.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void importPatients() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV file to import");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            CsvImporter.ImportResult importResult = CsvImporter.importPatients(
                selectedFile.getAbsolutePath(), patientManager
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
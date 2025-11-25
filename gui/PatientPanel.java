package clinicapp.gui;

import clinicapp.gui.components.*;
import clinicapp.io.CsvExporter;
import clinicapp.io.CsvImporter;
import clinicapp.model.Patient;
import clinicapp.service.PatientManager;
import clinicapp.util.InputValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Redesigned Patient Panel - Single page layout
 * Search bar + table + action buttons
 */
public class PatientPanel extends JPanel {
    private PatientManager patientManager;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;

    public PatientPanel(PatientManager patientManager) {
        this.patientManager = patientManager;
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

        // Left side - Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search by:");
        searchLabel.setFont(UIConstants.FONT_LABEL);

        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter ID");
        searchField.setPreferredSize(new Dimension(250, 35));

        searchTypeCombo = new JComboBox<>(new String[] { "ID", "Name" });
        searchTypeCombo.setPreferredSize(new Dimension(140, 35));

        StyledButton searchButton = StyledButton.createSuccess("Search");
        searchButton.setPreferredSize(new Dimension(80, 35));
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchButton);

        searchTypeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) searchTypeCombo.getSelectedItem();
                switch (selected) {
                    case "ID":
                        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter ID");
                        break;
                    case "Name":
                        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Name");
                        break;
                    default:
                        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter ID");
                        break;
                }
            }
        });

        // Right side - Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);

        StyledButton addButton = StyledButton.createPrimary("Add Patient");
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> showAddPatientDialog());

        StyledButton refreshButton = StyledButton.createSuccess("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> refreshTable());

        actionsPanel.add(addButton);
        actionsPanel.add(refreshButton);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(actionsPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(UIConstants.createCardBorder());

        // Table
        String[] columnNames = { "ID", "Name", "DOB", "Age", "Gender", "Phone", "Email", "Blood Type" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(30);
        patientTable.setFont(UIConstants.FONT_BODY);
        patientTable.getTableHeader().setFont(UIConstants.FONT_LABEL);

        // Add double-click to edit
        patientTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = patientTable.getSelectedRow();
                    if (row != -1) {
                        int patientId = (int) tableModel.getValueAt(row, 0);
                        showEditPatientDialog(patientId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIConstants.GRAY_50);

        // Left side - Update/Delete buttons
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftButtons.setBackground(UIConstants.GRAY_50);

        JButton updateButton = new JButton("Update Patient");
        updateButton.setPreferredSize(new Dimension(140, 35));
        updateButton.addActionListener(e -> updateSelectedPatient());

        JButton deleteButton = new JButton("Delete Patient");
        deleteButton.setPreferredSize(new Dimension(140, 35));
        deleteButton.addActionListener(e -> deleteSelectedPatient());

        leftButtons.add(updateButton);
        leftButtons.add(deleteButton);

        // Right side - Import/Export buttons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightButtons.setBackground(UIConstants.GRAY_50);

        JButton importButton = new JButton("Import CSV");
        importButton.setPreferredSize(new Dimension(120, 35));
        importButton.addActionListener(e -> importPatients());

        JButton exportButton = new JButton("Export CSV");
        exportButton.setPreferredSize(new Dimension(120, 35));
        exportButton.addActionListener(e -> exportPatients());

        rightButtons.add(importButton);
        rightButtons.add(exportButton);

        panel.add(leftButtons, BorderLayout.WEST);
        panel.add(rightButtons, BorderLayout.EAST);

        return panel;
    }

    private void updateSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to update",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        showEditPatientDialog(patientId);
    }

    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient: " + patientName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (patientManager.deletePatient(patientId)) {
                JOptionPane.showMessageDialog(this, "Patient deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete patient",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);

        if ("ID".equals(searchType)) {
            try {
                int id = Integer.parseInt(searchValue);
                Patient patient = patientManager.getPatientById(id);

                if (patient != null) {
                    addPatientToTable(patient);
                } else {
                    JOptionPane.showMessageDialog(this, "No patient found with ID: " + id,
                            "No Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            List<Patient> results = patientManager.searchPatientByName(searchValue);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No patients found with name: " + searchValue,
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Patient patient : results) {
                    addPatientToTable(patient);
                }
            }
        }
    }

    private void refreshTable() {
        searchField.setText("");
        tableModel.setRowCount(0);
        List<Patient> patients = patientManager.getAllPatients();
        for (Patient patient : patients) {
            addPatientToTable(patient);
        }
    }

    private void addPatientToTable(Patient patient) {
        Object[] row = {
                patient.getId(),
                patient.getName(),
                patient.getDateOfBirth(),
                patient.getAge(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getEmail() != null ? patient.getEmail() : "N/A",
                patient.getBloodType() != null ? patient.getBloodType() : "N/A"
        };
        tableModel.addRow(row);
    }

    private void showAddPatientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Patient", true);
        dialog.setSize(500, 500);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        JTextField nameField = new JTextField(20);
        DatePicker dobPicker = new DatePicker();
        JComboBox<String> genderCombo = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField bloodTypeField = new JTextField(20);
        JTextField allergiesField = new JTextField(20);

        int row = 0;
        addFormField(formPanel, gbc, row++, "Name: *", nameField);
        addFormField(formPanel, gbc, row++, "Date of Birth: *", dobPicker);
        addFormField(formPanel, gbc, row++, "Gender: *", genderCombo);
        addFormField(formPanel, gbc, row++, "Phone: *", phoneField);
        addFormField(formPanel, gbc, row++, "Email:", emailField);
        addFormField(formPanel, gbc, row++, "Address:", addressField);
        addFormField(formPanel, gbc, row++, "Blood Type:", bloodTypeField);
        addFormField(formPanel, gbc, row++, "Allergies:", allergiesField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton saveButton = StyledButton.createPrimary("Create");
        saveButton.setPreferredSize(new Dimension(90, 30));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(90, 30));

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dobStr = dobPicker.getDateString();
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
                JOptionPane.showMessageDialog(dialog, "Invalid date", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!InputValidator.isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(dialog, "Invalid phone number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Patient patient = patientManager.addPatient(name, dob, gender, phone,
                    email.isEmpty() ? null : email, address,
                    bloodType.isEmpty() ? null : InputValidator.normalizeBloodType(bloodType),
                    allergies.isEmpty() ? null : allergies);

            if (patient != null) {
                JOptionPane.showMessageDialog(dialog, "Patient added successfully!\nID: " + patient.getId(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshTable();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditPatientDialog(int patientId) {
        Patient patient = patientManager.getPatientById(patientId);
        if (patient == null)
            return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Patient", true);
        dialog.setSize(500, 600);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        JTextField nameField = new JTextField(patient.getName(), 20);
        DatePicker dobPicker = new DatePicker();
        dobPicker.setDate(patient.getDateOfBirth().toString());
        JComboBox<String> genderCombo = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        genderCombo.setSelectedItem(patient.getGender());
        JTextField phoneField = new JTextField(patient.getPhoneNumber(), 20);
        JTextField emailField = new JTextField(patient.getEmail() != null ? patient.getEmail() : "", 20);
        JTextField addressField = new JTextField(patient.getAddress(), 20);
        JTextField bloodTypeField = new JTextField(patient.getBloodType() != null ? patient.getBloodType() : "", 20);
        JTextField allergiesField = new JTextField(patient.getAllergies() != null ? patient.getAllergies() : "", 20);

        int row = 0;
        addFormField(formPanel, gbc, row++, "Name: *", nameField);
        addFormField(formPanel, gbc, row++, "Date of Birth: *", dobPicker);
        addFormField(formPanel, gbc, row++, "Gender: *", genderCombo);
        addFormField(formPanel, gbc, row++, "Phone: *", phoneField);
        addFormField(formPanel, gbc, row++, "Email:", emailField);
        addFormField(formPanel, gbc, row++, "Address:", addressField);
        addFormField(formPanel, gbc, row++, "Blood Type:", bloodTypeField);
        addFormField(formPanel, gbc, row++, "Allergies:", allergiesField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton saveButton = StyledButton.createSuccess("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dobStr = dobPicker.getDateString();
            String gender = (String) genderCombo.getSelectedItem();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String bloodType = bloodTypeField.getText().trim();
            String allergies = allergiesField.getText().trim();

            LocalDate dob = InputValidator.parseAndValidateDate(dobStr);
            if (dob == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid date", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (patientManager.updatePatient(patientId, name, dob, gender, phone,
                    email.isEmpty() ? null : email, address,
                    bloodType.isEmpty() ? null : InputValidator.normalizeBloodType(bloodType),
                    allergies.isEmpty() ? null : allergies)) {
                JOptionPane.showMessageDialog(dialog, "Patient updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshTable();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

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

    private void exportPatients() {
        List<Patient> patients = patientManager.getAllPatients();
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients to export", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String fileName = CsvExporter.exportPatients(patients);
            JOptionPane.showMessageDialog(this, "Patients exported successfully!\nFile: " + fileName,
                    "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to export patients: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
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
                    selectedFile.getAbsolutePath(), patientManager);

            StringBuilder message = new StringBuilder();
            message.append("Import completed!\n");
            message.append("Success: ").append(importResult.successCount).append("\n");
            message.append("Errors: ").append(importResult.errorCount);

            JOptionPane.showMessageDialog(this, message.toString(), "Import Results",
                    importResult.errorCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            refreshTable();
        }
    }
}

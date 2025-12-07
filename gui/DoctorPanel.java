package clinicapp.gui;

import clinicapp.gui.components.*;
import clinicapp.io.CsvExporter;
import clinicapp.io.CsvImporter;
import clinicapp.model.Doctor;
import clinicapp.service.DoctorManager;
import clinicapp.util.InputValidator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Doctor Panel - Search bar + table + action buttons
public class DoctorPanel extends JPanel {
    private DoctorManager doctorManager;
    private JTable doctorTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;

    public DoctorPanel(DoctorManager doctorManager) {
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

        // Left side - Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search by:");
        searchLabel.setFont(UIConstants.FONT_LABEL);

        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter ID");
        searchField.setPreferredSize(new Dimension(250, 35));

        searchTypeCombo = new JComboBox<>(new String[] { "ID", "Name", "Specialization" });
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
                    case "Specialization":
                        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Specialization");
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

        StyledButton addButton = StyledButton.createPrimary("Add Doctor");
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> showAddDoctorDialog());

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
        String[] columnNames = { "ID", "Name", "Specialization", "Phone", "Email", "Available Days" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setRowHeight(30);
        doctorTable.setFont(UIConstants.FONT_BODY);
        doctorTable.getTableHeader().setFont(UIConstants.FONT_LABEL);

        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        doctorTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        doctorTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        doctorTable.getColumnModel().getColumn(3).setPreferredWidth(25);
        doctorTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        doctorTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Add double-click to edit
        doctorTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = doctorTable.getSelectedRow();
                    if (row != -1) {
                        int doctorId = (int) tableModel.getValueAt(row, 0);
                        showEditDoctorDialog(doctorId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIConstants.GRAY_50);

        // Left side - Update/Delete buttons
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftButtons.setBackground(UIConstants.GRAY_50);

        JButton updateButton = new JButton("Update Doctor");
        updateButton.setPreferredSize(new Dimension(140, 35));
        updateButton.addActionListener(e -> updateSelectedDoctor());

        JButton deleteButton = new JButton("Delete Doctor");
        deleteButton.setPreferredSize(new Dimension(140, 35));
        deleteButton.addActionListener(e -> deleteSelectedDoctor());

        leftButtons.add(updateButton);
        leftButtons.add(deleteButton);

        // Right side - Import/Export buttons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightButtons.setBackground(UIConstants.GRAY_50);

        JButton importButton = new JButton("Import CSV");
        importButton.setPreferredSize(new Dimension(120, 35));
        importButton.addActionListener(e -> importDoctors());

        JButton exportButton = new JButton("Export CSV");
        exportButton.setPreferredSize(new Dimension(120, 35));
        exportButton.addActionListener(e -> exportDoctors());

        rightButtons.add(importButton);
        rightButtons.add(exportButton);

        panel.add(leftButtons, BorderLayout.WEST);
        panel.add(rightButtons, BorderLayout.EAST);

        return panel;
    }

    private void updateSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to update",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        showEditDoctorDialog(doctorId);
    }

    private void deleteSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete doctor: Dr. " + doctorName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorManager.deleteDoctor(doctorId)) {
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete doctor",
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
                Doctor doctor = doctorManager.getDoctorById(id);

                if (doctor != null) {
                    addDoctorToTable(doctor);
                } else {
                    JOptionPane.showMessageDialog(this, "No doctor found with ID: " + id,
                            "No Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("Name".equals(searchType)) {
            List<Doctor> results = doctorManager.searchDoctorByName(searchValue);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No doctors found with name: " + searchValue,
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Doctor doctor : results) {
                    addDoctorToTable(doctor);
                }
            }
        } else { // Specialization
            List<Doctor> results = doctorManager.searchDoctorBySpecialization(searchValue);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No doctors found with specialization: " + searchValue,
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Doctor doctor : results) {
                    addDoctorToTable(doctor);
                }
            }
        }
    }

    private void refreshTable() {
        searchField.setText("");
        tableModel.setRowCount(0);
        List<Doctor> doctors = doctorManager.getAllDoctors();
        for (Doctor doctor : doctors) {
            addDoctorToTable(doctor);
        }
    }

    private void addDoctorToTable(Doctor doctor) {
        String availableDays = doctor.getAvailableDays() != null ? String.join(", ", doctor.getAvailableDays()) : "N/A";

        Object[] row = {
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getPhoneNumber(),
                doctor.getEmail() != null ? doctor.getEmail() : "N/A",
                availableDays
        };
        tableModel.addRow(row);
    }

    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Doctor", true);
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
        JTextField specializationField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        // Available Days checkboxes
        JCheckBox[] dayCheckboxes = new JCheckBox[7];
        String[] dayNames = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        JPanel daysPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        for (int i = 0; i < 7; i++) {
            dayCheckboxes[i] = new JCheckBox(dayNames[i].substring(0, 3));
            daysPanel.add(dayCheckboxes[i]);
        }

        // Time pickers
        TimePicker startTimePicker = new TimePicker();
        TimePicker endTimePicker = new TimePicker();

        int row = 0;
        addFormField(formPanel, gbc, row++, "Name: *", nameField);
        addFormField(formPanel, gbc, row++, "Specialization: *", specializationField);
        addFormField(formPanel, gbc, row++, "Phone: *", phoneField);
        addFormField(formPanel, gbc, row++, "Email:", emailField);
        addFormField(formPanel, gbc, row++, "Available Days:", daysPanel);
        addFormField(formPanel, gbc, row++, "Start Time:", startTimePicker);
        addFormField(formPanel, gbc, row++, "End Time:", endTimePicker);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton saveButton = StyledButton.createPrimary("Create");
        saveButton.setPreferredSize(new Dimension(90, 30));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(90, 30));

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String specialization = specializationField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Collect selected days
            List<String> availableDays = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (dayCheckboxes[i].isSelected()) {
                    availableDays.add(dayNames[i]);
                }
            }

            String startTime = startTimePicker.getTimeString();
            String endTime = endTimePicker.getTimeString();

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

            Doctor doctor = doctorManager.addDoctor(name, specialization, phone,
                    email.isEmpty() ? null : email,
                    availableDays.isEmpty() ? null : availableDays,
                    startTime, endTime);

            if (doctor != null) {
                JOptionPane.showMessageDialog(dialog, "Doctor added successfully!\nID: " + doctor.getId(),
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

    private void showEditDoctorDialog(int doctorId) {
        Doctor doctor = doctorManager.getDoctorById(doctorId);
        if (doctor == null)
            return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Doctor", true);
        dialog.setSize(550, 650);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        JTextField nameField = new JTextField(doctor.getName(), 20);
        JTextField specializationField = new JTextField(doctor.getSpecialization(), 20);
        JTextField phoneField = new JTextField(doctor.getPhoneNumber(), 20);
        JTextField emailField = new JTextField(doctor.getEmail() != null ? doctor.getEmail() : "", 20);

        // Available Days checkboxes
        JCheckBox[] dayCheckboxes = new JCheckBox[7];
        String[] dayNames = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        JPanel daysPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        for (int i = 0; i < 7; i++) {
            dayCheckboxes[i] = new JCheckBox(dayNames[i].substring(0, 3));
            if (doctor.getAvailableDays() != null && doctor.getAvailableDays().contains(dayNames[i])) {
                dayCheckboxes[i].setSelected(true);
            }
            daysPanel.add(dayCheckboxes[i]);
        }

        // Time pickers
        TimePicker startTimePicker = new TimePicker();
        if (doctor.getStartTime() != null)
            startTimePicker.setTime(doctor.getStartTime());
        TimePicker endTimePicker = new TimePicker();
        if (doctor.getEndTime() != null)
            endTimePicker.setTime(doctor.getEndTime());

        int row = 0;
        addFormField(formPanel, gbc, row++, "Name: *", nameField);
        addFormField(formPanel, gbc, row++, "Specialization: *", specializationField);
        addFormField(formPanel, gbc, row++, "Phone: *", phoneField);
        addFormField(formPanel, gbc, row++, "Email:", emailField);
        addFormField(formPanel, gbc, row++, "Available Days:", daysPanel);
        addFormField(formPanel, gbc, row++, "Start Time:", startTimePicker);
        addFormField(formPanel, gbc, row++, "End Time:", endTimePicker);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton saveButton = StyledButton.createSuccess("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String specialization = specializationField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Collect selected days
            List<String> availableDays = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (dayCheckboxes[i].isSelected()) {
                    availableDays.add(dayNames[i]);
                }
            }

            String startTime = startTimePicker.getTimeString();
            String endTime = endTimePicker.getTimeString();

            if (doctorManager.updateDoctor(doctorId, name, specialization, phone,
                    email.isEmpty() ? null : email,
                    availableDays.isEmpty() ? null : availableDays,
                    startTime, endTime)) {
                JOptionPane.showMessageDialog(dialog, "Doctor updated successfully!",
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

    private void exportDoctors() {
        List<Doctor> doctors = doctorManager.getAllDoctors();
        if (doctors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No doctors to export", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String fileName = CsvExporter.exportDoctors(doctors);
            JOptionPane.showMessageDialog(this, "Doctors exported successfully!\nFile: " + fileName,
                    "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to export doctors: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
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
                    selectedFile.getAbsolutePath(), doctorManager);

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

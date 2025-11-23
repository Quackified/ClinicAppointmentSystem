package clinicapp.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Enhanced date picker with separate Day, Month, Year fields
 * Matches the user-friendly design pattern
 */
public class DatePicker extends JPanel {
    private JTextField dayField;
    private JTextField monthField;
    private JTextField yearField;

    public DatePicker() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setBackground(Color.WHITE);

        // Day field (2 digits)
        JLabel dayLabel = new JLabel("DAY");
        dayLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        dayLabel.setForeground(new Color(100, 100, 100));

        dayField = new JTextField(2);
        dayField.setFont(new Font("Arial", Font.PLAIN, 14));
        dayField.setPreferredSize(new Dimension(50, 35));
        dayField.setHorizontalAlignment(JTextField.CENTER);

        JPanel dayPanel = new JPanel(new BorderLayout(0, 2));
        dayPanel.setBackground(Color.WHITE);
        dayPanel.add(dayLabel, BorderLayout.NORTH);
        dayPanel.add(dayField, BorderLayout.CENTER);

        // Month field (2 digits)
        JLabel monthLabel = new JLabel("MONTH");
        monthLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        monthLabel.setForeground(new Color(100, 100, 100));

        monthField = new JTextField(2);
        monthField.setFont(new Font("Arial", Font.PLAIN, 14));
        monthField.setPreferredSize(new Dimension(50, 35));
        monthField.setHorizontalAlignment(JTextField.CENTER);

        JPanel monthPanel = new JPanel(new BorderLayout(0, 2));
        monthPanel.setBackground(Color.WHITE);
        monthPanel.add(monthLabel, BorderLayout.NORTH);
        monthPanel.add(monthField, BorderLayout.CENTER);

        // Year field (4 digits)
        JLabel yearLabel = new JLabel("YEAR");
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        yearLabel.setForeground(new Color(100, 100, 100));

        yearField = new JTextField(4);
        yearField.setFont(new Font("Arial", Font.PLAIN, 14));
        yearField.setPreferredSize(new Dimension(80, 35));
        yearField.setHorizontalAlignment(JTextField.CENTER);

        JPanel yearPanel = new JPanel(new BorderLayout(0, 2));
        yearPanel.setBackground(Color.WHITE);
        yearPanel.add(yearLabel, BorderLayout.NORTH);
        yearPanel.add(yearField, BorderLayout.CENTER);

        add(dayPanel);
        add(monthPanel);
        add(yearPanel);
    }

    public void setDate(int day, int month, int year) {
        dayField.setText(String.format("%02d", day));
        monthField.setText(String.format("%02d", month));
        yearField.setText(String.valueOf(year));
    }

    public void setDate(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                yearField.setText(parts[0]);
                monthField.setText(parts[1]);
                dayField.setText(parts[2]);
            }
        }
    }

    public String getDateString() {
        String day = dayField.getText().trim();
        String month = monthField.getText().trim();
        String year = yearField.getText().trim();

        if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            return null;
        }

        // Pad with zeros if needed
        if (day.length() == 1)
            day = "0" + day;
        if (month.length() == 1)
            month = "0" + month;

        return year + "-" + month + "-" + day;
    }

    public void clear() {
        dayField.setText("");
        monthField.setText("");
        yearField.setText("");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dayField.setEnabled(enabled);
        monthField.setEnabled(enabled);
        yearField.setEnabled(enabled);
    }
}

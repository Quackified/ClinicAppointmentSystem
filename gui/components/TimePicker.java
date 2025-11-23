package clinicapp.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Time picker with Hour, Minute, and AM/PM selector
 */
public class TimePicker extends JPanel {
    private JTextField hourField;
    private JTextField minuteField;
    private JComboBox<String> amPmCombo;

    public TimePicker() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setBackground(Color.WHITE);

        // Hour field
        JLabel hourLabel = new JLabel("HOUR");
        hourLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        hourLabel.setForeground(new Color(100, 100, 100));

        hourField = new JTextField(2);
        hourField.setFont(new Font("Arial", Font.PLAIN, 14));
        hourField.setPreferredSize(new Dimension(50, 35));
        hourField.setHorizontalAlignment(JTextField.CENTER);

        JPanel hourPanel = new JPanel(new BorderLayout(0, 2));
        hourPanel.setBackground(Color.WHITE);
        hourPanel.add(hourLabel, BorderLayout.NORTH);
        hourPanel.add(hourField, BorderLayout.CENTER);

        // Minute field
        JLabel minuteLabel = new JLabel("MINUTE");
        minuteLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        minuteLabel.setForeground(new Color(100, 100, 100));

        minuteField = new JTextField(2);
        minuteField.setFont(new Font("Arial", Font.PLAIN, 14));
        minuteField.setPreferredSize(new Dimension(50, 35));
        minuteField.setHorizontalAlignment(JTextField.CENTER);

        JPanel minutePanel = new JPanel(new BorderLayout(0, 2));
        minutePanel.setBackground(Color.WHITE);
        minutePanel.add(minuteLabel, BorderLayout.NORTH);
        minutePanel.add(minuteField, BorderLayout.CENTER);

        // AM/PM selector
        JLabel amPmLabel = new JLabel("PERIOD");
        amPmLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        amPmLabel.setForeground(new Color(100, 100, 100));

        amPmCombo = new JComboBox<>(new String[] { "AM", "PM" });
        amPmCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        amPmCombo.setPreferredSize(new Dimension(70, 35));

        JPanel amPmPanel = new JPanel(new BorderLayout(0, 2));
        amPmPanel.setBackground(Color.WHITE);
        amPmPanel.add(amPmLabel, BorderLayout.NORTH);
        amPmPanel.add(amPmCombo, BorderLayout.CENTER);

        add(hourPanel);
        add(new JLabel(":"));
        add(minutePanel);
        add(amPmPanel);
    }

    public void setTime(String timeStr) {
        if (timeStr != null && !timeStr.isEmpty()) {
            // Parse HH:MM format (24-hour)
            String[] parts = timeStr.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);

                // Convert to 12-hour format
                String period = "AM";
                if (hour >= 12) {
                    period = "PM";
                    if (hour > 12)
                        hour -= 12;
                }
                if (hour == 0)
                    hour = 12;

                hourField.setText(String.format("%02d", hour));
                minuteField.setText(String.format("%02d", minute));
                amPmCombo.setSelectedItem(period);
            }
        }
    }

    public String getTimeString() {
        String hour = hourField.getText().trim();
        String minute = minuteField.getText().trim();
        String period = (String) amPmCombo.getSelectedItem();

        if (hour.isEmpty() || minute.isEmpty()) {
            return null;
        }

        try {
            int h = Integer.parseInt(hour);
            int m = Integer.parseInt(minute);

            // Convert to 24-hour format
            if (period.equals("PM") && h != 12) {
                h += 12;
            } else if (period.equals("AM") && h == 12) {
                h = 0;
            }

            return String.format("%02d:%02d", h, m);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void clear() {
        hourField.setText("");
        minuteField.setText("");
        amPmCombo.setSelectedIndex(0);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        hourField.setEnabled(enabled);
        minuteField.setEnabled(enabled);
        amPmCombo.setEnabled(enabled);
    }
}

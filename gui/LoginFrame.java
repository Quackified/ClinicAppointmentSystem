package clinicapp.gui;

import clinicapp.gui.components.*;
import javax.swing.*;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;
import java.awt.event.*;

/**
 * Simplified login frame - direct login to dashboard
 * No role selection needed
 */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private LoginCallback callback;
    private JLabel errorLabel;

    ImageIcon appIcon = new ImageIcon(getClass().getResource("/clinicapp/resources/icons/clinic-logo.png"));

    ImageIcon closedEyeIcon = new ImageIcon(getClass().getResource("/clinicapp/resources/icons/closed-eye.png"));
    ImageIcon openEyeIcon = new ImageIcon(getClass().getResource("/clinicapp/resources/icons/opened-eye.png"));

    public interface LoginCallback {
        void onLoginSuccess(String username);
    }

    public LoginFrame(LoginCallback callback) {
        this.callback = callback;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Clinic Management System - Login");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set app icon for window and taskbar
        setIconImage(appIcon.getImage());

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.GRAY_50);
        mainPanel.setBorder(UIConstants.createPaddingBorder(40));

        // Center content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(UIConstants.createCardBorder());
        contentPanel.setMaximumSize(new Dimension(400, 400));

        // Logo/Title
        JLabel appIconLabel = new JLabel(appIcon);
        appIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appIconLabel.setBorder(UIConstants.createPaddingBorder(0));

        JLabel titleLabel = new JLabel("Pinnacle Medical Services");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.PRIMARY_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = StyledLabel.createSmall("Clinic Management System");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setMaximumSize(new Dimension(350, 200));

        // Email field
        JLabel emailLabel = new JLabel("Email *");
        emailLabel.setFont(UIConstants.FONT_LABEL);
        emailLabel.setForeground(UIConstants.GRAY_700);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new StyledTextField(20);
        emailField.setMaximumSize(new Dimension(300, 35));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Placeholder text
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter email");

        // Password field
        JLabel passwordLabel = new JLabel("Password *");
        passwordLabel.setFont(UIConstants.FONT_LABEL);
        passwordLabel.setForeground(UIConstants.GRAY_700);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(UIConstants.createInputBorder());
        passwordField.setFont(UIConstants.FONT_BODY);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(UIConstants.createFocusBorder());
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(UIConstants.createInputBorder());
            }
        });

        // Placeholder text
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter password");

        // Eye toggle button
        JButton toggleButton = new JButton(closedEyeIcon);
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);

        toggleButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        // Toggle logic
        toggleButton.addActionListener(e -> {
            if (passwordField.getEchoChar() != '\u0000') {
                passwordField.setEchoChar((char) 0);
                toggleButton.setIcon(openEyeIcon);
            } else {
                passwordField.setEchoChar('*'); // or '\u2022'
                toggleButton.setIcon(closedEyeIcon);
            }
        });

        // Attach button inside the field (trailing component)
        passwordField.putClientProperty(
                FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT,
                toggleButton);

        // Error field
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(UIConstants.FONT_LABEL);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(true); // hidden by default

        // Add the structure objects
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);

        // Login Button
        StyledButton loginButton = StyledButton.createPrimary("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(350, UIConstants.BUTTON_HEIGHT));
        loginButton.setBorder(UIConstants.createPaddingBorder(5));
        loginButton.addActionListener(e -> handleLogin());

        // Add Enter key support
        passwordField.addActionListener(e -> handleLogin());

        // Demo credentials hint
        JLabel hintLabel = StyledLabel.createSmall("Demo Credentials: admin / admin");
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setForeground(UIConstants.GRAY_500);

        // Assemble content panel
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(appIconLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(0));
        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(loginButton);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(hintLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Center the content panel
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(UIConstants.GRAY_50);
        wrapperPanel.add(contentPanel);

        mainPanel.add(wrapperPanel, BorderLayout.CENTER);
        add(mainPanel);

    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        email = "admin";
        password = "admin";

        emailField.putClientProperty(FlatClientProperties.OUTLINE, null);
        passwordField.putClientProperty(FlatClientProperties.OUTLINE, null);
        errorLabel.setVisible(false);

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter Email and Password");
            errorLabel.setVisible(true);

            emailField.setBackground(new Color(255, 230, 230)); // light red
            passwordField.setBackground(new Color(255, 230, 230)); // light red

            Timer timer = new Timer(3000, e -> {
                errorLabel.setText(" ");
                emailField.setBackground(UIConstants.WHITE);
                passwordField.setBackground(UIConstants.WHITE);
            });
            timer.setRepeats(false); // run only once
            timer.start();
            return;
        }

        // Simple demo authentication (no database integration currently)
        if (email.equals("admin") && password.equals("admin")) {
            dispose();
            if (callback != null) {
                callback.onLoginSuccess(email);
            }
        } else {
            errorLabel.setText("Invalid Email / Password");
            errorLabel.setVisible(true);

            emailField.setBackground(new Color(255, 230, 230)); // light red
            passwordField.setBackground(new Color(255, 230, 230)); // light red

            Timer timer = new Timer(3000, e -> {
                errorLabel.setText(" ");
                emailField.setBackground(UIConstants.WHITE);
                passwordField.setBackground(UIConstants.WHITE);
            });
            timer.setRepeats(false); // run only once
            timer.start();

        }
    }
}

package clinicapp.gui;

import clinicapp.gui.components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Vertical sidebar navigation panel
 * Matches the React UI sidebar design
 */
public class SidebarPanel extends JPanel {

    private String currentPage = "dashboard";
    private NavigationCallback callback;
    private JPanel menuPanel;

    private static ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(SidebarPanel.class.getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    ImageIcon dashboardIconDark = scaleIcon("/clinicapp/resources/icons/dashboard-32-1.png", 24, 24);
    ImageIcon dashboardIconLight = scaleIcon("/clinicapp/resources/icons/dashboard-32-2.png", 24, 24);
    ImageIcon patientsIconDark = scaleIcon("/clinicapp/resources/icons/person-32-1.png", 24, 24);
    ImageIcon patientsIconLight = scaleIcon("/clinicapp/resources/icons/person-32-2.png", 24, 24);
    ImageIcon doctorsIconDark = scaleIcon("/clinicapp/resources/icons/hospital-32-1.png", 24, 24);
    ImageIcon doctorsIconLight = scaleIcon("/clinicapp/resources/icons/hospital-32-2.png", 24, 24);
    ImageIcon appointmentsIconDark = scaleIcon("/clinicapp/resources/icons/date-32-1.png", 24, 24);
    ImageIcon appointmentsIconLight = scaleIcon("/clinicapp/resources/icons/date-32-2.png", 24, 24);

    public interface NavigationCallback {
        void onNavigate(String page);

        void onLogout();
    }

    public SidebarPanel(String userRole, String userName, NavigationCallback callback) {
        this.callback = callback;
        initializeUI(userRole, userName);
    }

    private void initializeUI(String userRole, String userName) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.GRAY_200));

        // Logo/Title Section
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(UIConstants.createPaddingBorder(20));

        JLabel logoLabel = new JLabel("Clinic System");
        logoLabel.setFont(UIConstants.FONT_HEADING);
        logoLabel.setForeground(UIConstants.PRIMARY_BLUE);

        logoPanel.add(logoLabel, BorderLayout.CENTER);

        // Menu Panel
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(UIConstants.createPaddingBorder(10));

        // Add menu items based on role
        addMenuItem("Dashboard", "dashboard", dashboardIconDark, dashboardIconLight);
        addMenuItem("Patients", "patients", patientsIconDark, patientsIconLight);
        addMenuItem("Doctors", "doctors", doctorsIconDark, doctorsIconLight);
        addMenuItem("Appointments", "appointments", appointmentsIconDark, appointmentsIconLight);

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);

        // User Info Panel at bottom
        JPanel userPanel = new JPanel(new BorderLayout(10, 5));
        userPanel.setBackground(UIConstants.GRAY_50);
        userPanel.setBorder(UIConstants.createPaddingBorder(15));

        JLabel userIcon = new JLabel("[U]");
        userIcon.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(UIConstants.GRAY_50);

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(UIConstants.FONT_LABEL_BOLD);
        nameLabel.setForeground(UIConstants.GRAY_800);

        JLabel roleLabel = StyledLabel.createSmall(userRole);

        userInfo.add(nameLabel);
        userInfo.add(roleLabel);

        StyledButton logoutBtn = StyledButton.createOutline("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 30));
        logoutBtn.addActionListener(e -> {
            if (callback != null) {
                callback.onLogout();
            }
        });

        JPanel userContent = new JPanel(new BorderLayout(10, 10));
        userContent.setBackground(UIConstants.GRAY_50);
        userContent.add(userIcon, BorderLayout.WEST);
        userContent.add(userInfo, BorderLayout.CENTER);

        userPanel.add(userContent, BorderLayout.CENTER);
        userPanel.add(logoutBtn, BorderLayout.SOUTH);

        add(logoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(userPanel, BorderLayout.SOUTH);
    }

    private void addMenuItem(String label, String pageId, ImageIcon darkIcon, ImageIcon lightIcon) {
        JButton menuItem = new JButton(label);
        menuItem.setFont(UIConstants.FONT_LABEL);
        menuItem.setFocusPainted(false);
        menuItem.setBorderPainted(false);
        menuItem.setContentAreaFilled(true);
        menuItem.setHorizontalAlignment(SwingConstants.LEFT);
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        menuItem.setPreferredSize(new Dimension(200, 40));

        // Store both icon variants as client properties
        menuItem.putClientProperty("SidebarPanel.darkIcon", darkIcon);
        menuItem.putClientProperty("SidebarPanel.lightIcon", lightIcon);

        // Set icon before text (aligned to the left)
        if (darkIcon != null) {
            menuItem.setIcon(darkIcon);
            menuItem.setIconTextGap(10); // Space between icon and text
        }

        // Set initial state
        updateMenuItemStyle(menuItem, pageId.equals(currentPage));

        menuItem.addActionListener(e -> {
            currentPage = pageId;
            refreshMenuStyles();
            if (callback != null) {
                callback.onNavigate(pageId);
            }
        });

        menuPanel.add(menuItem);
        menuPanel.add(Box.createVerticalStrut(5));
    }

    private void updateMenuItemStyle(JButton button, boolean isActive) {
        // Get stored icon variants
        ImageIcon darkIcon = (ImageIcon) button.getClientProperty("SidebarPanel.darkIcon");
        ImageIcon lightIcon = (ImageIcon) button.getClientProperty("SidebarPanel.lightIcon");

        if (isActive) {
            button.setBackground(UIConstants.PRIMARY_BLUE);
            button.setForeground(Color.WHITE);
            // Switch to light icon for active (selected) state
            if (lightIcon != null) {
                button.setIcon(lightIcon);
            }
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(UIConstants.GRAY_700);
            // Switch to dark icon for inactive state
            if (darkIcon != null) {
                button.setIcon(darkIcon);
            }

            // Add hover effect
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!button.getBackground().equals(UIConstants.PRIMARY_BLUE)) {
                        button.setBackground(UIConstants.PRIMARY_BLUE_LIGHT);
                        button.setForeground(UIConstants.PRIMARY_BLUE);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!button.getBackground().equals(UIConstants.PRIMARY_BLUE)) {
                        button.setBackground(Color.WHITE);
                        button.setForeground(UIConstants.GRAY_700);
                    }
                }
            });
        }
    }

    private void refreshMenuStyles() {
        for (Component comp : menuPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();
                String pageId = text.toLowerCase().replaceAll("[^a-z]", "");
                updateMenuItemStyle(button, pageId.equals(currentPage));
            }
        }
    }

    public void setCurrentPage(String page) {
        this.currentPage = page;
        refreshMenuStyles();
    }
}

package clinicapp.gui;

import clinicapp.gui.components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Vertical sidebar navigation panel
public class SidebarPanel extends JPanel {

    private String currentPage = "dashboard";
    private NavigationCallback callback;
    private JPanel menuPanel;

    // Scales an icon to the specified dimensions
    private static ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(SidebarPanel.class.getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    ImageIcon appIcon = scaleIcon("/clinicapp/resources/icons/clinic-logo-1.png", 32, 32);
    ImageIcon avatarIcon = scaleIcon("/clinicapp/resources/icons/avatar-32-1.png", 32, 32);

    ImageIcon dashboardIconDark = scaleIcon("/clinicapp/resources/icons/dashboard-24-1.png", 24, 24);
    ImageIcon dashboardIconLight = scaleIcon("/clinicapp/resources/icons/dashboard-24-2.png", 24, 24);
    ImageIcon patientsIconDark = scaleIcon("/clinicapp/resources/icons/person-32-1.png", 24, 24);
    ImageIcon patientsIconLight = scaleIcon("/clinicapp/resources/icons/person-32-2.png", 24, 24);
    ImageIcon doctorsIconDark = scaleIcon("/clinicapp/resources/icons/hospital-32-3.png", 24, 24);
    ImageIcon doctorsIconLight = scaleIcon("/clinicapp/resources/icons/hospital-32-4.png", 24, 24);
    ImageIcon appointmentsIconDark = scaleIcon("/clinicapp/resources/icons/date-32-1.png", 24, 24);
    ImageIcon appointmentsIconLight = scaleIcon("/clinicapp/resources/icons/date-32-2.png", 24, 24);
    ImageIcon queueIconDark = scaleIcon("/clinicapp/resources/icons/person-32-1.png", 24, 24);
    ImageIcon queueIconLight = scaleIcon("/clinicapp/resources/icons/person-32-2.png", 24, 24);

    // Navigation callback interface
    public interface NavigationCallback {
        void onNavigate(String page);

        void onLogout();
    }

    // Constructor
    public SidebarPanel(String userRole, String userName, NavigationCallback callback) {
        this.callback = callback;
        initializeUI(userRole, userName);
    }

    // Initializes the UI components
    private void initializeUI(String userRole, String userName) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.GRAY_200));

        // Logo/Title Section
        JPanel logoPanel = new JPanel(new BorderLayout(10, 0));
        logoPanel.setBackground(UIConstants.PRIMARY_BLUE);
        logoPanel.setBorder(UIConstants.createPaddingBorder(20));

        // Logo icon on the left
        JLabel logoIcon = new JLabel(appIcon);
        logoIcon.setVerticalAlignment(SwingConstants.CENTER);

        // Text panel on the right
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(UIConstants.PRIMARY_BLUE);

        JLabel logoTitle = new JLabel("Pinnacle Medical");
        logoTitle.setFont(UIConstants.FONT_LABEL_BOLD);
        logoTitle.setForeground(UIConstants.WHITE);

        JLabel logoLabel = new JLabel("Appointment System");
        logoLabel.setFont(UIConstants.FONT_LABEL);
        logoLabel.setForeground(UIConstants.WHITE);

        textPanel.add(logoTitle);
        textPanel.add(logoLabel);

        logoPanel.add(logoIcon, BorderLayout.WEST);
        logoPanel.add(textPanel, BorderLayout.CENTER);

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
        addMenuItem("Walk-in Queue", "walkinqueue", queueIconDark, queueIconLight);

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);

        // User Info Panel at bottom
        JPanel userPanel = new JPanel(new BorderLayout(10, 5));
        userPanel.setBackground(UIConstants.GRAY_50);
        userPanel.setBorder(UIConstants.createPaddingBorder(15));

        JLabel userIcon = new JLabel(avatarIcon);

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

    // Adds a menu item to the sidebar
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

    // Updates the style of a menu item
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

    // Refreshes the styles of all menu items
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

    // Sets the current page and refreshes the styles
    public void setCurrentPage(String page) {
        this.currentPage = page;
        refreshMenuStyles();
    }
}

package clinicapp.ui;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class ClinicUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        userLabel.setBounds(50, 50, 100, 30);
        passLabel.setBounds(50, 100, 100, 30);
        userField.setBounds(150, 50, 150, 30);
        passField.setBounds(150, 100, 150, 30);
        loginButton.setBounds(150, 150, 100, 30);

        frame.add(userLabel);
        frame.add(passLabel);
        frame.add(userField);
        frame.add(passField);
        frame.add(loginButton);

        frame.setSize(400, 300);
        frame.setLayout(null);
        frame.setVisible(true);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/testdb", "root", "yourpassword"
                    );
                    PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=?"
                    );
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(frame, "Login successful!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid credentials.");
                    }

                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}

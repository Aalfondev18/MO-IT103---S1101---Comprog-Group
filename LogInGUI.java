import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogInGUI implements ActionListener {

    JFrame frame;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton logInButton;

    public LogInGUI() {

        frame = new JFrame("MotorPH Payroll System");

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        logInButton = new JButton("Log In");
        logInButton.addActionListener(this);

        frame.setLayout(new FlowLayout());

        frame.add(usernameLabel);
        frame.add(usernameField);

        frame.add(passwordLabel);
        frame.add(passwordField);

        frame.add(logInButton);

        frame.setSize(300, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Method with return type
    public String authenticateUser(String username, String password)
            throws Exception {

        // Empty fields
        if (username.isEmpty() || password.isEmpty()) {

            throw new IllegalArgumentException(
                    "Username and Password cannot be empty.");
        }

        // Employee Account
        if (username.equals("Employee")
                && password.equals("12345")) {

            return "EMPLOYEE";
        }

        // Payroll Staff Account
        else if (username.equals("Payroll_Staff")
                && password.equals("12345")) {

            return "PAYROLL_STAFF";
        }

        // Invalid login
        else {

            throw new Exception("Invalid username or password.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {

            String username = usernameField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());

            // Store return value
            String role = authenticateUser(username, password);

            // Open Employee Class
            if (role.equals("EMPLOYEE")) {

                JOptionPane.showMessageDialog(frame,
                        "Welcome Employee!");

                new employeePage();
            }

            // Open Payroll Staff Class
            else if (role.equals("PAYROLL_STAFF")) {

                JOptionPane.showMessageDialog(frame,
                        "Welcome Payroll Staff!");

                new payrollStaffPage();
            }

            frame.dispose(); // Close login window
        }

        catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(frame,
                    ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        catch (Exception ex) {

            JOptionPane.showMessageDialog(frame,
                    ex.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        new LogInGUI();
    }
}
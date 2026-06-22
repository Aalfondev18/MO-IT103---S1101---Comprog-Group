import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class LogInGUI implements ActionListener {

    JFrame frame;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton logInButton;
    
    String userFile = "users.csv";

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
        public String authenticateUser(
                String username,
                String password)
                throws Exception {

            if (username.isEmpty()
                    || password.isEmpty()) {

                throw new IllegalArgumentException(
                        "Username and Password cannot be empty.");
            }

            BufferedReader br =
                    new BufferedReader(
                            new FileReader(userFile));

            br.readLine(); // Skip Header

            String line;

            boolean usernameFound = false;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                String csvUsername =
                        parts[0].trim();

                String csvPassword =
                        parts[1].trim();

                String role =
                        parts[2].trim();

                if (csvUsername.equals(username)) {

                    usernameFound = true;

                    if (csvPassword.equals(password)) {

                        br.close();
                        return role;
                    }

                    br.close();

                    throw new Exception(
                            "Invalid password.");
                }
            }

            br.close();

            if (usernameFound == false) {

                throw new Exception(
                        "Invalid username.");
            }

            throw new Exception(
                    "Invalid username and password.");
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
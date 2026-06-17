import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class employeePage implements ActionListener {

    JFrame frame;

    JLabel titleLabel;
    JLabel idLabel;

    JTextField idField;

    JButton searchButton;
    JButton exitButton;

    JTextArea resultArea;

    // Replace with your actual CSV/TXT file path
    String empFile = "employee_Data.csv";

    public employeePage() {

        frame = new JFrame("MotorPH Employee Dashboard");

        // Title
        titleLabel = new JLabel("MotorPH Employee Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Employee ID
        idLabel = new JLabel("Enter Employee ID:");

        idField = new JTextField(15);

        // Buttons
        searchButton = new JButton("Search");
        exitButton = new JButton("Exit");

        searchButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Result Area
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        panel.add(titleLabel);

        JPanel inputPanel = new JPanel();
        inputPanel.add(idLabel);
        inputPanel.add(idField);

        panel.add(inputPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchButton);
        buttonPanel.add(exitButton);

        panel.add(buttonPanel);

        panel.add(scrollPane);

        // Frame
        frame.add(panel);

        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // SEARCH BUTTON
        if (e.getSource() == searchButton) {

            try {

                String employeeID = idField.getText().trim();

                // Empty field validation
                if (employeeID.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Employee ID cannot be empty.");
                }

                boolean found = false;

                BufferedReader br = new BufferedReader(
                        new FileReader(empFile));

                br.readLine(); // Skip Header

                String line;

                while ((line = br.readLine()) != null) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split(",");

                    String id = parts[0].trim();

                    // Match employee ID
                    if (id.equals(employeeID)) {

                        String firstName = parts[1].trim();
                        String lastName = parts[2].trim();
                        String birthDay = parts[3].trim();

                        resultArea.setText(
                                "MotorPH Employee's Data\n"
                                        + "====================================\n"
                                        + "Employee Number: " + employeeID + "\n"
                                        + "Employee Name: "
                                        + firstName + " " + lastName + "\n"
                                        + "Employee Birthday: " + birthDay + "\n"
                                        + "===================================="
                        );

                        found = true;
                        break;
                    }
                }

                br.close();

                // If employee not found
                if (!found) {

                    resultArea.setText(
                            "Employee number does not exist!");
                }

            }

            // Empty field exception
            catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(frame,
                        ex.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // File handling exception
            catch (Exception ex) {

                JOptionPane.showMessageDialog(frame,
                        "Error reading employee file.",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // EXIT BUTTON
        if (e.getSource() == exitButton) {

            frame.dispose();
        }
    }
}

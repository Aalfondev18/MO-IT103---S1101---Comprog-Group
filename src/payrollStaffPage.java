import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.FileWriter;
import javax.swing.table.DefaultTableModel;
public class payrollStaffPage extends JFrame implements ActionListener {

    // ===== COMPONENTS =====
    JButton addEmployeeBtn;
    JButton updateEmployeeBtn;
    JButton employeeDetailsBtn;
    JButton deleteEmployeeBtn;
    JButton onePayrollBtn;
    JButton allPayrollBtn;
    JButton clearBtn;

    JTable employeeTable;
    DefaultTableModel tableModel;
    JTextArea outputArea;

    String attFile = "MotorPH_Attendance Record.csv";
    String empFile = "employee_Data.csv";

    DateTimeFormatter timeFormat =
            DateTimeFormatter.ofPattern("H:mm");

    // ===== CONSTRUCTOR =====
    public payrollStaffPage() {

        setTitle("MotorPH Payroll Staff Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        
        tableModel = new DefaultTableModel();

        tableModel.addColumn("Employee #");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("First Name");

        employeeTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        JScrollPane tableScrollPane =
                new JScrollPane(employeeTable);
        
        addEmployeeBtn =
                new JButton("Add Employee");
        updateEmployeeBtn =
                new JButton("Update Employee");
        
        employeeDetailsBtn =
                new JButton("Employee Details");
        deleteEmployeeBtn =
                new JButton("Delete Employee");

        onePayrollBtn =
                new JButton("Generate Payroll (One Employee)");

        allPayrollBtn =
                new JButton("Generate Payroll (All Employees)");

        clearBtn =
                new JButton("Clear");
        
        addEmployeeBtn.addActionListener(this);
        updateEmployeeBtn.addActionListener(this);
        employeeDetailsBtn.addActionListener(this);
        deleteEmployeeBtn.addActionListener(this);
        onePayrollBtn.addActionListener(this);
        allPayrollBtn.addActionListener(this);
        clearBtn.addActionListener(this);
            
        buttonPanel.add(addEmployeeBtn);
        buttonPanel.add(updateEmployeeBtn);
        buttonPanel.add(employeeDetailsBtn);
        buttonPanel.add(deleteEmployeeBtn);
        buttonPanel.add(onePayrollBtn);
        buttonPanel.add(allPayrollBtn);
        buttonPanel.add(clearBtn);

        // ===== OUTPUT AREA =====
        outputArea = new JTextArea();

        outputArea.setEditable(false);

        outputArea.setFont(
                new Font("Monospaced",
                        Font.PLAIN,
                        12));

        JScrollPane scrollPane =
                new JScrollPane(outputArea);

        // ===== LAYOUT =====
        setLayout(new BorderLayout());

        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        tableScrollPane,
                        scrollPane);

        splitPane.setDividerLocation(250);

        add(buttonPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        loadEmployeeTable();
            setVisible(true);
        }
        public void loadEmployeeTable() {
            System.out.println("TABLE LOADED");
            tableModel.setRowCount(0);

            try (BufferedReader br =
                         new BufferedReader(
                                 new FileReader(empFile))) {

                br.readLine(); // Skip header

                String line;

                while ((line = br.readLine()) != null) {

                    String[] parts = line.split(",");
                    System.out.println("Columns: " + parts.length);
                    if (parts.length >= 19) {
                        
                        tableModel.addRow(
                                new Object[] {
                                       parts[0],   // Employee #
                                       parts[1],   // Last Name
                                       parts[2],   // First Name
                                       parts[11],  // Position
                                       parts[10],  // Status
                                       parts[13],  // Basic Salary
                                       parts[18]   // Hourly Rate
                                });
                    }
                }
            }

           catch (Exception e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage());
            }
        }
    // ===== BUTTON EVENTS =====
    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            // =========================================
            // ADD EMPLOYEE DETAILS BUTTON
            // =========================================
            if (e.getSource() == addEmployeeBtn) {

                    addEmployee();
                }
            // =========================================
            // UPDATE EMPLOYEE DETAILS BUTTON
            // =========================================
            else if (e.getSource() == updateEmployeeBtn) {

                    updateEmployee();
                }
            // =========================================
            // DELETE EMPLOYEE  BUTTON
            // =========================================
                else if (e.getSource() == deleteEmployeeBtn) {

                    deleteEmployee();
                }
            // =========================================
            // EMPLOYEE DETAILS BUTTON
            // =========================================
            if (e.getSource() == employeeDetailsBtn) {

                String employeeID =
                        JOptionPane.showInputDialog(
                                this,
                                "Enter Employee Number:"
                        );

                if (employeeID == null ||
                        employeeID.trim().isEmpty()) {

                    throw new IllegalArgumentException(
                            "Employee number is required.");
                }

                showEmployeeDetails(employeeID.trim());
            }

            // =========================================
            // GENERATE PAYROLL ONE EMPLOYEE
            // =========================================
            else if (e.getSource() == onePayrollBtn) {

                String empNo =
                        JOptionPane.showInputDialog(
                                this,
                                "Enter Employee Number:"
                        );

                if (empNo == null ||
                        empNo.trim().isEmpty()) {

                    throw new IllegalArgumentException(
                            "Employee number is required.");
                }

                String payroll =
                        processPayrollToString(
                                empNo.trim(),
                                attFile,
                                empFile,
                                timeFormat
                        );

                outputArea.setText(payroll);
            }

            // =========================================
            // GENERATE PAYROLL ALL EMPLOYEES
            // =========================================
            else if (e.getSource() == allPayrollBtn) {

                outputArea.setText("");

                try (BufferedReader br =
                             new BufferedReader(
                                     new FileReader(empFile))) {

                    br.readLine();

                    String line;

                    while ((line = br.readLine()) != null) {

                        if (line.trim().isEmpty()) {
                            continue;
                        }

                        String[] parts =
                                line.split(
                                        ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
                                );

                        String empNo =
                                parts[0].trim();

                        outputArea.append(
                                processPayrollToString(
                                        empNo,
                                        attFile,
                                        empFile,
                                        timeFormat
                                )
                        );

                        outputArea.append(
                                "\n\n========================================\n\n");
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Error processing employees.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

            // =========================================
            // CLEAR BUTTON
            // =========================================
            else if (e.getSource() == clearBtn) {

                outputArea.setText("");
            }

        }

        // ===== EXCEPTION HANDLING =====
        catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "System Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    // ====================================================
    // ADD EMPLOYEE
    // ====================================================
            public void addEmployee() {

                String empNo =
                        JOptionPane.showInputDialog(
                                this,
                                "Employee Number:");


                if (empNo == null ||
                        empNo.trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Employee Number is required.");

                    return;
                }
                if (employeeExists(empNo)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Employee ID already exists.");

                    return;
                }
                
                String lastName =
                            JOptionPane.showInputDialog(
                               this,
                               "Last Name:");

               if (lastName == null ||
                       lastName.trim().isEmpty()) {

                   JOptionPane.showMessageDialog(
                           this,
                           "Employee creation cancelled.");

                   return;
               }

                String firstName =
                        JOptionPane.showInputDialog(
                                this,
                                "First Name:");
                if (firstName == null ||
                        firstName.trim().isEmpty()){
                        JOptionPane.showMessageDialog(
                        this,
                               "Employee creation cancelled.");

                        return;
                }
                
                try {

                        FileWriter fw =
                                new FileWriter(
                                        empFile,
                                        true);

                        fw.write(
                                "\n"
                                + empNo + ","
                                + lastName + ","
                                + firstName
                                + ",,,,,,,,,,,,,,,"
                        );

                        fw.close();

                        JOptionPane.showMessageDialog(
                                this,
                                "Employee added successfully.");
                        System.out.println("ADD REFRESH");
                        loadEmployeeTable();
                    }

                    catch (Exception e) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Error saving employee.");
                    }
            }
    // ====================================================
    // EMPLOYEE EXIST Validation
    // ====================================================
        public boolean employeeExists(String empNo) {

            try {

                BufferedReader br =
                        new BufferedReader(
                                new FileReader(empFile));

                br.readLine(); // Skip header

                String line;

                while ((line = br.readLine()) != null) {

                    String[] parts = line.split(",");

                    if (parts[0].trim().equals(empNo)) {

                        br.close();
                        return true;
                    }
                }

                br.close();
            }

            catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error checking employee records.");
            }

            return false;
        } 
    // ====================================================
    // UPDATE   EMPLOYEE DETAILS
    // ====================================================
        public void updateEmployee() {

            String empNo =
            JOptionPane.showInputDialog(
                    this,
                    "Enter Employee Number:");

                    if (empNo == null ||
                            empNo.trim().isEmpty()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Employee Number is required.");

                        return;
                    }

                    String newFirstName =
                            JOptionPane.showInputDialog(
                                    this,
                                    "Enter New First Name:");
                   try {

                    BufferedReader br =
                            new BufferedReader(
                                    new FileReader(empFile));

                    ArrayList<String> records =
                            new ArrayList<>();

                    String line;

                    boolean found = false;

                    while ((line = br.readLine()) != null) {

                        String[] parts =
                                line.split(",");

                        if (parts[0].trim()
                                .equals(empNo)) {

                            parts[2] = newFirstName;

                            line =
                                    String.join(
                                            ",",
                                            parts);

                            found = true;
                        }

                        records.add(line);
                    }

                    br.close();

                    BufferedWriter bw =
                            new BufferedWriter(
                                    new FileWriter(empFile));

                    for (String record : records) {

                        bw.write(record);
                        bw.newLine();
                    }

                    bw.close();

                    if (found) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Employee updated successfully.");
                                loadEmployeeTable();
                    }

                    else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Employee not found.");
                    }

                }

                catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Error updating employee.");
                }
            }
        
    // ====================================================
    //  EMPLOYEE
    // ====================================================
            public void deleteEmployee() {

                String empNo =
                        JOptionPane.showInputDialog(
                                this,
                                "Enter Employee Number to Delete:");

                if (empNo == null ||
                        empNo.trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Employee Number is required.");

                    return;
                }

                int confirm =
                        JOptionPane.showConfirmDialog(
                                this,
                                "Delete Employee "
                                        + empNo
                                        + "?",
                                "Confirm Delete",
                                JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) {

                    return;
                }

                try {

                    BufferedReader br =
                            new BufferedReader(
                                    new FileReader(empFile));

                    ArrayList<String> records =
                            new ArrayList<>();

                    String line;

                    boolean found = false;

                    while ((line = br.readLine()) != null) {

                        String[] parts =
                                line.split(",");

                        if (parts[0].trim()
                                .equals(empNo)) {

                            found = true;
                            continue;
                        }

                        records.add(line);
                    }

                    br.close();

                    BufferedWriter bw =
                            new BufferedWriter(
                                    new FileWriter(empFile));

                    for (String record : records) {

                        bw.write(record);
                        bw.newLine();
                    }

                    bw.close();

                    if (found) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Employee deleted successfully.");
                        loadEmployeeTable();
                    }

                    else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Employee not found.");
                    }

                }

                catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Error deleting employee.");
                }
            }
    // ====================================================
    // SHOW EMPLOYEE DETAILS
    // ====================================================
    public void showEmployeeDetails(String employeeID) {

        boolean found = false;

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(empFile))) {

            br.readLine();

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");

                String id = parts[0].trim();

                if (id.equals(employeeID)) {

                    String firstName =
                            parts[1].trim();

                    String lastName =
                            parts[2].trim();

                    String birthDay =
                            parts[3].trim();

                    outputArea.setText(
                            "MotorPH Employee Data\n" +
                                    "====================================\n" +
                                    "Employee Number: " + employeeID + "\n" +
                                    "Employee Name: " +
                                    firstName + " " + lastName + "\n" +
                                    "Birthday: " + birthDay + "\n"
                    );

                    found = true;
                    break;
                }
            }

            if (!found) {

                outputArea.setText(
                        "Employee number does not exist.");
            }

        } catch (Exception e) {

            outputArea.setText(
                    "Error reading employee file.");
        }
    }

    // ====================================================
    // PAYROLL PROCESSING TO STRING
    // ====================================================
    public static String processPayrollToString(
            String empNo,
            String attFile,
            String empFile,
            DateTimeFormatter timeFormat) {

        StringBuilder output =
                new StringBuilder();

        for (int month = 6; month <= 12; month++) {

            double firstHalf = 0;
            double secondHalf = 0;
            double hourlyRate = 0;

            int daysInMonth =
                    YearMonth.of(2024, month)
                            .lengthOfMonth();

            try (BufferedReader br =
                         new BufferedReader(
                                 new FileReader(attFile))) {

                br.readLine();

                String line;

                while ((line = br.readLine()) != null) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] data =
                            line.split(",");

                    if (!data[0].equals(empNo)) {
                        continue;
                    }

                    String[] dateParts =
                            data[3].split("/");

                    int recordMonth =
                            Integer.parseInt(dateParts[0]);

                    int day =
                            Integer.parseInt(dateParts[1]);

                    int year =
                            Integer.parseInt(dateParts[2]);

                    if (year != 2024 ||
                            recordMonth != month) {

                        continue;
                    }

                    LocalTime login =
                            LocalTime.parse(
                                    data[4].trim(),
                                    timeFormat
                            );

                    LocalTime logout =
                            LocalTime.parse(
                                    data[5].trim(),
                                    timeFormat
                            );

                    double hours =
                            computeHours(login, logout);

                    if (day <= 15) {
                        firstHalf += hours;
                    } else {
                        secondHalf += hours;
                    }
                }

            } catch (Exception e) {

                output.append(
                        "Error reading attendance file.\n");
            }

            try (BufferedReader br =
                         new BufferedReader(
                                 new FileReader(empFile))) {

                br.readLine();

                String line;

                while ((line = br.readLine()) != null) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts =
                            line.split(
                                    ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
                            );

                    String id =
                            parts[0].trim();

                    if (id.equals(empNo)) {

                        hourlyRate =
                                Double.parseDouble(
                                        parts[18]
                                                .replace(",", "")
                                                .trim()
                                );

                        String firstName =
                                parts[1].trim();

                        String lastName =
                                parts[2].trim();

                        output.append(
                                "====================================\n");

                        output.append(
                                "Employee Number: "
                                        + empNo + "\n");

                        output.append(
                                "Employee Name: "
                                        + firstName + " "
                                        + lastName + "\n");

                        output.append(
                                "====================================\n");

                        break;
                    }
                }

            } catch (Exception e) {

                output.append(
                        "Error reading employee file.\n");
            }

            String monthName = switch (month) {

                case 6 -> "June";
                case 7 -> "July";
                case 8 -> "August";
                case 9 -> "September";
                case 10 -> "October";
                case 11 -> "November";
                case 12 -> "December";
                default -> "Month " + month;
            };

            double firstGross =
                    firstHalf * hourlyRate;

            double secondGross =
                    secondHalf * hourlyRate;

            double monthlyGross =
                    firstGross + secondGross;
            // Prevent deductions when no payroll data exists
                if (monthlyGross <= 0) {

                output.append(
                        "\nCutoff: "
                                + monthName
                                + " has no payroll records.\n\n");

                continue;

             }
            double deductionsSSS =
                    (Math.ceil(
                            (monthlyGross - 3250) / 500))
                            * 22.5 + 135;

            if (deductionsSSS <= 135) {
                deductionsSSS = 135;
            } else if (deductionsSSS >= 1125) {
                deductionsSSS = 1125;
            }

            double deductionsPhilHealth =
                    (monthlyGross * 0.03) / 2;

            double deductionsPagibig = 0;

            if (monthlyGross <= 1500) {

                deductionsPagibig =
                        (monthlyGross * 0.03) / 3;

            } else {

                deductionsPagibig =
                        (monthlyGross * 0.04) / 2;

                if (deductionsPagibig >= 100) {
                    deductionsPagibig = 100;
                }
            }

            double taxableIncome =
                    monthlyGross -
                            (deductionsPagibig
                                    + deductionsSSS
                                    + deductionsPhilHealth);

            double deductionsTax = 0;

            if (taxableIncome <= 20832) {

                deductionsTax = 0;

            } else if (taxableIncome <= 33332) {

                deductionsTax =
                        (taxableIncome - 20833) * 0.2;
            }

            double totalDeductions =
                    deductionsPagibig
                            + deductionsSSS
                            + deductionsPhilHealth
                            + deductionsTax;

            double netSalary =
                    secondGross - totalDeductions;

            output.append(
                    "\nCutoff: "
                            + monthName
                            + " 1-15\n");

            output.append(
                    "Hours Worked: "
                            + firstHalf + "\n");

            output.append(
                    "Gross Salary: "
                            + firstGross + "\n");

            output.append(
                    "Net Salary: "
                            + firstGross + "\n");

            output.append(
                    "\nCutoff: "
                            + monthName
                            + " 16-"
                            + daysInMonth + "\n");

            output.append(
                    "Hours Worked: "
                            + secondHalf + "\n");

            output.append(
                    "Gross Salary: "
                            + secondGross + "\n");

            output.append(
                    "SSS: "
                            + deductionsSSS + "\n");

            output.append(
                    "PhilHealth: "
                            + deductionsPhilHealth + "\n");

            output.append(
                    "Pag-IBIG: "
                            + deductionsPagibig + "\n");

            output.append(
                    "Tax: "
                            + deductionsTax + "\n");

            output.append(
                    "Total Deductions: "
                            + totalDeductions + "\n");

            output.append(
                    "Net Salary: "
                            + netSalary + "\n\n");
        }

        return output.toString();
    }

    // ====================================================
    // COMPUTE HOURS
    // ====================================================
    static double computeHours(
            LocalTime login,
            LocalTime logout) {

        LocalTime graceTime =
                LocalTime.of(8, 5);

        LocalTime cutoffTime =
                LocalTime.of(17, 0);

        if (logout.isAfter(cutoffTime)) {
            logout = cutoffTime;
        }

        long minutesWorked =
                Duration.between(login, logout)
                        .toMinutes();

        if (minutesWorked > 60) {
            minutesWorked -= 60;
        } else {
            minutesWorked = 0;
        }

        double hours =
                minutesWorked / 60.0;

        if (!login.isAfter(graceTime)) {
            return 8.0;
        }

        return Math.min(hours, 8.0);
    }
}
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Table2 {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/my_db";
    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "tester11448209";

    private JFrame frame;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Table2 window = new Table2();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    NimbusLookAndFeel laf = (NimbusLookAndFeel) UIManager.getLookAndFeel();
                    laf.getDefaults().put("nimbusBase", new Color(18, 30, 49));
                    laf.getDefaults().put("nimbusBlueGrey", new Color(255, 255, 255));
                    laf.getDefaults().put("control", new Color(18, 30, 49));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Table2() {
        frame = new JFrame("Database Search GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setBackground(new Color(18, 30, 49));

        JButton insertButton = new JButton("Insert");
        panel.add(insertButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        panel.add(deleteButton);

        JLabel tableLabel = new JLabel("Enter Table Name:");
        tableLabel.setForeground(Color.WHITE);
        JTextField tableTextField = new JTextField(20);

        // Add a JButton for updating data
        JButton updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        panel.add(updateButton);


        JButton searchButton = new JButton("Search");

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    deleteButton.setEnabled(true);
                }
            }
        });

        resultTable.setFillsViewportHeight(true);

        panel.add(tableLabel);
        panel.add(tableTextField);
        panel.add(searchButton);
        panel.add(deleteButton);
        panel.add(scrollPane);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showInsertDialog(frame);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableTextField.getText();

                searchDatabase(tableName, tableModel);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = resultTable.getSelectedRow();
                if (selectedRow != -1) {
                    int idToDelete = (int) tableModel.getValueAt(selectedRow, 0);
                    deleteDataFromDatabase(idToDelete);
                    tableModel.removeRow(selectedRow);
                    deleteButton.setEnabled(false);
                }
            }
        });

        resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    deleteButton.setEnabled(true);
                    updateButton.setEnabled(true);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = resultTable.getSelectedRow();
                if (selectedRow != -1) {
                    showUpdateDialog(frame, tableModel, selectedRow);
                }
            }
        });
    }

    private void showInsertDialog(JFrame parentFrame) {
        JDialog insertDialog = new JDialog(parentFrame, "Insert Data");
        JTextField dataField1 = new JTextField(20);
        JTextField dataField2 = new JTextField(20);
        JButton insertDataButton = new JButton("Insert Data");

        JPanel dialogPanel = new JPanel(new GridLayout(0, 2));
        dialogPanel.add(new JLabel("Name:"));
        dialogPanel.add(dataField1);
        dialogPanel.add(new JLabel("Quote:"));
        dialogPanel.add(dataField2);
        dialogPanel.add(insertDataButton);

        insertDialog.add(dialogPanel);
        insertDialog.pack();
        insertDialog.setLocationRelativeTo(parentFrame);
        insertDialog.setVisible(true);

        insertDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Name = dataField1.getText();
                String Quote = dataField2.getText();
                insertDataIntoDatabase(Name, Quote);
                insertDialog.dispose();
            }
        });
    }

    private void showUpdateDialog(JFrame parentFrame, DefaultTableModel tableModel, int selectedRow) {
        JDialog updateDialog = new JDialog(parentFrame, "Update Data");

        // Create input fields for the data you want to update
        JTextField idField = new JTextField(20);
        JTextField dataField1 = new JTextField(20);
        JTextField dataField2 = new JTextField(20);

        // Initialize the input fields with the selected data
        dataField1.setText(tableModel.getValueAt(selectedRow, 1).toString());
        dataField2.setText(tableModel.getValueAt(selectedRow, 2).toString());

        JButton updateDataButton = new JButton("Update Data");

        JPanel dialogPanel = new JPanel(new GridLayout(0, 2));
        dialogPanel.add(new JLabel("ID:"));
        dialogPanel.add(idField);
        dialogPanel.add(new JLabel("Name:"));
        dialogPanel.add(dataField1);
        dialogPanel.add(new JLabel("Quote:"));
        dialogPanel.add(dataField2);
        dialogPanel.add(updateDataButton);

        updateDialog.add(dialogPanel);
        updateDialog.pack();
        updateDialog.setLocationRelativeTo(parentFrame);
        updateDialog.setVisible(true);

        updateDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idToUpdate = Integer.parseInt(idField.getText());
                String Name = dataField1.getText();
                String Quote = dataField2.getText();
                updateDataInDatabase(idToUpdate, Name, Quote);
                updateDialog.dispose();

                // Update the table model with the new data
                tableModel.setValueAt(idToUpdate, selectedRow, 0);
                tableModel.setValueAt(Name, selectedRow, 1);
                tableModel.setValueAt(Quote, selectedRow, 2);
            }
        });
    }


    private void deleteDataFromDatabase(int id) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "DELETE FROM fortest WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Data deleted successfully!");
            } else {
                System.out.println("No data found for deletion.");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDataIntoDatabase(String Name, String Quote) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO fortest (id, Name, Quote) VALUES (NULL, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Name);
            preparedStatement.setString(2, Quote);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully!");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchDatabase(String tableName, DefaultTableModel tableModel) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM " + tableName;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            tableModel.setRowCount(0);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Object[] columns = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }
            tableModel.setColumnIdentifiers(columns);

            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"No results found."});
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"Error: " + e.getMessage()});
            e.printStackTrace();
        }
    }
    // Create a method to update data in the database
    private void updateDataInDatabase(int id, String Name, String Quote) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "UPDATE fortest SET Name = ?, Quote = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Name);
            preparedStatement.setString(2, Quote);
            preparedStatement.setInt(3, id);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Data updated successfully!");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

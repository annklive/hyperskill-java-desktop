package sqlite_viewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;
import java.io.File;

public class SQLiteViewer extends JFrame {
    String databaseUrl;
    DefaultTableModel tableModel;
    JComboBox<String> tablesComboBox;
    JTextArea queryTextArea;
    JButton executeBtn;
    JTextField dbNameTextField;
    public SQLiteViewer() {
        super("SQLite Viewer");
        createFrame();

        JPanel dbPanel = new JPanel();
        dbPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));

        dbNameTextField = createDbNameField();

        queryTextArea = createQueryTextArea();
        tablesComboBox = createDbTablesComboBox(queryTextArea);
        executeBtn = createExecuteButton();
        JButton openFile = createDbConnectButton();
        JTable resultTable = createResultTable();

        JScrollPane tablePane = new JScrollPane(resultTable);
        tablePane.setPreferredSize(new Dimension(480, 400));

        dbPanel.add(dbNameTextField);
        dbPanel.add(openFile);
        add(dbPanel);
        add(tablesComboBox);
        add(queryTextArea);
        add(executeBtn);
        add(tablePane);

        setVisible(true);
    }
    private void disableQuery() {
        tablesComboBox.setEnabled(false);
        queryTextArea.setEnabled(false);
        executeBtn.setEnabled(false);
    }
    private void enableQuery() {
        tablesComboBox.setEnabled(true);
        queryTextArea.setEnabled(true);
        executeBtn.setEnabled(true);
    }
    private JTable createResultTable() {
        tableModel = new DefaultTableModel();
        JTable resultTable = new JTable(tableModel);
        resultTable.setName("Table");
        return resultTable;
    }
    private JTextArea createQueryTextArea() {
        JTextArea ta = new JTextArea("", 10, 32);
        ta.setName("QueryTextArea");
        ta.setEnabled(false);
        return ta;
    }
    private JButton createExecuteButton() {
        JButton btn = new JButton("Execute");
        btn.setName("ExecuteQueryButton");
        btn.addActionListener(actionEvent -> {
            String sqlStmt = queryTextArea.getText();
            System.out.println(sqlStmt);
            try (Connection con = DriverManager.getConnection(databaseUrl)) {
                if (con.isValid(5)) {
                    try (Statement stmt = con.createStatement()) {
                        ResultSet rs = stmt.executeQuery(sqlStmt);
                        ResultSetMetaData rsMeta = rs.getMetaData();
                        for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                            tableModel.addColumn(rsMeta.getColumnName(i));
                        }
                        while (rs.next()) {
                            Object[] row = new Object[rsMeta.getColumnCount()];
                            for (int i = 0; i < rsMeta.getColumnCount(); i++) {
                                row[i] = rs.getObject(i+1);
                            }
                            tableModel.addRow(row);
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (SQLException dbException) {
                dbException.printStackTrace();
            }
        });
        btn.setEnabled(false);
        return btn;
    }
    private JComboBox<String> createDbTablesComboBox(JTextArea queryTextArea) {
        JComboBox<String> cb = new JComboBox<>();
        cb.setName("TablesComboBox");
        cb.setPreferredSize(new Dimension(500, 30));

        cb.addActionListener(actionEvent -> {
            String tbName = (String) cb.getSelectedItem();
            queryTextArea.setText("SELECT * FROM " + tbName + ";");
        });
        return cb;
    }
    public static boolean isFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists() && !file.isDirectory();
    }
    private JButton createDbConnectButton() {
        JButton openFile = new JButton("Open");
        openFile.setName("OpenFileButton");
        openFile.setPreferredSize(new Dimension(100, 30));
        openFile.addActionListener(actionEvent -> {
            String fileName = dbNameTextField.getText();
            if (!isFileExists(fileName)) {
                disableQuery();
                JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
            } else {
                databaseUrl = "jdbc:sqlite:" + dbNameTextField.getText();
                try (Connection con = DriverManager.getConnection(databaseUrl)) {
                    if (con.isValid(5)) {
                        tablesComboBox.removeAllItems();
                        try (Statement statement = con.createStatement()) {
                            // Statement execution
                            ResultSet rs = statement.executeQuery(
                                    "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'");
                            int numRows = 0;
                            while (rs.next()) {
                                tablesComboBox.addItem(rs.getString("name"));
                                numRows++;
                            }
                            if (numRows > 0) {
                                tablesComboBox.setSelectedIndex(0);
                                enableQuery();
                            }
                        } catch (SQLException se) {
                            se.printStackTrace();
                            disableQuery();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("cannot connect to the database");
                    disableQuery();
                }
            }
        });
        return openFile;
    }
    private JTextField createDbNameField() {
        JTextField dbName = new JTextField();
        dbName.setName("FileNameTextField");
        dbName.setPreferredSize(new Dimension(400, 30));
        return dbName;
    }

    private void createFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 800);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        setLayout(new FlowLayout(FlowLayout.CENTER));
    }

}


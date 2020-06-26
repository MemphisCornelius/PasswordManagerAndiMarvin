package logic;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordManager {

    private static final String JDBC_DRIVER = "org.h2.Driver";

    private final Connection conn; //Connection object to the database
    private final String databaseName; //name of the database table

    /**
     * Creates PasswordManager object and connects to the database.
     * @param file the file of the database where the accountnames, usernames and passwords are stored
     * @param password the password for the database
     * @throws IOException if something is wrong with the file
     */
    public PasswordManager(File file, String password) throws IOException {
        Connection connTmp = null;
        String databaseNameTmp = null;

        //checks if the "selected" file is of the correct type
        if (file.exists() && file.isFile() && file.isAbsolute() && file.canRead() && file.canWrite() && file.getName().endsWith(".mv.db")) {

            //database URL
            String dbUrl = "jdbc:h2:file:" + file.getPath().substring(0, file.getPath().length() - 6) +
                    ";CIPHER=AES;TRACE_LEVEL_FILE=0;AUTO_RECONNECT=TRUE";

            try {
                //Connects to database
                Class.forName(JDBC_DRIVER);
                connTmp = DriverManager.getConnection(dbUrl, "user", password + " " + password);
                databaseNameTmp = file.getName().substring(0, file.getName().length() - 6);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

        } else {
            throw new IOException("THE FILE DOES NOT EXIST OR IS NOT READABLE/WRITABLE");
        }
        conn = connTmp;
        databaseName = databaseNameTmp;
    }

    /**
     * Creates PasswordManager object and crates and connects to the database.
     * @param file the path of the database where the accountnames, usernames and passwords are stored
     * @param databaseName nme of the database and the database file
     * @param password the password for the database
     * @throws IOException if something is wrong with the file
     */
    public PasswordManager(File file, String databaseName, String password) throws IOException {
        Connection connTmp = null;
        String databaseNameTmp = null;

        if (file.exists() && file.isDirectory() && file.isAbsolute() && file.canRead() && file.canWrite()) {

            //database URL
            String dbUrl = "jdbc:h2:file:" + file.getPath() + "/" + databaseName +
                    ";CIPHER=AES;TRACE_LEVEL_FILE=0;AUTO_RECONNECT=TRUE";

            //SQL statement to create the table to store all information
            String sql = "CREATE TABLE IF NOT EXISTS " + databaseName+ " (account VARCHAR, username VARCHAR, password VARCHAR, PRIMARY KEY (account))";

            try {
                //Connects to database and creates the table
                Class.forName(JDBC_DRIVER);
                connTmp = DriverManager.getConnection(dbUrl,"user", password + " " + password);
                PreparedStatement pst = connTmp.prepareStatement(sql);
                pst.executeUpdate();
                databaseNameTmp = databaseName;
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        } else {
            throw new IOException("THE PATH DOES NOT EXIST OR IS NOT READABLE/WRITABLE");
        }
        conn = connTmp;
        this.databaseName = databaseNameTmp;
    }

    /**
     * Closes the connection to the database.
     */
    public void logout() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Adds an entry to the database.
     * @param account name of the entry; primary key in database
     * @param username field of the entry for the username
     * @param password field of the entry fot the password
     * @throws IOException if accountname is already used by another entry
     */
    public void newEntry(String account, String username, String password) throws IOException {
        //SQL statement to insert the information to the database
        String sql = "INSERT INTO " + databaseName + "(account, username, password) VALUES(?, ?, ?)";

        //checks if accountname is already used by another account
        if(getEntrysByAccount(account) != null) {
            //execute the SQL statement
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, account);
                pst.setString(2, username);
                pst.setString(3, password);

                pst.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else {
            throw new IOException("Accountname already in use");
        }

    }

    /**
     * deletes entry from database
     * @param account name of which account should be removed
     */
    public void deleteEntry(String account) {
        //SQL statement to delete the account by name
        String sql = "DELETE FROM " + databaseName + " WHERE account = ?";

        //execute the SQL statement
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, account);

            pst.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Edits username and password of an existing entry
     * @param account name of the entry which wiil be edited
     * @param username new username
     * @param password new password
     */
    public void editEntryContent(String account, String username, String password) {
        //SQL statement to update username and password for an entry
        String sql = "UPDATE " + databaseName + " SET username = ?, password = ? WHERE account = ?";

        //execute SQL statement
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, account);

            pst.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Edits the account name of an entry
     * @param oldName current name of the entry which should be changed
     * @param newName new name of the entry
     * @throws IOException if newName is already the name of another entry
     */
    public void editEntryName(String oldName, String newName) throws IOException {
        //SQL statement to update account (primary key) for an entry
        String sql = "UPDATE " + databaseName + " SET account = ? WHERE account = ?";

        //checks if name already exists
        if (getEntrysByAccount(newName) != null) {
            //execute SQL statement
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, newName);
                pst.setString(2, oldName);

                pst.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else {
            throw new IOException("Accountname already in use");
        }
    }

    /**
     * Get all account names of a database
     * Can be empty if no accounts exists and null if something weird happens
     * @return String array of all account names
     */
    public String[] getEntryAccounts() {
        //temporary list to collect all names
        List<String> accounts = new ArrayList<String>();
        //SQL statement to get all account names
        String sql = "SELECT account FROM " + databaseName;

        //execte SQL statement
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            //adds all account names to temporary list
            while (rs.next()) {
                accounts.add(rs.getString(1));
            }
            //converts temporary list to array and returns it
            return (String[]) accounts.toArray();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Returns array of all information of an entry by the username in following format:
     * acccount name, username, password
     * Can be null if entry with account name does not exist
     * @param account name of entry
     * @return String array wit contenten of an etry {accountname, username, password}
     */
    public String[] getEntrysByAccount(String account) {
        //SQL statement to get the information of an account
        String sql = "SELECT * FROM " + databaseName + " WHERE account = ?";

        //executes SQL statement
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, account);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new String[]{rs.getString(1), rs.getString(2), rs.getString(3)};
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}

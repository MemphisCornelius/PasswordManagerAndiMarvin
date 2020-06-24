package logic;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordManager {

    private static final String JDBC_DRIVER = "org.h2.Driver";

    private final Connection conn;
    private final String databaseName;

    public PasswordManager(File file, String password) throws IOException {
        Connection connTmp = null;
        String databaseNameTmp = null;

        if (file.exists() && file.isFile() && file.isAbsolute() && file.canRead() && file.canWrite() && file.getName().endsWith(".mv.db")) {

            String dbUrl = "jdbc:h2:" + file.getPath().substring(0, file.getPath().length() - 6) +
                    ";CIPHER=AES;TRACE_LEVEL_FILE=0;AUTO_RECONNECT=TRUE ";

            try {
                Class.forName(JDBC_DRIVER);
                connTmp = DriverManager.getConnection(dbUrl, "user", password);
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

    public PasswordManager(File file, String databaseName, String password) throws IOException {
        Connection connTmp = null;
        String databaseNameTmp = null;

        if (file.exists() && file.isDirectory() && file.isAbsolute() && file.canRead() && file.canWrite()) {

            String dbUrl = "jdbc:h2:" + file.getPath() + databaseName +
                    ";CIPHER=AES;TRACE_LEVEL_FILE=0;AUTO_RECONNECT=TRUE ";

            String sql = "CREATE TABLE IF NOT EXISTS ? (account VARCHAR, username VARCHAR, password VARCHAR, PRIMARY KEY (account))";

            try {
                Class.forName(JDBC_DRIVER);
                connTmp = DriverManager.getConnection(dbUrl, "user", password);
                PreparedStatement pst = connTmp.prepareStatement(sql);
                pst.setString(1, file.getName());
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


    void logout() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void newEntry(String account, String username, String password) throws IOException {
        String sql = "INSERT INTO ?(account, username, password) VALUES(?, ?, ?)";

        if(getEntrysByAccount(account) != null) {
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, databaseName);
                pst.setString(2, account);
                pst.setString(3, username);
                pst.setString(4, password);

                pst.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else {
            throw new IOException("Accountname already in use");
        }

    }

    void deleteEntry(String account) {
        String sql = "DELETE FROM ? WHERE account = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, databaseName);
            pst.setString(2, account);

            pst.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void editEntryContent(String account, String username, String password) {
        String sql = "UPDATE ? SET username = ?, password = ? WHERE account = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, databaseName);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, account);

            pst.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void editEntryName(String oldName, String newName) throws IOException {
        String sql = "UPDATE ? SET account = ? WHERE account = ?";

        if (getEntrysByAccount(newName) != null) {
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, databaseName);
                pst.setString(2, newName);
                pst.setString(3, oldName);

                pst.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else {
            throw new IOException("Accountname already in use");
        }
    }

    String[] getEntryAccounts() {
        List<String> accounts = new ArrayList<String>();
        String sql = "SELECT account FROM ? ";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, databaseName);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                accounts.add(rs.getString(1));
            }
            return (String[]) accounts.toArray();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    String[] getEntrysByAccount(String account) {
        String sql = "SELECT * FROM ? WHERE account = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, databaseName);
            pst.setString(2, account);

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

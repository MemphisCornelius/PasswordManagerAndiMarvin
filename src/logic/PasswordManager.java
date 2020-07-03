package logic;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PasswordManager {

    private static final String JDBC_DRIVER = "org.h2.Driver";

    private final Connection conn; //Connection Object zur Datenbank

    /**
     * Erstellt ein PasswordManager Object und verbindet sich zur Datenbank
     * @param file absoluter Pfad zur Datei der Datenbank in der alle Informationen gespeichert werden
     * @param password Password der Datenbank
     * @throws IOException wenn etwas mit der Datenbank-Datei nicht stimmt
     */
    public PasswordManager(File file, String password) throws IOException {
        Connection connTmp = null;
        String databaseNameTmp = null;

        //prüft, ob die Datenbank-Daeti existiert, eine Datei ist, der Pfad zur Datei absolut ist,
        //ob die Datei les- und schreibbar ist und auf ".mv.db" endet,
        //um potenzielle Fehler auszuschließen
        if (file.exists() && file.isFile() && file.isAbsolute() && file.canRead() && file.canWrite() && file.getName().endsWith(".mv.db")) {

            //Datenbak-URL
            String dbUrl = "jdbc:h2:file:" + file.getPath().substring(0, file.getPath().length() - 6) +
                    ";CIPHER=AES;TRACE_LEVEL_FILE=0;AUTO_RECONNECT=TRUE";

            try {
                //Verbindet sich zur Datenbank
                Class.forName(JDBC_DRIVER);
                connTmp = DriverManager.getConnection(dbUrl, "user", password + " " + password);
                databaseNameTmp = file.getName().substring(0, file.getName().length() - 6);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

        } else {
            throw new IOException("Something is wrong with the database-file.");
        }
        conn = connTmp;
    }

    /**
     * Erstellt ein Paswordmanager Objekt, erstellt eine neue Datenbank und verbindet sich zu dieser
     * @param path absoluter Pfad zu dem Ordner in dem die Datenbank-Datei erstellt wird
     * @param databaseName Name der Datenbank-Datei
     * @param password Passwort für die Datenbank
     * @throws IOException wenn etwas mit dem Datenbank-Pfad nicht stimmt
     */
    public PasswordManager(File path, String databaseName, String password) throws IOException {
        Connection connTmp = null;
        String databaseNameTmp = null;

        //prüft, ob der Pfad für die Datenbank existiert, ein Ordner ist und les- und schreibbar ist,
        //um potenzielle Fehler auszuschließen
        if (path.exists() && path.isDirectory() && path.isAbsolute() && path.canRead() && path.canWrite()) {

            //Datenbank URL
            String dbUrl = "jdbc:h2:file:" + path.getPath() + "/" + databaseName +
                    ";CIPHER=AES;TRACE_LEVEL_FILE=0;AUTO_RECONNECT=TRUE";

            //SQL statement, um Tabelle zu erstellen, in der alle Informationen gespeichert werden
            String sql = "CREATE TABLE IF NOT EXISTS db(account VARCHAR, username VARCHAR, password VARCHAR, PRIMARY KEY (account))";

            try {
                //Verbindet sich zur Datenbank und führt das SQL statement aus
                Class.forName(JDBC_DRIVER);
                connTmp = DriverManager.getConnection(dbUrl,"user", password + " " + password);
                PreparedStatement pst = connTmp.prepareStatement(sql);
                pst.executeUpdate();
                databaseNameTmp = databaseName;
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        } else {
            throw new IOException("Something is wrong with the database directory");
        }
        conn = connTmp;
    }

    /**
     * Schließt die Verbindung zur Datenbank
     * @return den Erfolg der
     */
    public boolean logout() {
        try {
            conn.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Fügt einen neuen Eintrag zur Datenbank hinzu
     * @param account Name des Eintrags; Primärschlüssel
     * @param username Feld für den Usernamen des Eintrags
     * @param password Feld für das Passwort des Eintrags
     * @retrun Erfolg des SQL statements
     * @throws IllegalArgumentException wenn der Accountname schon von einem anderen Eintrag verwendet wird
     */
    public boolean newEntry(String account, String username, String password) throws IllegalArgumentException {
        //SQL statement um die Informationen in die Datenbank einzutragen
        String sql = "INSERT INTO db(account, username, password) VALUES(?, ?, ?)";

        //prüft, ob der Accountname von einem anderen Eintrag schon verwendet wird
        if(getEntryByName(account) == null) {
            //führt das SQL statement aus
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, account);
                pst.setString(2, username);
                pst.setString(3, password);

                pst.executeUpdate();
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else {
            throw new IllegalArgumentException("Accountname already in use");
        }
        return false;
    }

    /**
     * Löscht den Eintag aus der Datenbank
     * @param account Name des Eintrags, der gelöscht werden soll
     * @return Erfolg der SQL statements
     */
    public boolean deleteEntry(String account) {
        //SQL statement um den Eintrag aus der Datenbank zu löschen
        String sql = "DELETE FROM db WHERE account = ?";

        //führt das SQL statement aus
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, account);

            pst.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Ändert den Usernamen oder das Passwort eines Eintrags
     * @param account Name des Eintrags, welcher geändert werden soll
     * @param username neuer Username
     * @param password neues Passwort
     * @retrun Erfolg des SQL statements
     */
    public boolean editEntryContent(String account, String username, String password) {
        //SQL statement, um den Nutzernamen und das Passwort eines Eintrags zu überschreiben
        String sql = "UPDATE db SET username = ?, password = ? WHERE account = ?";

        //führt das SQL statement aus
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, account);

            pst.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Editiert den Namen eines Eintrags
     * @param oldName momentaner Namen des Eintrags, der geändert werden soll
     * @param newName neuer Name
     * @return Erfolg des SQL statements
     * @throws IllegalArgumentException wenn newName schon Name eines Eintrags ist
     */
    public boolean editEntryName(String oldName, String newName) throws IllegalArgumentException {
        //SQL statement, um den Primärschlüssel (account) zu ändern
        String sql = "UPDATE db SET account = ? WHERE account = ?";

        //prüft, ob der Name schon von einem anderen Eintrag genutzt wird
        if (getEntryByName(newName) == null) {
            //führt das SQL statement aus
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, newName);
                pst.setString(2, oldName);

                pst.executeUpdate();
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else {
            throw new IllegalArgumentException("Accountname already in use");
        }
        return false;
    }

    /**
     * Liefert alle Eintragsnamen der Datenbank
     * Kann leer sein, wenn keine Einträge in der Datenbank sind oder null wenn ein fehler mit der Datenbak auftritt
     * @return String array von allen Eintragsnamen
     */
    public String[] getEntryNames() {
        //temporäre Liste um alle Namen zu sammeln
        List<String> accounts = new ArrayList<String>();
        //SQL statement um alle Namen der Einträge zu bekommen
        String sql = "SELECT account FROM db";

        //führt das SQL statement aus
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            //fügt jedes Ergebnis des SQL statements in die temporäre Liste
            while (rs.next()) {
                accounts.add(rs.getString(1));
            }
            //wandelt die Liste in ein Array um und gibt dieses zurück
            return (String[]) accounts.toArray();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Liefert ein Array mit allen Informationen über einen Eintrag in folgendem Format:
     * acccount name, username, password
     * Kann null sein, wenn es keinen Eintrag für den Namen gibt
     * @param account Name des Eintrags
     * @return String array mit den Infromation des Eintrags {accountname, username, password}
     */
    public String[] getEntryByName(String account) {
        //SQL statement, um alle Informationen eines Eintrags zu bekommen
        String sql = "SELECT * FROM db WHERE account = ?";

        //führt das SQL statement aus
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, account);

            ResultSet rs = pst.executeQuery();
            //erstellt ein neues Array mit den Ergebnissen des SQL statements und gibt dieses zurück
            if (rs.next()) {
                return new String[]{rs.getString(1), rs.getString(2), rs.getString(3)};
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Kopiert den Usernamen oder das Passwort eines Accounts in die Zwischenablage
     * und löscht nach einer Minute die Zwischenablage
     * @param type "username" oder "password"; Feld, welches kopiert werden soll
     * @param account name des Accounts, von dem kopiert werden soll
     * @return ob das SQl statements erfolgreich war
     * @throws IllegalArgumentException wenn type nicht "username" oder "password" ist oder wenn account nicht existiert
     */
    public boolean copyToClipboard(String type, String account) throws IllegalArgumentException {
        //Variabel um den Erfolg des SQL statement zu verfolgen
        boolean success = false;

        //SQL statement um den Usernamen oder das Passwort eines Accounts
        String sql = "SELECT ? FROM db WHERE account = ?";

        //prüft, ob type username oder password ist
        if (type.equals("username") || type.equals("password")) {
            //prüft, ob der acccount existiert
            if (getEntryByName(account) != null) {
                //setzt das SQL statement
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, type);
                    pst.setString(2, account);

                    //führt das SQL statement aus
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        //kopiert das Ergebnis der SQL Querry in die Zwischenablage
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                new StringSelection(rs.getString(1)), null);
                        success = true;

                        //überschreibt die Zwischenablage nach einer Miute mit ""
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                        new StringSelection(""), null);
                            }
                        }, 60 * 1000);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
              throw new IllegalArgumentException("Accountname doe not exist.");
            }
        } else {
            throw new IllegalArgumentException("Illegal type. Must be \"username\" or \"password\".");
        }
    return success;
    }
}

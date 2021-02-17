import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class Application {

    //Textfelder für account, username und passwort
    TextField account, username, password;

    public Application(PasswordManager pwm){

        //das fenster selbst
        Frame window = new Frame("Passwordmanager");
        window.setBounds(5,5,500,500);
        window.setResizable(false);
        window.setLayout(null);

        //dropdownmenü
        JComboBox comboBox = new JComboBox(pwm.getEntryNames() != null ? pwm.getEntryNames() : new Object[0]);
        comboBox.setBounds(5,5,200,50);
        window.add(comboBox);
        //updated die textfelder bei dropdownmeü wechsel
        comboBox.addActionListener(e -> {
            account.setText((String) comboBox.getItemAt(comboBox.getSelectedIndex()));
            username.setText(pwm.getEntryByName((String) comboBox.getItemAt(comboBox.getSelectedIndex()))[1]);
            password.setText(pwm.getEntryByName((String) comboBox.getItemAt(comboBox.getSelectedIndex()))[2]);
        });

        //button um sich auszuloggen
        Button logout = new Button("Logout");
        logout.setBounds(5,window.getHeight() - 55, 200,50);
        window.add(logout);
        //button funktion
        logout.addActionListener(e -> {
            pwm.logout();
            //schließt das fenster und öffnet das login fenster
            window.dispose();
            new Login();
        });

        //button um einen eintrag zu löschen
        Button delete = new Button("Delete Entry");
        delete.setBounds(window.getWidth() - 205, window.getHeight() - 55, 200, 50);
        window.add(delete);
        delete.addActionListener(e -> {
            pwm.deleteEntry((String) comboBox.getItemAt(comboBox.getSelectedIndex())); //löscht aus der datenbank
            comboBox.removeItemAt(comboBox.getSelectedIndex()); //löscht aus dem dropdown
        });

        //button um eintrag zu ändern
        AtomicBoolean isEditing = new AtomicBoolean(false); //gibt den zustand an (muss so sein weil lambda)
        Button edit = new Button("Edit Entry");
        edit.setBounds(window.getWidth() - 205, window.getHeight() - 110, 200, 50);
        window.add(edit);
        //aktion des buttons
        edit.addActionListener(e -> {
            isEditing.set(!isEditing.get());
            username.setEditable(isEditing.get()); //ändert editierbarkeit
            password.setEditable(isEditing.get()); //ändert editierbarkeit
            //buttontext ändern
            edit.setLabel(isEditing.get() ? "Save" : "Edit Entry");
            if(!isEditing.get()) {
                pwm.editEntryContent((String) comboBox.getItemAt(comboBox.getSelectedIndex()), username.getText(), password.getText()); //update datenbank
            }
        });

        //button um eintrag hinzuzufügen
        AtomicBoolean isAdding = new AtomicBoolean(false);
        Button add = new Button("Add Entry");
        add.setBounds(window.getWidth() - 205, window.getHeight() - 165, 200, 50);
        window.add(add);
        //aktion des knopfes
        add.addActionListener(e -> {
            isAdding.set(!isAdding.get());
            account.setEditable(isAdding.get()); //ändert editierbarkeit
            username.setEditable(isAdding.get()); //ändert editierbarkeit
            password.setEditable(isAdding.get()); //ändert editierbarkeit
            //buttontext ändern
            add.setLabel(isAdding.get() ? "Save" : "Add Entry");

            if(!isAdding.get()) {
                pwm.newEntry(account.getText(), username.getText(), password.getText()); //update datenbank
                comboBox.addItem(account.getText()); //update dropdown
            }
        });

        //account textfeld
        account = new TextField((String) comboBox.getItemAt(comboBox.getSelectedIndex()));
        account.setBounds(5, 60, 200,50);
        account.setEditable(false);
        window.add(account);


        //username und passwort textfeld
        try {
            username = new TextField(pwm.getEntryByName((String) comboBox.getItemAt(comboBox.getSelectedIndex()))[1]);
            password = new TextField(pwm.getEntryByName((String) comboBox.getItemAt(comboBox.getSelectedIndex()))[2]);
        }catch (NullPointerException e) {
            username = new TextField();
            password = new TextField();
        }

        username.setBounds(5, 115, 200,50);
        username.setEditable(false);
        window.add(username);

        password.setBounds(5, 170, 200,50);
        password.setEditable(false);
        window.add(password);

        //button um benutzernamen zu kopieren
        Button copyB = new Button("Copy username");
        copyB.setBounds(210, 115, 200,50);
        window.add(copyB);
        copyB.addActionListener(e -> {
           pwm.copyToClipboard("username", (String) comboBox.getItemAt(comboBox.getSelectedIndex()));
        });

        //button um passwort zu kopieren
        Button copyP = new Button("Copy password");
        copyP.setBounds(210, 170, 200,50);
        window.add(copyP);
        copyP.addActionListener(e -> {
            pwm.copyToClipboard("password", (String) comboBox.getItemAt(comboBox.getSelectedIndex()));
        });

        //fesnter sichtbar machen
        window.setVisible(true);

        //lässt das fenster schließen beim drücken auf das rote x
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });
    }
}

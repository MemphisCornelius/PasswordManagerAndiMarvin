import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class Login {

    public Login() {
        //Das Fenster selbst
        Frame window = new Frame("Login PasswordManager");
        window.setResizable(false);
        window.setBounds(20, 20, 210, 225);
        window.setLayout(null);

        //passwort textfeld
        TextField password = new TextField("password");
        password.setBounds(5, 5, 200, 50);
        window.add(password);

        //pfad zur datei textfeld
        TextField path = new TextField("path");
        path.setBounds(5, 60, 200, 50);
        window.add(path);

        //login knopf
        Button login = new Button("LOGIN");
        login.setBounds(5, 115, 200, 50);
        window.add(login);

        //label um fehler mit der datei anzuzeigen
        Label label = new Label();
        label.setBounds(5, 170, 200,50);
        window.add(label);

        //aktion beim auf den loginknopf drücken
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File p = new File(path.getText());

                try {
                    //prüft ob der pfad existiert
                    if (p.exists()) {
                        //wenn ja, öffnet die datenbank
                        new Application(new PasswordManager(p, password.getText()));
                    } else {
                        //wenn nein, erstellt neue datenbank
                        new Application(new PasswordManager(new File(p.getPath().substring(0, p.getPath().length() - p.getName().length())), p.getName(), password.getText()));
                    }
                    //schließt das fenster
                    window.dispose();
                } catch (IOException ioException) {
                    //lässt den nutzer wissen, dass etwas schiefgekaufen ist
                    label.setText("Something went wrong!");
                }
            }
        });

        //lässt das fenster schließen beim drücken auf das rote x
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });

        window.setVisible(true);
    }
}

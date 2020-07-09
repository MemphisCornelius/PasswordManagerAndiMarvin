package sample;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;


import logic.PasswordManager;

import org.eclipse.swt.widgets.Button;


public class GUI_Login extends Composite {
	private Text text;
	private Text text_1;
	private String password;
	private String path;
	public static PasswordManager pasman;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		
		GUI_Login world = new GUI_Login(shell,SWT.NONE);
		shell.setText("Passwort Manager");
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	
	
	
	public GUI_Login(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(240, 240, 240));
		setLayout(null);
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(185, 123, 104, 26);
		
		text_1 = new Text(this, SWT.BORDER);
		text_1.setBounds(185, 155, 104, 26);
		
		Label lblPasswort = new Label(this, SWT.NONE);
		lblPasswort.setAlignment(SWT.CENTER);
		lblPasswort.setText("Passwort");
		lblPasswort.setBounds(109, 123, 55, 15);
		
		Label lblFilepfad = new Label(this, SWT.NONE);
		lblFilepfad.setAlignment(SWT.CENTER);
		lblFilepfad.setText("Datei");
		lblFilepfad.setBounds(109, 158, 55, 15);
		
		Label lblPasswortManager = new Label(this, SWT.NONE);
		lblPasswortManager.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPasswortManager.setAlignment(SWT.CENTER);
		lblPasswortManager.setBounds(168, 38, 133, 15);
		lblPasswortManager.setText("Passwort Manager");
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.setBounds(185, 187, 104, 25);
		btnNewButton.setText("Login");
		/*btnNewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	password= lblPasswort.getText();
            	path= lblFilepfad.getText();
            	File file  = new File(path);
                pasman= new PasswordManager (file,password);
            }*/
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				password= lblPasswort.getText();
            	path= lblFilepfad.getText();
            	File file  = new File(path);
				try {
					pasman=new PasswordManager(file,password);
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				GUI_App GUI_App =new GUI_App(parent,style);
				GUI_App.setVisible(true);
				System.exit(0);
				
			}
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	


}

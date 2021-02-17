package sample;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import logic.PasswordManager;

import sample.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.io.IOException;

public class GUI_App extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private boolean pushed=false;
	private PasswordManager pasman= new PasswordManager(new File("/home/marvin/testi.mv.db"), "testi");//GUI_Login.pasman;
	private Text text;
	private Text text_1;
	private String[] feld;
	private GUI_Login gl;
	private boolean pushed_2=false;
	
	public static void main(String[] args) throws IOException {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		GUI_App world = new GUI_App(shell,SWT.NONE);
		shell.setText("Passwort Manager");

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	public GUI_App(Composite parent, int style) throws IOException {
		
		super(parent, style);
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(10, 10, 106, 438);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setVisible(false);
		Combo Name = new Combo(this, SWT.NONE);
		Name.setBounds(315, 139, 120, 25);
		
		Button button2=new Button(this,SWT.NONE);
		button2.setBounds(315, 275, 120, 25);
		button2.setText("Get Password");
		button2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				feld=pasman.getEntryByName(Name.getText());
				text.setText(feld[1]);
				text_1.setText(feld[2]);
			}
		});	
		Button button = new Button(this, SWT.NONE);
		button.setText("+");
		
		button.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if(pushed_2) 
				{
					pasman.newEntry(Name.getText(), text_1.getText(), text.getText());
					Name.add(Name.getText());
					button.setText("+");
					pushed_2=false;
					text_1.setEnabled(false);
					text.setEnabled(false);
				}else {
				
					text_1.setEnabled(true);
					text.setEnabled(true);
				Name.setEnabled(true);
					button.setText("Save");
					pushed_2=true;
					
					}
				
			}
		});
		
		
			
		button.setBounds(10, 465, 25, 25);
		
		
		Button btnLogout = new Button(this, SWT.NONE);
		btnLogout.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				pasman.logout();
				gl=new GUI_Login(parent,style);
				gl.setVisible(true);
				
			}
		});
		btnLogout.setBounds(41, 465, 75, 25);
		btnLogout.setText("Logout");
		
	

		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				pasman.copyToClipboard("username",text.getText());
			}
		});
		btnNewButton.setText("Copy");
		btnNewButton.setBounds(459, 186, 75, 25);
		
		Button btnNewButton_1 = new Button(this, SWT.NONE);
		btnNewButton_1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				pasman.copyToClipboard("password",text_1.getText());
			}
		});
		btnNewButton_1.setBounds(459, 233, 75, 25);
		btnNewButton_1.setText("Copy");
		
		Button btnNewButton_2 = new Button(this, SWT.NONE);
		btnNewButton_2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		btnNewButton_2.setBounds(697, 465, 75, 25);
		btnNewButton_2.setText("Edit");
		btnNewButton_2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if(pushed) {
					//pasman.editEntryContent(pasman, text_1.getText(), text.getText());
					text_1.setEnabled(false);//test obs geht
					text.setEnabled(false);
					btnNewButton_2.setText("Edit");
					pasman.editEntryContent(Name.getText(), text_1.getText(), text.getText());
				}
				else {
					text_1.setEnabled(true);
					text.setEnabled(true);
					pushed=true;
					btnNewButton_2.setText("Save");
					}
			}
		});
		Button btnNewButton_3 = new Button(this, SWT.NONE);
	
		btnNewButton_3.setBounds(616, 465, 75, 25);
		btnNewButton_3.setText("Delete");
		btnNewButton_3.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				pasman.deleteEntry(Name.getText());
		}});
		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setText("Benutzername");
		lblNewLabel_2.setBounds(183, 186, 106, 25);
		
		Label lblNewLabel_3 = new Label(this, SWT.NONE);
		lblNewLabel_3.setBounds(183, 233, 106, 25);
		lblNewLabel_3.setText("Passwort");
		
		Label lblNewLabel_4 = new Label(this, SWT.NONE);
		lblNewLabel_4.setBounds(183, 142, 106, 25);
		lblNewLabel_4.setText("Account");
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(315, 186, 120, 25);
		text.setEnabled(false);
		
		text_1 = new Text(this, SWT.BORDER);
		text_1.setBounds(315, 233, 120, 25);
		text_1.setEnabled(false);
		
		

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}

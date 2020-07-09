package sample;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import logic.PasswordManager;

import sample.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class GUI_App extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private boolean pushed=false;
	private PasswordManager pasman= GUI_Login.pasman;
	private Text text;
	private Text text_1;
	private GUI_Login gl;
	private int page;
	
	public static void main(String[] args) {
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
	
	public GUI_App(Composite parent, int style) {
		
		super(parent, style);
		
		
		Button button = new Button(this, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		button.setBounds(10, 465, 25, 25);
		button.setText("+");
		
		Button btnLogout = new Button(this, SWT.NONE);
		btnLogout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				pasman.logout();
				gl=new GUI_Login(parent,style);
				gl.setVisible(true);
				System.exit(0);
			}
		});
		btnLogout.setBounds(41, 465, 75, 25);
		btnLogout.setText("Logout");
		
		/*btnLogout.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                
	            }
		}*/
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(10, 10, 106, 438);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				pasman.copyToClipboard("username",text.getText());
			}
		});
		btnNewButton.setText("Copy");
		btnNewButton.setBounds(459, 139, 75, 25);
		/*btnNewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
		}*/
		Button btnNewButton_1 = new Button(this, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				pasman.copyToClipboard("password",text_1.getText());
			}
		});
		btnNewButton_1.setBounds(459, 186, 75, 25);
		btnNewButton_1.setText("Copy");
		/* btnNewButton_1.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                copyToClipboard();
	            }
		
		}*/
		Button btnNewButton_2 = new Button(this, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnNewButton_2.setBounds(697, 465, 75, 25);
		btnNewButton_2.setText("Edit");
		
		Button btnNewButton_3 = new Button(this, SWT.NONE);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(pushed) {
					//pasman.editEntryContent(pasman, text_1.getText(), text.getText());
					text_1.setEnabled(false);
					text.setEnabled(false);
				}
				else {
					text_1.setEnabled(true);
					text.setEnabled(true);
					pushed=true;
					}
			}
		});
		btnNewButton_3.setBounds(616, 465, 75, 25);
		btnNewButton_3.setText("Delete");
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//pasman.deleteEntry();
			}
		});
		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setText("Benutzername");
		lblNewLabel_2.setBounds(183, 139, 106, 25);
		
		Label lblNewLabel_3 = new Label(this, SWT.NONE);
		lblNewLabel_3.setBounds(183, 186, 106, 25);
		lblNewLabel_3.setText("Passwort");
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(315, 186, 120, 25);
		text.setEnabled(false);
		
		text_1 = new Text(this, SWT.BORDER);
		text_1.setBounds(315, 139, 120, 25);
		text_1.setEnabled(false);

	}

	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	

}

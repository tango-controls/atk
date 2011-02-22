package fr.esrf.tangoatk.widget.util;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.ParallelGroup;

//create a dialog box to choose a configuration and a data file
//with JFileChooserFilter
public class JDialog2FileChooser extends JDialog {

	JPanel dialogPane;

	//group components
	JLabel dataLabel = new JLabel("Data file");
	JLabel configLabel = new JLabel("Configuration file");
	final JTextField dataText = new JTextField(20);
	JTextField configText = new JTextField(20);
	JButton dataBrowser = new JButton("...");
	JButton configBrowser = new JButton("...");

	//OK-Cancel panel
	JPanel buttonPanel = new JPanel();
	JButton okButton = new JButton("OK");
	JButton cancelButton = new JButton("CANCEL");

	//chosen files
	String dataFile ="";
	String configFile ="";
	static String[] result = null;

	boolean orderedChoice = true;


	private JDialog2FileChooser(Frame owner_p, String title_p, boolean orderedChoice_p) {
		super(owner_p, title_p);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout(0,10));
		dialogPane = (JPanel) getContentPane();
		dialogPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setGroup();
		setValidationButtons();

		//if true the configFile can be loaded only if there is a dataFile
		orderedChoice = orderedChoice_p;

		//display the dialog
		setContentPane(dialogPane);
		pack();
		setMaximumSize( new Dimension(500,200));
		setResizable(false);
		setLocationRelativeTo(null);

		//if we close the dialog with the cross
		addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e) {
				result = new String[0];
			}});

	}

	public static String[] showOpen2FileDialog(Frame owner_p, String title_p, boolean orderedChoice_p)throws HeadlessException {

		JDialog2FileChooser  pane = new JDialog2FileChooser(owner_p, title_p, orderedChoice_p);
		pane.setVisible(true);

		//doesn't return until the user has clicked on OK or Cancel button
		return result;
	}

	//set the panel of textFields, labels and buttons
	private void setGroup() {
		dataText.setEditable(false);
		configText.setEditable(false);

		//set the group
		JPanel groupPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(groupPanel);
		groupPanel.setLayout(groupLayout);

		groupLayout.setAutoCreateGaps(true);

		//horizontal way
		ParallelGroup parallelGroup1 = groupLayout.createParallelGroup()
		  .addComponent(dataLabel)
		  .addComponent(configLabel);

		ParallelGroup parallelGroup2 = groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING,true)
			.addComponent(dataText)
			.addComponent(configText);

		ParallelGroup parallelGroup3 = groupLayout.createParallelGroup()
			.addComponent(dataBrowser)
			.addComponent(configBrowser);

		groupLayout.setHorizontalGroup( groupLayout.createSequentialGroup()
				.addGroup(parallelGroup1)
				.addGroup(parallelGroup2)
				.addGroup(parallelGroup3)
		);

		//vertical way
		ParallelGroup parallelGroup4 = groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		  .addComponent(dataLabel)
		  .addComponent(dataText)
		  .addComponent(dataBrowser);

		ParallelGroup parallelGroup5 = groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		  .addComponent(configLabel)
		  .addComponent(configText)
		  .addComponent(configBrowser);

		groupLayout.setVerticalGroup( groupLayout.createSequentialGroup()
				.addGroup(parallelGroup4)
				.addGroup(parallelGroup5)
		);

		//listeners
		dataBrowser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dataBrowserPerformed();
			}
		});

		configBrowser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				configBrowserPerformed();
			}
		});

		if( orderedChoice)
			configBrowser.setEnabled(false);

		dialogPane.add(groupPanel, BorderLayout.CENTER);
	}

	//set the panel of buttons OK and CANCEL
	private void setValidationButtons() {

		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalGlue());


		//listeners
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				okButtonPerformed();
			}
		} );

		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				result = new String[0];
				JDialog2FileChooser.this.dispose();
			}
		} );

		dialogPane.add(buttonPanel, BorderLayout.SOUTH);
	}

	//action performed when we click on the OK button
	private void okButtonPerformed() {

		if( orderedChoice && dataFile.length() <= 0 && configFile.length() > 0) {
			dataMissing();
		} else {
			result = new String[]{dataFile,configFile};
			JDialog2FileChooser.this.dispose();
		}
	}

	//action performed when we click on the data browser button
	private void dataBrowserPerformed() {
		JFileChooserFilter chooser = new JFileChooserFilter("Search data file", "txt", "text file");
		dataFile = chooser.showChooserDialog(this);
		dataText.setText(dataFile);

		if( dataFile.length() <= 0 && orderedChoice) {
			configBrowser.setEnabled(false);
		} else {
			configBrowser.setEnabled(true);
		}
	}

	//action performed when we click on the configBrowser button
	private void configBrowserPerformed() {

		JFileChooserFilter chooser = new JFileChooserFilter("Search configuration file", "txt", "text file");
		configFile = chooser.showChooserDialog(this);
		configText.setText(configFile);
	}

	//message shown if orderedChoice is true and there is no data file and a config file
	private void dataMissing() {
		JOptionPane.showMessageDialog(this,
			    "You can't choose a configuration file without a data file.",
			    "Missing data file",
			    JOptionPane.ERROR_MESSAGE);
	}
}

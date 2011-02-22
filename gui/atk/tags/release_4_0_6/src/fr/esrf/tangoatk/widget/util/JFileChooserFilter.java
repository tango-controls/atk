package fr.esrf.tangoatk.widget.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


//JFileChooser with at least one filter
public class JFileChooserFilter extends JFileChooser {
	public JFileChooserFilter(String title_p, String extension_p, String description_p) {
		super(".");
		setDialogTitle(title_p);
		addFilter(extension_p, description_p);
	}

	/**
	 * the extension has to be given without "."
	 *
	 * @param extension_p the extension for the files
	 * @param description_p the description seen in the chooser
	 */
	public void addFilter(String extension_p, String description_p) {
		 addChoosableFileFilter(new MultiExtFileFilter(description_p, extension_p) );
	}


	  public String showChooserDialog(Component parent_p) {
		  String fileName = "";

		  int returnVal = super.showOpenDialog(parent_p);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File f = getSelectedFile();
	            if (f != null) {
	            	fileName = f.getAbsolutePath();
	            }
	        }

	    	return fileName;
	  }
}
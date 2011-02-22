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
		 addChoosableFileFilter(new MyFileFilter(extension_p,description_p) );
	}


	 /**
	   * <code>getExtension</code> returns the extension of a given file, that
	   * is the part after the last `.' in the filename.
	   *
	   * @param f
	   *            a <code>File</code> value
	   * @return a <code>String</code> value
	   */
	  public static String getExtension(File f) {
	      String ext = null;
	      String s = f.getName();
	      int i = s.lastIndexOf('.');
	      if (i > 0 && i < s.length() - 1) {
	          ext = s.substring(i + 1).toLowerCase();
	      }
	      return ext;
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

	  public class MyFileFilter extends FileFilter{
		private String description = null;
		private String extension = null;
		public MyFileFilter(String extension_p, String description_p){

			description = description_p;
			extension = extension_p;
		}

		public boolean accept(File f) {
	               if (f.isDirectory()) {
	                    return true;
	                }
	                String ext = JFileChooserFilter.getExtension(f);
	                if (ext != null && extension.equals(ext))
	                    return true;
	                return false;

		}
	    public String getDescription() {
			return description;
		}

	  }
}
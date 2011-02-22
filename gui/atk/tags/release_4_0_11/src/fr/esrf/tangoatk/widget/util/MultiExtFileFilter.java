/**
 * 
 */
package fr.esrf.tangoatk.widget.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter that can accept multiple extensions (as for jpg, jpe, jpeg). The description is
 * automatically computed with the prefix followed by the list of extensions.
 * 
 * @author MAINGUY
 * 
 */
public class MultiExtFileFilter extends FileFilter {

	protected String prefix;
	protected List<String> extensions;
	protected String description;

	/**
	 * @param filterDescription the description of the type of accepted files
	 * @param defaultExtension a default extension. It ensures we have at least one extension.
	 * @param otherExtensions more extensions for this type of files
	 */
	public MultiExtFileFilter(String filterDescription, String defaultExtension,
			String... otherExtensions) {
		super();

		prefix = filterDescription;

		extensions = new ArrayList<String>(1 + otherExtensions.length);
		extensions.add(defaultExtension);
		for (String ext : otherExtensions) {
			extensions.add(ext);
		}

		computeDescription();
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) {
		boolean result = false;
		if (f != null) {
			// we accept directories
			result |= f.isDirectory();

			// file extension must
			String fileExtension = getExtension(f);
			if (fileExtension != null) {
				result |= isAccepted(fileExtension);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Return true if the specified extension is accepted by the filter.
	 * 
	 * @param fileExtension the fileExtension to test
	 * @return true if the filter would accept this extension, false otherwise or if the extension
	 *         is null
	 */
	public boolean isAccepted(String fileExtension) {
		boolean result = false;
		for (String extension : extensions) {
			result |= extension.equalsIgnoreCase(fileExtension);
		}
		return result;
	}

	/**
	 * Return the default extension. It can be useful when saving a file whose filename has no
	 * extension specified.
	 * 
	 * @return the default extension.
	 */
	public String getDefaultExtension() {
		return extensions.get(0);
	}

	/**
	 * Compute the whole description for the filter. User description is followed by allowed
	 * extensions as a list.
	 */
	protected void computeDescription() {
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(" (");
		Iterator<String> iterator = extensions.iterator();
		// there is at least the default one
		do {
			String ext = iterator.next();
			sb.append("*.").append(ext);
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}
		while (iterator.hasNext());
		sb.append(")");
		description = sb.toString();
	}

	@Override
	public String toString() {
		return description;
	}

	/**
	 * returns the extension of a given file, that is the part after the last '.' in the filename.
	 * Result is in lower case.
	 * 
	 * @param f the file
	 * @return the extension if any, null otherwise
	 */
	public static String getExtension(File f) {
		String ext = null;
		if (f != null) {
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
		}
		return ext;
	}

}

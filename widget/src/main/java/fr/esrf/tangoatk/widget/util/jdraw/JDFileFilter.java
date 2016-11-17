/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/** A basic file filter class */
public class JDFileFilter extends FileFilter {

  private String   description = null;
  private String[] extensions;

  /**
   * Construct a File filter for the set of extension.
   * @param desc Description of this filter
   * @param ext Extension set
   */
  public JDFileFilter(String desc,String[] ext) {
    extensions = ext;
    description = desc + "  (";
    for(int i=0;i<ext.length;i++) {
      description += "." + ext[i];
      if(i<ext.length-1)
        description += ",";
    }
    description += ")";
  }

  // -------------------------------------------------
  // FileFilter interface
  // -------------------------------------------------
  /**
   * Returns true if the specified file has an allowed extension.
   * @param f File to be tested.
   */ 
  public boolean accept(File f) {
    if (f != null) {
      if (f.isDirectory())
        return true;
      return isWantedExtension(getExtension(f));
    }
    return false;
  }
  /**
   * Return the description of this FileFilter
   */
  public String getDescription() {
    return description;
  }

  // -------------------------------------------------

  private String getExtension(File f) {
    if (f != null) {
      String filename = f.getName();
      int i = filename.lastIndexOf('.');
      if (i > 0 && i < filename.length() - 1)
        return filename.substring(i + 1).toLowerCase();
    }
    return null;
  }

  private boolean isWantedExtension(String ext) {

    int i=0;
    boolean found=false;

    if( ext==null )
      return false;

    while(i<extensions.length && !found) {
      found = ext.equalsIgnoreCase(extensions[i]);
      i++;
    }

    return found;
  }


}

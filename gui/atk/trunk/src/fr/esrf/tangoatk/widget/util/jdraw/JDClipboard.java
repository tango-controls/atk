package fr.esrf.tangoatk.widget.util.jdraw;

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

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Vector;

/** Clipboard helper class */

public class JDClipboard {

  private Vector objects;
  Clipboard clipboard;

  static JDClipboard theClipboard = null;


  private JDClipboard() {

    objects = new Vector();
    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

  }

  static JDClipboard getInstance() {

    if( theClipboard== null )
      theClipboard = new JDClipboard();
    return theClipboard;

  }

  void clear() {

    objects.clear();

  }

  int size() {

    return objects.size();

  }

  void addObjects(Vector objs) {

    objects.addAll(objs);

  }

  void addObject(JDObject obj) {

    objects.add(obj);

  }

  void commit() {

    StringBuffer to_save = new StringBuffer();

    to_save.append("JDFile v11 {\n");
    for(int i=0;i<objects.size();i++)
      ((JDObject)objects.get(i)).recordObject(to_save,1);
    to_save.append("}\n");

    StringSelection str = new StringSelection(to_save.toString());
    clipboard.setContents(str, null);

  }

  void load(boolean showError) {

    objects.clear();

    Transferable contents = clipboard.getContents(null);

    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if( !hasTransferableText ) return;

    String str;
    try {
      str = (String)contents.getTransferData(DataFlavor.stringFlavor);
    } catch (UnsupportedFlavorException e1) {
      if(showError)
        JOptionPane.showMessageDialog(null, "Invalid clipboard content.\n" + e1.getMessage());
      return;
    } catch (IOException e2) {
      if(showError)
        JOptionPane.showMessageDialog(null, "Invalid clipboard content.\n" + e2.getMessage());
      return;
    }

    try {

      JDFileLoader loader = new JDFileLoader(str.toString());
      objects = loader.parseFile();

    } catch( IOException e ) {

      if(showError)
        JOptionPane.showMessageDialog(null, "Invalid clipboard content.\n" + e.getMessage());

    }

  }

  JDObject get(int idx) {

    return (JDObject)objects.get(idx);

  }

  Vector getObjects() {

    return objects;

  }

}

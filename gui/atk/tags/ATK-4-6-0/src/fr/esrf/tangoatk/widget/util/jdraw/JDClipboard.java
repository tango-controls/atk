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
  private Vector clipboardListener;

  static JDClipboard theClipboard = null;

  private JDClipboard() {

    objects = new Vector();
    clipboardListener = new Vector();
    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

  }

  static JDClipboard getInstance() {

    if( theClipboard== null )
      theClipboard = new JDClipboard();
    return theClipboard;

  }

  public void addChangeListener(JDrawEditorListener l) {
    if(!clipboardListener.contains(l)) clipboardListener.add(l);
  }

  public void check() {
    load(false);
    for(int i=0;i<clipboardListener.size();i++)
      ((JDrawEditorListener)clipboardListener.get(i)).clipboardChanged();
  }

  int size() {
    return objects.size();
  }

  void send(Vector objs) {

    StringBuffer to_save = new StringBuffer();

    to_save.append("JDFile v11 {\n");
    for(int i=0;i<objs.size();i++)
      ((JDObject)objs.get(i)).recordObject(to_save,1);
    to_save.append("}\n");

    StringSelection str = new StringSelection(to_save.toString());
    try {
      clipboard.setContents(str, null);
    } catch (IllegalStateException e1) {
      System.out.println("JDClipboard.send() : " + e1.getMessage());
      return;
    }

    //  Reload the clipboard
    check();

  }

  private void load(boolean showError) {

    Transferable contents;

    objects.clear();

    try {
      contents = clipboard.getContents(null);
    } catch (IllegalStateException e1) {
      if (showError)
        JOptionPane.showMessageDialog(null, "Clipboard not available.\n" + e1.getMessage());
      System.out.println(e1.getMessage());
      return;
    }

    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if( !hasTransferableText ) return;

    String str;
    try {
      str = (String)contents.getTransferData(DataFlavor.stringFlavor);
    } catch (UnsupportedFlavorException e2) {
      if(showError)
        JOptionPane.showMessageDialog(null, "Invalid clipboard content.\n" + e2.getMessage());
      System.out.println(e2.getMessage());
      return;
    } catch (IOException e3) {
      if(showError)
        JOptionPane.showMessageDialog(null, "Invalid clipboard content.\n" + e3.getMessage());
      System.out.println(e3.getMessage());
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

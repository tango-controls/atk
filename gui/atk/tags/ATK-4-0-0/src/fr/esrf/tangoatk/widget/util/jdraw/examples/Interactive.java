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
import java.io.IOException;
import java.util.Vector;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawEditor;
import fr.esrf.tangoatk.widget.util.jdraw.JDLabel;
import fr.esrf.tangoatk.widget.util.jdraw.JDObject;
import fr.esrf.tangoatk.widget.util.jdraw.JDValueListener;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

public class Interactive extends JFrame implements JDValueListener {

  JDrawEditor theGraph;
  JDObject btn1;
  JDObject btn2;
  JDObject checkbox;
  JDLabel  textArea;
  String[] lines = {"","",""};

  public Interactive() {

    // Creates a JDrawEditor in MODE_PLAY.
    theGraph = new JDrawEditor(JDrawEditor.MODE_PLAY);

    // Loads the JDraw file
    try {
      theGraph.loadFile("interactive.jdw");
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this,e.getMessage(),"Error loading file",JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }

    //Retreives button and checkbox handles
    btn1 = getObject("Button1");
    btn2 = getObject("Button2");
    checkbox = getObject("Checkbox");

    // Inits the text area
    textArea = (JDLabel)getObject("textArea");
    addText("");

    // Listens for value change
    btn1.addValueListener(this);
    btn2.addValueListener(this);
    checkbox.addValueListener(this);

    setContentPane(theGraph);
    setTitle("Interactive");
  }

  // ------------------------------------------------
  // JDValueListener
  // ------------------------------------------------
  public void valueChanged(JDObject src) {

    if(src==checkbox) {
      addText("Checkbox value changed: " + src.getValue());
      textArea.refresh();
    }

  }

  public void valueExceedBounds(JDObject src) {

    // We emulate a button by listening on valueExceedBounds()
    // The JDObject value represents the state of the button (Released=0,Pressed=1)
    // So we are triggered when the value will exceed 1 and will be
    // reseted to its minValue, in other terms, when the button passes
    // from the Pressed to Released state.
    if(src==btn1) {
      addText("Button1 pressed.");
    } else if(src==btn2) {
       addText("Button2 pressed.");
    }

  }

  /**
   * Returns the JDObject having the given name.
   * @param name Name to search
   */
  public JDObject getObject(String name) {

    Vector objs = theGraph.getObjectsByName(name,false);
    if(objs.size()==0) {
      System.out.print("Error , no object named '" + name + "' found." );
      System.exit(0);
    } else if(objs.size()>1) {
      System.out.print("Warning , more than one object having the name : " + name +" found ,getting first..." );
    }
    return (JDObject)objs.get(0);

  }

  /**
   * Adds the specified line to the text area.
   * @param s Line to be added
   */
  public void addText(String s) {
    int i;

    // Shit lines
    for(i=0;i<lines.length-1;i++)
      lines[i] = lines[i+1];

    // Add the new text
    lines[i] = s;

    //Update the text area
    String tmp="";
    for(i=0;i<lines.length;i++)
      tmp+=lines[i] + "\n";
    textArea.setText(tmp);
    textArea.refresh();

  }

  public static void main(String[] args) {
    final Interactive f = new Interactive();
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);
  }

}

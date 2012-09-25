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
 
/*
 * SimpleStringSpectrumViewer.java
 *
 * Created on December 15, 2003, 2:40 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;

import javax.swing.*;

/**
 *
 * @author  poncet
 */
 
public class SimpleStringSpectrumViewer extends javax.swing.JPanel
             implements IStringSpectrumListener
{
     private javax.swing.JScrollPane    jScrollPane1;
     private javax.swing.JTextArea      strSpectText;
     private boolean                    viewEnd = false;
     
     IStringSpectrum                    model;

     /** Creates new form SimpleStringSpectrumViewer */

     public SimpleStringSpectrumViewer()
     {
	initComponents();
	//UIManagerHelper.setAll("StatusViewer.TextArea", status);
     }

     private void initComponents()
     {
	jScrollPane1 = new javax.swing.JScrollPane();
	strSpectText = new javax.swing.JTextArea();

	setLayout(new java.awt.BorderLayout());

	setBorder(new javax.swing.border.TitledBorder("StringSpectrum"));
	jScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	strSpectText.setLineWrap(false);
	strSpectText.setEditable(false);
	strSpectText.setColumns(50);
	strSpectText.setRows(10);
	strSpectText.setText("Unknown");
	strSpectText.setBackground(new java.awt.Color(204, 204, 204));
	jScrollPane1.setViewportView(strSpectText);

	add(jScrollPane1, java.awt.BorderLayout.CENTER);
     }

     public void setModel(IStringSpectrum  strSpectAtt)
     {
	if (model != null)
	{
	   model.removeListener(this);
	}
	strSpectText.setText("");
	this.model = strSpectAtt;
	
	if ( model != null )
	{
	   setBorder(new javax.swing.border.TitledBorder(model.getNameSansDevice()));
	   model.addListener(this);
	   model.refresh();
	}
     }

     /**
      * <code>getModel</code> gets the model of this SimpleStringSpectrumViewer.
      *
      * @return a <code>IStringSpectrum</code> value
      */
     public IStringSpectrum getModel()
     {
        return model;
     }


     public int getRows()
     {
       return strSpectText.getRows();
     }

     public void setRows(int rows)
     {
       strSpectText.setRows(rows);
     }

     public int getColumns()
     {
       return strSpectText.getColumns();
     }

     public void setColumns(int columns)
     {
       strSpectText.setColumns(columns);
     }

     public JTextArea getText()
     {
       return strSpectText;
     }
     
     

     /* javax.swing.JTextArea:setText(String) method has a memory
     leak on SUN Solaris JVM (seems to be OK on windows)
     The setStrTextArea method is called each time the String spectrum attribute
     is read by the refresher even if it has not changed. This will be changed in the
     future when the Tango Events will be used instead of ATK refreshers.
     For the time being a test has been added to limit the memory leak of JVM.
     */
     public void setStrTextArea(String s)
     {
	if (s.equals(strSpectText.getText()))
	  return;
	  
        strSpectText.setText(s);
        if ( isViewEnd() )
           placeTextToEnd (); 
     }

     public void stringSpectrumChange(StringSpectrumEvent evt)
     {
        int       ind_str, attr_size;
	String    str;
	
	//System.out.println("stringSpectrumChange called.\n");
	if (evt.getValue() == null)
	{
	   str = "StringSpectrumAttribute is null.\n";
	}
	else
	{
	    String[]   str_array=null;
	    
	    str_array = evt.getValue();
	    attr_size= str_array.length;
	    
	    StringBuffer strbuff = new StringBuffer(attr_size);
	    
	    for (ind_str=0; ind_str < attr_size; ind_str++)
	    {
	       strbuff.append(str_array[ind_str]);
	       strbuff.append("\n");
	    }
	    str = strbuff.toString();
	}
	setStrTextArea(str);      
     }

     public void errorChange(ErrorEvent evt)
     {
       setStrTextArea("Unknown");
     }

     public void stateChange(AttributeStateEvent evt)
     {
	if ("VALID".equals(evt.getState()))
	{
	    strSpectText.setBackground(getBackground());
	    return;
	}
	strSpectText.setBackground(ATKConstant.getColor4Quality(evt.getState()));
     }

     /**
      * Returns whether user prefers to allways view the end of the text or not
      * @return A boolean to know whether user prefers to allways view the end of the text or not
      */
     public boolean isViewEnd ()
     {
         return viewEnd;
     }

     /**
      * Sets whether user prefers to allways view the end of the text or not.
      * @param viewEnd a boolean to set this preference
      */
     public void setViewEnd (boolean viewEnd)
     {
         this.viewEnd = viewEnd;
         if ( isViewEnd () )
             placeTextToEnd ();
     }

     private void placeTextToEnd ()
     {
         strSpectText.setCaretPosition(strSpectText.getDocument().getLength());
     }

     public static void main(String [] args)
     {
	 fr.esrf.tangoatk.core.AttributeList atl = new fr.esrf.tangoatk.core.AttributeList();
	 final SimpleStringSpectrumViewer sssv = new SimpleStringSpectrumViewer();
         sssv.setViewEnd(true);
	 try
	 {
	     //final IStringSpectrum attr = (IStringSpectrum)atl.add("JM/test/2/SequenceHistory");
	     final IStringSpectrum attr = (IStringSpectrum)atl.add("sys/MSTATUS/RF-TRA/Devices");
             sssv.setModel(attr);
	     atl.startRefresher();

	 }
	 catch (Exception e)
	 {
	     System.out.println(e);
	 } // end of try-catch
	 
	 IEntity ie = null;
	 
	 //ie = atl.get("JM/test/2/SequenceHistory");
         ie = atl.get("sys/MSTATUS/RF-TRA/Devices");
	 if (ie == null)
	    System.out.println("Cannot retreive the attribute from the list.");
	 else
	    if (ie instanceof IStringSpectrum)
	       System.out.println("the attribute retreived from the list.");
	    else
	       System.out.println("the attribute retreived is not IStringSpectrum");


         javax.swing.JFrame f = new javax.swing.JFrame();
	 f.getContentPane().setLayout(new java.awt.GridBagLayout());
	 java.awt.GridBagConstraints                 gbc;
	 gbc = new java.awt.GridBagConstraints();
	 gbc.gridx = 0; gbc.gridy = 0;
	 gbc.weightx = 1.0; gbc.weighty = 1.0;
	 gbc.fill = java.awt.GridBagConstraints.BOTH;
	 gbc.insets = new java.awt.Insets(0, 0, 0, 5);
	 f.getContentPane().add(sssv, gbc);
         f.pack();
         f.setVisible( true );
     }


}

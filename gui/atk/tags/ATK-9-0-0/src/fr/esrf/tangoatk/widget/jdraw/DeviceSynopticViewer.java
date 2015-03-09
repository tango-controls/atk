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
 
// File:          DeviceSynopticViewer.java
// Created:       2004-11-09 15:22:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2004-11-09 15:22:29, poncet>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.jdraw;


import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.JFrame;


public class DeviceSynopticViewer extends javax.swing.JPanel
{
    private static final String    defaultJdrawDir = "/users/poncet/ATK_OLD/jloox_files";

    private String                 jdrawDir = null;    
    private String                 devName = null;
    private TangoSynopticHandler   tsh = null;


    public DeviceSynopticViewer()
    {
	initComponents();
    }
 
    public DeviceSynopticViewer(String  dev)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
        setDevName(dev);
    }
  
    public DeviceSynopticViewer(String jdrd, String  dev)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
	setJdrawDir(jdrd);
        setDevName(dev);
    }
    
    
    public String getJdrawDir()
    {
	return jdrawDir;
    }  
    
    
    public void setJdrawDir(String newDir)
    {
        if (newDir == null)
	   return;
	if (newDir.length() <= 0)
	   return;
	jdrawDir = new String(newDir);
    }
     
    
    public String getDevName()
    {
	return devName;
    }  
     
    
    public void setDevName(String   dev)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
       String          fullFileName;

       devName = dev;
       if (jdrawDir == null)
          fullFileName =  defaultJdrawDir + "/" + getFileNameFromDev(devName) + ".jdw";
       else
          fullFileName =  jdrawDir + "/" + getFileNameFromDev(devName) + ".jdw";

       tsh = new TangoSynopticHandler(fullFileName);
       
       
    
       // Add tsh into the panel (this)    
       java.awt.GridBagConstraints      gbc;
       gbc = new java.awt.GridBagConstraints();

       gbc.gridx = 0;
       gbc.gridy = 1;
       gbc.insets = new java.awt.Insets(5, 5, 5, 5);
       gbc.fill = java.awt.GridBagConstraints.BOTH;
       gbc.weightx = 1.0;
       gbc.weighty = 1.0;
       
       this.add(tsh, gbc);
    }  

    
    public String getFileNameFromDev(String dev)
    {
        String     devFile;
	int        firstSlash, secondSlash;
	
	if (dev == null)
	   return dev;
	   
	devFile = dev.replace('/', '_');	
	return(devFile);
    }
    
    
    private void initComponents()
    {//initComponents
       this.setMinimumSize(new java.awt.Dimension(22, 22));
       this.setLayout(new java.awt.GridBagLayout());
    }//initComponents
    
     
    /**
    * @param args the command line arguments
    */
    public static void main(String args[])
    {
      DeviceSynopticViewer  dsv;

      JFrame jf = new JFrame();
      // Exit via 'window closing'.
      jf.addWindowListener(  new WindowAdapter()
				 {
				     public void windowClosing(WindowEvent e)
				     {
				       System.exit(0);
				     }
				 });



       try
       {
          dsv = new DeviceSynopticViewer("/users/poncet/ATK_OLD/jloox_files", "id14/eh3_mono/diamond");
          jf.setContentPane(dsv);
       }
       catch (Exception e)
       {
          System.out.println(e);
	  System.out.println("Prog Aborted.");
	  System.exit(-1);
       }
       
       jf.pack();
       jf.setVisible(true);
    }


}

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
 
// File:          DeviceSynopticFrame.java
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


public class DeviceSynopticFrame extends javax.swing.JFrame
{
    private static final String    defaultJdrawDir = "/users/poncet/ATK_OLD/jloox_files";

    private String                 jdrawDir = null;    
    private String                 devName = null;
    private DeviceSynopticViewer   dsv = null;


    public DeviceSynopticFrame()
    {
	initComponents();
    }

 
    public DeviceSynopticFrame(String  dev)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
	jdrawDir = defaultJdrawDir;
        setDevName(dev);
        setContentPane(dsv);
	pack();
	setVisible(true);
    }
  
    public DeviceSynopticFrame(String jdrd, String  dev)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
	setJdrawDir(jdrd);
        setDevName(dev);
        setContentPane(dsv);
	pack();
	setVisible(true);
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
       dsv = new DeviceSynopticViewer(jdrawDir, dev);
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
       addWindowListener(  
                 new WindowAdapter()
                     {
                	  public void windowClosing(java.awt.event.WindowEvent evt)
			  {
                	      exitForm(evt);
                	  }
		      });
    }//initComponents
    
    private void exitForm(java.awt.event.WindowEvent evt)
    {
       this.dispose();
    }
     
    /**
    * @param args the command line arguments
    */
    public static void main(String args[])
    {
       String              deviceName;
       WindowListener[]    wl;
       
       if (args.length <= 0)
          deviceName = "id14/eh3_mono/diamond";
       else
          deviceName = args[0];
	  
       try
       {
	  DeviceSynopticFrame  dsf = new DeviceSynopticFrame(deviceName);
	  wl = dsf.getWindowListeners();
	  for (int i = 0; i < wl.length; i++)
	  {
	     dsf.removeWindowListener(wl[i]);
	     System.out.println("windowListener["+i+"]");
	  }

	  dsf.addWindowListener(
	         new WindowAdapter()
                     {
                	public void windowClosing(java.awt.event.WindowEvent evt)
			{
                	    System.exit(0);
                	}
                     });
       }
       catch (Exception e)
       {
          System.out.println(e);
	  System.out.println("Prog Aborted.");
	  System.exit(-1);
       }
      
    }


}

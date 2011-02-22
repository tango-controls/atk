// File:          DeviceSynopticFrame.java
// Created:       2003-02-18 15:22:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2003-02-18 15:22:29, poncet>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.jloox;


import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.JFrame;

import com.loox.jloox.*;

public class DeviceSynopticFrame extends javax.swing.JFrame
{
    private static final String    defaultJlooxDir = "/users/poncet/ATK_OLD/jloox_files";

    private String                 jlooxDir = null;    
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
	jlooxDir = defaultJlooxDir;
        setDevName(dev);
        setContentPane(dsv);
	pack();
	show();
    }
  
    public DeviceSynopticFrame(String jlxd, String  dev)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
	setJlooxDir(jlxd);
        setDevName(dev);
        setContentPane(dsv);
	pack();
	show();
    }
    
    
    public String getJlooxDir()
    {
	return jlooxDir;
    }  
    
    
    public void setJlooxDir(String newDir)
    {
        if (newDir == null)
	   return;
	if (newDir.length() <= 0)
	   return;
	jlooxDir = new String(newDir);
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
       dsv = new DeviceSynopticViewer(jlooxDir, dev);
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

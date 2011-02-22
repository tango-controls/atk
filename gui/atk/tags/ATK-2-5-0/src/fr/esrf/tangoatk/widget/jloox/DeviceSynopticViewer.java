// File:          DeviceSynopticViewer.java
// Created:       2003-02-17 15:22:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2003-02-17 15:22:29, poncet>
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

public class DeviceSynopticViewer extends javax.swing.JPanel
{
    private static final String    defaultJlooxDir = "/users/poncet/ATK_OLD/jloox_files";

    private String                 jlooxDir = null;    
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
  
    public DeviceSynopticViewer(String jlxd, String  dev)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
	setJlooxDir(jlxd);
        setDevName(dev);
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
       if (jlooxDir == null)
          fullFileName =  defaultJlooxDir + "/" + getFileNameFromDev(devName) + ".jlx";
       else
          fullFileName =  jlooxDir + "/" + getFileNameFromDev(devName) + ".jlx";

       tsh = new TangoSynopticHandler(fullFileName);
       
       
    
       // Add tsh into the panel (this)    
       com.loox.jloox.LxView            lxv = tsh.getJlooxView();
       java.awt.GridBagConstraints      gbc;
       gbc = new java.awt.GridBagConstraints();
       
       if (lxv == null)
          throw new MissingResourceException(
	   "The JLoox file has no component inside. First draw a JLoox File.",
	   "LxGraph", null);

       lxv.fitToGraph(20, 20);
       
       gbc.gridx = 0;
       gbc.gridy = 1;
       gbc.insets = new java.awt.Insets(5, 5, 5, 5);
       gbc.fill = java.awt.GridBagConstraints.BOTH;
       gbc.weightx = 1.0;
       gbc.weighty = 1.0;
       
       this.add(lxv, gbc);
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
       jf.show();
    }


}

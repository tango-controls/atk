// File:          SynopticFileViewer.java
// Created:       2003-02-17 15:22:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2003-02-17 15:22:29, poncet>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.jloox;

/**
 *
 * @author  root
 */
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.JFrame;

import com.loox.jloox.*;
import fr.esrf.tangoatk.widget.util.ErrorHistory;

public class SynopticFileViewer extends javax.swing.JPanel
{

    private String                 jlooxDir = null;    
    private String                 jlooxFileName = null;
    private fr.esrf.tangoatk.widget.jloox.TangoSynopticHandler   tsh = null;
    private ErrorHistory           errorHistWind = null;


    public SynopticFileViewer()
    {
	initComponents();
    }
 
    public SynopticFileViewer(String  jlxf)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
        setJlooxFileName(jlxf);
    }
  
    public SynopticFileViewer(String jlxd, String  jlxf)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
	setJlooxDir(jlxd);
        setJlooxFileName(jlxf);
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
    
    
    public int getToolTipMode() throws IllegalStateException
    {
	if (tsh == null)
	   throw new IllegalStateException ("toto");
	return tsh.getToolTipMode();
    }  
    
   
    public void setToolTipMode(int ttm) throws IllegalStateException
    {
	if (tsh == null)
	   throw new IllegalStateException ("toto");
        tsh.setToolTipMode(ttm);
    }
     
    
    public String getJlooxFileName()
    {
	return jlooxFileName;
    }  
     
    
    public void setJlooxFileName(String   jlxf)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
       String          fullFileName;

       jlooxFileName = jlxf;
       fullFileName =  jlooxDir + "/" + jlooxFileName;

       if (errorHistWind != null)
          tsh = new fr.esrf.tangoatk.widget.jloox.TangoSynopticHandler(fullFileName, errorHistWind);
       else
          tsh = new fr.esrf.tangoatk.widget.jloox.TangoSynopticHandler(fullFileName);
       
       
    
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

    public void setErrorWindow(ErrorHistory  errw) throws IllegalStateException
    {
	if (tsh != null)
	   throw new IllegalStateException ("set error window before setJlooxFileName");
	   
        if (errw != null)
	   errorHistWind = errw;
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
      SynopticFileViewer  sfv;

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
          sfv = new SynopticFileViewer("/users/poncet/ATK_OLD/jloox_files", "id14_4.jlx");
          jf.setContentPane(sfv);
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

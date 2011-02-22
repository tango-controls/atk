// File:          SynopticFileViewer.java
// Created:       2004-11-09 15:22:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2004-11-09 15:22:29, poncet>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.jdraw;

/**
 *
 * @author  root
 */
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.JFrame;

import fr.esrf.tangoatk.widget.util.ErrorHistory;

public class SynopticFileViewer extends javax.swing.JPanel
{

    private String                 jdrawDir = null;    
    private String                 jdrawFileName = null;
    private fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler   tsh = null;
    private ErrorHistory           errorHistWind = null;


    public SynopticFileViewer()
    {
	initComponents();
    }
 
    public SynopticFileViewer(String  jdrawf)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
        setJdrawFileName(jdrawf);
    }
  
    public SynopticFileViewer(String jdrawd, String  jdrawf)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
        this();
	setJdrawDir(jdrawd);
        setJdrawFileName(jdrawf);
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
     
    
    public String getJdrawFileName()
    {
	return jdrawFileName;
    }  
     
    
    public void setJdrawFileName(String   jdrawf)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
       String          fullFileName;

       jdrawFileName = jdrawf;
       fullFileName =  jdrawDir + "/" + jdrawFileName;

       if (errorHistWind != null)
          tsh = new fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler(fullFileName, errorHistWind);
       else
          tsh = new fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler(fullFileName);
       
       
    
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

    public void setErrorWindow(ErrorHistory  errw)
    {	   
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

       ErrorHistory errorHistory = new ErrorHistory();

       try
       {
          sfv = new SynopticFileViewer();
	  sfv.setErrorWindow(errorHistory);
          sfv.setJdrawDir("/users/poncet/ATK_OLD/jloox_files");
          //sfv.setJdrawFileName("id14.jlx");
          sfv.setJdrawFileName("id14_4.jlx");
	  sfv.setToolTipMode(TangoSynopticHandler.TOOL_TIP_NAME);
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

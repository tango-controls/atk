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

public class SynopticFileViewer extends TangoSynopticHandler
{

    private String                 jdrawDir = null;    
    private String                 jdrawFileName = null;


    public SynopticFileViewer()
    {
	super();
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
	String   fn = null;
	
	fn = getSynopticFileName();
	
	if (fn != null)
	{
	   int  indSlash = fn.lastIndexOf('/');
	   try
	   {
	      jdrawDir = fn.substring(0, indSlash-1);
	   }
	   catch (Exception ex)
	   {
	      jdrawDir = null;
	   }
	}
	return jdrawDir;
    }  
    
    
    public void setJdrawDir(String newDir)
    {
	String   fn = null;
	
	fn = getSynopticFileName();
	
	if (fn != null)
	   return;

	if (newDir == null)
	   return;
	if (newDir.length() <= 0)
	   return;
	jdrawDir = new String(newDir);
    }
    
    
   
     
    
    public String getJdrawFileName()
    {
	String   fn = null;
	
	fn = getSynopticFileName();
	
	if (fn == null)
	   jdrawFileName = null;
	else
	{
	   int  indSlash = fn.lastIndexOf('/');
	   int  fnSize = fn.length();
	   try
	   {
	      jdrawFileName = fn.substring(indSlash+1, fnSize-1);
	   }
	   catch (Exception ex)
	   {
	      jdrawFileName = null;
	   }
	}
	return jdrawFileName;
    }  
     
    
    public void setJdrawFileName(String   jdrawf)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
    {
       String          fullFileName;
       
       if (jdrawf == null)
       {
           clearDirAndFileNames();
	   return;
       }

       jdrawFileName = jdrawf;
       
       if (jdrawFileName.startsWith("/"))
       {
          fullFileName = jdrawFileName;
       }
       else
          if (getJdrawDir() == null)
              fullFileName = jdrawFileName;
          else
              fullFileName =  getJdrawDir() + "/" + jdrawFileName;
       
       setSynopticFileName(fullFileName);

    }  
    
    public void clearDirAndFileNames()
    {
        jdrawDir = null;
	jdrawFileName = null;
    }


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
       sfv = new SynopticFileViewer();

       try
       {
	  sfv.setErrorHistoryWindow(errorHistory);
	  sfv.setToolTipMode(TangoSynopticHandler.TOOL_TIP_NAME);
	  sfv.setAutoZoom(true);
          sfv.setJdrawDir("/segfs/tango/jclient/JMultiBunchFdbk/src/mbFeedBack");
          sfv.setJdrawFileName("mfdbk.jdw");
          jf.setContentPane(sfv);
       }
       catch (Exception e)
       {
          System.out.println(e);
	  System.out.println("Prog Aborted.");
	  System.exit(-1);
       }
       
       jf.pack();
       jf.setVisible(true);
       try
       {
          Thread.sleep(10000);
	  System.out.println("coucou");
	  sfv.setJdrawFileName("/segfs/tango/jclient/JBpss/jdraw_files/Bpss.jdw");

          Thread.sleep(10000);
	  System.out.println("coucou");
	  sfv.setJdrawFileName("/segfs/tango/jclient/JMultiBunchFdbk/src/mbFeedBack/mfdbk.jdw");
       }
       catch (Exception ex)
       {
       }
    }


}

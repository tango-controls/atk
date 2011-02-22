/*
 * JSmoothDelayDialog.java
 * Author: Faranguiss PONCET
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.*;
import javax.swing.*;

public class JSmoothDelayDialog extends JDialog
{

    // Local declarations
    static private final int    REFRESH_DELAY = 100;// Unit is ms.
    private JSmoothProgressBar  jp = null;
    private JSmoothDelayDialog  selfref = null;
    private int                 delay = 0;// Unit is ms.
    
    
    /** Constructor takes JDialog parent, title */
    public JSmoothDelayDialog(JDialog owner, String title)
    {
        super(owner,title, true);
        init_component(0);
	selfref = this;
    }
    
    
    /** Constructor takes JDialog parent, title, delay in milli seconds arguments */
    public JSmoothDelayDialog(JDialog owner, String title, int d)
    {
        super(owner,title, true);
        init_component(d);
	selfref = this;
    }

    /** Constructor takes Frame parent, title, delay in milli seconds arguments */
    public JSmoothDelayDialog(Frame owner, String title, int d)
    {
        super(owner,title, true);
        init_component(d);
	selfref = this;
    }

    private void init_component(int d)
    {
	delay = d;
	jp = new JSmoothProgressBar();
	jp.setValue(0);
	jp.setMaximum(delay);
	jp.setStringPainted(true);
	this.getContentPane().add(jp);
    }
    
    public int getDelay()
    {
       return delay;
    }
    
    public void setDelay (int  d) // d unit is millisecond
    {
       if (d > 0)
       {
          delay = d;
	  jp.setValue(0);
	  jp.setMaximum(delay);
       }
    }
    
    
    

    public void start() //strats a thread to update the progressbar while waiting for the delay
    {
        
	new Thread()
	    {
	       public void run()
	       {
		   try 
		   {
		       for (int i = 0; i < delay / REFRESH_DELAY; i++)
		       {
	        	   Thread.sleep(REFRESH_DELAY);
			   jp.setValue(i * REFRESH_DELAY);
			   //jp.setString( (int)(((double)(i * REFRESH_DELAY) / delay) * 100.0) + "%");
			   selfref.repaint();
		       }
		       selfref.dispose();

		   } catch (InterruptedException i)
		   {
		   }
	       }
	    }.start();		// Updater Thread.
	    
	this.show();		// GUI Thread.
    }



    static public void main(String[] args)
    {
        //JFrame        jf = new JFrame();
        JSmoothDelayDialog JD = new JSmoothDelayDialog((JFrame) null,"Attente", 10000);
	JD.setSize(200, 50);
	//jf.pack();
	//jf.show();
	//JD.show();
	JD.start();
    }

}

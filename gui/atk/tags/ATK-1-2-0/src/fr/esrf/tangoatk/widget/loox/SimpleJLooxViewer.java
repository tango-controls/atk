/*
 * SimpleJLooxViewer.java
 *
 * Created on October 1st, 2002, 9:42 AM
 */

package fr.esrf.tangoatk.widget.loox;

/**
 *
 * @author  root
 */
import java.awt.event.*;

import com.loox.jloox.*;

public class SimpleJLooxViewer extends javax.swing.JFrame
{

    static String                  synopDir="/users/poncet/ATK/jloox_files";
    
    
    String                         drawingFile="/users/poncet/ATK/jloox_files/id14bis.jlx";
    SimpleJLooxAdapter             sjlx;
    extendedJLM.ExtendedGraph      lxg;
    LxView                         lxv;

    /** Creates new frame SimpleJLooxViewer */

    public SimpleJLooxViewer()
    {
        initComponents();
		
	try
	{
	   sjlx.setViewer(lxv);
	}
	catch (Exception e)
	{
	   System.out.println(e);
	   System.out.println("Prog Aborted.");
	   System.exit(-1);
	}
    }
 
    public SimpleJLooxViewer(String  devname)
    {
        this();
		
	try
	{
	   setModel(devname);
	}
	catch (Exception e)
	{
	   System.out.println(e);
	   System.out.println("Prog Aborted.");
	   System.exit(-1);
	}
	
	pack();
	show();
    }
   
    

    public void setModel(String  devname)
    {
System.out.println("SimpleJLooxViewer.setModel("+devname+") called.");
	drawingFile = drawingFileName(devname);
	
	try
	{
	   sjlx.setViewer(lxv);
	   sjlx.setModel(drawingFile);
	}
	catch (Exception e)
	{
	   System.out.println(e);
	   System.out.println("Prog Aborted.");
	   System.exit(-1);
	}
        lxv.fitToGraph(20, 20);
    }
    
    public String drawingFileName(String dev)
    {
        String     devFile;
	String     devCompleteFileName;
	int        firstSlash, secondSlash;
	
	if (dev == null)
	   return ("/users/poncet/ATK/jloox_files/id14bis.jlx");
	   
	devFile = dev.replace('/', '_');
	devCompleteFileName = new String(synopDir);
	devCompleteFileName = devCompleteFileName.concat("/");
	devCompleteFileName = devCompleteFileName.concat(devFile);
	devCompleteFileName = devCompleteFileName.concat(".jlx");
	
	return(devCompleteFileName);
    }
    
    
    public static void setSynopticDir(String newDir)
    {
        if (newDir == null)
	   return;
	   
	if (newDir.length() <= 0)
	   return;
	   
	synopDir = new String(newDir);
    }


    private void initComponents()
    {//initComponents
    
       addWindowListener(new WindowAdapter()
			       {
				  public void windowClosing(WindowEvent evt)
				  {
				     exitForm(evt);
			          }
			       }
			);

       sjlx = new SimpleJLooxAdapter();
       lxg = new extendedJLM.ExtendedGraph();
       lxv = new LxView();
       lxv.setGraph(lxg);
       lxv.fitToGraph(20, 20);
       setContentPane(lxv);
    }//initComponents
    
       
    /** Exit the Application */
    private void exitForm(WindowEvent evt)
    {
	   this.dispose();
    }
    
     
    /**
    * @param args the command line arguments
    */
    public static void main(String args[])
    {
       SimpleJLooxViewer  sjlv;
       
       if (args.length <= 0)
          sjlv = new SimpleJLooxViewer("id14/eh3_mono/diamond");
       else
	  sjlv = new SimpleJLooxViewer(args[0]);
       
    }


}

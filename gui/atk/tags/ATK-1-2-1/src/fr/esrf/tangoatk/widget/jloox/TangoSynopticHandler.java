
// File:          TangoSynopticHandler.java
// Created:       2003-02-17 15:22:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2003-02-17 15:22:29, poncet>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.jloox;

import com.loox.jloox.*;

import fr.esrf.Tango.DevFailed;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;
import fr.esrf.tangoatk.core.command.*;
import fr.esrf.tangoatk.widget.device.StateViewer;
import fr.esrf.tangoatk.widget.command.AnyCommandViewer;

import atkpanel.MainPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;




 /**
  * TangoSynopticHandler is the base class used to display and animate any
  * tango synoptic drawing made with the JLoox drawing tool "JLooxMaker".
  *
  * The drawing file is browsed and a behaviour is attached to each drawing
  * component according to the tango object which is associated with.
  *
  * While brawsing the drawing file the name of the graphical component
  * determines the associated tango object:
  *
  * For example if inside the synoptic drawing a simple rectangle is given
  * the name "eas/test-api/1" it will be associated to the tango device
  * eas/test-api/1.
  *
  * The animation on the graphical component depends on the type of the
  * Tango object (device, attribute, command) and the type of JLoox graphical
  * component (simple graphical component, multi-state Dyno, push-button Dyno)
  *
  * Here are the default animations provided :
  * <ul>
  * <li>Tango device - simple graphical component : the colour of the graphical
  *  component represents the state of the tango device (on, off, alarm, fault...)
  * <li>Tango device - multi-state Dyno : the form of the dyno represents the
  * tango device state (on, off, alarm, fault, ...)
  * <li>Tango device command - push-button Dyno : no animation
  * </ul>
  * <p>
  * In addition to the animation a default interaction behaviour is
  * provided (reaction to mouse clicks). Here are the default interactions :
  *
  * <ul>
  * <li>Tango device - simple graphical component : Click on the graphical component
  * will launch a java class whose name has been specified during the drawing phase.
  * If this class name is missing, the generic panel class for the Tango Device
  * class is launched and if this panel does not exist the generic tool atkpanel is
  * launched.
  * <li>Tango device - multi-state Dyno : the same interaction model
  * <li>Tango device command - push-button Dyno : Click on the pushbutton will display
  * an "input / output argument window" if the input is required or execute the
  * associated command on the tango device if no input is required.
  * </ul>
  * 
  * @author      Faranguiss  PONCET
  * @since       ATKWidget-1.2.1
  */
  

public class TangoSynopticHandler implements IStateListener
{
   private static final String      TACO_HEADER = "taco:";
   
   private static Map               dynoState;
   
   private    LxAbstractGraph       graph = null;
   private    LxView                view = null;
   private    String                jlooxFileName = null;
   
   private    AttributeFactory      aFac = null;
   private    CommandFactory        cFac = null;
   private    DeviceFactory         dFac = null;

   private    AttributeList         lxAtl = null;
   private    List                  lxDevl = null;
   private    LxComponent[]         lxComps = null;
   
   private    Map                   lxHash;
   private    Map                   panelHash;

   private    AnyCommandViewer      acv = null; 
   private    JFrame                argFrame = null;
      

   static
   {
       dynoState = new HashMap();
       dynoState.put("UNKNOWN", new Integer(0));
       dynoState.put("OFF",     new Integer(1));
       dynoState.put("CLOSE",   new Integer(1));
       dynoState.put("EXTRACT", new Integer(1));
       dynoState.put("INIT",    new Integer(1));
       dynoState.put("DISABLE", new Integer(1));
       dynoState.put("ON",      new Integer(2));
       dynoState.put("OPEN",    new Integer(2));
       dynoState.put("INSERT",  new Integer(2));
       dynoState.put("ALARM",   new Integer(3));
       dynoState.put("FAULT",   new Integer(4));
       dynoState.put("MOVING",  new Integer(5));
       dynoState.put("RUNNING", new Integer(5));
       dynoState.put("STANDBY", new Integer(6));
   }


   public TangoSynopticHandler()
   {
      esrfSynopticJLM.ExtendedGraph lxg = new esrfSynopticJLM.ExtendedGraph();
      LxView                    lxv = new LxView();
      
      lxv.setGraph(lxg);
      graph = lxg;
      view = lxv;
      
      aFac = AttributeFactory.getInstance();
      cFac = CommandFactory.getInstance();
      dFac = DeviceFactory.getInstance();
//      lxAtl = new AttributeList();
//      lxDevl = new Vector();
   }
   


   public TangoSynopticHandler(String  jlxFileName)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
   {
      this();
      setJlooxFileName(jlxFileName);
   }


   public com.loox.jloox.LxView getJlooxView()
   {
      return (view);
   }

   
   public String getJlooxFileName() 
   {
      return (jlooxFileName);
   }


   

// Read the JLoox file and browse and parse the synoptic components 
// ----------------------------------------------------------------
   
   public void setJlooxFileName( String  jlxFileName) 
               throws MissingResourceException, FileNotFoundException, IllegalArgumentException
   {
      lxHash = new HashMap();
// Here should disconnect from all attributes and devices in the previous
// Loox file.
      
      FileReader  f_read = new FileReader(jlxFileName);
      String      errStr = graph.read(jlxFileName);
      if (errStr != null)
         throw new IllegalArgumentException(errStr);

      lxComps = null;
      lxComps = graph.getComponents();
      
      if ( (graph.getComponentCount() == 0) || (lxComps == null))
         throw new MissingResourceException(
	   "The JLoox file has no component inside. First draw a JLoox File.",
	   "LxGraph", null);
      parseJlooxComponents(lxComps);
// not needed : automatically started in dFac class      dFac.startRefresher();
   }

   
   protected void parseJlooxComponents(LxComponent alxcomponent[])
   {
      for (int i = 0; i < alxcomponent.length; i++)
      {
	 LxComponent component = alxcomponent[i];
	 String s = component.getName();

	 if (isAttribute(s))
	 {
	     addAttribute(component, s);
	 }
	 else
	 {
	    if (isCommand(s))
	    {
	       addCommand(component, s);
	    }
	    else
	    {
               if (isDevice(s))
	       {
	          addDevice(component, s);
	       }
	       else
	       {
		  /*
		  if (isTacoDevice(s))
		  {
		    addDevice(component, TACO_HEADER+s);
		  }
		  else
		  {
		    System.out.println(s+" is not a TACO device; ignored.");
		  }
		  else
		  */
	          //System.out.println(s+" is not an attribute, nor a command, nor a device; ignored.");
	       }
	    }
	 }
      } /* for */

   }


   protected boolean isAttribute(String s)
   {
       return aFac.isAttribute(s);
   }

   protected boolean isCommand(String s)
   {
       return cFac.isCommand(s);
   }

   protected boolean isDevice(String s)
   {
       return dFac.isDevice(s);
   }


   protected boolean isTacoDevice(String s)
   {
       boolean   devNamePattern = Pattern.matches(".*/.*/.*", s);
       
       if (devNamePattern)
       { // Handle a TACO device if possible
	   System.out.println(s+" is a sort of device name.");
	   String   tacoName= TACO_HEADER+s;
           return dFac.isDevice(tacoName);
       }
       
       return false;
   }





// Adding a device 
// ---------------

   private void addDevice(LxComponent lxcomp, String s)
   {
      try
      {
	 addDeviceListener(dFac.getDevice(s));
	 mouseify(lxcomp, s);
	 stashComponent(s, lxcomp);
      } catch (ConnectionException connectionexception)
      {
	 System.out.println("Couldn't load device " + s + " " +
			    connectionexception);
      }
   }
 
   private void addDeviceListener(Device device)
   {
      System.out.println("connecting to " + device);
      device.addStateListener(this);
   }
   

   private void mouseify(LxComponent lxc, String name)
   {   

      /* Attach a mouse listener to the device component for mouse press */
      lxc.addMouseListener(
             new LxMouseAdapter()
	     {
		public void mousePressed(LxMouseEvent e)
		{
		   deviceClicked(e);
		}
		
             });
   }
    
    
   private void deviceClicked(LxMouseEvent evt)
   {
      esrfSynopticJLM.ExtendedData  panelInfo;
      
      LxComponent  comp = evt.getLxComponent();
      String       devName = comp.getName();     // The name of the device
      Object       extension = comp.getUserData(); // The user data structure
      String       clName = null;
      String       constParam = null;

      if (extension != null)  // Check if the panel class is well specified to instantiate it
      {
	 if (extension instanceof esrfSynopticJLM.ExtendedData)
	 {
	    panelInfo = (esrfSynopticJLM.ExtendedData) extension;
	    clName = panelInfo.getClassName();
	    if (clName != null)
	    {
	       constParam = panelInfo.getConstParameter();
	    }
	    else // By default instantiate atkpanel generic panel appli for all devices
	    {
	       clName = "atkpanel.MainPanel";
	       constParam = null;
	    }
	 }
	 else // By default instantiate atkpanel generic panel appli for all devices
	 {
	    clName = "atkpanel.MainPanel";
	    constParam = null;
	 }
      }
      else // By default instantiate atkpanel generic panel appli for all devices
      {
	 clName = "atkpanel.MainPanel";
	 constParam = null;
      }


System.out.println("clName = "+clName+"  constParam = "+constParam+ " devName = " + devName);


      Class        panelCl;
      Constructor  panelClNew;
      Class[]      paramCls = new Class[1];
      Object[]     params = new Object[1];
     
      try // Load the class and the constructor of the device panel
      {
         panelCl = Class.forName(clName);
         paramCls[0] = clName.getClass();
         panelClNew = panelCl.getConstructor(paramCls);
      }
      catch (ClassNotFoundException clex)
      {
	 JOptionPane.showMessageDialog(null, "The panel class : "+clName+" not found; ignored.\n");
	 return;
      }
      catch (Exception e)
      {
	 JOptionPane.showMessageDialog(null, "Failed to load the constructor "+clName+"( String ) for the panel class.\n");
	 return;
      }
 

      // Passe either the constructor parameter specified by the user or
      // the device name to the class constructor
      if (constParam == null)
      {
         params[0] = devName;
      }
      else
      {
         if( constParam.length() > 0 )
	 {
            params[0] = constParam;
	 }
	 else
	    params[0] = devName;
      }
System.out.println("params[0]= "+ params[0]);
      
      
      
      try // Instantiate the device panel class
      {
         panelClNew.newInstance(params);
      }
      catch (InstantiationException  instex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 1 the panel class : "+clName+".\n");
      }
      catch (IllegalAccessException  accesex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 2 the panel class : "+clName+".\n");
      }
      catch (IllegalArgumentException argex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 3 the panel class : "+clName+".\n");
      }
      catch (InvocationTargetException invoqex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 4 the panel class : "+clName+".\n");
System.out.println(invoqex);
System.out.println(invoqex.getMessage());
invoqex.printStackTrace();
      }
      catch (Exception e)
      {
	 JOptionPane.showMessageDialog(null, "Got an exception when instantiate the panel class : "+clName+".\n");
      }
      
   }
   

   private void stashComponent(String s, LxComponent lxcomp)
   {
      List list = (List)lxHash.get(s);
      if (list == null)
	  list = new Vector();
      list.add(lxcomp);
      lxHash.put(s, list);
   }




// Adding a command 
// ----------------

   private void addCommand(LxComponent lxcomp, String s)
   {
      if (lxcomp instanceof LxPushButton)
	 addCommmand((LxPushButton)lxcomp, s);
   }


   private void addCommmand(LxPushButton lxpb, String s)
   {
      ICommand  cmd = null;

      try
      {
         cmd = cFac.getCommand(s);
	 if (cmd != null)
	    mouseify(lxpb, cmd);
      } 
      catch (ConnectionException connectionexception)
      {
	 System.out.println("Couldn't load device for command" + s + " " + connectionexception);
      }
      catch (DevFailed dfEx)
      {
	 System.out.println("Couldn't find the command" + s + " " + dfEx);
      }

   }
   

   private void mouseify(LxPushButton lxpb, ICommand devCmd)
   {   
      
      final  ICommand  cmd = devCmd;  
      /* Attach a mouse listener to the lxpushbutton component for mouse press */
      lxpb.addVariableListener (
	     new LxVariableListener()
        	{
		   public void valueChanged(LxVariableEvent e)
		   {
		      LxVariable  lv=null;
		      
		      lv = e.getVariable();
		      if (lv instanceof LxPushButton)
		      {
		         LxPushButton  pb = null;
			 pb = (LxPushButton) lv;
			 if (pb.getState() == true)
			 {
	        	    System.out.println("Acommand is " + cmd);
		            commandClicked(cmd);
			 }
		      }
        	   }
        	});
   }
 
   
   private void commandClicked(ICommand  ic)
   {
      if (ic instanceof InvalidCommand)
      {
	  javax.swing.JOptionPane.showMessageDialog(view, ic.getName() + " is not supported. ", "Error", 1);
	  return;
      }
	
      if (ic instanceof VoidVoidCommand)
      {
	  ic.execute();
	  return;
      }

      if ( acv==null )
      {
	  argFrame = new JFrame();
	  acv      = new AnyCommandViewer();
	  argFrame.getContentPane().add(acv);
      }
      
      acv.initialize(ic);
      acv.setBorder(null);
      acv.setInputVisible(true);	     
      acv.setDeviceButtonVisible(true);
      acv.setDescriptionVisible(true);
      acv.setInfoButtonVisible(true);
      
      if (!ic.takesInput())
      {
	  ic.execute();
      } 

      argFrame.setTitle(ic.getName());
      argFrame.pack();
      argFrame.show();
   }






// Adding an attribute
// -------------------

   private void addAttribute(LxComponent lxcomp, String s)
   {
   }

   





// Implement the interface methods for synoptic animation 
// -------------------------------------------------------

   // Interface IErrorListener (superclass of IStateListener)
   public void errorChange(ErrorEvent evt)
   {
   }


   // Interface IStateListener
   public void stateChange(StateEvent event)
   {
      LxComponent   lxComp;

      Device device = (Device)event.getSource();
      String s = device.getName();

      List list = (List) lxHash.get(s);
      if (list == null)
         return;
	 
//System.out.println("stateChange : device name = "+s);    
//System.out.println("stateChange : device state = "+event.getState());    
      int  nbLxComps = list.size();
      int  i;
      
      for (i=0; i<nbLxComps; i++)
      {
         lxComp = (LxComponent) list.get(i);
	 
	 if (lxComp instanceof LxElement)
	 {
	    if (((LxElement) lxComp).getPaint().getTransparency() == java.awt.Paint.OPAQUE)
	       ((LxElement) lxComp).setPaint(StateViewer.getColor4State(event.getState()));
	    else
	       ((LxElement) lxComp).setLineColor(StateViewer.getColor4State(event.getState()));
	 }
	 else
	 {
	    if (lxComp instanceof LxGroup)
	    {
	        groupChangeColor((LxGroup)lxComp, event.getState());
	    }
	    else
	    {
	       if (lxComp instanceof LxMultiState)
	          multiStateDynoChange((LxMultiState)lxComp, event.getState());
	    }
	 }
      }
   }
   

   private void groupChangeColor(LxGroup  lxg, String state)
   {
      if (lxg == null)
         return;
	 
      LxComponent[]  all_lxc = lxg.getComponents();
      int            nb_elem = lxg.getComponentCount();
      
      int  i;
      
      for (i=0; i<nb_elem; i++)
      {
         LxComponent  lxc = all_lxc[i];
	 
	 if (lxc instanceof LxElement)
	 {
	    if (((LxElement)lxc).getPaint() == null)
	    {
	       ((LxElement)lxc).setLineColor(StateViewer.getColor4State(state));
	    }
	    else
	    {
	       if (((LxElement)lxc).getPaint().getTransparency() == java.awt.Paint.OPAQUE)
		  ((LxElement)lxc).setPaint(StateViewer.getColor4State(state));
	       else
		  ((LxElement)lxc).setLineColor(StateViewer.getColor4State(state));
	    }
	 }
	 else
	 {
	    if (lxc instanceof LxGroup)
	        groupChangeColor((LxGroup)lxc, state);
	    else
	    {
	       if (lxc instanceof LxMultiState)
	          multiStateDynoChange((LxMultiState)lxc, state);
	    }
	 }
	 
      }
   }
   


   private int getDynoState(String deviceState)
   {  
      Integer   intObj;
      
      intObj = (Integer) dynoState.get(deviceState);
      return (intObj.intValue());
   }
   


   private void multiStateDynoChange(LxMultiState  lxms, String state)
   {
      int    dynoState;
      
      dynoState = getDynoState(state);
      lxms.setValue(dynoState);
   }




   public static void main(String args[])
   {
      TangoSynopticHandler     tsh = new TangoSynopticHandler();
      
      try
      {
	 tsh.setJlooxFileName("/users/poncet/ATK_OLD/jloox_files/id14_4.jlx");
      }
      catch (Exception e)
      {
         System.out.println(e);
	 System.out.println("Prog Aborted.");
	 System.exit(-1);
      }

      JFrame jf = new JFrame();
      // Exit via 'window closing'.
      jf.addWindowListener(  new WindowAdapter()
				 {
				     public void windowClosing(WindowEvent e)
				     {
				       System.exit(0);
				     }
				 });
      
      com.loox.jloox.LxView  lxv = tsh.getJlooxView();
      lxv.fitToGraph(20, 20);
      jf.setContentPane(lxv);
      jf.pack();
      jf.show();
   }


}

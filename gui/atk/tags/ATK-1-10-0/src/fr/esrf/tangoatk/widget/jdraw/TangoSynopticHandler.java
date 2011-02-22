// File:          TangoSynopticHandler.java
// Created:       2004-10-13 16:45:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2004-10-13 16:45:29, poncet>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.widget.jdraw;


import fr.esrf.Tango.DevFailed;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;
import fr.esrf.tangoatk.core.command.*;
import fr.esrf.tangoatk.widget.device.StateViewer;
import fr.esrf.tangoatk.widget.command.AnyCommandViewer;
import fr.esrf.tangoatk.widget.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

import fr.esrf.tangoatk.widget.util.jdraw.*;


 /**
  * TangoSynopticHandler is the base class used to display and animate any
  * tango synoptic drawing made with the JDraw drawing tool "JDraw".
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
  * Tango object (device, attribute, command) and the type of JDraw graphical
  * component (simple graphical component, multi-state JDraw object, interactive JDraw object)
  *
  * Here are the default (state) animations provided :
  * <ul>
  * <li>Tango device - simple graphical component : the colour of the graphical
  *  component represents the state of the tango device (on, off, alarm, fault...)
  * <li>Tango device - multi-state JDraw object : the form of the JDraw Object represents the
  * tango device state (on, off, alarm, fault, ...)
  * <li>Tango device command - interactive JDraw object : no state animation
  * <li>Tool Tip : A tooltip can be associated to any tango device. The tooltip can
  * display either the name of the device or it's state according to the tooltip mode used.
  * </ul>
  * <p>
  * In addition to the animation a default interaction behaviour is
  * provided (reaction to mouse clicks). Here are the default interactions :
  *
  * <ul>
  * <li>Tango device - simple graphical component : Click on the graphical component
  * will launch a java class whose name has been specified during the drawing phase.
  * If this class name is missing, the generic tool atkpanel is launched.
  * <li>Tango device - multi-state JDraw object : the same interaction model
  * <li>Tango device command - interactive JDraw object : Click on the JDraw object will display
  * an "input / output argument window" if the input is required or execute the
  * associated command on the tango device if no input is required.
  * </ul>
  *
  * @author      Faranguiss  PONCET
  * @since       ATKWidget-1.9.7
  */


public class TangoSynopticHandler extends JDrawEditor
                                  implements IStateListener, IStatusListener,
                                             INumberScalarListener
{

   public static final int          TOOL_TIP_NONE = 0;
   public static final int          TOOL_TIP_STATE = 1;
   public static final int          TOOL_TIP_STATUS = 2;
   public static final int          TOOL_TIP_NAME = 3;



   private static final String      TACO_HEADER = "taco:";
   private static final int         STATE_INDEX = 0;
   private static final int         STATUS_INDEX = 1;

   private static Map               dynoState;

   private    int                   toolTipMode;
   //private    JDrawEditor           graph = null;
   private    String                jdrawFileName = null;

   private    AttributeFactory      aFac = null;
   private    CommandFactory        cFac = null;
   private    DeviceFactory         dFac = null;

   private    AttributeList         jdAtl = null;
   private    List                  jdDevl = null;

   private    Map                   jdHash;
   private    Map                   stateCashHash;
   private    Map                   panelHash;

   private    AnyCommandViewer      acv = null;
   private    JFrame                argFrame = null;

   private    ErrorHistory          errorHistWind = null;


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

      super(JDrawEditor.MODE_PLAY);
      toolTipMode = TOOL_TIP_NONE;
      jdrawFileName = null;

      aFac = AttributeFactory.getInstance();
      cFac = CommandFactory.getInstance();
      dFac = DeviceFactory.getInstance();
//      jdAtl = new AttributeList();
//      jdDevl = new Vector();
   }



   public TangoSynopticHandler(String  jdFileName)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
   {
      this();

/*
      boolean b;
      b = isDevice ("toto");
      b = isDevice ("toto:tata");
      b = isDevice ("toto/tata");
      b = isDevice ("toto:tata/titi");
      b = isDevice ("toto:tata/titi/tutu");
      b = isDevice ("//popo");
      b = isDevice ("//popo/toto:tata");
      b = isDevice ("//popo/toto/tata");
      b = isDevice ("//popo/toto:tata/titi");
      b = isDevice ("//popo/toto/tata/titi");
      b = isDevice ("//popo/toto:tata/titi/tutu");
      b = isDevice ("//popo:kkkkl/toto/tata/titi");
      b = isDevice ("");
      b = isDevice (":");
      b = isDevice ("/");
      b = isDevice (":/");
      b = isDevice ("/dd/");
      b = isDevice (":/dd/");
      b = isDevice ("//");
      b = isDevice ("///:");
      b = isDevice ("////");
      b = isDevice ("///:/");
      b = isDevice ("///://");
      b = isDevice ("/////");
      b = isDevice ("//:///");
      b = isDevice ("//:102///");

      b = isDevice ("toto/tata/titi");
      b = isDevice ("toto-dd/tata-dd/titi-dd");
      b = isDevice ("taco:tata/titi/tutu");
      b = isDevice ("//popo:102/toto/tata/titi");
      b = isDevice ("//popo:102/toto-dd/tata-dd/titi-dd");

      b = isDevice ("toto/tata/titi/aaaa");
      b = isDevice ("taco:tata/titi/tutu/aaaaa");
      b = isDevice ("//popo:102/toto/tata/titi/aaaaa");
*/


      setSynopticFileName(jdFileName);
   }


   public TangoSynopticHandler(String  jdFileName, ErrorHistory errh)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
   {
      this();

      if (errh != null)
         errorHistWind = errh;

      setSynopticFileName(jdFileName);
   }




   public TangoSynopticHandler(String  jdFileName, int ttMode)
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
   {
      this();

      if (     (ttMode == TOOL_TIP_NONE)    ||  (ttMode == TOOL_TIP_STATE)
           ||  (ttMode == TOOL_TIP_STATUS)  ||  (ttMode == TOOL_TIP_NAME)   )
	  toolTipMode = ttMode;

      setSynopticFileName(jdFileName);
   }



   public int getToolTipMode()
   {
     return(toolTipMode);
   }



   public void setToolTipMode( int  ttMode)
   {

      if (     (ttMode == TOOL_TIP_NONE)    ||  (ttMode == TOOL_TIP_STATE)
           ||  (ttMode == TOOL_TIP_STATUS)  ||  (ttMode == TOOL_TIP_NAME)   )
      {
	  if (toolTipMode != ttMode)
	  {
	     toolTipMode = ttMode;
	  }
      }

   }


   public String getSynopticFileName()
   {
      return (jdrawFileName);
   }




// Read the Jdraw file and browse and parse the synoptic components
// ----------------------------------------------------------------

   public void setSynopticFileName( String  jdFileName)
               throws MissingResourceException, FileNotFoundException, IllegalArgumentException
   {
      jdHash = new HashMap();
      stateCashHash = new HashMap();
      // Here should disconnect from all attributes and devices in the previous
      // Jdraw file.

      try
      {
         loadFile(jdFileName);
      }
      catch (IOException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }

      if (getObjectNumber() == 0)
         throw new MissingResourceException(
             "The Jdraw file has no component inside. First draw a Jdraw File.",
             "JDrawEditor", null);

      jdrawFileName = jdFileName;

      parseJdrawComponents();
      computePreferredSize();
// not needed : automatically started in dFac class      dFac.startRefresher();
   }


   protected void parseJdrawComponents()
   {


      for (int i = 0; i < getObjectNumber(); i++)
      {
        JDObject jdObj = getObjectAt(i);
	 String s = jdObj.getName();

         if (isDevice(s))
	 {
	    addDevice(jdObj, s);
	 }
	 else
	 {
	    if (isCommand(s))
	    {
	       addCommand(jdObj, s);
	    }
	    else
	    {
	       if (isAttribute(s))
	       {
		   addAttribute(jdObj, s);
	       }
 	       else
	       {
	          //System.out.println(s+" is not an attribute, nor a command, nor a device; ignored.");
	       }
	    }
	 }
      } /* for */

   }



   protected boolean isAttribute(String s)
   {
       String     attDevName, attName;
       int        lastSlash;
       boolean    isdev;

       lastSlash = s.lastIndexOf("/");

       if ( (lastSlash <= 0) || (lastSlash >= s.length()) )
          return false;

       try
       {
	  attDevName = s.substring(0, lastSlash);

	  isdev = isDevice(attDevName);

	  if (isdev == false)
             return false;

	  attName = s.substring(lastSlash, s.length());

	  boolean   attPattern;

	  attPattern = Pattern.matches("/[a-zA-Z_0-9[-]]+", attName);
	  if (attPattern == false)
	     return false;
	  else
             return aFac.isAttribute(s);
       }
       catch (IndexOutOfBoundsException ex)
       {
          return false;
       }
   }


   protected boolean isCommand(String s)
   {
       String     cmdDevName, cmdName;
       int        lastSlash;
       boolean    isdev;

       lastSlash = s.lastIndexOf("/");

       if ( (lastSlash <= 0) || (lastSlash >= s.length()) )
          return false;

       try
       {
	  cmdDevName = s.substring(0, lastSlash);

	  isdev = isDevice(cmdDevName);

	  if (isdev == false)
             return false;

	  cmdName = s.substring(lastSlash, s.length());

	  boolean   cmdPattern;

	  cmdPattern = Pattern.matches("/[a-zA-Z_0-9[-]]+", cmdName);
	  if (cmdPattern == false)
	     return false;
	  else
             return cFac.isCommand(s);
       }
       catch (IndexOutOfBoundsException ex)
       {
          return false;
       }
   }

   protected boolean isDevice(String s)
   {
       boolean   devNamePattern;

       devNamePattern = Pattern.matches("//[a-zA-Z_0-9]+:[0-9]+/[a-zA-Z_0-9[-]]+/[a-zA-Z_0-9[-]]+/[a-zA-Z_0-9[-]]+", s);

       if (devNamePattern == false)
          devNamePattern = Pattern.matches("[a-zA-Z_0-9[-]]+/[a-zA-Z_0-9[-]]+/[[a-zA-Z_0-9][-]]+", s);

       if (devNamePattern == false)
          devNamePattern = Pattern.matches("taco:[a-zA-Z_0-9]+/[a-zA-Z_0-9]+/[a-zA-Z_0-9]+", s);

       //return devNamePattern;


       if (devNamePattern == true)
          return dFac.isDevice(s);
       else
          return false;

   }






// Adding a device
// ---------------

   private void addDevice(JDObject jdObj, String s)
   {
      try
      {
	 addDeviceListener(dFac.getDevice(s));
	 mouseify(jdObj, s);
	 stashComponent(s, jdObj);
	 addDevToStateCashHashMap(s);
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
      /* Added on 16/june/2003 to add status tooltip into synoptic */
      device.addStatusListener(this);
      /* Added on 23/june/2003 to add the errors of state reading into error history window */
      if (errorHistWind != null)
         device.addErrorListener(errorHistWind);
   }


   private void mouseify(JDObject jdObj, String name)
   {

      /* Attach a mouse listener to the device component for mouse press */
     jdObj.addMouseListener(
             new JDMouseAdapter() {
               public void mousePressed(JDMouseEvent e) {
                 deviceClicked(e);
               }

               public void mouseEntered(JDMouseEvent e) {
                 devDisplayToolTip(e);
               }

               public void mouseExited(JDMouseEvent e) {
                 devRemoveToolTip(e);
               }

             });
   }


   private void deviceClicked(JDMouseEvent evt)
   {

      JDObject     comp = (JDObject)evt.getSource();
      String       devName = comp.getName();     // The name of the device
      String       clName =comp.getExtendedParam("className");
      String       constParam = comp.getExtendedParam("classParam");

      if (clName.length()==0)
      {
         showDefaultPanel(devName);
	 return;
      }
      
      // The case standard for user defined panel class :
      
      if (constParam.length()==0)
         constParam = null;

      System.out.println("clName = "+clName+"  constParam = "+constParam+ " devName = " + devName);

      
      
      Class        panelCl;
      Constructor  panelClNew;
      Class[]      paramCls = new Class[1];
      Object[]     params = new Object[1];

      try // Load the class and the constructor (one String argument) of the device panel
      {
         panelCl = Class.forName(clName);
         paramCls[0] = devName.getClass();
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
	 // Added a sleep to allow some applications long to start
	 Thread.sleep(500);
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


   private void showDefaultPanel(String devName)
   {
      // The default panel is atkpanel.MainPanel in read-only mode
      // atkpanel.MainPanel in read-only mode is instantiated by the following constructor
      // public MainPanel(String  deviceName, Boolean standAlone=false,
      //                          Boolean keepStateRefresherThreadWhenExiting=true,
      //                          Boolean propertyButtonVisible = false,
      //                          Boolean atkpanelReadOnly = true)
      // So the constructor has five arguments one String followed by four Booleans

       Class          atkpanelCl;
       Constructor    atkpanelClNew;
       Class[]        atkpanelParamCls = new Class[5];
       Object[]       params = new Object[5];
      
System.out.println("showDefaultPanel called");

      try // Load the class and the constructor of the device panel
      {
         atkpanelCl = Class.forName("atkpanel.MainPanel");
      }
      catch (ClassNotFoundException clex)
      {
	 JOptionPane.showMessageDialog(null, "showDefaultPanel : atkpanel.MainPanel not found; ignored.\n");
	 return;
      }
      
      try
      {
         atkpanelParamCls[0] = devName.getClass();
         atkpanelParamCls[1] = Class.forName("java.lang.Boolean");
         atkpanelParamCls[2] = atkpanelParamCls[1];
         atkpanelParamCls[3] = atkpanelParamCls[1];
         atkpanelParamCls[4] = atkpanelParamCls[1];
         atkpanelClNew = atkpanelCl.getConstructor(atkpanelParamCls);
      }
      catch (ClassNotFoundException clex)
      {
	 JOptionPane.showMessageDialog(null, "showDefaultPanel :java.lang.Boolean not found; ignored.\n");
	 return;
      }
      catch (Exception e)
      {
	 JOptionPane.showMessageDialog(null, "showDefaultPanel : Failed to load the constructor (five arguments) for atkpanel read-only.\n");
	 return;
      }

      // Initialize the values for the constructor arguments
      params[0] = devName; // device name
      params[1] = Boolean.FALSE; // atkpanel.standAlone (don't exit when ending)
      params[2] = Boolean.TRUE; // keepStateRefresher (the stateRefresher thread will be kept running at the end)
      params[3] = Boolean.FALSE; //  the property button (not visible)
      params[4] = Boolean.TRUE; // the atkpanel read only

      try // Instantiate the read-only atkpanel for the device
      {
         atkpanelClNew.newInstance(params);
	 // Added a sleep to allow some applications long to start
	 Thread.sleep(500);
      }
      catch (InstantiationException  instex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 1 the atkpanel read-only.\n");
      }
      catch (IllegalAccessException  accesex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 2 the atkpanel read-only.\n");
      }
      catch (IllegalArgumentException argex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 3 the atkpanel read-only.\n");
      }
      catch (InvocationTargetException invoqex)
      {
	 JOptionPane.showMessageDialog(null, "Failed to instantiate 4 the atkpanel read-only.\n");
System.out.println(invoqex);
System.out.println(invoqex.getMessage());
invoqex.printStackTrace();
      }
      catch (Exception e)
      {
	 JOptionPane.showMessageDialog(null, "Got an exception when instantiate the default panel : atkpanel readonly.\n");
      }

   }


   private void stashComponent(String s, JDObject jdObj)
   {
      List list = (List)jdHash.get(s);
      if (list == null)
	  list = new Vector();
      list.add(jdObj);
      jdHash.put(s, list);
   }


   private void addDevToStateCashHashMap(String s)
   {
      List        list;
      String      str;

      list = (List)stateCashHash.get(s);
      if (list != null)
         return;

      list = new Vector();
      str = new String("no status");
      list.add(STATE_INDEX, str);
      list.add(STATUS_INDEX, str);
      stateCashHash.put(s, list);
   }




// Adding a command
// ----------------

   private void addCommand(JDObject jdObj, String s)
   {
     if(jdObj.isInteractive())
	   addCommmand(jdObj, s);
   }

   private void addCommmand(JDObject jdpb, String s)
   {
      ICommand  cmd = null;

      try
      {
         cmd = cFac.getCommand(s);
	 if (cmd != null)
	    mouseify(jdpb, cmd);
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


   private void mouseify(JDObject jdpb, ICommand devCmd)
   {

      final  ICommand  cmd = devCmd;
      /* Attach a mouse listener to the jdpushbutton component for mouse press */
      jdpb.addValueListener ( new JDValueListener() {
        public void valueChanged(JDObject jdObject) {}
        public void valueExceedBounds(JDObject jdObject) {
       	    System.out.println("Acommand is " + cmd);
            commandClicked(cmd);
    	   }
       });

   }


   private void commandClicked(ICommand  ic)
   {
      if (ic instanceof InvalidCommand)
      {
	  javax.swing.JOptionPane.showMessageDialog(this, ic.getName() + " is not supported. ", "Error", 1);
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

   private void addAttribute(JDObject jddg, String s)
   {
      IAttribute  att = null;

      try
      {
         att = aFac.getAttribute(s);
	 if (att != null)
	 {
	    if (att instanceof INumberScalar)
	    {
	       addAttributeListener((INumberScalar) att);
	       // set min, max, value for the digit? jddg.setAll();
	       stashComponent(s, jddg);
	    }
	 }
      }
      catch (ConnectionException connectionexception)
      {
	 System.out.println("Couldn't load device for attribute" + s + " " + connectionexception);
      }
      catch (DevFailed dfEx)
      {
	 System.out.println("Couldn't find the attribute" + s + " " + dfEx);
      }

   }

   private void addAttributeListener(INumberScalar ins)
   {
      System.out.println("connecting to a number scalar attribute : " + ins);
      ins.addNumberScalarListener(this);
      if (errorHistWind != null)
         ins.addErrorListener(errorHistWind);
   }





// Implement the interface methods for synoptic animation
// -------------------------------------------------------


   // Interface INumberScalarListener
   public void numberScalarChange(NumberScalarEvent evt)
   {
      JDObject       jdObj;
      INumberScalar  ins;
      int            value=(int)evt.getValue();

      ins = null;
      ins = evt.getNumberSource();

      String s = ins.getName();

      if (ins != null)
      {
	 List list = (List) jdHash.get(s);
	 if (list == null)
            return;

	 int  nbJdObjs = list.size(); int  i;

	 for (i=0; i<nbJdObjs; i++)
	 {
            jdObj = null;
	    jdObj = (JDObject) list.get(i);

	    if (jdObj != null)
	    {
		  jdObj.setValue(value);
                  jdObj.refresh();
	    }
	 }
      }
   }



   // Interface INumberScalarListener (superclass of IStateListener and INumberScalarListener)
   public void stateChange(AttributeStateEvent evt)
   {
   }



   // Interface IErrorListener (superclass of IStateListener and INumberScalarListener)
   public void errorChange(ErrorEvent evt)
   {
   }


   // Interface IStateListener
   public void stateChange(StateEvent event)
   {
//long before, after, duree;
//before = System.currentTimeMillis();
      JDObject   jdObj;

      Device device = (Device)event.getSource();
      String s = device.getName();

      /* Added on 16/june/2003 to add state tooltip into synoptic */

      // Update and test the "cashed" state
      List  stateStatusCash = null;

      stateStatusCash = (List) stateCashHash.get(s);
      if (stateStatusCash != null)
      {
         String  stateCash = null;
	 try
	 {
	    stateCash = (String) stateStatusCash.get(STATE_INDEX);

	    if (stateCash != null)
	    {
	       if (stateCash.equals(event.getState()))
		  return;
	    }

	    stateCash = new String(event.getState());
	    stateStatusCash.set(STATE_INDEX, stateCash);
            stateCashHash.put(s, stateStatusCash);
	 }
	 catch (IndexOutOfBoundsException  iob)
	 {
	 }
      }
      /* */

      // Here we are sure that the new state is different from the "cashed" state

//System.out.println("State has changed for "+s+" : "+event.getState());
      List list = (List) jdHash.get(s);
      if (list == null)
         return;

      int  nbJdObjs = list.size();
      int  i;

      for (i=0; i<nbJdObjs; i++)
      {
	 jdObj = (JDObject) list.get(i);
	 if( jdObj.isProgrammed())
	 {
	    jdObj.setValue(getDynoState(event.getState()));
	    jdObj.refresh();
	 }
	 else
	 {
	    jdObj.setBackground(ATKConstant.getColor4State(event.getState()));
	    jdObj.refresh();
	 }
      }
//System.out.println("Left state change for "+s);
//after = System.currentTimeMillis();
//duree = after - before;
//System.out.println("TangoSynopticHandler.stateChange took "+  duree + " milli seconds.");
   }


   // Interface IStatusListener
   /* Added on 16/june/2003 to add status tooltip into synoptic */
   public void statusChange(StatusEvent event)
   {

      Device device = (Device)event.getSource();
      String s = device.getName();

      List  stateStatusCash = null;

      stateStatusCash = (List) stateCashHash.get(s);
      if (stateStatusCash != null)
      {
         String  statusCash = null;

	 try
	 {
	    statusCash = (String) stateStatusCash.get(STATUS_INDEX);

	    if (statusCash != null)
	    {
	       if (statusCash.equals(event.getStatus()))
		  return;
	    }

	    statusCash = new String(event.getStatus());
	    stateStatusCash.set(STATUS_INDEX, statusCash);
            stateCashHash.put(s, stateStatusCash);
	 }
	 catch (IndexOutOfBoundsException  iob)
	 {
	    return;
	 }
      }
      /* */
   }




   private int getDynoState(String deviceState)
   {
      Integer   intObj;

      intObj = (Integer) dynoState.get(deviceState);
      return (intObj.intValue());
   }



   public void devDisplayToolTip(JDMouseEvent e)
   {
       String              devName;
       JDObject            jdObj;
       List                stateStatusCash = null;
       String              stateCash = null;

       if ( toolTipMode == TOOL_TIP_NONE )
       {
          setToolTipText(null);
	      return;
       }

       if ( toolTipMode == TOOL_TIP_NAME)
       {
          jdObj = (JDObject)e.getSource();
	  devName = jdObj.getName();     // The name of the device
          setToolTipText(devName);
	  return;
       }

       if ( toolTipMode == TOOL_TIP_STATE)
       {
	  jdObj = (JDObject)e.getSource();
	  devName = jdObj.getName();     // The name of the device

	  // get the "cashed" state for the device
	  stateStatusCash = (List) stateCashHash.get(devName);
	  if (stateStatusCash == null)
	  {
	     setToolTipText(null);
	     return;
	  }

	  try
	  {
	     stateCash = (String) stateStatusCash.get(STATE_INDEX);
	     if (stateCash == null)
	     {
		setToolTipText(null);
		return;
	     }

	     setToolTipText(stateCash);
	     return;
	  }
	  catch (IndexOutOfBoundsException  iob)
	  {
	     setToolTipText(null);
	     return;
	  }
       }

       setToolTipText(null);
   }



   public void devRemoveToolTip(JDMouseEvent e)
   {
       setToolTipText(null);
   }



   public static void main(String args[])
   {
      TangoSynopticHandler     tsh = new TangoSynopticHandler();

      try
      {
	     tsh.setSynopticFileName("/users/poncet/ATK_OLD/jloox_files/id14.jlx");
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

      jf.setContentPane(tsh);
      jf.pack();
      jf.show();
   }


}

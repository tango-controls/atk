
// File:          SimpleJLooxAdapter.java
// Created:       2002-09-23 15:22:29, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2002-09-23 15:22:29, poncet>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.loox;

import com.loox.jloox.*;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;
import fr.esrf.tangoatk.widget.device.StateViewer;

import atkpanel.MainPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.*;


public class SimpleJLooxAdapter implements IStateListener
{
   private    Refresher          refresher = null;
   private    LxAbstractGraph    graph = null;
   private    LxView             view = null;
   private    AttributeFactory   aFac = null;
   private    DeviceFactory      dFac = null;

   private    AttributeList      lxAtl = null;
   private    List               lxDevl = null;
   private    LxComponent[]      lxComps = null;
   
   private    Map                lxHash;




   public SimpleJLooxAdapter()
   {
      aFac = AttributeFactory.getInstance();
      dFac = DeviceFactory.getInstance();
//      lxAtl = new AttributeList();
//      lxDevl = new Vector();
   }

   // Interface IErrorListener
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
	    if (lxComp instanceof LxAbstractGroup)
	        groupChangeColor((LxAbstractGroup)lxComp, StateViewer.getColor4State(event.getState()));
	 }
      }
   }
   

   private void groupChangeColor(LxAbstractGroup  lxg, java.awt.Color col)
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
	       System.out.println("getPaint() returns null");
	    if (((LxElement)lxc).getPaint().getTransparency() == java.awt.Paint.OPAQUE)
	       ((LxElement)lxc).setPaint(col);
	    else
	       ((LxElement)lxc).setLineColor(col);
	 }
	 else
	 {
	    if (lxc instanceof LxAbstractGroup)
	        groupChangeColor((LxAbstractGroup)lxc, col);
	 }
	 
      }
   }
   
   
   public void setViewer( LxView  v) throws IllegalArgumentException
   {
      if (v == null)
      {
         throw new IllegalArgumentException("Cannot set viewer to null.");
      }
            
      lxHash = new HashMap();
      view = v;
      graph = v.getGraph();
   }
   
   
   public void setModel( String  jlxFileName) 
              throws MissingResourceException, FileNotFoundException, IllegalArgumentException
   {
      lxHash = new HashMap();
// Here should disconnect from all attributes and devices in the previous
// Loox file.

      if (view == null)
      {
         throw new MissingResourceException(
	   "The viewer is not set. Call setViewer before setModel.",
	   "LxView", null);
      }
      
      if (graph == null)
      {
         throw new MissingResourceException(
	   "The viewer's graph is not set. Call viewer.setGraph then this.setViewer before setModel.",
	   "LxGraph", null);
      }
      
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
   }


   
   protected void parseJlooxComponents(LxComponent alxcomponent[])
   {
      for (int i = 0; i < alxcomponent.length; i++)
      {
	 LxComponent component = alxcomponent[i];
	 String s = component.getName();

	 if (isAttribute(s))
	     addAttribute(component, s);

	 if (isDevice(s))
	     addDevice(component, s);
	 else
	    System.out.println(s+" is not an attribute nor a device; ignored.");

//	 if (isGroup(component))
//	     parseComponents(((LxAbstractGroup)component).getComponents());
      }

   }


   protected boolean isAttribute(String s)
   {
       return aFac.isAttribute(s);
   }

   protected boolean isDevice(String s)
   {
       return dFac.isDevice(s);
   }


   private void addAttribute(LxComponent lxcomp, String s)
   {
   }


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
      extendedJLM.ExtendedData  classNames;
      
      LxComponent  comp = evt.getLxComponent();
      String       devName = comp.getName();     // The name of the device
      Object       extension = comp.getUserData(); // The name of the panel class
      String       clName = "atkpanel.MainPanel";

System.out.println("userData = "+comp.getUserData());
      if (extension != null)  // Check if the panel class is well specified to instantiate it
      {
	 if (extension instanceof extendedJLM.ExtendedData)
	 {
	    classNames = (extendedJLM.ExtendedData) extension;
	    clName = classNames.getString1();
	    JOptionPane.showMessageDialog(null, "Found Device Panel Class name = "+ clName + "\n");
	 }
	 else
	    JOptionPane.showMessageDialog(null, "Extension data is not an extendedJLM.ExtendedData.\n"
	       + "Will call atkpanel for : " + devName);
      }
      else
	 JOptionPane.showMessageDialog(null, "No extension data defined.\n"
	    + "Will call atkpanel for : " + devName);

      // By default instantiate atkpanel generic panel appli for all devices
//      String[]  strArr = new String[1];
//      strArr[0] = devName;
//      atkpanel.MainPanel atkmain = new MainPanel(strArr, false);

      Class        panelCl;
      Constructor  panelClNew;
      Class[]      paramCls = new Class[1];
      Object[]     params = new Object[1];
     
      try // Load the class and the constructor of the device panel
      {
         panelCl = Class.forName(clName);
	 System.out.println("Youpi");
         paramCls[0] = clName.getClass();
	 System.out.println("Youpi for String");
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
 

      params[0] = devName;
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

   public static void main(String args[])
   {
      SimpleJLooxAdapter     sjlx = new SimpleJLooxAdapter();
      extendedJLM.ExtendedGraph          lxg = new extendedJLM.ExtendedGraph();
      LxView                 lxv = new LxView();
      
      lxv.setGraph(lxg);
      
      try
      {
	 sjlx.setViewer(lxv);
	 sjlx.setModel("/users/poncet/ATK/jloox_files/id14bis.jlx");
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
      
      lxv.fitToGraph(20, 20);
      jf.setContentPane(lxv);
      jf.pack();
      jf.show();
   }


}

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
 
/*
 * TabbedPaneDevStateScalarViewer.java
 *
 * Created on April 25, 2007, 13:10
 *
 * @author poncet
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.*;

import java.util.HashMap;

/**
 * <code>TabbedPaneDevStateScalarViewer</code>
 * TabbedPaneDevStateScalarViewer is a viewer to display several attributes of type DevState. The colour of
 * the tab associated to a devstate attribute will change colour if the state changes.
 * A typical use of the TabbedPaneDevStateScalarViewer is:<br>
 * <code>
 * fr.esrf.tangoatk.widget.attribute.TabbedPaneDevStateScalarViewer tabbedState = new TabbedPaneDevStateScalarViewer();
 *
 * IEntity ie =attributeList.add("firstdev/State");
 * DevStateScalar stateAtt1 = (DevStateScalar) stateAtt;
 * tabbedState.addTab("firsDev", a specific device panel);
 * int tabidx1 = tabbedState.indexOfComponent(a specific device panel);
 * Integer tabIndex1 = new Integer(tabidx1);
 * tabbedState.addDevStateScalarModel(tabIndex1, stateAtt1);
 *
 * ie =attributeList.add("seconddev/State");
 * DevStateScalar stateAtt2 = (DevStateScalar) stateAtt;
 * tabbedState.addTab("secondDev", a component);
 * int tabidx2 = tabbedState.indexOfComponent(a component);
 * Integer tabIndex2 = new Integer(tabidx2);
 * tabbedState.addDevStateScalarModel(tabIndex2, stateAtt2);
 *
 * attributeList.startRefresher();
 * </code>
 */
public class TabbedPaneDevStateScalarViewer extends JTabbedPane implements IDevStateScalarListener
{


    public static final int      TOOLTIP_NONE = 0;
    public static final int      TOOLTIP_STATE = 1;
    public static final int      TOOLTIP_NAME = 2;
    public static final int      TOOLTIP_NAME_AND_STATE = 3;
    


    private HashMap<IDevStateScalar, Integer>   stateModelMap = null;
    private HashMap<Integer, IDevStateScalar>   indexStateAttMap = null;
    //private String    tooltipMode;
    private int       tooltipMode;
    
    /**
     * Creates a new instance of TabbedPaneDevStateScalarViewer
     */
    public TabbedPaneDevStateScalarViewer()
    {
        tooltipMode = TOOLTIP_NONE;
	stateModelMap = new HashMap<IDevStateScalar, Integer> ();
	indexStateAttMap = new HashMap<Integer, IDevStateScalar> ();
	setUI(new DevStateTabbedPaneUI());
    }
    
    public int getTooltipMode()
    {
       return tooltipMode;
    }
    
    public void setTooltipMode(int ttmode)
    {
       int                nbtabs;
       IDevStateScalar    stateAtt;
       
       
       if (tooltipMode==ttmode)
          return;
       /*
       if ( (!ttmode.equalsIgnoreCase(TAB_NO_TOOLTIP)) &&
            (!ttmode.equalsIgnoreCase(TAB_NAME_TOOLTIP)) &&
	    (!ttmode.equalsIgnoreCase(TAB_STATE_TOOLTIP))  )
          return;
	  */
       if ( (ttmode < TOOLTIP_NONE) || (ttmode > TOOLTIP_NAME_AND_STATE) )
          return;
	  
       tooltipMode = ttmode;
       
       nbtabs = getTabCount();
       if (nbtabs <= 0) return;
       
       for (int i=0; i<nbtabs; i++)
       {
	  switch (tooltipMode)
	  {
	      case TOOLTIP_NONE: 
	                  setToolTipTextAt(i, null);
			  break;
	      case TOOLTIP_STATE:
			  stateAtt = getStateAttAt(i);
			  if (stateAtt == null)
			     setToolTipTextAt(i, "NoState");
			  else
			  {
			     try
			     {
				setToolTipTextAt(i, stateAtt.getValue());
			     }
			     catch (Exception ex)
			     {
				setToolTipTextAt(i, IDevice.UNKNOWN);
			     }
			  }
	                  break;
	      case TOOLTIP_NAME:
	      		  stateAtt = getStateAttAt(i);
			  if (stateAtt == null)
			     setToolTipTextAt(i, "NoName");
			  else
			     setToolTipTextAt(i, stateAtt.getName());
	                  break;
	      case TOOLTIP_NAME_AND_STATE:
			  stateAtt = getStateAttAt(i);
			  if (stateAtt == null)
			     setToolTipTextAt(i, "NoNameAndState");
			  else
			  {
			     String  tt=stateAtt.getName();
			     try
			     {
				tt = tt + " : " + stateAtt.getValue();
			     }
			     catch (Exception ex)
			     {
				tt = tt + " : " + IDevice.UNKNOWN;
			     }
			     setToolTipTextAt(i, tt);
			  }
	                  break;
	  }
       }
    }
    
    
    public IDevStateScalar getStateAttAt(int tidx)
    {
        if ( (tidx < 0) || (tidx >= getTabCount()) )
	   return null;
	   
	Integer  tabIndex = new Integer(tidx);
	if (!(indexStateAttMap.containsKey(tabIndex))) return null;
	if (!(stateModelMap.containsValue(tabIndex))) return null;
	
	IDevStateScalar  stateAtt = indexStateAttMap.get(tabIndex);
	return stateAtt;
    }
    
    
    public int getTabIndexForStateAtt(IDevStateScalar stateAtt)
    {
        if (stateAtt == null) return -1;
	if (!(stateModelMap.containsKey(stateAtt))) return -1;
	
	Integer  stateIdx = stateModelMap.get(stateAtt);
	if (stateIdx == null) return -1;
	
	return stateIdx.intValue();
    }
    
    
    public void addDevStateScalarModel(Integer tabIndex, IDevStateScalar devStateAtt)
    {
        if ((tabIndex == null) || (devStateAtt == null)) return;
        if ( (tabIndex.intValue() < 0) || (tabIndex.intValue() >= getTabCount()) ) return;
        if (stateModelMap.containsValue(tabIndex)) return;

        indexStateAttMap.put(tabIndex, devStateAtt);
        stateModelMap.put(devStateAtt, tabIndex);
        
        if (!devStateAtt.areAttPropertiesLoaded())
            devStateAtt.loadAttProperties();
        
        devStateAtt.addDevStateScalarListener(this);
        devStateAtt.addErrorListener(this);
        devStateAtt.refresh();
	switch (tooltipMode)
	{
	    case TOOLTIP_NONE: 
	                setToolTipTextAt(tabIndex.intValue(), null);
			break;
	    case TOOLTIP_STATE:
			try
			{
			   setToolTipTextAt(tabIndex.intValue(), devStateAtt.getValue());
			}
			catch (Exception ex)
			{
			   setToolTipTextAt(tabIndex.intValue(), IDevice.UNKNOWN);
			}
	                break;
	    case TOOLTIP_NAME:
			setToolTipTextAt(tabIndex.intValue(), devStateAtt.getName());
	                break;
	    case TOOLTIP_NAME_AND_STATE:
			String  tt=devStateAtt.getName();
			try
			{
			   tt = tt + " : " + devStateAtt.getValue();
			}
			catch (Exception ex)
			{
			   tt = tt + " : " + IDevice.UNKNOWN;
			}
			setToolTipTextAt(tabIndex.intValue(), tt);
	                break;
	}
    }
    
    public void removeDevStateScalarModel(Integer tabIndex, IDevStateScalar devStateAtt)
    {
        if ((tabIndex == null) || (devStateAtt == null)) return;
        if ( (tabIndex.intValue() < 0) || (tabIndex.intValue() >= this.getTabCount()) ) return;
        if (!(stateModelMap.containsKey(devStateAtt))) return;
        if (!(indexStateAttMap.containsKey(tabIndex))) return;
        
        Integer tidx = stateModelMap.get(devStateAtt);
        if ( tidx.intValue() != tabIndex.intValue() ) return;
        
        
        devStateAtt.removeDevStateScalarListener(this);
        devStateAtt.removeErrorListener(this);
        stateModelMap.remove(devStateAtt);
        indexStateAttMap.remove(tabIndex);
    }
    
    public void devStateScalarChange(DevStateScalarEvent evt)
    {
        IDevStateScalar  stateAtt = null;
        
        if (evt.getSource() instanceof IDevStateScalar)
            stateAtt = (IDevStateScalar) evt.getSource();
        else
            return;
        
        setStateOnTab(stateAtt, evt.getValue());
    }

    public void stateChange(AttributeStateEvent attributeStateEvent)
    {
    }

    public void errorChange(ErrorEvent evt)
    {
         IDevStateScalar  stateAtt = null;
        
        if (evt.getSource() instanceof IDevStateScalar)
            stateAtt = (IDevStateScalar) evt.getSource();
        else
            return;
        
        setStateOnTab(stateAtt, IDevice.UNKNOWN);    
    }

    private void setStateOnTab(IDevStateScalar stateAtt, String currentState)
    {
        if (stateAtt == null) return;
        if (!(stateModelMap.containsKey(stateAtt))) return;
        
        Integer tidx = stateModelMap.get(stateAtt);
        try
        {
            this.setBackgroundAt(tidx.intValue(), ATKConstant.getColor4State(currentState, stateAtt.getInvertedOpenClose(), stateAtt.getInvertedInsertExtract()));
        }
        catch (IndexOutOfBoundsException iob)
        { 
        }
	
	// Update the tooltip if necessary
	if ( (tooltipMode != TOOLTIP_STATE) && (tooltipMode != TOOLTIP_NAME_AND_STATE) )
	   return;
	   
	int ind = getTabIndexForStateAtt(stateAtt);
	if ( (ind < 0) || (ind >= getTabCount()) )
	   return;
	   
	if (tooltipMode == TOOLTIP_STATE)
	   setToolTipTextAt(ind, currentState);
	else //TOOLTIP_NAME_AND_STATE
	   setToolTipTextAt(ind, stateAtt.getName()+" : "+currentState);
    }
    
    public static void main(String[] args)
    {
       AttributeList         attList = new AttributeList();
       IDevStateScalar       attState1, attState2;
       JPanel                jp1 = new JPanel();
       JPanel                jp2 = new JPanel();
       JFrame                mainFrame;
       
       TabbedPaneDevStateScalarViewer    tbv = new TabbedPaneDevStateScalarViewer();


       jp1.setPreferredSize(new java.awt.Dimension(300, 200));
       jp2.setPreferredSize(new java.awt.Dimension(300, 200));
       
       //tbv.setTooltipMode(TOOLTIP_NONE);
       //tbv.setTooltipMode(TOOLTIP_STATE);
       //tbv.setTooltipMode(TOOLTIP_NAME);
       tbv.setTooltipMode(TOOLTIP_NAME_AND_STATE);

       tbv.addTab("jlptest1", jp1);
       tbv.addTab("jlptest2", jp2);
       // Connect to 2 DevStateScalar attributes
       try
       {
          attState1 = (IDevStateScalar) attList.add("jlp/test/1/State");
          attState2 = (IDevStateScalar) attList.add("jlp/test/2/State");
	  
	  int tabidx1 = tbv.indexOfComponent(jp1);
	  Integer tabIndex1 = new Integer(tabidx1);
	  tbv.addDevStateScalarModel(tabIndex1, attState1);
	  int tabidx2 = tbv.indexOfComponent(jp2);
	  Integer tabIndex2 = new Integer(tabidx2);
	  tbv.addDevStateScalarModel(tabIndex2, attState2);
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  System.exit(-1);
       }
       
       mainFrame = new JFrame();
       
       mainFrame.addWindowListener(new java.awt.event.WindowAdapter()
       {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });
				     

       mainFrame.setContentPane(tbv);
       attList.startRefresher();
       mainFrame.pack();

       mainFrame.setVisible(true);

    } // end of main ()
    
    

   class DevStateTabbedPaneUI extends MetalTabbedPaneUI
   {

       /** Creates a new instance of DevStateTabbedPaneUI */
       DevStateTabbedPaneUI()
       {
       }

       protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                                	 int x, int y, int w, int h, boolean isSelected)
       {
           // All TabBackgrounds selected or not are displayed in the same manner 
           super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, false);        
       }    
   }
    
}


// File:          NumberSpectrumItemTrend.java
// Created:       2007-11-15 15:03:37, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;

import java.util.*;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;

/**
 * <code>NumberSpectrumItemTrend</code>
 * NumberSpectrumItemTrend is a viewer to display the trend of several items of 
 * a NumberSpectrum attributes. Each item is seen as a scalar and the evolution
 * of the items' value during the time is displayed in a chart.<br>
 * The major difference between NumberSpectrumItemTrend and NumberSpectrumTrendViewer is that 
 * with NumberSpectrumItemTrend you can specify which items of the array should be plotted
 * where NumberSpectrumTrendViewer plots all the items of the spectrum attribute. Moreover with
 * NumberSpectrumItemTrend the user has the possibility to name each plot associated
 * to each item. This possibility is absent from NumberSpectrumTrendViewer. <br>
 * <code>
 * fr.esrf.tangoatk.widget.attribute.NumberSpectrumItemTrend spectTrend = new NumberSpectrumItemTrend();
 *
 * IEntity ie =attributeList.add("firstdev/MyNumberSpectrumAttribute");
 * INumberSpectrum spectAtt = (INumberSpectrum) ie;
 * spectTrend.setPlotAll(false);
 * spectTrend.setModel(spectAtt);
 * spectTrend.plotItem(0, NumberSpectrumItemTrend.AXIS_Y1, "SrCurrentDelta");
 * spectTrend.plotItem(3, NumberSpectrumItemTrend.AXIS_Y2, "TL2SRTrEfficiency");
 *
 * attributeList.startRefresher();
 * </code>
 */

public class NumberSpectrumItemTrend extends JLChart
                                     implements ISpectrumListener, IAttributeStateListener, ActionListener
{
    public static final int         AXIS_NONE = 0;
    public static final int         AXIS_Y1 = 1;
    public static final int         AXIS_Y2 = 2;


    private   boolean                             plotAll = true;
    private   boolean                             plotting = true;
    protected INumberSpectrum                     model = null;
    protected Map<Integer, JLDataView>            itemMap = null;
    protected Map<JLDataView, Integer>            dvAxisMap = null;
    protected List<JLDataView>                    allItems = null;
    private   String                              qualityFactor = null;


    //Default Color
    protected static final Color[] defaultColor = {
		       Color.red,
		       Color.blue,
                       new Color(0,120,0), //ForestGreen
                       new Color(250,70,0), //dark Orange
		       Color.magenta,
                       new Color(120,0,120), //Violet
		       Color.black,
		       Color.pink,
		       Color.green,
		       Color.orange};

    protected final static int [] defaultMarkerStyle = {
            JLDataView.MARKER_BOX, JLDataView.MARKER_CIRCLE, 
            JLDataView.MARKER_CROSS, JLDataView.MARKER_DIAMOND,
            JLDataView.MARKER_DOT, JLDataView.MARKER_HORIZ_LINE,
            JLDataView.MARKER_SQUARE, JLDataView.MARKER_STAR,
            JLDataView.MARKER_TRIANGLE, JLDataView.MARKER_VERT_LINE};

    public NumberSpectrumItemTrend ()
    {
	itemMap = new HashMap<Integer, JLDataView> ();
	dvAxisMap = new HashMap<JLDataView, Integer> ();
	allItems = new Vector<JLDataView> ();
    }


    public void setModel(INumberSpectrum insModel)
    {
	if (model != null)
	   clearModel();

	if (insModel == null)
	   return;  

	model = insModel;
	model.addSpectrumListener(this);
	model.addStateListener(this);
	model.addErrorListener(this);
    }

    public void clearModel()
    {
	getY1Axis().clearDataView();
	getY2Axis().clearDataView();
	itemMap.clear();
	dvAxisMap.clear();
	allItems.clear();
	//itemMap = new HashMap<Integer, JLDataView> ();
	//dvAxisMap = new HashMap<JLDataView, Integer> ();
	//allItems = new Vector<JLDataView> ();
	if (model != null)
	{
            model.removeSpectrumListener(this);
            model.removeStateListener(this);
            model.removeErrorListener(this);
	}
	repaint();
    }
     
    public boolean getPlotAll()
    {
        return (plotAll);
    }   
   
    public void setPlotAll(boolean  b)
    {
        if (b == plotAll) return;
	
	getY1Axis().clearDataView();
	getY2Axis().clearDataView();

	itemMap.clear();
	dvAxisMap.clear();
	allItems.clear();
	//itemMap = new HashMap<Integer, JLDataView> ();
	//dvAxisMap = new HashMap<JLDataView, Integer> ();
	//allItems = new Vector<JLDataView> ();
	
	plotAll = b;
	repaint();
    }   
     
    public boolean isPlotting()
    {
        return (plotting);
    }   
   
    public void setPlotting(boolean  b)
    {
	plotting = b;
    }  
    
    public void removeAllPlots()
    {
         boolean  plotState = plotting;
	 
	 plotting=false;
	 getY1Axis().clearDataView();
	 getY2Axis().clearDataView();

	 itemMap.clear();
	 dvAxisMap.clear();
	 allItems.clear();
	 repaint();
	 plotting = plotState;
    } 
    
    public JLDataView getDataViewForItem(int itemIndex)
    {
        if (itemIndex < 0) return null;
        if (plotAll && (itemIndex >= allItems.size())) return null;
	if (plotAll)
	   return allItems.get(itemIndex);
	   
	Set<Integer> itemSet = itemMap.keySet();
	if (itemSet != null)
	{
	    Iterator<Integer>  itemIt=itemSet.iterator();
	    while ( itemIt.hasNext() )
	    {
		Integer currItem= itemIt.next();
		if (currItem.intValue() == itemIndex)
		   return itemMap.get(currItem);
	    }
	}
	return null;
    }
    
    public void plotItem(int itemIndex, int axis, String plotLabel)
    {
	if (model == null) return;
	if (plotAll) return;
	if ((axis != AXIS_Y1) && (axis != AXIS_Y2)) return;
	
	   
	JLDataView  data = getDataViewForItem(itemIndex);
	if (data != null)
	   removePlotItem(itemIndex);
	   
	Integer      itemKey = new Integer(itemIndex);
	Integer      axisNumber = new Integer(axis);
        Color        drawColor = defaultColor [ itemMap.size() % defaultColor.length ];
        //int          markerStyle = defaultMarkerStyle [ (itemIndex / defaultColor.length) % defaultMarkerStyle.length ];
	
	data = new JLDataView();   
        data.setViewType(JLDataView.TYPE_LINE);
        data.setStyle(JLDataView.STYLE_SOLID);
        data.setColor(drawColor);
        //data.setMarker(markerStyle);
        data.setMarkerColor(drawColor);
	data.setName(plotLabel);
        data.setUnit( model.getUnit() );
	if (axis == AXIS_Y2)
           getY2Axis().addDataView(data);
	else
	   getY1Axis().addDataView(data);
	   
        itemMap.put(itemKey, data);
        dvAxisMap.put(data, axisNumber);
    }
    
    public void removePlotItem(int  itemIndex)
    {
	if (model == null) return;
	if (plotAll) return;
	if ((itemIndex < 0) || (itemIndex >= model.getXDimension())) return;

	Set<Integer> itemSet = itemMap.keySet();
	if (itemSet != null)
	{
	    Iterator<Integer>  itemIt=itemSet.iterator();
	    while ( itemIt.hasNext() )
	    {
		Integer currItem= itemIt.next();
		if (currItem.intValue() == itemIndex)
		{
		   JLDataView   dv=itemMap.get(currItem);
		   JLAxis       dvAxis = dv.getAxis();
		   if (dvAxis != null)
		   {
		      dvAxisMap.remove(dv);
		      dvAxis.removeDataView(dv);
		   }
		   itemMap.remove(currItem);
		   repaint();
		}
	    }
	}
    }
    
    public void hideItem(int itemIndex)
    {
	if (model == null) return;
	if (plotAll) return;
	
	JLDataView  data = getDataViewForItem(itemIndex);
	if (data == null) return;
	JLAxis   itemAxis;
	itemAxis = data.getAxis();
	if ( (itemAxis != getY1Axis()) && (itemAxis != getY2Axis()) )
	   return;  // already hidden
	itemAxis.removeDataView(data); 
    }
    
    public void showItem(int itemIndex)
    {
	if (model == null) return;
	if (plotAll) return;
	
	JLDataView  data = getDataViewForItem(itemIndex);
	if (data == null) return;
	JLAxis   itemAxis;
	itemAxis = data.getAxis();
	if ( (itemAxis == getY1Axis()) || (itemAxis == getY2Axis()) )
	   return;  // already visible
	
	// Find out which axis to add the dataview to
	if (!dvAxisMap.containsKey(data))  return;  // cannot find the axis
	
	Integer   itemAxisNumber=dvAxisMap.get(data);
	if (itemAxisNumber == null) return;
	
	if (itemAxisNumber.intValue() == AXIS_Y1)
	   getY1Axis().addDataView(data);
	else
	   if (itemAxisNumber.intValue() == AXIS_Y2)
	      getY2Axis().addDataView(data);
    }
    
    public void changeItemAxis(int itemIndex, int newAxis)
    {
	if (model == null) return;
	if (plotAll) return;
	if ((newAxis != AXIS_Y1) && (newAxis != AXIS_Y2) && (newAxis != AXIS_NONE)) return;
	if (newAxis == AXIS_NONE)
	{
	   hideItem(itemIndex);
	   return;
	}
	
	   
	JLDataView  data = getDataViewForItem(itemIndex);
	if (data == null) return;
	
	int   oldAxis;
	if (data.getAxis() == getY1Axis())
	   oldAxis = AXIS_Y1;
	else
	   if (data.getAxis() == getY2Axis())
	      oldAxis = AXIS_Y2;
	   else
	      oldAxis = AXIS_NONE;
	
	if (oldAxis == AXIS_NONE)
	{
	   showItem(itemIndex);
	   return;
	}
	
	// Change the current Axis of the item if necessary
	if (oldAxis == newAxis) return;
	
	if (oldAxis == AXIS_Y1)
	   getY1Axis().removeDataView(data);
	else
	   getY2Axis().removeDataView(data);
	   
	if (newAxis == AXIS_Y1)
	   getY1Axis().addDataView(data);
	else
	   getY2Axis().addDataView(data);
	 
	if (dvAxisMap.containsKey(data))
	   dvAxisMap.remove(data);
	dvAxisMap.put(data, new Integer(newAxis));
    }

    private void plotAllItems(NumberSpectrumEvent e)
    {
	int    spectrumSize = ((INumberSpectrum)e.getSource()).getXDimension(); 
	
	for (int i = 0; i < e.getValue().length; i++)
        {
            if (i >= spectrumSize)
	       break;
	    JLDataView data;
            if (i < allItems.size())
            {
                data = allItems.get(i);
		if (qualityFactor == null)
	           data.add(e.getTimeStamp(), Double.NaN);
		else
	           if (qualityFactor.equals(IAttribute.INVALID))
		      data.add(e.getTimeStamp(), Double.NaN);
		   else
                      data.add(e.getTimeStamp(), e.getValue()[i]);
                garbageData(data);
            }
            else
            {
                data = new JLDataView();
 		if (qualityFactor == null)
	           data.add(e.getTimeStamp(), Double.NaN);
		else
	           if (qualityFactor.equals(IAttribute.INVALID))
		      data.add(e.getTimeStamp(), Double.NaN);
		   else
                      data.add(e.getTimeStamp(), e.getValue()[i]);
                Color drawColor = defaultColor [ i % defaultColor.length ];
                //int markerStyle = defaultMarkerStyle [ (i / defaultColor.length) % defaultMarkerStyle.length ];
                data.setViewType(JLDataView.TYPE_LINE);
                data.setStyle(JLDataView.STYLE_SOLID);
                data.setColor(drawColor);
                //data.setMarker(markerStyle);
                data.setMarkerColor(drawColor);
		data.setName(Integer.toString(i));
                data.setUnit( ( (INumberSpectrum)e.getSource() ).getUnit() );
                getY1Axis().addDataView(data);
                allItems.add(data);
            }
            data = null;
        }
    }

    public void spectrumChange (NumberSpectrumEvent e)
    {
	int         spectrumSize = ((INumberSpectrum)e.getSource()).getXDimension(); 
	JLDataView  data=null;

        if (!plotting) return;
	
	if (plotAll)
	   plotAllItems(e);
	else
	{	   
	   for (int i = 0; i < e.getValue().length; i++)
	   {
               if (i >= spectrumSize)
		  break;
	       data = getDataViewForItem(i);
	       if (data == null)
		  continue;
	       if (qualityFactor == null)
	          data.add(e.getTimeStamp(), Double.NaN);
	       else
	          if (qualityFactor.equals(IAttribute.INVALID))
		     data.add(e.getTimeStamp(), Double.NaN);
		  else
                     data.add(e.getTimeStamp(), e.getValue()[i]);
               garbageData(data);
	   }
	}
	repaint();
    }

    public void stateChange (AttributeStateEvent e)
    {
        qualityFactor = e.getState();
    }

    public void errorChange (ErrorEvent evt)
    {
        if (!plotting) return;
	
	if (plotAll)
	   plotAllError(evt);
	else
	{
	   Set<Integer> itemSet = itemMap.keySet();
	   if (itemSet != null)
	   {
	       Iterator<Integer>  itemIt=itemSet.iterator();
	       while ( itemIt.hasNext() )
	       {
		   Integer currItem= itemIt.next();
		   JLDataView data = itemMap.get(currItem);
        	   data.add(evt.getTimeStamp(), Double.NaN);
        	   garbageData(data);
	       }
	   }
	}
	repaint();
    }
    
    private void plotAllError(ErrorEvent evt)
    {
	for (int i = 0; i < allItems.size(); i++)
        {
	    JLDataView data = (JLDataView)allItems.get(i);
	    data.add(evt.getTimeStamp(), Double.NaN);
	    garbageData(data);
	}
    }




    public static void main(String[] args)
    {
	 AttributeList              attList = new AttributeList();
	 INumberSpectrum            ins;
	 JFrame                     mainFrame;
	 String                     attributeName;

	 NumberSpectrumItemTrend    nsit = new NumberSpectrumItemTrend();

	 if (args.length > 0)
	 {
             attributeName = args[0];
	 }
	 else
	 {
             //attributeName = "sr/xfrefflibera/1/Results";
             attributeName = "jlp/test/1/att_spectrum";
	 }

	 // Connect to 2 DevStateScalar attributes
	 try
	 {
            ins = (INumberSpectrum) attList.add(attributeName);

	    nsit.getXAxis().setGridVisible(true);
	    nsit.getY1Axis().setGridVisible(true);
	    nsit.getY1Axis().setAutoScale(false);
	    nsit.getY2Axis().setAutoScale(false);
	    nsit.getY1Axis().setMinimum(0.0);
	    nsit.getY1Axis().setMaximum(10.0);
	    nsit.getY2Axis().setMinimum(0.0);
	    nsit.getY2Axis().setMaximum(100.0);
	    
	    nsit.setPlotAll(false);
	    nsit.setModel(ins);
	    nsit.plotItem(7, NumberSpectrumItemTrend.AXIS_Y1, "SrCurrentDelta");
	    nsit.plotItem(1, NumberSpectrumItemTrend.AXIS_Y2, "TL2SRTrEfficiency");
	 }
	 catch (Exception ex)
	 {
            System.out.println("caught exception : "+ ex.getMessage());
	    System.exit(-1);
	 }
	
         mainFrame = new JFrame();
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         mainFrame.getContentPane().add(nsit);
	 
	 attList.startRefresher();
	 
         mainFrame.setSize(800,600);
	 mainFrame.pack();
	 mainFrame.setVisible(true);
	 
	 // Test hide and show item!
	 for (int i=0; i<10; i++)
	 {
	     try
	     {
	         Thread.sleep(5000);
	     }
	     catch(Exception ex)
	     {
	     }
	     nsit.hideItem(7);
	     try
	     {
	         Thread.sleep(5000);
	     }
	     catch(Exception ex)
	     {
	     }
	     nsit.showItem(7);
	 }
    }
}

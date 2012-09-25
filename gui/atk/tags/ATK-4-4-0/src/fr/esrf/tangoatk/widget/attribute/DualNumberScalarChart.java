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
 
/*	Synchrotron Soleil 
 *  
 *   File          :  DualNumberScalarChart.java
 *  
 *   Project       :  ATKWidgetSoleil
 *  
 *   Description   :  
 *  
 *   Author        :  SOLEIL
 *  
 *   Original      :  13 sept. 2005 
 *  
 *   Revision:  					Author:  
 *   Date: 							State:  
 *  
 *   Log: AttributeMultiChart.java,v 
 *
 */
package fr.esrf.tangoatk.widget.attribute;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;

/**
 * 
 * @author SOLEIL
 */
public class DualNumberScalarChart extends JLChart 
                                 implements INumberScalarListener {

    private JLDataView pointView = new JLDataView();;
    private JLDataView midleLineView = new JLDataView();
    private int markerStyle = JLDataView.MARKER_DOT;
    private boolean middleLineVisible = true;
    
    private INumberScalar xattribute = null;
    private INumberScalar yattribute = null;
    
    private double defaultXMinValue = -100;
    private double defaultXMaxValue = 100;
    private double defaultXMinAlarm = -100;
    private double defaultXMaxAlarm = 100;
    
    private double defaultYMinValue = -100;
    private double defaultYMaxValue = 100;
    private double defaultYMinAlarm = -100;
    private double defaultYMaxAlarm = 100;
    
     /**
     * Constructor
     * @param chartStyle the kind of chart chosen
     * @see barChart
     * @see dotChart
     * @see lineChart
     */
    public DualNumberScalarChart() {
        super();
        initComponents();
    }

    /**
     * Returns the marker style style for line chart and dot chart
     * @return int representing the marker style
     */
    public int getMarkerStyle() {
        return markerStyle;
    }

    /**
     * Sets the marker style for line chart and dot chart
     * @see MARKER_DOT
     * @see MARKER_BOX
     * @see MARKER_TRIANGLE
     * @see MARKER_DIAMOND
     * @see MARKER_STAR
     * @see MARKER_VERT_LINE
     * @see MARKER_HORIZ_LINE
     * @see MARKER_CROSS
     * @see MARKER_CIRCLE
     * @see MARKER_SQUARE
     */
    public void setMarkerStyle(int style) {
        markerStyle = style;
        if(pointView != null)
        {
            pointView.setMarker(markerStyle);
            repaint();
        }
    }

    // called by constructor
    // initializes every component, including axis
    protected void initComponents() {

        // Initialise chart properties
        setSize(640, 480);
        setHeaderVisible(true);
        setLabelVisible(true);
        // Initialise axis properties
        getXAxis().setDrawOpposite(true);
        setXAxisOnBottom(false);
        setMarkerStyle(markerStyle);
        getY1Axis().setDrawOpposite(true);
        getY1Axis().setAutoScale(false);
        getY1Axis().setGridVisible(true);
        getY1Axis().setSubGridVisible(true);
        getY1Axis().setMinimum(defaultYMinValue);
        getY1Axis().setMaximum(defaultYMaxValue);
        getXAxis().setAutoScale(false);
        getXAxis().setMinimum(defaultXMinValue);
        getXAxis().setMaximum(defaultXMaxValue);
        getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
        getXAxis().setGridVisible(false);
        getXAxis().setSubGridVisible(true);
        setPaintAxisFirst(false);
    }

      /**
     * Sets the attributes this chart will display.
     * It calls to clearmodel() first
     * @param attl the list of Attribute as an <code>AttributeList</code>
     */
    public void setXYModel(INumberScalar axattribute,INumberScalar ayattribute) {
             if ((xattribute != null) || (yattribute != null))
            clearXYModel();
        //System.out.println("setXYModel" + axattribute + " " + ayattribute);
        if((axattribute == null) || (ayattribute == null))
            return;
            
        xattribute = axattribute;
        yattribute = ayattribute;
        
        if(xattribute.getMaxValue() > defaultXMaxValue)
            setDefaultXMaxValue(xattribute.getMaxValue());
        
        if(xattribute.getMaxAlarm() < defaultXMaxAlarm)
            setDefaultXMaxAlarm(xattribute.getMaxAlarm());
        
        if(xattribute.getMinValue() < defaultXMinValue)
            setDefaultXMinValue(xattribute.getMinValue());
        
        if(xattribute.getMinAlarm() > defaultXMinAlarm)
            setDefaultXMinAlarm(xattribute.getMinAlarm());
        
        if(yattribute.getMaxValue() > defaultYMaxValue)
            setDefaultYMaxValue(yattribute.getMaxValue());
        
        if(yattribute.getMaxAlarm() < defaultYMaxAlarm)
            setDefaultYMaxAlarm(yattribute.getMaxAlarm());
        
        if(yattribute.getMinValue() < defaultYMinValue)
            setDefaultYMinValue(yattribute.getMinValue());
        
        if(yattribute.getMinAlarm() > defaultYMinAlarm)
            setDefaultYMinAlarm(yattribute.getMinAlarm());
        
        setHeader(xattribute.getName());
        pointView.setName(yattribute.getName());
        pointView.setLineWidth(2);
        pointView.setColor(ATKConstant.getColor4State("VALID"));
        pointView.setLabelVisible(true);
        pointView.setStyle(JLDataView.FILL_STYLE_SOLID);
        pointView.setLineWidth(1);
        pointView.setFillStyle(JLDataView.FILL_STYLE_SOLID);
        pointView.setMarker(markerStyle);
        getY1Axis().addDataView(pointView);
        //xattribute.addNumberScalarListener(this);
        yattribute.addNumberScalarListener(this);
        
        setMiddleLineVisible(middleLineVisible);
        midleLineView.setColor(Color.BLACK);
        midleLineView.setStyle(JLDataView.FILL_STYLE_NONE);
        midleLineView.setMarker(JLDataView.MARKER_HORIZ_LINE);
        midleLineView.setName("X middle axis");
        //System.out.println(defaultXMaxValue + " " + defaultXMinValue);
        double xmidle = (defaultXMaxValue + defaultXMinValue)/2; 
        midleLineView.add(xmidle,defaultYMinValue);
        midleLineView.add(xmidle,defaultYMaxValue);
        getY1Axis().addDataView(midleLineView);
    }

    /**
     * Clears the chart
     */
    public void clearXYModel() {
       
        if(xattribute != null) 
        {
            //xattribute.removeNumberScalarListener(this);
            xattribute = null;
        }
        if(yattribute != null) 
        {
            yattribute.removeNumberScalarListener(this);
            yattribute = null;
        }    
        // Remove all dataviews and numberScalars from HMap and from JLChart
        getY1Axis().clearDataView();
        pointView.reset();
        midleLineView.reset();
    }

    public boolean isMiddleLineVisible() {
        return middleLineVisible;
    }
    public void setMiddleLineVisible(boolean middleLineVisible) {
        this.middleLineVisible = middleLineVisible;
        if(middleLineVisible)
            midleLineView.setLineWidth(1);
        else
            midleLineView.setLineWidth(0);
    }
    public double getDefaultXMaxAlarm() {
        return defaultXMaxAlarm;
    }
    public void setDefaultXMaxAlarm(double defaultXMaxAlarm) {
        this.defaultXMaxAlarm = defaultXMaxAlarm;
    }
    public double getDefaultXMaxValue() {
        return defaultXMaxValue;
    }
    public void setDefaultXMaxValue(double defaultXMaxValue) {
        this.defaultXMaxValue = defaultXMaxValue;
        getXAxis().setMaximum(defaultXMaxValue);
    }
    public double getDefaultXMinAlarm() {
        return defaultXMinAlarm;
    }
    public void setDefaultXMinAlarm(double defaultXMinAlarm) {
        this.defaultXMinAlarm = defaultXMinAlarm;
    }
    public double getDefaultXMinValue() {
        return defaultXMinValue;
    }
    public void setDefaultXMinValue(double defaultXMinValue) {
        this.defaultXMinValue = defaultXMinValue;
        getXAxis().setMinimum(defaultXMinValue);
    }
    public double getDefaultYMaxAlarm() {
        return defaultYMaxAlarm;
    }
    public void setDefaultYMaxAlarm(double defaultYMaxAlarm) {
        this.defaultYMaxAlarm = defaultYMaxAlarm;
    }
    public double getDefaultYMaxValue() {
        return defaultYMaxValue;
    }
    public void setDefaultYMaxValue(double defaultYMaxValue) {
        this.defaultYMaxValue = defaultYMaxValue;
        getY1Axis().setMaximum(defaultYMaxValue);
    }
    public double getDefaultYMinAlarm() {
        return defaultYMinAlarm;
    }
    public void setDefaultYMinAlarm(double defaultYMinAlarm) {
        this.defaultYMinAlarm = defaultYMinAlarm;
    }
    public double getDefaultYMinValue() {
        return defaultYMinValue;
    }
    public void setDefaultYMinValue(double defaultYMinValue) {
        this.defaultYMinValue = defaultYMinValue;
        getY1Axis().setMinimum(defaultYMinValue);
    }
    /*
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    */
    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.INumberScalarListener#numberScalarChange(fr.esrf.tangoatk.core.NumberScalarEvent)
     */
    public void numberScalarChange(NumberScalarEvent numberScalarEvent) {
       //System.out.println("numberScalarChange");
       double yvalue = numberScalarEvent.getValue();
       double xvalue = xattribute.getNumberScalarValue();
       pointView.reset();
       pointView.add(xvalue, yvalue);
       
       if (yvalue <= defaultYMinValue || yvalue >= defaultYMaxValue)
           pointView.setMarkerColor(ATKConstant.getColor4Quality("INVALID"));
       else  if (yvalue <= defaultYMinAlarm || yvalue >= defaultYMaxAlarm)
           pointView.setMarkerColor(ATKConstant.getColor4Quality("ALARM"));
       else if (xvalue >= defaultXMaxValue || xvalue <= defaultXMinValue)
           pointView.setMarkerColor(ATKConstant.getColor4Quality("INVALID"));
       else  if (xvalue >= defaultXMaxAlarm || xvalue <= defaultXMinAlarm)
           pointView.setMarkerColor(ATKConstant.getColor4Quality("ALARM"));
       else
           pointView.setMarkerColor(ATKConstant.getColor4Quality("VALID"));
       
       getY1Axis().addDataView(pointView);
      // Commit change
       repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IAttributeStateListener#stateChange(fr.esrf.tangoatk.core.AttributeStateEvent)
     */
    public void stateChange(AttributeStateEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange(ErrorEvent arg0) {
    }

    /**
     * Main class, so you can have an example.
     * You can put your own attribute names in parameter
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        DualNumberScalarChart f = new DualNumberScalarChart();
        INumberScalar xattribute=null;
        INumberScalar yattribute=null;
        AttributePolledList attributeList = new AttributePolledList();
        try
        {
	        if (args != null && args.length > 1)
	        {
		        xattribute = (INumberScalar)attributeList.add(args[0].trim());
		        yattribute = (INumberScalar)attributeList.add(args[1].trim());
	        }
		    else
		    {
		        xattribute = (INumberScalar)attributeList.add("LT1/AE/CH.1/voltage");
		        yattribute = (INumberScalar)attributeList.add("LT1/AE/CH.2/voltage");
		    }
	        
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            System.exit(1);//RG comment : I added this code to avoid freeze
        }
        f.setDefaultYMaxValue(1);
        f.setDefaultYMinValue(-1);
        f.setDefaultXMaxValue(1);
        f.setDefaultXMinValue(-1);
        f.setMiddleLineVisible(true);
        f.setXYModel(xattribute,yattribute);
        attributeList.startRefresher();
        frame.getContentPane().add(f, BorderLayout.CENTER);
        frame.setSize(640, 480);
        
        frame.setTitle(f.getHeader() + " Example 1");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }
}

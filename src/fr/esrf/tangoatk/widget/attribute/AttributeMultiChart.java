/*	Synchrotron Soleil 
 *  
 *   File          :  AttributeMultiChart.java
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.chart.CfFileReader;
import fr.esrf.tangoatk.widget.util.chart.DataList;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;
import fr.esrf.tangoatk.widget.util.chart.OFormat;

/**
 * 
 * @author SOLEIL
 */
public class AttributeMultiChart extends JLChart 
                                 implements INumberScalarListener {

    protected String header = "Barchart";
    protected String xaxisName = "X";
    protected String unit = "unknown";
    protected double alarmLevel = 0.0;
    protected double faultLevel = 0.0;
    protected JLDataView alarm;
    protected JLDataView fault;
    protected JLDataView lineView;
    protected String lastConfig = "";
    protected int refreshingPeriod = 1000;
    protected Map dataViewHash = null;
    protected AttributeList model = null;
    protected int chartStyle;
    protected int markerStyle;

    protected JMenu multiChartMenu;
    protected JMenuItem barChartItem;

    protected JMenu dotChartMenu;
    protected JMenuItem dotItem;
    protected JMenuItem boxItem;
    protected JMenuItem triangleItem;
    protected JMenuItem diamondItem;
    protected JMenuItem starItem;
    protected JMenuItem vertLineItem;
    protected JMenuItem horizLineItem;
    protected JMenuItem crossItem;
    protected JMenuItem circleItem;
    protected JMenuItem squareItem;

    protected JMenu lineChartMenu;
    protected JMenuItem dotItem2;
    protected JMenuItem boxItem2;
    protected JMenuItem triangleItem2;
    protected JMenuItem diamondItem2;
    protected JMenuItem starItem2;
    protected JMenuItem vertLineItem2;
    protected JMenuItem horizLineItem2;
    protected JMenuItem crossItem2;
    protected JMenuItem circleItem2;
    protected JMenuItem squareItem2;
    
    protected JMenu YScale;
    protected JMenuItem logarithmic;
    protected JMenuItem linear;

    /**
     * int value representing the kind of chart "BarChart"
     */
    public final static int barChart = 0;

    /**
     * int value representing the kind of chart "DotChart"
     */
    public final static int dotChart = 1;

    /**
     * int value representing the kind of chart "LineChart"
     */
    public final static int lineChart = 2;

     /**
     * Default Constructor
     */
    public AttributeMultiChart()
    {
        super();
        getXAxis().setDrawOpposite(false);
        dataViewHash = new HashMap();
        setMarkerStyle(JLDataView.MARKER_STAR);
        initComponents();
        
        //Menu initialization
        addUserAction("Load configuration");
        addUserAction("Save configuration");
        addUserAction("Set Refresh Interval...");

        multiChartMenu = new JMenu("Chart Style");
        barChartItem = new JMenuItem("Bar Chart");
        dotChartMenu = new JMenu("Dot Chart (choose marker)");
        lineChartMenu = new JMenu("Line Chart (choose marker)");

        dotItem = new JMenuItem("dot");
        boxItem = new JMenuItem("box");
        triangleItem = new JMenuItem("triangle");
        diamondItem = new JMenuItem("diamond");
        starItem = new JMenuItem("star");
        vertLineItem = new JMenuItem("vertical line");
        horizLineItem = new JMenuItem("horizontal line");
        crossItem = new JMenuItem("cross");
        circleItem = new JMenuItem("circle");
        squareItem = new JMenuItem("square");

        dotItem2 = new JMenuItem("dot");
        boxItem2 = new JMenuItem("box");
        triangleItem2 = new JMenuItem("triangle");
        diamondItem2 = new JMenuItem("diamond");
        starItem2 = new JMenuItem("star");
        vertLineItem2 = new JMenuItem("vertical line");
        horizLineItem2 = new JMenuItem("horizontal line");
        crossItem2 = new JMenuItem("cross");
        circleItem2 = new JMenuItem("circle");
        squareItem2 = new JMenuItem("square");

        barChartItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(barChart);
            }
        });
        
        dotItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_DOT);
            }
        });
        boxItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_BOX);
            }
        });
        triangleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_TRIANGLE);
            }
        });
        diamondItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_DIAMOND);
            }
        });
        starItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_STAR);
            }
        });
        vertLineItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_VERT_LINE);
            }
        });
        horizLineItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_HORIZ_LINE);
            }
        });
        crossItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_CROSS);
            }
        });
        circleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_CIRCLE);
            }
        });
        squareItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(dotChart);
                setMarkerStyle(JLDataView.MARKER_SQUARE);
            }
        });
        
        dotItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_DOT);
            }
        });
        boxItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_BOX);
            }
        });
        triangleItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_TRIANGLE);
            }
        });
        diamondItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_DIAMOND);
            }
        });
        starItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_STAR);
            }
        });
        vertLineItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_VERT_LINE);
            }
        });
        horizLineItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_HORIZ_LINE);
            }
        });
        crossItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_CROSS);
            }
        });
        circleItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_CIRCLE);
            }
        });
        squareItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setChartStyle(lineChart);
                setMarkerStyle(JLDataView.MARKER_SQUARE);
            }
        });
        
        YScale = new JMenu("Y Axis Scale");
        logarithmic = new JMenuItem("logarithmic");
        linear = new JMenuItem("linear");
        
        logarithmic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getY1Axis().setScale(JLAxis.LOG_SCALE);
                repaint();
            }
        });
        linear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getY1Axis().setScale(JLAxis.LINEAR_SCALE);
                repaint();
            }
        });

        dotChartMenu.add(dotItem);
        dotChartMenu.add(boxItem);
        dotChartMenu.add(triangleItem);
        dotChartMenu.add(diamondItem);
        dotChartMenu.add(starItem);
        dotChartMenu.add(vertLineItem);
        dotChartMenu.add(horizLineItem);
        dotChartMenu.add(crossItem);
        dotChartMenu.add(circleItem);
        dotChartMenu.add(squareItem);

        lineChartMenu.add(dotItem2);
        lineChartMenu.add(boxItem2);
        lineChartMenu.add(triangleItem2);
        lineChartMenu.add(diamondItem2);
        lineChartMenu.add(starItem2);
        lineChartMenu.add(vertLineItem2);
        lineChartMenu.add(horizLineItem2);
        lineChartMenu.add(crossItem2);
        lineChartMenu.add(circleItem2);
        lineChartMenu.add(squareItem2);
        
        multiChartMenu.add(barChartItem);
        multiChartMenu.add(dotChartMenu);
        multiChartMenu.add(lineChartMenu);
        
        YScale.add(linear);
        YScale.add(logarithmic);
        
        addSeparator();
        addMenuItem(multiChartMenu);
        
        addMenuItem(YScale);
        
        // avoids user to change axis properties
        //removeMenuItem(JLChart.MENU_CHARTPROP);
        // avoids user to change dataViews colors
        removeMenuItem(JLChart.MENU_DVPROP);
    }
    

     /**
     * Constructor
     * @param chartStyle the kind of chart chosen
     * @see barChart
     * @see dotChart
     * @see lineChart
     */
    public AttributeMultiChart(int chartStyle) {
        this();
        setChartStyle(chartStyle);
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
        Set keySet = dataViewHash.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            INumberScalar ns = (INumberScalar) it.next();
            List tempList = (List) dataViewHash.get(ns);
            JLDataView tempView = (JLDataView) tempList.get(0);
            switch (chartStyle) {
            case barChart:
                break;

            case dotChart:
            case lineChart:
                tempView.setMarker(markerStyle);
                break;

            default:
                // we have a bug, so display nothing
                tempView.setMarker(JLDataView.MARKER_NONE);
                tempView.setViewType(JLDataView.TYPE_LINE);
            }

        }
        repaint();
    }

    // called by constructor
    // initializes every component, including axis
    protected void initComponents() {

        // Initialise chart properties
        setHeaderFont(new Font("Times", Font.BOLD, 18));
        switch (chartStyle) {
        case barChart:
            setHeader("BarChart");
            break;

        case dotChart:
            setHeader("DotChart");
            break;

        case lineChart:
            setHeader("LineChart");
            break;

        default:
            setHeader("");
        }
        setLabelVisible(false);
        setSize(640, 480);
        setXAxisOnBottom(false);

        // Initialise axis properties
        getY1Axis().setAutoScale(true);
        getY1Axis().setGridVisible(true);
        getY1Axis().setSubGridVisible(true);
        getXAxis().setAutoScale(false);
        getXAxis().setMinimum(0.0);
        getXAxis().setMaximum(0);
        getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
        getXAxis().setGridVisible(false);
        getXAxis().setSubGridVisible(false);

        initLevels();

        setPaintAxisFirst(false);
    }

    // initializes the fault and alarm levels
    // also initializes the line that joins the points for line Chart
    protected void initLevels() {
        // Alarm and Fault level line
        alarm = new JLDataView();
        alarm.setName("Alarm level");
        //        alarm.setStyle(JLDataView.STYLE_DASH);
        alarm.setColor(ATKConstant.getColor4State("ALARM"));
        alarm.setLineWidth(2);
        getY1Axis().addDataView(alarm);
        fault = new JLDataView();
        fault.setName("Fault level");
        //        fault.setStyle(JLDataView.STYLE_DASH);
        fault.setColor(ATKConstant.getColor4State("FAULT"));
        fault.setLineWidth(2);
        getY1Axis().addDataView(fault);
        lineView = new JLDataView();
        lineView.setColor(Color.black);
        lineView.setLineWidth(1);
        if (chartStyle == lineChart) {
            getY1Axis().addDataView(lineView);
        } else {
            getY1Axis().removeDataView(lineView);
        }
    }

    /**
     * Sets the attributes this chart will display.
     * It calls to clearmodel() first
     * @param attl the list of Attribute as an <code>AttributeList</code>
     */
    public void setModel(AttributeList attl) {
        int nbAtts, idx;
        boolean containsNumberScalar;
        Object elem;
        int nbNs;
        int bar_width = 10;

        if (model != null) {
            clearModel();
            model = null;
        }

        if (attl == null)
            return;

        nbAtts = attl.getSize();

        if (nbAtts <= 0)
            return;

        containsNumberScalar = false;

        for (idx = 0; idx < nbAtts; idx++) {
            elem = attl.getElementAt(idx);
            if (elem instanceof INumberScalar) {
                containsNumberScalar = true;
                break;
            }
        }

        if (containsNumberScalar == false)
            return;

        model = attl;
        refreshingPeriod = model.getRefreshInterval();
        nbNs = 0;
        for (idx = 0; idx < nbAtts; idx++) {
            elem = attl.getElementAt(idx);
            if (elem instanceof INumberScalar) {
                INumberScalar ins = (INumberScalar) elem;
                if (!dataViewHash.containsKey(ins)) // add only once each
                // NumberScalar
                {
                    JLDataView dvy_new = new JLDataView();
                    dvy_new.setUnit(ins.getUnit());
                    dvy_new.setName(ins.getName());
                    dvy_new.setColor(Color.black);
                    dvy_new.setLineWidth(1);
                    dvy_new.setBarWidth(bar_width);
                    dvy_new.setFillStyle(JLDataView.FILL_STYLE_SOLID);
                    if (chartStyle == barChart) {
                        dvy_new.setMarker(JLDataView.MARKER_NONE);
                        dvy_new.setViewType(JLDataView.TYPE_BAR);
                    } else {
                        dvy_new.setMarker(JLDataView.MARKER_STAR);
                        dvy_new.setViewType(JLDataView.TYPE_LINE);
                    }
                    getY1Axis().addDataView(dvy_new);
                    ins.addNumberScalarListener(this);

                    List list = new Vector();
                    Integer XaxisValue = new Integer(nbNs);
                    list.add(0, dvy_new);
                    list.add(1, XaxisValue);
                    dataViewHash.put(ins, list);
                    nbNs++;
                }
            }
        }

        getXAxis().setMaximum(nbNs + 1);
        getXAxis().setTickSpacing(nbNs);
        //  	    System.out.println("bar_width :" + bar_width);
    }

    /**
     * Clears the chart
     */
    public void clearModel() {
        int nbAtts, idx;
        Object elem;

        if (model == null)
            return;

        nbAtts = model.getSize();

        if (nbAtts <= 0)
            return;

        for (idx = 0; idx < nbAtts; idx++) {
            elem = model.getElementAt(idx);
            if (elem instanceof INumberScalar) {
                INumberScalar ins = (INumberScalar) elem;
                if (dataViewHash.containsKey(ins)) {
                    ins.removeNumberScalarListener(this);
                }
            }
        }

        // Remove all dataviews and numberScalars from HMap and from JLChart
        getY1Axis().clearDataView();
        dataViewHash.clear();
        initLevels();
    }

    /**
     * Sets the width of the bars of the bar chart
     * @param bar_width the width
     */
    public void setWidth(int bar_width) {
        int nbAtts, idx;
        Object elem;

        if (model == null)
            return;

        nbAtts = model.getSize();

        if (nbAtts <= 0)
            return;

        for (idx = 0; idx < nbAtts; idx++) {
            elem = model.getElementAt(idx);
            if (elem instanceof INumberScalar) {
                INumberScalar ins = (INumberScalar) elem;
                if (dataViewHash.containsKey(ins)) {
                    java.util.List dvyAndIndex = (java.util.List) dataViewHash
                            .get(ins);
                    if (dvyAndIndex == null)
                        continue;
                    int nbObjs = dvyAndIndex.size();
                    if (nbObjs < 2)
                        continue;
                    Object obj = dvyAndIndex.get(0);
                    if (obj == null)
                        continue;
                    if (!(obj instanceof JLDataView))
                        continue;
                    JLDataView dvy = (JLDataView) obj;
                    dvy.setBarWidth(bar_width);
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.INumberScalarListener#numberScalarChange(fr.esrf.tangoatk.core.NumberScalarEvent)
     */
    public void numberScalarChange(NumberScalarEvent numberScalarEvent) {
        INumberScalar source = (INumberScalar) numberScalarEvent.getSource();

        if (dataViewHash.containsKey(source)) {
            List dvyAndIndex = (List) dataViewHash.get(source);
            if (dvyAndIndex == null)
                return;

            int nbObjs = dvyAndIndex.size();

            if (nbObjs < 2)
                return;

            Object obj = dvyAndIndex.get(0);

            if (obj == null)
                return;

            if (!(obj instanceof JLDataView))
                return;

            JLDataView dvy = (JLDataView) obj;

            obj = dvyAndIndex.get(1);

            if (obj == null)
                return;

            if (!(obj instanceof Integer))
                return;

            int dvyIndex = ((Integer) obj).intValue();

            double yvalue = numberScalarEvent.getValue();
            dvy.reset();
            dvy.add((double) (dvyIndex + 1), yvalue);

            if (yvalue > faultLevel) {
                dvy.setFillColor(ATKConstant.getColor4State("FAULT"));
                dvy.setMarkerColor(ATKConstant.getColor4State("FAULT"));
            } else if (yvalue > alarmLevel) {
                dvy.setFillColor(ATKConstant.getColor4State("ALARM"));
                dvy.setMarkerColor(ATKConstant.getColor4State("ALARM"));
            } else {
                dvy.setFillColor(Color.green);
                dvy.setMarkerColor(Color.green);
            }

            //update line
            lineView.reset();
            Set keySet = dataViewHash.keySet();
            Iterator it = keySet.iterator();
            DataList[] position = new DataList[keySet.size()];
            for (int i = 0; i < position.length; i++) {
                position[i] = null;
            }
            while (it.hasNext()) {
                INumberScalar ns = (INumberScalar) it.next();
                List tempList = (List) dataViewHash.get(ns);
                JLDataView tempView = (JLDataView) tempList.get(0);
                Integer tempInteger = (Integer) tempList.get(1);
                DataList tempData = tempView.getData();
                position[tempInteger.intValue()] = tempData;
            }
            for (int i = 0; i < position.length; i++) {
                if (position[i] != null) {
                    lineView.add(i + 1, position[i].y);
                }
            }

            // Commit change
            repaint();
        }
    }

    /**
     * 
     * @return an int representing the kind of chart used.
     * @see barChart
     * @see dotChart
     * @see lineChart
     */
    public int getChartStyle() {
        return chartStyle;
    }

    /**
     * Allows you to choose which kind of chart you want to use
     * @param style the kind of chart you want to use
     * @see barChart
     * @see dotChart
     * @see lineChart
     */
    public void setChartStyle(int style) {
        chartStyle = style;
        Set keySet = dataViewHash.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            INumberScalar ns = (INumberScalar) it.next();
            List tempList = (List) dataViewHash.get(ns);
            JLDataView tempView = (JLDataView) tempList.get(0);
            switch (chartStyle) {
            case barChart:
                tempView.setMarker(JLDataView.MARKER_NONE);
                tempView.setViewType(JLDataView.TYPE_BAR);
                getY1Axis().removeDataView(lineView);
                break;

            case dotChart:
                tempView.setMarker(markerStyle);
                tempView.setViewType(JLDataView.TYPE_LINE);
                getY1Axis().removeDataView(lineView);
                break;

            case lineChart:
                tempView.setMarker(markerStyle);
                tempView.setViewType(JLDataView.TYPE_LINE);
                getY1Axis().addDataView(lineView);
                break;

            default:
                // we have a bug, so display nothing
                tempView.setMarker(JLDataView.MARKER_NONE);
                tempView.setViewType(JLDataView.TYPE_LINE);
            }

        }
        repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IAttributeStateListener#stateChange(fr.esrf.tangoatk.core.AttributeStateEvent)
     */
    public void stateChange(AttributeStateEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange(ErrorEvent arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * @return Returns the alarm_level.
     */
    public double getAlarmLevel() {
        return alarmLevel;
    }

    /**
     * @param alarm_level
     *            The alarm_level to set.
     */
    public void setAlarmLevel(double alarm_level) {
        this.alarmLevel = alarm_level;
        alarm.add(Integer.MIN_VALUE, alarm_level);
        alarm.add(Integer.MAX_VALUE, alarm_level);
        //	    alarm.add(current_model_number,alarm_level);
    }

    /**
     * @return Returns the fault_level.
     */
    public double getFaultLevel() {
        return faultLevel;
    }

    /**
     * @param fault_level
     *            The fault_level to set.
     */
    public void setFaultLevel(double fault_level) {
        this.faultLevel = fault_level;
        fault.add(Integer.MIN_VALUE, fault_level);
        fault.add(Integer.MAX_VALUE, fault_level);
        //	    fault.add(current_model_number,fault_level);
    }

    /**
     * @return Returns the header.
     */
    public String getChartHeader() {
        return header;
    }

    /**
     * @param header
     *            The header to set.
     */
    public void setChartHeader(String header) {
        this.header = header;
        setHeader(header);
    }

    /**
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     *            The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
        getY1Axis().setName(unit);
        alarm.setUnit(unit);
        fault.setUnit(unit);
    }

    /**
     * @return Returns the xaxis_name.
     */
    public String getXaxisName() {
        return xaxisName;
    }

    /**
     * @param xaxis_name
     *            The xaxis_name to set.
     */
    public void setXaxisName(String xaxis_name) {
        this.xaxisName = xaxis_name;
        getXAxis().setName(xaxis_name);
    }

    /**
     * @return The String that will be written in a configuration file
     */
    public String getSettings() {

        int nbAtts, nbNs;
        Object elem;
        String s = "";
        s = s + this.getConfiguration();
        s = s + "chartStyle:" + this.chartStyle + "\n";
        s = s + "markerStyle:" + this.markerStyle + "\n";
        s = s + "alarm:" + this.getAlarmLevel() + "\n";
        s = s + "fault:" + this.getFaultLevel() + "\n";
        if (model != null) {
            s = s + "refresh_time:" + this.refreshingPeriod + "\n";
        }
        s = s + this.getXAxis().getConfiguration("x");
        s = s + this.getY1Axis().getConfiguration("y1");
        s = s + this.getY2Axis().getConfiguration("y2");

        nbAtts = model.getSize();
        nbNs = 0;
        for (int idx = 0; idx < nbAtts; idx++) {
            elem = model.getElementAt(idx);
            if (elem instanceof INumberScalar) {
                INumberScalar ins = (INumberScalar) elem;
                if (dataViewHash.containsKey(ins))
                    nbNs++;
            }
        }

        s = s + "dv_number:" + nbNs + "\n";

        nbNs = 0;

        for (int idx = 0; idx < nbAtts; idx++) {
            elem = model.getElementAt(idx);
            if (elem instanceof INumberScalar) {
                INumberScalar ins = (INumberScalar) elem;
                if (dataViewHash.containsKey(ins)) {
                    java.util.List dvyAndIndex = (java.util.List) dataViewHash
                            .get(ins);
                    if (dvyAndIndex == null)
                        continue;

                    int nbObjs = dvyAndIndex.size();

                    if (nbObjs < 2)
                        continue;

                    Object obj = dvyAndIndex.get(0);

                    if (obj == null)
                        continue;

                    if (!(obj instanceof JLDataView))
                        continue;

                    Object obj2 = dvyAndIndex.get(1);

                    if (obj2 == null)
                        continue;

                    if (!(obj2 instanceof Integer))
                        continue;

                    int dvyIndex = ((Integer) obj2).intValue();

                    JLDataView dvy = (JLDataView) obj;
                    s = s + "dv" + dvyIndex + "_name:'" + ins + "'\n";
                    s = s + dvy.getConfiguration("dv" + dvyIndex);
                }
            }
        }
        return s;
    }

    /**
     * Saves the configuration of the chart in a file
     * @param s the file path
     */
    public void saveSetting(String s) {
        try {
            FileWriter filewriter = new FileWriter(s);
            String s1 = getSettings();
            filewriter.write(s1, 0, s1.length());
            filewriter.close();
            lastConfig = s;
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this.getParent(), "Failed to write "
                    + s, "Error", 0);
            exception.printStackTrace();
        }
    }

    /**
     * Loads configuration from a file
     * @param s the file path
     * @return a String that is empty if the loading is all right, or
     * contains an error message otherwise
     */
    public String loadSetting(String s) {
        CfFileReader cffilereader = new CfFileReader();
        if (!cffilereader.readFile(s)) {
            return "Failed to read " + s;
        } else {
            lastConfig = s;
            return applySettings(cffilereader);
        }
    }

    /**
     * @return the refreshing period of the associated <code>AttributeList</code>
     */
    public int getRefreshingPeriod() {
        if (model == null)
            return 0;

        refreshingPeriod = model.getRefreshInterval();
        return refreshingPeriod;
    }

    /**
     * sets the refreshing period of the associated <code>AttributeList</code>
     * @param refreshingPeriod the refreshing period
     */
    public void setRefreshingPeriod(int refreshingPeriod) {
        this.refreshingPeriod = refreshingPeriod;
        if (model == null)
            return;
        model.stopRefresher();
        model.setRefreshInterval(refreshingPeriod);
        model.startRefresher();
    }

    //Applies the configuration from a file readen by the CfFileReader
    protected String applySettings(CfFileReader cffilereader) {
        String s = "";
        String attributeListTmp = "";
        Vector vector = cffilereader.getParam("dv_number");
        int k;
        if (vector != null) {
            try {
                k = Integer.parseInt(vector.get(0).toString());
            } catch (NumberFormatException numberformatexception) {
                s = s + "dv_number: invalid number\n";
                return s;
            }
            for (int i = 0; i < k; i++) {
                vector = cffilereader.getParam("dv" + i + "_name");
                if (vector == null) {
                    s = s + "Unable to find dv" + i + "_name param\n";
                    return s;
                }
                try {
                    attributeListTmp = attributeListTmp
                            + vector.get(0).toString() + ",";
                } catch (Exception exception) {
                    s = s + exception.getMessage() + "\n";
                    return s;
                }
            }
            //System.out.println("k="+k);
            if (k > 0) {
                vector = cffilereader.getParam("refresh_time");
                if (vector != null) {
                    setRefreshingPeriod(OFormat
                            .getInt(vector.get(0).toString()));
                }
                //System.out.println("attributeListTmp="+attributeListTmp);
                attributeListTmp = attributeListTmp.substring(0,
                        attributeListTmp.lastIndexOf(","));
                setAttributeListAsString(attributeListTmp);
            }
        } else {
            k = 0;
        }
        this.applyConfiguration(cffilereader);
        this.getXAxis().applyConfiguration("x", cffilereader);
        this.getY1Axis().applyConfiguration("y1", cffilereader);
        this.getY2Axis().applyConfiguration("y2", cffilereader);
        vector = cffilereader.getParam("dv" + 0 + "_barwidth");
        if (vector == null) {
            s = s + "Unable to find dv" + 0 + "_barwidth param";
            return s;
        }
        setWidth(Integer.parseInt(vector.get(0).toString().trim()));
        vector = cffilereader.getParam("xtitle");
        if (vector == null) {
            s = s + "Unable to find xtitle param";
            return s;
        }
        setXaxisName(vector.get(0).toString().replaceAll("'", "").trim());
        vector = cffilereader.getParam("graph_title");
        if (vector == null) {
            s = s + "Unable to find graph_title param";
            return s;
        }
        setChartHeader(vector.get(0).toString().replaceAll("'", "").trim());
        vector = cffilereader.getParam("y1title");
        if (vector == null) {
            s = s + "Unable to find y1title param";
            return s;
        }
        setUnit(vector.get(0).toString().replaceAll("'", "").trim());
        vector = cffilereader.getParam("chartStyle");
        if (vector == null) {
            s = s + "Unable to find chartStyle param";
            return s;
        }
        setChartStyle(Integer.valueOf(
                vector.get(0).toString().replaceAll("'", "").trim()).intValue());
        vector = cffilereader.getParam("markerStyle");
        if (vector == null) {
            s = s + "Unable to find markerStyle param";
            return s;
        }
        setMarkerStyle(Integer.valueOf(
                vector.get(0).toString().replaceAll("'", "").trim()).intValue());
        vector = cffilereader.getParam("alarm");
        if (vector == null) {
            s = s + "Unable to find alarm param";
            return s;
        }
        setAlarmLevel(Double.valueOf(
                vector.get(0).toString().replaceAll("'", "").trim()).intValue());
        vector = cffilereader.getParam("fault");
        if (vector == null) {
            s = s + "Unable to find fault param";
            return s;
        }
        setFaultLevel(Double.valueOf(
                vector.get(0).toString().replaceAll("'", "").trim()).intValue());
        return s;
    }

    /*
     * Constructs the attribute List with the String given in parameter.
     * attributeListAsString represents the attributes' names.
     * Separator is ","
     * This means that for 4 attributes,  your String has to look like this :
     * "attributeName1,attributeName2,attributeName3,attributeName4"
     */
    protected void setAttributeListAsString(String attributeListAsString) {
        String[] attrList = attributeListAsString.split(",");
        AttributeList attributeList = new AttributeList();
        for (int i=0; i<attrList.length; i++) {
            try {
                attributeList.add(attrList[i]);
            }
            catch (ConnectionException e) {
                JOptionPane.showMessageDialog(this, "Failed to connect to "
                        + attrList[i], "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        setModel(attributeList);
    }

    /**
     * @see fr.esrf.tangoatk.widge.util.chart.JLChart#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().trim()
                .equalsIgnoreCase("Load configuration")) {
            loadPerformed();
        } else if (evt.getActionCommand().trim().equalsIgnoreCase(
                "Save configuration")) {
            savePerformed();
        } else if (evt.getActionCommand().trim().equalsIgnoreCase(
                "Set Refresh Interval...")) {
            int ref_period = -1;
            ref_period = getRefreshingPeriod();
            String refp_str = JOptionPane.showInputDialog(this,
                    "Enter refresh interval (ms)", (Object) new Integer(
                            ref_period));

            if (refp_str != null) {
                if (refp_str.length() > 0) {
                    try {
                        int period_int = Integer.parseInt(refp_str);
                        setRefreshingPeriod(period_int);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid number !",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } else {
            super.actionPerformed(evt);
        }
    }

    // manages the click on the option "load configuration"
    protected void loadPerformed() {

        if (model != null)
            model.stopRefresher();

        clearModel();

        /*        double fault = faultLevel;
         double alarm = alarmLevel;
         initComponents();
         setFaultLevel(fault);
         setAlarmLevel(alarm);*/
        boolean flag = false;
        JFileChooser jfilechooser = new JFileChooser();
        jfilechooser.setSelectedFile(new File(lastConfig));
        jfilechooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String extension = getExtension(f);
                if (extension != null && extension.equals("txt"))
                    return true;
                return false;
            }

            public String getDescription() {
                return "text files ";
            }
        });
        int i = jfilechooser.showOpenDialog(this.getParent());
        if (i == JFileChooser.APPROVE_OPTION) {
            File file = jfilechooser.getSelectedFile();
            if (file != null && !flag) {
                String s = loadSetting(file.getAbsolutePath());
                if (s.length() > 0)
                    JOptionPane.showMessageDialog(this.getParent(), s,
                            "Errors reading " + file.getName(), 0);
            }
        }
        repaint();
    }

    // manages the click on the option "save configuration"
    protected void savePerformed() {
        int i = 0;
        JFileChooser jfilechooser = new JFileChooser(".");
        jfilechooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String extension = getExtension(f);
                if (extension != null && extension.equals("txt"))
                    return true;
                return false;
            }

            public String getDescription() {
                return "text files ";
            }
        });
        jfilechooser.setSelectedFile(new File(lastConfig));
        int j = jfilechooser.showSaveDialog(this.getParent());
        if (j == JFileChooser.APPROVE_OPTION) {
            File file = jfilechooser.getSelectedFile();
            if (getExtension(file) == null) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            if (file != null) {
                if (file.exists())
                    i = JOptionPane
                            .showConfirmDialog(this.getParent(),
                                    "Do you want to overwrite "
                                            + file.getName() + " ?",
                                    "Confirm overwrite", 0);
                if (i == JOptionPane.OK_OPTION) {
                    saveSetting(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * <code>getExtension</code> returns the extension of a given file, that
     * is the part after the last `.' in the filename.
     * 
     * @param f
     *            a <code>File</code> value
     * @return a <code>String</code> value
     */
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Main class, so you can have an example.
     * You can put your own attribute names in parameter
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        AttributeMultiChart f = new AttributeMultiChart(
                AttributeMultiChart.barChart);
        AttributeList attributeList = new AttributeList();
        f.setXAxisOnBottom(false);
        try {
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    attributeList.add(args[i]);
                }
            }
            else {
                attributeList.add("LT1/AE/CH.1/voltage");
                attributeList.add("LT1/AE/CH.2/voltage");
                attributeList.add("LT1/AE/CH.3/voltage");
            }
        }
        catch (ConnectionException e) {
            e.printStackTrace();
            System.exit(1);//RG comment : I added this code to avoid freeze
        }
        f.setModel(attributeList);
        attributeList.startRefresher();
        f.setWidth(10);
        f.setXaxisName("Pressures");
        f.setUnit("mbar");
        f.setHeader("LT1");
        f.setFaultLevel(2.0);
        f.setAlarmLevel(1.0);
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

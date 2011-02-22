// File:          plot1.java
// Created:       2002-01-07 11:09:54, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-19 14:30:22, assum>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.widget.attribute;

import java.awt.GridLayout;
import javax.swing.*;
import java.util.*;
import com.klg.jclass.util.swing.JCExitFrame;
import com.klg.jclass.chart.*;
import com.klg.jclass.chart.data.*;
import java.beans.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.util.*;
/**
 *Basic example of Chart use.  Load data from
 *a file and displays it as a simple plot chart.
 */
public class JChartAttributeViewer extends JPanel {
    JCChart chart;
    JChartAdapter adapter;
    public JChartAttributeViewer() {
	setLayout(new GridLayout(1,1));
	setChart(new JCChart());
	setAdapter(new JChartAdapter());
	chart.getDataView(0).setChartType(JCChart.PLOT);
	chart.getDataView(0).setFastUpdate(false);
	add(chart);
    }

    public void setModel(INumberScalar iNumberScalar) {
	System.out.println("JCHartAttributeViewer: setting model " + iNumberScalar);
	adapter.setModel(iNumberScalar);
    }

    public JChartAdapter getAdapter() {
	return adapter;
    }

    public void setAdapter(JChartAdapter adapter) {
	this.adapter = adapter;
	adapter.setChart(getChart());
    }
    
    public JCChart getChart() {
	return chart;
    }

    public void setChart(JCChart chart) {
	this.chart = chart;
    }
    

    public static void main(String args[]) {
	JCExitFrame f = new JCExitFrame("Plot1");
	JChartAttributeViewer p = new JChartAttributeViewer();
	fr.esrf.tangoatk.core.AttributeList list =
	    new fr.esrf.tangoatk.core.AttributeList();
	JChartAdapter adapter = p.getAdapter();
	adapter.setChart(p.getChart());

	try {
	    adapter.setModel((INumberScalar)list.add("eas/test-api/1/Att_sinus1"));
	    adapter.setModel((INumberScalar)list.add("eas/test-api/1/Att_sinus2"));
	    adapter.setModel((INumberScalar)list.add("eas/test-api/1/Att_sinus3"));
	    adapter.setModel((INumberScalar)list.add("eas/test-api/1/Att_sinus4"));
	    adapter.setModel((INumberScalar)list.add("eas/test-api/1/Att_sinus5"));
	    list.setRefreshInterval(50);
	    list.startRefresher();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} // end of try-catch
	
	
	f.getContentPane().add(p);
	//	f.setSize(300, 400);
	f.setVisible(true);
	f.pack();
	f.show();
    }
}

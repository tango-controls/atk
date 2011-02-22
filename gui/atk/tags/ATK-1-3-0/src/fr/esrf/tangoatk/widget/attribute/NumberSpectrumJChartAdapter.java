// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   NumberSpectrumJChartAdapter.java

package fr.esrf.tangoatk.widget.attribute;

import com.klg.jclass.chart.*;
import com.klg.jclass.chart.beans.SimpleChart;
import com.klg.jclass.chart.data.ChartDataSourceUtil;
import fr.esrf.tangoatk.core.*;
import java.awt.Window;
import java.io.PrintStream;
import javax.swing.JFrame;

// Referenced classes of package fr.esrf.TangoATK.Widget.Attribute:
//      ChartDataEventAdapter

public class NumberSpectrumJChartAdapter implements
					     ISpectrumListener,
					     ChartDataManageable,
					     ChartDataModel,
					     ChartDataManager {

    public void spectrumChange(NumberSpectrumEvent evt) {
	double ad[] = evt.getValue();
	if (ad == null) {
	    return;
	} else {
	    dataEvent.setAll(13, 0, 0);
	    yaxis = ad;
	    xaxis =
		ChartDataSourceUtil.generateSingleXSeries(yaxis.length)[0];
	    dataView.chartDataChange(dataEvent);
	    return;
	}
    }

    public void addChartDataListener(ChartDataListener chartdatalistener) {
	System.out.println("adding listener  " + chartdatalistener);
	dataView = (ChartDataView)chartdatalistener;
    }

    public void removeChartDataListener(ChartDataListener chartdatalistener) {
	System.out.println("removing listener  " + chartdatalistener);
	dataView = null;
    }

    public ChartDataManager getChartDataManager() {
	return this;
    }

    public int getNumSeries() {
	return 1;
    }

    public double[] getXSeries(int i) {
	return xaxis;
    }

    public double[] getYSeries(int i) {
	return yaxis;
    }

    public void stateChange(AttributeStateEvent attributestateevent) {
    }

    public void errorChange(ErrorEvent errorevent) {
	System.out.println(errorevent.getError());
    }

    public void setModel(INumberSpectrum inumberspectrum) {
	if (model != null)
	    model.removeSpectrumListener(this);
	model = inumberspectrum;
	model.addSpectrumListener(this);
	dataView.chartDataChange(dataEvent);
	dataView.setDataSource(this);
    }

    public INumberSpectrum getModel() {
	return model;
    }
    /**
     * <code>setChart</code>
     *
     * @param jcchart a <code>JCChart</code> value
     * @deprecated use setViewer(JCChart jcchart) instead
     */
    public void setChart(JCChart jcchart) {
	setViewer(jcchart);
    }

    public void setViewer(JCChart jcchart) {
	chart = jcchart;
	dataView = jcchart.getDataView(0);
	dataEvent = new ChartDataEventAdapter(this, 0, 0, 0);
    }

    /**
     * <code>getChart</code>
     *
     * @return a <code>JCChart</code> value
     * @deprecated use getViewer instead
     */
    public JCChart getChart() {
	return getViewer();
    }

    public JCChart getViewer() {
	return chart;
    }

    public static void main(String args[]) throws Exception {
	fr.esrf.tangoatk.core.AttributeList attributelist =
	    new fr.esrf.tangoatk.core.AttributeList();
	INumberSpectrum inumberspectrum = (INumberSpectrum)attributelist.add("eas/test-api/1/Short_spec_attr");
	NumberSpectrumJChartAdapter numberspectrumjchartadapter = new NumberSpectrumJChartAdapter();
	SimpleChart simplechart = new SimpleChart();
	numberspectrumjchartadapter.setChart(simplechart);
	numberspectrumjchartadapter.setModel(inumberspectrum);
	JFrame jframe = new JFrame();
	jframe.setContentPane(simplechart);
	jframe.pack();
	jframe.show();
	attributelist.startRefresher();
    }

    public NumberSpectrumJChartAdapter() {
    }

    protected INumberSpectrum model;
    protected JCChart chart;
    protected double yaxis[];
    protected double xaxis[];
    ChartDataView dataView;
    ChartDataEventAdapter dataEvent;
}



// File:          JChartAdapter.java
// Created:       2002-02-19 11:22:29, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-11 15:45:1, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.attribute;
import fr.esrf.tangoatk.core.*;
import com.klg.jclass.chart.*;
import com.klg.jclass.chart.data.*;

/**
 * <code>JChartAdapter</code> is an adapter-class which knows how to 
 * translate between ATK-like data and the data-format required by JCChart.
 * Think of it as a pipe where you need to plug one or more INumberScalars
 * in one end of the pipe and a JCChart in the other end<br>
 * <code>
 * JChartAdapter adapter = new JChartAdapter();<br>
 * adapter.setChart(myJCChart);<br>
 * adapter.setModel(myINumberScalar);<br>
 * </code>
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public class JChartAdapter extends JCDefaultDataSource
    implements INumberScalarListener,
               IRefreshee {
    boolean first = true;

    protected ARefresher refresher = null;
    String [] names = new String[1];
    INumberScalar[] models = new INumberScalar[1];
    int series = -1;
    double[][] y = new double[0][];
    double[] x = new double[0];
    int [] indecies = new int[1];
    double samplingRate = 1000;
    boolean sampling = false;
    int refreshInterval = 1000;
    boolean reserialized = false;

    public JChartAdapter() {
	setCloneArrays(true);
    }
    
    public void setRefreshInterval(int milliSeconds) {
	refreshInterval = milliSeconds;
    }

    public int getRefreshInterval() {
	return refreshInterval;
    }

    public void stopRefresher() {
	if (refresher != null) 
	    refresher.stop = true;
	refresher = null;
    }

    public void startRefresher() {
	if (refresher == null) {
	    refresher = new ARefresher("GraphUpdater") {

		    IRefreshee refreshee;

		    public Thread addRefreshee(IRefreshee e) {

			refreshee = e;
			setPriority(Thread.MAX_PRIORITY);
			return this;
		    }

		    public void run() {
			try {
			    while (true) {
				sleep(refreshInterval);
				if (stop) {
				    return;
				}
				refreshee.refresh();
			    }
			} catch (Exception e) {
			    ;
			}
		    }
		};
	}
	refresher.addRefreshee(this).start();
    }


    long [] previousUpdate = new long[1];

    int xAxisLength = 50;

    double maxValue;

    double minValue;


    public String getName(int i) {
	return models[i].getName();
    }
    /**
     * Get the value of minValue.
     * @return value of minValue.
     */
    public double getMinValue() {
	return minValue;
    }
    
    /**
     * Set the value of minValue.
     * @param v  Value to assign to minValue.
     */
    public void setMinValue(double  v) {
	this.minValue = v;
    }
    
    /**
     * Get the value of maxValue.
     * @return value of maxValue.
     */
    public double getMaxValue() {
	return maxValue;
    }
    
    /**
     * Set the value of maxValue.
     * @param v  Value to assign to maxValue.
     */
    public void setMaxValue(double  v) {
	this.maxValue = v;
    }
    
    
    /**
     * Get the value of xaxislength.
     * @return value of xaxislength.
     */
    public int getXAxisLength() {
	return xAxisLength;
    }
    
    /**
     * Set the value of xaxislength.
     * @param v  Value to assign to xaxislength.
     */
    public synchronized void setXAxisLength(int  v) {

	if (v == xAxisLength) return;
	
	int mySeries = series + 1;

	int newLength = v < xAxisLength ? v : xAxisLength;


	
	this.xAxisLength = v;

	
	double [] tmpx = new double[xAxisLength];
	System.arraycopy(x, 0, tmpx, 0, xAxisLength);
	x = tmpx;

	double [][] tmp = new double[mySeries][];
	System.out.println("created new double[" + mySeries + "]");
	System.arraycopy(y, 0, tmp, 0, mySeries);

	for (int j = 0; j < mySeries; j++) {
	    tmp[j] = new double[xAxisLength];
	    System.out.println("tmp[" + j + "] = " + " new double[" +
			       xAxisLength + "]");
	    System.arraycopy(y[j], 0, tmp[j], 0, newLength);
	    
	} // end of for ()
	y = tmp;

	setData(null, y);
    }
    
    /**
     * Get the value of samplingRate.
     * @return value of samplingRate.
     */
    public double getSamplingRate() {
	return 1/(samplingRate/1000);
    }
    
    /**
     * Set the value of samplingRate.
     * @param v  Value to assign to samplingRate.
     */
    public void setSamplingRate(double  v) {
	this.samplingRate = (1/v) * 1000;
	sampling = true;
    }
    
    JCChart chart;
    
    /**
     * Get the value of chart.
     * @return value of chart.
     */
    public JCChart getChart() {
	return chart;
    }
    
    /**
     * Set the value of chart.
     * @param v  Value to assign to chart.
     */
    public void setChart(JCChart  v) {
	this.chart = v;
	JCAxis xaxis = chart.getDataView(0).getXAxis();
	xaxis.setAnnotationMethod(JCAxis.TIME_LABELS);
	xaxis.setTimeUnit(JCAxis.SECONDS);
	xaxis.setTimeBase(new java.util.Date(0));
    }
    
    /**
     * Default constructor for this class.  Loads data and
     * sets up chart.
     */

    public void numberScalarChange(NumberScalarEvent evt) {

	int serie = findModel(evt.getNumberSource());
	long timestamp = evt.getTimeStamp();

	if (sampling) {
	    long diff = timestamp - previousUpdate[serie];
	    
	    if (diff < samplingRate) return;

	    previousUpdate[serie] = timestamp;
	}

	double d = evt.getValue();

	if (indecies[serie] > xAxisLength) indecies[serie] = xAxisLength;

	if (x.length == indecies[serie] && x.length < xAxisLength) {
	    double [] tmpx = new double[x.length + 1];
	    System.arraycopy(x, 0, tmpx, 0, x.length);
	    x = tmpx;
	}
	
	if (indecies[serie] == xAxisLength) {
	    System.arraycopy(y[serie], 1, y[serie], 0, xAxisLength -1);
	    y[serie][xAxisLength -1] = d;

	    System.arraycopy(x, 1, x, 0, xAxisLength -1);
	    x[xAxisLength -1] = timestamp / 1000;

	} else {
	    y[serie][indecies[serie]] = d;
	    x[indecies[serie]] = timestamp / 1000;
	    ++indecies[serie];
	}
    }


    public void refresh() {
	long start = System.currentTimeMillis();
	int serie;
	boolean reloadAll = false;

	updateChart(ChartDataEvent.RELOAD, 0, 0);
    }
	
    public void errorChange(ErrorEvent evt) {

    }

    public void stateChange(AttributeStateEvent evt) {

    }

    public INumberScalar getModel() {
	return models[0];
    }

    int findModel(INumberScalar iNumberScalar) {
	int i = 0;
	
	for (; i < models.length; i++) {
	    if (models[i] == iNumberScalar) return i;
	} // end of for ()
	return -1;
    }
	
    public boolean hasModel(INumberScalar iNumberScalar) {
	if (findModel(iNumberScalar) != -1) return true;
	return false;
    }

    public synchronized void removeModel(INumberScalar iNumberScalar) {
	int serie = findModel(iNumberScalar);

	if (serie == -1) return;



	models[serie] = models[series];
	models[series] = null;
	names[serie]  = names[series];
	names[series] = null;
	previousUpdate[serie] = previousUpdate[series];
	indecies[serie] = indecies[series];
	y[serie] = y[series];

	double [][]tmp = new double[series][];
	for (int i = 0; i < tmp.length; i++) {
	    System.arraycopy(y, 0, tmp, 0, tmp.length);
	} // end of for ()
	y = tmp;
	setData(null, y);

	series--;	
	updateChart(ChartDataEvent.RELOAD_ALL_SERIES_LABELS, 0, 0);
	

    }

    public void setShowingDeviceNames(boolean b) {
	showingDeviceNames = b;
    }

    public boolean isShowingDeviceNames() {
	return showingDeviceNames;
    }

    boolean showingDeviceNames = true;

    /**
     * <code>setModel</code> hook an attribute into the one end of this
     * adapter. Don't forget to hook a dataView into the other end first.
     *
     * @param iNumberScalar an <code>INumberScalar</code> value
     * @see setChart
     */
    public synchronized void setModel(INumberScalar iNumberScalar) {
	series++;

	if (series >= previousUpdate.length) {
	    long [] tmp = new long[series + 1];
	    System.arraycopy(previousUpdate, 0, tmp,
			     0, previousUpdate.length);
	    previousUpdate = tmp;
	}
	
	if (series >= models.length) {
	    INumberScalar [] tmp = new INumberScalar[series + 1];
	    System.arraycopy(models, 0, tmp, 0, models.length);
	    models = tmp;
	}
	if (series >= indecies.length) {
	    int [] tmp = new int[series + 1];
	    System.arraycopy(indecies, 0, tmp, 0, indecies.length);
	    indecies = tmp;
	    indecies[series] = indecies[series - 1];
	}

	if (series >= names.length) {
	    String [] tmp = new String[series + 1];
	    System.arraycopy(names, 0, tmp, 0, names.length);
	    names = tmp;

	}

	names[series]  = showingDeviceNames ? iNumberScalar.getName() :
	    iNumberScalar.getNameSansDevice();
	models[series] = iNumberScalar;

	updateChart(ChartDataEvent.RELOAD_ALL_SERIES_LABELS, 0, 0);

	if (iNumberScalar.getMaxValue() > maxValue) {
	    maxValue = iNumberScalar.getMaxValue();
	}

	if (iNumberScalar.getMinValue() < minValue) {
	    minValue = iNumberScalar.getMinValue();
	}
	
//	System.out.println("series = " + series + " models.length = " +
//			   models.length);

	if (series >= y.length) {
	    double [][] tmp = new double[ series + 1][];
	    System.arraycopy(y, 0, tmp, 0, y.length);

	    for (int j = y.length; j < series; j++) {
		tmp[j] = y[j];
	    } // end of for ()
	    tmp[series] = new double[xAxisLength];
	    y = tmp;

	}
	
	iNumberScalar.addNumberScalarListener(this);

	if (!(series == 0)) {
	    updateChart(ChartDataEvent.ADD_SERIES, 0, 0);	    
	} else {
	    setData(null, y);
	    chart.getDataView(0).setDataSource(this);
	    startRefresher();
	} // end of else
    }
    
    public int getNumSeries() {
        if(y == null) return 0;

	return y.length;
    }

    public String[] getSeriesLabels() {
	return names;
    }

    public double[] getYSeries(int i) {
        if(y == null || i > y.length) return null;
	
	return y[i];
    }

    public double [] getXSeries(int i) {
	if (x == null) {
	    return new double[0];		
	} // end of else

	return x;
     }

    private void readObject(java.io.ObjectInputStream in)
 	throws java.io.IOException, ClassNotFoundException {
 	System.out.print("Loading JChartAdapter ");
 	in.defaultReadObject();
	
	//	reserialized = true;
 	if (refresher != null) 
	    startRefresher();	    

     }

    public static void main (String[] args) {
	fr.esrf.tangoatk.core.AttributeList attributelist =
	    new fr.esrf.tangoatk.core.AttributeList();
	JChartAdapter adapter =
	    new JChartAdapter();
    } // end of main ()
    
    private void debug(String s) {
	if (!reserialized ) return;
	System.out.println(s);
    }
	
}


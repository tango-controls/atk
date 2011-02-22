package fr.esrf.tangoatk.widget.attribute;


import com.klg.jclass.chart.*;
import com.klg.jclass.chart.data.*;
import fr.esrf.tangoatk.core.*;


public class NumberImageJChartAdapter extends JCDefaultDataSource
    implements IImageListener {

    protected INumberImage model;
    protected JCChart chart;
    protected double x[][];
    protected double y[][] = new double[0][0];
    protected boolean useXAxis = true;
    protected boolean addMode = false;
    int [] yindex;
    int xindex = 0;
    String [] names = new String[1];

    public void setAddMode(boolean b) {
	addMode = b;
    }

    public boolean getAddMode() {
	return addMode;
    }
    
    public void setUseXAxis(boolean b) {
	useXAxis = b;
    }

    public boolean getUseXAxis() {
	return useXAxis;
    }

    public NumberImageJChartAdapter() {

    }

    public void imageChange(NumberImageEvent numberimageevent) {
	double ad[][] = numberimageevent.getValue();

	if (ad == null || ad[0] == null)  return;



	if (!addMode) {
	    x[0] = ad[0];
	    System.arraycopy(ad, 1, y, 0, ad.length - 1);
	} else {

	    if (x[0].length < xindex + ad[0].length) {
		double [] tmp = new double[x[0].length];
// 		System.out.println(x[0].length + " " + tmp.length + " " +
// 				   ad[0].length + " " + (tmp.length -
// 						    ad[0].length));
		xindex = tmp.length - ad[0].length;
		System.arraycopy(x[0], ad[0].length, tmp, 0, xindex);

		//		System.out.println("Got here1 ");
		//		System.out.println("");
		x[0] = tmp;
	    }
	    //	    System.out.println("Got here3 " + xindex);
			       
	    System.arraycopy(ad[0], 0, x[0], xindex,
			     ad[0].length);
	    //	    System.out.println("Got here " + xindex);
	    xindex += ad[0].length;
	    
	    for (int i = 0; i < y.length; i++) {
		if (y[i].length < yindex[i] + ad[i + 1].length) {
		    double tmp[] = new double[y[i].length];

		    yindex[i] = tmp.length - ad[i + 1].length;
		    System.arraycopy(y[i], ad[i + 1].length, tmp, 0,
				     yindex[i]);
		    y[i] = tmp;

		}
// 		System.out.println("Got here2 " + yindex[i] + " " + i);
// 		System.out.println(ad[i + 1]);
// 		System.out.println(y[i].length + " " + ad[i + 1].length);
// 		System.out.println(ad[i + 1].length  + ", " + 0 + ", " +
// 				  y[i].length + ", " + yindex[i] + ", " +
// 				  ad[i + 1].length);
		System.arraycopy(ad[i + 1], 0, y[i],
				 yindex[i], ad[i + 1].length);
		
		yindex[i] += ad[i + 1].length;
	    } // end of for ()
	    
	    
	} // end of else
	
	


	updateChart(ChartDataEvent.RELOAD, 0, 0);

    }

    public int getNumSeries() {
	if (y == null)  return 0;

	return y.length;
    }

     public double [] getXSeries(int i) {
	 if (!useXAxis) return super.getXSeries(i);

	if (x == null || i >= x.length) {
	    return new double[0];		
	} // end of else

	return x[i];
     }

	
    public double[] getYSeries(int i) {
	if(y == null || i > y.length) return null;
	return y[i];
    }

    public void stateChange(AttributeStateEvent attributestateevent) {

    }

    public void errorChange(ErrorEvent errorevent) {
	System.out.println(errorevent.getError());
    }

    public void setModel(INumberImage inumberimage) {
	if (model != null)
	    model.removeImageListener(this);
	model = inumberimage;
	model.addImageListener(this);

	y = new double[model.getMaxYDimension() - 1][model.getMaxXDimension()];
	//		y = new double[model.getMaxYDimension() - 1][50];
	yindex = new int [model.getMaxYDimension() - 1];
	
	//	y = new double[model.getMaxYDimension() - 1][0];
	x = new double[1][model.getMaxXDimension()];
	//	x = new double[1][50];

	updateChart(ChartDataEvent.RELOAD_ALL_SERIES_LABELS, 0, 0);
	setData(null, y);
	chart.getDataView(0).setDataSource(this);

    }

    public INumberImage getModel() {
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

    public String[] getSeriesLabels() {
	return names;
    }

    
    public static void main(String args[]) throws Exception {
	fr.esrf.tangoatk.core.AttributeList attributelist =
	    new fr.esrf.tangoatk.core.AttributeList();
	NumberImageJChartAdapter numberimagejchartadapter =
	    new NumberImageJChartAdapter();
	numberimagejchartadapter.setUseXAxis(false);
	numberimagejchartadapter.setAddMode(true);
	com.klg.jclass.chart.beans.SimpleChart simplechart = new com.klg.jclass.chart.beans.SimpleChart();
	//	simplechart.setYAxisLogarithmic(true);
	//simplechart.setChartType(com.klg.jclass.chart.JCChart.BAR);
	//simplechart.setXAxisAnnotationMethod(2);
	numberimagejchartadapter.setChart(simplechart);
	INumberImage inumberimage =
	    (INumberImage)attributelist.add("fe/xfreff/1/Data");
	    //	(INumberImage)attributelist.add("tango://gizmo:20000/sys/machstat/tango/current_history");
	//	

	numberimagejchartadapter.setModel(inumberimage);


	javax.swing.JFrame jframe = new javax.swing.JFrame();
	jframe.setContentPane(simplechart);
	jframe.pack();
	jframe.show();
	attributelist.setRefreshInterval(10000);
	attributelist.startRefresher();
    }

}


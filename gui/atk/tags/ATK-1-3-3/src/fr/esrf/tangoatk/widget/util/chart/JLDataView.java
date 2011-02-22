//
// JLDataView.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import java.util.*;
import java.awt.*;
import javax.swing.*;
/**
 * A class to handle data view. It handles data and all graphics stuff related to a serie of data.
 * @author JL Pons
 */
public class JLDataView {

	//Static declaration

    /** No marker displayed */
	public static final int MARKER_NONE       = 0;
    /** Display a dot for each point of the view */
	public static final int MARKER_DOT        = 1;
    /** Display a box for each point of the view */
	public static final int MARKER_BOX        = 2;
    /** Display a triangle for each point of the view */
	public static final int MARKER_TRIANGLE   = 3;
    /** Display a diamond for each point of the view */
	public static final int MARKER_DIAMOND    = 4;
    /** Display a start for each point of the view */
	public static final int MARKER_STAR       = 5;
    /** Display a vertical line for each point of the view */
	public static final int MARKER_VERT_LINE  = 6;
    /** Display an horizontal line for each point of the view */
	public static final int MARKER_HORIZ_LINE = 7;
    /** Display a cross for each point of the view */
	public static final int MARKER_CROSS      = 8;
    /** Display a circle for each point of the view */
	public static final int MARKER_CIRCLE     = 9;
    /** Display a square for each point of the view */
	public static final int MARKER_SQUARE     = 10;

    /** Solid line style */
	public static final int STYLE_SOLID       = 0;
    /** Dot line style */
	public static final int STYLE_DOT         = 1;
    /** Dash line style */
	public static final int STYLE_DASH        = 2;
    /** Long Dash line style */
	public static final int STYLE_LONG_DASH   = 3;
    /** Dash + Dot line style */
	public static final int STYLE_DASH_DOT    = 4;

	//Local declaration
	private JLAxis      parentAxis;
	private Color       lineColor;
	private Color       markerColor;
	private int         lineStyle;
	private int         lineWidth;
	private int         markerType;
	private int         markerSize;	
	private double      A0;
	private double      A1;
	private double      A2;
	private DataList    theData;
	private int         dataLength;
	private DataList    theDataEnd;
	private double      max;
	private double      min;
	private String      name;
	private String      unit;
    private boolean     isFilled;

    /**
     * DataView constructor.
     */
	public JLDataView() {
		theData = null;
		theDataEnd = null;
		dataLength=0;
		name = "";
		unit = "";
	    lineColor = Color.red;
	    markerColor = Color.red;
		min =  Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
		markerType = MARKER_NONE;
		lineStyle = STYLE_SOLID;
		lineWidth = 1;
        markerSize = 6;	
	    A0 = 0;
	    A1 = 1;
	    A2 = 0;
		parentAxis=null;
        isFilled=false;
	}

    /**
     * Sets the color of the curve.
     * @param c Curve color
     * @see JLDataView#getColor
     */
	public void setColor(Color c) {
	  lineColor=c;
	}

    /**
     * Gets the curve color
     * @return Curve color
     * @see JLDataView#setColor
     */
	public Color getColor() {
	  return lineColor;
	}

    /**
     * Fill this data view
     * @param b True when filled, false otherwise
     * @see JLDataView#isFill
     */
	public void setFill(boolean b) {
	  isFilled=b;
	}

    /**
     * Determines wether the view is filled
     * @return True when filled, false otherwise
     * @see JLDataView#setFill
     */
	public boolean isFill() {
	  return isFilled;
	}

    /**
     * Sets the marker color
     * @param c Marker color
     * @see JLDataView#getMarkerColor
     */
	public void setMarkerColor(Color c) {
	  markerColor=c;
	}

    /**
     * Gets the marker color
     * @return Marker color
     * @see JLDataView#setMarkerColor
     */
	public Color getMarkerColor() {
	  return markerColor;
	}

    /**
     * Set the plot line style.
     * @param c Line style
     * @see JLDataView#STYLE_SOLID
     * @see JLDataView#STYLE_DOT
     * @see JLDataView#STYLE_DASH
     * @see JLDataView#STYLE_LONG_DASH
     * @see JLDataView#STYLE_DASH_DOT
     * @see JLDataView#getStyle
     */
	public void setStyle(int c) {
	  lineStyle=c;
	}
	
    /**
     * Gets the marker size
     * @return Marker size (pixel)
     * @see JLDataView#setMarkerSize
     */
	public int getMarkerSize() {
	  return markerSize;
	}

    /**
     * Sets the marker size (pixel)
     * @param c
     * @see JLDataView#getMarkerSize
     */
	public void setMarkerSize(int c) {
	  markerSize=c;
	}
	
    /**
     * Gets the line style.
     * @return Line style
     * @see JLDataView#setStyle
     */
	public int getStyle() {
	  return lineStyle;
	}

    /**
     * Gets the line width.
     * @return Line width
     * @see JLDataView#setLineWidth
     */
	public int getLineWidth() {
	  return lineWidth;
	}
	
    /**
     * Sets the plot line width
     * @param c Line width
     * @see JLDataView#getLineWidth
     */
	public void setLineWidth(int c) {
	  lineWidth=c;
	}
	
    /**
     * Sets the view name.
     * @param s Name of this view
     * @see JLDataView#getName
     */
	public void setName(String s) {
	  name = s;
	}

    /**
     * Gets the view name.
     * @return Dataview name
     * @see JLDataView#setName
     */
	public String getName() {
	  return name;
	}
	
    /**
     * Set the dataView unit. (Used only for display)
     * @param s Dataview unit.
     * @see JLDataView#getUnit
     */
	public void setUnit(String s) {
	  unit = s;
	}

    /**
     * Gets the dataView unit
     * @return Dataview unit
     * @see JLDataView#setUnit
     */
	public String getUnit() {
	  return unit;
	}

    /**
     * Gets the extended name. (including transform description when used)
     * @return Extended name of this view.
     */
	public String getExtendedName() {
	
	  String r;
	  String t="";
	  
	  if( hasTransform() ) {
	    r = name + " [";
		
		if( A0!=0.0 ) { 
		  r += Double.toString(A0);
		  t = " + ";
		}
		if( A1!=0.0 ) {
		  r = r + t + Double.toString(A1) + "*y";
		  t = " + ";		  
		}
		if( A2!=0.0 ) {
		  r = r + t + Double.toString(A2) + "*y^2";
		}
		r += "]";
		
		return r;
				  
	  } else
	    return name;
	}

    /**
     * Gets the marker type.
     * @return Marker type
     * @see JLDataView#setMarker
     */
	public int getMarker() {
	  return markerType;
	}

    /**
     * Sets the marker type.
     * @param m Marker type
     * @see JLDataView#MARKER_NONE
	 * @see JLDataView#MARKER_DOT
	 * @see JLDataView#MARKER_BOX
	 * @see JLDataView#MARKER_TRIANGLE
	 * @see JLDataView#MARKER_DIAMOND
	 * @see JLDataView#MARKER_STAR
	 * @see JLDataView#MARKER_VERT_LINE
	 * @see JLDataView#MARKER_HORIZ_LINE
	 * @see JLDataView#MARKER_CROSS
	 * @see JLDataView#MARKER_CIRCLE
	 * @see JLDataView#MARKER_SQUARE
     */
	public void setMarker(int m) {
	  markerType = m;
	}

    /**
     * Gets the A0 transformation coeficient.
     * @return A0 value
     * @see JLDataView#setA0
     */
     public double getA0() {
	  return A0;
	}
	
    /**
     * Gets the A1 transformation coeficient.
     * @return A1 value
     * @see JLDataView#setA1
     */
	public double getA1() {
	  return A1;
	}
	
    /**
     * Gets the A2 transformation coeficient.
     * @return A2 value
     * @see JLDataView#setA2
     */
	public double getA2() {
	  return A2;
	}

    /**
     * Set A0 transformation coeficient. The transformation computes
     * new value = A0 + A1*v + A2*v*v.
     * Transformation is disabled when A0=A2=0 and A1=1.
     * @param d A0 value
     */
	public void setA0(double d) {
	  A0=d;
	}
	
    /**
     * Set A1 transformation coeficient. The transformation computes
     * new value = A0 + A1*v + A2*v*v.
     * Transformation is disabled when A0=A2=0 and A1=1.
     * @param d A1 value
     */
	public void setA1(double d) {
	  A1=d;
	}
	
    /**
     * Set A2 transformation coeficient. The transformation computes
     * new value = A0 + A1*v + A2*v*v.
     * Transformation is disabled when A0=A2=0 and A1=1.
     * @param d A2 value
     */
	public void setA2(double d) {
	  A2=d;
	}
    	
    /**
     * Determines wether this views has a transformation.
     * @return false when A0=A2=0 and A1=1, true otherwise
     */
	public boolean hasTransform() {
	  return !(A0==0 && A1==1 && A2==0);	
	}	

    /** Expert usage.
     * Sets the parent axis.
     * ( Used by JLAxis.addDataView() )
     * @param a Parent axis
     */
	public void setAxis(JLAxis a) {
	  parentAxis=a;
	}

    /** Expert usage.
     * Sets the parent axis.
     * @return Parent axis
     */
	public JLAxis getAxis() {
	  return parentAxis;
	}

    /** Expert usage.
     * Gets the minimum (Y axis)
     * @return Minimum value
     */
	public double getMinimum() {
	  return min;					  
	}

    /** Expert usage.
     * Gets the maximum (Y axis)
     * @return Maximun value
     */
	public double getMaximum() {
	  return max;
	}

    /** Expert usage.
     * Gets the minimun on X axis (TIME_ANNO)
     * @return Minimum time
     */
	public double getMinTime() {
	  if( theData!= null )
	    return theData.x;
	  else
	    return Double.MAX_VALUE;
	}

    /** Expert usage.
     * Get the positive minimun on X axis (with TIME_ANNO)
     * @return Minimum value strictly positive
     */
	public double getPositiveMinTime() {
	  DataList e=theData;
	  boolean found=false;
	  while( e!=null && !found) {
	    found=(e.x>0);
		if(!found) e=e.next;	  
	  }

	  if( e!= null )
	    return e.x;
	  else
	    return Double.MAX_VALUE;
	}

    /** Expert usage.
     * Get the maxinmun on X axis (with TIME_ANNO)
     * @return Maximum value
     */
	public double getMaxTime() {
	  if( theDataEnd!=null )
	    return theDataEnd.x;
	  else
	    return -Double.MAX_VALUE;
	}

    /**
     * Gets the number of data in this view
     * @return Data length
     */
	public int getDataLength() {
	  return dataLength;
	}

    /** Expert usage.
     * Return a handle on DATA
	 * !! Dot not modifie data by this way !!
     * @return A handle to the last value.
     */
	public DataList getData() {
	  return theData;
	}

    /**
     * Add datum the the dataview.If you call this routine directly the graph will be updated only after a repaint.
     * and your data won't be garbaged. You should use JLChart.addData
     * @param x x coordinates (real space)
     * @param y y coordinates (real space)
     * @see JLChart#addData
     */
	public void add(double x,double y) {

	  if( theData==null ) {
	    theData = new DataList(x,y);
	    theDataEnd = theData;
	  } else {
	    theDataEnd.next = new DataList(x,y);
	    theDataEnd = theDataEnd.next;
	  }

	  if( y<min ) min = y;
	  if( y>max ) max = y;

	  dataLength++;

	}

    /**
     * Garbage old data according to time.
     * @param garbageLimit Limit time (in millisec)
     * @return Number of deleted point.
     */
	public int garbagePointTime(double garbageLimit) {

	  boolean need_to_recompute_max=false;
	  boolean need_to_recompute_min=false;
	  boolean found =false;
	  int     nbr = 0;

	  // Garbage old data

	  if( theData!=null ) {
	    double xmax = theDataEnd.x;

        while( theData!=null && !found )
		{
		  found = ( theData.x > (xmax-garbageLimit) );
		  if( !found ) {
		    // Remve first element
			need_to_recompute_max = need_to_recompute_max || (theData.y == max);
			need_to_recompute_min = need_to_recompute_min || (theData.y == min);
		    theData=theData.next;
		    dataLength--;
			nbr++;
		  }
		}
	  }

	  if( need_to_recompute_max ) computeMax();
	  if( need_to_recompute_min ) computeMin();

	  return nbr;
	}

    /**
     * Garbage old data according to data length.
     * It will remove the (dataLength-garbageLimit) fist point.
     * @param garbageLimit Index limit
     */
	public void garbagePointLimit(int garbageLimit) {

	  boolean need_to_recompute_max=false;
	  boolean need_to_recompute_min=false;
	  boolean found =false;

	  // Garbage old data
	  int nb = dataLength-garbageLimit;
	  for(int i=0;i<nb;i++) {
		need_to_recompute_max = need_to_recompute_max || (theData.y == max);
		need_to_recompute_min = need_to_recompute_min || (theData.y == min);
	    theData=theData.next;
	    dataLength--;
	  }

	  if( need_to_recompute_max ) computeMax();
	  if( need_to_recompute_min ) computeMin();

	}

	//Compute min
	private void computeMin() {
      min = Double.MAX_VALUE;
	  DataList e = theData;
	  while(e!=null) {
	    if( e.y < min ) min = e.y;
		e=e.next;
	  }
	  //System.out.println("JLDataView.computeMin() done.");
	}

	//Compute max
	private void computeMax() {
      max = -Double.MAX_VALUE;
	  DataList e = theData;
	  while(e!=null) {
	    if( e.y > max ) max = e.y;
		e=e.next;
	  }
	  //System.out.println("JLDataView.computeMax() done.");
	}

    /** Expert usage.
     * Compute transformed min and max.
     * @return Transformed min and max
     */
	public double[] computeTransformedMinMax() {

	  double[] ret = new double[2];

	  double mi =  Double.MAX_VALUE;
	  double ma = -Double.MAX_VALUE;

	  DataList e = theData;

	  while(e!=null) {
	    double v = A0 + A1*e.y + A2*e.y*e.y;
	    if( v < mi ) mi = v;
	    if( v > ma ) ma = v;
		e=e.next;
	  }

	  if(mi== Double.MAX_VALUE) mi = 0;
	  if(ma==-Double.MAX_VALUE) ma = 99;

	  ret[0] = mi;
	  ret[1] = ma;

	  return ret;
	}

	/**
     * Compute minimun of positive value
     * @return Double.MAX_VALUE when no positive value are found
     */
    public double computePositiveMin() {

	  double mi = Double.MAX_VALUE;
	  DataList e = theData;
	  while(e!=null) {
	    if( e.y>0 && e.y<mi ) mi = e.y;
		e=e.next;
	  }
	  return mi;

	}
    /**
     *  Compute transformed value of x.
     * @param x Value to transform
     * @return transformed value (through A0,A1,A2 transformation)
     */
	public double getTransformedValue(double x) {
      return A0 + A1*x + A2*x*x;
	}

   /**
    * Get last value.
    * @return Last value
    */
	public DataList getLastValue() {
          return theDataEnd;
	}

	/**
     * Reset the view.
     */
	public void reset() {
	  theData = null;
	  theDataEnd = null;
	  dataLength=0;
	  computeMin();
	  computeMax();
	}

}
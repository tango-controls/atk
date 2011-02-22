//
// JLDataView.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import java.util.*;
import java.awt.*;
import javax.swing.*;

public class JLDataView {

	//Static declaration
	
	public static final int MARKER_NONE       = 0; 
	public static final int MARKER_DOT        = 1; 
	public static final int MARKER_BOX        = 2; 
	public static final int MARKER_TRIANGLE   = 3; 
	public static final int MARKER_DIAMOND    = 4;
	public static final int MARKER_STAR       = 5; 
	public static final int MARKER_VERT_LINE  = 6; 
	public static final int MARKER_HORIZ_LINE = 7; 
	public static final int MARKER_CROSS      = 8; 
	public static final int MARKER_CIRCLE     = 9;
	public static final int MARKER_SQUARE     = 10;

	public static final int STYLE_SOLID       = 0; 
	public static final int STYLE_DOT         = 1;
	public static final int STYLE_DASH        = 2; 
	public static final int STYLE_LONG_DASH   = 3; 
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

	// Default DataView constructor.
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
	}

	//Set the plot color
	public void setColor(Color c) {
	  lineColor=c;
	}

	//Get the plot color
	public Color getColor() {
	  return lineColor;
	}
	
	//Set the marker color
	public void setMarkerColor(Color c) {
	  markerColor=c;
	}

	//Get the marker color
	public Color getMarkerColor() {
	  return markerColor;
	}

	//Set the plot style
	public void setStyle(int c) {
	  lineStyle=c;
	}
	
	//Get the marker size
	public int getMarkerSize() {
	  return markerSize;
	}
	//Set the plot style
	public void setMarkerSize(int c) {
	  markerSize=c;
	}
	
	//Get the plot style
	public int getStyle() {
	  return lineStyle;
	}

	//Get the plot line width
	public int getLineWidth() {
	  return lineWidth;
	}
	
	//Set the plot line width
	public void setLineWidth(int c) {
	  lineWidth=c;
	}
	
	// Set the dataView name
	public void setName(String s) {
	  name = s;
	}

	// Get the dataView name
	public String getName() {
	  return name;
	}
	
	// Set the dataView unit
	public void setUnit(String s) {
	  unit = s;
	}

	// Get the dataView unit
	public String getUnit() {
	  return unit;
	}

	// Get the extended name
	// Return the name (+ transform description when used)
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


	// Get the marker type
	public int getMarker() {
	  return markerType;
	}

	// Set the marker type
	public void setMarker(int m) {
	  markerType = m;
	}

	// Get A0 transformation coef
	public double getA0() {
	  return A0;
	}
	
	// Get A1 transformation coef
	public double getA1() {
	  return A1;
	}
	
	// Get A2 transformation coef
	public double getA2() {
	  return A2;
	}

	// Set A0 transformation coef
	public void setA0(double d) {
	  A0=d;
	}
	
	// Set A1 transformation coef
	public void setA1(double d) {
	  A1=d;
	}
	
	// Set A2 transformation coef
	public void setA2(double d) {
	  A2=d;
	}
    	
	//Return true if this dataView has a transform
	public boolean hasTransform() {
	  return !(A0==0 && A1==1 && A2==0);	
	}	

    //Set the parent axis ( Used by JLAxis.addDataView() )
	//Expert usage
	public void setAxis(JLAxis a) {
	  parentAxis=a;
	}

    //Set the parent axis
	//Expert usage
	public JLAxis getAxis() {
	  return parentAxis;
	}

	//Get the minimun (Y axis)
	//Expert usage
	public double getMinimum() {
	  return min;					  
	}

	//Get the maxinmun (Y axis)
	//Expert usage
	public double getMaximum() {	
	  return max;
	}

	//Get the minimun on X axis (with TIME_ANNO)
	//Expert usage
	public double getMinTime() {
	  if( theData!= null )
	    return theData.x;
	  else
	    return Double.MAX_VALUE;
	}

	//Get the positive minimun on X axis (with TIME_ANNO)
	//Expert usage
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

	//Get the maxinmun on X axis (with TIME_ANNO)
	//Expert usage
	public double getMaxTime() {
	  if( theDataEnd!=null )
	    return theDataEnd.x;
	  else
	    return -Double.MAX_VALUE;
	}

	//Get the length of this dataView
	public int getDataLength() {
	  return dataLength;
	}

	//Return a handle on DATA
	// !! Dot not modifie data by this way !!
	//Expert usage
	public DataList getData() {
	  return theData;
	}

	//Add datum the the dataview
	//If you call this routine directly the graph will be updated after a repaint.
	//and your data won't be garbaged.
	//You should use JLChart.addData()
	
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

	// Garbage old data
	// Return the number of deleted point
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
	
	// Garbage old data
	// Trunc the dataView to the last garbageLimit item.
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

	//Compute transformed min and max
	//Expert usage
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
	
	//Compute minimun of positive value
	//Return Double.MAX_VALUE when no positive value are found
	public double computePositiveMin() {
      
	  double mi = Double.MAX_VALUE;
	  DataList e = theData;
	  while(e!=null) {
	    if( e.y>0 && e.y<mi ) mi = e.y;
		e=e.next;	    
	  }
	  return mi;
	  
	}

	//Compute transformed value of x
	public double getTransformedValue(double x) {
      return A0 + A1*x + A2*x*x;	  
	}
	
	//Compute transformed value of x
	public DataList getLastValue() {
      return theDataEnd;	  
	}
	
	// Free all data
	public void reset() {
	  theData = null;
	  theDataEnd = null;
	  dataLength=0;
	}
	
}
//
// JLAxis.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import java.awt.event.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import com.braju.format.Format;

// Inner class to handle label info

class LabelInfo {
	
	String    value;
	boolean   isVisible;
	Dimension size;
	int       pos;
	int       subtick_step;  // -1 for logarithmic step, 0 None 

	LabelInfo(String lab,boolean v,int w,int h,int d,int sub) {
	  value = lab;
	  size = new Dimension(w,h);
	  pos = d;
	  subtick_step = sub;
	  isVisible=v;
	}

}

public class JLAxis {

    // constant
	public static final int HORIZONTAL    =1;
	public static final int VERTICAL_RIGHT=2;
	public static final int VERTICAL_LEFT =3;

	public static final int TIME_ANNO=1;
	public static final int VALUE_ANNO=2;

	public static final int LINEAR_SCALE=0;
	public static final int LOG_SCALE=1;

	public static final int AUTO_FORMAT      =0;
	public static final int SCIENTIFIC_FORMAT=1;
	public static final int TIME_FORMAT      =2;
	public static final int DECINT_FORMAT    =3;
	public static final int HEXINT_FORMAT    =4;
	public static final int BININT_FORMAT    =5;

	public static final double YEAR= 31536000000.0;
	public static final double MONTH= 2592000000.0;
	public static final double DAY  =   86400000.0;
	public static final double HOUR =    3600000.0;
	public static final double MINU =      60000.0;
	public static final double SECO =       1000.0;

	//Local declaration
	private double  min=0.0;
	private double  max=100.0;
	private double  minimum=0.0;
	private double  maximum=100.0;
	private boolean autoScale=false;
	private int     scale=LINEAR_SCALE;
	private Color   labelColor;
	private Font    labelFont;
	private int     labelFormat;
	private Vector  labels;
	private int     orientation;
	private int     tick=10;  // label precision
	private boolean subtickVisible;
	private Dimension csize=null;
	private String name;
	private int annotation=VALUE_ANNO;
	private Vector  dataViews;
	private JComponent parent;
	private double  ln10;
	private boolean gridVisible;
	private boolean subGridVisible;
	private int gridStyle;
	private Rectangle boundRect;
	private boolean lastAutoScate;
	private boolean isZoomed;
	private double  percentScrollback;
	private String  axeName;

	//Global
    static final java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
    static final java.text.SimpleDateFormat  genFormat = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    static final java.text.SimpleDateFormat  yearFormat = new java.text.SimpleDateFormat("yyyy");
    static final java.text.SimpleDateFormat  monthFormat = new java.text.SimpleDateFormat("MMMMM yy");
    static final java.text.SimpleDateFormat  weekFormat = new java.text.SimpleDateFormat("dd/MM/yy");
    static final java.text.SimpleDateFormat  dayFormat = new java.text.SimpleDateFormat("EEE dd");
    static final java.text.SimpleDateFormat  hour12Format = new java.text.SimpleDateFormat("EEE HH:mm");
    static final java.text.SimpleDateFormat  hourFormat = new java.text.SimpleDateFormat("HH:mm");
    static final java.text.SimpleDateFormat  secFormat = new java.text.SimpleDateFormat("HH:mm:ss");

	static final double[] timePrecs = { 
		1*SECO,5*SECO,10*SECO,30*SECO,
		1*MINU,5*MINU,10*MINU,30*MINU,
		1*HOUR,3*HOUR,6*HOUR,12*HOUR,
		1*DAY,7*DAY,1*MONTH,1*YEAR,5*YEAR,
		10*YEAR
	};

    static final java.text.SimpleDateFormat timeFormats[] = { 
		secFormat,secFormat,secFormat,secFormat,
		secFormat,secFormat,secFormat,hourFormat,
		hourFormat,hourFormat,hourFormat,hour12Format,
		dayFormat,weekFormat,monthFormat,yearFormat,
		yearFormat,yearFormat };

    static final String labelFomats[] = { "%g" , "" , "%02d:%02d:%02d" , "%d" , "%X" , "%b" };
		
	static final int triangleX[] = {  0 ,  4 , -4 };
	static final int triangleY[] = { -3 ,  3 ,  3 };
	static final Polygon triangleShape = new Polygon( triangleX , triangleY , 3 );

	static final int diamondX[] = {  0 ,  4 ,  0,  -4};
	static final int diamondY[] = {  4 ,  0 , -4,   0};
	static final Polygon diamondShape = new Polygon( diamondX , diamondY , 4 );

    static float dashDotPattern[]  = {5.0f,3.0f,2.0f,3.0f};
    static float dotPattern[]      = {2.0f,4.0f};
    static float dashPattern[]     = {5.0f};
    static float longDashPattern[] = {10.0f};

	static double linStep[] = { 0.0, 0.1 , 0.2 , 0.3 , 0.4 , 0.5 , 0.6 , 0.7 , 0.8 , 0.9 };
	static double logStep[] = { 0.301 , 0.477 , 0.602 , 0.699 , 0.778 , 0.845 , 0.903 , 0.954 };
	
	// Default graph constructor.
	// orientation determines the axis placement:
	//   JLAxis.HORIZONTAL , JLAxis.VERTICAL_RIGHT , JLAxis.VERTICAL_LEFT
	public JLAxis(JComponent parent,int orientation) {
	  labels    = new Vector();
	  labelFont = new Font("Dialog",Font.BOLD,11);
	  labelColor = Color.black;
	  name=null;
      this.orientation = orientation;
	  dataViews = new Vector();
	  this.parent=parent;
	  ln10 = Math.log(10);
	  gridVisible = false;
	  subGridVisible = false;
	  gridStyle   = JLDataView.STYLE_DOT;
	  labelFormat = AUTO_FORMAT;
	  subtickVisible = true;
	  boundRect = new Rectangle(0,0,0,0);
	  isZoomed = false;
	  percentScrollback = 0.025;
	  axeName="";
	}

	//Set the percent scrollback
	//In TIME_ANNO, add when using JLChart.addData() this
	//allow to avoid repainting the graph every time.
	public void setPercentScrollback(double d) {
	  percentScrollback=d/100;
	}

	//Get the percent scrollback
	public double getPercentScrollback() {
	  return percentScrollback;
	}

	//Set the axis color
	public void setAxisColor(Color c) {
      labelColor = c;
	}

	//Get the axis color
	public Color getAxisColor() {
      return labelColor;
	}
	
	//Set the axis label format
	public void setLabelFormat(int l) {
      labelFormat = l;
	}

	//Set the axis label format
	public int getLabelFormat() {
      return labelFormat;
	}

	//Show/Hide the grid
	public void setGridVisible(boolean b) {
      gridVisible = b;
	}

	//Retrun true is grid is visible
	public boolean isGridVisible() {
      return gridVisible;
	}
	
	//Show/Hide the sub grid
	public void setSubGridVisible(boolean b) {
      subGridVisible = b;
	}

	//Retrun true is subgrid is visible
	public boolean isSubGridVisible() {
      return subGridVisible;
	}

	//Set the grid style
	public void setGridStyle(int s) {
      gridStyle=s;
	}

	//Get the grid style
	public int getGridStyle() {
      return gridStyle;
	}

	//Set the label font
	public void setFont(Font f) {
      labelFont = f;
	}

	//Get the label font
	public Font getFont() {
      return labelFont;
	}

	//Set the annotation method
	//JLAxis.TIME_ANNO , JLAxis.VALUE_ANNO
	public void setAnnotation(int a) {
	  annotation = a;
	}

	//Return true is axis is zoomed
	public boolean isZoomed() {
	  return isZoomed;
	}

	//Return true if xAxis is in XY mode
	public boolean isXY() {
	  return (dataViews.size()>0) ;
	}

	//Set minimum axis value
	//! min must be positive in logarithmic scale !
	public void setMinimum(double d) {
	  
	  minimum = d;

	  if( !autoScale ) {
	    if( scale==LOG_SCALE ) {
	      if(d<=0) d=1;
		  min=Math.log(d)/ln10;		
		} else
	      min=d;
	  }

	}

	//Get minimum axis value
	public double getMinimum() {
	  return minimum;
	}

	//Set maximum axis value
	//! max must be positive in logarithmic scale !
	public void setMaximum(double d) {
	  
	  maximum = d;

	  if( !autoScale ) {
	    if( scale==LOG_SCALE ) {
	      if(max<=0) max=min*10.0;
	      max=Math.log(d)/ln10;
		} else
	      max=d;
	  }

	}

	//Get maximum axis value
	public double getMaximum() {
	  return maximum;
	}
	
	//Get minimum axis value (including scale transformation)
	//Expert usage
	public double getMin() {
	  return min;
	}

	//Get maximum axis value (including scale transformation)
	//Expert usage
	public double getMax() {
	  return max;
	}
	
	//Set autoScaleMode
	public boolean isAutoScale() {
	    return autoScale;
	}

	//Get the autoScaleMode
	public void setAutoScale(boolean b) {
	  autoScale = b;	  
	}

	//Get the scale mode
	public int getScale() {
	    return scale;
	}

	//Set scale mode LINEAR_SCALE,LOG_SCALE
	public void setScale(int s) {
	
	  scale = s;

  	  if( scale==LOG_SCALE ) {
	    // Check min and max
		if(minimum<=0 || maximum<=0) {
		  minimum=1;
		  maximum=10;
		}
	  }

	  if( scale==LOG_SCALE ) {
	      min=Math.log(minimum)/ln10;
	      max=Math.log(maximum)/ln10;
	  } else {
	      min=minimum;
	      max=maximum;
	  }

	}

	// Zoom axis
	public void zoom(int x1,int x2) {

		if(!isZoomed) lastAutoScate = autoScale;

		if( orientation==HORIZONTAL ) {

		  // Clip
		  if( x1<boundRect.x ) x1=boundRect.x;
		  if( x2>(boundRect.x+boundRect.width) ) x2=boundRect.x+boundRect.width;

		  // Too small zoom
		  if( (x2-x1) < 10 ) return;

		  // Compute new min and max
		  double xr1 = (double)(x1-boundRect.x)/(double)(boundRect.width);
		  double xr2 = (double)(x2-boundRect.x)/(double)(boundRect.width);
		  double nmin = min + (max-min)*xr1;
		  double nmax = min + (max-min)*xr2;
		  min = nmin;
		  max = nmax;

		} else {		

		  // Clip
		  if( x1<boundRect.y ) x1=boundRect.y;
		  if( x2>(boundRect.y+boundRect.height) ) x2=boundRect.y+boundRect.height;

		  // Too small zoom
		  if( (x2-x1) < 10 ) return;

		  // Compute new min and max
		  double yr1 = (double)(boundRect.y+boundRect.height-x2)/(double)(boundRect.height);
		  double yr2 = (double)(boundRect.y+boundRect.height-x1)/(double)(boundRect.height);
		  double nmin = min + (max-min)*yr1;
		  double nmax = min + (max-min)*yr2;
		  min = nmin;
		  max = nmax;

		}

		autoScale = false;
		isZoomed = true;

	}

	// Unzoom axis
	public void unzoom() {
	    autoScale = lastAutoScate;
		if( !lastAutoScate ) {
		  setMinimum( getMinimum() );
		  setMaximum( getMaximum() );
		}
		isZoomed=false;
	}

	//Get the labels interval ( tick is the desired number
	//of label on the axis ) if font overlap happens
	//you may get less labels
	public int getTick() {
	    return tick;
	}

	//Set the labels interval
	public void setTick(int s) {
	  tick = s;
	}

	// Get the axis name
	public String getName() {
	  return name;
	}

	// Set the axis name
	public void setName(String s) {
		
     int z=0;
	 if( s!=null ) z=s.length();

	 if(z>0) name = s;
	 else    name = null;

	}
	
	// Get the axis name
	public String getAxeName() {
	  return axeName;
	}
	
	// set the axis name
	public void setAxeName(String s) {
	  axeName=s;
	}

	// Add a DataView to a this axis.
	// The graph switches in XY monitoring mode when adding 
	// a dataView to X axis, Only one view is allowed on HORIZONTAL Axis
	// and you must ensure that all views in all axis have the same number 
	// of point, else some views can be truncated.
	public void addDataView(JLDataView v) {

	  if( dataViews.contains(v) )
		  return;

	  if( orientation != HORIZONTAL ) {
        dataViews.add(v);
		v.setAxis(this);
	  } else {
	    // Switch to XY mode
		// Only one view on X
		dataViews.clear();
		dataViews.add(v);
		v.setAxis(this);
		setAnnotation(VALUE_ANNO);
	  }
	}

	// Remove dataview from this axis
	public void removeDataView(JLDataView v) {
      dataViews.remove(v);
	  v.setAxis(null);
	  if( orientation==HORIZONTAL ) {
	    // Restore TIME_ANNO and Liner scale
		setAnnotation(TIME_ANNO);
		if( scale!=LINEAR_SCALE ) setScale(LINEAR_SCALE);
	  }
	}

	// Clear all dataview from this axis
	public void clearDataView() {
	  int sz = dataViews.size();
	  JLDataView v;
	  for(int i=0;i<sz;i++) {
	    v=(JLDataView)dataViews.get(i);
		v.setAxis(null);
      }
      dataViews.clear();
	}

	// Get DataViews
	// Do not modify (Use as read only)
	public Vector getViews() {
	  return dataViews;
	}
	
	// Return the bouding rectangle
	public Rectangle getBoundRect() {
	  return boundRect;
	}
	
	// Return a scientific representation of the double
	public String toScientific(double d) {
	
	  double  a=Math.abs(d);
	  int     e=0;
	  String  f="%.2fE%d";

	  if( a!=0 ) {
	    if( a<1 ) {
		  while(a<1) { a=a*10;e--; }
		} else {
		  while(a>=10) { a=a/10;e++; }
		}
	  }

	  if(a>=9.999999999) { a=a/10;e++; }

	  if( d<0 ) a=-a;

	  Object o[] = { new Double(a) , new Integer(e) };

	  return Format.sprintf( f , o );

	}

	// Return a representation of the double in time format
	public String formatTimeValue(double vt) {
  		java.util.Date date;	    
  	    calendar.setTimeInMillis((long)vt);
        date = calendar.getTime();
        return genFormat.format(date);		
	}
	
	// Return a representation of the double acording to the format	
	// prec is the desired prec (Pass 0 to not perform prec rounding)
	public String formatValue(double vt,double prec) {
	  
	  // Round value according to desired prec	
	  // TODO: rounding in LOG_SCALE
	  if( prec!=0 && scale==LINEAR_SCALE) {
	    long r;
		if( vt>=0 ) {
	      vt = vt/prec * 1e5;
		  r  = (long)( vt + 0.5 );
		  vt = (r*prec) / 1e5;
		} else {
	      vt = -vt/prec * 1e5;
		  r  = (long)( vt + 0.5 );
		  vt = -(r*prec) / 1e5;
		}
	  }
	    
	  switch( labelFormat ) {
		case SCIENTIFIC_FORMAT:
		  return toScientific(vt);
		  
		case DECINT_FORMAT:
		case HEXINT_FORMAT:
		case BININT_FORMAT:
	      Object[] o2 = { new Integer( (int)(Math.abs(vt)) ) };
		  if( vt<0.0 ) return "-" + Format.sprintf(labelFomats[labelFormat],o2);				  
		  else         return Format.sprintf(labelFomats[labelFormat],o2);				  

		case TIME_FORMAT:
		
		  int sec=(int)(Math.abs(vt));
	      Object[] o3 = {
		        new Integer( sec/3600 ) ,
		        new Integer( (sec%3600)/60 ) ,
				new Integer( sec%60 ) };
				
		  if( vt<0.0 ) return "-" + Format.sprintf(labelFomats[labelFormat],o3);				  
		  else         return Format.sprintf(labelFomats[labelFormat],o3);				  
		  
	   default:
	  
	       return Double.toString(vt);
		   
	  }
	  
	}

	// *****************************************************
	// AutoScaling stuff
	// Expert usage
	
	// log10(x) = ln(x)/ln(10);
	private double computeHighTen(double d) {
	  int p = (int)(Math.log(d)/ln10);
	  return Math.pow(10.0,p+1);
	}

	private double computeLowTen(double d) {
	  int p = (int)(Math.log(d)/ln10);
	  return Math.pow(10.0,p);
	}

	private void computeAutoScale() {

	  int i=0;
	  int sz = dataViews.size();
	  double mi=0,ma=0;

	  if( autoScale && sz>0 ) {
  	   
		JLDataView v;			    
	    min =  Double.MAX_VALUE;
	    max = -Double.MAX_VALUE;

		for(i=0;i<sz;i++) {
		
		  v= (JLDataView)dataViews.get(i);
		  
		  if( v.hasTransform() ) {
		    double[] mm = v.computeTransformedMinMax();	
		    mi=mm[0];
		    ma=mm[1];		  			
          } else {
		    mi=v.getMinimum();
		    ma=v.getMaximum();		  
		  }
		  
		  if (scale==LOG_SCALE) {
		    
			if( mi<=0 ) mi=v.computePositiveMin();
			if( mi!=Double.MAX_VALUE ) mi=Math.log(mi)/ln10;
			
			if( ma<=0)  ma=-Double.MAX_VALUE;
		    else        ma=Math.log(ma)/ln10;
		  } 
		  
		  if( ma>max ) max = ma;
		  if( mi<min ) min = mi;
		  
		}

		// Check max and min
		if( min==Double.MAX_VALUE && max==-Double.MAX_VALUE ) {

		    // Only invalid data !!		  
			if( scale==LOG_SCALE ) {
			  min = 0;
			  max = 1;
			} else {
			  min = 0;
			  max = 99.99;
			}
		  
		} 
		
		if( (max-min)<1e-100 ) { max+=0.999;min-=0.999; }
		  
		double prec = computeLowTen( max-min );

		//System.out.println("ComputeAutoScale: Prec= " + prec );

		if( min<0 )
		  min = ((int)(min/prec)-1) * prec;
		else
		  min = (int)(min/prec) * prec;


		if( max<0 ) 
		  max = (int)(max/prec) * prec;
		else
		  max = ((int)(max/prec)+1) * prec;
		  
		//System.out.println("ComputeAutoScale: " + min + "," + max );

	  } // end ( if autoScale )

	}

	// Compute X auto scale (HORIZONTAL axis only)
	// Expert usage
	public void computeXScale(Vector views) {

	  int i=0;
	  int sz = views.size();
	  double t;
	  double mi,ma;

	  if( orientation==HORIZONTAL && autoScale && sz>0 ) {

		if( !isXY() ) {

		  //******************************************************
		  // Classic monitoring	

	      JLDataView v;
		  min = Double.MAX_VALUE;
		  max = -Double.MAX_VALUE;

          // Horizontal autoScale

		  for(i=0;i<sz;i++) {

		    v= (JLDataView)views.get(i);
		  
		    ma = v.getMaxTime();
		    mi = v.getMinTime();	
		  
		    if( scale == LOG_SCALE ) {
			  if( mi<=0 ) mi=v.getPositiveMinTime();
			  if( mi!=Double.MAX_VALUE ) mi=Math.log(mi)/ln10;
			
			  if( ma<=0)  ma=-Double.MAX_VALUE;
		      else        ma=Math.log(ma)/ln10;
			} 
		  
		    if( ma>max ) max = ma;
		    if( mi<min ) min = mi;

		  }


		  if(	min==Double.MAX_VALUE && max==-Double.MAX_VALUE ) {

	        // Only empty views !
		  
		    if( scale==LOG_SCALE ) {
		    
			  min = 0;
		      max = 1;

			} else {
			
			  if( annotation==TIME_ANNO ) {
	            min = System.currentTimeMillis()-HOUR;
	            max = System.currentTimeMillis();
			  } else {
		        min = 0;
		        max = 99.99;
			  }

			}

		  }

		  if( annotation==TIME_ANNO ) {
		    // percent scrollBack
		    max += (max-min) * percentScrollback;
		  }

		  if( (max-min)<1e-100 ) { max+=0.999;min-=0.999; }

		} else {

		  //******************************************************
		  // XY monitoring
		  computeAutoScale();
		
		}

	  }


	}

	// *****************************************************
	// Measurements stuff
	
	// Return axis font height
	// Expert usage
	public int getFontHeight(Graphics g) {
 	  if( orientation==HORIZONTAL ) {
	    if( name!=null )
	      return 2*g.getFontMetrics(labelFont).getHeight();
		else
	      return   g.getFontMetrics(labelFont).getHeight();
	  } else {
	    if( name!=null )
	      return g.getFontMetrics(labelFont).getHeight();
		else
	      return 5;
	  }
	}

	// Return axis tichkness in pixel ( shorter side )
	// Expert usage
	public int getThickness() {

	  if( csize!=null )
	  switch( orientation ) {

	   case VERTICAL_RIGHT:
	   case VERTICAL_LEFT:
	     return csize.width;
	   case HORIZONTAL:
	     return csize.height;
		
	  }

	  return 0;
	}

	// Return axis lenght in pixel ( larger side )
	// Expert usage
	public int getLength() {

	  if( csize!=null )
	  switch( orientation ) {

	   case VERTICAL_RIGHT:
	   case VERTICAL_LEFT:
	     return csize.height;
	   case HORIZONTAL:
	     return csize.width;
		
	  }

	  return 0;
	}

	// Compute labels and measure dimension
	// Expert usage
	public void measureAxis(Graphics g,FontRenderContext frc,int desiredWidth,int desiredHeight) {
	  	  
		  int i;
		  int max_width =10; // Minimun width
		  int max_height=0;

	  

		  g.setFont(labelFont);
	      
		  computeAutoScale();

		  switch( orientation ) {

		   case VERTICAL_RIGHT:
		   case VERTICAL_LEFT:
		     computeLabels(frc,desiredHeight,true);
			 break;
		   case HORIZONTAL:
		     computeLabels(frc,desiredWidth,false);
		 	 break;
		
		  }

		  for(i=0;i<labels.size();i++) {
			 LabelInfo li = (LabelInfo)labels.get(i);
			 if( li.size.width>max_width )  
				 max_width  = li.size.width;
			 if( li.size.height>max_height ) 
				 max_height = li.size.height;
		  }	
		  
		  switch( orientation ) {

		   case VERTICAL_RIGHT:
		   case VERTICAL_LEFT:
		     csize = new Dimension(max_width+5,desiredHeight);
			 break;
		   case HORIZONTAL:
		     csize = new Dimension(desiredWidth,max_height);
		 	 break;
		
		  }

	}
	
	// ****************************************************************
    //	search nearest point stuff
		
	//transfrom given coordinates into pixel
	//Return (-100,-100) when cannot transform
	public Point transform(double x,double y,JLAxis xAxis) {
	
	  // The graph must have been measured before
	  // we can transform 
	  if( csize==null ) return new Point(-100,-100);
	 
	  double xlength = ( xAxis.getMax() - xAxis.getMin() );
	  int xOrg = boundRect.x;
	  int yOrg = boundRect.y+getLength();
	  double vx,vy;

	  // Check validity
	  if( Double.isNaN(y) || Double.isNaN(x) ) 
	    return new Point(-100,-100);
  
	  if( xAxis.getScale()==LOG_SCALE ) {
	    if( x<=0 ) 
		  return new Point(-100,-100);
		else       
	      vx = Math.log(x)/ln10;	  
	  } else
	      vx = x;	  

	  if( scale==LOG_SCALE ) {	  
	    if( y<=0 ) 
		  return new Point(-100,-100);
		else       
	      vy = Math.log(y)/ln10;	  
		  
	  } else
	      vy = y;
	  
	  double xratio =  (vx-xAxis.getMin())/(xlength) * (xAxis.getLength());
	  double yratio = -(vy -min)/(max-min) * csize.height;
				 
 	  // Saturate
	  if( xratio<-32000 ) xratio = -32000;
	  if( xratio> 32000 ) xratio =  32000;
	  if( yratio<-32000 ) yratio = -32000;
 	  if( yratio> 32000 ) yratio =  32000;
	  
	  return new Point( (int)(xratio) + xOrg , (int)(yratio) + yOrg );
	
	}
	
	//Return the square distance 
	private int distance2(int x1,int y1,int x2,int y2) {
	  return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
	}

	//Search the nearest point in the dataViews in
	//Normal monitoring mode
	public SearchInfo searchNearestNormal(int x,int y,JLAxis xAxis) {

      int sz =    dataViews.size();
	  int         norme2;
	  DataList    minP=null;
	  Point       minPt=null;
	  int         minNorme=Integer.MAX_VALUE;
	  JLDataView  minDataView=null;
	  int         minPl=0;
	  
	  for(int i=0;i<sz;i++) {
	  
	    JLDataView v = (JLDataView) dataViews.get(i);				
	    DataList   e = v.getData();
		
		while(e!=null) {
	
		  Point p = transform( e.x , v.getTransformedValue(e.y) , xAxis );		  
		  
		  if( boundRect.contains(p) ) {
	        norme2 = distance2(x , y , p.x , p.y);		  
	        if( norme2<minNorme ) {
			
	           minNorme = norme2;
	           minP = e;
			   minDataView = v;
			   minPt = p;
			   
			   // Compute placement for the value info window
			   if( p.x < ( boundRect.x + boundRect.width/2 ) ) {
			     if( p.y < ( boundRect.y + boundRect.height/2 ) ) {
				   minPl = SearchInfo.BOTTOMRIGHT;
				 } else {
				   minPl = SearchInfo.TOPRIGHT;
				 }
			   } else {
			     if( p.y < ( boundRect.y + boundRect.height/2 ) ) {
				   minPl = SearchInfo.BOTTOMLEFT;
				 } else {
				   minPl = SearchInfo.TOPLEFT;
				 }
			   }
			 }
		  }
		  	 
	      e=e.next;
		     
	    } 
			  
	  }
	  
	  if( minNorme==Integer.MAX_VALUE )
	    return new SearchInfo(); //No item found
	  else
	    return new SearchInfo(minPt.x,minPt.y,minDataView,this,minP,minNorme,minPl); //No item found
	
	}

	//Search the nearest point in the dataViews in
	//XY monitoring mode
	public SearchInfo searchNearestXY(int x,int y,JLAxis xAxis) {

      int sz =    dataViews.size();
	  int         norme2;
	  DataList    minP=null;
	  DataList    minXP=null;
	  Point       minPt=null;
	  int         minNorme=Integer.MAX_VALUE;
	  JLDataView  minDataView=null;
	  int         minPl=0;
      
	  JLDataView w = (JLDataView) xAxis.getViews().get(0);				
	  
	  for(int i=0;i<sz;i++) {
	  
	    JLDataView v = (JLDataView) dataViews.get(i);				
	    DataList   e = v.getData();
		DataList   f = w.getData();
		
		while(e!=null && f!=null) {
	
		  Point p = transform( w.getTransformedValue(f.y) , v.getTransformedValue(e.y) , xAxis );
		  
		  if( boundRect.contains(p) ) {
	        norme2 = distance2(x , y , p.x , p.y);		  
	        if( norme2<minNorme ) {
			
	           minNorme = norme2;
	           minP  = e;
	           minXP = f;
			   minDataView = v;
			   minPt = p;
			   
			   // Compute placement for the value info window
			   if( p.x < ( boundRect.x + boundRect.width/2 ) ) {
			     if( p.y < ( boundRect.y + boundRect.height/2 ) ) {
				   minPl = SearchInfo.BOTTOMRIGHT;
				 } else {
				   minPl = SearchInfo.TOPRIGHT;
				 }
			   } else {
			     if( p.y < ( boundRect.y + boundRect.height/2 ) ) {
				   minPl = SearchInfo.BOTTOMLEFT;
				 } else {
				   minPl = SearchInfo.TOPLEFT;
				 }
			   }
			 }
		  }
		  	 
	      e=e.next;
	      f=f.next;
		     
	    } 
			  
	  }
	  
	  if( minNorme==Integer.MAX_VALUE )
	    return new SearchInfo(); //No item found
	  else {
		SearchInfo si=new SearchInfo(minPt.x,minPt.y,minDataView,this,minP,minNorme,minPl);
	    si.setXValue( minXP , w );
		return si;
	  }
	
	}
	
	//Search the nearest point in the dataViews and
	//return a SearchInfo
	public SearchInfo searchNearest(int x,int y,JLAxis xAxis) {

      int sz =    dataViews.size();
	  int         norme2;
	  DataList    minP=null;
	  Point       minPt=null;
	  int         minNorme=Integer.MAX_VALUE;
	  JLDataView  minDataView=null;
	  int         minPl=0;
	  
	  //Search only in graph area
	  if( !boundRect.contains(x,y) ) return new SearchInfo();

	  if( xAxis.isXY() ) {
		return searchNearestXY(x,y,xAxis);
	  } else {
		return searchNearestNormal(x,y,xAxis);
	  }


	}

	// ****************************************************************
	// Compute labels
	// Expert usage
	private void computeLabels(FontRenderContext frc,double length,boolean invert) {

		double sz = max-min;
		int    pos,w,h,i;
		int    lgth = (int)length;
		java.util.Date date;
		String s;
		double    startx;
		double    prec;
		LabelInfo lastLabel=null;

		labels.clear();
		Rectangle2D bounds;

		switch( annotation ) {

		  case TIME_ANNO:

			// Only for HORINZONTAL axis !
			// This has nothing to fo with TIME_FORMAT

		    java.text.SimpleDateFormat format;
			int       round;
			double    desiredPrec;

			//find optimal precision
			boolean found=false;
			i=0;
			while(i<timePrecs.length && !found) {
			  int n = (int)( (max-min) / timePrecs[i] );
			  found = (n<=tick);
			  if( !found ) i++;
			}

			if( !found ) {
			  // TODO Year Linear scale
		      i--;
			  desiredPrec = 10*YEAR;
			  format=yearFormat;
			} else {
			  desiredPrec = timePrecs[i];
			  format = timeFormats[i];
			}

			// round to multiple of prec
			round  = (int)(min/desiredPrec);
		  	startx = (round+1) * desiredPrec;

			if(invert) 
			   pos = (int)(length * (1.0-(startx-min)/sz));
			else 
			   pos = (int)(length * ((startx-min)/sz));

		  	calendar.setTimeInMillis((long)startx);
            date = calendar.getTime();
            s = format.format(date);
		    bounds = labelFont.getStringBounds(s, frc);
			w=(int)bounds.getWidth();
			h=(int)bounds.getHeight();
		    lastLabel = new LabelInfo(s,true,w,h,pos,0);
		    labels.add( lastLabel );

			double minPrec = (((double)w*1.3) / length) * sz;

			// Correct to avoid label overlap
			prec=desiredPrec;
			while( prec<minPrec ) prec += desiredPrec;

			startx += prec;

			// Build labels
		    while( startx<=max ) {

			  if(invert) 
			    pos = (int)(length * (1.0-(startx-min)/sz));
			  else 
			    pos = (int)(length * ((startx-min)/sz));

		  	  calendar.setTimeInMillis((long)startx);
              date = calendar.getTime();
              s = format.format(date);
		      bounds = labelFont.getStringBounds(s, frc);

			  // Check limit
			  if( pos>0 && pos<lgth ) {
				w=(int)bounds.getWidth();
				h=(int)bounds.getHeight();
				lastLabel = new LabelInfo(s,true,w,h,pos,0);
		        labels.add( lastLabel );				  
			  }

			  startx += prec;

			}
		    break;

		  case VALUE_ANNO:

			//Do not compute labels on vertical axis if no data displayed
			if( dataViews.size()==0 && orientation!=HORIZONTAL ) return;

			double fontAscent = (double)parent.getFontMetrics(labelFont).getAscent();
			int nbMaxLab = (int)(length/fontAscent);
			int n;
		    int step=0;


			if( nbMaxLab>tick ) nbMaxLab=tick;

			// Find the best precision

			if( scale==LOG_SCALE ) {

			  prec = 1;   // Decade
			  step = -1;  // Logarithm subgrid

			  startx = Math.rint(min);

			  n = (int)((max-min)/prec);
			
			  while( n > nbMaxLab ) {
			    prec = prec*2;
				step = 2;
			    n = (int)((max-min)/prec);
			    if( n > nbMaxLab ) {
			      prec = prec * 5;
				  step = 10;
			      n = (int)((max-min)/prec);
				}
			  }
			
			} else {

			  prec = computeLowTen(max-min);
			  step = 10;
			  
			  n = (int)((max-min)/(prec/2.0));
			
			  while( n <= nbMaxLab ) {
			    prec = prec / 2.0;
			    step = 5;
			    n = (int)((max-min)/(prec/5.0));
			    if( n <= nbMaxLab ) {
			      prec = prec / 5.0;
			      step = 10;
			      n = (int)((max-min)/(prec/2.0));
				}
			  }

			  // round to multiple of prec
			  round  = (int)(min/prec);
		  	  startx = (round-1) * prec;

			}
			
			//Build labels

		    while( startx <= max ) {
			
			  if(invert) 
			    pos = (int)(length * (1.0-(startx-min)/sz));
			  else 
			    pos = (int)(length * ((startx-min)/sz));
			  	
			  double vt;
			  if( scale==LOG_SCALE ) vt = Math.pow(10.0,startx);
			  else 			         vt = startx;

		      s = formatValue(vt,prec);		  
			  bounds = labelFont.getStringBounds(s, frc);
			  
			  // Check overlap 
			  boolean visible = true;
			  if( lastLabel!=null && orientation==HORIZONTAL ) {
			    // Ckech bounds
				visible = (lastLabel.pos + lastLabel.size.width/2) <
					      (pos - ((int)bounds.getWidth())/2) ;					      
			  }

			  if( startx >= (min-1e-12) ) {
				LabelInfo li = new LabelInfo(s,visible,(int)bounds.getWidth(),(int)fontAscent,pos,step);
                if( visible ) lastLabel = li;
		        labels.add( li );
			  }

			  startx += prec;

			}
		    break;

		}

	}
    
	// ****************************************************************
	// Painting stuff
	
	// Paint last point of a dataView
	// Expert Usage
	public void drawFast( Graphics g , Point lp , Point p , JLDataView v ) {
	
		if( lp!=null ) {
		  if( boundRect.contains(lp) ) {
	   
            Graphics2D  g2  = (Graphics2D)g;
		    Stroke old = g2.getStroke();
		    BasicStroke bs = createStroke(v.getLineWidth(),v.getStyle());
		    if(bs!=null) g2.setStroke(bs);
			
		    // Draw 	 		
		    g.setColor( v.getColor() );
		    g.drawLine( lp.x , lp.y , p.x , p.y );
		 		 
		    //restore default stroke
		    g2.setStroke(old);
		  }
	   } 
	   
	   //Paint marker
	   Color oc = g.getColor();
	   g.setColor( v.getMarkerColor() );
	   paintMarker( g , v.getMarker() , v.getMarkerSize() , p.x , p.y );
	   g.setColor(oc);
	
	}
		
	// Paint a marker a the specified position
	// Expert usage
    public static void paintMarker(Graphics g,int mType,int mSize,int x,int y) {
	  
	  int mSize2  = mSize/2;
	  int mSize21 = mSize/2 + 1;
	  
	  switch( mType ) {
	    case JLDataView.MARKER_DOT:
		  g.fillOval(x-mSize2,y-mSize2,mSize,mSize); 
		break;
	    case JLDataView.MARKER_BOX:
		  g.fillRect(x-mSize2,y-mSize2,mSize,mSize); 
		break;
	    case JLDataView.MARKER_TRIANGLE:
		  triangleShape.translate(x,y);
		  g.fillPolygon(triangleShape); 
		  triangleShape.translate(-x,-y);
		break;
        case JLDataView.MARKER_DIAMOND:
		  diamondShape.translate(x,y);
		  g.fillPolygon(diamondShape); 
		  diamondShape.translate(-x,-y);
		break;
        case JLDataView.MARKER_STAR:
		  g.drawLine( x-mSize2 , y+mSize2 , x+mSize21 , y-mSize21 );
		  g.drawLine( x+mSize2 , y+mSize2 , x-mSize21 , y-mSize21 );
		  g.drawLine( x   , y-mSize2 , x   , y+mSize21 );
		  g.drawLine( x-mSize2 , y   , x+mSize21 , y   );		  
		break;
        case JLDataView.MARKER_VERT_LINE:
		  g.drawLine( x   , y-mSize2 , x   , y+mSize21 );
		break;
        case JLDataView.MARKER_HORIZ_LINE:
		  g.drawLine( x-mSize2 , y   , x+mSize21 , y   );		  
		break;
        case JLDataView.MARKER_CROSS:
		  g.drawLine( x   , y-mSize2 , x   , y+mSize21 );
		  g.drawLine( x-mSize2 , y   , x+mSize21 , y   );		  
		break;
		case JLDataView.MARKER_CIRCLE:
		  g.drawOval(x-mSize2,y-mSize2,mSize+1,mSize+1); 
		break;		
	    case JLDataView.MARKER_SQUARE:
		  g.drawRect(x-mSize2,y-mSize2,mSize,mSize); 
		break;		
	  }
	  	
	}
	
	//Create a Basic stroke for the dashMode
	//Return null when no stroke is needed
	// Expert usage
	public static BasicStroke createStroke(int lw,int style) {
	
	  BasicStroke bs=null;
	  	  
	  if( lw!=1 || style!=JLDataView.STYLE_SOLID ) {
	   switch( style ) {		
	    case JLDataView.STYLE_DOT:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dotPattern, 0.0f);
		  break;
	    case JLDataView.STYLE_DASH:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
		  break;
	    case JLDataView.STYLE_LONG_DASH:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longDashPattern, 0.0f);
		  break;
	    case JLDataView.STYLE_DASH_DOT:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashDotPattern, 0.0f);
		  break;
	   default:
          bs = new BasicStroke(lw);
	 	  break;
	   }
	 }
	 
	 return bs;
   }
	
	
	// Draw a sample line
	// Expert usage
	public static void drawSampleLine( Graphics g , int x , int y , JLDataView v ) {
	
           Graphics2D  g2  = (Graphics2D)g;
		   Stroke old = g2.getStroke();
		   BasicStroke bs = createStroke(v.getLineWidth(),v.getStyle());
		   if(bs!=null) g2.setStroke(bs);
			
		   // Draw 	 		
		   g.drawLine( x , y , x+40 , y );
		 		 
		   //restore default stroke
		   g2.setStroke(old);		   
		   
		   //Paint marker
		   Color oc = g.getColor();
		   g.setColor( v.getMarkerColor() );
		   paintMarker( g , v.getMarker() , v.getMarkerSize() , x+20 , y );
	  	   g.setColor(oc);
		   
	}

	// Paint dataviews along the given axis
	// Expert usage
	private void paintDataViews(Graphics g,JLAxis xAxis,int xOrg,int yOrg) {
	  if( xAxis.isXY() )
	    paintDataViewsXY(g,xAxis,xOrg,yOrg);
	  else
	    paintDataViewsNormal(g,xAxis,xOrg,yOrg);
	}

	// Paint dataviews along the given axis
	// Expert usage
	private void paintDataViewsNormal(Graphics g,JLAxis xAxis,int xOrg,int yOrg) {
	
	 int nbView=dataViews.size();

	 int k,j;

	 //Clip
     g.setClip(xOrg , yOrg-getLength() , xAxis.getLength() , getLength() );

	 //Draw dataView
	 for(k=0;k<nbView;k++) {
		   
	   JLDataView v = (JLDataView)dataViews.get(k);
	   DataList l  = v.getData();
       int lw    = v.getLineWidth();
		   
	   if( l!=null ) {
		 
 	     int nbPoint = v.getDataLength();
		 int mType   = v.getMarker();
		 int mSize   = v.getMarkerSize();
		 Color mColor = v.getMarkerColor();
		 
		 int pointX[] = new int[nbPoint];
		 int pointY[] = new int[nbPoint];

 	     // Transform points
		   
		 double minx,maxx,lx;
		 double miny,maxy,ly;
		 double xratio;
		 double yratio;
		 double vt;
		 double A0 = v.getA0();
		 double A1 = v.getA1();
		 double A2 = v.getA2();

		 minx = xAxis.getMin();
		 maxx = xAxis.getMax();
		 lx   = xAxis.getLength();
		 int sx   = xAxis.getScale();

		 miny = min;
		 maxy = max;
		 ly   = getLength();
		   
		 j=0;
		 boolean valid = true;
		   
		 // Set the stroke mode for dashed line
         Graphics2D  g2  = (Graphics2D)g;
		 Stroke old = g2.getStroke();
		 BasicStroke bs = createStroke(v.getLineWidth(),v.getStyle());
		 
		   
	     while(l!=null) {
		   
		   g.setColor( mColor );
		   

		   while( valid && l!=null ) {
			   // Compute transform here for performance
			   vt = A0 + A1*l.y + A2*l.y*l.y;
			   valid = !Double.isNaN(vt) && (sx!=LOG_SCALE || l.x>1e-100)
				       && (scale!=LOG_SCALE || vt>1e-100);

			   if( valid ) {
				 
				 if( sx==LOG_SCALE )
		           xratio =  (Math.log(l.x)/ln10-minx)/(maxx-minx) * lx;
				 else
		           xratio =  (l.x-minx)/(maxx-minx) * lx;

				 if( scale==LOG_SCALE )
		           yratio = -(Math.log(vt)/ln10-miny)/(maxy-miny) * ly;
				 else
		           yratio = -(vt -miny)/(maxy-miny) * ly;

				 // Saturate
				 if( xratio<-32000 ) xratio = -32000;
				 if( xratio> 32000 ) xratio =  32000;
				 if( yratio<-32000 ) yratio = -32000;
				 if( yratio> 32000 ) yratio =  32000;				 
		         pointX[j] = (int)(xratio) + xOrg;
		         pointY[j] = (int)(yratio) + yOrg;
				 
				 // Draw marker
			     if( mType>JLDataView.MARKER_NONE ) 
			       paintMarker(g,mType,mSize,pointX[j],pointY[j]);
		         l=l.next;
			     j++;
			   }

		   }
			 
		   if(bs!=null) g2.setStroke(bs);
	       
		   // Draw the polyline 		 
		   g.setColor( v.getColor() );
		   
		   if( j>1 && lw>0 ) 	 		
		     g.drawPolyline( pointX , pointY , j );
			   
		   //restore default stroke
		   g2.setStroke(old);		   
			 
		   j=0;		 
		   if( !valid ) {
		     l=l.next;
			 valid=true;
		   }
			 
		 } // End (while l!=null)
		 		 
	   } // End (if l!=null)

	 } // End (for k<nbView)

	 //Restore clip
	 Dimension d = parent.getSize();
     g.setClip(0 , 0 , d.width , d.height );

	}

	// Paint dataviews along the given axis in XY mode
	// Expert usage
	private void paintDataViewsXY(Graphics g,JLAxis xAxis,int xOrg,int yOrg) {
	
	 int nbView=dataViews.size();

	 int k,j;

	 //Clip
     g.setClip(xOrg , yOrg-getLength() , xAxis.getLength() , getLength() );

	 //Draw dataView
	 for(k=0;k<nbView;k++) {
		   
	   JLDataView v = (JLDataView)dataViews.get(k);
	   DataList l  = v.getData();
	   JLDataView w = (JLDataView)xAxis.getViews().get(0);
	   DataList m  = w.getData();
       int lw    = v.getLineWidth();
		   
	   if( l!=null && m!=null ) {
		 
 	     int nbPoint = v.getDataLength();
		 int mType   = v.getMarker();
		 int mSize   = v.getMarkerSize();
		 Color mColor = v.getMarkerColor();
		 
		 int pointX[] = new int[nbPoint];
		 int pointY[] = new int[nbPoint];

 	     // Transform points
		   
		 double minx,maxx,lx;
		 double miny,maxy,ly;
		 double xratio;
		 double yratio;
		 double vtx;
		 double vty;
		 double A0y = v.getA0();
		 double A1y = v.getA1();
		 double A2y = v.getA2();
		 double A0x = w.getA0();
		 double A1x = w.getA1();
		 double A2x = w.getA2();

		 minx = xAxis.getMin();
		 maxx = xAxis.getMax();
		 lx   = xAxis.getLength();
		 int sx   = xAxis.getScale();

		 miny = min;
		 maxy = max;
		 ly   = getLength();
		   
		 j=0;
		 boolean valid = true;
		   
		 // Set the stroke mode for dashed line
         Graphics2D  g2  = (Graphics2D)g;
		 Stroke old = g2.getStroke();
		 BasicStroke bs = createStroke(v.getLineWidth(),v.getStyle());
		 
		   
	     while(l!=null && m!=null) {
		   
		   g.setColor( mColor );
		   

		   while( valid && l!=null && m!=null ) {

			   // Compute transform here for performance
			   vty = A0y + A1y*l.y + A2y*l.y*l.y;
			   vtx = A0x + A1x*m.y + A2x*m.y*m.y;

			   valid = !Double.isNaN(vtx) && !Double.isNaN(vty) && 
				       (sx!=LOG_SCALE || vtx>1e-100) && 
					   (scale!=LOG_SCALE || vty>1e-100);

			   if( valid ) {
				 
				 if( sx==LOG_SCALE )
		           xratio =  (Math.log(vtx)/ln10-minx)/(maxx-minx) * lx;
				 else
		           xratio =  (vtx-minx)/(maxx-minx) * lx;

				 if( scale==LOG_SCALE )
				   yratio = -(Math.log(vty)/ln10-miny)/(maxy-miny) * ly;
		         else
				   yratio = -(vty -miny)/(maxy-miny) * ly;
				 
				 // Saturate
				 if( xratio<-32000 ) xratio = -32000;
				 if( xratio> 32000 ) xratio =  32000;
				 if( yratio<-32000 ) yratio = -32000;
				 if( yratio> 32000 ) yratio =  32000;				 
		         pointX[j] = (int)(xratio) + xOrg;
		         pointY[j] = (int)(yratio) + yOrg;
				 
				 // Draw marker
			     if( mType>JLDataView.MARKER_NONE ) 
			       paintMarker(g,mType,mSize,pointX[j],pointY[j]);
		         l=l.next;
				 m=m.next;
			     j++;
			   }

		   }

			 
		   if(bs!=null) g2.setStroke(bs);
	       
		   // Draw the polyline 		 
		   g.setColor( v.getColor() );
		   
		   if( j>1 && lw>0 ) 	 		
		     g.drawPolyline( pointX , pointY , j );
			   
		   //restore default stroke
		   g2.setStroke(old);		   
			 
		   j=0;		 
		   if( !valid ) {
		     l=l.next;
			 m=m.next;
			 valid=true;
		   }
			 
		 } // End (while l!=null)
		 		 
	   } // End (if l!=null)

	 } // End (for k<nbView)

	 //Restore clip
	 Dimension d = parent.getSize();
     g.setClip(0 , 0 , d.width , d.height );

	}

	// Paint sub tick outside label limit
	// Expert usage
    private int paintExtraYSubTicks(Graphics g,int x0,int ys,int length,int y0,int la,BasicStroke bs,int step) {

	  int j,h;
	  Graphics2D g2 = (Graphics2D)g;	  
      Stroke old = g2.getStroke();
	  
	  if( subtickVisible  ) {
		       
	    if( step == -1 ) {			    
			   
		  for( j=0 ; j<logStep.length ; j++ ) {
		    h = ys+(int)(length*logStep[j]);
			if( h>y0 && h<(y0+csize.height) ) {
	          g.drawLine( x0 -1 , h , x0 + 2 , h );
			  if( gridVisible && subGridVisible ) {
			    if(bs!=null) g2.setStroke(bs);
	            g.drawLine(x0,h,x0+la,h);
			    g2.setStroke(old);
			  }
			}
		  }
			   
		} else if (step>0) {

		  for( j=0 ; j<linStep.length ; j+=(10/step) ) {
		    h = ys+(int)(length*linStep[j]);
			if( h>y0 && h<(y0+csize.height) ) {
		      g.drawLine( x0 -1 , h , x0 + 2 , h );
			  if( (j>0) && gridVisible && subGridVisible ) {
			    if(bs!=null) g2.setStroke(bs);
	            g.drawLine(x0,h,x0+la,h);
			    g2.setStroke(old);
			  }
			}
		  }
			   
		}
		
		return length;
		   
	  } else {
	  
	    return 0;
		
	  }

	}

	// Paint sub tick outside label limit
	// Expert usage
    private int paintExtraXSubTicks(Graphics g,int y0,int xs,int length,int x0,int la,BasicStroke bs,int step) {

	  int j,w;
	  Graphics2D g2 = (Graphics2D)g;	  
      Stroke old = g2.getStroke();
	  
	  if( subtickVisible  ) {
		       
	    if( step == -1 ) {			    
			   
		  for( j=0 ; j<logStep.length ; j++ ) {
		    w = xs+(int)(length*logStep[j]);
			if( w>x0 && w<(x0+csize.width) ) {
	          g.drawLine( w , y0-1 , w , y0 + 2 );
			  if( gridVisible && subGridVisible ) {
			    if(bs!=null) g2.setStroke(bs);
	            g.drawLine(w , y0, w ,y0+la );
			    g2.setStroke(old);
			  }
			}
		  }
			   
		} else if (step>0) {

		  for( j=0 ; j<linStep.length ; j+=(10/step) ) {
		    w = xs+(int)(length*linStep[j]);
			if( w>x0 && w<(x0+csize.width) ) {
		      g.drawLine( w , y0 -1 , w , y0 + 2 );
			  if( (j>0) && gridVisible && subGridVisible ) {
			    if(bs!=null) g2.setStroke(bs);
	            g.drawLine(w,y0,w,y0+la);
			    g2.setStroke(old);
			  }
			}
		  }
			   
		}
		
		return length;
		   
	  } else {
	  
	    return 0;
		
	  }

	}


	// Paint Y sub tick and return tick spacing
	// Expert usage
    private int paintYSubTicks(Graphics g,int i,int x0,int y,int length,int la,BasicStroke bs) {

	  int j,h;
	  Graphics2D g2 = (Graphics2D)g;	  
      Stroke old = g2.getStroke();

	  if( subtickVisible && i<(labels.size()-1) ) {
		       
	    LabelInfo li = (LabelInfo)labels.get(i+1);
		length += li.pos;
		int step = li.subtick_step;
			
		if( step==-1 ) {  // Logarithmic step

		  for( j=0 ; j<logStep.length ; j++ ) {
		      h = y+(int)(length*logStep[j]);
	          g.drawLine( x0 -1 , h , x0 + 2 , h );
			  if( gridVisible && subGridVisible ) {
			    if(bs!=null) g2.setStroke(bs);
	            g.drawLine(x0,h,x0+la,h);
			    g2.setStroke(old);
			  }
			}

			   
		} else if (step>0) {  // Linear step

		  for( j=0 ; j<linStep.length ; j+=(10/step)) {
		    h = y+(int)(length*linStep[j]);
		    g.drawLine( x0 -1 , h , x0 + 2 , h );
			if( (j>0) && gridVisible && subGridVisible ) {
			  if(bs!=null) g2.setStroke(bs);
	          g.drawLine(x0,h,x0+la,h);
			  g2.setStroke(old);
			}
		  }
			   
		}
		
		return length;
		   
	  } else {
	  
	    return 0;
		
	  }

	}

	// Paint X sub tick and return tick spacing
	// Expert usage
    private int paintXSubTicks(Graphics g,int i,int y0,int x,int length,int la,BasicStroke bs) {

	  int j,w;
	  Graphics2D g2 = (Graphics2D)g;	  
      Stroke old = g2.getStroke();

	  if( subtickVisible && i<(labels.size()-1) ) {
		       
	    LabelInfo li = (LabelInfo)labels.get(i+1);
		length += li.pos;
		int step = li.subtick_step;
			
		if( step==-1 ) {  // Logarithmic step

		  for( j=0 ; j<logStep.length ; j++ ) {
		      w = x+(int)(length*logStep[j]);
	          g.drawLine( w , y0-1 , w , y0 + 2 );
			  if( gridVisible && subGridVisible ) {
			    if(bs!=null) g2.setStroke(bs);
	            g.drawLine(w , y0 , w , y0+la );
			    g2.setStroke(old);
			  }
			}

			   
		} else if (step>0) {  // Linear step

		  for( j=0 ; j<linStep.length ; j+=(10/step)) {
		    w = x+(int)(length*linStep[j]);
		    g.drawLine( w , y0-1 , w , y0 + 2 );
			if( (j>0) && gridVisible && subGridVisible ) {
			  if(bs!=null) g2.setStroke(bs);
	          g.drawLine(w , y0 , w , y0+la );
			  g2.setStroke(old);
			}
		  }
			   
		}
		
		return length;
		   
	  } else {
	  
	    return 0;
		
	  }

	}
	// Compute the medium color of c1,c2
	public Color computeMediumColor(Color c1,Color c2) {
		return new Color( (c1.getRed()+3*c2.getRed())/4 , 
			              (c1.getGreen()+3*c2.getGreen())/4 ,
						  (c1.getBlue()+3*c2.getBlue())/4 );
	}

	// Paint the axis and its DataView at the specified position along the given axis
	// Expert usage
    public void paintAxis(Graphics g,FontRenderContext frc,int x0,int y0,JLAxis xAxis,int xOrg,int yOrg,Color back) {

	 //Do not draw vertical axis without data
	 if( orientation!=HORIZONTAL && dataViews.size()==0 ) return;

     int i,j,x,y,la=0;
     BasicStroke bs=null;
	 Graphics2D g2 = (Graphics2D)g;
	 int tickStep=0;
	 Color subgridColor = computeMediumColor( labelColor , back );
     
	 g.setFont(labelFont);

	 // stroke for the grid
   	 if( gridVisible ) bs = createStroke(1,gridStyle);
  	 la=xAxis.getLength()-2;

     switch( orientation ) {

	   case VERTICAL_LEFT:

		 for(i=0;i<labels.size();i++) {
		   
		   // Draw labels
		   g.setColor(labelColor);
		   LabelInfo li = (LabelInfo)labels.get(i);

		   x = x0 + (csize.width-4) - li.size.width;
		   y = li.pos + y0 ;
		   g.drawString( li.value , x , y + li.size.height/3);

		   //Draw tick
		   g.drawLine( x0 + (csize.width-2) , y , x0 + (csize.width+3) , y );

		   //Draw the grid
		   if( gridVisible ) {
			  Stroke old = g2.getStroke();
			  if( bs!=null ) g2.setStroke(bs);
		      g.drawLine( x0 + (csize.width+2) , y , x0 + (csize.width+2) + la , y );
		 	  g2.setStroke(old);
		   }

		   //Draw sub tick
		   g.setColor(subgridColor);
           int ts = paintYSubTicks(g,i,x0+csize.width,y,-li.pos,la,bs);
		   if( ts!=0 && tickStep==0 ) tickStep=ts;

		 }
		 
		 //Draw extra sub ticks (outside labels limit)
		 if(tickStep!=0) {		    
		    LabelInfo lis = (LabelInfo)labels.get(0);
		    LabelInfo lie = (LabelInfo)labels.get(labels.size()-1);
		 
			paintExtraYSubTicks(g,x0+csize.width,y0+lis.pos-tickStep,tickStep,y0,la,bs,lis.subtick_step);
			paintExtraYSubTicks(g,x0+csize.width,y0+lie.pos,tickStep,y0,la,bs,lis.subtick_step);
		 }

		 // Draw Axe
	     g.setColor(labelColor);
		 g.drawLine( x0 + csize.width , y0 , x0 + csize.width , y0+csize.height );

		 if(name!=null) {
	   	   Rectangle2D bounds = labelFont.getStringBounds(name, frc);		
		   g.drawString( name , (x0 + csize.width) - (int)bounds.getWidth()/2 , y0 - (int)(bounds.getHeight()/2));
		 }
		 
		 boundRect.setRect(x0+csize.width,y0,la,csize.height);
		 paintDataViews(g,xAxis,xOrg,yOrg);
		 break;


	   case VERTICAL_RIGHT:

		 for(i=0;i<labels.size();i++) {
		   
		   // Draw labels
  	       g.setColor(labelColor);
		   LabelInfo li = (LabelInfo)labels.get(i);

		   y = li.pos + y0 ;
		   g.drawString( li.value , x0+6 , y + li.size.height/3);

		   //Draw tick
		   g.drawLine( x0-2 , y , x0+2 , y );

		   //Draw the grid
		   if( gridVisible ) {
			  Stroke old = g2.getStroke();
			  if( bs!=null ) g2.setStroke(bs);
		      g.drawLine( x0-2 , y , x0-2 - la , y );
		 	  g2.setStroke(old);
		   }

		   //Draw sub tick
   	       g.setColor(subgridColor);
           int ts = paintYSubTicks(g,i,x0,y,-li.pos,-la,bs);
		   if( ts!=0 && tickStep==0 ) tickStep=ts;

		 }

		 //Draw extra sub ticks (outside labels limit)
		 if(tickStep!=0) {		    
		    LabelInfo lis = (LabelInfo)labels.get(0);
		    LabelInfo lie = (LabelInfo)labels.get(labels.size()-1);
		 
			paintExtraYSubTicks(g,x0,y0+lis.pos-tickStep,tickStep,y0,-la,bs,lis.subtick_step);
			paintExtraYSubTicks(g,x0,y0+lie.pos,tickStep,y0,-la,bs,lis.subtick_step);
		 }
		 
		 // Draw Axe
	     g.setColor(labelColor);
		 g.drawLine( x0 , y0 , x0 , y0+csize.height );
		 
		 if(name!=null) {
	   	   Rectangle2D bounds = labelFont.getStringBounds(name, frc);		
		   g.drawString( name , x0 - (int)bounds.getWidth()/2 , y0 - (int)(bounds.getHeight()/2));
		 }

		 boundRect.setRect(x0-la-1,y0,la,csize.height);
		 paintDataViews(g,xAxis,xOrg,yOrg);
		 break;

	   case HORIZONTAL:

		 for(i=0;i<labels.size();i++) {
		   
		   // Draw labels
  	       g.setColor(labelColor);
		   LabelInfo li = (LabelInfo)labels.get(i);

		   x = li.pos + x0;
		   y = y0;
		   if(li.isVisible)
		   g.drawString( li.value , x - li.size.width/2 , y + li.size.height + 2);

		   //Draw tick
		   g.drawLine( x , y0-2 , x , y0+3 );

		   //Draw the grid
		   if( gridVisible ) {
			  Stroke old = g2.getStroke();
			  if( bs!=null ) g2.setStroke(bs);
		      g.drawLine( x , y0-2 , x , y0-2-la );
		 	  g2.setStroke(old);
		   }

		   //Draw sub tick
   	       g.setColor(subgridColor);
           int ts = paintXSubTicks(g,i,y,x,-li.pos,-la,bs);
		   if( ts!=0 && tickStep==0 ) tickStep=ts;

		 }

		 //Draw extra sub ticks (outside labels limit)
		 if(tickStep!=0) {		    
		    LabelInfo lis = (LabelInfo)labels.get(0);
		    LabelInfo lie = (LabelInfo)labels.get(labels.size()-1);
		 
			paintExtraXSubTicks(g,y0,x0+lis.pos-tickStep,tickStep,x0,-la,bs,lis.subtick_step);
			paintExtraXSubTicks(g,y0,x0+lie.pos,tickStep,x0,-la,bs,lis.subtick_step);
		 }

		 // Draw Axe
   	     g.setColor(labelColor);
		 g.drawLine( x0 , y0 , x0 + csize.width , y0 );

		 if(name!=null) {
	   	   Rectangle2D bounds = labelFont.getStringBounds(name, frc);		
		   g.drawString( name , x0 + ((csize.width) - (int)bounds.getWidth())/2 , 
			                    y0 + 2*(int)bounds.getHeight() );
		 }
		 
		 boundRect.setRect(x0,y0-la,csize.width,la);
	 	 break;
		
	 }
			
    }

}

//
// SearchInfo.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002
package fr.esrf.tangoatk.widget.util.chart;

public class SearchInfo {

   public static final int TOPLEFT     = 0;
   public static final int TOPRIGHT    = 1;
   public static final int BOTTOMLEFT  = 2;
   public static final int BOTTOMRIGHT = 3;
   
   boolean     found;
   int         x;
   int         y;
   JLAxis      axis;

   JLDataView  dataView;
   DataList    value;

   JLDataView  xdataView; // XY moni
   DataList    xvalue;    // XY moni
   double      dist;
   int         placement;   
   
   SearchInfo(int x,int y,JLDataView  dataView,JLAxis axis,DataList value,double dist,int placement) 
   {
     this.found=true;
     this.x=x;
     this.y=y;
     this.dataView=dataView;
     this.value=value;
     this.dist=dist;
     this.placement=placement;
     this.axis=axis;
	 this.xvalue=null;
	 this.xdataView=null;
   }

   public void setXValue(DataList d,JLDataView  x) {
	 this.xvalue    = d;
	 this.xdataView = x;
   }

   SearchInfo() 
   {
     this.found=false;
     this.dist=Integer.MAX_VALUE;
   }
   
   public String toString() {
     if( found ) 
       return "SearchInfo[ Hit ("+x+","+y+") View name="+dataView.getName()+
              " Org ("+value.x+","+value.y+") Dist="+dist;
     else 
       return "SearchInfo[ No Hit]";
   }
   
}

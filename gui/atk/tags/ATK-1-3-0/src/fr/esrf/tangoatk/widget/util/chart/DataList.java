//
// DataList.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;

// Class to handle data (LinkedList)
class DataList {

	// Original coordinates
	public double x;
	public double y;
	
	//pointer to next item
	DataList next;

	//Construct a node
	DataList(double x,double y) {
	  this.x = x;
	  this.y = y;
	  next=null;
	}
	
}

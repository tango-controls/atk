// JLChartEvent.java
// 
// Description:       

package fr.esrf.tangoatk.widget.util.chart;
import java.util.EventObject;

/**   Event sent when when the user click on the graph */
public class JLChartEvent extends EventObject {

    public SearchInfo searchResult;
    
    public JLChartEvent(Object source,SearchInfo si) {
	super(source);
	searchResult = si;
    }

    public void setSource(Object source) {
	this.source = source;
    }
    
    public String getVersion() {
	return "$Id$";
    }

    public Object clone() {
	return new JLChartEvent(source, searchResult);
    }
    
}

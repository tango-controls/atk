// File:          ChartDataEventAdapter.java
// Created:       2002-04-30 13:10:11, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-04-30 13:16:22, assum>
// 
// $Id$
// 
// Description:


package fr.esrf.tangoatk.widget.attribute;

import com.klg.jclass.chart.*;

class ChartDataEventAdapter extends ChartDataEvent {

    public ChartDataEventAdapter(Object source, int type,
				 int serie, int point) {
	super(source, type, serie, point);
    }
    
    public void setPointIndex(int i) {
	pointIndex = i;
    }

    public void setSeriesIndex(int i) {
	seriesIndex = i;
    }

    public void setType(int i) {
	type = i;
    }

    public void setAll(int type, int serie, int point) {
	this.type = type;
	this.seriesIndex = serie;
	this.pointIndex = point;
    }

    public String toString() {
	return "{ " + type + ", " + seriesIndex + ", " + pointIndex + " }";
    }

}

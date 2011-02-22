package fr.esrf.tangoatk.core.util;

/*
 * NonAttrNumberSpectrumEvent.java
 *
 * Created on 12 septembre 2003, 11:02
 */



import java.util.EventObject;


/**
 *
 * @author  OUNSY
 */
public class NonAttrNumberSpectrumEvent extends EventObject {
    
    /** Creates a new instance of NonAttrNumberSpectrumEvent */
    
    public NonAttrNumberSpectrumEvent(INonAttrNumberSpectrum source, double[] xvalue, double[] yvalue) {
        super(source);
	setValue(xvalue,yvalue);
    }

    public double[] getXValue() {
	return xvalue;
    }

    public double[] getYValue() {
	return yvalue;
    }

    public void setValue(double [] xvalue,double [] yvalue) {
	this.xvalue = xvalue;
	this.yvalue = yvalue;
    }

    public void setSource(INonAttrNumberSpectrum source) {
	this.source = source;
    }

    
    private double [] xvalue;  
    private double [] yvalue;  
}

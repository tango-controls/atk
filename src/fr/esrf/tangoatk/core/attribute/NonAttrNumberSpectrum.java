package fr.esrf.tangoatk.core.attribute;

/*
 * NonAttrNumberSpectrum.java
 *
 * Created on 12 septembre 2003, 11:29
 */

import javax.swing.event.EventListenerList;

import fr.esrf.tangoatk.core.*;

/**
 *
 * @author  OUNSY
 */
public class NonAttrNumberSpectrum implements INonAttrNumberSpectrum {
    
    /** Creates a new instance of NonAttrNumberSpectrum */
    
    public NonAttrNumberSpectrum() {
           eventListeners = new EventListenerList();
    }

    public void addNonAttrSpectrumListener(INonAttrSpectrumListener l) {
    	eventListeners.add(INonAttrSpectrumListener.class , l);
    }

    public void removeNonAttrSpectrumListener(INonAttrSpectrumListener l) {
    	eventListeners.remove(INonAttrSpectrumListener.class , l);
    }


    public double[] getXValue() {
        return xValue;
    }
    
    public void setXYValue(double[] xd,double[] yd) {
        int xLength = 0;
        int yLength = 0;
        if (xd != null)
        {
            xLength = xd.length;
        }
        if (yd != null)
        {
            yLength = yd.length;
        }
        xValue = new double[xLength];
        for (int i=0; i < xLength ; i++ )
             xValue[i] = xd[i];
        
        spectrumValue = new double[yLength];
        for (int i=0; i < yLength ; i++ )
             spectrumValue[i] = yd[i];
        
        fireNonAttrNumberSpectrumEvent(xValue,spectrumValue);
    }
    
    public double[] getYValue() {
        return spectrumValue;
    }
        
 // Notify all listeners that have registered interest for
 // notification on this event type.  The event instance 
 // is lazily created using the parameters passed into 
 // the fire method.

    protected void fireNonAttrNumberSpectrumEvent(double [] xv,double [] yv)
    {
        NonAttrNumberSpectrumEvent nonAttrNumberSpectrumEvent = null;
        // Guaranteed to return a non-null array
        Object[] listeners = eventListeners.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==INonAttrSpectrumListener.class) {
                // Lazily create the event:
                if (nonAttrNumberSpectrumEvent == null)
                    nonAttrNumberSpectrumEvent = new NonAttrNumberSpectrumEvent(this,xv,yv);
                ((INonAttrSpectrumListener)listeners[i+1]).spectrumChange(nonAttrNumberSpectrumEvent);
            }
        }
     }
                
    public String getXUnit() {
        return xunit;
    }    

    public void setXUnit(String xunit) {
        this.xunit = xunit;
    }
    
    public String getXName() {
        return xname;
    }
    
    public void setXName(String xname) {
        this.xname = xname;
    }
    
    public String getYUnit() {
        return yunit;
    }    

    public void setYUnit(String yunit) {
        this.yunit = yunit;
    }
    
    public String getYName() {
        return yname;
    }
    
    public void setYName(String yname) {
        this.yname = yname;
    }
    
    private double [] spectrumValue = null;
    private double [] xValue = null;
    private EventListenerList eventListeners;
    private String xname = "Unknown";
    private String xunit = "Unknown";
    private String yname = "Unknown";
    private String yunit = "Unknown";
   
}

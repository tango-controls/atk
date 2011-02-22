package fr.esrf.tangoatk.core.util;

/*
 * INonAttrNumberSpectrum.java
 *
 * Created on 12 septembre 2003, 10:55
 */


/**
 *
 * @author  OUNSY
 */
public interface INonAttrNumberSpectrum {
    
     public void addNonAttrSpectrumListener(INonAttrSpectrumListener l) ;

    public void removeNonAttrSpectrumListener(INonAttrSpectrumListener l);

    public double[] getYValue();

    public void setXYValue(double[] xd,double[] yd);

    public double[] getXValue();

    public String getXUnit();

    public void setXUnit( String xunit);

    public String getXName();

    public void setXName( String xname);

    public String getYUnit();

    public void setYUnit( String yunit);

    public String getYName();

    public void setYName( String yname);
   
}

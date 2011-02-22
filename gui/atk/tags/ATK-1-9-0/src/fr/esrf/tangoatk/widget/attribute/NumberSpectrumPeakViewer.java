/*
 * NumberSpectrumPeakViewer.java
 *
 * Author:Faranguiss Poncet 2004
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.Color;
import javax.swing.*;
import java.util.*;

import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.AAttribute;

/**
 * A class to display a scalar spectrum attribute . The NumberSpectrumPeakViewer
 * displays the spectrum according to index value (x axis shows value index).
 * This viewer accepts the min and max values for x axis.
 * 
 * The application can add NumberScalar attributes as peaks and the viewer
 * will highlight them in the graph. 
 *
 * @author  E.S.R.F
 */
public class NumberSpectrumPeakViewer 
                         extends NumberSpectrumViewer 
                         implements fr.esrf.tangoatk.core.INumberScalarListener
{


  List                  peak_models = null;
  List                  peak_data_views = null;
  JLDataView            refCurveDV = null;
  INumberScalar         x_min_model = null;
  INumberScalar         x_max_model = null;
  INumberScalar         ind_track_span_model = null;
  double                min_x = 0;
  double                max_x = 0;
  double                min_y1 = 0;
  double                max_y1 = 0;
  int                   index_track_span=0;
  Color                 spectrumColor;
  Color                 refCurveColor;
  boolean               xAxisAutoScale=true;
  boolean               y1AxisAutoScale=true;
  double                refCurveA0 = 0.0;
  double                refCurveA1 = 1.0;
  double[]              refCurveValues = null;

  /** Creates new NumberSpectrumPeakViewer */
  public NumberSpectrumPeakViewer()
  {
      // Create the graph
      super();
      spectrumColor = Color.red;
      refCurveColor = Color.red;
      dvy.setColor(spectrumColor);
      peak_models = new Vector();
      peak_data_views = new Vector();
      refCurveDV = new JLDataView();
      refCurveDV.setColor(refCurveColor);
      refCurveDV.setName("Reference");
  }


  /**
   * Gets the color of the curve representing the spectrum attribute.
   * @return Curve color
   * @see NumberSpectrumPeakViewer#setSpectrumColor
   */
  public java.awt.Color getSpectrumColor()
  {
     return spectrumColor;
  }

  
  /**
   * Sets the color of the curve representing the spectrum attribute.
   * @param c Curve color
   * @see NumberSpectrumPeakViewer#getSpectrumColor
   */
  public void setSpectrumColor(Color c)
  {
     if (c == null)
        return;
	
     spectrumColor = c;
     dvy.setColor(spectrumColor);
  }


  /**
   * Gets the color of the reference curve.
   * @return Reference Curve color
   * @see NumberSpectrumPeakViewer#setRefCurveColor
   */
  public java.awt.Color getRefCurveColor()
  {
     return refCurveColor;
  }

  
  /**
   * Sets the color of the reference curve.
   * @param c Reference Curve color
   * @see NumberSpectrumPeakViewer#getRefCurveColor
   */
  public void setRefCurveColor(Color c)
  {
     if (c == null)
        return;
	
     refCurveColor = c;
     refCurveDV.setColor(refCurveColor);
  }


  /**
   * Gets the autoscaling state of the X axis of the JLChart.
   * @return X axis autoscaling state
   * @see NumberSpectrumPeakViewer#setXAxisAutoScale
   */
  public boolean getXAxisAutoScale()
  {
     return xAxisAutoScale;
  }

  
  /**
   * Sets the autoscaling state of the X axis of the JLChart.
   * @param X axis autoscaling state
   * @see NumberSpectrumPeakViewer#getXAxisAutoScale
   */
  public void setXAxisAutoScale(boolean auto)
  {	
     xAxisAutoScale = auto;
     getXAxis().setAutoScale(xAxisAutoScale);
  }


  /**
   * Gets the autoscaling state of the Y1 axis of the JLChart.
   * @return Y1 axis autoscaling state
   * @see NumberSpectrumPeakViewer#setY1AxisAutoScale
   */
  public boolean getY1AxisAutoScale()
  {
     return y1AxisAutoScale;
  }

  
  /**
   * Sets the autoscaling state of the Y1 axis of the JLChart.
   * @param Y1 axis autoscaling state
   * @see NumberSpectrumPeakViewer#getY1AxisAutoScale
   */
  public void setY1AxisAutoScale(boolean auto)
  {	
     y1AxisAutoScale = auto;
     getY1Axis().setAutoScale(y1AxisAutoScale);
  }



  /**
   * Sets the min and max values for Y1 axis to two doubles
   *
   * @param min
   * @param max
   */
  public void setY1AxisMinMax(double min, double max)
  {
     if (min >= max)
        return;
     
     if (y1AxisAutoScale)
        return;
	
     min_y1 = min;
     max_y1 = max;
     
     getY1Axis().setMinimum(min);
     getY1Axis().setMaximum(max);
     
  }


  /**
   * Sets the min and max values for X axis to two scalar attributes
   *
   * @param min
   * @param max
   */
  public void setXaxisModels(INumberScalar min, INumberScalar max)
  {
     
     if (x_min_model != null)
     {
        x_min_model.removeNumberScalarListener(this);
	x_min_model = null;
     }
	
     if (x_max_model != null)
     {
        x_max_model.removeNumberScalarListener(this);
 	x_max_model = null;
     }
	
     if ( (min == null) || (max == null) )
       return;
       
     if (min == max)
       return;
       
     x_min_model = min;
     x_max_model = max;
     
     x_min_model.addNumberScalarListener(this);
     x_max_model.addNumberScalarListener(this);
     
     min.refresh();
     max.refresh();
     
  }


  /**
   * Adds a scalar attribute as the model for a peak representation on the graph
   *
   * @param peak
   */
  public void addPeakModel(INumberScalar peak)
  {
     JLDataView       peakDV = null;
      
     if (peak == null)
        return;
	
     if (peakAlreadyUsed(peak))
        return;
	     
     peak_models.add(peak);
     
     peakDV = new JLDataView();
     peakDV.setMarker(JLDataView.MARKER_BOX);
     peakDV.setMarkerSize(4);
     peakDV.setColor(Color.black);
     peakDV.setMarkerColor(Color.black);
     peakDV.setLabelVisible(false);
     peak_data_views.add(peakDV);

     getY1Axis().addDataView(peakDV);
     
     peak.addNumberScalarListener(this);
     
     peak.refresh();
     
  }


  /**
   * Removes a scalar attribute from the list of the peak models
   *
   * @param peak
   */
  public void removePeakModel(INumberScalar peak)
  {
     
     if (peak == null)
        return;
	
     if (peakAlreadyUsed(peak))
     {
        removePeakFromList(peak);
     }
  }


  /**
   * returns the number of the peak models associated with this viewer
   *
   * @return the number of peaks
   */
  public int getPeakModelNumber()
  {
     int   pn = -1;
     
     pn = peak_models.size();
     return pn;
  }


  /**
   * Removes all the existing peak models
   *
   * @param peak
   */
  public void clearPeakModels()
  {
     int             pmodeSize = -1;
     int             pdvSize = -1;
     int             index = 0;
     Object          obj = null;
     INumberScalar   pObj = null;
     JLDataView      jldvObj = null;
     
     pmodeSize = peak_models.size();
     pdvSize = peak_data_views.size();
     
     
     for (index = 0; index < pmodeSize; index++)
     {
         try
	 {
	     obj = null;
	     obj = peak_models.get(index);
	     if (obj != null)
	     {
	        if (obj instanceof INumberScalar)
		{
		   pObj = (INumberScalar) obj;
		   pObj.removeNumberScalarListener(this);
		}
	     }
	 }
	 catch (Exception e)
	 {
	     System.out.println("NumberSpectrumPeakViewer : this case should not happen;");
	     System.out.println("NumberSpectrumPeakViewer : cannot get peak model from peak_models List for index.");
	 }
     }
 
      
     for (index = 0; index < pdvSize; index++)
     {
         try
	 {
	     obj = null;
	     obj = peak_data_views.get(index);
	     if (obj != null)
	     {
	        if (obj instanceof JLDataView)
		{
		   jldvObj = (JLDataView) obj;
		   getY1Axis().removeDataView(jldvObj);
		}
	     }
	 }
	 catch (Exception e)
	 {
	     System.out.println("NumberSpectrumPeakViewer : this case should not happen;");
	     System.out.println("NumberSpectrumPeakViewer : cannot get peak dataview from peak_data_views List for index.");
	 }
     }
     
     peak_models = new Vector();
     peak_data_views = new Vector();
     
  }
  
  

  /**
   * Sets the tracking span (width) to a NumberScalar model
   *
   * @param trackSpan
   */
  public void setIndexTrackSpanModel(INumberScalar trackSpan)
  {
     
     if (ind_track_span_model != null)
     {
        ind_track_span_model.removeNumberScalarListener(this);
	ind_track_span_model = null;
	index_track_span = 0;
     }
	
     if (trackSpan == null)
       return;
       
     ind_track_span_model = trackSpan;
     
     ind_track_span_model.addNumberScalarListener(this);
     
     trackSpan.refresh();     
  }
  
  

  /**
   * Returns the IndexTrackSpanModel if it exists and null otherwise
   *
   * @return the IndexTrackSpan number scalar model
   */
  public INumberScalar getIndexTrackSpanModel()
  {
     return ind_track_span_model;
  }


  /**
   * Gets the x value (in the spectrum) for point selected by a JLChartEvent.
   * @return the x value
   * @see NumberSpectrumPeakViewer#getYValue
   */
  public double getXValue(JLChartEvent e)
  {
     return e.getXValue();
  }


  /**
   * Gets the y value (in the spectrum) for point selected by a JLChartEvent.
   * @return the y value
   * @see NumberSpectrumPeakViewer#getXValue
   */
  public double getYValue(JLChartEvent e)
  {
     return e.getYValue();
  }


  // -------------------------------------------------------------
  // xAxis min and max listener, peakIndex listener
  // -------------------------------------------------------------

  public void numberScalarChange(NumberScalarEvent evt)
  {
     int    peakIndex = -1;
     
     if (evt.getSource() == x_min_model)
     {
        minXchange(evt);
	return;
     }
     
     if (evt.getSource() == x_max_model)
     {
        maxXchange(evt);
	return;
     }
     
     if (evt.getSource() == ind_track_span_model)
     {
        trackSpanChange(evt);
	return;
     }
     
     
     peakIndex = peak_models.indexOf(evt.getSource());
     if ( peakIndex >= 0 )
     {
        refreshPeak(peakIndex, evt);
     }
  }


  private void minXchange(NumberScalarEvent evt)
  {
       min_x = evt.getValue();
       
       if (min_x < max_x)
       {
	  if (xAxisAutoScale != true);
	  {
             getXAxis().setMinimum(min_x);
             getXAxis().setMaximum(max_x);
          }
	  changeAffineTransform();
       }
  }


  private void maxXchange(NumberScalarEvent evt)
  {
       max_x = evt.getValue();
       
       if (min_x < max_x)
       {
	  if (xAxisAutoScale != true);
	  {
             getXAxis().setMinimum(min_x);
             getXAxis().setMaximum(max_x);
          }
	  changeAffineTransform();
       }
  }
  

  private void changeAffineTransform()
  {
      int      nb_x_values, refCurve_nb_x_values;
      double   step, refCurve_step;
      
      if (dvy == null)
         return;
	 
      nb_x_values = dvy.getDataLength();
      if (nb_x_values <= 0)
         return;
      
      step = (max_x - min_x) / nb_x_values;
      
      setXAxisAffineTransform(min_x, step);
            
  }


  private void trackSpanChange(NumberScalarEvent evt)
  {
       if ( evt.getValue() > 5.0 )
       {
           index_track_span = (int) evt.getValue();
	   return;
       }
       else
           index_track_span = 0;
  }
  
  
  private boolean peakAlreadyUsed(INumberScalar p)
  {
     if (peak_models.size() == 0)
        return false;
	
     if ( peak_models.indexOf(p) < 0 )
        return false;
     else
        return true;
  }
  
  
  private void removePeakFromList(INumberScalar p)
  {
     int   peakIndex = -1;
     
     
     if (peak_models.size() == 0)
        return;
	
     peakIndex = peak_models.indexOf(p);
     
     if ( peakIndex < 0 )
        return;
     else
     {
        try
	{
	    peak_models.remove(peakIndex);
	    p.removeNumberScalarListener(this);
	}
	catch (Exception e)
	{
	   System.out.println("NumberSpectrumPeakViewer : this case should not happen;");
	   System.out.println("NumberSpectrumPeakViewer : cannot remove peak model from peak_models List.");
	}
	
        try
	{
	    peak_data_views.remove(peakIndex);
	}
	catch (Exception e)
	{
	   System.out.println("NumberSpectrumPeakViewer : this case should not happen;");
	   System.out.println("NumberSpectrumPeakViewer : cannot remove peak model from peak_data_views List.");
	}
     }
  }
  

  private void refreshPeak(int peakInd, NumberScalarEvent evt)
  {

     JLDataView       peakDV = null;
     int              plotIndex = -1, maxDim;
     double           xfVal=0.0, yfVal=0.0, xVal, yVal, xlVal=0.0, ylVal=0.0;
     AAttribute       spectrumAtt = null;
  
     if (model instanceof AAttribute)
        spectrumAtt = (AAttribute) model;
	
     if (spectrumAtt != null)
        maxDim = spectrumAtt.getMaxXDimension();
     else
        maxDim = 0;
	
     try
     {
	 peakDV = (JLDataView) peak_data_views.get(peakInd);
	 if (peakDV != null)
	 {
             plotIndex = (int) evt.getValue();
	     
	     if (index_track_span > 5)
	     { // set the first point and the last point of the peak dataview
	          int    firstIndex, lastIndex;
		  
		  firstIndex = plotIndex - (index_track_span / 2);
		  lastIndex = plotIndex + (index_track_span / 2);
		  
		  if (firstIndex < 0)
		     firstIndex = 0;
		     
		  if (lastIndex >= maxDim)
		     lastIndex = maxDim - 1;
//System.out.println("firstIndex = "+firstIndex + "  plotIndex = " + plotIndex + " et  lastIndex = " + lastIndex);
		  xfVal = dvy.getXValueByIndex(firstIndex);
		  yfVal = dvy.getYValueByIndex(plotIndex);
	     
		  if ((xfVal == Double.NaN) || (yfVal == Double.NaN))
	             return;
		  
		  xlVal = dvy.getXValueByIndex(lastIndex);
		  ylVal = dvy.getYValueByIndex(plotIndex);
	     
		  if ((xlVal == Double.NaN) || (ylVal == Double.NaN))
	             return;
             }
	     
	     xVal = dvy.getXValueByIndex(plotIndex);
	     yVal = dvy.getYValueByIndex(plotIndex);
	     
	     if ((xVal == Double.NaN) || (yVal == Double.NaN))
	        return;

		
	     peakDV.reset();
	     
	     if (index_track_span > 5)
	     { // add the first point of the peak dataview
	         peakDV.add(xfVal, yfVal);
	     }
	     
	     peakDV.add(xVal, yVal);
	     
	     if (index_track_span > 5)
	     { // add the last point of the peak dataview
	         peakDV.add(xlVal, ylVal);
	     }

	     // Commit change
	     repaint();
	 }
     }
     catch (IndexOutOfBoundsException  iobExp)
     {
         return;
     }
  }
  
  
  
  

  
  
  
  public void resetRefCurve()
  {
     refCurveDV.reset();
  }
  
  
  public void updateRefCurve(double[]  values, double min_x_ref, double max_x_ref)
  {
      int       refCurve_nb_x_values;
      double    refCurve_step;
      

      refCurve_nb_x_values = values.length;
      
      if (refCurve_nb_x_values <= 0)
         return;
      
      refCurveValues = values;
      
      refCurve_step = (max_x_ref - min_x_ref) / refCurve_nb_x_values;
      
      if (refCurve_step <= 0)
      {
	  refCurveA0 = max_x_ref;
	  refCurveA1 = 0-refCurve_step;
      }
      else
      {
	  refCurveA0 = min_x_ref;
	  refCurveA1 = refCurve_step;
      }
      
      refCurveDV.reset();

      for (int i = 0; i < refCurve_nb_x_values; i++)
      {
          refCurveDV.add(refCurveA0 + refCurveA1 * (double) i, values[i]);
      }
  }
  
  
  public void displayRefCurve()
  {
       getY1Axis().removeDataView(refCurveDV);
       getY1Axis().addDataView(refCurveDV);
  }
  
  public void removeRefCurve()
  {
       getY1Axis().removeDataView(refCurveDV);
  }



  public static void main(String[] args)
  {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       NumberSpectrumPeakViewer               nspv = new NumberSpectrumPeakViewer();
       INumberSpectrum                        spect;
       INumberScalar                          minx, maxx;
       INumberScalar                          peakH, peakV;
       INumberScalar                          tSpan;
       JFrame                                 mainFrame;

       try
       {
          spect = (INumberSpectrum) attList.add("sr/tune_server/1/Spectrum");
          minx = (INumberScalar) attList.add("sr/tune_server/1/FreqMin");
          maxx = (INumberScalar) attList.add("sr/tune_server/1/FreqMax");
          peakH = (INumberScalar) attList.add("sr/tune_server/1/PeakHCenter");
          peakV = (INumberScalar) attList.add("sr/tune_server/1/PeakVCenter");
          tSpan = (INumberScalar) attList.add("sr/tune_server/1/IndexTrackSpan");
	  nspv.setModel(spect);
	  nspv.setXaxisModels(minx, maxx);
	  nspv.addPeakModel(peakH);
	  nspv.addPeakModel(peakV);
	  nspv.setIndexTrackSpanModel(tSpan);
       } 
       catch (Exception e)
       {
          System.out.println("caught exception : "+ e.getMessage());
	  System.exit(-1);
       }
       
       mainFrame = new JFrame();
       
       mainFrame.addWindowListener(
	       new java.awt.event.WindowAdapter()
			  {
			      public void windowActivated(java.awt.event.WindowEvent evt)
			      {
				 // To be sure that the refresher (an independente thread)
				 // will begin when the the layout manager has finished
				 // to size and position all the components of the window
				 attList.startRefresher();
			      }
			  }
                                     );
				     

       mainFrame.setContentPane(nspv);
       mainFrame.pack();

       mainFrame.show();


  } // end of main ()


}

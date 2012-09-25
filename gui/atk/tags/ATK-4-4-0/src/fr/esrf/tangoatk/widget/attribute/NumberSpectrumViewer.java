/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/*
 * NumberSpectrumViewer.java
 *
 * Author:JL Pons 2003
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.NumberScalarEvent;
import javax.swing.*;
import java.util.*;
import java.awt.*;

import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.TangoDs.AttrManip;
import com.braju.format.Format;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;

/**
 * A class to display a scalar spectrum attribute . The NumberSpectrumViewer
 * displays the spectrum according to index value (x axis shows value index).
 *
 * @author  E.S.R.F
 */
public class NumberSpectrumViewer extends AdvancedJLChart 
                                  implements ISpectrumListener, INumberScalarListener,
                                             IJLChartActionListener, IJLChartListener, JDrawable
{

  protected INumberSpectrum model = null;
  protected JLDataView dvy;
  protected SimplePropertyFrame pf = null;
  protected double A0=0.0;
  protected double A1=1.0;
  protected String xAxisUnit="";
  protected String format;
  protected boolean unitVisible = false;//DO NOT display unit by default for backward compatibility
  protected boolean qualityVisible = false;//DO NOT display quality by default for backward compatibility
  protected final static String DISPLAY_UNIT_STRING = "Display/Hide Unit";
  protected final static String DISPLAY_QUALITY_STRING = "Display/Hide Attribute Quality";
  protected final static String SET_SPECTRUM_MENU_LABEL = "Set attribute";
  
  private boolean manageXaxis = false;
  
  private boolean xAxisManagedByProps = false;
  private boolean xAxisManagedByAtts = false;
  private boolean xAxisMinMaxApplied = false;
  private double  xAxisMin = -1.0;
  private double  xAxisMax = -1.0;
 
  private INumberScalar   x_min_model = null;
  private INumberScalar   x_max_model = null;
  private double          x_min_value = 0;
  private double          x_max_value = 0;
  
  // For writable attributes only :  used for setting the numberspectrum attribute
  private NumberSpectrumTableEditor        spectrumEditor = null;
  private JMenuItem                        setAttMenuItem = null;

  static String[] exts = {"manageXaxis", "graphSettings"};


  /** Creates new fNumberSpectrumViewer */
  public NumberSpectrumViewer()
  {

    // Create the graph
    super();

    setBorder(new javax.swing.border.EtchedBorder());
    setBackground(new java.awt.Color(180, 180, 180));
    getY1Axis().setAutoScale(true);
    getXAxis().setAutoScale(true);
    getXAxis().setAnnotation(JLAxis.VALUE_ANNO);

    dvy = new JLDataView();
    getY1Axis().addDataView(dvy);
        
    //Suppress non applyable menuItems in the context of a simple NumberSpectrumViewer
    removeMenuItem(AdvancedJLChart.MENU_DATALOAD);
    removeMenuItem(AdvancedJLChart.MENU_RESET);

    addUserAction("Attribute properties");
    addUserAction(DISPLAY_UNIT_STRING);
    addUserAction(DISPLAY_QUALITY_STRING);
    addUserAction(SET_SPECTRUM_MENU_LABEL);
    
    setAttMenuItem = getUserActionMenuItem(SET_SPECTRUM_MENU_LABEL);
    if (setAttMenuItem != null)
        setAttMenuItem.setEnabled(false);
    
    addJLChartActionListener(this);
    setJLChartListener(this);
  }

  /* Bean Property stuff */

  /**
   * Gets if the viewer should manage the X axis according to a min and max value.
   */
  public boolean getManageXaxis()
  {
    return (manageXaxis);
  }

  /**
   * Sets if the viewer should manage the X axis according to a min and max value. This method should be called BEFORE setModel()
   * @param b
   */
  public void setManageXaxis(boolean b)
  {
     if (model != null) return;
     manageXaxis = b;
  }

  /**
   * Gets the x Axis unit.
   */
  public String getXAxisUnit() {
    return (xAxisUnit);
  }

  /**
   * Sets the x Axis unit.
   * @param u
   */
  public void setXAxisUnit(String u) {
    xAxisUnit = u;
  }
  
  

  /**
   * Sets an affine tranform to the X axis. This allows to transform
   * spectra index displayed on X axis.
   * @param a0
   * @param a1
   */
  public void setXAxisAffineTransform(double a0,double a1) {
    A0 = a0;
    A1 = a1;
  }

  // ------------------------------------------------------
  // Implementation of JDrawable interface
  // ------------------------------------------------------
  public void initForEditing() {
    // Add a demo curve
    dvy.add(0.0,0.0);
    dvy.add(2.0,1.0);
    dvy.add(4.0,1.2);
    dvy.add(6.0,3.0);
    dvy.add(8.0,0.2);
    dvy.add(10.0,1.5);
    setPreferredSize(new Dimension(400,300));
  }

  public JComponent getComponent() {
    return this;
  }

  public String getDescription(String name)
  {
    if (name.equalsIgnoreCase("manageXaxis"))
    {
       return "When enabled, the viewer will adapt the Xaxis min and Xaxis max \n" +
              "to the attribute properties defined in the Tango DataBase. \n" +
              "Possible values are: true, false.";
    }
    
    if (name.equalsIgnoreCase("graphSettings"))
    {
       return getHelpString();
    }

    return "";

  }

  public String[] getExtensionList() {
    return exts;
  }

  public boolean setExtendedParam(String name,String value,boolean popupErr)
  {
    if (name.equalsIgnoreCase("manageXaxis"))
    {
        if(value.equalsIgnoreCase("true"))
        {
           setManageXaxis(true);
           return true;
        } 
        else 
           if (value.equalsIgnoreCase("false"))
           {
              setManageXaxis(false);
              return true;
           } 
           else
           {
              showJdrawError(popupErr,"manageXaxis","Wrong syntax: 'true' or 'false' expected.");
              return false;
           }  
    }

    if (name.equalsIgnoreCase("graphSettings"))
    {
      // Handle a 'bug' in CFFileReader parsing
      if(!value.endsWith("\n")) value = value + "\n";
      String err = setSettings(value);
      if(err.length()>0) showJdrawError(popupErr,"graphSettings",err);
      return true;

    }

    return false;

  }

  public String getExtendedParam(String name)
  {
     if(name.equalsIgnoreCase("manageXaxis"))
     {
        if (getManageXaxis())
           return "true";
        else
           return "false";
     }
     
     if(name.equalsIgnoreCase("graphSettings"))
     {
        return getSettings();
     }

     return "";
  }

  private void showJdrawError(boolean popup,String paramName,String message) {
    if(popup)
      JOptionPane.showMessageDialog(null, "NumberSpectrumViewer: "+paramName+" incorrect.\n" + message,
                                    "Error",JOptionPane.ERROR_MESSAGE);
  }

  // -------------------------------------------------------------
  // JLChart action listener
  // -------------------------------------------------------------

  public void actionPerformed(JLChartActionEvent evt)
  {
     if (evt.getName().equals("Attribute properties"))
     {
        if (pf == null)
        {
           pf = new SimplePropertyFrame();
           pf.setModel(model);
        }
        pf.setVisible(true);
     }
     else
        if (DISPLAY_UNIT_STRING.equals( evt.getName() ))
        {
           setUnitVisible( !isUnitVisible() );
        }
        else 
           if (DISPLAY_QUALITY_STRING.equals( evt.getName() ))
           {
              setQualityVisible( !isQualityVisible() );
           }
           else 
              if (SET_SPECTRUM_MENU_LABEL.equals( evt.getName() ))
              {
                 setSpectrumAttribute();
              }
  }

  public boolean getActionState(JLChartActionEvent evt) {
    return (model != null);
  }

  // -------------------------------------------------------------
  // Spectrum listener
  // -------------------------------------------------------------

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent)
  {
     if (errorEvent.getSource() != model) //source is not the spectrum
	return;

     if (isQualityVisible())
     {
       setToolTipText( model.getName() + ": " + IAttribute.UNKNOWN );
       dvy.setLabelColor( ATKConstant.getColor4Quality(IAttribute.UNKNOWN) );
     }
     // Clear the graph
     dvy.reset();
     repaint();
     refreshTableSingle(dvy);
  }

  public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {
    if (isQualityVisible()) {
      setToolTipText( model.getName() + ": " + evt.getState() );
      dvy.setLabelColor( ATKConstant.getColor4Quality(evt.getState()) );
    }
    // no repaint() needed because spectrumChange() already does it
  }

  public void spectrumChange(fr.esrf.tangoatk.core.NumberSpectrumEvent numberSpectrumEvent)
  {
     double[] value = numberSpectrumEvent.getValue();
     int length = value.length;
     
     if (xAxisManagedByProps) // the viewer must manage the Xaxis min and max
     {
        if (!xAxisMinMaxApplied) //min and max have never been taken into account
           changeXaxisAffineTransform(xAxisMin, xAxisMax, length);
        else
           if (dvy == null) // the data view has never been initialized
              changeXaxisAffineTransform(xAxisMin, xAxisMax, length);
           else
              if (dvy.getDataLength() != length) // the number of the points in the spectrum has changed
                 changeXaxisAffineTransform(xAxisMin, xAxisMax, length); 
     }
     
     if (xAxisManagedByAtts) // the viewer must manage the Xaxis min and max according to two attributes
     {
        if (!xAxisMinMaxApplied) //min and max have never been taken into account
           changeXaxisAffineTransform(x_min_value, x_max_value, length);
     }
   
     synchronized(dvy)
     {
        dvy.reset();

        for (int i = 0; i < length; i++)
        {
           dvy.add(A0 + A1 * (double) i, value[i], false);
        }
        dvy.updateFilters();

        // Commit change
        repaint();
        refreshTableSingle(dvy);
     }     
  }

  public String[] clickOnChart(JLChartEvent e) {
    String[] ret;

    if(model!=null) {

      String yValue;

      if( format==null ) {
        yValue = Double.toString(e.getTransformedYValue());
      } else if (format.indexOf('%') == -1) {
        yValue = AttrManip.format(format, e.getTransformedYValue());
      } else {
        Object[] o = {new Double(e.getTransformedYValue())};
        yValue = Format.sprintf(format, o);
      }

      ret = new String[3];
      ret[0] = model.getName();
      if( xAxisUnit.length()>0 )
        ret[1] = "X=" + e.getTransformedXValue() + " " + xAxisUnit;
      else
        ret[1] = "X=" + e.getTransformedXValue();

      ret[2] = "Y=" + yValue + " " + model.getUnit();

    } else {

      ret = new String[2];
      if( xAxisUnit.length()>0 )
        ret[0] = "X=" + e.getTransformedXValue() + " " + xAxisUnit;
      else
        ret[0] = "X=" + e.getTransformedXValue();
      ret[1] = "Y=" + e.getTransformedYValue();

    }


    return ret;
  }

  /**<code>setModel</code> Set the model.
   * @param v  Value to assign to model.
   */
  public void setModel(INumberSpectrum v)
  {
      clearModel();
      if (v == null) 
      {
         repaint();
         return;
      }
      
      this.model = v;
      if (model.isWritable())
      {
         if (setAttMenuItem != null)
            setAttMenuItem.setEnabled(true);
         spectrumEditor = new NumberSpectrumTableEditor();
         spectrumEditor.setModel(this.model);
      }
      
      if (manageXaxis)
      {
          if (!model.areAttPropertiesLoaded())
              model.loadAttProperties();

          if (model.hasMinxMaxxAttributes())
             xAxisManagedByAtts = true;
          else
          {
             if (model.hasMinxMaxxProperties())
             {
                xAxisManagedByProps = true;
                getXAxis().setAutoScale(false);
                xAxisMin = model.getMinx();
                xAxisMax = model.getMaxx();
             }
          }
          xAxisMinMaxApplied = false;
      }
         
      format = model.getFormat();
      dvy.setUserFormat(format);
      dvy.setUnit(v.getUnit());
      dvy.setName(v.getName());
      if (isQualityVisible())
      {
         setToolTipText( model.getName() + ": " + model.getState() );
         dvy.setLabelColor( ATKConstant.getColor4Quality(model.getState()) );
      }
      
      model.addSpectrumListener(this);
      if (pf != null)
         pf.setModel(model);
      if (unitVisible)
         getY1Axis().setName( v.getUnit() );
      else
         getY1Axis().setName( "" );
      repaint();
  }

  /**<code>clearModel</code> removes the model.
   */
  
  public void clearModel()
  {
      xAxisManagedByProps = false;
      xAxisManagedByAtts = false;
      xAxisMinMaxApplied = false;
      xAxisMin = -1.0;
      xAxisMax = -1.0;
      x_min_value = 0;
      x_max_value = 0;
      if (model != null)
      {
          model.removeSpectrumListener(this);
          if (pf != null) pf.setModel(null);
          model = null;
      }
      
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
      setToolTipText(null);
      dvy.setLabelColor(Color.BLACK);
      if (spectrumEditor != null)
      {
          spectrumEditor.clearModel();
          spectrumEditor = null;
      }
      if (setAttMenuItem != null)
         setAttMenuItem.setEnabled(false);      
  }
  

  /**
   * Apply configuration.
   * @param cfg String containing configuration
   * @return error string when failure or an empty string when succesfull
   */

  public String setSettings(String cfg) {

    CfFileReader f = new CfFileReader();
    Vector p;

    if (!f.parseText(cfg)) {
      return "NumberSpectrumViewer.setSettings: Failed to parse given config";
    }

    // General settings
    applyConfiguration(f);


    // Local settings
    p = f.getParam("xaxis_transform");
    if (p != null)
      setXAxisAffineTransform(OFormat.getDouble(p.get(0).toString()),
                              OFormat.getDouble(p.get(1).toString()));
    p = f.getParam("xaxis_unit");
    if (p != null) xAxisUnit = p.get(0).toString();

    // Axis
    getXAxis().applyConfiguration("x",f);
    getY1Axis().applyConfiguration("y1", f);

    // Dataview options
    dvy.applyConfiguration("dvy",f);

    return "";
  }

  /**
   * Return configuration.
   * @return current chart configuration as string
   */
  public String getSettings() {

    String to_write = "";

    // General settings
    to_write += getConfiguration();

    // Local settings
    to_write += "xaxis_transform:" + A0 + "," + A1 + "\n";
    to_write += "xaxis_unit:\'" + xAxisUnit + "\'\n";

    // xAxis
    to_write += getXAxis().getConfiguration("x");
    to_write += getY1Axis().getConfiguration("y1");

    // Dataview
    to_write += dvy.getConfiguration("dvy");

    return to_write;
  }

  /**
   * Returns whether unit shoud be visible or not
   * @return boolean corresponding to the choice
   */
  public boolean isUnitVisible () {
    return unitVisible;
  }

  /**
   * Sets whether unit shoud be visible or not
   * @param displayUnit corresponding to the choice
   */
  public void setUnitVisible (boolean displayUnit) {
    this.unitVisible = displayUnit;
    if (displayUnit && (model != null) ) {
      getY1Axis().setName( model.getUnit() );
    }
    else
    {
      getY1Axis().setName( "" );
    }
    repaint();
  }

  public boolean isQualityVisible () {
    return qualityVisible;
  }

  public void setQualityVisible (boolean qualityVisible) {
    if(this.qualityVisible != qualityVisible) {
      this.qualityVisible = qualityVisible;
      if (qualityVisible) {
        if (model != null) {
          setToolTipText( model.getName() + ": " + model.getState() );
          dvy.setLabelColor( ATKConstant.getColor4Quality(model.getState()) );
        }
      }
      else {
        setToolTipText(null);
        dvy.setLabelColor(Color.BLACK);
      }
      repaint();
    }
  }

  public void setSpectrumAttribute ()
  {
      if (spectrumEditor == null) return;
      spectrumEditor.centerWindow();
      spectrumEditor.setVisible(true);    
  }

  @Override
  public void removeDataView (JLDataView view) {
    if (view != dvy) {
        super.removeDataView(view);
    }
  }

  @Override
  protected boolean prepareDataViewMenu (JLDataView dataView) {
    if (dataView == dvy)
       return false;
    boolean  b = super.prepareDataViewMenu(dataView);
    return b;
  }
  
  private void changeXaxisAffineTransform(double minx, double maxx, int nb)
  {
      if (maxx <= minx) return;
      if (nb <= 0) return;

      getXAxis().setMinimum(minx);
      getXAxis().setMaximum(maxx);
      double  step = (maxx - minx) / nb;
      setXAxisAffineTransform(minx, step);
      xAxisMinMaxApplied = true;
  }

  
  private void changeXaxisAffineTransform ()
  {
      if (dvy == null)
         return;
	 
      changeXaxisAffineTransform(x_min_value, x_max_value, dvy.getDataLength());
  }


  /**
   * Sets the min and max  for X axis to two scalar attributes
   *
   * @param min
   * @param max
   */
  public void setXaxisModels(INumberScalar min, INumberScalar max)
  {
     if (!manageXaxis) return;
     if (!xAxisManagedByAtts) return;
     
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
     x_min_value = 0;
     x_max_value = 0;
     getXAxis().setAutoScale(true);
     xAxisMinMaxApplied	= false;
     
     if ( (min == null) || (max == null) )
       return;
       
     if (min == max)
       return;
     
     if (!(min.getNameSansDevice().equalsIgnoreCase(model.getMinxAttName())))
        return;
     if (!(max.getNameSansDevice().equalsIgnoreCase(model.getMaxxAttName())))
        return;
     
     x_min_model = min;
     x_max_model = max;
     getXAxis().setAutoScale(false);
     
     x_min_model.addNumberScalarListener(this);
     x_max_model.addNumberScalarListener(this);
     
     min.refresh();
     max.refresh();
     
  }
  

  public void numberScalarChange(NumberScalarEvent evt)
  {
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
  }


  private void minXchange(NumberScalarEvent evt)
  {
       x_min_value = evt.getValue();
       
       if (x_min_value < x_max_value)
       {
	  if (getXAxis().isAutoScale() != true);
	  {
             getXAxis().setMinimum(x_min_value);
             getXAxis().setMaximum(x_max_value);
          }
	  changeXaxisAffineTransform();
       }
  }


  private void maxXchange(NumberScalarEvent evt)
  {
       x_max_value = evt.getValue();
       
       if (x_min_value < x_max_value)
       {
	  if (getXAxis().isAutoScale() != true);
	  {
             getXAxis().setMinimum(x_min_value);
             getXAxis().setMaximum(x_max_value);
          }
	  changeXaxisAffineTransform();
       }
  }
  
private static void addXaxisMinMaxAtt (NumberSpectrumViewer viewer, INumberSpectrum vModel)
{
    if (viewer == null) return;
    if (vModel == null) return;
    if (vModel.hasMinxMaxxAttributes())
    {
        String          attFullName = null;
        fr.esrf.tangoatk.core.IEntity         ie;
        fr.esrf.tangoatk.core.AttributeList       attl = new fr.esrf.tangoatk.core.AttributeList();
        INumberScalar   minAtt=null, maxAtt=null; 
        try
        {
        if ((vModel.getMinxAttName() != null) && (vModel.getMaxxAttName() != null))
        {
            attFullName = vModel.getDevice().getName()+"/"+ vModel.getMinxAttName();                          
            ie = attl.add(attFullName);
            if (ie != null)
            if (ie instanceof INumberScalar)
            minAtt = (INumberScalar) ie;

            attFullName = vModel.getDevice().getName()+"/"+ vModel.getMaxxAttName();                          
            ie = attl.add(attFullName);
            if (ie != null)
            if (ie instanceof INumberScalar)
            maxAtt = (INumberScalar) ie;                          
        }
        if ((minAtt != null) && (maxAtt != null))
            viewer.setXaxisModels(minAtt, maxAtt); 
        }
        catch (Exception ex)
        {
            
        }
    }
}

  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

  public static void main(String[] args) {

    try {
      fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();

      NumberSpectrumViewer nsv = new NumberSpectrumViewer();
      nsv.setManageXaxis(true);
      //INumberSpectrum      ins = (INumberSpectrum) attributeList.add("sr/d-mfdbk/horizontal/FFT");
      //INumberSpectrum      ins = (INumberSpectrum) attributeList.add("//orion:10000/sr/d-tm/ts2/Spectrum");
      INumberSpectrum      ins = (INumberSpectrum) attributeList.add("//kidiboo:10000/fp/test/1/double_spectrum");
      nsv.setModel(ins);
      addXaxisMinMaxAtt(nsv, ins);
      //nsv.setModel((INumberSpectrum) attributeList.add("jlp/test/1/att_spectrum"));
      //nsv.setModel((INumberSpectrum) attributeList.add("sr/d-tm/agilent4395/spectrum"));
      nsv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
      nsv.setFont(new java.awt.Font("Dialog", 0, 12));
      //nsv.setXAxisAffineTransform(0.0,1000);
      nsv.setXAxisUnit("mA");
      JFrame f = new JFrame();
      attributeList.startRefresher();
      f.setContentPane(nsv);
      f.setSize(640,480);
      f.setVisible( true );
    } catch (Exception e) {
      e.printStackTrace();
    }

  } // end of main ()


}

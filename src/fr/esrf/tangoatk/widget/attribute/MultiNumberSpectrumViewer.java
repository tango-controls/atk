// File:          MultiNumberSpectrumViewer.java
// Created:       2007-05-14 14:41:03, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import java.util.*;
import java.awt.*;

import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;
import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoDs.AttrManip;
import com.braju.format.Format;

/**
 * A class to display several spectrum attributes in the same chart. The MultipleNumberSpectrumViewer
 * displays the spectrums according to index value (x axis shows value index).
 *
 */
public class MultiNumberSpectrumViewer extends JLChart 
                                       implements ISpectrumListener, 
				                  IJLChartListener
{

   public static final String         AXIS_X="X";
   public static final String         AXIS_Y1="Y1";
   public static final String         AXIS_Y2="Y2";
   
   //Default Color
   static final Color[] defColors = { Color.red,
				      Color.blue,
				      Color.magenta,
				      Color.cyan,
				      new Color(50,120,0), //forestGreen
				      Color.black,
				      new Color(255,120,0), //orange
				      Color.pink,
				      Color.green,
				      Color.yellow};

   private Map       attMap = null;
   private double    affineA0=0.0;
   private double    affineA1=1.0;
   
   private String    defaultAxis=null;


   // ---------------------------------------------------
   // Contruction
   // ---------------------------------------------------
   public MultiNumberSpectrumViewer()
   {       
       // Create the graph
       super();
       
       attMap = new HashMap();
       defaultAxis = new String(AXIS_Y1);

       setBorder(new javax.swing.border.EtchedBorder());
       setBackground(Color.white);
       getY1Axis().setAutoScale(true);
       getY2Axis().setAutoScale(true);
       getXAxis().setAutoScale(true);
       getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
       
       //addUserAction("Attribute properties");
       //addUserAction( DISPLAY_UNIT_STRING );
       //addJLChartActionListener(this);
       setJLChartListener(this);
       
   }

  /**
   * Adds a numberSpectrum attribute model to the viewer; This method adds the representing
   * DataView to the default axis determined by the defaultAxis bean property which can only be set to AXIS_Y1 or AXIS_Y2
   * @param ins : INumberSpectrum the number spectrum attribute
   **/

   public void addNumberSpectrumModel( INumberSpectrum ins)
   {
       String       attFormat = null;
       JLDataView   attDvy = null;
       int          orderNumber = 0;
       
       if (ins == null) return;
       
       if (attMap.containsKey(ins)) return;
       
       orderNumber = attMap.size();
       
       attFormat = ins.getFormat();
       attDvy = new JLDataView();
       attDvy.setUserFormat(attFormat);
       attDvy.setUnit(ins.getUnit());
       attDvy.setName(ins.getName());
       attDvy.setColor(defColors[orderNumber % defColors.length]);
       if (defaultAxis.equalsIgnoreCase(AXIS_Y1))
          getY1Axis().addDataView(attDvy);
       else
          getY2Axis().addDataView(attDvy);
       
       attMap.put(ins, attDvy);
       ins.addSpectrumListener(this);
       ins.refresh();
   }



  /**
   * Adds a numberSpectrum attribute model to the viewer; This method allows to add the representing
   * DataView to the specified axis. 
   * @param ins : INumberSpectrum the number spectrum attribute
   * @param axis : String if AXIS_Y1 the spectrum plot will be added to Y1 axis,  if AXIS_Y2 it will be added to Y2 axis,
   *  if AXIS_X the spectrum plot will be added to X axis.
   **/

   public void addNumberSpectrumModel( INumberSpectrum ins, String axis)
   {
       String       attFormat = null;
       JLDataView   attDvy = null;
       int          orderNumber = 0;

       if (defaultAxis.equalsIgnoreCase(axis))
       {
          addNumberSpectrumModel(ins);
	  return;
       }   
             
       if (axis.equalsIgnoreCase(AXIS_X)) // add to X axis
       {
          addNumberSpectrumModelToX(ins);
	  return;
       }
       
       // Add to Y1 or Y2 axis
       if (ins == null) return;
       
       if (attMap.containsKey(ins)) return;
       
       orderNumber = attMap.size();
              
       attFormat = ins.getFormat();
       attDvy = new JLDataView();
       attDvy.setUserFormat(attFormat);
       attDvy.setUnit(ins.getUnit());
       attDvy.setName(ins.getName());
       attDvy.setColor(defColors[orderNumber % defColors.length]);
       if (axis.equalsIgnoreCase(AXIS_Y1))
          getY1Axis().addDataView(attDvy);
       else
          getY2Axis().addDataView(attDvy);
       
       attMap.put(ins, attDvy);
       ins.addSpectrumListener(this);
       ins.refresh();
   }
   
   private void addNumberSpectrumModelToX(INumberSpectrum ins)
   {
       String       attFormat = null;
       JLDataView   attDvx = null;
       int          orderNumber = 0;

       if (ins == null) return;
       
       if (attMap.containsKey(ins)) return;
       
       if (getXAxis().getViewNumber() > 0)
       {
           JLDataView dvx = getXAxis().getDataView(0);
	   if (attMap.containsValue(dvx))
	   {
	       Set attSet = attMap.keySet();
	       if (attSet != null)
	       {
		   Iterator attIt=attSet.iterator();
		   while ( attIt.hasNext() )
		   {
		       INumberSpectrum currIns = (INumberSpectrum) attIt.next();
		       JLDataView attDv = (JLDataView) attMap.get(currIns);
		       if (attDv == dvx)
		       {
		          currIns.removeSpectrumListener(this);
			  attMap.remove(currIns);
			  break;
		       }
		   }
	       }
	   }
	   getXAxis().clearDataView();
       }
       
       //orderNumber = attMap.size();
              
       attFormat = ins.getFormat();
       attDvx = new JLDataView();
       attDvx.setUserFormat(attFormat);
       attDvx.setUnit(ins.getUnit());
       attDvx.setName(ins.getName());
       //attDvx.setColor(defColors[orderNumber % defColors.length]);
       getXAxis().addDataView(attDvx);
       
       attMap.put(ins, attDvx);
       ins.addSpectrumListener(this);
       ins.refresh();
   }



  /**
   * Removes a numberSpectrum attribute model from the viewer
   * @param ins : INumberSpectrum the number spectrum attribute to remove
   **/
      
   public void removeNumberSpectrumModel( INumberSpectrum ins)
   {
       String       attFormat = null;
       JLDataView   attDvy = null;
       boolean      contained = false;
       
       if (ins == null) return;
       
       if (attMap.containsKey(ins) == false) return;
       
       attDvy = (JLDataView) attMap.get(ins);
       ins.removeSpectrumListener(this);
       attMap.remove(ins);
       
       if (attDvy == null) return;
       
       JLAxis  dvAxis = attDvy.getAxis();
       
       if (dvAxis == getY1Axis())
          getY1Axis().removeDataView(attDvy);
       else
          if (dvAxis == getY2Axis())
	     getY2Axis().removeDataView(attDvy);
	  else
	     getXAxis().removeDataView(attDvy);       
   }


   public void clearModel()
   {
       Set               attSet = null;
       JLDataView        attDvy = null;
       INumberSpectrum   ins=null;
       boolean           contained = false;
       
       if (attMap == null) return;
       
       attSet = attMap.keySet();
       if (attSet == null) return;
       
       Iterator attIt=attSet.iterator();
       while ( attIt.hasNext() )
       {
	   ins = (INumberSpectrum) attIt.next();
	   attDvy = (JLDataView) attMap.get(ins);
	   ins.removeSpectrumListener(this);
	   if (attDvy != null)
	   {
	      contained = getY1Axis().checkRemoveDataView(attDvy);
	      if (contained == false)
	         getY2Axis().removeDataView(attDvy);
	   }
       }
       
       attMap = new HashMap();
   }


   // ---------------------------------------------------
   // Bean Property stuff
   // ---------------------------------------------------

   /**
    * Gets the default Axis name.
    * @return the default axis name : AXIS_Y1 or AXIS_Y2
    */
    public String getDefaultAxis()
    {
       return defaultAxis;
    }

   /**
    * Sets the default Axis name.
    * @param axisName : one of the AXIS_Y1 or AXIS_Y2; The default axis cannot be set to AXIS_X
    */
    public void setDefaultAxis(String axisName)
    {
       if (axisName.equalsIgnoreCase(AXIS_Y1))
          defaultAxis=AXIS_Y1;
       if (axisName.equalsIgnoreCase(AXIS_Y2))
          defaultAxis=AXIS_Y2;
    }
  

   /**
   * Sets an affine tranform to the X axis. This allows to transform
   * spectra index displayed on X axis.
   * @param a0
   * @param a1
   */
   public void setXAxisAffineTransform(double a0,double a1)
   {
       affineA0 = a0;
       affineA1 = a1;
   }

   /**
   * Return configuration.
   * @return current chart configuration as string
   */
   public String getSettings()
   {
      String to_write = "";

      // General settings
      to_write += getConfiguration();

      // Local settings
      to_write += "xaxis_transform:" + affineA0 + "," + affineA1 + "\n";

      // x, y1 and y2 Axis
      to_write += getXAxis().getConfiguration("x");
      to_write += getY1Axis().getConfiguration("y1");
      to_write += getY2Axis().getConfiguration("y2");

      return to_write;
   }

   // -------------------------------------------------------------
   //  JLChart listener interface
   // -------------------------------------------------------------

   public String[] clickOnChart(JLChartEvent e)
   {
       JLDataView  attDv= e.getDataView();
       
       if (attDv == null) return null;
       if (attMap.containsValue(attDv) == false) return null;
       
       String               nameStr=null, xStr=null, yStr=null;
       nameStr = attDv.getExtendedName() + " " + attDv.getAxis().getAxeName();
       xStr = "X=" + e.getTransformedXValue();
       yStr = "Y=" + attDv.formatValue(e.getTransformedYValue()) + " " + attDv.getUnit();
       
       String[] str = new String[3];
       str[0] = nameStr;
       str[1] = xStr;
       str[2] = yStr;
       
       return str;
   }
   

   // -------------------------------------------------------------
   //  Spectrum listener interface
   // -------------------------------------------------------------

   public void errorChange(ErrorEvent errorEvent)
   {
       INumberSpectrum   ins=null;
       JLDataView        attDvy = null;
       
       ins = (INumberSpectrum) errorEvent.getSource();
       if (ins == null) return;
       if (attMap == null) return;
       if (attMap.containsKey(ins) == false) return;
       
       attDvy = (JLDataView) attMap.get(ins);
       if (attDvy == null) return;
       // Clear the dataview
       attDvy.reset();
       repaint();
       //refreshTableSingle(attDvy); cannot call refreshTableSingle because there are multiple DataViews
   }


   public void stateChange(AttributeStateEvent evt)
   {
   }


   public void spectrumChange(NumberSpectrumEvent evt)
   {
       INumberSpectrum   ins=null;
       JLDataView        attDvy = null;
       double[]          value = null;
       double            affineTA0, affineTA1;
             
       ins = (INumberSpectrum) evt.getSource();
       if (ins == null) return;
       if (attMap == null) return;
       if (attMap.containsKey(ins) == false) return;
       
       attDvy = (JLDataView) attMap.get(ins);
       if (attDvy == null) return;
       
       // update the dataview
       value = evt.getValue();
       attDvy.reset();
       int length = value.length;

       if (attDvy.getAxis() == getXAxis())
       {
	   for (int i = 0; i < length; i++)
	       attDvy.add((double) i, value[i]);
       }
       else
       {
	   for (int i = 0; i < length; i++)
	       attDvy.add(affineA0 + affineA1 * (double) i, value[i]);
       }

       // Commit changes
       repaint();
       //refreshTableSingle(attDvy); cannot call refreshTableSingle because there are multiple DataViews
   }
   
   // ---------------------------------------------------
   // Main test fucntion
   // ---------------------------------------------------
   static public void main(String args[])
   {
        INumberSpectrum              ins;
	AttributeList                attl = new AttributeList();
	JFrame                       jf = new JFrame();
	MultiNumberSpectrumViewer    mnsv = new MultiNumberSpectrumViewer();
	
	//mnsv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	//mnsv.setFont(new java.awt.Font("Dialog", 0, 12));
	//mnsv.setXAxisAffineTransform(0.0,1000);
	try
	{
	   ins = (INumberSpectrum) attl.add("jlp/test/1/att_spectrum");
	   mnsv.addNumberSpectrumModel(ins);
	   ins = (INumberSpectrum) attl.add("jlp/test/1/Rwspectrum");
	   mnsv.addNumberSpectrumModel(ins);
	   ins = (INumberSpectrum) attl.add("jlp/test/2/att_spectrum");
	   mnsv.addNumberSpectrumModel(ins);
	   ins = (INumberSpectrum) attl.add("jlp/test/2/Rwspectrum");
	   mnsv.addNumberSpectrumModel(ins);
	}
	catch (Exception ex)
	{
	   System.out.println("Cannot connect to jlp/test/1");
           ex.printStackTrace();
	}

        attl.startRefresher();
	
	jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	jf.setContentPane(mnsv);
	jf.setSize(640,480);
	jf.pack();
	jf.setVisible(true);
   }
    

}

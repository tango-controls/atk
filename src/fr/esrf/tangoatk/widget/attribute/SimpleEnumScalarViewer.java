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
 
package fr.esrf.tangoatk.widget.attribute;

import java.awt.*;

import javax.swing.JFrame;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.*;

/** A light weigth viewer which display an enumerated scalar attribute (EnumScalar).
 * Here is an example of use:
 * <p>
 * <pre>
 * fr.esrf.tangoatk.core.AttributeList attributeList = new fr.esrf.tangoatk.core.AttributeList();
 * SimpleEnumScalarViewer  = new SimpleEnumScalarViewer();
 * IEnumScalar model = (IEnumScalar) attributeList.add("jlp/test/1/Att_six");
 * senv.setModel(model);
 * attributeList.startRefresher();
 * </pre>
 */

public class SimpleEnumScalarViewer extends JAutoScrolledText
                                    implements IEnumScalarListener, IErrorListener
{

   private IEnumScalar    model = null;
   private String         invalidText = "-----";
   private Color          backgroundColor;
   private boolean        alarmEnabled = true;
   private boolean        hasToolTip=false;
   private boolean        qualityInTooltip=false;


   /**
    * Contructs a SimpleEnumScalar viewer.
    */
   public SimpleEnumScalarViewer()
   {
      backgroundColor = ATKConstant.getColor4Quality(IAttribute.VALID);
      setOpaque(true);
   }

   /**
   * Set the text which will be displayed in case of error or INVALID quality.
   * @param s Text to be displayed.
   */
   public void setInvalidText(String s)
   {
      invalidText = s;
   }

   /**
   * Returns the current text which is displayed in case of error.
   * @see #setInvalidText
   */
   public String getInvalidText()
   {
      return invalidText;
   }
  

   /** Returns the current background color of this viewer. Color used for the VALID attribute quality state */
   public Color getBackgroundColor()
   {
      return backgroundColor;
   }

   /**
    * Sets the 'VALID' background color of this viewer.
    * Color used for the VALID attribute quality state.
    * @param bg Background color.
    * @see #setAlarmEnabled
    */
   public void setBackgroundColor(Color bg)
   {
      backgroundColor = bg;
   }

  /**
    * Enables or disables alarm background (represents the attribute quality factor).
    * @param b True to enable alarm.
    * @see #setBackgroundColor
    */
   public void setAlarmEnabled(boolean b)
   {
      alarmEnabled = b;
   }

   /**
    * Determines whether the background color is overrided by the quality factor.
    * @see #setAlarmEnabled
    * @see #setBackgroundColor
    */
   public boolean isAlarmEnabled()
   {
      return alarmEnabled;
   }



   public IEnumScalar getModel()
   {
       return model;  
   }

   /**
   * Sets the model for this viewer.
   * @param IEnumScalar model
   */
   public void setModel(IEnumScalar enumeration)
   {
      clearModel();

      if (enumeration != null)
      {
	 model = enumeration;
	 model.addEnumScalarListener(this);
         if (hasToolTip)
    	    setToolTipText(model.getName());
         model.refresh();
      }
   }
   
   /**
   * Clears all model and listener attached to the component
   */
   public void clearModel()
   {
      if (hasToolTip) setToolTipText(null);
      
      if (model != null)
      {
	 model.removeEnumScalarListener(this);
	 model = null;
      }
   }
  
  


   // -------------------------------------------------------------
   // EnumScalar listener
   // -------------------------------------------------------------
   public void enumScalarChange(EnumScalarEvent evt)
   {
      String val = evt.getValue();

      if (val == null)
      {
	 setText(invalidText);
	 return;
      }

      String oldVal=getText();
      if(!val.equals(oldVal)) setText(val);
   }


   public void stateChange(AttributeStateEvent evt)
   {
      String state = evt.getState();
      
      if (hasToolTip)
      {
         if (qualityInTooltip)
         { //set a ToolTip attributename + quality
	    IAttribute attSource = (IAttribute)evt.getSource();
	    setToolTipText(attSource.getName() + " : " + state);
         }
      }

      if (state.equals(IAttribute.INVALID))
	 setText(invalidText);

      if (!alarmEnabled) return;

      if (state.equals(IAttribute.VALID))
      {
	 setBackground(backgroundColor);
	 return;
      }
      setBackground(ATKConstant.getColor4Quality(state));
   }
   

   public void errorChange(ErrorEvent evt)
   {
      setText(invalidText);
      if (!alarmEnabled) return;
      setBackground(ATKConstant.getColor4Quality(IAttribute.UNKNOWN));
   }
   
   
   
   /**
    * <code>getHasToolTip</code> returns true if the viewer has a tooltip (attribute full name)
    *
    * @return a <code>boolean</code> value
    */
   public boolean getHasToolTip()
   {
      return hasToolTip;
   }

   /**
    * <code>setHasToolTip</code> display or not a tooltip for this viewer
    *
    * @param b If True the attribute full name will be displayed as tooltip for the viewer
    */
   public void setHasToolTip(boolean b)
   {
      if (hasToolTip == b) return;
    
      hasToolTip = b;
      setToolTipText(null);
      
      if ((hasToolTip) && (model != null))
         setToolTipText(model.getName());
   }
  
  
   /**
    * <code>getQualityInTooltip</code> returns true if the attribute quality factor is displayed inside the viewer's tooltip
    *
    * @return a <code>boolean</code> value
    */
   public boolean getQualityInTooltip()
   {
      return qualityInTooltip;
   }

   /**
    * <code>setQualityInTooltip</code> display or not the attribute quality factor inside the tooltip
    *
    * @param b If True the attribute quality factor will be displayed inside the tooltip.
    */
   public void setQualityInTooltip(boolean b)
   {    
      if (qualityInTooltip == b) return;
      qualityInTooltip = b;
      if (!hasToolTip) return;
       
      if (!qualityInTooltip)
         if (model != null)
              setToolTipText(model.getName());
   }
  
  
  

  /**
   * Test function
   * @param args Not used
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {

    AttributeList              attributeList = new AttributeList();
    SimpleEnumScalarViewer     sesv = new SimpleEnumScalarViewer();
    IEnumScalar                enumeration;
    
    enumeration = (IEnumScalar) attributeList.add("jlp/test/1/Att_six");
    sesv.setHasToolTip(true);
    sesv.setModel(enumeration);
    sesv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    sesv.setBackgroundColor(java.awt.Color.WHITE);
    sesv.setForeground(java.awt.Color.BLACK);
    sesv.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
    sesv.setAutoScroll(30);
    //sesv.setScrollingMode(SCROLL_LOOP);
    JFrame f = new JFrame();
    f.setContentPane(sesv);
    f.pack();
    f.setVisible(true);
    attributeList.startRefresher();

  } // end of main ()

}

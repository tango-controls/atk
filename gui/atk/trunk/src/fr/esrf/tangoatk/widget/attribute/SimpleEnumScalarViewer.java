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

import javax.swing.*;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;

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
                                    implements IEnumScalarListener, IErrorListener, JDrawable
{

   private IEnumScalar    model = null;
   private String         invalidText = "-----";
   private Color          backgroundColor;
   private boolean        alarmEnabled = true;
   private boolean        hasToolTip=false;
   private boolean        qualityInTooltip=false;

   static String[] exts = {"alarmEnabled","validBackground"};

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
  
  
  // ------------------------------------------------------
  // Implementation of JDrawable interface
  // ------------------------------------------------------
  public void initForEditing() {
  }

  public JComponent getComponent() {
    return this;
  }

  public String getDescription(String name) {
    
    if (name.equalsIgnoreCase("alarmEnabled")) {
      return "When enabled, the background color change with the\n"+
             "Tango attribute quality factor.\n" +
             "Default colors are: ( unless they have been changed with\n"+
             "ATKConstant.setColor4Quality() )\n" +
             " VALID   => Green\n" +
             " INVALID => Grey\n" +
             " ALARM   => Orange\n" +
             " WARNING => Orange\n" +
             " CHANGING => Blue\n" +
             " UNKNOWN => Grey\n" +
             "Possible values are: true, false.";
    } else if (name.equalsIgnoreCase("validBackground")) {
      return "Sets the background color (r,g,b) for the VALID quality factor for this viewer.\n" +
             "Has effect only if alarmEnabled is true.";
    }

    return "";
  }

  public String[] getExtensionList() {
    return exts;
  }

  public boolean setExtendedParam(String name,String value,boolean popupErr) {

    if (name.equalsIgnoreCase("alarmEnabled")) {

      if(value.equalsIgnoreCase("true")) {
        setAlarmEnabled(true);
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        setAlarmEnabled(false);
        return true;
      } else {
        showJdrawError(popupErr,"alarmEnabled","Wrong syntax: 'true' or 'false' expected.");
        return false;
      }

    } else if (name.equalsIgnoreCase("validBackground")) {

      String[] c = value.split(",");
      if (c.length != 3) {
        showJdrawError(popupErr,"validBackground","Integer list expected: r,g,b");
        return false;
      }

      try {
        int r = Integer.parseInt(c[0]);
        int g = Integer.parseInt(c[1]);
        int b = Integer.parseInt(c[2]);
        if(r<0 || r>255 || g<0 || g>255 || b<0 || b>255) {
          showJdrawError(popupErr,"validBackground", "Parameter out of bounds. [0..255]");
          return false;
        }
        setBackgroundColor(new Color(r, g, b));
        return true;

      } catch (NumberFormatException e) {
        showJdrawError(popupErr,"validBackground", "Wrong integer syntax.");
        return false;
      }

    }

    return false;

  }

  public String getExtendedParam(String name) {

    if(name.equals("alarmEnabled")) {
      return (isAlarmEnabled())?"true":"false";
    } else if(name.equalsIgnoreCase("validBackground")) {
      Color c = backgroundColor;
      return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    }
    return "";

  }

  private void showJdrawError(boolean popup,String paramName,String message) {
    if(popup)
      JOptionPane.showMessageDialog(null, "SimpleScalarViewer: "+paramName+" incorrect.\n" + message,
                                    "Error",JOptionPane.ERROR_MESSAGE);
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

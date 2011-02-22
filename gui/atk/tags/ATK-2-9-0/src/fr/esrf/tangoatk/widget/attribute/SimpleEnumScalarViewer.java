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
         model.refresh();
      }
   }
   
   /**
   * Clears all model and listener attached to the component
   */
   public void clearModel()
   {
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

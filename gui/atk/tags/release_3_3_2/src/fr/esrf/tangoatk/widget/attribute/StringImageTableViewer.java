// File:          StringImageTableViewer.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;
import javax.swing.table.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;


/** A StringImageTableViewer is a Swing JTable provided for the StringImage attributes.
 * The elements of the string image attribute will be displayed in a two dimensional
 * matrix, displayed by a Swing JTable.
 * The attribute used as the model for this viewer should implement the IStringImage
 * interface. The viewer is updated when the string image attribute value changes.
 *
 */
public class StringImageTableViewer extends JTable implements IStringImageListener
{


  private IStringImage          attModel=null;
  private DefaultTableModel     tabModel=null;
  private String[]              columnIdents=null;
  private boolean               qualityEnabled = false;
  private Color                 background;
  

  // ---------------------------------------------------
  // Contruction
  // ---------------------------------------------------
  public StringImageTableViewer()
  {
      background = getBackground();
      tabModel = new DefaultTableModel();
      super.setModel(tabModel);
  }

  // ---------------------------------------------------
  // Property stuff
  // ---------------------------------------------------

  public IStringImage getAttModel()
  {
     return attModel;
  }


  public void setAttModel( IStringImage siModel)
  {
      if (attModel != null)
      {
	  attModel.removeStringImageListener(this);
	  attModel = null;
      }

      if (siModel != null)
      {
	  attModel = siModel;
	  attModel.addStringImageListener(this);
	  attModel.refresh();
	  setStringImageValue(attModel.getValue());
      }
      else
          setStringImageValue(null);
  }

  /**
   *<code>getQualityEnabled</code> returns a boolean to know whether quality will be displayed as background
   * or not.
   * 
   * @return a <code>boolean</code> value
   */
  public boolean getQualityEnabled ()
  {
      return qualityEnabled;
  }

  /**
   * <code>setQualityEnabled</code> view or not the attribute quality for this viewer
   *
   * @param b If True the attribute full name will be displayed as tooltip for the viewer
   * @param qualityEnabled If True the background Color represents the attribute quality factor
   */
  public void setQualityEnabled (boolean b)
  {
     qualityEnabled = b;
     if (!qualityEnabled)
     {
	super.setBackground(background);
	repaint();
     }
  }

  /**
   *<code>getColumnIdents</code> returns a String Array corresponding to the column identifiers
   * 
   * @return a <code>String[]</code> value
   */
  public String[] getColumnIdents ()
  {
      return columnIdents;
  }

  /**
   * <code>setColumnIdents</code> sets the table's column identifiers
   *
   * @param columnIdents
   */
  public void setColumnIdents (String[] colIds)
  {
     columnIdents = colIds;
     tabModel.setColumnIdentifiers(colIds);
  }



  public void clearModel()
  {
      setAttModel( (IStringImage) null);
  }



  public void setBackground (Color bg)
  {
    background = bg;
    super.setBackground( bg );
  }

  // ---------------------------------------------------
  // IStringImageListener listener
  // ---------------------------------------------------
  public void stringImageChange(StringImageEvent e)
  {
      setStringImageValue(e.getValue());
  }

  public void stateChange(AttributeStateEvent evt)
  {
      String state = evt.getState();
      if (!qualityEnabled) return;
      super.setBackground(ATKConstant.getColor4Quality(state));
      repaint();
  }

  public void errorChange(ErrorEvent evt)
  {
  }

  protected void setStringImageValue (String[][] val)
  {
      if (val == null)
      {
	 tabModel = new DefaultTableModel();
	 super.setModel(tabModel);
	 return;
      }
      tabModel.setDataVector(val, columnIdents);
  }

  // ---------------------------------------------------
  // Main test fucntion
  // ---------------------------------------------------
  static public void main(String args[])
  {
       IEntity                   ie;
       IStringImage              ismAtt;
       AttributeList             attl = new AttributeList();
       JFrame                    f = new JFrame();
       StringImageTableViewer    sitv = new StringImageTableViewer();

       try
       {
	  ie = attl.add("tests/machine/status/operatorMessageHistory");
	  if (!(ie instanceof IStringImage))
	  {
              System.out.println("tests/machine/status/operatorMessageHistory is not a IStringImage");
	      System.exit(0);
	  }

          ismAtt = (IStringImage) ie;
	  sitv.setAttModel(ismAtt);
       }
       catch (Exception ex)
       {
          System.out.println("Cannot connect to tests/machine/status");
       }


       f.setContentPane(sitv);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.pack();
       f.setVisible(true);
  }

}

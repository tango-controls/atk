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
 
// File:          StringImageTableViewer.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;
import java.awt.Color;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.JTableRow;


/** A StringImageTableViewer is a Swing JTable provided for the StringImage attributes.
 * The elements of the string image attribute will be displayed in a two dimensional
 * matrix, displayed by a Swing JTable.
 * The attribute used as the model for this viewer should implement the IStringImage
 * interface. The viewer is updated when the string image attribute value changes.
 *
 */
public class StringImageTableViewer extends JTableRow implements IStringImageListener
{


  private IStringImage          attModel=null;
  private boolean               qualityEnabled = false;
  private Color                 background;
  

  // ---------------------------------------------------
  // Contruction
  // ---------------------------------------------------
  public StringImageTableViewer()
  {
      background = getBackground();
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
      clearModel();

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

  public void clearModel()
  {
      if (attModel != null)
      {
	  attModel.removeStringImageListener(this);
	  attModel = null;
          clearData();
      }
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
	 clearData();
	 return;
      }
      
      if (val.length == 0)
      {
	 clearData();
	 return;
      }
      
      int nbCol = val[0].length;
      if (nbCol == 0)
      {
	 clearData();
	 return;
      }
      super.setData(val, 0, 0);      
  }

  // ---------------------------------------------------
  // Main test fucntion
  // ---------------------------------------------------
  static public void main(String args[])
  {
       IEntity                   ie;
       IStringImage              ismAtt=null;
       AttributeList             attl = new AttributeList();
       JFrame                    f = new JFrame();
       StringImageTableViewer    sitv = new StringImageTableViewer();

       try
       {
          ie = attl.add("fp/test/1/string_image_ro");
	  if (!(ie instanceof IStringImage))
	  {
              System.out.println("fp/test/1/string_image_ro is not a IStringImage");
	      System.exit(-1);
	  }

          ismAtt = (IStringImage) ie;
       }
       catch (Exception ex)
       {
          System.out.println("Cannot connect to fp/test/1/string_image_ro");
          System.exit(-1);
       }
       
	
       f.setContentPane(sitv);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       sitv.setAttModel(ismAtt);
       attl.startRefresher();
       f.pack();
       f.setVisible(true);
  }

}

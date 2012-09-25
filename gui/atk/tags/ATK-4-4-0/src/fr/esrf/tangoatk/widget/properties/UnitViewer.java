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
 
package fr.esrf.tangoatk.widget.properties;

import java.beans.*;
import javax.swing.*;
import java.util.*;
import java.awt.GridBagConstraints;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.JSmoothLabel;

public class UnitViewer extends JSmoothLabel implements PropertyChangeListener { 

  private IAttribute model=null;

  public UnitViewer() {
  }

  public void setModel(IAttribute a)
  {
      if (model != null)
	  model.getProperty("unit").removePresentationListener(this);

      model = a;
      
      if (model != null)
      {
	 model.getProperty("unit").addPresentationListener(this);
	 setText(a.getUnit());
      }
  }
  
  public IAttribute getModel()
  {
     return model;
  }

  public void propertyChange(PropertyChangeEvent evt) {

    Property src = (Property) evt.getSource();
    if (model != null) {
      if (src.getName().equalsIgnoreCase("unit")) {
        setText(src.getValue().toString());
      }
    }
  }

}

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

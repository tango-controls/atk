package fr.esrf.tangoatk.widget.properties;

import java.beans.*;
import javax.swing.*;
import javax.swing.text.Caret;
import java.util.*;
import java.awt.GridBagConstraints;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.JSmoothLabel;

public class LabelViewer extends JSmoothLabel implements PropertyChangeListener { 

  private IAttribute model=null;

  public LabelViewer() {
  }

  public void setModel(IAttribute a)
  {
      if (model != null)
	  model.getProperty("label").removePresentationListener(this);

      model = a;
      
      if (model != null)
      {
	 model.getProperty("label").addPresentationListener(this);
	 setText(a.getLabel());
      }
  }
  
  public IAttribute getModel()
  {
     return model;
  }

  public void propertyChange(PropertyChangeEvent evt) {

    Property src = (Property) evt.getSource();
    if (model != null) {
      if (src.getName().equalsIgnoreCase("label")) {
        setText(src.getValue().toString());
      }
    }
  }

}

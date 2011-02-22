package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.widget.util.WheelSwitch;
import fr.esrf.tangoatk.widget.util.ErrorPane;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.core.*;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A class to display a scalar aligned verticaly to the NumberScalarWheelEditor
 */
public class NumberScalarViewer extends WheelSwitch implements INumberScalarListener, PropertyChangeListener {

  private INumberScalar model = null;

  public NumberScalarViewer() {
    super(false);
  }

  public INumberScalar getModel() {
    return model;
  }

  public void setModel(INumberScalar m) {

    // Remove old registered listener
    if (model != null) {
      model.removeNumberScalarListener(this);
      model.getProperty("format").removePresentationListener(this);
      model = null;
    }

    if( m==null ) return;

    model = m;

    // Register new listener
    model.addNumberScalarListener(this);
    model.getProperty("format").addPresentationListener(this);

    setFormat(model.getProperty("format").getPresentation());
    model.refresh();
    double d = model.getNumberScalarValue();
    setValue(d);

  }

  public void numberScalarChange(NumberScalarEvent evt)
  {
      double val = evt.getValue();
      if (getValue() != val) setValue(val);
  }

  public void errorChange(ErrorEvent e) {
    setValue(Double.NaN);
  }

  public void stateChange(AttributeStateEvent e) {
  }

  public void propertyChange(PropertyChangeEvent evt) {

    Property src = (Property) evt.getSource();
    if (model != null) {
      if (src.getName().equalsIgnoreCase("format")) {
        setFormat(src.getValue().toString());
        model.refresh();
      }
    }

  }

  public static void main(String[] args) {

    INumberScalar attr=null;

    fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();

    try {
      attr = (INumberScalar) attributeList.add("jlp/test/1/att_trois");
    } catch(Exception e) {
      ErrorPane.showErrorMessage(null,"jlp/test/1",e);
      System.exit(0);
    }
    attributeList.startRefresher();

    JFrame f = new JFrame();
    JPanel innerPanel = new JPanel(new GridLayout(2,1));
    NumberScalarWheelEditor we = new NumberScalarWheelEditor();
    NumberScalarViewer      wv = new NumberScalarViewer();
    //javax.swing.border.Border   loweredBevel = BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED);
    //wv.setBorder(loweredBevel);

    we.setModel(attr);
    wv.setModel(attr);
    innerPanel.add(we);
    innerPanel.add(wv);
    f.setContentPane(innerPanel);
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);

  }

}

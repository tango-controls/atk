/*
 * TrendSelectionNode.java
 *
 * Created on May 13, 2002, 4:28 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.chart.*;

import java.awt.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.tree.*;

/*
 * @author  pons
 */

class TrendSelectionNode extends DefaultMutableTreeNode implements INumberScalarListener,PropertyChangeListener {

  // Local declaration
  private String devname = "";
  private INumberScalar model;
  private int selected;
  private JLDataView data;
  private long lastErrorTime = 0;

  // Global
  static java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
  static java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss");
  Trend chart;


  // *****************************************************************************************************************
  // Contrcut the root Node
  public TrendSelectionNode(Trend g) {
    // Root node
    this.devname = "Trend";
    this.model = null;
    data = null;
    chart = g;
  }

  // Contruct a device node
  public TrendSelectionNode(Trend g, String name) {
    // Root node
    this.devname = name;
    data = null;
    this.model = null;
    chart = g;
  }

  // Contruct an attribute node (model cannot be null !!)
  public TrendSelectionNode(Trend g, String name, INumberScalar model, int selection, Color c) {
    // Root node
    this.devname = name;
    this.model = model;
    this.selected = selection;
    chart = g;

    data = new JLDataView();
    data.setColor(c);
    data.setMarkerColor(c);

    // Retrieve attribute history
    INumberScalarHistory[] history = model.getNumberScalarHistory();
    if (history != null) {
      //System.out.println(name + ":Reading "+history.length+ " values");
      for (int i = 0; i < history.length; i++)
        data.add(history[i].getTimestamp(), history[i].getValue());
      chart.getChart().garbageData(data);
    }

    // Register attribute
    model.addNumberScalarListener(this);

    // Register on property change
    model.getProperty("label").addPresentationListener(this);
    model.getProperty("unit").addPresentationListener(this);

  }

  // Refresh node after a property change
  public void refreshNode() {

    data.setUnit(model.getUnit());

    // Set the chart label
    if(model.getLabel().length()>0 && !model.getLabel().equalsIgnoreCase("not specified")) {
      if( chart.displayDeviceNames() )
        data.setName(devname + "/" + model.getLabel());
      else
        data.setName(model.getLabel());
    } else {
      data.setName(model.getName());
    }

  }

  public void propertyChange(PropertyChangeEvent evt) {

    if (model != null) {
      refreshNode();
      chart.refreshNode(this);
    }

  }

  // *****************************************************************************************************************
  // Selection stuff
  public int getSelected() {
    return selected;
  }

  public void setSelected(int s) {

    switch (s) {

      case Trend.SEL_NONE:
        switch (selected) {
          case Trend.SEL_X:
            chart.getChart().getXAxis().removeDataView(data);
            break;
          case Trend.SEL_Y1:
            chart.getChart().getY1Axis().removeDataView(data);
            break;
          case Trend.SEL_Y2:
            chart.getChart().getY2Axis().removeDataView(data);
            break;
        }
        break;

      case Trend.SEL_X:
        switch (selected) {
          case Trend.SEL_Y1:
            chart.getChart().getY1Axis().removeDataView(data);
            break;
          case Trend.SEL_Y2:
            chart.getChart().getY2Axis().removeDataView(data);
            break;
        }
        chart.getChart().getXAxis().addDataView(data);
        break;

      case Trend.SEL_Y1:
        switch (selected) {
          case Trend.SEL_X:
            chart.getChart().getXAxis().removeDataView(data);
            break;
          case Trend.SEL_Y2:
            chart.getChart().getY2Axis().removeDataView(data);
            break;
        }
        chart.getChart().getY1Axis().addDataView(data);
        break;

      case Trend.SEL_Y2:
        switch (selected) {
          case Trend.SEL_X:
            chart.getChart().getXAxis().removeDataView(data);
            break;
          case Trend.SEL_Y1:
            chart.getChart().getY1Axis().removeDataView(data);
            break;
        }
        chart.getChart().getY2Axis().addDataView(data);
        break;

    }

    selected = s;

  }

  // *****************************************************************************************************************
  // Return true when tree node is a Leaf
  public boolean isLeaf() {
    return (model != null);
  }

  // *****************************************************************************************************************
  // Return the model
  public INumberScalar getModel() {
    return model;
  }

  // *****************************************************************************************************************
  // Return handle to dataView
  public JLDataView getData() {
    return data;
  }

  // ****************************************************************
  // Delete an item in the list
  public void delItem(INumberScalar model) {

    int i = 0,j = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    if (found) {

      TrendSelectionNode dev = (TrendSelectionNode) getChildAt(i);
      found = false;
      j = 0;

      // Look for the att name
      int nb_att = dev.getChildCount();
      while (j < nb_att && !found) {
        found = (attname.equals(dev.getChild(j).getModelName()));
        if (!found) j++;
      }

      if (found) {
        dev.getChild(j).setSelected(Trend.SEL_NONE);
        model.removeNumberScalarListener(dev.getChild(j));

        // remove the node
        dev.remove(j);

        if (nb_att == 1) {
          // Remove device node
          remove(i);
        }
      }
    }
  }

  // ********************************************************************
  // Add a new item in the Tree
  public TrendSelectionNode addItem(Trend g, INumberScalar model, Color c) {

    int i = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    TrendSelectionNode nn = new TrendSelectionNode(g, devname, model, Trend.SEL_NONE, c);

    if (found) {
      // add the attribute
      getChild(i).add(nn);
    } else {
      TrendSelectionNode n = new TrendSelectionNode(g, devname);
      add(n);
      n.add(nn);
    }

    return nn;
  }

  // *****************************************************************************************************************
  // Return all selectable items in a vector
  public Vector getSelectableItems() {
    Vector v;

    if (isLeaf()) {
      v = new Vector();
      v.add(this);
    } else {
      int i;
      int nb = getChildCount();

      v = new Vector();
      for (i = 0; i < nb; i++) {
        TrendSelectionNode child = getChild(i);
        v.addAll(child.getSelectableItems());
      }

    }

    return v;
  }

  // *****************************************************************************************************************
  // Return the child at the specified postion
  public void showOptions() {

    if (data != null) {
      chart.getChart().showDataOptionDialog(data);
    }

  }

  // *****************************************************************************************************************
  // Return the full attribute name
  public String getModelName() {
    if (model != null)
      return model.getName();
    else
      return "";
  }

  // *****************************************************************************************************************
  // Unregister the node
  public void clearModel() {
    if( model!=null ) {
      model.removeNumberScalarListener(this);
      model.getProperty("label").removePresentationListener(this);
      model.getProperty("unit").removePresentationListener(this);
    }
    data = null;
    model = null;
  }


  // *****************************************************************************************************************
  // Return the child at the specified postion
  public TrendSelectionNode getChild(int id) {
    return (TrendSelectionNode) (getChildAt(id));
  }

  // *****************************************************************************************************************
  // INumberScalarListener
  public void numberScalarChange(NumberScalarEvent evt) {

    if( model==null )
      return;

    // Add data to the dataView
    // Hack to avoid multiple identical value
    // (should be removed when ATK will move to Tango event API)
    boolean ok = true;
    DataList lv = data.getLastValue();

    double x = (double) evt.getTimeStamp();
    double y = evt.getValue();

    if (lv != null) ok = (lv.x != x) || (lv.y != y);

    if (ok) {
      data.add((double) evt.getTimeStamp(), evt.getValue());
      chart.getChart().garbageData(data);
    }

    //calendar.setTimeInMillis(evt.getTimeStamp());
    //java.util.Date date = calendar.getTime();
    //String tm = format.format(date);
    //System.out.println( model.getName() + ":" + evt.getValue() + " [" + tm + "]");
  }

  public void errorChange(ErrorEvent evt) {

    if (model == null)
      return;

    // Add data to the dataView
    if (lastErrorTime != evt.getTimeStamp()) {

      lastErrorTime = evt.getTimeStamp();
      data.add((double) lastErrorTime, Double.NaN);
      chart.getChart().garbageData(data);

      //calendar.setTimeInMillis(evt.getTimeStamp());
      //java.util.Date date = calendar.getTime();
      //String tm = format.format(date);
      //System.out.println( model.getName() + ": Error [" + tm + "]");

    }
  }

  public void stateChange(AttributeStateEvent evt) {
  }

  // *****************************************************************************************************************
  // Return string representation of the node
  public String toString() {

    if (model != null) {
      if(model.getLabel().length()>0 && !model.getLabel().equalsIgnoreCase("not specified"))
        return model.getLabel();
      else
        return model.getNameSansDevice();
    } else {
      return devname;
    }

  }

}

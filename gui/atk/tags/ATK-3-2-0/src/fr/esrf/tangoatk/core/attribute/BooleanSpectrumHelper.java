// File:          BooleanSpectrumHelper.java
// Created:       2005-02-03 10:45:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.DevFailed;

public class BooleanSpectrumHelper implements java.io.Serializable
{
  IAttribute attribute;
  EventSupport propChanges;

  public BooleanSpectrumHelper(IAttribute attribute)
  {
    init(attribute);
  }

  void init(IAttribute attribute)
  {
    setAttribute(attribute);
    propChanges = ((AAttribute) attribute).getPropChanges();
  }
  
  
  public void setAttribute(IAttribute attribute)
  {
    this.attribute = attribute;
  }

  public IAttribute getAttribute()
  {
    return attribute;
  }

  protected void setProperty(String name, Number value)
  {
    attribute.setProperty(name, value);
    attribute.storeConfig();
  }

  protected void setProperty(String name, Number value, boolean writable)
  {
    attribute.setProperty(name, value, writable);
  }


  void fireSpectrumValueChanged(boolean[] newValue, long timeStamp)
  {
    propChanges.fireBooleanSpectrumEvent((IBooleanSpectrum) attribute,
      newValue, timeStamp);
  }

  void insert(boolean[] boolSpect)
  {
      attribute.getAttribute().insert(boolSpect,
      ((IAttribute) attribute).getXDimension(),
      ((IAttribute) attribute).getYDimension());
  }

  boolean[] extract() throws DevFailed
  {
    return attribute.getAttribute().extractBooleanArray();
  }

  
  void addBooleanSpectrumListener(IBooleanSpectrumListener l)
  {
      propChanges.addBooleanSpectrumListener(l);
  }

  
  void removeBooleanSpectrumListener(IBooleanSpectrumListener l)
  {
      propChanges.removeBooleanSpectrumListener(l);
  }


  public String getVersion() {
    return "$Id$";
  }

}

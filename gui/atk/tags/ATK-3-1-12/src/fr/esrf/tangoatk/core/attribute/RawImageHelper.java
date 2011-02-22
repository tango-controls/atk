// File:          RawImageHelper.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <pons@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class RawImageHelper implements java.io.Serializable {

  IAttribute attribute;
  EventSupport propChanges;
  byte[][] retval = new byte[1][1];

  public RawImageHelper(IAttribute attribute) {
    init(attribute);
  }


  void init(IAttribute attribute) {
    setAttribute(attribute);
    propChanges = ((AAttribute) attribute).getPropChanges();
  }


  public void setAttribute(IAttribute attribute) {
    this.attribute = attribute;
  }

  public IAttribute getAttribute() {
    return attribute;
  }

  protected void setProperty(String name, Number value) {
    attribute.setProperty(name, value);
    attribute.storeConfig();
  }

  protected void setProperty(String name, Number value, boolean writable) {
    attribute.setProperty(name, value, writable);
  }


  void fireImageValueChanged(byte[][] newValue, long timeStamp) {
    propChanges.fireRawImageEvent((IRawImage) attribute,
            newValue, timeStamp);
  }

  byte[][] getRawImageValue(DeviceAttribute deviceAttribute) throws DevFailed {
    byte[] tmp;

    tmp = deviceAttribute.extractCharArray();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || xdim != retval[0].length) {
      retval = new byte[ydim][xdim];
    }

    int k = 0;
    for (int y = 0; y < ydim; y++) {
      System.arraycopy(tmp,k,retval[y],0,xdim);
      k+=xdim;
    }

    return retval;
  }

  void addRawImageListener(IRawImageListener l) {
    propChanges.addRawImageListener(l);
  }


  void removeRawImageListener(IRawImageListener l) {
    propChanges.removeRawImageListener(l);
  }


  public String getVersion() {
    return "$Id$";
  }

}

// File:          ANumberImageHelper.java
// Created:       2002-01-24 11:22:33, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-11 14:29:54, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.IImageListener;
import fr.esrf.tangoatk.core.INumberImage;

abstract class ANumberImageHelper extends NumberAttributeHelper {
  double[][] retval = new double[0][0];

  abstract String[][] getImageValue(DeviceAttribute attribute) throws DevFailed;
  
  abstract double[][] getNumberImageDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed;

  abstract double[][] getNumberImageValue(DeviceAttribute attribute) throws DevFailed;

  abstract void insert(double[][] d);
  
  void insert(double[] d)
  {
  }
  
  void fireImageValueChanged(double[][] newValue, long timeStamp) {
	propChanges.fireImageEvent((INumberImage) attribute, newValue, timeStamp);
  }
  
  void addImageListener(IImageListener l) {
    propChanges.addImageListener(l);
  }

  void removeImageListener(IImageListener l) {
    propChanges.removeImageListener(l);
  }  

  public String getVersion() {
    return "$Id$";
  }

}

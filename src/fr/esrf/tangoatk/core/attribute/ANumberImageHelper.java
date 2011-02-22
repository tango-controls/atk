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

abstract class ANumberImageHelper extends NumberAttributeHelper {
  double[][] retval = new double[0][0];

  abstract String[][] getImageValue(DeviceAttribute attribute) throws DevFailed;

  public String getVersion() {
    return "$Id$";
  }

}

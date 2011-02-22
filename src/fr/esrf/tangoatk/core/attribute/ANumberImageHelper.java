// File:          ANumberImageHelper.java
// Created:       2002-01-24 11:22:33, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 15:56:28, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.TangoApi.*;
abstract class ANumberImageHelper extends NumberAttributeHelper {

    abstract String[][] getImageValue(DeviceAttribute attribute);

    public String getVersion() {
	return "$Id$";
    }

}

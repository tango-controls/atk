// File:          DevStateScalarHelper.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;


import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;

class DevStateScalarHelper implements java.io.Serializable
{
    IAttribute attribute;
    EventSupport propChanges;

    public DevStateScalarHelper(IAttribute attribute)
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


    void fireValueChanged(String newValue, long timeStamp)
    {
      propChanges.fireDevStateScalarEvent((IDevStateScalar) attribute,
	newValue, timeStamp);
    }


    String extract() throws DevFailed
    {
      return Device.toString(attribute.getAttribute().extractState());
    }


    void addDevStateScalarListener(IDevStateScalarListener l)
    {
	propChanges.addDevStateScalarListener(l);
    }


    void removeDevStateScalarListener(IDevStateScalarListener l)
    {
	propChanges.removeDevStateScalarListener(l);
    }


    public String getVersion() {
	return "$Id$";
    }
}

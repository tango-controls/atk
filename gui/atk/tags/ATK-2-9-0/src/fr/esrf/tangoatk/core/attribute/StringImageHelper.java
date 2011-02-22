// File:          StringImageHelper.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class StringImageHelper implements java.io.Serializable
{
    IAttribute      attribute;
    EventSupport    propChanges;
    String[][]      retval = new String[1][1];

    public StringImageHelper(IAttribute attribute)
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


    void fireImageValueChanged(String[][] newValue, long timeStamp)
    {
        propChanges.fireStringImageEvent(  (IStringImage) attribute,
	                                   newValue, timeStamp);
    }

    void insert(String[] stringImg)
    {
	attribute.getAttribute().insert(stringImg,
	         ((IAttribute) attribute).getXDimension(),
	         ((IAttribute) attribute).getYDimension());
    }


    String[] flatten(String[][] src)
    {
       int  size = src.length * src[0].length;
       String[] dst = new String[size];

       for (int i = 0; i < src.length; i++)
	 System.arraycopy(src[i], 0, dst, i * src.length, src.length);
       return dst;
    }



    String[][] getStringImageValue(DeviceAttribute deviceAttribute) throws DevFailed
    {
	String[] tmp;

	tmp = deviceAttribute.extractStringArray();
	int ydim = attribute.getYDimension();
	int xdim = attribute.getXDimension();

	if (ydim != retval.length || xdim != retval[0].length)
	{

	  retval = new String[ydim][xdim];
	}

	int k = 0;
	for (int y = 0; y < ydim; y++)
	  for (int x = 0; x < xdim; x++)
	  {
            retval[y][x] = tmp[k++];
	  }

	return retval;
    }

    void addStringImageListener(IStringImageListener l)
    {
	propChanges.addStringImageListener(l);
    }


    void removeStringImageListener(IStringImageListener l)
    {
	propChanges.removeStringImageListener(l);
    }


    public String getVersion() {
      return "$Id$";
    }

}

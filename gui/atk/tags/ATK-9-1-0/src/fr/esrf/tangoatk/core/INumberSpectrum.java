/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public interface INumberSpectrum extends INumber
{

    public static final String XMIN_ATT_PROP = "XminAttribute";
    public static final String XMAX_ATT_PROP = "XmaxAttribute";
    public static final String XMIN_PROP = "Xmin";
    public static final String XMAX_PROP = "Xmax";
    
    public void addSpectrumListener(ISpectrumListener l) ;

    public void removeSpectrumListener(ISpectrumListener l);

    public double[] getSpectrumValue();
    public double[] getSpectrumDeviceValue();
    public double[] getSpectrumStandardValue();
    
    public double[] getSpectrumSetPoint();
    public double[] getSpectrumDeviceSetPoint();
    public double[] getSpectrumStandardSetPoint();
    
    public void setValue(double[] d);

    public INumberSpectrumHistory[] getNumberSpectrumHistory();
    public INumberSpectrumHistory[] getNumberSpectrumDeviceHistory();
    
    public boolean hasMinxMaxxAttributes();
    
    public boolean hasMinxMaxxProperties();
  
    public String getMinxAttName();
   
    public String getMaxxAttName();
  
    public double getMinx();
  
    public double getMaxx();

}

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
 
package fr.esrf.tangoatk.core.util;

/*
 * AttrFunctionSpectrum.java
 *
 * Created on 12 septembre 2003, 15:39
 */

import fr.esrf.tangoatk.core.* ;

/**
 *
 * @author  OUNSY
 */
public abstract class AttrFunctionSpectrum extends NonAttrNumberSpectrum 
                                           implements IRefreshee {
    
    /** Creates a new instance of AttrFunctionSpectrum */
    public AttrFunctionSpectrum() {
    }
    
    public abstract double [] updateX();
    
    public abstract double [] updateY();
    
    public void refresh() {
        setXYValue( updateX(), updateY() );
    }
    
    /**
     * <code>setRefreshInterval</code> sets the refresh interval for
     * the EntityList. This interval decides how often an entity is polled
     * to see if its value has changed. The default value is 1000, that is,
     * the entity is polled once a second.
     * @param milliSeconds an <code>int</code> value
     */
    public void setRefreshInterval(int milliSeconds) {
	refreshInterval = milliSeconds;
	if (refresher != null) {
	    refresher.setRefreshInterval(refreshInterval);
	}
    }
    
    /**
     * <code>getRefreshInterval</code> gets the refresh-interval for
     * the entity list. The default value is 1000 milliseconds.
     * @return an <code>int</code> value which is the refresh-interval
     */
    public int getRefreshInterval() {
	return refreshInterval;
    }

    public void stopRefresher() {
	if (refresher != null) 
	    refresher.stop = true;
	refresher = null;
    }

    /**
     * <code>startRefresher</code>  starts the default refresher thread
     * for the entity list,which sleeps for refreshInterval seconds.
     * @see fr.esrf.tangoatk.core.AEntityList#setRefreshInterval
     * @see java.lang.Thread
     */
    public void startRefresher() {
	if (refresher == null) {
	    refresher = new Refresher("NonAttrNumberSpectrumRefresher");
	    refresher.setRefreshInterval(getRefreshInterval());
	}
    
	refresher.addRefreshee((IRefreshee)this).start();
    }

    protected int refreshInterval = 1000;
    private Refresher refresher = new Refresher();
}

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
 
// File:          IRefresherListener.java
// Author:        PONS
// Description:   An interface for refreshser listener

package fr.esrf.tangoatk.core;

/**
 * The interface <code>IRefresherListener</code> has one method
 * {@link #refreshStep} which is called by the refresher after
 * all models belonging to a list are updated.
 * @see AEntityList#addRefresherListener
 */
public interface IRefresherListener {

  /**
   * Called by an entityList refresher afer models update.
   */
   public void refreshStep();

}

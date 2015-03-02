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
 
package fr.esrf.tangoatk.widget.util.jdraw;

/**
 * An abstract adapter class for receiving mouse events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 */
public abstract class JDMouseAdapter implements JDMouseListener {
    /**
     * Invoked when the mouse has been clicked on a JDObject.
     */
    public void mouseClicked(JDMouseEvent e) {}

    /**
     * Invoked when a mouse button has been pressed on a JDObject.
     */
    public void mousePressed(JDMouseEvent e) {}

    /**
     * Invoked when a mouse button has been released on a JDObject.
     */
    public void mouseReleased(JDMouseEvent e) {}

    /**
     * Invoked when the mouse enters a JDObject.
     */
    public void mouseEntered(JDMouseEvent e) {}

    /**
     * Invoked when the mouse exits a JDObject.
     */
    public void mouseExited(JDMouseEvent e) {}
}

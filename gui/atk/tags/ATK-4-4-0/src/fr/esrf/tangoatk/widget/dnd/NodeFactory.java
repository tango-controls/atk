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
 
// File:          NodeFactory.java
// Created:       2002-09-12 12:38:06, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 11:8:16, erik>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.dnd;

import fr.esrf.tangoatk.core.*;

public class NodeFactory {
    protected static NodeFactory instance;
    public static String MIME_NUMBERSCALAR   = "tango/numberscalar";
    public static String MIME_NUMBERIMAGE    = "tango/numberimage";
    public static String MIME_NUMBERSPECTRUM = "tango/numberspectrum";
    public static String MIME_ENTITY         = "tango/entity";
    public static String MIME_COMMAND       = "tango/icommand";
    public static String MIME_ATTRIBUTE     = "tango/iattribute";
    public static String MIME_STRINGSCALAR   = "tango/strinscalar";
    
    protected NodeFactory() {

    }

    public static NodeFactory getInstance() {
	if (instance == null) {
	    instance = new NodeFactory();
	}
	return instance;
    }

    public Node getNode4Entity(IAttribute entity) {
	if (entity instanceof INumberScalar) 
	    return getNode4Entity((INumberScalar)entity);
	
	if (entity instanceof IStringScalar) 
	    return getNode4Entity((IStringScalar)entity);

	if (entity instanceof INumberSpectrum) 
	    return getNode4Entity((INumberSpectrum)entity);

	if (entity instanceof INumberImage) 
	    return getNode4Entity((INumberImage)entity);

	return new AttributeNode(entity);
    }
    
    public Node getNode4Entity(INumberScalar entity) {
	return new NumberScalarNode(entity);
    }

    public Node getNode4Entity(IStringScalar entity) {
	return new StringScalarNode(entity);
    }

    public Node getNode4Entity(INumberSpectrum entity) {
	return new NumberSpectrumNode(entity);
    }

    public Node getNode4Entity(INumberImage entity) {
	return new NumberImageNode(entity);
    }

    public Node getNode4Entity(ICommand entity) {
	return new CommandNode(entity);
    }

}

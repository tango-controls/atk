// File:          ATKNodeFactory.java
// Created:       2002-09-12 12:38:06, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 10:28:9, erik>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.dnd;

import fr.esrf.tangoatk.core.*;

public class NodeFactory {
    protected static ATKNodeFactory instance;
    public static String MIME_NUMBERSCALAR   = "tango/numberscalar";
    public static String MIME_NUMBERIMAGE    = "tango/numberimage";
    public static String MIME_NUMBERSPECTRUM = "tango/numberspectrum";
    public static String MIME_ENTITY         = "tango/entity";
    public static String MIME_ICOMMAND       = "tango/icommand";
    public static String MIME_IATTRIBUTE     = "tango/iattribute";
    public static String MIME_STRINGSCALAR   = "tango/strinscalar";
    
    protected NodeFactory() {

    }

    public static NodeFactory getInstance() {
	if (instance == null) {
	    instance = new NodeFactory();
	}
	return instance;
    }

    public ATKNode getNode4Entity(IAttribute entity) {
	if (entity instanceof INumberScalar) {
	    return getNode4Entity((INumberScalar)entity);
	}
	if (entity instanceof IStringScalar) {
	    return getNode4Entity((IStringScalar)entity);
	}
	if (entity instanceof INumberSpectrum) {
	    return getNode4Entity((INumberSpectrum)entity);
	}
	return getNode4Entity((INumberImage)entity);
    }
    
    public ATKNode getNode4Entity(INumberScalar entity) {
	return new NumberScalarNode(entity);
    }

    public ATKNode getNode4Entity(IStringScalar entity) {
	return new StringScalarNode(entity);
    }

    public ATKNode getNode4Entity(INumberSpectrum entity) {
	return new NumberSpectrumNode(entity);
    }

    public ATKNode getNode4Entity(INumberImage entity) {
	return new NumberImageNode(entity);
    }

    public ATKNode getNode4Entity(ICommand entity) {
	return new CommandNode(entity);
    }

}

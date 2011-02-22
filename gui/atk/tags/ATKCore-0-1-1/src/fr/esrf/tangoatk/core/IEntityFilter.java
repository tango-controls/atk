// File:          IFilter.java
// Created:       2002-03-27 12:56:49, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-04-05 10:5:52, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

/**
 * <code>IEntityFilter</code>
 *
 * @author <a href="mailto:erik@assum.net">Erik Assum</a>
 * @version $Version$
 */
public interface IEntityFilter extends java.io.Serializable {


    /**
     * <code>keep</code> should return true if the 
     * IEntity passed as argument is to be kept.
     * @param entity an <code>IEntity</code> value
     * @return a <code>boolean</code> value
     */
    public boolean keep(IEntity entity);

}

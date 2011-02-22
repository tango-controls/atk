// File:          IEntityCollection.java
// Created:       2001-10-15 14:53:16, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-04-05 9:31:38, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import java.util.List;

public interface IEntityCollection extends IRefreshee, java.io.Serializable {

    public IEntity add(String name)
    	throws ConnectionException;

    public void add(String []name)
    	throws ConnectionException;

    public boolean remove(String name);

    public IEntity get(String name);

    public List get(String [] name);

    public int size();
}

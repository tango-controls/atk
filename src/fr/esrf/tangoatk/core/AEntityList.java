// File:          TangoListModel.java
// Created:       2001-09-28 10:46:59, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-23 10:25:29, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.*;
import fr.esrf.tangoatk.core.AEntityFactory;
import fr.esrf.tangoatk.core.util.*;
import javax.swing.ComboBoxModel;

public abstract class AEntityList extends javax.swing.DefaultListModel
implements IEntityCollection, ComboBoxModel  {


    protected int refreshInterval = 1000;
    protected AEntityFactory factory;
    protected Refresher refresher = null;
    protected static ThreadGroup refreshers;
    transient protected AtkTimer timer = AtkTimer.getInstance();
    protected List errorListeners = new Vector();
    protected List setErrorListeners = new Vector();
    protected IEntity selectedItem;

    protected IEntityFilter filter = new IEntityFilter() {
	    public boolean keep(IEntity entity) {
		return true;
	    }
	};
    

    static {
	refreshers = new ThreadGroup("listRefreshers");
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

    public Object getSelectedItem() {
	return selectedItem;
    }

    public void setSelectedItem(Object obj) {
	selectedItem = (IEntity)obj;
    }
    
    /**
     * <code>refresh</code> refreshes the EntityList, that is, it asks
     * all its entities to poll its Tango peer to see if its value has
     * changed.
     */
    public void refresh() {
	int size = size();
	for (int i = 0; i < size; i++) {
	    ((IRefreshee)get(i)).refresh();
	}
	    
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
	    refresher = new Refresher("ListRefresher");
	    refresher.setRefreshInterval(getRefreshInterval());
	}
    
	refresher.addRefreshee((IRefreshee)this).start();
    }

    
    /**
     * <code>setRefresher</code> sets the resher thread 
     * for this EntityList.
     * @param r an <code>Refresher</code> value
     * @see fr.esrf.tangoatk.core.Refresher
     */
    public void setRefresher(Refresher r) {
	refresher = r;
    }
    
    /**
     * <code>setFilter</code> to filter out which IEntities 
     * should be added to the list and which should not be added.
     * @see IEntityFilter
     * @param filter an <code>IEntityFilter</code> value
     */
    public void setFilter(IEntityFilter filter) {
	this.filter = filter;
    }

    public IEntityFilter getFilter() {
	return filter;
    }

    public List get(String [] names) {
	List l = new Vector();
	
	for (int i = 0; i < names.length; i ++) {
	    l.add(get(names[i]));
	}
	return l;
    }


    public IEntity get(String attributeName) {

	for (int i = 0; i < size(); i++) {
	    IEntity e = (IEntity)get(i);

	    if (e.getName().equals(attributeName)) return e;
	}
	return null;
    }

    public void add(String [] names) throws ConnectionException {
	for (int i = 0; i < names.length; i++) {
	    if (names[i] == null) continue;
	    add(names[i]);
	}
    }

    public void add(IEntity entity) {
	if (contains(entity)) return;
	if (!filter.keep(entity)) return;
	addElement(entity);
    }
	
	
    public IEntity add(String name) throws ConnectionException
    {
       IEntity e = get(name);

       if (e == null)
       {
	  List l = factory.getEntities(name);
	  Iterator i = l.iterator();

	  while(i.hasNext())
	  {
	    e = (IEntity)i.next();
	    if (e == null || contains(e))
	       continue;
	    if (!filter.keep(e))
	       continue;


	    // error listeners
	    if (errorListeners != null)
	    {
	       for (int j = 0; j < errorListeners.size(); j++ )
	       {
		  e.addErrorListener((IErrorListener)errorListeners.get(j));
	       } 
	    }
	    
	    // set attribute error listeners meaningful only for attributes
	    if (e instanceof IAttribute)
	       if (setErrorListeners != null)
	       {
		  for (int j = 0; j < setErrorListeners.size(); j++ )
		  {
		     ((IAttribute)e).addSetErrorListener((ISetErrorListener)setErrorListeners.get(j));
		  } 
	       }// end of if ()
	    addElement(e);
	  }// end of while ()
       }
       return e;
    }


    public boolean remove(String entityName) {
	IEntity a = (IEntity)get(entityName);
	if (remove(indexOf(a)) != null)
	    return true;
	return false;

    }

    /**
     * <code>setErrorListener</code>
     *
     * @param l an <code>IErrorListener</code> value
     * @deprecated
     */
    public void setErrorListener(IErrorListener l) {
	errorListeners.clear();
	errorListeners.add(l);
    }
    
    public void addErrorListener(IErrorListener l) {
	errorListeners.add(l);
    }

    
    public void addSetErrorListener(ISetErrorListener l)
    {
        /* Sensless for a CommandList */
	   setErrorListeners.add(l);
    }

    public void finalize() {
	stopRefresher();
    }

    private static String VERSION = "$Id$";

    public String getVersion() {
	return VERSION;
    }


}

// File:          TangoListModel.java
// Created:       2001-09-28 10:46:59, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 14:5:48, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.*;
import fr.esrf.tangoatk.core.AEntityFactory;
import org.apache.log4j.*;
import fr.esrf.tangoatk.core.util.*;
import javax.swing.ComboBoxModel;

public abstract class AEntityList extends javax.swing.DefaultListModel
implements IEntityCollection, ComboBoxModel  {

    static Logger log =
	ATKLogger.getLogger(AEntityList.class.getName());

    protected int refreshInterval = 1000;
    protected AEntityFactory factory;
    protected ARefresher refresher = null;
    protected static ThreadGroup refreshers;
    transient protected AtkTimer timer = AtkTimer.getInstance();
    protected List errorListeners = new Vector();
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
	    refresher = new ARefresher("ListRefresher") {

		    public Thread addRefreshee(IRefreshee e) {
			refreshee = e;
			return this;
		    }

		    public void run() {
			    while (true) {
				if (stop) return;
				refreshee.refresh();
				try {
				    sleep(refreshInterval);
				} catch (Exception e) {
				    ;
				} // end of try-catch
				
			    }
		    }
		};
	}
    
	refresher.addRefreshee((IRefreshee)this).start();
    }

    
    /**
     * <code>setRefresher</code> sets the resher thread 
     * for this EntityList.
     * @param r an <code>ARefresher</code> value
     * @see fr.esrf.tangoatk.core.ARefresher
     */
    public void setRefresher(ARefresher r) {
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
	    log.debug("Adding " + names[i]);
	    add(names[i]);
	}
    }

    public void add(IEntity entity) {
	if (contains(entity)) return;
	if (!filter.keep(entity)) return;
	addElement(entity);
    }
	
	
    public IEntity add(String name) throws ConnectionException {
	IEntity e = get(name);
	log.debug("add(" + name + ")");

	if (e == null) {
	    List l = factory.getEntities(name);
	    log.debug("factory returned " + l);
	    Iterator i = l.iterator();

	    while(i.hasNext()) {
		e = (IEntity)i.next();
		if (e == null || contains(e)) continue;
		if (!filter.keep(e)) continue;
		
		log.debug("Adding " + e.getName());
		if (errorListeners != null)
		    for (int j = 0; j < errorListeners.size(); j++ ) {
			e.addErrorListener
			    ((IErrorListener)errorListeners.get(j));
		    } // end of for ()
		
		addElement(e);

	    }
	}
	return e;
    }


    public boolean remove(String entityName) {
	IEntity a = (IEntity)get(entityName);
	log.debug("removing " + entityName);
	if (remove(indexOf(a)) != null)
	    return true;
	return false;

    }

    public void clear() {
	factory.clear();
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

    public void finalize() {
	stopRefresher();
    }

    private static String VERSION = "$Id$";

    public String getVersion() {
	return VERSION;
    }


}

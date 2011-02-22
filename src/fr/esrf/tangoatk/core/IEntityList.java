package fr.esrf.tangoatk.core;

import java.util.List;



public interface IEntityList {

	/**
	 * <code>setRefreshInterval</code> sets the refresh interval for
	 * the EntityList. This interval decides how often an entity is polled
	 * to see if its value has changed. The default value is 1000, that is,
	 * the entity is polled once a second.
	 * @param milliSeconds an <code>int</code> value
	 */
	public abstract void setRefreshInterval(int milliSeconds);

	/**
	 * <code>getRefreshInterval</code> gets the refresh-interval for
	 * the entity list. The default value is 1000 milliseconds.
	 * @return an <code>int</code> value which is the refresh-interval
	 */
	public abstract int getRefreshInterval();

	/**
	 * <code>refresh</code> refreshes the EntityList, that is, it asks
	 * all its entities to poll its Tango peer to see if its value has
	 * changed.
	 */
	public abstract void refresh();

	public abstract boolean isRefresherStarted();

	public abstract void stopRefresher();

	/**
	 * <code>startRefresher</code>  starts the default refresher thread
	 * for the entity list,which sleeps for refreshInterval seconds.
	 * @see fr.esrf.tangoatk.core.AEntityList#setRefreshInterval
	 * @see java.lang.Thread
	 */
	public abstract void startRefresher();

	/**
	 * <code>setRefresher</code> sets the resher thread
	 * for this EntityList.
	 * @param r an <code>Refresher</code> value
	 * @see fr.esrf.tangoatk.core.Refresher
	 */
	public abstract void setRefresher(Refresher r);

	/**
	 * <code>setFilter</code> to filter out which IEntities
	 * should be added to the list and which should not be added.
	 * @see IEntityFilter
	 * @param filter an <code>IEntityFilter</code> value
	 */
	public abstract void setFilter(IEntityFilter filter);

	public abstract IEntityFilter getFilter();

	public abstract List<IEntity> get(String[] names);

	public abstract IEntity get(String attributeName);

	public abstract void add(String[] names) throws ConnectionException;

	public abstract void add(IEntity entity);

	public abstract IEntity add(String name) throws ConnectionException;

	public abstract boolean remove(String entityName);

	public abstract void addErrorListener(IErrorListener l);

	public abstract void addSetErrorListener(ISetErrorListener l);

	/**
	 * Remove an ErrorListener for all entities in the entitylist. Invokes
	 * removeErrorListener on all list members.
	 * 
	 * @param errl Error Listener
	 */

	public abstract void removeErrorListener(IErrorListener errl);

	/**
	 * Remove a SetErrorListener for all entities in the entitylist. Invokes
	 * removeSetErrorListener on all list members.
	 * 
	 * @param setErrl SetError Listener
	 */

	public abstract void removeSetErrorListener(ISetErrorListener setErrl);

	/**
	 * Add a listener on the refresher. Listeners are triggered
	 * at each refresher step after models refresh.
	 * @param l Refresher listener
	 */
	public abstract void addRefresherListener(IRefresherListener l);

	public abstract void removeRefresherListener(IRefresherListener l);

	public abstract void clearRefresherListener();

	public abstract String getVersion();

	public abstract void setSynchronizedPeriod(boolean synchro);

	public abstract void setTraceUnexpected(boolean trace);

	
	// Temp ?
	
	public abstract int size();
	
	public abstract int getSize();
	
	public abstract Object getElementAt(int index);
	
	public abstract Object get(int index);
	
	public abstract Object elementAt(int index);
	
	public abstract void clear();
	
	public abstract boolean contains(Object elem);

	public abstract int indexOf(Object elem);	
}
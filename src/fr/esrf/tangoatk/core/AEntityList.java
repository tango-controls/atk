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

import javax.swing.ComboBoxModel;

public abstract class AEntityList extends javax.swing.DefaultListModel
implements IEntityCollection, ComboBoxModel  {

  protected int refreshInterval = 1000;
  protected AEntityFactory factory;
  protected Refresher refresher = null;
  protected static ThreadGroup refreshers;
  //protected List errorListeners = new Vector();
  //protected List refresherListeners = new Vector();
  //protected List setErrorListeners = new Vector();
  protected List<IErrorListener> errorListeners = new Vector<IErrorListener> ();
  protected List<IRefresherListener> refresherListeners = new Vector<IRefresherListener> ();
  protected List<ISetErrorListener> setErrorListeners = new Vector<ISetErrorListener> ();
  protected IEntity selectedItem;
  
  boolean    refresherStarted = false;

  protected IEntityFilter filter = new IEntityFilter() {
    public boolean keep(IEntity entity) {
      return true;
    }
  };

  protected boolean synchronizedPeriod = false;
  protected boolean traceUnexpected = false;

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
    selectedItem = (IEntity) obj;
  }

  /**
   * <code>refresh</code> refreshes the EntityList, that is, it asks
   * all its entities to poll its Tango peer to see if its value has
   * changed.
   */
  public void refresh() {
    IEntity ie = null;
    long t0 = System.currentTimeMillis();

    trace(DeviceFactory.TRACE_REFRESHER, "AEntityList.refresh()  ", t0);
    for (int i = 0; i < size(); i++) {
      ie = (IEntity) get(i);
      if (!(ie.getDevice().doesEvent())) {
        //System.out.println("AEntityList.refresh() : "+ie.getName()+" device event incompatible");
        trace(DeviceFactory.TRACE_REFRESHER, "AEntityList.refresh() : device is not event compatible; will call refresh for " + ie.getName(), t0);
        ie.refresh();
      } else // device is event compatible
      {
        //System.out.println("AEntityList.refresh() : "+ie.getName()+" device event compatible");
        if (ie instanceof IAttribute) {
          IAttribute att = (IAttribute) ie;
          if (!att.hasEvents()) // subscribe event for the attribute failed at initialization
          {
            //System.out.println("AEntityList.refresh() : "+att.getName()+" has not events");
            trace(DeviceFactory.TRACE_REFRESHER, "AEntityList.refresh() : attribute has not subscribed event; will call refresh for " + ie.getName(), t0);
            ie.refresh(); // do polling instead of events!
          }
          //trace(DeviceFactory.TRACE_REFRESHER, "AEntityList.refresh() : attribute has events; do nothing for "+ie.getName(), t0);
        }
      }
    }

  }

  public boolean isRefresherStarted(){
      return refresherStarted;
  }

  
  public void stopRefresher() {
    if (refresher != null)
      refresher.stop = true;
    refresher = null;
    refresherStarted = false;
  }

  /**
   * <code>startRefresher</code>  starts the default refresher thread
   * for the entity list,which sleeps for refreshInterval seconds.
   * @see fr.esrf.tangoatk.core.AEntityList#setRefreshInterval
   * @see java.lang.Thread
   */
  public void startRefresher() {
    if ( this.isRefresherStarted() ) return;
    if (refresher == null) {
      refresher = new Refresher("ListRefresher");
      refresher.setRefreshInterval(getRefreshInterval());
      refresher.setSynchronizedPeriod(synchronizedPeriod);
      refresher.setTraceUnexpected(this.traceUnexpected);
    }

    refresher.addRefreshee(this).start();
    refresherStarted = true;
  }


  /**
   * <code>setRefresher</code> sets the resher thread
   * for this EntityList.
   * @param r an <code>Refresher</code> value
   * @see fr.esrf.tangoatk.core.Refresher
   */
  public void setRefresher(Refresher r) {
    if (r!= null) {
      synchronizedPeriod = r.isSynchronizedPeriod();
      traceUnexpected = r.isTraceUnexpected();
    }
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

  public List<IEntity> get(String[] names)
  {
    //List l = new Vector();
    List<IEntity> l = new Vector<IEntity> ();

    for (int i = 0; i < names.length; i++) {
      l.add(get(names[i]));
    }
    return l;
  }


  public IEntity get(String attributeName) {

    for (int i = 0; i < size(); i++) {
      IEntity e = (IEntity) get(i);

      if (e.getName().equalsIgnoreCase(attributeName)) return e;
    }
    return null;
  }

  public void add(String[] names) throws ConnectionException {
    for (int i = 0; i < names.length; i++) {
      if (names[i] == null) continue;
      add(names[i]);
    }
  }

  public void add(IEntity entity) {
    if (contains(entity)) return;
    if (!filter.keep(entity)) return;

   // error listeners
   if (errorListeners != null)
   {
     for (int j = 0; j < errorListeners.size(); j++)
     {
       //entity.addErrorListener((IErrorListener) errorListeners.get(j));
       entity.addErrorListener(errorListeners.get(j));
     }
   }

   // set attribute error listeners meaningful only for attributes
   if (entity instanceof IAttribute)
     if (setErrorListeners != null)
     {
       for (int j = 0; j < setErrorListeners.size(); j++)
       {
         //((IAttribute) entity).addSetErrorListener((ISetErrorListener) setErrorListeners.get(j));
         ((IAttribute) entity).addSetErrorListener(setErrorListeners.get(j));
       }
     }// end of if ()
     
     addElement(entity);

  }


  public IEntity add(String name) throws ConnectionException {
    IEntity e = get(name);

    if (e == null) {
      List l=null;
      try {
        l = factory.getEntities(name);
      } catch (ConnectionException ex) {
        // Fire an error event when initialisation failed.
        if( factory.isWildCard(name))
          name = name.substring(0,name.length()-2);
        fireErrorEvent(ex,name);
        throw ex;
      }

      Iterator i = l.iterator();

      while (i.hasNext()) {
        e = (IEntity) i.next();
        if (e == null || contains(e))
          continue;
        if (!filter.keep(e))
          continue;


        // error listeners
        if (errorListeners != null) {
          for (int j = 0; j < errorListeners.size(); j++) {
            //e.addErrorListener((IErrorListener) errorListeners.get(j));
            e.addErrorListener(errorListeners.get(j));
          }
        }

        // set attribute error listeners meaningful only for attributes
        if (e instanceof IAttribute)
          if (setErrorListeners != null) {
            for (int j = 0; j < setErrorListeners.size(); j++) {
              //((IAttribute) e).addSetErrorListener((ISetErrorListener) setErrorListeners.get(j));
              ((IAttribute) e).addSetErrorListener(setErrorListeners.get(j));
            }
          }// end of if ()
        addElement(e);
      }// end of while ()
    }
    return e;
  }


  public boolean remove(String entityName)
  {
     IEntity a = get(entityName);
     int     idx = indexOf(a);
     
     if (idx >= 0)
        if (remove(idx) != null)
           return true;
	   
     return false;

  }



  public void addErrorListener(IErrorListener l) {
    errorListeners.add(l);
  }


  public void addSetErrorListener(ISetErrorListener l) {
    /* Sensless for a CommandList */
    setErrorListeners.add(l);
  }

  /**
   * Remove an ErrorListener for all entities in the entitylist. Invokes
   * removeErrorListener on all list members.
   * 
   * @param errl Error Listener
   */

  public void removeErrorListener(IErrorListener errl) {
    IEntity ie = null;

    if (errl == null)
      return;

    if (errorListeners.contains(errl)) {
      for (int i = 0; i < size(); i++) {
        ie = (IEntity) get(i);
        ie.removeErrorListener(errl);
      }
      try {
        errorListeners.remove(errl);
      } catch (Exception ex) {
      }
    }

  }


  /**
   * Remove a SetErrorListener for all entities in the entitylist. Invokes
   * removeSetErrorListener on all list members.
   * 
   * @param setErrl SetError Listener
   */

  public void removeSetErrorListener(ISetErrorListener setErrl)
  {
     IEntity     ie=null;
     IAttribute  ia=null;
     
     if (setErrl == null)
        return;
	
     if (setErrorListeners.contains(setErrl))
     {
	for (int i=0; i<size(); i++)
	{
	    ie = (IEntity) get(i);
	    if (ie instanceof IAttribute)
	    {
	       ia = (IAttribute) ie;
	       ia.removeSetErrorListener(setErrl);
	    }
	}
	try
	{
	   setErrorListeners.remove(setErrl);
	}
	catch (Exception  ex)
	{
	}
     }
     
  }

  /**
   * Add a listener on the refresher. Listeners are triggered
   * at each refresher step after models refresh.
   * @param l Refresher listener
   */
  public void addRefresherListener(IRefresherListener l) {
    if( !refresherListeners.contains(l) )
      refresherListeners.add(l);
  }

  public void removeRefresherListener(IRefresherListener l) {
    refresherListeners.remove(l);
  }

  public void clearRefresherListener() {
    refresherListeners.clear();
  }

  protected void fireRefresherStepEvent() {

    int s = refresherListeners.size();

    for(int i=0;i<s;i++) {
      //IRefresherListener a = (IRefresherListener)refresherListeners.get(i);
      IRefresherListener a = refresherListeners.get(i);
      try {

        a.refreshStep();

      } catch (Exception e) {

        System.out.println("AEntityList.fireRefresherStepEvent() Exception caught ------------------------------");
        e.printStackTrace();
        System.out.println("AEntityList.fireRefresherStepEvent()------------------------------------------------");

      }
    }

  }

  private void trace(int level,String msg,long time)
  {
    DeviceFactory.getInstance().trace(level,msg,time);
  }

  private void fireErrorEvent(ConnectionException ex,String name) {
    if (errorListeners != null) {
      for (int j = 0; j < errorListeners.size(); j++)
        //((IErrorListener) errorListeners.get(j)).errorChange(new ErrorEvent(name,ex,System.currentTimeMillis()));
        errorListeners.get(j).errorChange(new ErrorEvent(name,ex,System.currentTimeMillis()));
    }
  }

  public void finalize() {
    stopRefresher();
  }

  private static String VERSION = "$Id$";

  public String getVersion() {
    return VERSION;
  }

  public void setSynchronizedPeriod(boolean synchro) {
    synchronizedPeriod = synchro;
    if (refresher == null) {
      refresher = new Refresher("ListRefresher");
      refresher.setRefreshInterval(getRefreshInterval());
    }
    refresher.setSynchronizedPeriod(synchronizedPeriod);
    refresher.setTraceUnexpected(traceUnexpected);
  }

  public void setTraceUnexpected(boolean trace) {
    traceUnexpected = trace;
    if (refresher == null) {
      refresher = new Refresher("ListRefresher");
      refresher.setRefreshInterval(getRefreshInterval());
    }
    refresher.setSynchronizedPeriod(synchronizedPeriod);
    refresher.setTraceUnexpected(traceUnexpected);
  }

}



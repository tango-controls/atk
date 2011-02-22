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
                implements IEntityCollection, ComboBoxModel, IEntityList 
{

  protected int refreshInterval = 1000;
  protected AEntityFactory factory;
  protected Refresher refresher = null;
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

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#setRefreshInterval(int)
 */
  public void setRefreshInterval(int milliSeconds) {
    refreshInterval = milliSeconds;
    if (refresher != null) {
      refresher.setRefreshInterval(refreshInterval);
    }
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#getRefreshInterval()
 */
  public int getRefreshInterval() {
    return refreshInterval;
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#getSelectedItem()
 */
  public Object getSelectedItem() {
    return selectedItem;
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#setSelectedItem(java.lang.Object)
 */
  public void setSelectedItem(Object obj) {
    selectedItem = (IEntity) obj;
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#refresh()
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


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#isRefresherStarted()
 */
  public boolean isRefresherStarted(){
      return refresherStarted;
  }


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#stopRefresher()
 */  
  public void stopRefresher() {
    if (refresher != null)
    {
      refresher.stop = true;
      refresher.refreshee = null;
    }
    refresher = null;
    refresherStarted = false;
  }


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#startRefresher()
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




/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#setRefresher(fr.esrf.tangoatk.core.Refresher)
 */
  public void setRefresher(Refresher r) {
    if (r!= null) {
      synchronizedPeriod = r.isSynchronizedPeriod();
      traceUnexpected = r.isTraceUnexpected();
    }
    refresher = r;
  }


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#setFilter(fr.esrf.tangoatk.core.IEntityFilter)
 */
  public void setFilter(IEntityFilter filter) {
    this.filter = filter;
  }


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#getFilter()
 */
  public IEntityFilter getFilter() {
    return filter;
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#get(java.lang.String[])
 */
  public List<IEntity> get(String[] names)
  {
    //List l = new Vector();
    List<IEntity> l = new Vector<IEntity> ();

    for (int i = 0; i < names.length; i++) {
      l.add(get(names[i]));
    }
    return l;
  }


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#get(java.lang.String)
 */
  public IEntity get(String attributeName) {

    for (int i = 0; i < size(); i++) {
      IEntity e = (IEntity) get(i);

      if (e.getName().equalsIgnoreCase(attributeName)) return e;
    }
    return null;
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#add(java.lang.String[])
 */
  public void add(String[] names) throws ConnectionException {
    for (int i = 0; i < names.length; i++) {
      if (names[i] == null) continue;
      add(names[i]);
    }
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#add(fr.esrf.tangoatk.core.IEntity)
 */
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

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#add(java.lang.String)
 */
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


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#remove(java.lang.String)
 */
  public boolean remove(String entityName)
  {
     IEntity a = get(entityName);
     int     idx = indexOf(a);
     
     if (idx >= 0)
        if (remove(idx) != null)
           return true;
	   
     return false;

  }



/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#addErrorListener(fr.esrf.tangoatk.core.IErrorListener)
 */
  public void addErrorListener(IErrorListener l) {
    errorListeners.add(l);
  }


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#addSetErrorListener(fr.esrf.tangoatk.core.ISetErrorListener)
 */
  public void addSetErrorListener(ISetErrorListener l) {
    /* Sensless for a CommandList */
    setErrorListeners.add(l);
  }


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#removeErrorListener(fr.esrf.tangoatk.core.IErrorListener)
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


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#removeSetErrorListener(fr.esrf.tangoatk.core.ISetErrorListener)
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


/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#addRefresherListener(fr.esrf.tangoatk.core.IRefresherListener)
 */
  public void addRefresherListener(IRefresherListener l) {
    if( !refresherListeners.contains(l) )
      refresherListeners.add(l);
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#removeRefresherListener(fr.esrf.tangoatk.core.IRefresherListener)
 */
  public void removeRefresherListener(IRefresherListener l) {
    if(refresherListeners != null)
	    refresherListeners.remove(l);
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#clearRefresherListener()
 */
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

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#finalize()
 */
  public void finalize() {
    stopRefresher();
  }

  private static String VERSION = "$Id$";

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#getVersion()
 */
  public String getVersion() {
    return VERSION;
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#setSynchronizedPeriod(boolean)
 */
  public void setSynchronizedPeriod(boolean synchro) {
    synchronizedPeriod = synchro;
    if (refresher == null) {
      refresher = new Refresher("ListRefresher");
      refresher.setRefreshInterval(getRefreshInterval());
    }
    refresher.setSynchronizedPeriod(synchronizedPeriod);
    refresher.setTraceUnexpected(traceUnexpected);
  }

/* (non-Javadoc)
 * @see fr.esrf.tangoatk.core.IEntityList#setTraceUnexpected(boolean)
 */
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



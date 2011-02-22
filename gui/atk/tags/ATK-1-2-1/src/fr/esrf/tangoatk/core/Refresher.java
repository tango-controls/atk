// File:          Refresher.java
// Created:       2001-09-28 10:42:32, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-23 15:48:42, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

/**
 * <code>ARefresher</code> is used as a base class to implement refreshers
 * of IRefreshees. Refreshers are Threads which calls the
 * IEntityCollections refresh method.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Id$
 */
public class Refresher extends Thread implements java.io.Serializable {
    public boolean stop = false;
    public boolean running = false;
    long refreshInterval = 1000;
    
    IRefreshee refreshee;
    
    protected static ThreadGroup refreshers;
    
    static {
	refreshers = new ThreadGroup("ATKRefreshers");
    }


    public Refresher() {
	super();
    }
    
    public Refresher(String s) {
	super(refreshers, s);
    }

    public void setRefreshInterval(long milliSeconds) {
	refreshInterval = milliSeconds;
    }

    public long getRefreshInterval() {
	return refreshInterval;
    }
    
    public void start() {
	if (running) return;
	
	running = true;
	stop = false;
	super.start();
    }


    public void stopRunning() {
	running = false;
	stop = true;
    }

    public boolean isRunning() {
	return running;
    }

    public void run() {
	while (true) {
	    if (stop) return;
		
	    refreshee.refresh();
	    try {
		sleep(refreshInterval);  
	    } catch (Exception e) {
		;
	    }
		
	} // end of while ()
    }

    /**
     * <code>addRefreshee</code> is used by the refreshee to 
     * add itself to the ARefresher. This method returns its Thread
     * so that the IRefreshee implementator can write
     * <code>refrehser.addRefreshee(this).start();</code>
     * @param e an <code>IRefreshee</code> value
     * @return a <code>Thread</code> value
     */
    public  Thread addRefreshee(IRefreshee e) {
	this.refreshee = e;
	return this;
    }	    

    public String getVersion() {
	return "$Id$";
    }

    private void readObject(java.io.ObjectInputStream in)
	throws java.io.IOException, ClassNotFoundException {
	System.out.print("Loading EntityList ");
	in.defaultReadObject();
	stopRunning();
    }

}

// File:          Refresher.java
// Created:       2001-09-28 10:42:32, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-04-05 14:1:34, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

/**
 * <code>ARefresher</code> is used as a base class to implement refreshers
 * of the IEntityCollections. Refreshers are Threads which calls the
 * IEntityCollections refresh method.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Id$
 */
public abstract class ARefresher extends Thread implements java.io.Serializable {
    public boolean stop = false;
    public boolean running = false;
    
    IRefreshee refreshee;
    public ARefresher() {
	super();
    }
    
    public ARefresher(ThreadGroup g, String s) {
	super(g, s);
    }

    public ARefresher(String s) {
	super(s);
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
	
    /**
     * <code>addRefreshee</code> is used by the IEntityCollection to 
     * add itself to the ARefresher. This method returns its Thread
     * so that the IEntityCollection implementator can write
     * <code>refrehser.addRefreshee(this).start();</code>
     * @param e an <code>IRefreshee</code> value
     * @return a <code>Thread</code> value
     */
    public abstract Thread addRefreshee(IRefreshee e);

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

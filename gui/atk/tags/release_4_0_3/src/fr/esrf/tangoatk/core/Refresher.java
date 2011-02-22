/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

/**
 * <code>ARefresher</code> is used as a base class to implement refreshers of
 * IRefreshees. Refreshers are Threads which calls the IEntityCollections
 * refresh method.
 * 
 * @author <a href="mailto:assum@esrf.fr">Erik Assum </a>
 * @version $Id$
 */
public class Refresher extends Thread implements java.io.Serializable {
    public boolean               stop               = false;
    public boolean               running            = false;

    private boolean              synchronizedPeriod = false;
    private boolean              traceUnexpected = false;

    long                         refreshInterval = 1000;
    private long                 before, after, sleepingPeriod;
    IRefreshee                   refreshee;

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

            before = System.currentTimeMillis();
            try {
                refreshee.refresh();
            }
            catch (Exception unexpected) {
                if (traceUnexpected) {
                    System.out.println("!!!!!!!!!!! Unexpected exception in refreshment !!!!!!!!!!!!!!!!!!!!!!");
                    unexpected.printStackTrace();
                }
            }
            //long duree = after - before;
            //System.out.println("One refresh cycle took "+ duree + " milliseconds.");
            try {
                //before = System.currentTimeMillis();
                if (synchronizedPeriod) {
                    synchronRefresh();
                }
                else {
                    sleep(refreshInterval);
                }
                //after = System.currentTimeMillis();
                //duree = after - before;
                //System.out.println("The sleep in refresher took "+ duree + "milli seconds.");
            }
            catch (Exception e) {
                if (traceUnexpected) {
                    System.out.println("!!!!!!!!!!! Refreshment sleep exception !!!!!!!!!!!!!!!!!!!!!!");
                    e.printStackTrace();
                }
            }

        } // end of while ()
    }

    /**
     * <code>addRefreshee</code> is used by the refreshee to add itself to the
     * ARefresher. This method returns its Thread so that the IRefreshee
     * implementator can write
     * <code>refrehser.addRefreshee(this).start();</code>
     * 
     * @param e
     *            an <code>IRefreshee</code> value
     * @return a <code>Thread</code> value
     */
    public Thread addRefreshee(IRefreshee e) {
        this.refreshee = e;
        return this;
    }

    public String getVersion() {
        return "$Id$";
    }

    private void synchronRefresh() {
        //synchronizing refreshment with its expected period.
        after = System.currentTimeMillis();
        sleepingPeriod = refreshInterval - (after - before);
        try {
            // resynchronizes the period
            if (sleepingPeriod > 0) {
                sleep(sleepingPeriod);
            }
        }
        catch (InterruptedException e) {
            if (traceUnexpected) {
                System.out.println("!!!!!!!!!!! Refreshment sleep exception !!!!!!!!!!!!!!!!!!!!!!");
                e.printStackTrace();
            }
        }
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        System.out.print("Loading EntityList ");
        in.defaultReadObject();
        stopRunning();
    }

    /**
     * To synchronize or not the refreshing period.
     * @param synchronizedP 21 dec. 2005
     */
    public void setSynchronizedPeriod(boolean synchronizedP) {
        synchronizedPeriod = synchronizedP;
    }

    /**
     * To know wheather the period is resynchronized or not
     * @return 21 dec. 2005
     */
    public boolean isSynchronizedPeriod() {
        return synchronizedPeriod;
    }

    /**
     * To trace or not unexpected Exceptions.
     * @param synchronizedP 21 dec. 2005
     */
    public void setTraceUnexpected(boolean trace) {
        traceUnexpected = trace;
    }

    /**
     * To know wheather unexpected exceptions are traced or not
     * @return 21 dec. 2005
     */
    public boolean isTraceUnexpected() {
        return traceUnexpected;
    }
    
    public IRefreshee getRefreshee() {
	    return refreshee;
    }

    public void setRefreshee(IRefreshee refreshee) {
	    this.refreshee = refreshee;
    }

}

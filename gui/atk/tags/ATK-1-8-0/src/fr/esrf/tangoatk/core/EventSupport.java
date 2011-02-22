// File:          EventSupport.java
// Created:       2002-01-31 17:33:25, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 14:27:14, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import javax.swing.event.EventListenerList;
import fr.esrf.tangoatk.core.util.*;
import java.util.List;
import java.io.*;

/**
 * <code>EventSupport</code> handles the event-generating of the core
 * part in ATK. EventSupport was implemented to get rid of
 * java.beans.PropertyChange*, since it was too general.
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public class EventSupport implements Serializable {

    INumberScalarListener[] numberscalarlisteners =
	new INumberScalarListener[0];
    EventListenerList listenerList = new EventListenerList();
    NumberScalarEvent numberScalarEvent =
	new NumberScalarEvent(new Object());
    StringScalarEvent stringScalarEvent;
    StringSpectrumEvent stringSpectrumEvent;
    NumberSpectrumEvent numberSpectrumEvent = null;
    NumberImageEvent numberImageEvent = null;
    StateEvent stateEvent = null;
    AttributeStateEvent attributeStateEvent = null;
    StatusEvent statusEvent = null;
    ResultEvent resultEvent = null;
    ErrorEvent readErrorEvent = null;
    ErrorEvent setErrorEvent = null;

    public synchronized void addImageListener(IImageListener l) {
	listenerList.add(IImageListener.class, l);
	addErrorListener(l);
    }

    public synchronized void removeImageListener(IImageListener l) {
	listenerList.remove(IImageListener.class, l);
	removeErrorListener(l);
    }

    public synchronized void addSpectrumListener(ISpectrumListener l) {
	listenerList.add(ISpectrumListener.class, l);
	addErrorListener(l);
    }

    public synchronized void removeSpectrumListener(ISpectrumListener l) {
	listenerList.remove(ISpectrumListener.class, l);
	removeErrorListener(l);
    }


    public synchronized void addStringScalarListener(IStringScalarListener l) {
	listenerList.add(IStringScalarListener.class, l);
	addErrorListener(l);
    }

    public synchronized void removeStringScalarListener(IStringScalarListener l) {
	listenerList.remove(IStringScalarListener.class, l);
	removeErrorListener(l);
    }


    public synchronized void addStringSpectrumListener(IStringSpectrumListener l) {
	listenerList.add(IStringSpectrumListener.class, l);
	addErrorListener(l);
    }

    public synchronized void removeStringSpectrumListener(IStringSpectrumListener l) {
	listenerList.remove(IStringSpectrumListener.class, l);
	removeErrorListener(l);
    }

    public synchronized void addResultListener(IResultListener l) {
	listenerList.add(IResultListener.class, l);
	addErrorListener(l);
    }

    public synchronized void removeResultListener(IResultListener l) {
	listenerList.remove(IResultListener.class, l);
	removeErrorListener(l);
    }

    public synchronized void addStateListener(IStateListener l) {
	listenerList.add(IStateListener.class, l);
    }
    public synchronized void removeStateListener(IStateListener l) {
	listenerList.remove(IStateListener.class, l);
    }
    public synchronized void addAttributeStateListener(IAttributeStateListener l) {
	listenerList.add(IAttributeStateListener.class, l);
	addErrorListener(l);
    }
    public synchronized void removeAttributeStateListener(IAttributeStateListener l) {
	listenerList.remove(IAttributeStateListener.class, l);
	removeErrorListener(l);
    }

    public synchronized void addStatusListener(IStatusListener l) {
	listenerList.add(IStatusListener.class, l);
    }

    public synchronized void removeStatusListener(IStatusListener l) {
	listenerList.remove(IStatusListener.class, l);
    }

    public synchronized void addErrorListener(IErrorListener l) {
	listenerList.add(IErrorListener.class, l);
    }

    public synchronized void removeErrorListener(IErrorListener l) {
	listenerList.remove(IErrorListener.class, l);
    }

    public synchronized void addSetErrorListener(ISetErrorListener l) {
	listenerList.add(ISetErrorListener.class, l);
    }

    public synchronized void removeSetErrorListener(ISetErrorListener l) {
	listenerList.remove(ISetErrorListener.class, l);
    }

    public synchronized void addNumberScalarListener(INumberScalarListener l) {
	int i = numberscalarlisteners.length;
	INumberScalarListener[] tmp = new INumberScalarListener[i + 1];
	System.arraycopy(numberscalarlisteners, 0, tmp, 0, i);
	tmp[i] = l;
	numberscalarlisteners = tmp;
	addErrorListener(l);
			
    }

    public synchronized void removeNumberScalarListener(INumberScalarListener l) {
	boolean found = false;
	int length = numberscalarlisteners.length;
	int i = 0;
	while (i < length) {
	    if (numberscalarlisteners[i] == l) {
		found = true;
		break;
	    }
	    i++;
	}

	if (!found) return;
	numberscalarlisteners[i] =
	    numberscalarlisteners[length - 1];
	INumberScalarListener[] tmp = new INumberScalarListener[length -1];
	System.arraycopy(numberscalarlisteners, 0, tmp, 0, length - 1);
	numberscalarlisteners = tmp;
    	removeErrorListener(l);
    }


    public void fireResultEvent(ICommand source, List result) {
	fireResultEvent(source, result, System.currentTimeMillis());
    }

    public void fireResultEvent(ICommand source, List result,
				   long timeStamp) {
    // Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==IResultListener.class) {
		// Lazily create the event:
		if (resultEvent == null)
		    resultEvent = new ResultEvent(source, result, timeStamp);
		else {
		    resultEvent.setSource(source);
		    resultEvent.setResult(result);
		    resultEvent.setTimeStamp(timeStamp);
		} // end of else
		((IResultListener)listeners[i+1]).resultChange(resultEvent);
	    }
	}
    }

    public void fireStatusEvent(Object source, String status) {
	fireStatusEvent(source, status, System.currentTimeMillis());
    }


    public void fireStatusEvent(Object source, String status,
				   long timeStamp ) {
    // Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==IStatusListener.class) {
		// Lazily create the event:
		if (statusEvent == null)
		    statusEvent = new StatusEvent(source, status, timeStamp);
		else {
		    statusEvent.setSource(source);
		    statusEvent.setStatus(status);
		    statusEvent.setTimeStamp(timeStamp);
		} // end of else
		((IStatusListener)listeners[i+1]).statusChange(statusEvent);
	    }
	}
    }

    public void fireStateEvent(Device source, String state) {
	fireStateEvent(source, state, System.currentTimeMillis());
    }
    
    public void fireStateEvent(Device source, String state,
				  long timeStamp) {
    // Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==IStateListener.class) {
		// Lazily create the event:
		if (stateEvent == null)
		    stateEvent = new StateEvent(source, state, timeStamp);
		else {
		    stateEvent.setSource(source);
		    stateEvent.setState(state);
		    stateEvent.setTimeStamp(timeStamp);
		} // end of else
		((IStateListener)listeners[i+1]).stateChange(stateEvent);
	    }
	}
    }

    
    public void fireAttributeStateEvent(IAttribute source, String state) {
	fireAttributeStateEvent(source, state, System.currentTimeMillis());
    }

    public void fireAttributeStateEvent(IAttribute source, String state,
					   long timeStamp) {
    // Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==IAttributeStateListener.class) {
		// Lazily create the event:
		if (attributeStateEvent == null)
		{
		    attributeStateEvent =
			new AttributeStateEvent(source, state, timeStamp);
		}
		else
		{
		    attributeStateEvent.setSource(source);
		    attributeStateEvent.setState(state);
		    attributeStateEvent.setTimeStamp(timeStamp);
		} // end of else
		((IAttributeStateListener)listeners[i+1]).stateChange(attributeStateEvent);
	    }
	}
    }

    public void fireReadErrorEvent(Object source, Throwable t) {
	fireReadErrorEvent(source, t, System.currentTimeMillis());
    }
    
    public void fireReadErrorEvent(Object source, Throwable t, long timeStamp) {

    // Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

	for (int i = listeners.length-2; i>=0; i-=2) {

	    if (listeners[i]==IErrorListener.class) {
		// Lazily create the event:
		if (readErrorEvent == null)
		    readErrorEvent = new ErrorEvent(source, t, timeStamp);
		else {
		    readErrorEvent.setSource(source);
		    readErrorEvent.setError(t);
		    readErrorEvent.setTimeStamp(timeStamp);
		} // end of else
		((IErrorListener)listeners[i+1]).errorChange(readErrorEvent);
	    }
	}
    }

    public void fireSetErrorEvent(Object source, Throwable t) {
	fireSetErrorEvent(source, t, System.currentTimeMillis());
    }
    
    public void fireSetErrorEvent(Object source, Throwable t, long timeStamp) {

    // Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

	for (int i = listeners.length-2; i>=0; i-=2) {

	    if (listeners[i]==ISetErrorListener.class) {
		// Lazily create the event:
		if (setErrorEvent == null)
		    setErrorEvent = new ErrorEvent(source, t, timeStamp);
		else {
		    setErrorEvent.setSource(source);
		    setErrorEvent.setError(t);
		    setErrorEvent.setTimeStamp(timeStamp);
		} // end of else
		((ISetErrorListener)listeners[i+1]).setErrorOccured(setErrorEvent);
	    }
	}
    }

    public void fireNumberScalarEvent(INumberScalar source,
					 double value, long timeStamp) {
	numberScalarEvent.setSource(source);
	numberScalarEvent.setValue(value);
	numberScalarEvent.setTimeStamp(timeStamp);
	for (int i = 0; i < numberscalarlisteners.length; i++) 
	    numberscalarlisteners[i].numberScalarChange(numberScalarEvent);
    }

    public void fireStringScalarEvent(IStringScalar source,
				     String value, long timeStamp) {
    // Guaranteed to return a non-null array

	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
	try {
	    stringScalarEvent.setSource(source);
	    stringScalarEvent.setValue(value);
	    stringScalarEvent.setTimeStamp(timeStamp);
	} catch (NullPointerException e) {
	    stringScalarEvent = new StringScalarEvent(source, value, timeStamp);    
	} // end of try-catch
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==IStringScalarListener.class) {
		((IStringScalarListener)listeners[i+1]).stringScalarChange(stringScalarEvent);
	    } 
	}
    }

    public void fireSpectrumEvent(INumberSpectrum source,
				     double []value, long timeStamp) {
    // Guaranteed to return a non-null array

	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
	try {
	    numberSpectrumEvent.setSource(source);
	    numberSpectrumEvent.setValue(value);
	    numberSpectrumEvent.setTimeStamp(timeStamp);
	} catch (NullPointerException e) {
	    numberSpectrumEvent = new NumberSpectrumEvent(source, value,
							  timeStamp);    
	} // end of try-catch
	
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==ISpectrumListener.class) {
		((ISpectrumListener)listeners[i+1]).spectrumChange(numberSpectrumEvent);
	    }
	}
    }

    public void fireImageEvent(INumberImage source,
					double [][]value, long timeStamp) {
    // Guaranteed to return a non-null array

	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
	try {
	    numberImageEvent.setSource(source);
	    numberImageEvent.setValue(value);
	    numberImageEvent.setTimeStamp(timeStamp);
	} catch (NullPointerException e) {
	    numberImageEvent = new NumberImageEvent(source, value, timeStamp);
	} // end of try-catch
	
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==IImageListener.class) {
		// Lazily create the event:
		((IImageListener)listeners[i+1]).imageChange(numberImageEvent);
	    }
	}
    }


    public void fireStringSpectrumEvent(IStringSpectrum source,
				     String[] value, long timeStamp)
    {
    // Guaranteed to return a non-null array

	Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
	try
	{
	   stringSpectrumEvent.setSource(source);
	   stringSpectrumEvent.setValue(value);
	   stringSpectrumEvent.setTimeStamp(timeStamp);
	}
	catch (NullPointerException e)
	{
	    stringSpectrumEvent = new StringSpectrumEvent(source, value, timeStamp);    
	}
	
	for (int i = listeners.length-2; i>=0; i-=2)
	{
	    if (listeners[i]==IStringSpectrumListener.class)
	    {
		((IStringSpectrumListener)listeners[i+1]).stringSpectrumChange(stringSpectrumEvent);
	    } 
	}
    }


    public String getVersion() {
	return "$Id$";
    }
}

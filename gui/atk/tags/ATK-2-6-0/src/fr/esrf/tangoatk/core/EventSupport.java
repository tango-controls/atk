// File:          EventSupport.java
// Created:       2002-01-31 17:33:25, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 14:27:14, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

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

  AtkEventListenerList listenerList = new AtkEventListenerList();

  // This to avoid an object creation at each fireEvent
  NumberScalarEvent numberScalarEvent = null;
  StringScalarEvent stringScalarEvent = null;
  StringSpectrumEvent stringSpectrumEvent;
  NumberSpectrumEvent numberSpectrumEvent = null;
  NumberImageEvent numberImageEvent = null;
  StateEvent stateEvent = null;
  AttributeStateEvent attributeStateEvent = null;
  StatusEvent statusEvent = null;
  ResultEvent resultEvent = null;
  ErrorEvent readErrorEvent = null;
  ErrorEvent setErrorEvent = null;
  BooleanImageEvent boolImageEvent = null;
  BooleanSpectrumEvent boolSpectrumEvent = null;
  BooleanScalarEvent boolScalarEvent = null;
  DevStateScalarEvent devStateScalarEvent = null;
  EndGroupExecutionEvent endGroupExecEvent = null;
  RawImageEvent rawImageEvent = null;
  

  public int getListenerCount() {
    return listenerList.getListenerCount();
  }

  /**
   * Returns a list (as String) of registered listener.
   */
  public String getListenerInfo() {

    String ret = "";
    Object[] lst = listenerList.getListenerList();

    for(int i=1;i<lst.length;i+=2) {

      String lName = lst[i-1].toString();
      int p = lName.lastIndexOf('.');
      if(p>=0) lName = lName.substring(p+1);

      String cName = lst[i].getClass().toString();
      if(cName.startsWith("class ")) cName = cName.substring(6);
      
      ret += cName + " [@" + Integer.toHexString(lst[i].hashCode()) + "] is " + lName + "\n";
    }

    if(ret.length()==0) {
      return "No listener registered.";
    } else {
      return ret;
    }

  }

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
    listenerList.add(INumberScalarListener.class, l);
    addErrorListener(l);
  }

  public synchronized void removeNumberScalarListener(INumberScalarListener l) {
    listenerList.remove(INumberScalarListener.class, l);
    removeErrorListener(l);
  }
  
  public synchronized void removeAtkEventListeners(){
      listenerList.removeAtkEventListeners();
  }

  public void fireResultEvent(ICommand source, List result) {
    fireResultEvent(source, result, System.currentTimeMillis());
  }

  public void fireResultEvent(ICommand source, List result, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

    // Lazily create the event
    if (resultEvent == null)
      resultEvent = new ResultEvent(source, result, timeStamp);
    else {
      resultEvent.setSource(source);
      resultEvent.setResult(result);
      resultEvent.setTimeStamp(timeStamp);
    }

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IResultListener.class)
        ((IResultListener) listeners[i + 1]).resultChange(resultEvent);
    }

  }

  public void fireStatusEvent(Object source, String status) {
    fireStatusEvent(source, status, System.currentTimeMillis());
  }


  public void fireStatusEvent(Object source, String status, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event:
    if (statusEvent == null)
      statusEvent = new StatusEvent(source, status, timeStamp);
    else {
      statusEvent.setSource(source);
      statusEvent.setStatus(status);
      statusEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IStatusListener.class)
        ((IStatusListener) listeners[i + 1]).statusChange(statusEvent);
    }

  }

  public void fireStateEvent(Device source, String state) {
    fireStateEvent(source, state, System.currentTimeMillis());
  }

  public void fireStateEvent(Device source, String state,
                             long timeStamp) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event:
    if (stateEvent == null)
      stateEvent = new StateEvent(source, state, timeStamp);
    else {
      stateEvent.setSource(source);
      stateEvent.setState(state);
      stateEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IStateListener.class)
        ((IStateListener) listeners[i + 1]).stateChange(stateEvent);
    }

  }


  public void fireAttributeStateEvent(IAttribute source, String state) {
    fireAttributeStateEvent(source, state, System.currentTimeMillis());
  }

  public void fireAttributeStateEvent(IAttribute source, String state,
                                      long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (attributeStateEvent == null) {
      attributeStateEvent =
              new AttributeStateEvent(source, state, timeStamp);
    } else {
      attributeStateEvent.setSource(source);
      attributeStateEvent.setState(state);
      attributeStateEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IAttributeStateListener.class)
        ((IAttributeStateListener) listeners[i + 1]).stateChange(attributeStateEvent);
    }

  }

  public void fireReadErrorEvent(Object source, Throwable t) {
    fireReadErrorEvent(source, t, System.currentTimeMillis());
  }

  public void fireReadErrorEvent(Object source, Throwable t, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (readErrorEvent == null)
      readErrorEvent = new ErrorEvent(source, t, timeStamp);
    else {
      readErrorEvent.setSource(source);
      readErrorEvent.setError(t);
      readErrorEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IErrorListener.class)
        ((IErrorListener) listeners[i + 1]).errorChange(readErrorEvent);
    }

  }

  public void fireSetErrorEvent(Object source, Throwable t) {
    fireSetErrorEvent(source, t, System.currentTimeMillis());
  }

  public void fireSetErrorEvent(Object source, Throwable t, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (setErrorEvent == null)
      setErrorEvent = new ErrorEvent(source, t, timeStamp);
    else {
      setErrorEvent.setSource(source);
      setErrorEvent.setError(t);
      setErrorEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ISetErrorListener.class)
        ((ISetErrorListener) listeners[i + 1]).setErrorOccured(setErrorEvent);
    }

  }

  public void fireNumberScalarEvent(INumberScalar source,
                                    double value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (numberScalarEvent == null)
      numberScalarEvent = new NumberScalarEvent(source, value, timeStamp);
    else {
      numberScalarEvent.setSource(source);
      numberScalarEvent.setValue(value);
      numberScalarEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == INumberScalarListener.class)
        ((INumberScalarListener) listeners[i + 1]).numberScalarChange(numberScalarEvent);
    }

  }

  public void fireStringScalarEvent(IStringScalar source,
                                    String value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (stringScalarEvent == null)
      stringScalarEvent = new StringScalarEvent(source, value, timeStamp);
    else {
      stringScalarEvent.setSource(source);
      stringScalarEvent.setValue(value);
      stringScalarEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IStringScalarListener.class)
        ((IStringScalarListener) listeners[i + 1]).stringScalarChange(stringScalarEvent);
    }

  }

  public void fireSpectrumEvent(INumberSpectrum source,
                                double[] value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (numberSpectrumEvent == null)
      numberSpectrumEvent = new NumberSpectrumEvent(source, value, timeStamp);
    else {
      numberSpectrumEvent.setSource(source);
      numberSpectrumEvent.setValue(value);
      numberSpectrumEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ISpectrumListener.class)
        ((ISpectrumListener) listeners[i + 1]).spectrumChange(numberSpectrumEvent);
    }

  }

  public void fireImageEvent(INumberImage source,
                             double[][] value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (numberImageEvent == null)
      numberImageEvent = new NumberImageEvent(source, value, timeStamp);
    else {
      numberImageEvent.setSource(source);
      numberImageEvent.setValue(value);
      numberImageEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IImageListener.class)
        ((IImageListener) listeners[i + 1]).imageChange(numberImageEvent);
    }

  }


  public void fireStringSpectrumEvent(IStringSpectrum source,
                                      String[] value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (stringSpectrumEvent == null)
      stringSpectrumEvent = new StringSpectrumEvent(source, value, timeStamp);
    else {
      stringSpectrumEvent.setSource(source);
      stringSpectrumEvent.setValue(value);
      stringSpectrumEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IStringSpectrumListener.class)
        ((IStringSpectrumListener) listeners[i + 1]).stringSpectrumChange(stringSpectrumEvent);
    }

  }

// Added support for new Tango attribute types February 2005


  public synchronized void addBooleanImageListener(IBooleanImageListener l) {
    listenerList.add(IBooleanImageListener.class, l);
    addErrorListener(l);
  }

  public synchronized void removeBooleanImageListener(IBooleanImageListener l) {
    listenerList.remove(IBooleanImageListener.class, l);
    removeErrorListener(l);
  }

  public void fireBooleanImageEvent(IBooleanImage source, boolean[][] value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (boolImageEvent == null)
      boolImageEvent = new BooleanImageEvent(source, value, timeStamp);
    else {
      boolImageEvent.setSource(source);
      boolImageEvent.setValue(value);
      boolImageEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IBooleanImageListener.class)
        ((IBooleanImageListener) listeners[i + 1]).booleanImageChange(boolImageEvent);
    }

  }


  public synchronized void addBooleanSpectrumListener(IBooleanSpectrumListener l) {
    listenerList.add(IBooleanSpectrumListener.class, l);
    addErrorListener(l);
  }

  public synchronized void removeBooleanSpectrumListener(IBooleanSpectrumListener l) {
    listenerList.remove(IBooleanSpectrumListener.class, l);
    removeErrorListener(l);
  }

  public void fireBooleanSpectrumEvent(IBooleanSpectrum source, boolean[] value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (boolSpectrumEvent == null)
      boolSpectrumEvent = new BooleanSpectrumEvent(source, value, timeStamp);
    else {
      boolSpectrumEvent.setSource(source);
      boolSpectrumEvent.setValue(value);
      boolSpectrumEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IBooleanSpectrumListener.class)
        ((IBooleanSpectrumListener) listeners[i + 1]).booleanSpectrumChange(boolSpectrumEvent);
    }

  }


  public synchronized void addBooleanScalarListener(IBooleanScalarListener l) {
    listenerList.add(IBooleanScalarListener.class, l);
    addErrorListener(l);
  }

  public synchronized void removeBooleanScalarListener(IBooleanScalarListener l) {
    listenerList.remove(IBooleanScalarListener.class, l);
    removeErrorListener(l);
  }

  public void fireBooleanScalarEvent(IBooleanScalar source, boolean value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (boolScalarEvent == null)
      boolScalarEvent = new BooleanScalarEvent(source, value, timeStamp);
    else {
      boolScalarEvent.setSource(source);
      boolScalarEvent.setValue(value);
      boolScalarEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IBooleanScalarListener.class)
        ((IBooleanScalarListener) listeners[i + 1]).booleanScalarChange(boolScalarEvent);
    }

  }


  public synchronized void addDevStateScalarListener(IDevStateScalarListener l) {
    listenerList.add(IDevStateScalarListener.class, l);
    addErrorListener(l);
  }

  public synchronized void removeDevStateScalarListener(IDevStateScalarListener l) {
    listenerList.remove(IDevStateScalarListener.class, l);
    removeErrorListener(l);
  }

  public void fireDevStateScalarEvent(IDevStateScalar source, String value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (devStateScalarEvent == null)
      devStateScalarEvent = new DevStateScalarEvent(source, value, timeStamp);
    else {
      devStateScalarEvent.setSource(source);
      devStateScalarEvent.setValue(value);
      devStateScalarEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IDevStateScalarListener.class)
        ((IDevStateScalarListener) listeners[i + 1]).devStateScalarChange(devStateScalarEvent);
    }

  }





  public synchronized void addEndGroupExecutionListener(IEndGroupExecutionListener l) {
    listenerList.add(IEndGroupExecutionListener.class, l);
    //addErrorListener(l);
  }

  public synchronized void removeEndGroupExecutionListener(IEndGroupExecutionListener l) {
    listenerList.remove(IEndGroupExecutionListener.class, l);
    //removeErrorListener(l);
  }

  public void fireEndGroupExecutionEvent(ICommandGroup source, List result) {
    fireEndGroupExecutionEvent(source, result, System.currentTimeMillis());
  }

  public void fireEndGroupExecutionEvent(ICommandGroup source, List result, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event

    // Lazily create the event
    if (endGroupExecEvent == null)
      endGroupExecEvent = new EndGroupExecutionEvent(source, result, timeStamp);
    else {
      endGroupExecEvent.setSource(source);
      endGroupExecEvent.setResult(result);
      endGroupExecEvent.setTimeStamp(timeStamp);
    }

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IEndGroupExecutionListener.class)
        ((IEndGroupExecutionListener) listeners[i + 1]).endGroupExecution(endGroupExecEvent);
    }

  }

  public synchronized void addRawImageListener(IRawImageListener l) {
    listenerList.add(IRawImageListener.class, l);
    addErrorListener(l);
  }

  public synchronized void removeRawImageListener(IRawImageListener l) {
    listenerList.remove(IRawImageListener.class, l);
    removeErrorListener(l);
  }

  public void fireRawImageEvent(IRawImage source, byte[][] value, long timeStamp) {

    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();

    // Lazily create the event
    if (rawImageEvent == null)
      rawImageEvent = new RawImageEvent(source, value, timeStamp);
    else {
      rawImageEvent.setSource(source);
      rawImageEvent.setValue(value);
      rawImageEvent.setTimeStamp(timeStamp);
    }

    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == IRawImageListener.class)
        ((IRawImageListener) listeners[i + 1]).rawImageChange(rawImageEvent);
    }

  }

  public String getVersion() {
    return "$Id$";
  }

}

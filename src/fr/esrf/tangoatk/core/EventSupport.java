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
 
// File:          EventSupport.java
// Created:       2002-01-31 17:33:25, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 14:27:14, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventListener;
import java.util.List;
import java.io.*;
import java.lang.ref.WeakReference;

/**
 * <code>EventSupport</code> handles the event-generating of the core
 * part in ATK. EventSupport was implemented to get rid of
 * java.beans.PropertyChange*, since it was too general.
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public class EventSupport implements Serializable
{

    protected AtkEventListenerList listenerList = new AtkEventListenerList();

    // This to avoid an object creation at each fireEvent
    protected NumberScalarEvent numberScalarEvent = null;
    protected StringScalarEvent stringScalarEvent = null;
    protected StringSpectrumEvent stringSpectrumEvent;
    protected NumberSpectrumEvent numberSpectrumEvent = null;
    protected NumberImageEvent numberImageEvent = null;
    protected StateEvent stateEvent = null;
    protected AttributeStateEvent attributeStateEvent = null;
    protected StatusEvent statusEvent = null;
    protected ResultEvent resultEvent = null;
    protected ErrorEvent readErrorEvent = null;
    protected ErrorEvent setErrorEvent = null;
    protected BooleanImageEvent boolImageEvent = null;
    protected BooleanSpectrumEvent boolSpectrumEvent = null;
    protected BooleanScalarEvent boolScalarEvent = null;
    protected DevStateScalarEvent devStateScalarEvent = null;
    protected EndGroupExecutionEvent endGroupExecEvent = null;
    protected RawImageEvent rawImageEvent = null;
    protected EnumScalarEvent enumScalarEvent = null;
    protected StringImageEvent strImageEvent = null;
    protected DevStateSpectrumEvent devStateSpectrumEvent = null;


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

    public void fireResultEvent(ICommand source, List result, long timeStamp)
    {

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

        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IResultListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IResultListener) weakRef.get()).resultChange(resultEvent);
              else
                      listenerList.remove(i);
          }
        }
        
        // Free memory in the Event Structure
        resultEvent.setSource(null);
        resultEvent.setResult(null);
    }

    public void fireStatusEvent(Object source, String status) {
    fireStatusEvent(source, status, System.currentTimeMillis());
    }


    public void fireStatusEvent(Object source, String status, long timeStamp)
    {
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
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == IStatusListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IStatusListener) weakRef.get()).statusChange(statusEvent);
              else
                      listenerList.remove(i);
          }
        }        
        // Free memory in the Event Structure
        statusEvent.setSource(null);
        statusEvent.setStatus(null);
    }

    public void fireStateEvent(Device source, String state) {
    fireStateEvent(source, state, System.currentTimeMillis());
    }

    public void fireStateEvent(Device source, String state, long timeStamp)
    {
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
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == IStateListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IStateListener) weakRef.get()).stateChange(stateEvent);
              else
                      listenerList.remove(i);
          }
        }       
        // Free memory in the Event Structure
        stateEvent.setSource(null);
        stateEvent.setState(null);
    }


    public void fireAttributeStateEvent(IAttribute source, String state) {
    fireAttributeStateEvent(source, state, System.currentTimeMillis());
    }

    public void fireAttributeStateEvent(IAttribute source, String state, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (attributeStateEvent == null)
        {
          attributeStateEvent = new AttributeStateEvent(source, state, timeStamp);
        } 
        else
        {
          attributeStateEvent.setSource(source);
          attributeStateEvent.setState(state);
          attributeStateEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == IAttributeStateListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IAttributeStateListener) weakRef.get()).stateChange(attributeStateEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        attributeStateEvent.setSource(null);
        attributeStateEvent.setState(null);
    }

    public void fireReadErrorEvent(Object source, Throwable t) {
    fireReadErrorEvent(source, t, System.currentTimeMillis());
    }

    public void fireReadErrorEvent(Object source, Throwable t, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (readErrorEvent == null)
          readErrorEvent = new ErrorEvent(source, t, timeStamp);
        else
        {
          readErrorEvent.setSource(source);
          readErrorEvent.setError(t);
          readErrorEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IErrorListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IErrorListener) weakRef.get()).errorChange(readErrorEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        readErrorEvent.setSource(null);
        readErrorEvent.setError(null);
    }

    public void fireSetErrorEvent(Object source, Throwable t) {
    fireSetErrorEvent(source, t, System.currentTimeMillis());
    }

    public void fireSetErrorEvent(Object source, Throwable t, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (setErrorEvent == null)
          setErrorEvent = new ErrorEvent(source, t, timeStamp);
        else
        {
          setErrorEvent.setSource(source);
          setErrorEvent.setError(t);
          setErrorEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == ISetErrorListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((ISetErrorListener) weakRef.get()).setErrorOccured(setErrorEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        setErrorEvent.setSource(null);
        setErrorEvent.setError(null);
    }

    public void fireNumberScalarEvent(INumberScalar source, double value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (numberScalarEvent == null)
          numberScalarEvent = new NumberScalarEvent(source, value, timeStamp);
        else
        {
          numberScalarEvent.setSource(source);
          numberScalarEvent.setValue(value);
          numberScalarEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == INumberScalarListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((INumberScalarListener) weakRef.get()).numberScalarChange(numberScalarEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        numberScalarEvent.setSource(null);
    }

    public void fireStringScalarEvent(IStringScalar source, String value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (stringScalarEvent == null)
          stringScalarEvent = new StringScalarEvent(source, value, timeStamp);
        else
        {
          stringScalarEvent.setSource(source);
          stringScalarEvent.setValue(value);
          stringScalarEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IStringScalarListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IStringScalarListener) weakRef.get()).stringScalarChange(stringScalarEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        stringScalarEvent.setSource(null);
        stringScalarEvent.setValue(null);
    }

    public void fireSpectrumEvent(INumberSpectrum source, double[] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (numberSpectrumEvent == null)
          numberSpectrumEvent = new NumberSpectrumEvent(source, value, timeStamp);
        else
        {
          numberSpectrumEvent.setSource(source);
          numberSpectrumEvent.setValue(value);
          numberSpectrumEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == ISpectrumListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((ISpectrumListener) weakRef.get()).spectrumChange(numberSpectrumEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        numberSpectrumEvent.setSource(null);
        numberSpectrumEvent.setValue(null);
    }

    public void fireImageEvent(INumberImage source, double[][] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (numberImageEvent == null)
          numberImageEvent = new NumberImageEvent(source, value, timeStamp);
        else
        {
          numberImageEvent.setSource(source);
          numberImageEvent.setValue(value);
          numberImageEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IImageListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IImageListener) weakRef.get()).imageChange(numberImageEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        numberImageEvent.setSource(null);
        numberImageEvent.setValue(null);
    }


    public void fireStringSpectrumEvent(IStringSpectrum source, String[] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (stringSpectrumEvent == null)
          stringSpectrumEvent = new StringSpectrumEvent(source, value, timeStamp);
        else
        {
          stringSpectrumEvent.setSource(source);
          stringSpectrumEvent.setValue(value);
          stringSpectrumEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IStringSpectrumListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IStringSpectrumListener) weakRef.get()).stringSpectrumChange(stringSpectrumEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        stringSpectrumEvent.setSource(null);
        stringSpectrumEvent.setValue(null);
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

    public void fireBooleanImageEvent(IBooleanImage source, boolean[][] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (boolImageEvent == null)
          boolImageEvent = new BooleanImageEvent(source, value, timeStamp);
        else
        {
          boolImageEvent.setSource(source);
          boolImageEvent.setValue(value);
          boolImageEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IBooleanImageListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IBooleanImageListener) weakRef.get()).booleanImageChange(boolImageEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        boolImageEvent.setSource(null);
        boolImageEvent.setValue(null);
    }


    public synchronized void addBooleanSpectrumListener(IBooleanSpectrumListener l) {
    listenerList.add(IBooleanSpectrumListener.class, l);
    addErrorListener(l);
    }

    public synchronized void removeBooleanSpectrumListener(IBooleanSpectrumListener l) {
    listenerList.remove(IBooleanSpectrumListener.class, l);
    removeErrorListener(l);
    }

    public void fireBooleanSpectrumEvent(IBooleanSpectrum source, boolean[] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (boolSpectrumEvent == null)
          boolSpectrumEvent = new BooleanSpectrumEvent(source, value, timeStamp);
        else
        {
          boolSpectrumEvent.setSource(source);
          boolSpectrumEvent.setValue(value);
          boolSpectrumEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IBooleanSpectrumListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IBooleanSpectrumListener) weakRef.get()).booleanSpectrumChange(boolSpectrumEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        boolSpectrumEvent.setSource(null);
        boolSpectrumEvent.setValue(null);
    }


    public synchronized void addBooleanScalarListener(IBooleanScalarListener l) {
    listenerList.add(IBooleanScalarListener.class, l);
    addErrorListener(l);
    }

    public synchronized void removeBooleanScalarListener(IBooleanScalarListener l) {
    listenerList.remove(IBooleanScalarListener.class, l);
    removeErrorListener(l);
    }

    public void fireBooleanScalarEvent(IBooleanScalar source, boolean value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (boolScalarEvent == null)
          boolScalarEvent = new BooleanScalarEvent(source, value, timeStamp);
        else
        {
          boolScalarEvent.setSource(source);
          boolScalarEvent.setValue(value);
          boolScalarEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IBooleanScalarListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IBooleanScalarListener) weakRef.get()).booleanScalarChange(boolScalarEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        boolScalarEvent.setSource(null);
    }


    public synchronized void addDevStateScalarListener(IDevStateScalarListener l) {
    listenerList.add(IDevStateScalarListener.class, l);
    addErrorListener(l);
    }

    public synchronized void removeDevStateScalarListener(IDevStateScalarListener l) {
    listenerList.remove(IDevStateScalarListener.class, l);
    removeErrorListener(l);
    }

    public void fireDevStateScalarEvent(IDevStateScalar source, String value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (devStateScalarEvent == null)
          devStateScalarEvent = new DevStateScalarEvent(source, value, timeStamp);
        else
        {
          devStateScalarEvent.setSource(source);
          devStateScalarEvent.setValue(value);
          devStateScalarEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IDevStateScalarListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IDevStateScalarListener) weakRef.get()).devStateScalarChange(devStateScalarEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        devStateScalarEvent.setSource(null);
        devStateScalarEvent.setValue(null);
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

    public void fireEndGroupExecutionEvent(ICommandGroup source, List result, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event

        // Lazily create the event
        if (endGroupExecEvent == null)
          endGroupExecEvent = new EndGroupExecutionEvent(source, result, timeStamp);
        else
        {
          endGroupExecEvent.setSource(source);
          endGroupExecEvent.setResult(result);
          endGroupExecEvent.setTimeStamp(timeStamp);
        }

        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IEndGroupExecutionListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IEndGroupExecutionListener) weakRef.get()).endGroupExecution(endGroupExecEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        endGroupExecEvent.setSource(null);
        endGroupExecEvent.setResult(null);
    }

    public synchronized void addRawImageListener(IRawImageListener l) {
    listenerList.add(IRawImageListener.class, l);
    addErrorListener(l);
    }

    public synchronized void removeRawImageListener(IRawImageListener l) {
    listenerList.remove(IRawImageListener.class, l);
    removeErrorListener(l);
    }

    public void fireRawImageEvent(IRawImage source,String encFormat, byte[] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (rawImageEvent == null)
          rawImageEvent = new RawImageEvent(source, encFormat, value, timeStamp);
        else
        {
          rawImageEvent.setSource(source);
          rawImageEvent.setValue(encFormat,value);
          rawImageEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IRawImageListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IRawImageListener) weakRef.get()).rawImageChange(rawImageEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        rawImageEvent.setSource(null);
        rawImageEvent.setValue(null,null);
    }



    public synchronized void addEnumScalarListener(IEnumScalarListener l) {
    listenerList.add(IEnumScalarListener.class, l);
    addErrorListener(l);
    }

    public synchronized void removeEnumScalarListener(IEnumScalarListener l) {
    listenerList.remove(IEnumScalarListener.class, l);
    removeErrorListener(l);
    }

    public void fireEnumScalarEvent(IEnumScalar source, String value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (enumScalarEvent == null)
          enumScalarEvent = new EnumScalarEvent(source, value, timeStamp);
        else
        {
          enumScalarEvent.setSource(source);
          enumScalarEvent.setValue(value);
          enumScalarEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IEnumScalarListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IEnumScalarListener) weakRef.get()).enumScalarChange(enumScalarEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        enumScalarEvent.setSource(null);
        enumScalarEvent.setValue(null);
    }


    // Added support for StringImage Tango attributes May 2007


    public synchronized void addStringImageListener(IStringImageListener l) {
    listenerList.add(IStringImageListener.class, l);
    addErrorListener(l);
    }

    public synchronized void removeStringImageListener(IStringImageListener l) {
    listenerList.remove(IStringImageListener.class, l);
    removeErrorListener(l);
    }

    public void fireStringImageEvent(IStringImage source, String[][] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (strImageEvent == null)
          strImageEvent = new StringImageEvent(source, value, timeStamp);
        else {
          strImageEvent.setSource(source);
          strImageEvent.setValue(value);
          strImageEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
          if (listeners[i] == IStringImageListener.class)
          {
              weakRef = (WeakReference)listeners[i + 1];
              if(weakRef.get()!=null)
                      ((IStringImageListener) weakRef.get()).stringImageChange(strImageEvent);
              else
                      listenerList.remove(i);
          }
        }
        // Free memory in the Event Structure
        strImageEvent.setSource(null);
        strImageEvent.setValue(null);
    }


    public synchronized void addDevStateSpectrumListener(IDevStateSpectrumListener l)
    {
        listenerList.add(IDevStateSpectrumListener.class, l);
        addErrorListener(l);
    }

    public synchronized void removeDevStateSpectrumListener(IDevStateSpectrumListener l)
    {
        listenerList.remove(IDevStateSpectrumListener.class, l);
        removeErrorListener(l);
    }

    public void fireDevStateSpectrumEvent(IDevStateSpectrum source, String[] value, long timeStamp)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Lazily create the event
        if (devStateSpectrumEvent == null)
        {
            devStateSpectrumEvent = new DevStateSpectrumEvent(source, value, timeStamp);
        }
        else
        {
            devStateSpectrumEvent.setSource(source);
            devStateSpectrumEvent.setValue(value);
            devStateSpectrumEvent.setTimeStamp(timeStamp);
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        WeakReference<EventListener> weakRef;
        
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == IDevStateSpectrumListener.class)
            {
                weakRef = (WeakReference) listeners[i + 1];
                if (weakRef.get() != null)
                {
                    ((IDevStateSpectrumListener) weakRef.get()).devStateSpectrumChange(devStateSpectrumEvent);
                }
                else{
                    listenerList.remove(i);
                }
            }
        }
        // Free memory in the Event Structure
        devStateSpectrumEvent.setSource(null);
        devStateSpectrumEvent.setValue(null);
     }
  
  
    public String getVersion()
    {
        return "$Id$";
    }

    public AtkEventListenerList getListenerList () {
        return listenerList;
    }

}

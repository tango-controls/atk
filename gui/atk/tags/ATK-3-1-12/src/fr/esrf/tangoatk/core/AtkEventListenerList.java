package fr.esrf.tangoatk.core;

import java.util.EventListener;

/**
 * A class to handle AtkEventList. Main difference with EventListenerList is
 * that this class does not duplicate event Listener.
 */
public class AtkEventListenerList {

  private final static Object[] nullArray = new Object[0];

  Object[] listenerList = nullArray;

  public Object[] getListenerList() {
  return listenerList;
  }

  public synchronized void add(Class t, EventListener l) {

    if (l==null) {
      System.out.print("AtkEventListenerList.add() : Trying to register a null listener object ["+t+"]");
      return;
    }

    // Check instance of the listener
    if (!t.isInstance(l)) {
       throw new IllegalArgumentException("Listener " + l + " is not of type " + t);
    }

    // Does not add it if already registered
    if(foundEntry(t,l)<0) {
      // Add it
      int i = listenerList.length;
      Object[] tmp = new Object[i+2];
      System.arraycopy(listenerList, 0, tmp, 0, i);
      tmp[i] = t;
      tmp[i+1] = l;
      listenerList = tmp;
    }

  }

  public synchronized void remove(Class t, EventListener l) {

    int index = foundEntry(t,l);
    if(index<0) return;

    // Remove it
    Object[] tmp = new Object[listenerList.length-2];
    // Copy the list up to index
    System.arraycopy(listenerList, 0, tmp, 0, index);
    // Copy from two past the index, up to
    // the end of tmp (which is two elements
    // shorter than the old list)
    if (index < tmp.length)
      System.arraycopy(listenerList, index+2, tmp, index, tmp.length - index);
    // set the listener array to the new array or null
    listenerList = (tmp.length == 0) ? nullArray : tmp;

  }

  // If you need to purge your list
  public synchronized void removeAtkEventListeners(){
      listenerList = nullArray;
  }
  
  public synchronized int getListenerCount() {
    return listenerList.length/2;
  }

  private int foundEntry(Class t,EventListener l) {

    int index = -1;
    for (int i = listenerList.length-2; i>=0; i-=2) {
      if ((listenerList[i]==t) && (listenerList[i+1].equals(l) == true)) {
        index = i;
        break;
      }
    }

    return index;

  }

}

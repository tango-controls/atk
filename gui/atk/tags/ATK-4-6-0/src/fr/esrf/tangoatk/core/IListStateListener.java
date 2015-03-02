package fr.esrf.tangoatk.core;

/**
 * The interface <code>IListStateListener</code> has one method
 * {@link #stateChange} which is called by the EntityList after
 * startRefresher or stopRefresher is called.
 * @see AEntityList#addListStateListener
 */
public interface IListStateListener {

  /**
   * Called by an entityList after a call to EntityList.startRefresher or EntityList.stopRefresher.
   */
   public void stateChange(int state);

}

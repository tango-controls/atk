// File:          IRefresherListener.java
// Author:        PONS
// Description:   An interface for refreshser listener

package fr.esrf.tangoatk.core;

/**
 * The interface <code>IRefresherListener</code> has one method
 * {@link #refreshStep} which is called by the refresher after
 * all models belonging to a list are updated.
 * @see AEntityList#addRefresherListener
 */
public interface IRefresherListener {

  /**
   * Called by an entityList refresher afer models update.
   */
   public void refreshStep();

}

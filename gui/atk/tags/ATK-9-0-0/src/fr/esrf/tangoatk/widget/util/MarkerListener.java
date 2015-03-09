package fr.esrf.tangoatk.widget.util;

import java.util.EventListener;

/**
 * Interface for marker listener of JImage
 */
public interface MarkerListener extends EventListener {

  /**
   * Called when a marker is selected
   * @param source Component that has generated the event
   * @param markerId Id of the selected marker
   */
  public void clickOnMarker(JImage source,int markerId);

  /**
   * Called when a marker has been moved
   * @param markerId Id of the selected marker
   * @param source Component that has generated the event
   * @param x1 marker coordinates
   * @param y1 marker coordinates
   * @param x2 marker coordinates
   * @param y2 marker coordinates
   */
  public void markerMoved(JImage source,int markerId,int x1,int y1,int x2,int y2);

}

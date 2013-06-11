package fr.esrf.tangoatk.widget.jdraw;

/**
 * Listen on synoptic file loading progress
 */
public interface SynopticProgressListener {

  /**
   * Called when an item is loaded and connected to the device
   * @param p Loading progress (from 0 to 1).
   */
  public void progress(double p);

}

package fr.esrf.tangoatk.widget.attribute;

import java.util.EventListener;

/**
 * Interface to listen on cursor motion of the NumberSpectrumTrend3DViewer
 */
public interface I3DTrendCursorListener extends EventListener {

  /**
   * Called when the cursor move.
   * @param source Source object
   * @param x X coordinates (-1 if cursor not present)
   * @param y Y coordinates (-1 if cursor not present)
   */
  public void cursorMove(NumberSpectrumTrend3DViewer source,int x,int y);

}

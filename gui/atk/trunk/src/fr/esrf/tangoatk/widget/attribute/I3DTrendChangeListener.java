package fr.esrf.tangoatk.widget.attribute;

import java.util.EventListener;

/**
 * Interface to listen on configuration change of the NumberSpectrumTrend3DViewer
 */

public interface I3DTrendChangeListener extends EventListener {

  /**
   * Called when zoom change
   * @param source source object
   * @param zoomX Horizontal zoom factor
   * @param zoomY Vertical zoom factor
   */
  public void zoomChanged(NumberSpectrumTrend3DViewer source,int zoomX,int zoomY);

  /**
   * Called when horizontal scroll changed
   * @param source source object
   * @param hScrollPos Horizontal scroll pose
   */
  public void horinzontalScrollChanged(NumberSpectrumTrend3DViewer source,int hScrollPos);

  /**
   * Called when vertical scroll changed
   * @param source source object
   * @param vScrollPos Horizontal scroll pose
   */
  public void verticalScrollChanged(NumberSpectrumTrend3DViewer source,int vScrollPos);

}

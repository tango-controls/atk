package fr.esrf.tangoatk.widget.attribute;

import java.util.EventListener;

/**
 * NumberSpectrumTrend3DViewer listener
 */
public interface NumberSpectrumTrend3DViewerListener extends EventListener {

  public String getStatusLabel(NumberSpectrumTrend3DViewer src,int xIndex,int yIndex,long time,double value);

}

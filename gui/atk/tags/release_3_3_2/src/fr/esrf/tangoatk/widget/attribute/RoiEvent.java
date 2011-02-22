package fr.esrf.tangoatk.widget.attribute;

// RoiEvent.java
//
// Description:
//   Event send when selection change in the image viewer


import java.util.EventObject;
import java.awt.*;

public class RoiEvent extends EventObject {

  Rectangle roi;

  public RoiEvent(Object source, Rectangle r) {
    super(source);
    setRoi(r);
  }

  public void setRoi(Rectangle r) {
    this.roi = r;
  }

  public Rectangle getRoi() {
    return roi;
  }

  public void setSource(Object source) {
    this.source = source;
  }

  public String getVersion() {
    return "$Id$";
  }

  public Object clone() {
    if( roi!=null )  return new RoiEvent(source, new Rectangle(roi));
    else             return new RoiEvent(source, null);
  }

}

package fr.esrf.tangoatk.widget.attribute;
import java.util.EventListener;

// File:          WheelSwitchListener.java
//
// Description:
//   An interface to handle valueChanged in a WheelSwitch


public interface IRoiListener extends EventListener, java.io.Serializable {

  public void roiChange(RoiEvent evt);

}

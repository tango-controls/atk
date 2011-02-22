// File:          WheelSwitchListener.java
// 
// Description:
//   An interface to handle valueChanged in a WheelSwitch

package fr.esrf.tangoatk.widget.util;
import java.util.EventListener;

public interface IWheelSwitchListener extends EventListener, java.io.Serializable {

    public void valueChange(WheelSwitchEvent evt);

}

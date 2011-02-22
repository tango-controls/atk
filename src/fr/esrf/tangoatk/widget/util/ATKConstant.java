package fr.esrf.tangoatk.widget.util;

import java.util.Map;
import java.util.HashMap;
import java.awt.*;

/**
 * Contains various constants used in ATK.
 */
public class ATKConstant {

  private static Map qualityMap;
  private static Map stateMap;

  /** Default font used by some ATK viewers */
  public static final Font labelFont = new Font("Dialog", Font.PLAIN, 12);

  static {

    qualityMap = new HashMap();
    qualityMap.put("INVALID", Color.gray);
    qualityMap.put("ALARM", Color.orange);
    qualityMap.put("VALID", Color.green);
    qualityMap.put("UNKNOWN", Color.gray);

    stateMap = new HashMap();
    stateMap.put("ON", new java.awt.Color(0, 255, 0));          // Green
    stateMap.put("OFF", new java.awt.Color(255, 255, 255));     // White
    stateMap.put("CLOSE", new java.awt.Color(255, 255, 255));   // White
    stateMap.put("OPEN", new java.awt.Color(0, 255, 0));        // Green
    stateMap.put("INSERT", new java.awt.Color(0, 255, 0));      // Green
    stateMap.put("EXTRACT", new java.awt.Color(0, 255, 0));     // Green
    stateMap.put("MOVING", new java.awt.Color(128, 160, 255));  // Light Blue
    stateMap.put("STANDBY", new java.awt.Color(255, 255, 0));   // Yellow
    stateMap.put("FAULT", new java.awt.Color(255, 0, 0));       // Red
    stateMap.put("INIT", new java.awt.Color(204, 204, 122));    // Beige
    stateMap.put("RUNNING", new java.awt.Color(128, 160, 255)); // Light Blue
    stateMap.put("ALARM", new java.awt.Color(255, 140, 0));     // Orange
    stateMap.put("DISABLE", new java.awt.Color(255, 0, 255));   // Magenta
    stateMap.put("UNKNOWN", new java.awt.Color(155, 155, 155)); // Grey

  }

  /**
   * Return the default background color according to the given device state.
   * @param state Attribute state.
   * @return Background color.
   */
  public static Color getColor4State(String state) {
   return (Color)stateMap.get(state);
  }

  /**
   * Sets the default background color for the given device state.
   * Affects all viewer running in this JVM. If the state does not already exists,
   * a new entry in the correspondance table is created.
   * @param state value
   * @param c New color
   */
  public static void setColor4State(String state,Color c) {
    stateMap.put(state,c);
  }

  /**
   * Return the default background color according to the given attribute state.
   * @param quality Attribute quality factor (can be "INVALID","ALARM","VALID","INVALID")
   * @return Background color.
   */
  public static Color getColor4Quality(String quality) {
    return (Color)qualityMap.get(quality);
  }

  /**
   * Sets the default background color for the attribute quality factor.
   * Affects all viewer running in this JVM.
   * @param quality value (can be "INVALID","ALARM","VALID","UNKNOWN")
   * @param c New color
   */
  public static void setColor4Quality(String quality,Color c) {
    qualityMap.put(quality,c);
  }


}

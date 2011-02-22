package fr.esrf.tangoatk.widget.util;

import java.util.Map;
import java.util.HashMap;
import java.awt.*;
import javax.swing.ImageIcon;

import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IAttribute;

/**
 * Contains various constants used in ATK.
 */
public class ATKConstant {

  private static Map qualityMap;
  private static Map stateMap;

  private static Map qualityLightMap;
  private static Map stateLightMap;

  /** Default font used by some ATK viewers */
  public static final Font labelFont = new Font("Dialog", Font.PLAIN, 12);

  static {

    qualityMap = new HashMap();
    qualityMap.put(IAttribute.INVALID, Color.gray);
    qualityMap.put(IAttribute.ALARM, Color.orange);
    qualityMap.put(IAttribute.VALID, Color.green);
    qualityMap.put(IAttribute.UNKNOWN, Color.gray);
    qualityMap.put(IAttribute.WARNING, Color.orange);
    qualityMap.put(IAttribute.CHANGING, new java.awt.Color(128, 160, 255));

    stateMap = new HashMap();
    stateMap.put(IDevice.ON, new java.awt.Color(0, 255, 0));          // Green
    stateMap.put(IDevice.OFF, new java.awt.Color(255, 255, 255));     // White
    stateMap.put(IDevice.CLOSE, new java.awt.Color(255, 255, 255));   // White
    stateMap.put(IDevice.OPEN, new java.awt.Color(0, 255, 0));        // Green
    stateMap.put(IDevice.INSERT, new java.awt.Color(255, 255, 255));  // White
    stateMap.put(IDevice.EXTRACT, new java.awt.Color(0, 255, 0));     // Green
    stateMap.put(IDevice.MOVING, new java.awt.Color(128, 160, 255));  // Light Blue
    stateMap.put(IDevice.STANDBY, new java.awt.Color(255, 255, 0));   // Yellow
    stateMap.put(IDevice.FAULT, new java.awt.Color(255, 0, 0));       // Red
    stateMap.put(IDevice.INIT, new java.awt.Color(204, 204, 122));    // Beige
    stateMap.put(IDevice.RUNNING, new java.awt.Color(128, 160, 255)); // Light Blue
    stateMap.put(IDevice.ALARM, new java.awt.Color(255, 140, 0));     // Orange
    stateMap.put(IDevice.DISABLE, new java.awt.Color(255, 0, 255));   // Magenta
    stateMap.put(IDevice.UNKNOWN, new java.awt.Color(155, 155, 155)); // Grey




    qualityLightMap = new HashMap();
    qualityLightMap.put( IAttribute.INVALID, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGray.gif")) );
    qualityLightMap.put( IAttribute.ALARM, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledLightOrange.gif")) );
    qualityLightMap.put( IAttribute.VALID, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );
    qualityLightMap.put( IAttribute.UNKNOWN, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGray.gif")) );
    qualityLightMap.put( IAttribute.WARNING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledLightOrange.gif")) );
    qualityLightMap.put( IAttribute.CHANGING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif")) );


    stateLightMap = new HashMap();
    stateLightMap.put( IDevice.ON, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );         // Green
    stateLightMap.put( IDevice.OFF, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif")) );        // White
    stateLightMap.put( IDevice.CLOSE, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif")) );      // White
    stateLightMap.put( IDevice.OPEN, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );       // Green
    stateLightMap.put( IDevice.INSERT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif")) );     // White
    stateLightMap.put( IDevice.EXTRACT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );    // Green
    stateLightMap.put( IDevice.MOVING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif")) );      // Light Blue
    stateLightMap.put( IDevice.STANDBY, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledYellow.gif")) );   // Yellow
    stateLightMap.put( IDevice.FAULT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledRed.gif")) );        // Red
    stateLightMap.put( IDevice.INIT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBrownGray.gif")) );   // Beige
    stateLightMap.put( IDevice.RUNNING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif")) );     // Light Blue
    stateLightMap.put( IDevice.ALARM, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkOrange.gif")) ); // Orange
    stateLightMap.put( IDevice.DISABLE, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledPink.gif")) );     // Magenta
    stateLightMap.put( IDevice.UNKNOWN, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGray.gif")) );     // Gray

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
   * @param quality Attribute quality factor (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @return Background color.
   */
  public static Color getColor4Quality(String quality) {
    return (Color)qualityMap.get(quality);
  }

  /**
   * Sets the default background color for the attribute quality factor.
   * Affects all viewer running in this JVM.
   * @param quality value (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @param c New color
   */
  public static void setColor4Quality(String quality,Color c) {
    qualityMap.put(quality,c);
  }



  /**
   * Return the default ImageIcon according to the given device state.
   * @param state Attribute state.
   * @return ImageIcon.
   */
  public static ImageIcon getIcon4State(String state) {
   return (ImageIcon)stateLightMap.get(state);
  }

  /**
   * Sets the default ImageIcon for the given device state.
   * Affects all viewer running in this JVM. If the state does not already exists,
   * a new entry in the correspondance table is created.
   * @param state value
   * @param c New ImageIcon
   */
  public static void setIcon4State(String state,ImageIcon c) {
    stateLightMap.put(state,c);
  }

  /**
   * Return the default ImageIcon according to the given attribute state.
   * @param quality Attribute quality factor (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @return ImageIcon.
   */
  public static ImageIcon getIcon4Quality(String quality) {
    return (ImageIcon)qualityLightMap.get(quality);
  }

  /**
   * Sets the default ImageIcon for the attribute quality factor.
   * Affects all viewer running in this JVM.
   * @param quality value (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @param c New ImageIcon
   */
  public static void setIcon4Quality(String quality,ImageIcon c) {
    qualityLightMap.put(quality,c);
  }



}


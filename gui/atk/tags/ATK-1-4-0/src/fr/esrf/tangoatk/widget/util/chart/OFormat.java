//
package fr.esrf.tangoatk.widget.util.chart;

import java.awt.*;
import java.util.*;

/** Helper class for load/save graph settings. Multiple field parameters are returned in one
 * string , each field is separated by a colon */
public class OFormat {

  /**
   * Convert Color to String.
   * @param c Color to convert
   * @return A string containing color: "rrr,ggg,bbb"
   */
  public static String color(Color c) {
    return c.getRed()+","+c.getGreen()+","+c.getBlue();
  }
  /**
   * Convert Font to String
   * @param f Font to convert
   * @return A string containing the font: "Family,Style,Size"
   */
  public static String font(Font f) {
    return f.getFamily()+","+f.getStyle()+","+f.getSize();
  }

  /**
   * Convert String to String
   * @param s Input string
   * @return if s is equals to "null" return null, the given string otherwise
   */
  public static String getName(String s) {
    if( s.equalsIgnoreCase("null") )
      return null;
    else
      return s;
  }

  /**
   * Convert String to Boolean
   * @param s String to convert
   * @return true is string is "true" (case unsensitive), false otherwise
   */
  public static boolean getBoolean(String s) {
    return s.equalsIgnoreCase("true");
  }

  /**
   * Convert String to integer
   * @param s String to convert
   * @return Interger representation of the given string.
   */
  public static int getInt(String s) {
    int ret=0;    
    try {
      ret = Integer.parseInt(s);
    } catch( NumberFormatException e ) {
      System.out.println("Failed to parse" + s + "as integer");
    }      
    return ret;  
  }    

  /**
   * Convert String to double
   * @param s String to convert
   * @return Double representation of the given string.
   */
  public static double getDouble(String s) {
    double ret=0;    
    try {
      ret = Double.parseDouble(s);
    } catch( NumberFormatException e ) {
      System.out.println("Failed to parse" + s + "as double");
    }
    return ret;  
  }

  /**
   * Convert String to Color
   * @param s String to convert
   * @return Color representation of the given string.
   * @see OFormat#color
   */
  public static Color getColor(Vector v) {    
    
    int r=0,g=0,b=0;
    
    if( v.size()!=3 ) {
      System.out.println("Invalid color parameters.");
      return new Color(0,0,0);
    }
    
    try {
      r = Integer.parseInt(v.get(0).toString());
      g = Integer.parseInt(v.get(1).toString());
      b = Integer.parseInt(v.get(2).toString());
    } catch( Exception e ) {
      System.out.println("Invalid color parameters.");
    }
    
    return new Color(r,g,b);  
  }    

  /**
   * Convert String to Font
   * @param s String to convert
   * @return Font handle coresponding to the given string.
   * @see OFormat#font
   */
  public static Font getFont(Vector v) {

    String f="Dialog";
    int style=Font.PLAIN;
    int size=11;
        
    if( v.size()!=3 ) {
      System.out.println("Invalid font parameters.");
      return new Font(f,style,size);  
    }
    
    try {
      f = v.get(0).toString();
      style = Integer.parseInt(v.get(1).toString());
      size  = Integer.parseInt(v.get(2).toString());
    } catch( Exception e ) {
      System.out.println("Invalid font parameters.");
    }
    
    return new Font(f,style,size);  
  }    
  
    
}

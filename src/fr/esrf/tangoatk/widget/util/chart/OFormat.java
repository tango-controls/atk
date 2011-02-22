// Helper class for save/load settings
package fr.esrf.tangoatk.widget.util.chart;

import java.awt.*;
import java.util.*;

public class OFormat {

  public static String color(Color c) {
    return c.getRed()+","+c.getGreen()+","+c.getBlue();
  }    
  public static String font(Font f) {
    return f.getFamily()+","+f.getStyle()+","+f.getSize();
  }    

  public static String getName(String s) {
    if( s.equalsIgnoreCase("null") )
      return null;
    else
      return s;
  }    

  public static boolean getBoolean(String s) {
    return s.equalsIgnoreCase("true");
  }    

  public static int getInt(String s) {    
    int ret=0;    
    try {
      ret = Integer.parseInt(s);
    } catch( NumberFormatException e ) {
      System.out.println("Failed to parse" + s + "as integer");
    }      
    return ret;  
  }    

  public static double getDouble(String s) {    
    double ret=0;    
    try {
      ret = Double.parseDouble(s);
    } catch( NumberFormatException e ) {
      System.out.println("Failed to parse" + s + "as double");
    }
    return ret;  
  }    

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

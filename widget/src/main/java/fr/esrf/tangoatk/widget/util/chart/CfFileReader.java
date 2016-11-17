/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/**
 * CfFileReader.java
 * A config file browser
 */
package fr.esrf.tangoatk.widget.util.chart;


import java.io.*;
import java.util.*;

/**
 * A class to parse configuration file
 * @author JL Pons
 */
public class CfFileReader {

  // ----------------------------------------------------
  // Inner class Item
  // Handle one property in the config file
  // ----------------------------------------------------
  protected class Item {

    public Vector<String> items;
    String name;

    public Item(String name) {
      items = new Vector<String>();
      this.name=name;
    }

    public void addProp(String value) {
      items.add( value );
    }

    public String toString() {
      return name;
    }

  }

  // ----------------------------------------------------
  // Class variable
  // ----------------------------------------------------
  protected Vector<Item>   prop;
  protected FileReader     file;
  protected String         cfStr;
  protected char           currentChar;
  protected BufferedReader stream;

  // ----------------------------------------------------
  // General constructor
  // ----------------------------------------------------
  public CfFileReader() {
    prop  = new Vector<Item>();
    cfStr = null;
    file  = null;
    stream = null;
  }

  // ----------------------------------------------------
  // Get the current char
  // ----------------------------------------------------
  protected char getCurrentChar() throws IOException {

    char c;

    if( file!=null ) {
      return (char)file.read();
      
    } else if( cfStr!=null ) {
      c = (char)cfStr.charAt(0);
      cfStr = cfStr.substring(1);
      return c;
      
    } else if( stream != null){
    	return (char)stream.read();
    }

    return (char)0;
  }

  // ----------------------------------------------------
  // Return true when EOF
  // ----------------------------------------------------
  protected boolean eof() throws IOException {

    if( file!=null ) {
      return !file.ready();
      
    } else if( cfStr!=null ) {
      return cfStr.length()==0;
      
    } else if ( stream != null) {
    	
    	return !stream.ready();
	}
    
    return true;
  }

  // ----------------------------------------------------
  // Read the file word by word
  // ----------------------------------------------------
  protected String readWord() throws IOException {

    boolean found=(currentChar>32);
    String  ret = "";

    // Jump space
    while( !eof() && !found ) {
      currentChar = getCurrentChar();
      found = (currentChar>32);
    }

    if( !found ) return null;

    // Treat strings
    if( currentChar=='\'' ) {

      found=false;
      while( !eof() && !found ) {
        currentChar = getCurrentChar();
        found = (currentChar=='\'');
	if( !found ) ret += currentChar;
      }

      if( !found ) {
         System.out.println("CfFileReader.parse: '\'' is missing");
	 return null;
      }

      //System.out.println("ReadWord:" + ret);
      currentChar = getCurrentChar();
      return ret;
    }

    // Read the next word
    ret += currentChar;
    if( (currentChar==',') || (currentChar==':') ) {
      currentChar = getCurrentChar();
      //System.out.println("ReadWord:" + ret);
      return ret;
    } else {
      found = false;
      while( !eof() && !found ) {
        currentChar = getCurrentChar();
        found = (currentChar==',') || (currentChar==':') || (currentChar<=32);
        if( !found) ret += currentChar;
      }
      //System.out.println("ReadWord:" + ret);
      return ret;
    }

  }

  // ----------------------------------------------------
  // Read the config file and fill properties vector
  // Return true when succesfully browsed
  // ----------------------------------------------------
  protected boolean parse() throws IOException {

    prop.clear();
    currentChar=0;
    String word=readWord();
    boolean sameItem;

    while(word!=null) {

         // Create new item
	 Item it = new Item(word);

	 // Jump ':'
         word = readWord();

	 if( !word.equals(":") ) {
	   System.out.println("CfFileReader.parse: ':' expected instead of " + word);
	   return false;
	 }

	 // Read values

	 sameItem = true;
	 while( sameItem ) {
	   sameItem = false;
	   word = readWord();
	   if( word!=null ) it.addProp( word );
	   word = readWord();
	   if( word!=null ) sameItem = word.equals(",");
	 }

	 prop.add( it );

    }

    return true;
  }

  /**
  * Parse the given string and fill property vector.
  * @param text String containing text to parse
  * @return Return true when text succesfully parsed
  */

  public boolean parseText(String text) {
    boolean ok=false;
    try {
      cfStr=text;
      ok = parse();
    } catch ( Exception e ) {
    }

    return ok;
  }

  /**
  * Parse the given file and fill property vector.
  * @param filename File to parse
  * @return Return true when file succesfully parsed
  */

  public boolean readFile(String filename) {
    boolean ok=false;
    try {
      file = new FileReader(filename);
      ok = parse();
      file.close();
    } catch ( Exception e ) {
    }

    return ok;
  }
  /**
   * Parse the given file and fill property vector.
   * @param filename File to parse
   * @return Return true when file succesfully parsed
   */

   public boolean readFile(File file) {
     boolean ok=false;
     try {
       this.file = new FileReader(file);
       ok = parse();
       this.file.close();
     } catch ( Exception e ) {
     }

     return ok;
   }

   public boolean readStream(BufferedReader stream) {
	     boolean ok=false;
	     try {
	       this.stream = stream;
	       ok = parse();
	       this.stream.close();
	     } catch ( Exception e ) {
	     }
	     return ok;
	   }

  /**
  * Return all parameter names found in the config file.
  * @return Returns a vector of String.
  */

   public Vector<String> getNames() {

    Vector<String> v = new Vector<String>();
    for( int i=0;i<prop.size();i++ ) {
      v.add( prop.get(i).toString() );
    }
    return v;

  }

  /**
  * Return parameter value, one parameter can have multiple fields seperated by a colon.
  * @param name Parameter name
  * @return Returns a vector of String. (1 string per field)
  */

  public Vector<String> getParam(String name) {
    boolean found=false;
    int i=0;

    while( !found && i<prop.size() ) {
      found = name.equals( prop.get(i).toString() );
      if(!found) i++;
    }
    if( found ) {
      Item it = (Item)prop.get(i);
      return it.items;
    } else {
      return null;
    }

  }

  // ----------------------------------------------------
  public static void main(String args[]) {
    final CfFileReader cf = new CfFileReader();

    if( cf.readFile("test.cfg") ) {

	  Vector<String> names = cf.getNames();
      System.out.println("Read " + names.size() +" params");

	  for(int i=0;i<names.size();i++) {
	    System.out.println( names.get(i).toString() );
	    Vector<String> values = cf.getParam( names.get(i).toString() );
	    for(int j=0;j<values.size();j++) {
	      System.out.println( "   " + values.get(j).toString() );
	    }
	  }

    } else {
      System.out.println("Error while reading config file");
    }
  }

}

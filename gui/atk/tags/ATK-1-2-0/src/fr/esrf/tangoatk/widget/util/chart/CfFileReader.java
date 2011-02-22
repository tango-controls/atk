/*
 * CfFileReader.java
 * A config file brower
 */

package fr.esrf.tangoatk.widget.util.chart;

import java.io.*;
import java.util.*;

public class CfFileReader {

  // ----------------------------------------------------
  // Inner class Item
  // Handle one property in the config file
  // ----------------------------------------------------
  class Item {
    
    public Vector items;
    String name;
    
    public Item(String name) {
      items = new Vector();
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
  Vector     prop;
  FileReader file;
  char       currentChar;

  // ----------------------------------------------------
  // General constructor  
  // ----------------------------------------------------
  public CfFileReader() {
    prop = new Vector();
  }
  
  // ----------------------------------------------------
  // Read the config file and fill properties vector
  // Return true when succesfully browsed  
  // ----------------------------------------------------
  public boolean readFile(String filename) {
    
    prop.clear();
    currentChar=0;
    String word;
    boolean sameItem;
    
    try {    
    
      file = new FileReader(filename);
      word = readWord(file);
      
      while(word!=null) {
      
         // Create new item
	 Item it = new Item(word);

	 // Jump ':'
         word = readWord(file);
	 
	 if( !word.equals(":") ) {
	   System.out.println(": expected instead of " + word);
	   file.close();
	   return false;
	 }	 
	 
	 // Read values
	 
	 sameItem = true;
	 while( sameItem ) {	 
	   sameItem = false;	   
	   word = readWord(file);	   
	   if( word!=null ) it.addProp( word );
	   word = readWord(file);	   
	   if( word!=null ) sameItem = word.equals(",");	   	   
	 }
	 
	 prop.add( it );

      }
      
      file.close();      
    
    } catch ( Exception e ) {
      return false;
    }
    
    return true;
  }
  
  // ----------------------------------------------------
  // Read file word by word
  // ----------------------------------------------------
  String readWord(FileReader f) throws IOException {
  
    boolean found=(currentChar>=32);
    String  ret = "";
         
    // Go to the next word
    while( f.ready() && !found ) {
      currentChar = (char)f.read();
      found = (currentChar>=32);
    }
    
    if( !found ) {
      return null;
    } else {
    
      // Read the next word
      ret += currentChar;
      if( (currentChar==',') || (currentChar==':') ) {
        currentChar = (char)f.read();
	return ret;
      } else {
        found = false;
        while( f.ready() && !found ) {
          currentChar = (char)f.read();
          found = (currentChar==',') || (currentChar==':') || (currentChar<32);
          if( !found) ret += currentChar;
        }
        return ret;
      } 
       
    }
  
  }
  
  // ----------------------------------------------------
  // Return all parameters found in the config file 
  // ----------------------------------------------------
  public Vector getNames() {
    
    Vector v = new Vector();
    for( int i=0;i<prop.size();i++ ) {
      v.add( prop.get(i).toString() );
    }
    return v;
            
  }

  // ----------------------------------------------------
  // Return parameter value
  // ----------------------------------------------------
  public Vector getParam(String name) {    
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

  public static void main(String args[]) {
      final CfFileReader cf = new CfFileReader();
      
      if( cf.readFile("test.cfg") ) {
        
	Vector names = cf.getNames();
        System.out.println("Read " + names.size() +" params");
	
	for(int i=0;i<names.size();i++) {
	  System.out.println( names.get(i).toString() );
	  Vector values = cf.getParam( names.get(i).toString() );
	  for(int j=0;j<values.size();j++) {
	    System.out.println( "   " + values.get(j).toString() );
	  }	  
	}
      
      } else {
        System.out.println("Error while reading config file");      
      }
  }

}

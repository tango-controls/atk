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
 
package fr.esrf.tangoatk.widget.util.interlock;

import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import java.awt.*;

// ------------------------------------------------------------------
// Link Class
// ------------------------------------------------------------------

class XpssLink {

  public static final int LINK_INPUT = 1;
  public static final int LINK_OUTPUT = 2;

  public int type;
  public Point pos;

  XpssLink(int t, int x, int y) {
    type = t;
    pos = new Point(x, y);
  }

}

// ------------------------------------------------------------------
// XpssObject Class
// ------------------------------------------------------------------

class XpssObject {

  public static final int OBJECT_NONE = 0;
  public static final int OBJECT_BUBBLE = 1;
  public static final int OBJECT_SPLIT = 2;
  public static final int OBJECT_JOIN = 3;
  public static final int OBJECT_STARTPOINT = 4;
  public static final int OBJECT_ENDPOINT = 5;
  public static final int OBJECT_CORNER = 6;
  public static final int OBJECT_TEXT = 7;
  public static final int OBJECT_PERMIT = 8;

  public int type;
  public Point org;
  String label1;
  String label2;
  String typeStr;
  String permitFileName;
  public int id;
  public int idHigh;
  public int idLow;
  public int idDev;
  Vector links;
  String retLabel;
  int lx;
  int ly;

  /* List comming from the old xpss application */

  static String xpssDevices[] = {
                "ELIN/ELIN/ELIN",       /* crate zero is NEVER used */
                "ELIN/SA-CR1/SL0",
                "ELIN/SA-CR2/SL0",
                "ELIN/SA-CR3/SL0",
                "ELIN/INTLK/0",         /* this crate may be used one day */
                "SY/SA-CR5/SL0",
                "SY/SA-CR6/SL0",
                "SY/SA-CR7/SL0",
                "SY/SA-CR8/SL0",
                "SR/SA-CR9/SL0",
                "ELIN/ELIN/ELIN",       /* this crate may be used one day */
                "SR/SA-CR11/SL0",
                "ELIN/ELIN/ELIN",       /* this crate may be used one day */
                "SR/SA-CR13/SL0",
                };

  static String bubbleExts[] = {
    "Device",
    "Board",
    "Relay",
    "Type",
    "Net Name"
  };

  static String permitExts[] = {
    "Type",
    "Net Name"
  };

  XpssObject() {
    type = OBJECT_NONE;
    org = new Point();
    links = new Vector();
    id = 0;
    label1 = "";
    label2 = "";
    typeStr = "";
    permitFileName = "";
  }

  void addLink(XpssLink l) {

    // Add only output link
    if (l.type == XpssLink.LINK_OUTPUT)
      links.add(l);

  }

  void convertLabels() {

    retLabel = "";

    if (label1.length() == 0) {

      if (label2.length() != 0) {
        retLabel += label2;
      }

    } else {

      if (label2.length() == 0) {

        retLabel += label1;
        switch (type) {
          case XpssObject.OBJECT_PERMIT:
            lx = 17;
            ly = 13;
            break;
          case XpssObject.OBJECT_BUBBLE:
            lx = 0;
            ly = -12;
            break;
          case XpssObject.OBJECT_STARTPOINT:
            lx = 0;
            ly = 8;
            break;
        }

      } else {
        retLabel += label1 + "\n" + label2;
        if (type != XpssObject.OBJECT_PERMIT) {
          lx = 0;
          ly = -12;
        } else {
          lx = 17;
          ly = 13;
        }
      }

    }

  }

  // Convert XpssObject to NetObject
  NetObject convert(Dimension gridSize) {

    NetObject no = null;

    switch (type) {

      case XpssObject.OBJECT_BUBBLE:
        no = new NetObject(NetObject.OBJECT_BUBBLE,1,1,1, org.x, org.y);
        convertLabels();
        no.setLabel(retLabel);
        no.labelOffset.x = lx;
        no.labelOffset.y = ly;
        no.setExtensionList(bubbleExts);
        no.setExtendedParam(0,xpssDevices[idDev]);
        no.setExtendedParam(1,Integer.toString(idHigh));
        no.setExtendedParam(2,Integer.toString(idLow));
        no.setExtendedParam(3,typeStr);
        no.setExtendedParam(4,permitFileName);
        break;

      case XpssObject.OBJECT_SPLIT:
      case XpssObject.OBJECT_JOIN:
      case XpssObject.OBJECT_CORNER:
        no = new NetObject(NetObject.OBJECT_BUBBLE,5,1000,1000, org.x, org.y);
        no.setShape(NetShape.SHAPE_DOT);
        break;

      case XpssObject.OBJECT_STARTPOINT:
        no = new NetObject(NetObject.OBJECT_BUBBLE,3,0,1, org.x, org.y);
        convertLabels();
        no.setLabel(retLabel);
        no.labelOffset.x = lx;
        no.labelOffset.y = ly;
        no.setShape(NetShape.SHAPE_VCC);
        break;

      case XpssObject.OBJECT_ENDPOINT:
        no = new NetObject(NetObject.OBJECT_BUBBLE,4,1,0, org.x, org.y);
        no.setShape(NetShape.SHAPE_GROUND);
        break;

      case XpssObject.OBJECT_TEXT:
        no = new NetObject(NetObject.OBJECT_TEXT,0,0,0, org.x*gridSize.width, org.y*gridSize.height-3);
        convertLabels();
        no.setLabel(retLabel);
        no.labelOffset.x = lx;
        no.labelOffset.y = ly-2;
        no.justify = NetObject.JUSTIFY_LEFT;
        break;

      case XpssObject.OBJECT_PERMIT:
        no = new NetObject(NetObject.OBJECT_BUBBLE,2,1,1, org.x, org.y);
        convertLabels();
        no.setLabel(retLabel);
        no.labelOffset.x = lx;
        no.labelOffset.y = ly;
        no.setExtensionList(permitExts);
        no.setExtendedParam(0,typeStr);
        no.setExtendedParam(1,permitFileName);
        no.justify = NetObject.JUSTIFY_LEFT;
        no.setShape(NetShape.SHAPE_SQUARE);
        no.setSize(9);
        break;

      default:
        System.out.println("Warning: Unknown object detected. Ignoring...");
        return null;

    }

    return no;

  }

}

// ------------------------------------------------------------------
// Xpss File loader
// ------------------------------------------------------------------

class XpssFileLoader {

  /* Lexical coce */

  private static final int NUMBER = 1;
  private static final int STRING = 2;
  private static final int SLASH = 3;
  private static final int BEGIN_KW = 4;
  private static final int END_KW = 5;
  private static final int LINK_KW = 6;
  private static final int OBJECT_KW = 7;

  private final String[] lexical_word = {
    "NULL",
    "NUMBER",
    "STRING",
    "SLASH",
    "Begin",
    "End",
    "Link",
    "Object"
  };

  private int CrtLine;
  private int StartLine;
  private char CurrentChar;

  private String word;
  FileReader f;

  public XpssFileLoader(FileReader file) throws IOException {
    f = file;
    CrtLine = 1;
    CurrentChar = ' ';
  }

  private void read_char(FileReader f) throws IOException {

    if (f.ready())
      CurrentChar = (char) f.read();
    else
      CurrentChar = 0;
    if (CurrentChar == '\n') CrtLine++;
  }

  private void jump_space(FileReader f) throws IOException {

    while (CurrentChar <= 32 && CurrentChar > 0) read_char(f);
  }

  private String read_word(FileReader f) throws IOException {

    String ret_word = "";

    /* Jump space */
    jump_space(f);

    /* Treat special character */
    if (CurrentChar == '/') {
      ret_word += CurrentChar;
      read_char(f);
      return ret_word;
    }
    StartLine = CrtLine;

    /* Treat other word */
    while (CurrentChar > 32 && CurrentChar != '/') {
      ret_word += CurrentChar;
      read_char(f);
    }

    if (ret_word.length() == 0) {
      return null;
    }

    return ret_word;
  }

  private String read_full_line(FileReader f) throws IOException {

    String ret_word = "";

    StartLine = CrtLine;

    /* Go to the begining of the next line */
    while (CurrentChar != '\n' && CurrentChar != 0) {
      read_char(f);
    }

    if (CurrentChar != 0)
      read_char(f);

    /* Read the full line */
    while (CurrentChar >= 32) {
      ret_word += CurrentChar;
      read_char(f);
    }

    return ret_word;
  }

  private boolean isNumber(String s) {
    boolean ok = true;
    for (int i = 0; i < s.length() && ok; i++) {
      char c = s.charAt(i);
      ok = ok & ((c >= '0' && c <= '9') || c == '.' || c == 'e' || c == 'E' || c == '-');
    }
    return ok;
  }

  private int class_lex(String word) {

    /* exepction */

    if (word == null) return 0;
    if (word.length() == 0) return STRING;

    /* Keyword */

    if (word.equals("/")) return SLASH;
    if (word.equalsIgnoreCase("begin")) return BEGIN_KW;
    if (word.equalsIgnoreCase("end")) return END_KW;
    if (word.equalsIgnoreCase("link")) return LINK_KW;
    if (word.equalsIgnoreCase("object")) return OBJECT_KW;

    /* Number */

    if (isNumber(word)) return NUMBER;

    /* Other word */

    return STRING;
  }

  private void CHECK_LEX(int lt, int le) throws IOException {
    if (lt != le)
      throw new IOException("Invalid syntyax at line " + StartLine + ", " + lexical_word[le] + " expected");
  }

  public int getCurrentLine() {
    return StartLine;
  }

  public void startBlock() throws IOException {
    CHECK_LEX(class_lex(word), BEGIN_KW);
    word = read_word(f);
    CHECK_LEX(class_lex(word), OBJECT_KW);
    word = read_word(f);
  }

  public void endBlock() throws IOException {
    CHECK_LEX(class_lex(word), END_KW);
    word = read_word(f);
    CHECK_LEX(class_lex(word), OBJECT_KW);
    word = read_word(f);
  }

  public boolean isEndBlock() {
    return class_lex(word) == END_KW;
  }

  public XpssLink parseLink() throws IOException {
    int t,x,y;
    CHECK_LEX(class_lex(word), LINK_KW);
    word = read_word(f);
    t = parseInteger();
    x = parseInteger();
    y = parseInteger();
    // Jump the n value
    parseInteger();
    return new XpssLink(t, x, y);
  }

  public XpssLink parseLastLink() throws IOException {
    int t,x,y;
    CHECK_LEX(class_lex(word), LINK_KW);
    word = read_word(f);
    t = parseInteger();
    x = parseInteger();
    y = parseInteger();
    return new XpssLink(t, x, y);
  }

  int parseInteger() throws IOException {
    CHECK_LEX(class_lex(word), NUMBER);
    int ret = 0;
    try {
      ret = Integer.parseInt(word);
    } catch (NumberFormatException e) {
      throw new IOException("Invalid number at line " + StartLine);
    }
    word = read_word(f);
    return ret;
  }

  public String parseString() throws IOException {
    CHECK_LEX(class_lex(word), STRING);
    String s = word;
    word = read_word(f);
    return s;
  }

  public XpssObject parseObject() throws IOException {

    XpssObject o = new XpssObject();

    // parse header
    o.type = parseInteger();
    o.org.x = parseInteger();
    o.org.y = parseInteger();

    // parse links
    o.addLink(parseLink());
    o.addLink(parseLink());
    o.addLink(parseLastLink());

    // parse object params
    switch (o.type) {

      case XpssObject.OBJECT_BUBBLE:

        /* Parse labels */
        o.label1 = read_full_line(f);
        o.label2 = read_full_line(f);
        o.typeStr = read_full_line(f);
        word = read_word(f);

        o.idHigh = parseInteger();

        /* Jump the slash */
        CHECK_LEX(class_lex(word), SLASH);
        word = read_word(f);

        o.idLow = parseInteger();
        o.idDev = parseInteger();
        break;

      case XpssObject.OBJECT_TEXT:

        /* Parse label */
        o.label1 = read_full_line(f);
        word = read_word(f);
        break;

      case XpssObject.OBJECT_PERMIT:

        /* Parse labels */
        o.label1 = read_full_line(f);
        o.label2 = read_full_line(f);
        o.typeStr = read_full_line(f);
        o.permitFileName = read_full_line(f);
        word = read_word(f);
        break;

      case XpssObject.OBJECT_STARTPOINT:

        o.label1 = "24V";
        word = read_word(f);
        break;

        /* No params */
      default:
        word = read_word(f);

    }

    return o;

  }

  private int findWithCoord(Vector objects, int x, int y) {

    int i = 0;
    boolean found = false;
    XpssObject o = null;
    int sz = objects.size();

    // Find root object
    while (i < sz && !found) {
      o = (XpssObject) objects.get(i);
      found = (o.org.x == x) && (o.org.y == y);
      if (!found) i++;
    }

    if (found)  return i;
    else        return -1;

  }

  // ****************************************************
  // Parse an xpss file
  // ****************************************************
  public Vector parseXpssFile(Dimension gridSize) throws IOException {

    boolean eof = false;
    int j,i,idx,lex;
    Vector  objects = new Vector();  // Vector of Xpss object
    Vector iobjects = new Vector();  // Vector of converted NetObject
    NetObject  no;

    // CHECK BEGINING OF FILE
    word = read_word(f);
    if (word == null) throw new IOException("File empty !");
    lex = class_lex(word);

    // PARSE
    while (!eof) {
      switch (lex) {
        case BEGIN_KW:
          startBlock();
          XpssObject p = parseObject();
          if (p != null) {
            if( p.org.x<0 || (p.org.x*gridSize.width)>=1800 ||
                p.org.y<0 || (p.org.y*gridSize.height)>=1600 ) {
              System.out.println("Warning:Out of bounds object detected. Ignoring...");
            } else {
              objects.add(p);
              no = p.convert(gridSize);
              if( no!=null ) iobjects.add(no);
            }
          }
          endBlock();
          break;
        default:
          throw new IOException("Invalid syntyax at line " + StartLine + ": 'Begin' expected.");
      }
      lex = class_lex(word);
      eof = (word == null);
    }

    // Build the children list
    XpssObject  o;
    XpssLink    l;
    for (i = 0; i < objects.size(); i++) {

      o  = (XpssObject) objects.get(i);
      no = (NetObject) iobjects.get(i);

      for (j = 0; j < o.links.size(); j++) {
        l = (XpssLink) o.links.get(j);
        idx = findWithCoord(objects,l.pos.x, l.pos.y);
        if (idx != -1) {
          no.addChild((NetObject)iobjects.get(idx));
        } else {
          System.out.println("Warning: Not ended link detected. Ignoring...");
        }
      }

    }

    // Return objects
    objects.clear();
    return iobjects;

  }
}

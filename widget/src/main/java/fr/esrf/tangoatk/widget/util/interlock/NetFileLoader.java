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
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */


package fr.esrf.tangoatk.widget.util.interlock;

import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.awt.*;

/** A class to load net file. */
public class NetFileLoader {

/* Lexical coce */

  private final static int NUMBER = 1;
  private final static int STRING = 2;
  private final static int COMA = 3;
  private final static int COLON = 4;
  private final static int OPENBRACE = 5;
  private final static int CLOSEBRACE = 6;
  private final static int OPENBRACKET = 7;
  private final static int CLOSEBRACKET = 8;
  private final static int BUBBLE_KW = 9;
  private final static int TEXT_KW = 10;
  private final static int LABEL_KW = 11;
  private final static int CHILDREN_KW = 12;
  private final static int LABELOFFSET_KW = 13;
  private final static int JUSTIFY_KW = 14;
  private final static int EXTENSIONS_KW = 15;
  private final static int SIZE_KW = 16;
  private final static int SHAPE_KW = 17;
  private final static int EDSHAPE_KW = 18;
  private final static int FONT_KW = 19;

  private final static int NETFILE_KW = 20;
  private final static int GLOBALPARAM_KW = 21;
  private final static int LABELFONT_KW = 22;
  private final static int SMALLFONT_KW = 23;
  private final static int USEAAFONT_KW = 24;
  private final static int DRAWARROW_KW = 25;

  private final String[] lexical_word = {
    "NULL",
    "NUMBER",
    "STRING",
    ",",
    ":",
    "{",
    "}",
    "(",
    ")",
    "Bubble",
    "Text",
    "label",
    "children",
    "label_offset",
    "justify",
    "extensions",
    "size",
    "shape",
    "editable_shape",
    "font",
    "NetFile",
    "GlobalParam",
    "label_font",
    "small_font",
    "use_aa_font",
    "draw_arrow"
  };

  private FileReader f;
  private String fileName;
  private int CrtLine;
  private int StartLine;
  private char CurrentChar;
  private char NextChar;
  private boolean firstWarning;
  private int objIdx;

  private String word;

// Global param section

  private Font smallFont = NetEditor.defaultSmallFont;
  private Font labelFont = NetEditor.defaultLabelFont;
  private boolean useAAFont = false;
  private boolean drawArrow = true;

  /**
   * Contruct a NetFileLoader.
   * @param file Handle the the file reader.
   * @param fileName Used to print warning message if any.
   * @throws IOException In case of failure
   */
  public NetFileLoader(FileReader file, String fileName) throws IOException {
    CrtLine = 1;
    NextChar = ' ';
    CurrentChar = ' ';
    firstWarning = true;
    this.fileName = fileName;
    f = file;
  }

  /**
   * Contruct a NetFileLoader.
   * @param file Handle the the file reader.
   * @param fileName Used to print warning message if any.
   * @param startLine Start line number for error message.
   * @throws IOException In case of failure
   */
  public NetFileLoader(FileReader file, String fileName, int startLine) throws IOException {
    CrtLine = startLine;
    NextChar = ' ';
    CurrentChar = ' ';
    firstWarning = true;
    this.fileName = fileName;
    f = file;
  }


  /** read the next character in the file */
  private void read_char() throws IOException {

    CurrentChar = NextChar;
    if (f.ready())
      NextChar = (char) f.read();
    else
      NextChar = 0;
    if (CurrentChar == '\n') CrtLine++;
  }

// -------------------------------------------------------
// Go to the next significant character
// -------------------------------------------------------
  private void jump_space() throws IOException {
    while (CurrentChar <= 32 && CurrentChar > 0) read_char();
  }

// -------------------------------------------------------
// Read the next word in the file
// -------------------------------------------------------
  private String read_word() throws IOException {

    StringBuffer ret_word = new StringBuffer();

    /* Jump space and comments */
    jump_space();

    StartLine = CrtLine;

    /* Treat special character */
    if (CurrentChar == ':' || CurrentChar == '{' || CurrentChar == '}' ||
            CurrentChar == ',' || CurrentChar == '(' || CurrentChar == ')') {
      ret_word.append(CurrentChar);
      read_char();
      return ret_word.toString();
    }

    /* Treat string */
    if (CurrentChar == '"') {
      read_char();
      while (CurrentChar != '"' && CurrentChar != 0 && CurrentChar != '\n') {
        ret_word.append(CurrentChar);
        read_char();
      }
      if (CurrentChar == 0 || CurrentChar == '\n') {
        IOException e = new IOException("String too long at line " + StartLine);
        throw e;
      }
      read_char();
      return ret_word.toString();
    }

    /* Treat other word */
    while (CurrentChar > 32 && CurrentChar != '{' && CurrentChar != '}'
            && CurrentChar != ',' && CurrentChar != ':' && CurrentChar != '(' && CurrentChar != ')') {
      ret_word.append(CurrentChar);
      read_char();
    }

    if (ret_word.length() == 0) {
      return null;
    }

    return ret_word.toString();
  }

// -------------------------------------------------------
// return the lexical classe of the next word
// -------------------------------------------------------
  private int class_lex(String word) {

    int i = COMA;
    boolean found = false;

    /* exepction */

    if (word == null) return 0;
    if (word.length() == 0) return STRING;

    /* Find keyword */

    while (i < lexical_word.length && !found) {
      found = word.equalsIgnoreCase(lexical_word[i]);
      if (!found) i++;
    }

    if (found)
      return i;

    /* Number */

    char c = word.charAt(0);
    if ((c >= '0' && c <= '9') || (c == '-')) return NUMBER;

    /* String */
    return STRING;
  }

// -------------------------------------------------------
// Check lexical word
// -------------------------------------------------------
  private void CHECK_LEX(int lt, int le) throws IOException {
    if (lt != le)
      throw new IOException("Error at line " + StartLine + ", '" + lexical_word[le] + "' expected");
  }

// -------------------------------------------------------
// Jump a specific lexem
// -------------------------------------------------------
  private void jump_lexem(int l) throws IOException {
    CHECK_LEX(class_lex(word), l);
    word = read_word();
  }

// -------------------------------------------------------
// Parse an integer
// -------------------------------------------------------
  private int parseInteger() throws IOException {
    CHECK_LEX(class_lex(word), NUMBER);
    int ret = 0;
    try {
      ret = Integer.parseInt(word);
    } catch (NumberFormatException e) {
      throw new IOException("Invalid number at line " + StartLine);
    }
    word = read_word();
    return ret;
  }

// -------------------------------------------------------
// Parse a param string
// -------------------------------------------------------
  private String parseParamString() throws IOException {
    String ret = word;
    int lex = class_lex(word);
    // Get the string
    // (interpret number as string in param list)
    // (interpret kw as string in param list)
    if (lex != STRING && lex != NUMBER && lex < BUBBLE_KW)
      throw new IOException("Error at line " + StartLine + ", '" + lexical_word[NUMBER] + "' or '" + lexical_word[STRING] + "' expected");
    word = read_word();
    return ret;
  }

// -------------------------------------------------------
// Parse an integer list
// -------------------------------------------------------
  private int[] parseIntegerList() throws IOException {

    boolean eol = false;
    Vector tmpResult = new Vector();
    int[] result;

    do {

      // Get the int
      tmpResult.add(new Integer(parseInteger()));

      eol = class_lex(word) != COMA;
      // Jump the coma
      if (!eol) word = read_word();

    } while (!eol);

    result = new int[tmpResult.size()];
    for (int i = 0; i < tmpResult.size(); i++) result[i] = ((Integer) tmpResult.get(i)).intValue();

    return result;

  }

// -------------------------------------------------------
// Parse a string list
// -------------------------------------------------------
  private String[] parseStringList() throws IOException {

    boolean eol = false;
    Vector tmpResult = new Vector();
    String[] result;

    do {

      tmpResult.add(parseParamString());
      eol = class_lex(word) != COMA;
      // Jump the coma
      if (!eol) word = read_word();

    } while (!eol);

    result = new String[tmpResult.size()];
    for (int i = 0; i < tmpResult.size(); i++) result[i] = (String) tmpResult.get(i);

    return result;

  }

// -------------------------------------------------------
// Parse a Font
// -------------------------------------------------------
  private Font parseFont() throws IOException {

    String fntName;
    int fntType;
    int fntSize;

    fntName = word;
    word = read_word();
    jump_lexem(COMA);
    fntType = parseInteger();
    jump_lexem(COMA);
    fntSize = parseInteger();

    return new Font(fntName, fntType, fntSize);

  }

// -------------------------------------------------------
// Parse the extension block
// -------------------------------------------------------
  private void parseObjectExtension(NetObject o) throws IOException {

    int lex = class_lex(word);
    Vector extN = new Vector();
    Vector extV = new Vector();

    while (word != null && lex != CLOSEBRACE) {

      extN.add(parseParamString());
      jump_lexem(COLON);
      extV.add(parseParamString());
      lex = class_lex(word);

    }

    int sz = extN.size();
    int i;

    if (sz > 0) {
      String[] strs = new String[sz];
      for (i = 0; i < sz; i++)
        strs[i] = (String) (extN.get(i));
      o.setExtensionList(strs);
      for (i = 0; i < sz; i++)
        o.setExtendedParam(i, (String) (extV.get(i)));
    }

  }

// -------------------------------------------------------
// Parse object params
// -------------------------------------------------------
  private void parseObjectParam(NetObject o) throws IOException {

    int lex = class_lex(word);

    while (word != null && lex != CLOSEBRACE) {

      switch (lex) {

        case LABEL_KW: // label
          jump_lexem(LABEL_KW);
          jump_lexem(COLON);
          o.setLabel(parseStringList());
          break;

        case LABELOFFSET_KW: // label offsets
          jump_lexem(LABELOFFSET_KW);
          jump_lexem(COLON);
          int[] off = parseIntegerList();
          if (off.length != 2)
            throw new IOException("Error at line " + StartLine + ", 2 values expected for label_offset");
          o.labelOffset.x = off[0];
          o.labelOffset.y = off[1];
          break;

        case JUSTIFY_KW: // Justification
          jump_lexem(JUSTIFY_KW);
          jump_lexem(COLON);
          o.justify = parseInteger();
          break;

        case SIZE_KW: // Bubble size
          jump_lexem(SIZE_KW);
          jump_lexem(COLON);
          o.bSize = parseInteger();
          break;

        case SHAPE_KW: // Bubble shape
          jump_lexem(SHAPE_KW);
          jump_lexem(COLON);
          o.shape = parseInteger();
          break;

        case EDSHAPE_KW: // editable shape
          jump_lexem(EDSHAPE_KW);
          jump_lexem(COLON);
          o.editableShape = (parseInteger() == 1);
          break;

        case FONT_KW: // Text font
          jump_lexem(FONT_KW);
          jump_lexem(COLON);
          o.setTextFont(parseFont());
          break;

        case CHILDREN_KW: // Children list
          jump_lexem(CHILDREN_KW);
          jump_lexem(COLON);
          o.setChildrenIdList(parseIntegerList());
          break;

        case EXTENSIONS_KW: // extensions
          jump_lexem(EXTENSIONS_KW);
          jump_lexem(OPENBRACE);
          parseObjectExtension(o);
          jump_lexem(CLOSEBRACE);
          break;

        default:
          if (lex != CLOSEBRACE) {
            warningMessage("Warning: Unknown param name '" + word + "' at line " + CrtLine + "... Ignoring.");
            jump_lexem(STRING);
            jump_lexem(COLON);
            parseStringList();
          }
      }

      lex = class_lex(word);

    }

  }

// -------------------------------------------------------
// Parse an object
// -------------------------------------------------------
  private NetObject parseObject(int type) throws IOException {

    int x,y,maxI = 0,maxO = 0,uType;

    // Jump the type
    word = read_word();

    // User type
    uType = parseInteger();

    // Coordinates
    jump_lexem(OPENBRACKET);
    x = parseInteger();
    jump_lexem(COMA);
    y = parseInteger();
    jump_lexem(CLOSEBRACKET);

    if (type == NetObject.OBJECT_BUBBLE) {
      // input & ouput max number
      maxI = parseInteger();
      jump_lexem(COMA);
      maxO = parseInteger();
    }

    NetObject ret = new NetObject(type, uType, maxI, maxO, x, y);
    ret.setIndex(objIdx);
    objIdx++;

    // Params
    jump_lexem(OPENBRACE);
    parseObjectParam(ret);
    jump_lexem(CLOSEBRACE);

    return ret;

  }

// -------------------------------------------------------
// Parse global param block
// -------------------------------------------------------
  private void parseGlobalParam() throws IOException {

    jump_lexem(GLOBALPARAM_KW);
    jump_lexem(OPENBRACE);

    int lex = class_lex(word);

    while (word != null && lex != CLOSEBRACE) {

      switch (lex) {

        case LABELFONT_KW:
          jump_lexem(LABELFONT_KW);
          jump_lexem(COLON);
          labelFont = parseFont();
          break;

        case SMALLFONT_KW:
          jump_lexem(SMALLFONT_KW);
          jump_lexem(COLON);
          smallFont = parseFont();
          break;

        case USEAAFONT_KW:
          jump_lexem(USEAAFONT_KW);
          jump_lexem(COLON);
          useAAFont = (parseInteger() == 1);
          break;

        case DRAWARROW_KW:
          jump_lexem(DRAWARROW_KW);
          jump_lexem(COLON);
          drawArrow = (parseInteger() == 1);
          break;

        default:
          if (lex != CLOSEBRACE) {
            warningMessage("Warning: Unknown global param name '" + word + "' at line " + CrtLine + "... Ignoring.");
            jump_lexem(STRING);
            jump_lexem(COLON);
            parseStringList();
          }
      }

      lex = class_lex(word);

    }


    jump_lexem(CLOSEBRACE);

  }

  /**
   * Parse a network file (net format)
   * @return NetObject vector
   * @throws IOException Contains error message
   */
  public Vector parseNetFile() throws IOException {

    boolean eof = false;
    int i,j,lex;
    Vector objects = new Vector();
    objIdx = 0;

    /* CHECK BEGINING OF FILE  */
    word = read_word();
    if (word == null) throw new IOException("File empty !");
    lex = class_lex(word);
    if (lex != NETFILE_KW)
      throw new IOException("Invalid net file header !");
    word = read_word();

    // Jump the relase number
    jump_lexem(STRING);

    // Jump the first '{'
    jump_lexem(OPENBRACE);

    // Global param
    if (class_lex(word) == GLOBALPARAM_KW)
      parseGlobalParam();

    /* PARSE the NetFile */
    lex = class_lex(word);
    while (!eof) {
      switch (lex) {
        case BUBBLE_KW:
          objects.add(parseObject(NetObject.OBJECT_BUBBLE));
          break;
        case TEXT_KW:
          objects.add(parseObject(NetObject.OBJECT_TEXT));
          break;
        case CLOSEBRACE:
          // We have reached the end
          break;
        default:
          throw new IOException("Invalid keyword at line " + StartLine);
      }
      lex = class_lex(word);
      eof = (word == null) || (lex==CLOSEBRACE);
    }

    if(word == null)
      throw new IOException("Unexpected end of file.");

    // Check the last '}'
    CHECK_LEX(class_lex(word), CLOSEBRACE);

    // Build children list
    for (i = 0; i < objects.size(); i++) {
      NetObject o = (NetObject) objects.get(i);
      int[] childIds = o.getChildrenIdList();
      if (childIds != null) {
        for (j = 0; j < childIds.length; j++) {
          NetObject dst = findFromIndex(objects, childIds[j]);
          if (dst == null)
            throw new IOException("Invalid index '" + childIds[j] + "' in children list of object #" + o.getIndex());
          o.addChild(dst);
        }
      }
    }

    return objects;

  }

  /** Gets the label font read in the global param section if any, default font otherwise */
  public Font getLabelFont() {
    return labelFont;
  }

  /** Gets the small font read in the global param section if any, default font otherwise */
  public Font getSmallFont() {
    return smallFont;
  }

  /** Gets the Anti-Aliased font usage flag read in the global param section if any, false otherwise */
  public boolean getUseAAFont() {
    return useAAFont;
  }

  /** Gets the draw arrow flag read in the global param section if any, true otherwise */
  public boolean getDrawArrow() {
    return drawArrow;
  }

// -------------------------------------------------------
// Display a warning message
// -------------------------------------------------------
  private void warningMessage(String message) {
    if (firstWarning) {
      System.out.println("Warning while parsing: " + fileName);
      firstWarning = false;
    }
    System.out.println(message);
  }

  private NetObject findFromIndex(Vector objs, int idx) {

    int i = 0,sz = objs.size();
    boolean found = false;
    NetObject o = null;

    while (i < sz && !found) {
      o = (NetObject) objs.get(i);
      found = (o.getIndex() == idx);
      if (!found) i++;
    }

    if (found)
      return o;
    else
      return null;

  }

}

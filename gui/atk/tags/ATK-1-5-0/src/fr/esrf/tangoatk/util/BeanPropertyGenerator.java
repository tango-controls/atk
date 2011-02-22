// File:          BeanPropertyGenerator.java
// Created:       2002-01-25 13:20:42, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-04-25 13:39:44, assum>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.util;
import java.io.*;
import java.util.*;

public class BeanPropertyGenerator {
    int indentLevel = 4;
    PrintStream out;
    String name;
    Properties prop;

    public BeanPropertyGenerator(InputStream properties, PrintStream out)
    throws java.io.IOException {
	int level = 0;
	this.out = out;
	prop = new Properties();
	prop.load(properties);	     
	name = prop.getProperty("name").trim();

	generateBeanHeader(prop.getProperty("package").trim());
	++level;
	if (!"".equals(prop.getProperty("events", "").trim())) 
	    generateEventSetDescriptor(level,
				       new StringTokenizer
				       (prop.getProperty("events").trim(),
					","));
	if (!"".equals(prop.getProperty("properties", "").trim())) 
	    generatePropertyDescriptor(level,
				       new StringTokenizer
				       (prop.getProperty("properties").trim(),
					","));
	generateIcon(level);
	generateAdditionalBeanInfo(level);
	generateBeanFooter(--level);


    }

    /**
     * Get the value of name.
     * @return value of name.
     */
    public String getName() {
	return name;
    }
    
    /**
     * Set the value of name.
     * @param v  Value to assign to name.
     */
    public void setName(String  v) {
	this.name = v;
    }
    

    /**
     * Get the value of indent.
     * @return value of indent.
     */
    public int getIndentLevel() {
	return indentLevel;
    }
    
    /**
     * Set the value of indent.
     * @param v  Value to assign to indent.
     */
    public void setIndentLevel(int  v) {
	this.indentLevel = v;
    }

    int generateBeanHeader(String pckge) {
	out.print("package ");
	out.print(pckge);
	out.println(";\n");
	out.println("import java.beans.*;\n");
	out.println("// This class is autogenerated by " +
		    "the BeanPropertyGenerator");
	out.println("// Do not edit");
	out.println("// See " + name + "BeanInfo.info\n");
	out.print("public class ");
	out.print(name);
	out.println("BeanInfo extends SimpleBeanInfo {");
	out.println("");
	return 1;
    }

    int generateBeanFooter(int level) {
	out.print(generatePad(level));
					  
	out.println("}");
	return 0;
    }
    
    String generatePad(int level) {
	StringBuffer pad = new StringBuffer();
	int length = level * indentLevel;

	for (;length >= 0; length--) {
	    pad.append(" ");
	}
	return pad.toString();
    }

    void generateHeader(String retval, String name, int level, boolean tc) {
	StringBuffer line =
	    new StringBuffer(generatePad(level));
	line.append("public ");
	line.append(retval);
	line.append("[] get");
	line.append(name);
	line.append("() {");
	out.println(line);
	out.println("");
	if (tc) {
	    level++;
	    line.delete(0, line.length());

	    line.append(generatePad(level));
	    line.append("try {");
	    out.println(line);	    
	}
	

    }	

    void generateHeader(String name, int level) {
	generateHeader(name, name + "s", level, true);
    }

    void generateHeader(String name, int level, boolean tc) {
	generateHeader(name, name + "s", level, tc);
    }

    void generateFooter(String name, int level) {
	level--;
	StringBuffer line = new StringBuffer(generatePad(level));
	line.append("} catch (Exception e) {");
	out.println(line);
	line.delete(0, line.length());
	level++;
	line.append(generatePad(level));
	line.append("System.err.println(\"" + prop.getProperty("name").trim()
		    + "\" + \":\");\n");
	line.append(generatePad(level));
	line.append("System.err.println(e);");

	out.println(line);
	level--;
	line.delete(0, line.length());
	line.append(generatePad(level));
	line.append("}");
	out.println(line);

	line.delete(0, line.length());
	line.append(generatePad(level));
	line.append("return null;");
	out.println(line);
	line.delete(0, line.length());
	level--;
	line.append(generatePad(level));
	line.append("}");
	out.println(line);
	out.println();
	out.println();
    }	

    void generateTry(int level) {
	printPad(level, "try {");
    }

    void generateCatch(int level, String exception) {
	out.print(generatePad(--level));
	out.println("} catch (" + exception + ") {");
    }
			 
    void printPad(int level, String s) {
	out.print(generatePad(level));
	out.println(s);
    }
    
    void generateAdditionalBeanInfo(int level) {
	String name = getName();
	StringBuffer retval =
	    new StringBuffer("return new BeanInfo[] { sbi };");
	generateHeader("BeanInfo", "AdditionalBeanInfo", level, true);
	level++;
	printPad(++level, "Class s = " +
			   getName() +
			   ".class.getSuperclass();");
	printPad(++level, "BeanInfo sbi = Introspector.getBeanInfo(s);");
	printPad(level, retval.toString());
	generateCatch(level, "IntrospectionException e");
	printPad(level, "System.out.println(e);");
	printPad(level, "return null;");
	printPad(--level, "}");
	printPad(--level, "}");
    }
	    

	

		  
	
    void generatePropertyDescriptor(int level, StringTokenizer properties) {
	String property;
	StringBuffer retval =
	    new StringBuffer("return new PropertyDescriptor [] { ");
	generateHeader("PropertyDescriptor", level, false);
	level++;
	List plist = new Vector();

	while (properties.hasMoreTokens()) {
	    plist.add(properties.nextToken().trim());
	}
	printPad(level, "PropertyDescriptor [] propdesc = new PropertyDescriptor[" +
		 plist.size() + "];");

	for (int i = 0; i < plist.size(); i++) {
	    generateTry(level++);
	    property = (String)plist.get(i);
	    printPad(level, "propdesc[" + i +
		     "] = new PropertyDescriptor(\"" + property + "\", " +
		     name + ".class);");
	    generateCatch(level, "Exception e");
	    printPad(level, "System.out.println(\"\\n" + property +
		     " not supported (please verify your code)\");");
	    printPad(--level, "}");
	}
	printPad(level, "return propdesc;");
	printPad(--level, "}");
    }
	    
	    

    void generateEventSetDescriptor(int level,
				   StringTokenizer events) {
	generateHeader("EventSetDescriptor", level);
	level++;
	StringBuffer retval =
	    new StringBuffer("return new EventSetDescriptor [] { ");
	String event;
	String listenerType;
	String listenerMethodName;
	String displayName;
	
	while (events.hasMoreTokens()) {
	    out.print(generatePad(++level));
	    event = events.nextToken().trim();
	    retval.append(event); retval.append(", ");
	    listenerType = prop.getProperty(event + ".listenerType").trim();
	    listenerMethodName = prop.getProperty(event +
						  ".listenerMethodName")
		.trim();
	    displayName = prop.getProperty(event + ".displayName").trim();;
	    out.print("EventSetDescriptor "); out.print(event);
	    out.print(" = new EventSetDescriptor(");
	    out.print(name); out.print(".class, \"");
	    out.print(event); out.print("\", ");

	    out.print(listenerType); out.print(".class, \"");
	    out.print(listenerMethodName); out.println("\");");
	    out.print(event); out.print(".setDisplayName(\"");
	    out.print(displayName);out.println("\");");

	} // end of for ()
	retval.append(" };");
	out.println(retval);
	generateFooter("EventSetDescriptor", --level);
    }

    void generateIcon(int level) {
	String color16 = prop.getProperty("ICON_COLOR_16x16");
	String mono16 = prop.getProperty("ICON_MONO_16x16");
	String color32 = prop.getProperty("ICON_COLOR_32x32");
	String mono32 = prop.getProperty("ICON_MONO_32x32");

	if (color16 == null &&
	    mono16  == null &&
	    color32 == null &&
	    mono32  == null) {
	    return;
	}

	out.print(generatePad(level));
	out.println("public java.awt.Image getIcon(int icon) {\n");
	++level;
	generateSingleIcon(color16, "ICON_COLOR_16x16", level);
	generateSingleIcon(color32, "ICON_COLOR_32x32",   level);
	generateSingleIcon(mono16, "ICON_MONO_16x16", level);
	generateSingleIcon(mono32, "ICON_MONO_32x32", level);
	out.print(generatePad(level));
	out.println("return null;");
	out.print(generatePad(--level));
	out.println("}");
    }

    void generateSingleIcon(String iconName, String type, int level) {
	if (iconName == null) return;
	iconName.trim();
	if (iconName.length() < 1) return;

	out.print(generatePad(level));
	out.println("if (icon == BeanInfo." + type + ") {");
	out.print(generatePad(++level));
	out.println("return loadImage(\"" + iconName + "\");");
	out.print(generatePad(--level));
	out.println("}");
    }
	

    public static void main (String[] args) {
	try {
	    BeanPropertyGenerator generator =
		new BeanPropertyGenerator(new FileInputStream(args[0]),
							      new PrintStream(new FileOutputStream(args[1])));
	
	} catch (Exception e) {
	    e.printStackTrace();
	    System.err.println(e);
	} // end of try-catch
	
    } // end of main ()
}


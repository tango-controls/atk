// File:          JarVerifier.java
// Created:       2002-06-11 13:42:30, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-11 15:36:36, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.util;
import java.io.*;
import java.util.*;
public class JarVerifier {
    static boolean error = false;
    class Bean {
	    String name;

	    Bean(String s) {
		name = s.substring(0, s.lastIndexOf(".class"));
	    }

	    public String getName() {
		return name;
	    }

	    public String toString() {
		return "Name: " + name + "\n" + "Java-Bean: True";
	    }
	}
	

    public JarVerifier(String path) throws Exception {
	Iterator beans =
	    new Manifest
	    (new File(path + "/META-INF/MANIFEST.MF")).getBeans();
	Bean bean;
	File f;
	String beanInfo, beanName;
	while (beans.hasNext()) {
	    
	    bean = (Bean)beans.next();
	    beanName = bean.getName();
	    f = new File(path, beanName + ".class");
	    if (!f.exists()) {
		System.err.println(beanName + " not found in jar");
		error = true;
	    }
	    beanName = beanName.replace('/', '.');
	    beanInfo = beanName + "BeanInfo";
	    String out = "Verifying " + beanName;
	    System.out.print(out);

	    for (int i = 0; i < 75 - out.length(); i++) {
		System.out.print(".");
	    }
	    
	    if (new BeanPropertyVerifier(beanName, beanInfo).verify()) {
		System.out.println("Ok");
	    } else {
		System.out.println("Failed");
		error = true;
	    } // end of else
	}
    }


    public static void main (String[] args) throws Exception {
	new JarVerifier(args[0]);

	if (error) System.exit(1);

	System.exit(0);
	
    } // end of main ()
    

    class Manifest {
	BufferedReader stream;

	public Manifest(File manifestFile) throws IOException {
	    stream =
		new BufferedReader(new FileReader(manifestFile));
	    while (!("".equals(stream.readLine()))) { ; } 
	    
	}

	Iterator getBeans() {

	    return new Iterator() {
		    String line;
		    Bean bean;

		    protected String getValue(String start)
		    throws IOException {
			while (line.startsWith(" ")) {
			    start += line.substring(1, line.length());
			    line = stream.readLine();
			} // end of while ()
			return start;
		    }
		    
		    public Bean getBean() throws IOException {
			String name =
			    line.substring
			    ("Name: ".length(), line.length());
			line = stream.readLine();
			name = getValue(name);
			if ("Java-Bean: True".equals(line.trim()))
			    return new Bean(name);
			return null;
		    }

		    public boolean hasNext() {
			try {
			    do {
				line = stream.readLine();
			    } while (!line.startsWith("Name: "));
			    
			    bean = getBean();
			    if (bean == null) return false;
			    
			} catch (Exception e) {
			    return false;
			}
			return true;
		    }

		    public Object next() {
			return bean;
		    }

		    public void remove() {

		    }
		};
	}
    }

}

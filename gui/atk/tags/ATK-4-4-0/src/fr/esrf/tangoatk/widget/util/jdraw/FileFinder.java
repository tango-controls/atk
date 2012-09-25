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
 
package fr.esrf.tangoatk.widget.util.jdraw;

import java.io.IOException;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * FileFinder, give a root directory, an extension and a maximum number of files, and the list of files will be returned
 * created by srubio@cells, oktober 2006
 */

public class FileFinder {
	private String rootDir;
	private String extension;
	private int maxFiles, counter;
	private String[] fileList;
	private boolean recursive;
	
	public FileFinder(String rd, String xt, int mx) {
		rootDir=rd;
		extension=xt;
		maxFiles=mx;
		fileList = new String[maxFiles];
		counter=0;
	}
	
	public String[] getList(boolean rc) {
		recursive=rc;
		FileFilter fileFilter1 = new FileFilter() {
			public boolean accept(File file) {
				return !file.getName().startsWith(".");
			}
		};		
		FilenameFilter fileFilter2 = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".lib");
			}
		};
		File dir = new File(rootDir);
		File[] files = dir.listFiles(fileFilter1);
		if (files == null)
			return null;
		for (int i=0;i<files.length && counter<maxFiles;i++) {
			if (files[i].isDirectory() && recursive) {
				FileFinder subdir = new FileFinder(rootDir+"/"+files[i].getName(),extension,maxFiles-counter);
				String[] sublist = subdir.getList(true);
				//To the list returned the subdirectory name must be added
				for (int j=0;j<sublist.length;j++) {
					fileList[counter]=files[i].getName()+"/"+sublist[j];
					counter++;
				}
			} else if (files[i].getName().endsWith(".lib")) {
				fileList[counter]=files[i].getName();
				counter++;
			}
		}
		String[] returnList = new String[counter];
		for (int i=0;i<counter;i++) {
			returnList[i]=fileList[i];
			System.out.println("File found: "+returnList[i]);
		}
		return returnList;
	}
}

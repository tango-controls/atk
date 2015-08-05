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
 
/*
 * Splash2.java
 *
 * Created on February 28, 2002, 10:37 AM
 */

package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;


public class Splash extends JWindow {

  JFileChooser f;

  protected SplashPanel splashPanel;

  /** Creates and displays an ATK splash panel using the default ATK splash image. */
  public Splash() {
    initComponents(null,null,null);
  }

  /**
   * Creates and displays an ATK splash panel using the given image.
   * @param splashImage Splash image
   */
  public Splash(ImageIcon splashImage) {
    initComponents(splashImage,null,null);
  }

  /**
   * Creates and displays an ATK splash panel using the given image and text color.
   * The textColor param does not affect the ProgressBar.
   * @param splashImage Splash image
   * @param textColor Text color
   */
  public Splash(ImageIcon splashImage,Color textColor) {
    initComponents(splashImage,textColor,null);
  }

  /**
   * Creates and displays an ATK splash panel using the given image ,text color
   * and JSmoothProgressBar.
   * @param splashImage Splash image
   * @param textColor Text color
   * @param newBar ProgressBar which will be used by this splah window.
   */
  public Splash(ImageIcon splashImage,Color textColor,JSmoothProgressBar newBar) {
    initComponents(splashImage,textColor,newBar);
  }

  protected void initComponents(ImageIcon icon,Color textColor,JSmoothProgressBar newBar) {

    setBackground(new Color(100,110,140));

    splashPanel = new SplashPanel(icon,textColor,newBar);

    setContentPane(splashPanel);
    pack();

    // Center the splash window
    Dimension d = splashPanel.getPreferredSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width - d.width)/2 , (screenSize.height - d.height)/2,
              d.width,d.height);
    setVisible(true);

  }

  public void setCopyright(String copyright) {
    splashPanel.setCopyright(copyright);
  }

  public String getCopyright() {
    return splashPanel.getCopyright();
  }

  public void setMessage(String message) {
    splashPanel.setMessage(message);
  }

  public String getMessage() {
    return splashPanel.getMessage();
  }

  public void setTitle(String title) {
    splashPanel.setTitle(title);
  }

  public String getTitle() {
    return splashPanel.getTitle();
  }

  public JSmoothProgressBar getProgressBar() {
    return splashPanel.getProgress();
  }

  public void initProgress() {
    splashPanel.setProgress(0);
  }

  public void setMaxProgress(int i) {
    splashPanel.getProgress().setMaximum(i);
  }

  public void progress(int i) {
    splashPanel.setProgress(i);
  }


  // For backward compatibilty (No longer used)

  /** @deprecated */
  public void initProgress(int maxValue) {
    splashPanel.getProgress().setMaximum(maxValue);
    splashPanel.setProgress(maxValue);
  }
  /** @deprecated */
  public void setIndeterminateProgress(boolean b) {}
  /** @deprecated */
  public void setAuthor(String s) {}
  /** @deprecated */
  public void setPanelForeground(java.awt.Color color) {}
  /** @deprecated */
  public String getAuthor() { return ""; }
  /** @deprecated */
  public void setVersion(String s) {}
  /** @deprecated */
  public String getVersion() { return ""; }


  public static void main(String[] args) {

    //JSmoothProgressBar myBar = new JSmoothProgressBar();
    //myBar.setStringPainted(true);
    //myBar.setProgressBarColors(Color.GRAY,Color.LIGHT_GRAY,Color.DARK_GRAY);
    //Splash s = new Splash(new ImageIcon("Z:\\tmp\\esrf.gif"),new Color(255,100,100),myBar);
    //Splash s = new Splash(new ImageIcon("Z:\\tmp\\shot1.gif"));
    Splash s = new Splash();
    s.setTitle("SplashScreen");
    s.setMessage("This is the free message line");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {}
    for(int i=0;i<=100;i++) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {}
      s.progress(i);
    }
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {}
    System.exit(0);

  } // end of main ()


}

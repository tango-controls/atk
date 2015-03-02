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
 * SplashPanel.java
 *
 * Created on February 28, 2002, 10:37 AM
 */

package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

class SplashPanel extends JComponent implements MouseListener {

  // Default image
  protected final static String defaultImgLocation = "/fr/esrf/tangoatk/widget/util/splash.jpg";
  protected final static int foregroundDefault = ( (255 & 0xFF) << 24 )// Alpha
                                                 | ( (204 & 0xFF) << 16 )// Red
                                                 | ( (204 & 0xFF) << 8 )// Green
                                                 | ( (204 & 0xFF) << 0 );// Blue

  // Panel components
  private JSmoothLabel title;
  private JSmoothLabel message;
  private JSmoothLabel copyright;
  private JSmoothProgressBar progress = null;
  private BufferedImage ddBuffer; // To avoid flickering paint
  private boolean firstUpdate=true;
  private Dimension imgSize;
  private ImageIcon img;
  private Color textForeground;
  private JButton exitButton;

  // Splah panel constructor
  public SplashPanel() {
    this(null,null,null);
  }

  public SplashPanel(ImageIcon icon,Color textForeground,JSmoothProgressBar bar) {

    if( icon == null ) {
      img = new ImageIcon(
              SplashPanel.class.getResource(defaultImgLocation)
      );
    }
    else {
      img = icon;
    }
    if ( textForeground == null ) {
      this.textForeground = new Color(foregroundDefault);
    }
    else {
      this.textForeground = textForeground;
    }
    if(bar!=null) progress = bar;
    initComponents();

  }

  private void initComponents() {

    imgSize = new Dimension(img.getIconWidth(),img.getIconHeight());

    setLayout(null);
    setDoubleBuffered(false);

    title = new JSmoothLabel();
    title.setFont(new java.awt.Font("Dialog", Font.BOLD, 18));
    title.setForeground(textForeground);
    title.setText("");
    title.setHorizontalAlignment(JSmoothLabel.LEFT_ALIGNMENT);
    title.setOpaque(false);
    title.setDoubleBuffered(false);

    message = new JSmoothLabel();
    message.setFont(new java.awt.Font("Dialog", Font.BOLD, 12));
    message.setForeground(textForeground);
    message.setText("Initializing...");
    message.setHorizontalAlignment(JSmoothLabel.LEFT_ALIGNMENT);
    message.setOpaque(false);
    message.setDoubleBuffered(false);

    copyright = new JSmoothLabel();
    copyright.setFont(new java.awt.Font("Dialog", Font.PLAIN, 10));
    copyright.setForeground(textForeground);
    copyright.setText("(c) ESRF 2002");
    copyright.setHorizontalAlignment(JSmoothLabel.LEFT_ALIGNMENT);
    copyright.setOpaque(false);
    copyright.setDoubleBuffered(false);

    if(progress==null) {
      progress = new JSmoothProgressBar();
      progress.setStringPainted(true);
    }
    progress.setDoubleBuffered(false);

    exitButton = new JButton("x");
    exitButton.setFont(new java.awt.Font("Dialog", Font.PLAIN, 10));
    exitButton.setMargin(new Insets(0, 0, 0, 0));
    exitButton.setDoubleBuffered(false);

    setPreferredSize(imgSize);
    setMinimumSize(imgSize);
    setMaximumSize(imgSize);

    title.setSize(imgSize.width - 10, 23);
    message.setSize(imgSize.width - 10, 18);
    progress.setSize(imgSize.width - 10, 21);
    copyright.setSize(imgSize.width - 10, 15);
    exitButton.setSize(15,15);

    // Update the whole double buffer
    ddBuffer = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB);
    Graphics g = ddBuffer.getGraphics();
    paintBackground(g, 0, 0, imgSize.width, imgSize.height);
    paintComponent(g, message, 5, imgSize.height - 30);
    paintComponent(g, progress, 5, imgSize.height - 55);
    paintComponent(g, copyright, 5, imgSize.height - 75);
    paintComponent(g, title, 5, imgSize.height - 95);
    paintComponent(g, exitButton, imgSize.width - 20, 5);
    g.dispose();

    addMouseListener(this);

  }

  public void mouseClicked(MouseEvent e) {};
  public void mousePressed(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    if( x>imgSize.width-20 && x<imgSize.width-5 && y>5 && y<20 ) {
      Runtime.getRuntime().halt(0);
    }
  };
  public void mouseReleased(MouseEvent e) {};
  public void mouseEntered(MouseEvent e) {};
  public void mouseExited(MouseEvent e) {};


  private void paintComponent(Graphics g, Component c, int x, int y) {
    g.translate(x, y);
    c.paint(g);
    g.translate(-x, -y);
  }

  private void paintBackground(Graphics g, int x, int y, int w, int h) {
    g.drawImage(img.getImage(), x, y, x+w, y+h, x, y, x+w, y+h, null);
  }

  private void repaintComponent(Component c, int x, int y) {

    // Paint inside the double buffer
    Graphics g = ddBuffer.getGraphics();
    paintBackground(g, x, y, c.getSize().width, c.getSize().height);
    paintComponent(g, c, x, y);
    g.dispose();

    // Now force paint on screen
    g = getGraphics();
    if (g != null) {
      // Repaint the whole component at first update.
      // When the Swing thread is much solicited ,
      // the background is not always painted (no call to paint)
      if(firstUpdate) {
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        g.drawImage(ddBuffer, 0, 0, null);
        firstUpdate = false;
      } else {
        // Repaint only updated component
        g.drawImage(ddBuffer, x, y, x+c.getSize().width, y+c.getSize().height,
                  x, y, x+c.getSize().width, y+c.getSize().height, null);
      }
      g.dispose();
    }

  }

  void setTitle(String s) {
    title.setText(s);
    repaintComponent(title, 5, imgSize.height - 95);
  }

  String getTitle() {
    return title.getText();
  }

  void setProgress(int p) {
    if( progress.getValue()!=p ) {
      progress.setValue(p);
      repaintComponent(progress, 5, imgSize.height - 55);
    }
  }

  JSmoothProgressBar getProgress() {
    return progress;
  }

  void setMessage(String s) {
    message.setText(s);
    repaintComponent(message, 5, imgSize.height - 30);
  }

  String getMessage() {
    return message.getText();
  }

  void setCopyright(String s) {
    copyright.setText(s);
    repaintComponent(copyright, 5, imgSize.height - 75);
  }

  String getCopyright() {
    return copyright.getText();
  }

  // Paint panel
  public void paint(Graphics g) {
    g.drawImage(ddBuffer, 0, 0, null);
  }

}

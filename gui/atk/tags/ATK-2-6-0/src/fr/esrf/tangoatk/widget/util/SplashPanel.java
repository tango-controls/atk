/*
 * SplashPanel.java
 *
 * Created on February 28, 2002, 10:37 AM
 */

package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class SplashPanel extends JComponent {

  // Default image
  static ImageIcon imgDefault = new ImageIcon(SplashPanel.class.getResource("/fr/esrf/tangoatk/widget/util/splash.jpg"));
  static Color foregroundDefault = new Color(204, 204, 204);

  // Panel components
  private JSmoothLabel title;
  private JSmoothLabel message;
  private JSmoothLabel copyright;
  private JSmoothProgressBar progress = null;
  private BufferedImage ddBuffer; // To avoid flickering paint
  private boolean firstUpdate=true;
  private Dimension imgSize;
  private ImageIcon img = imgDefault;
  private Color textForeground = foregroundDefault;

  // Splah panel constructor

  public SplashPanel() {
    initComponents();
  }

  public SplashPanel(ImageIcon icon,Color textForeground,JSmoothProgressBar bar) {

    if(icon!=null) img = icon;
    if(textForeground!=null) this.textForeground = textForeground;
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

    setPreferredSize(imgSize);
    setMinimumSize(imgSize);
    setMaximumSize(imgSize);

    title.setSize(imgSize.width - 10, 23);
    message.setSize(imgSize.width - 10, 18);
    progress.setSize(imgSize.width - 10, 21);
    copyright.setSize(imgSize.width - 10, 15);

    // Update the whole double buffer
    ddBuffer = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB);
    Graphics g = ddBuffer.getGraphics();
    paintBackground(g, 0, 0, imgSize.width, imgSize.height);
    paintComponent(g, message, 5, imgSize.height - 30);
    paintComponent(g, progress, 5, imgSize.height - 55);
    paintComponent(g, copyright, 5, imgSize.height - 75);
    paintComponent(g, title, 5, imgSize.height - 95);
    g.dispose();

  }

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

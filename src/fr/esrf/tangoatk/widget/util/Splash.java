/*
 * Splash2.java
 *
 * Created on February 28, 2002, 10:37 AM
 */

package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class Splash extends JWindow {

  /** Creates new form Splash */
  public Splash() {

    getContentPane().setLayout(new GridLayout(1, 1));
    setBackground(Color.black);

    ImageIcon img = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/esrf.gif"));
    imgSize = new Dimension(img.getIconWidth(), img.getIconHeight());

    // Center the splah window

    Dimension screenSize =
        Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(screenSize.width / 2 - (imgSize.width / 2),
        screenSize.height / 2 - (imgSize.height / 2));
    setSize(imgSize);

    icon = new JLabel();
    icon.setIcon(img);
    setContentPane(icon);

    splashPanel = new SplashPanel(imgSize);
    setGlassPane(splashPanel);
    pack();
    getGlassPane().setVisible(true);

    super.setVisible(true);

    // Let it appear
    try { Thread.sleep(200); }
    catch (Exception e) {}

    // Force the paint here
    Graphics g = getGraphics();
    if( g!=null ) paintAll(g);
    else System.out.println("Splash::Splash() Warning cannot retrieve graphics object.");

  }


  // Paint a component in double buffer mode to avoid flickering paint
  public void panelComponentPaint(JComponent c) {
    Graphics g = getGraphics();
    if( g!=null ) {
      Rectangle  r = c.getBounds();
      BufferedImage bi=new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_BGR );
      Graphics big = bi.getGraphics();
      big.translate(-r.x, -r.y);
      paintAll(big);
      g.drawImage(bi,r.x,r.y,this);
    }
  }

  public void setPanelForeground(java.awt.Color color) {
    setForeground(color);
  }

  public void setAuthor(String s) {
  }

  public String getAuthor() {
    return "";
  }

  public JSmoothProgressBar getProgressBar() {
    return splashPanel.progress;
  }

  public void setVersion(String s) {
  }

  public String getVersion() {
    return "";
  }

  public void setCopyright(String copyright) {
    splashPanel.copyright.setText(copyright);
    panelComponentPaint(splashPanel.copyright);
  }

  public String getCopyright() {
    return splashPanel.copyright.getText();
  }

  public void setMessage(String message) {
    splashPanel.message.setText(message);
    panelComponentPaint(splashPanel.message);
  }

  public String getMessage() {
    return splashPanel.message.getText();
  }

  public void setTitle(String title) {
    splashPanel.title.setText(title);
    // Force the progress bar to be paint
    panelComponentPaint(splashPanel.title);
  }

  public String getTitle() {
    return splashPanel.title.getText();
  }

  public void initProgress(int i) {
    splashPanel.progress.setValue(0);
    splashPanel.progress.setMaximum(i);
  }

  public void initProgress() {
    splashPanel.progress.setValue(0);
  }

  public void setMaxProgress(int i) {
    splashPanel.progress.setMaximum(i);
  }

  public void progress(int i) {
    splashPanel.progress.setValue(i);
    // Force the progress bar to be paint
    panelComponentPaint(splashPanel.progress);
  }

  public void setIndeterminateProgress(boolean b) {
    splashPanel.progress.setIndeterminate(b);
  }

  public static void main(String[] args) {
    new Splash();
  } // end of main ()

  private Dimension imgSize;
  private SplashPanel splashPanel;
  private JLabel icon;

  // End of variables declaration//GEN-END:variables

}

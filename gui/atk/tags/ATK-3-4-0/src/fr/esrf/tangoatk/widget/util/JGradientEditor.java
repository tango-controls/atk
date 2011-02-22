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
 
// A class to handle an image viewer
package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *  A Color gradient editor component.
 */
public class JGradientEditor extends JComponent implements ActionListener,MouseMotionListener, MouseListener {

  private boolean isEditable;
  private boolean isDragging;
  private Gradient grad;
  private int[] palette;
  private int currentSel = -1;
  private double currentPos;
  private int barHeight = 25; // Height of the JGradientEditor

  private JPopupMenu popMenu;
  private JMenuItem infoMenuItem;
  private JMenuItem addMenuItem;
  private JMenuItem removeMenuItem;
  private JMenuItem changeColorMenuItem;
  private JMenuItem resetMenuItem;
  private JMenuItem rainbowMenuItem;

  private final static int[] cursorShapeX = {-5, -2, 2, 5, 5, 2, -2, -5, -5};
  private final static int[] cursorShapeY = {10, 15, 15, 10, -10, -15, -15, -10, 10};

  // ----------------------------------------------
  // Contruction
  // ----------------------------------------------

  public JGradientEditor() {

    setBorder(null);
    setOpaque(true);
    setOpaque(true);
    addMouseMotionListener(this);
    addMouseListener(this);

    isEditable = true;
    grad = new Gradient();
    palette = grad.buildColorMap(256);

    popMenu = new JPopupMenu();

    infoMenuItem = new JMenuItem("Gradient editor");
    infoMenuItem.setEnabled(false);

    addMenuItem = new JMenuItem("Add color here");
    addMenuItem.addActionListener(this);

    removeMenuItem = new JMenuItem("Remove");
    removeMenuItem.addActionListener(this);

    changeColorMenuItem = new JMenuItem("Change color");
    changeColorMenuItem.addActionListener(this);

    resetMenuItem = new JMenuItem("Reset to default gradient");
    resetMenuItem.addActionListener(this);

    rainbowMenuItem = new JMenuItem("Reset to rainbow gradient");
    rainbowMenuItem.addActionListener(this);

    popMenu.add(infoMenuItem);
    popMenu.add(new JSeparator());
    popMenu.add(addMenuItem);
    popMenu.add(removeMenuItem);
    popMenu.add(changeColorMenuItem);
    popMenu.add(resetMenuItem);
    popMenu.add(rainbowMenuItem);

  }

  // ----------------------------------------------
  // Property
  // ----------------------------------------------

  public void setEditable(boolean b) {
    isEditable = b;
  }

  public boolean isEditable() {
    Dimension d = getSize();
    return isEditable && (d.width >= 256);
  }

  public void setGradient(Gradient g) {
    if (g != null) {
      grad = g;
      palette = g.buildColorMap(256);
    }
  }

  public Gradient getGradient() {
    return grad;
  }

  public void setRainbowGradient() {
    grad = new Gradient();
    grad.buildRainbowGradient();
    palette = grad.buildColorMap(256);
    repaint();
  }

  public void setDefaultGradient() {
    grad = new Gradient();
    palette = grad.buildColorMap(256);
    repaint();
  }

  public void invertGradient() {
    grad.invertGradient();
    palette = grad.buildColorMap(256);
    repaint();
  }

  // --------------------------------------------------------
  // Overrides
  // --------------------------------------------------------

  public void paint(Graphics g) {

    Dimension d = getSize();

    if (d.width <= 0) return;

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, d.width, d.height);
    }

    if (d.width < 256) {

      // Minimal view
      int startY = (d.height - barHeight) / 2;

      double r = 256.0 / (double) d.width;
      for (int i = 0; i < d.width; i++) {
        int id = (int) (r * (double) i);

        if (id <= 255)
          g.setColor(new Color(palette[id]));
        else
          g.setColor(new Color(palette[255]));

        g.drawLine(i, startY, i, startY + barHeight);
      }

    } else {

      //Normal view
      int startX = (d.width - 256) / 2;
      int startY = (d.height - barHeight) / 2;

      //Draw gradient
      for (int i = 0; i < 256; i++) {
        g.setColor(new Color(palette[i]));
        g.drawLine(startX + i, startY, startX + i, startY + barHeight);
      }

      //Draw entries
      if (isEditable) {
        for (int i = 0; i < grad.getEntryNumber(); i++) {
          Color c = grad.getColorAt(i);
          double p = grad.getPosAt(i);
          int xc = startX + (int) (p * 256.0);
          int yc = startY + barHeight / 2;
          g.translate(xc, yc);
          paintCursor(g, c);
          g.translate(-xc, -yc);
        }
      }

    }

  }

  public Dimension getMinimumSize() {
    return new Dimension(300, 50);
  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  // ----------------------------------------------------------
  // Mouse Listeners
  // ----------------------------------------------------------

  public void mouseDragged(MouseEvent e) {

    if (isDragging == true) {

      Dimension d = getSize();
      int xe = e.getX();
      int startX = (d.width - 256) / 2;

      currentPos = ((xe - startX) / 256.0);
      if (currentPos < 0.0) currentPos = 0.0;
      if (currentPos > 1.0) currentPos = 1.0;

      grad.setPosAt(currentSel, currentPos);
      palette = grad.buildColorMap(256);
      repaint();

    }

  }

  public void mouseMoved(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {

    if (!isEditable())
      return;

    if (!isInsideBar(e))
      return;

    if( e.getClickCount()==2 ) {
      if (foundEntry(e)) {
        Color nColor = JColorChooser.showDialog(this, "Choose gradient color #" + (currentSel + 1), grad.getColorAt(currentSel));
        if (nColor != null) {
          grad.setColorAt(currentSel, nColor);
          palette = grad.buildColorMap(256);
          repaint();
        }
      }
    }

  }

  public void mouseReleased(MouseEvent e) {
    isDragging = false;
  }

  public void mousePressed(MouseEvent e) {

    if (!isEditable())
      return;

    if (!isInsideBar(e))
      return;

    if (foundEntry(e)) {

      if (e.getButton() == MouseEvent.BUTTON3) {
        addMenuItem.setVisible(false);
        removeMenuItem.setVisible(currentSel > 0 && currentSel < (grad.getEntryNumber() - 1));
        changeColorMenuItem.setVisible(true);
        popMenu.show(this, e.getX(), e.getY());
      }

      if (e.getButton() == MouseEvent.BUTTON1) {
        isDragging = true;
      }

    } else {

      if (e.getButton() == MouseEvent.BUTTON3) {
        addMenuItem.setVisible(true);
        removeMenuItem.setVisible(false);
        changeColorMenuItem.setVisible(false);
        popMenu.show(this, e.getX(), e.getY());
      }

    }


  }

  // ----------------------------------------------------------
  // Action Listener
  // ----------------------------------------------------------

  public void actionPerformed(ActionEvent evt) {

    if (evt.getSource() == changeColorMenuItem) {

      if (currentSel >= 0 && currentSel < grad.getEntryNumber()) {
        Color nColor = JColorChooser.showDialog(this, "Choose gradient color #" + (currentSel + 1), grad.getColorAt(currentSel));
        if (nColor != null) {
          grad.setColorAt(currentSel, nColor);
          palette = grad.buildColorMap(256);
          repaint();
        }
      }

    } else if (evt.getSource() == resetMenuItem) {

      setDefaultGradient();

    } else if (evt.getSource() == rainbowMenuItem ) {

      setRainbowGradient();

    } else if (evt.getSource() == addMenuItem) {

      Color nColor = JColorChooser.showDialog(this, "Choose new color", Color.black);
      grad.addEntry(nColor, currentPos);
      palette = grad.buildColorMap(256);
      repaint();

    } else if (evt.getSource() == removeMenuItem) {

      grad.removeEntry(currentSel);
      palette = grad.buildColorMap(256);
      repaint();

    }

  }

  // ------------------------------------------------------------
  // Static Help public function
  // ------------------------------------------------------------

  /**
   * Display the Gradient editor view.
   * @param parent parent dialog
   * @param g Gradient to be edited (can be null)
   * @return New Gradient , null when canceled
   */
  static public Gradient showDialog(JDialog parent, Gradient g) {

    JDialog gDialog;
    gDialog = new JDialog(parent, true);
    return showDefDialog(gDialog, g);

  }

  /**
   * Display the Gradient editor view.
   * @param parent parent frame
   * @param g Gradient to be edited (can be null)
   * @return New Gradient , null when canceled
   */
  static public Gradient showDialog(JFrame parent, Gradient g) {

    JDialog gDialog;
    gDialog = new JDialog(parent, true);
    return showDefDialog(gDialog, g);

  }

  /**
   * Display the Gradient editor view.
   * @param parent parent componenent
   * @param g Gradient to be edited (can be null)
   * @return New Gradient , null when canceled
   */
  static public Gradient showDialog(JComponent parent, Gradient g) {

    JDialog gDialog;
    Object p = parent.getRootPane().getParent();
    if (p instanceof JDialog) {
      gDialog = new JDialog((JDialog) p, true);
    } else if (p instanceof JDialog) {
      gDialog = new JDialog((JFrame) p, true);
    } else {
      gDialog = new JDialog((JFrame) null, true);
    }

    return showDefDialog(gDialog, g);

  }
  
  // ----------------------------------------------------------
  // Private stuff
  // ----------------------------------------------------------

  private Color transformColor(Color c, int offset) {

    int nr = (c.getRed() + offset);
    int ng = (c.getBlue() + offset);
    int nb = (c.getGreen() + offset);

    if (nr > 255) nr = 255;
    if (ng > 255) ng = 255;
    if (nb > 255) nb = 255;

    if (nr < 0) nr = 0;
    if (ng < 0) ng = 0;
    if (nb < 0) nb = 0;

    return new Color(nr, ng, nb);

  }

  private boolean isLightColor(Color c) {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();
    return Math.sqrt(r * r + g * g + b * b) > 180.0;
  }

  private void paintCursor(Graphics g, Color c) {

    g.setColor(c);
    g.fillPolygon(cursorShapeX, cursorShapeY, 9);

    if (isLightColor(c)) {
      g.setColor(transformColor(c, -128));
    } else {
      g.setColor(transformColor(c, 128));
    }

    g.drawPolygon(cursorShapeX, cursorShapeY, 9);

  }

  private boolean isInsideBar(MouseEvent e) {

    Dimension d = getSize();
    int xe = e.getX();
    int ye = e.getY();
    int startX = (d.width - 256) / 2;
    int startY = (d.height - barHeight) / 2;

    return (xe >= (startX - 5) && xe <= (startX + 261) &&
        ye >= startY && ye <= (startY + barHeight));

  }

  private boolean foundEntry(MouseEvent e) {

    boolean found=false;

    Dimension d = getSize();
    int xe = e.getX();
    int ye = e.getY();
    int startX = (d.width - 256) / 2;
    int startY = (d.height - barHeight) / 2;
    int i = 0;

    while (i < grad.getEntryNumber() && !found) {
      double p = grad.getPosAt(i);
      int xc = startX + (int) (p * 256.0);
      int yc = startY + barHeight / 2;
      found = (xe > xc - 8) && (xe < xc + 8) && (ye > yc - 15) && (ye < yc + 15);
      if (!found) i++;
    }

    if (found) {
      currentSel = i;
    } else {
      currentSel = -1;
    }

    currentPos = ((xe - startX) / 256.0);
    if (currentPos < 0.0) currentPos = 0.0;
    if (currentPos > 1.0) currentPos = 1.0;

    return found;

  }

  static private Gradient showDefDialog(JDialog gDialog, Gradient gOrg) {

    // Construct editor panel
    EditorPanel gradEditor = new EditorPanel();
    if (gOrg != null) {
      Gradient g = gOrg.cloneMe();
      gradEditor.setGradient(g);
    }

    gDialog.setResizable(false);
    gDialog.setContentPane(gradEditor);
    gDialog.setTitle("Colormap gradient editor");

    // Placement
    ATKGraphicsUtils.centerDialog(gDialog);

    gDialog.setVisible(true);
    gDialog.dispose();

    if (gradEditor.dlgRetValue == 0) {
      return null;
    } else {
      return gradEditor.getGradient();
    }

  }


  /** Main test function. */
  static public void main(String[] args) {

    final Gradient d = new Gradient();
    d.buildRainbowGradient();
    showDialog((JFrame)null,d);

  }

}

/** A panel for the grad editor. */
class EditorPanel extends JPanel implements ActionListener {


  private JButton bwButton;
  private JButton colorButton;
  private JButton invertButton;
  private JButton helpButton;
  private JButton okButton;
  private JButton cancelButton;
  private JGradientEditor gradEditor;

  private static String helpString = "Cursor color can be changed by double clicking on it.\n" +
                                     "Cursors can be moved by dragging them.\n" +
                                     "Color entry can be added or removed by right clicking on the gradient.";

  int dlgRetValue = 0;

  /**
   * Construction
   */
  public EditorPanel() {

    setLayout(new BorderLayout());

    // ----------------------------------------------------------
    JPanel gPanel = new JPanel();
    gPanel.setLayout(new BorderLayout());
    gPanel.setBorder(BorderFactory.createEtchedBorder());

    gradEditor = new JGradientEditor();
    gPanel.add(gradEditor, BorderLayout.CENTER);

    JPanel cPanel = new JPanel();
    cPanel.setLayout(new FlowLayout());

    bwButton = new JButton("Monochrome");
    bwButton.setFont(ATKConstant.labelFont);
    bwButton.addActionListener(this);
    cPanel.add(bwButton);

    colorButton = new JButton("Color");
    colorButton.setFont(ATKConstant.labelFont);
    colorButton.addActionListener(this);
    cPanel.add(colorButton);

    invertButton = new JButton("Invert");
    invertButton.setFont(ATKConstant.labelFont);
    invertButton.addActionListener(this);
    cPanel.add(invertButton);

    gPanel.add(cPanel,BorderLayout.SOUTH);

    add(gPanel, BorderLayout.CENTER);

    // ----------------------------------------------------------
    FlowLayout fl = new FlowLayout();
    fl.setAlignment(FlowLayout.RIGHT);
    JPanel panelButton = new JPanel(fl);

    helpButton = new JButton("Help");
    helpButton.setFont(ATKConstant.labelFont);
    helpButton.addActionListener(this);

    okButton = new JButton("Apply");
    okButton.setFont(ATKConstant.labelFont);
    okButton.addActionListener(this);

    cancelButton = new JButton("Dismiss");
    cancelButton.setFont(ATKConstant.labelFont);
    cancelButton.addActionListener(this);

    panelButton.add(helpButton);
    panelButton.add(okButton);
    panelButton.add(cancelButton);

    add(panelButton, BorderLayout.SOUTH);

  }

  // ----------------------------------------------
  // Property
  // ----------------------------------------------
  /**
   * Sets the Gradient editable. Must be fully sized !!!
   * @param b editable value
   */
  public void setEditable(boolean b) {
    gradEditor.setEditable(b);
  }

  public boolean isEditable() {
    return gradEditor.isEditable();
  }

  public void setGradient(Gradient g) {
    gradEditor.setGradient(g);
  }

  public Gradient getGradient() {
    return gradEditor.getGradient();
  }

  // ----------------------------------------------------------
  // Action Listener
  // ----------------------------------------------------------
  public void actionPerformed(ActionEvent evt) {

    Object src = evt.getSource();

    if( src==okButton ) {
      dlgRetValue = 1;
      getRootPane().getParent().setVisible(false);
    } else if ( src==cancelButton ) {
      dlgRetValue = 0;
      getRootPane().getParent().setVisible(false);
    } else if ( src==bwButton ) {
      gradEditor.setDefaultGradient();
    } else if ( src==colorButton ) {
      gradEditor.setRainbowGradient();
    } else if ( src==invertButton ) {
      gradEditor.invertGradient();
    } else if ( src==helpButton ) {
      JOptionPane.showMessageDialog(null,helpString,"Help on Gradient Editor",JOptionPane.INFORMATION_MESSAGE);
    }

  }

}

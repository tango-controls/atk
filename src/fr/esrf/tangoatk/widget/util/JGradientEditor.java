// A class to handle an image viewer
package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JGradientEditor extends JComponent implements MouseMotionListener, MouseListener, ActionListener {

  private boolean isEditable;
  private boolean isDragging;
  private Gradient grad;
  private int[] palette;
  private int currentSel = -1;
  private double currentPos;

  // dlgRetValue decalared static to be accessed by the listener
  // of dialod panel. (see showDialog())
  // This shouldn't have side fx if 2 dialogs are opened as
  // the dialog is imediatly closed after this value is set.
  private static int dlgRetValue = 0;

  final static int[] cursorShapeX = {-5, -2, 2, 5, 5, 2, -2, -5, -5};
  final static int[] cursorShapeY = {10, 15, 15, 10, -10, -15, -15, -10, 10};
  final static int barHeight = 25;

  private JPopupMenu popMenu;
  private JMenuItem infoMenuItem;
  private JMenuItem addMenuItem;
  private JMenuItem removeMenuItem;
  private JMenuItem changeColorMenuItem;
  private JMenuItem resetMenuItem;
  private JMenuItem rainbowMenuItem;

  /**
   * Construction
   */
  public JGradientEditor() {
    setLayout(null);
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
  /**
   * Draw the gradient editor. Must be fully sized !!!
   * @param b editable value
   */
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

  // ----------------------------------------------
  // Private stuff
  // ----------------------------------------------
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

  static private Gradient showDefDialog(JDialog gDialog, Gradient gOrg) {

    // Construct editor panel
    Font defFont = new Font("Dialog", Font.PLAIN, 11);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JGradientEditor gradEditor = new JGradientEditor();
    gradEditor.setEditable(true);
    if (gOrg != null) {
      Gradient g = gOrg.cloneMe();
      gradEditor.setGradient(g);
    }
    panel.add(gradEditor, BorderLayout.CENTER);

    JPanel panelButton = new JPanel(null);
    panelButton.setPreferredSize(new Dimension(0, 30));

    ActionListener l = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JButton src = (JButton) e.getSource();

        if (src.getText().equals("Apply")) dlgRetValue = 1;
        if (src.getText().equals("Dismiss")) dlgRetValue = 0;

        // Close dialog
        src.getRootPane().getParent().setVisible(false);
      }
    };

    JButton okButton = new JButton("Apply");
    okButton.setFont(defFont);
    okButton.addActionListener(l);
    okButton.setBounds(15, 3, 80, 25);

    JButton cancelButton = new JButton("Dismiss");
    cancelButton.setFont(defFont);
    cancelButton.addActionListener(l);
    cancelButton.setBounds(205, 3, 80, 25);

    panelButton.add(okButton);
    panelButton.add(cancelButton);

    panel.add(panelButton, BorderLayout.SOUTH);

    gDialog.setResizable(false);
    gDialog.setContentPane(panel);
    gDialog.setTitle("Colormap gradient editor");
    gDialog.pack();

    // Placement

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension _scr = toolkit.getScreenSize();
    Dimension _dlg = gDialog.getPreferredSize();
    gDialog.setBounds((_scr.width - _dlg.height) / 2, (_scr.height - _dlg.height) / 2,
        _dlg.width, _dlg.height);

    gDialog.setVisible(true);
    gDialog.dispose();

    if (dlgRetValue == 0) {
      return null;
    } else {
      return gradEditor.getGradient();
    }

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

  // --------------------------------------------------------
  // Measurements stuff
  // --------------------------------------------------------

  public Dimension getMinimumSize() {
    return new Dimension(300, 50);
  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
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

      grad = new Gradient();
      palette = grad.buildColorMap(256);
      repaint();

    } else if (evt.getSource() == rainbowMenuItem) {

      grad = new Gradient();
      grad.buidColorGradient();
      palette = grad.buildColorMap(256);
      repaint();

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

  // ------------------------------------------------------------
  // Static public function
  // ------------------------------------------------------------

  static public Gradient showDialog(JDialog parent, Gradient g) {

    JDialog gDialog;
    gDialog = new JDialog(parent, true);
    return showDefDialog(gDialog, g);

  }

  static public Gradient showDialog(JFrame parent, Gradient g) {

    JDialog gDialog;
    gDialog = new JDialog(parent, true);
    return showDefDialog(gDialog, g);

  }

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

  static public void main(String[] args) {

    final Gradient d = new Gradient();
    final JGradientEditor ge = new JGradientEditor();
    final JFrame f = new JFrame();

    d.buidColorGradient();
    ge.setGradient(d);

    f.getContentPane().add(ge);
    f.pack();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);

  }

}

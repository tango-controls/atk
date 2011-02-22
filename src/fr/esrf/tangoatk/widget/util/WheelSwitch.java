/*
 * WheelSwitch.java
 * Author: JL Pons 2002 E.S.R.F.
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.event.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.EventListener;

public class WheelSwitch extends JComponent {

  int off_x;                 // Postion horz offset
  int off_y;                 // Postion vert offset
  int intNumber;             // Number of integer digit
  int fracNumber;            // Number of decimal digit
  double value;                 // Current value
  double maxValue;              // Maximun value
  Dimension dz;                    // digit size
  boolean editMode;              // edition mode
  String editValue;             // value entered by keyboard
  EventListenerList listenerList;  // list of WheelSwitch listeners

  // Arrow buttons
  JArrowButton buttons_up[];
  JArrowButton buttons_down[];
  int nbButton = 0;
  Color buttonBackground;

  // General constructor
  public WheelSwitch() {

    setLayout(null);
    setForeground(Color.black);
    setBackground(new Color(200, 200, 200));
    setBorder(null);
    buttonBackground = getBackground();
    setOpaque(true);
    setPrecision(3, 2);
    setFont(new Font("Lucida Bright", Font.PLAIN, 16));

    value = 0.0;
    editMode = false;
    editValue = "";

    listenerList = new EventListenerList();

    addComponentListener(new ComponentListener() {
      public void componentHidden(ComponentEvent e) {
      }

      public void componentMoved(ComponentEvent e) {
      }

      public void componentResized(ComponentEvent e) {
        placeComponents();
      }

      public void componentShown(ComponentEvent e) {
        placeComponents();
      }
    });

    addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
        processKey(e);
      }

      public void keyReleased(KeyEvent e) {
      }

      public void keyTyped(KeyEvent e) {
      }
    });

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        grabFocus();
      }
    });

    addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        repaint();
      }

      public void focusLost(FocusEvent e) {
        repaint();
      }
    });


  }

  // Place the components
  private void placeComponents() {

    int total_width;
    int total_height;
    int i;

    computeDigitSize();

    // Place buttons
    Dimension sz = getSize();

    if (fracNumber > 0)
      total_width = dz.width * (nbButton + 2);
    else
      total_width = dz.width * (nbButton + 1);

    total_height = dz.height + 2 * dz.width;

    off_x = (sz.width - total_width) / 2;
    off_y = (sz.height - total_height) / 2;

    for (i = 0; i < nbButton; i++) {
      int xpos;
      if (i < intNumber)
        xpos = off_x + (i + 1) * dz.width + 1;
      else
        xpos = off_x + (i + 2) * dz.width + 1;

      // Top buttons
      buttons_up[i].setBounds(xpos, off_y + 2, dz.width - 2, dz.width - 2);

      // Bottom buttons
      int h = dz.width + dz.height + off_y;
      buttons_down[i].setBounds(xpos, h, dz.width - 2, dz.width - 2);
    }


  }

  public Dimension getPreferredSize() {

    int w,h;
    Insets borderSize;

    if (hasBorder())
      borderSize = getInsets();
    else
      borderSize = new Insets(1, 1, 1, 1);

    computeDigitSize();

    if (fracNumber > 0) {
      w = dz.width * (intNumber + fracNumber + 2);
    } else {
      w = dz.width * (intNumber + fracNumber + 1);
    }

    h = dz.width * 2 + dz.height;

    return new Dimension(w + borderSize.right + borderSize.left,
        h + borderSize.top + borderSize.bottom);

  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  // Process Key Envent
  private void processKey(KeyEvent e) {

    char c = e.getKeyChar();
    int code = e.getKeyCode();

    if ((c >= '0' && c <= '9') || c == '.' || c == '-') {
      editValue += c;
      editMode = true;
      repaint();
    }

    if (code == KeyEvent.VK_CLEAR || code == KeyEvent.VK_CANCEL ||
        code == KeyEvent.VK_ESCAPE) {
      editValue = "";
      editMode = false;
      repaint();
    }

    if (editMode && (code == KeyEvent.VK_BACK_SPACE ||
        code == KeyEvent.VK_DELETE)) {
      editValue = editValue.substring(0, editValue.length() - 1);
      repaint();
    }

    if (editMode && code == KeyEvent.VK_ENTER) {
      try {
        double newValue = near(Double.parseDouble(editValue));
        if (Math.abs(newValue) < maxValue) {
          value = newValue;
          fireValueChange();
          editValue = "";
          editMode = false;
          repaint();
        }
      } catch (NumberFormatException n) {
      }
    }

  }

  // Set up the Value
  public void setValue(double v) {
    value = near(v);
    repaint();
  }

  // Return the whellswitch value
  public double getValue() {
    return value;
  }

  // Set the Font of the wheelSwitch
  public void setFont(Font f) {
    super.setFont(f);
    // reset digit size
    dz = null;
    computeDigitSize();
    placeComponents();
  }

  // Return the digit size in pixel
  public Dimension getDigitSize() {
    return dz;
  }

  // Returns if true if the component has a border
  public boolean hasBorder() {
    return getBorder() != null;
  }

  // Call when user click on top button
  private void clickUp(MouseEvent evt) {

    int i = 0;
    boolean found = false;

    if (Double.isNaN(value))
      return;

    while (i < nbButton && !found) {
      found = (evt.getSource() == buttons_up[i]);
      if (!found) i++;
    }

    if (!editMode && found) {
      double newValue = near(value + Math.pow(10, (intNumber - i - 1)));
      if (Math.abs(newValue) < maxValue) value = newValue;
      fireValueChange();
      repaint();
    }
    grabFocus();
  }

  // Call when user click on bottom buttons
  private void clickDown(MouseEvent evt) {

    int i = 0;
    boolean found = false;

    if (Double.isNaN(value))
      return;

    while (i < nbButton && !found) {
      found = (evt.getSource() == buttons_down[i]);
      if (!found) i++;
    }

    if (!editMode && found) {
      double newValue = near(value - Math.pow(10, (intNumber - i - 1)));
      if (Math.abs(newValue) < maxValue) value = newValue;
      fireValueChange();
      repaint();
    }
    grabFocus();
  }

  // Set the color of buttons
  public void setButtonColor(Color c) {

    int i;

    buttonBackground = c;

    for (i = 0; i < nbButton; i++) {
      buttons_up[i].setBackground(buttonBackground);
      buttons_down[i].setBackground(buttonBackground);
    }

  }

  // Set the format as C format (only "%x.yf" or "%xd" is supported)
  public void setFormat(String format) {

    if (format.length() <= 2) {
      System.out.println("WheelSwitch: Invalid format use %x.yf or %xd");
      return;
    }

    String f = format.replace('.', '_');
    f = f.replace('%', '0');
    f = f.substring(0, f.length() - 1);

    String[] s = f.split("_");

    try {

      if (s.length == 2) {
        int a = Integer.parseInt(s[0]);
        int b = Integer.parseInt(s[1]);

        if (a <= b) {
          System.out.println("WheelSwitch: Invalid format a<b in %a.bf");
        } else {
          setPrecision(a - b, b);
        }
      } else if (s.length == 1) {
        int a = Integer.parseInt(s[0]);
        setPrecision(a, 0);
      }

    } catch (NumberFormatException n) {
      System.out.println("WheelSwitch: Invalid format use %x.yf or %xd");
    }

  }

  // Set the precision of the wheelswitch
  // inb = number of digit for the integer part
  // fnb = number of digit for the decimal part
  public void setPrecision(int inb, int fnb) {

    int i;
    int nb = nbButton;

    // Reset nbButton to zero during precision update
    nbButton = 0;

    // Remove old button
    for (i = 0; i < nb; i++) {
      remove(buttons_up[i]);
      remove(buttons_down[i]);
      buttons_up[i] = null;
      buttons_down[i] = null;
    }

    // Create new buttons

    intNumber = inb;
    if (intNumber < 1) intNumber = 1;
    fracNumber = fnb;
    if (fracNumber < 0) fracNumber = 0;

    nb = intNumber + fracNumber;
    buttons_up = new JArrowButton[nb];
    buttons_down = new JArrowButton[nb];

    maxValue = Math.pow(10, intNumber);

    for (i = 0; i < nb; i++) {

      // Top buttons
      buttons_up[i] = new JArrowButton();
      add(buttons_up[i]);
      buttons_up[i].addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent evt) {
          clickUp(evt);
        }
      });
      buttons_up[i].setBackground(buttonBackground);

      // Bottom buttons
      buttons_down[i] = new JArrowButton();
      add(buttons_down[i]);
      buttons_down[i].setOrientation(JArrowButton.DOWN);
      buttons_down[i].addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent evt) {
          clickDown(evt);
        }
      });
      buttons_down[i].setBackground(buttonBackground);

    }

    value = near(value);
    nbButton = nb;

    placeComponents();
    repaint();

  }

  // Compute minimun size for the digit
  // according to current font
  private void computeDigitSize() {

    int max_width = 0;
    int max_height = 0;
    int i;

    if (dz == null) {

      // Create a Buffered image to get high precision
      // Font metrics
      BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
      Graphics g = img.getGraphics();
      g.setFont(getFont());
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
          RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      FontRenderContext frc = g2.getFontRenderContext();

      String[] charSet = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

      for (i = 0; i < charSet.length; i++) {

        Rectangle2D b = g.getFont().getStringBounds(charSet[i], frc);
        int w = (int) b.getWidth() + 1;
        if (w > max_width) max_width = w;

      }

      max_height = (int) g.getFont().getLineMetrics("0123456789", frc).getAscent() + 1;
      dz = new Dimension(max_width, max_height);

      g.dispose();

    }

  }


  // Round according to desired precision (fracNumber)
  private double near(double d) {
    double r = Math.pow(10, fracNumber);
    return Math.rint(d * r) / r;
  }

  //Return the digit at the specified position
  //Negative position returns decimal digits
  private String getDigit(int pos) {

    if (Double.isNaN(value))
      return "X";

    int i;

    double tmp = value;
    if (tmp < 0) tmp = -tmp;

    if (pos >= 0) {

      // Integer part
      tmp = tmp / Math.pow(10.0, pos);

    } else {

      // Decimal part
      // Round to nearest int
      // Value must be rounded to desirec prec see near()
      tmp += (0.5 / Math.pow(10.0, fracNumber));
      int f = (int) tmp;
      tmp = tmp - f;
      tmp = tmp * Math.pow(10.0, -pos);

    }

    if (tmp > 1e9) {
      int m = (int) (tmp / 1e7);
      tmp = tmp - m * 1e7;
    }
    Integer is = new Integer((int) (tmp) % 10);
    return is.toString();

  }

  // Paint the component
  protected void paintComponent(Graphics g) {

    int w = getWidth();
    int h = getHeight();

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    g.setPaintMode();

    computeDigitSize();


    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, w, h);
    }

    g.setColor(getForeground());
    g.setFont(getFont());

    if (editMode) {

      FontMetrics fm = getFontMetrics(getFont());
      Rectangle2D b = fm.getStringBounds(editValue, g);
      int xpos = (w - (int) b.getWidth()) / 2;
      g.drawString(editValue, xpos, off_y + dz.width + dz.height - 2);

    } else {

      if (value < 0.0)
        g.drawString("-", off_x, off_y + dz.width + dz.height - 2);

      if (fracNumber > 0)
        g.drawString(".", off_x + (intNumber + 1) * dz.width + 2, off_y + dz.width + dz.height - 2);

      for (int i = 0; i < nbButton; i++) {
        int xpos;
        if (i < intNumber)
          xpos = off_x + (i + 1) * dz.width;
        else
          xpos = off_x + (i + 2) * dz.width;

        g.drawString(getDigit(intNumber - i - 1), xpos, off_y + dz.width + dz.height - 2);
      }

    }

    // Draw the focus
    if (hasFocus()) {

      Insets b;
      if (hasBorder())
        b = getInsets();
      else
        b = new Insets(1, 1, 1, 1);

      g.setColor(new Color(156, 154, 206));
      g.drawLine(b.left + 1, b.top + 1, w - b.right - 1, b.top + 1);
      g.drawLine(w - b.right - 1, b.top + 1, w - b.right - 1, h - b.bottom - 1);
      g.drawLine(w - b.right - 1, h - b.bottom - 1, b.left + 1, h - b.bottom - 1);
      g.drawLine(b.left + 1, h - b.bottom - 1, b.left + 1, b.top + 1);

    }

  }

  //Add the specified WheelSwitch Listeners
  public void addWheelSwitchListener(IWheelSwitchListener l) {
    listenerList.add(IWheelSwitchListener.class, (EventListener) l);
  }

  //Remove the specified WheelSwitch Listeners
  public void removeWheelSwitchListener(IWheelSwitchListener l) {
    listenerList.remove(IWheelSwitchListener.class, (EventListener) l);
  }

  // Fire WheelSwitchEvent to all registered listeners
  public void fireValueChange() {
    IWheelSwitchListener[] list = (IWheelSwitchListener[]) (listenerList.getListeners(IWheelSwitchListener.class));
    WheelSwitchEvent w = new WheelSwitchEvent(this, value);
    for (int i = 0; i < list.length; i++) list[i].valueChange(w);
  }

  /* main: Test the wheel switch */
  public static void main(String args[]) {

    JFrame f = new JFrame();

    WheelSwitch ws1 = new WheelSwitch();
    WheelSwitch ws2 = new WheelSwitch();
    WheelSwitch ws3 = new WheelSwitch();
    WheelSwitch ws4 = new WheelSwitch();
    WheelSwitch ws5 = new WheelSwitch();

    ws1.setFormat("%7.3f");
    ws1.setButtonColor(new Color(100, 200, 160));
    ws1.setValue(-42.9995);
    ws1.addWheelSwitchListener(new IWheelSwitchListener() {
      public void valueChange(WheelSwitchEvent e) {
        System.out.println("Value changed ws1:" + e.getValue());
      }
    });

    ws2.setFormat("%6.3f");
    ws2.setButtonColor(new Color(100, 200, 160));
    ws2.setValue(-12.5);
    ws2.setFont(new Font("Dialog", Font.BOLD, 50));
    ws2.addWheelSwitchListener(new IWheelSwitchListener() {
      public void valueChange(WheelSwitchEvent e) {
        System.out.println("Value changed ws2:" + e.getValue());
      }
    });

    ws3.setFormat("%6.0f");
    ws3.setButtonColor(new Color(100, 200, 160));
    ws3.setValue(48.0);
    ws3.setFont(new Font("Lucida Bright", Font.BOLD, 30));
    ws3.setBorder(BorderFactory.createEtchedBorder());
    ws3.addWheelSwitchListener(new IWheelSwitchListener() {
      public void valueChange(WheelSwitchEvent e) {
        System.out.println("Value changed ws3:" + e.getValue());
      }
    });

    ws4.setFormat("%5.2f");
    ws4.setButtonColor(new Color(100, 200, 160));
    ws4.setValue(28.1);
    ws4.setFont(new Font("Lucida Bright", Font.BOLD, 12));
    ws4.setBorder(BorderFactory.createEtchedBorder());
    ws4.addWheelSwitchListener(new IWheelSwitchListener() {
      public void valueChange(WheelSwitchEvent e) {
        System.out.println("Value changed ws4:" + e.getValue());
      }
    });

    ws5.setFormat("%5.2f");
    ws5.setButtonColor(new Color(100, 200, 160));
    ws5.setValue(28.1);
    ws5.setFont(new Font("Dialog", Font.PLAIN, 16));
    ws5.addWheelSwitchListener(new IWheelSwitchListener() {
      public void valueChange(WheelSwitchEvent e) {
        System.out.println("Value changed ws5:" + e.getValue());
      }
    });

    JButton b = new JButton("OK");
    b.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        System.exit(0);
      }
    });

    //f.getContentPane().setLayout(new GridLayout(3,1));
    f.getContentPane().setBackground(Color.white);
    f.getContentPane().setLayout(new FlowLayout());
    f.getContentPane().add(ws1);
    f.getContentPane().add(ws2);
    f.getContentPane().add(ws3);
    f.getContentPane().add(ws4);
    f.getContentPane().add(ws5);
    f.getContentPane().add(b);
    f.pack();
    f.setVisible(true);

  }

}

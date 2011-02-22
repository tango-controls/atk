/*
 * WheelSwitch.java
 * Author: JL Pons 2002 E.S.R.F.
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import com.braju.format.Format;

/** A WheelSwitch editor. */
public class WheelSwitch extends JComponent {

    private static Font defaultFont = new Font("Lucida Bright", Font.PLAIN, 16);
    private static Color defaultBackground = new Color(200, 200, 200);
    private static Color defaultSelectionColor = new Color(156, 154, 206);
    private int off_x; // Postion horz offset
    private int off_y; // Postion vert offset
    private int intNumber; // Number of integer digit
    private int fracNumber; // Number of decimal digit
    private int expNumber; // Number of exponential digit
    private double value; // Current value
    private double maxValue; // Maximun value
    private Dimension dz; // digit size
    private boolean editMode; // edition mode
    private String editValue; // value entered by keyboard
    private EventListenerList listenerList; // list of WheelSwitch listeners
    private String format = "%5.2f";

    // Arrow buttons
    private JArrowButton buttons_up[];
    private JArrowButton buttons_down[];
    private int nbButton = 0;
    private int selButton = 0;
    private Color buttonBackground;
    private Color selectionColor;

    /**
     * WheelSwitch constructor.
     */
    public WheelSwitch() {

        setLayout(null);
        setForeground(Color.black);
        setBackground(defaultBackground);
        setBorder(null);
        setFont(defaultFont);
        buttonBackground = getBackground();
        selectionColor = defaultSelectionColor;
        setOpaque(true);
        setPrecision(3, 2, 0);

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
                updateButtonFocus();
                repaint();
            }

            public void focusLost(FocusEvent e) {
                updateButtonFocus();
                repaint();
            }
        });

    }

    public Dimension getPreferredSize() {

        int w, h;
        Insets borderSize;

        if (hasBorder())
            borderSize = getInsets();
        else
            borderSize = new Insets(1, 1, 1, 1);

        computeDigitSize();

        if (expNumber > 0) {
            w = dz.width * (intNumber + fracNumber + expNumber + 4);
        }
        else if (fracNumber > 0) {
            w = dz.width * (intNumber + fracNumber + 2);
        }
        else {
            w = dz.width * (intNumber + fracNumber + 1);
        }

        h = dz.width * 2 + dz.height;

        return new Dimension(w + borderSize.right + borderSize.left, h
                + borderSize.top + borderSize.bottom);

    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * Sets the WheelSwitch value.
     * 
     * @param v
     *            New WheelSwitch value.
     */
    public void setValue(double v) {
        double dval1 = 0;
        double dval2 = 0;
        value = v;
        if (format.indexOf('e') > 0) {
            dval1 = getFloatPart().doubleValue();
            dval2 = getExpPart().doubleValue();
        }
        else {
            dval1 = value;
        }
        double newValue = near(dval1) * Math.pow(10, dval2);
        Double tempDouble = new Double(Double.NaN);
        try {
            Double [] tab = new Double [1];
            tab[0] = new Double (newValue);
            String valFormated = Format.sprintf(format, tab);
            tempDouble = Double.valueOf(valFormated);
        }
        catch(Exception e) {
            tempDouble = new Double(Double.NaN);
        }
        if (!tempDouble.equals(new Double(Double.NaN))) {
            value = tempDouble.doubleValue();
        }
        else {
            value = newValue;
        }
        repaint();
    }

    /**
     * Returns the current wheelswitch value.
     */
    public double getValue() {
        return value;
    }

    public void setFont(Font f) {
        super.setFont(f);
        // reset digit size
        dz = null;
        computeDigitSize();
        placeComponents();
    }

    /**
     * Returns the current digit size according the the component Font.
     */
    public Dimension getDigitSize() {
        return dz;
    }

    /**
     * Sets the color of arrow buttons.
     * 
     * @param c
     *            New button color.
     */
    public void setButtonColor(Color c) {

        int i;

        buttonBackground = c;

        for (i = 0; i < nbButton; i++) {
            buttons_up[i].setBackground(buttonBackground);
            buttons_down[i].setBackground(buttonBackground);
        }

    }

    /**
     * Returns the current button color.
     * 
     * @see #setButtonColor
     */
    public Color getButtonColor() {
        return buttonBackground;
    }

    /**
     * Sets the selected button color.
     * 
     * @param c
     *            Color for selected button.
     */
    public void setSelButtonColor(Color c) {
        selectionColor = c;
    }

    /**
     * Returns the current button selection color.
     */
    public Color getSelButtonColor() {
        return selectionColor;
    }

    /**
     * Set the format as C format (only "%x.yf" or "%xd" is supported). This
     * will change the button configuration.
     * 
     * @param format
     *            New wheelswitch format.
     */
    public void setFormat(String aformat) {
        String oldFormat = format;
        double oldValue = value;
        // format validation
        if (aformat.length() <= 2) {
            System.out.println("WheelSwitch: Invalid format use %x.yf, %x.ye, %x.yE or %xd");
            return;
        }
        else if (aformat.lastIndexOf('%') != 0) {
            System.out.println("WheelSwitch: Invalid format use %x.yf, %x.ye, %x.yE or %xd");
            return;
        }
        else if (aformat.toLowerCase().indexOf('e') != -1
                && aformat.toLowerCase().indexOf('e') != aformat.length() - 1
                && aformat.indexOf('.') != aformat.lastIndexOf('.')
                && aformat.indexOf('.') < 1) {
            System.out.println("WheelSwitch: Invalid format use %x.yf, %x.ye, %x.yE or %xd");
            return;
        }
        else if (aformat.toLowerCase().indexOf('f') != -1
                && aformat.toLowerCase().indexOf('f') != aformat.length() - 1
                && aformat.indexOf('.') != aformat.lastIndexOf('.')
                && aformat.indexOf('.') < 1) {
            System.out.println("WheelSwitch: Invalid format use %x.yf, %x.ye, %x.yE or %xd");
            return;
        }
        else if (aformat.toLowerCase().indexOf('d') != -1
                && aformat.toLowerCase().indexOf('d') != aformat.length() - 1) {
            System.out.println("WheelSwitch: Invalid format use %x.yf, %x.ye, %x.yE or %xd");
            return;
        }

        format = aformat.toLowerCase(); // to associate %x.ye with %x.yE

        String f = format.replace('.', '_');
        f = f.replace('%', '0');
        f = f.substring(0, f.length() - 1);

        String[] s = f.split("_");

        try {

            if (s.length == 2) {
                int a = Integer.parseInt(s[0]);
                int b = Integer.parseInt(s[1]);

                if (a <= b) {
                    System.out.println("WheelSwitch: Invalid format a<b in %x.yf or %x.ye");
                }
                else {
                    // scientific format
                    if (format.indexOf('e') != -1) {
                        String temp = Format.sprintf(format, new Object[] { new Double(value) });
                        if (temp != null) {
                            reformat(temp, a-b, b);
                        }
                        else {
                            System.out.println("WheelSwitch: Invalid format use %x.yf, %x.ye, %x.yE or %xd");
                            format = oldFormat;
                            value = oldValue;
                        }
                    }
                    // float
                    else {
                        setPrecision(a - b, b, 0);
                    }
                }
            }
            // integer
            else if (s.length == 1) {
                int a = Integer.parseInt(s[0]);
                setPrecision(a, 0, 0);
            }

        }
        catch (NumberFormatException n) {
            System.out.println("WheelSwitch: Invalid format use %x.yf, %x.ye, %x.yE or %xd");
            format = oldFormat;
            value = oldValue;
        }

    }

    /*
     * This function is necessary to recalculate the number of digits for scientific format
     */
    private void reformat(String valFormated, int integerPart, int decimalPart) {
        String valueFormated = valFormated.toLowerCase();
        int e = valueFormated.indexOf('e');
        int plus = valueFormated.lastIndexOf('+');
        int moins = valueFormated.lastIndexOf('-');
        if (plus > e) {
            e = plus;
        }
        else if (moins > e) {
            e = moins;
        }
        valueFormated = valueFormated.substring(e+1, valueFormated.length());
        setPrecision(integerPart, decimalPart, valueFormated.length());
    }
    
    /**
     * Set the precision of this wheelswitch.
     * 
     * @param inb
     *            number of digit for the integer part
     * @param fnb
     *            number of digit for the decimal part
     */
    synchronized public void setPrecision(int inb, int fnb, int enb) {

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
        if (intNumber < 1) {
            intNumber = 1;
        }
        fracNumber = fnb;
        if (fracNumber < 0) {
            fracNumber = 0;
        }
        expNumber = enb;
        if (expNumber < 0) {
            expNumber = 0;
        }

        nb = intNumber + fracNumber + expNumber;
        buttons_up = new JArrowButton[nb];
        buttons_down = new JArrowButton[nb];

        if (expNumber == 0) {
            maxValue = Math.pow(10, intNumber);
        }
        else {
            maxValue = Double.MAX_VALUE;
        }

        for (i = 0; i < nb; i++) {

            // Top buttons
            buttons_up[i] = new JArrowButton();
            add(buttons_up[i]);
            buttons_up[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    clickUp(evt);
                }
            });

            // Bottom buttons
            buttons_down[i] = new JArrowButton();
            add(buttons_down[i]);
            buttons_down[i].setOrientation(JArrowButton.DOWN);
            buttons_down[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    clickDown(evt);
                }
            });

        }
        double dval1 = 0;
        double dval2 = 0;
        if (format.indexOf('e') > 0) {
            dval1 = getFloatPart().doubleValue();
            dval2 = getExpPart().doubleValue();
        }
        else {
            dval1 = value;
        }
        //value = near(dval1) * Math.pow(10, dval2);
        setValue(near(dval1) * Math.pow(10, dval2));
        nbButton = nb;
        selButton = nb - 1;

        placeComponents();
        updateButtonFocus();
        repaint();

    }

    // Returns if true if the component has a border
    private boolean hasBorder() {
        return getBorder() != null;
    }

    // Call when user click on top button
    private void clickUp(MouseEvent evt) {

        int i = 0;
        boolean found = false;

        grabFocus();

        if (Double.isNaN(value))
            return;

        while (i < nbButton && !found) {
            found = (evt.getSource() == buttons_up[i]);
            if (!found)
                i++;
        }

        if (!editMode && found) {
            increaseValue(i);
            selButton = i;
            updateButtonFocus();
            repaint();
        }
    }

    // Call when user click on bottom buttons
    private void clickDown(MouseEvent evt) {

        int i = 0;
        boolean found = false;

        grabFocus();

        if (Double.isNaN(value))
            return;

        while (i < nbButton && !found) {
            found = (evt.getSource() == buttons_down[i]);
            if (!found)
                i++;
        }

        if (!editMode && found) {
            decreaseValue(i);
            selButton = i;
            updateButtonFocus();
            repaint();
        }
    }

    // Compute minimun size for the digit
    // according to current font
    private void computeDigitSize() {

        int max_width = 0;
        int max_height = 0;
        int i;

        if (dz == null) {

            String[] charSet = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
                    "9", "E", "+", "-", " " };

            for (i = 0; i < charSet.length; i++) {

                Dimension b = ATKGraphicsUtils.measureString(charSet[i],
                        getFont());
                int w = b.width + 1;
                if (w > max_width)
                    max_width = w;

            }

            max_height = (int) ATKGraphicsUtils.getLineMetrics("0123456789E+- ",
                    getFont()).getAscent() + 1;
            dz = new Dimension(max_width, max_height);

        }

    }

    // Round according to desired precision (fracNumber)
    private double near(double d) {
        double r = Math.pow(10, fracNumber);
        return Math.rint(d * r) / r;
    }

    //Return the digit at the specified position
    private String getDigit(int pos) {

        if (Double.isNaN(value)) {
            return "X";
        }

        String valueFormated = Format.sprintf(format, new Object[] { new Double(value) });
        if (valueFormated == null) {
            return "X";
        }
        else {
            valueFormated = valueFormated.toLowerCase();
            valueFormated = valueFormated.replaceAll("\\-", "");
            valueFormated = valueFormated.replaceAll("\\+", "");
            valueFormated = valueFormated.replaceAll(" ", "");
            String intPart = "";
            if (format.indexOf('d') != -1) {
                intPart = valueFormated;
            }
            else {
                intPart = valueFormated.substring(0, valueFormated.indexOf('.'));
            }
            if (pos < intNumber) {
                if (intPart.length() < intNumber) {
                    if (pos < intNumber - intPart.length()) {
                        return "0";
                    }
                    else {
                        return intPart.substring(pos + intPart.length() - intNumber, pos + 1 + intPart.length() - intNumber);
                    }
                }
                else {
                    return intPart.substring(pos, pos + 1);
                }
            }
            else if (pos < intNumber + fracNumber) {
                return valueFormated.substring(pos + 1 + intPart.length() - intNumber, pos + 2 + intPart.length() - intNumber);
            }
            else {
                int e = valueFormated.indexOf('e');
                return valueFormated.substring(pos + e + 1 - intNumber - fracNumber, pos + e + 2 - intNumber - fracNumber);
            }
        }
        /*double tmp = value;
        if (tmp < 0) {
            tmp = -tmp;
        }

        if (pos >= 0) {
            // Integer part
            tmp = tmp / Math.pow(10.0, pos);
        }
        else {

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
        return is.toString();*/

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

        }
        else {

            if (value < 0.0) {
                g.drawString("-", off_x, off_y + dz.width + dz.height - 2);
            }
            if (fracNumber > 0) {
                g.drawString(".", off_x + (intNumber + 1) * dz.width + 2, off_y
                        + dz.width + dz.height - 2);
            }
            if (expNumber > 0) {
                g.drawString("E", off_x + (intNumber + fracNumber + 2)
                        * dz.width + 2, off_y + dz.width + dz.height - 2);
                String ePart = "" + getExpPart();
                if (ePart != null) {
                    String s = ePart.substring(0, 1);
                    if (!"-".equals(s)) {
                        s = "+";
                    }
                    g.drawString(s, off_x + (intNumber + fracNumber + 3)
                            * dz.width + 2, off_y + dz.width + dz.height - 2);
                }
            }

            for (int i = 0; i < nbButton; i++) {
                int xpos;
                if (i < intNumber) {
                    xpos = off_x + (i + 1) * dz.width;
                }
                else if (i < intNumber + fracNumber) {
                    xpos = off_x + (i + 2) * dz.width;
                }
                else {
                    xpos = off_x + (i + 4) * dz.width;
                }

                /*g.drawString(getDigit(intNumber - i - 1), xpos, off_y
                        + dz.width + dz.height - 2);*/
                g.drawString(getDigit(i), xpos, off_y
                        + dz.width + dz.height - 2);
            }

        }

        // Draw the focus
        if (hasFocus()) {

            Insets b;
            if (hasBorder())
                b = getInsets();
            else
                b = new Insets(1, 1, 1, 1);

            g.setColor(defaultSelectionColor);
            g.drawLine(b.left + 1, b.top + 1, w - b.right - 1, b.top + 1);
            g.drawLine(w - b.right - 1, b.top + 1, w - b.right - 1, h
                    - b.bottom - 1);
            g.drawLine(w - b.right - 1, h - b.bottom - 1, b.left + 1, h
                    - b.bottom - 1);
            g.drawLine(b.left + 1, h - b.bottom - 1, b.left + 1, b.top + 1);

        }

    }

    /**
     * Add the specified WheelSwitch Listener.
     * 
     * @param l
     *            WheelSwitch Listener
     */
    public void addWheelSwitchListener(IWheelSwitchListener l) {
        listenerList.add(IWheelSwitchListener.class, l);
    }

    /**
     * Remove the specified WheelSwitch Listener.
     * 
     * @param l
     *            WheelSwitch Listener to be removed
     */
    public void removeWheelSwitchListener(IWheelSwitchListener l) {
        listenerList.remove(IWheelSwitchListener.class, l);
    }

    private Double getExpPart() {
        int e = format.indexOf('e');
        if (e == -1) {
            return null;
        }
        else {
            String displayVal = Format.sprintf(format, new Object[] { new Double(value) });
            if (displayVal != null) {
                displayVal = displayVal.toLowerCase();
                return Double.valueOf(displayVal.substring(displayVal.indexOf('e') + 1));
            }
            else {
                return null;
            }
        }
    }
    
    private Double getFloatPart() {
        int e = format.indexOf('e');
        if (e == -1) {
            return new Double(value);
        }
        else {
            String displayVal = Format.sprintf(format, new Object[] { new Double(value) });
            if (displayVal != null) {
                displayVal = displayVal.toLowerCase();
                return Double.valueOf(displayVal.substring(0,displayVal.indexOf('e')));
            }
            else {
                return null;
            }
        }
    }
    
    // Fire WheelSwitchEvent to all registered listeners
    private void fireValueChange() {
        IWheelSwitchListener[] list = (IWheelSwitchListener[]) (listenerList
                .getListeners(IWheelSwitchListener.class));
        WheelSwitchEvent w = new WheelSwitchEvent(this, value);
        for (int i = 0; i < list.length; i++) {
            list[i].valueChange(w);
        }
    }

    // Update button color according to the focus.
    private void updateButtonFocus() {
        for (int i = 0; i < nbButton; i++) {
            if (i == selButton && hasFocus()) {
                buttons_up[i].setBackground(selectionColor);
                buttons_down[i].setBackground(selectionColor);
            }
            else {
                buttons_up[i].setBackground(buttonBackground);
                buttons_down[i].setBackground(buttonBackground);
            }
        }
    }
    
    // Increase value by 10^diz(idx)
    private void increaseValue(int idx) {

        double dval1 = 0;
        double dval2 = 0;

        if (format.indexOf('e') > 0) {
            dval1 = getFloatPart().doubleValue();
            dval2 = getExpPart().doubleValue();
        }
        else {
            dval1 = value;
        }

        if (idx < intNumber + fracNumber) {
            dval1 = incrementeValue(dval1, idx, intNumber);
        }
        else {
            int idx2 = idx - intNumber - fracNumber;
            dval2 = incrementeValue(dval2, idx2, expNumber);
        }

/*        System.out.println("near(dval1) : " + near(dval1));
        System.out.println("dval2 : " + dval2);
        System.out.println("Math.pow(10, dval2) : " + Math.pow(10, dval2));*/
        double newValue = near(dval1) * Math.pow(10, dval2);

        if (!Double.isNaN(newValue)) {
            //double newValue = near(value + Math.pow(10, (intNumber - idx -
            // 1)));
            if (Math.abs(newValue) < maxValue) {
                //value = newValue;
                setValue(newValue);
                if (format.indexOf('e') > 0) {
                    reformat(Format.sprintf(format, new Object[] { new Double(value) }), intNumber, fracNumber);
                }
            }
            fireValueChange();
        }
    }

    private double incrementeValue(double val, int idx, int ref) {
        double newValue = val + Math.pow(10, (ref - idx - 1));
        return newValue;
    }

    private double decrementeValue(double val, int idx, int ref) {
        double newValue = val - Math.pow(10, (ref - idx - 1));
        return newValue;
    }

    // Decrease value by 10^diz(idx)
    private void decreaseValue(int idx) {

        double dval1 = 0;
        double dval2 = 0;
        
        if (format.indexOf('e') > 0) {
            dval1 = getFloatPart().doubleValue();
            dval2 = getExpPart().doubleValue();
        }
        else {
            dval1 = value;
        }
        if (idx < intNumber + fracNumber) {
            dval1 = decrementeValue(dval1, idx, intNumber);
        }
        else {
            int idx2 = idx - (intNumber + fracNumber);
            dval2 = decrementeValue(dval2, idx2, expNumber);//PROBLEME : 1-->0.99999
        }

/*        System.out.println("near(dval1) : " + near(dval1));
        System.out.println("dval2 : " + dval2);
        System.out.println("Math.pow(10, dval2) : " + Math.pow(10, dval2));*/
        double newValue = near(dval1) * Math.pow(10, dval2);

        if (!Double.isNaN(newValue)) {
           
            //double newValue = near(value + Math.pow(10, (intNumber - idx -
            // 1)));
            if (Math.abs(newValue) < maxValue) {
                //value = newValue;
                setValue(newValue);
                if (format.indexOf('e') > 0) {
                    reformat(Format.sprintf(format, new Object[] { new Double(value) }), intNumber, fracNumber);
                }  
            }
            fireValueChange();
        }
        /*if (!Double.isNaN(value)) {
            double newValue = near(value - Math.pow(10, (intNumber - idx - 1)));
            if (Math.abs(newValue) < maxValue)
                value = newValue;
            fireValueChange();
        }*/
    }

    // Process Key Envent
    private void processKey(KeyEvent e) {

        char c = e.getKeyChar();
        int code = e.getKeyCode();

        // Button selection
        if (!editMode && nbButton > 0) {
            switch (code) {
            case KeyEvent.VK_RIGHT:
                selButton++;
                if (selButton >= nbButton)
                    selButton = nbButton - 1;
                updateButtonFocus();
                repaint();
                break;
            case KeyEvent.VK_LEFT:
                selButton--;
                if (selButton < 0)
                    selButton = 0;
                updateButtonFocus();
                repaint();
                break;
            case KeyEvent.VK_UP:
                increaseValue(selButton);
                repaint();
                break;
            case KeyEvent.VK_DOWN:
                decreaseValue(selButton);
                repaint();
                break;
            }
        }

        if ((c >= '0' && c <= '9') || c == '.' || c == '-') {
            editValue += c;
            editMode = true;
            repaint();
        }
        
        if ((c == 'e') || (c == 'E')) {
            if (editValue.toLowerCase().indexOf('e') == -1 && !"".equals(editValue)) {
                editValue += c;
                editMode = true;
                repaint();
            }
        }

        if (code == KeyEvent.VK_CLEAR || code == KeyEvent.VK_CANCEL
                || code == KeyEvent.VK_ESCAPE) {
            editValue = "";
            editMode = false;
            repaint();
        }

        if (editMode
                && editValue.length() > 0
                && (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE)) {
            editValue = editValue.substring(0, editValue.length() - 1);
            repaint();
        }

        if (editMode && code == KeyEvent.VK_ENTER) {
            try {
                double newValue = Double.parseDouble(editValue);
                if (Math.abs(newValue) < maxValue) {
                    value = newValue; // For a value entered manually, we don't want to round it
                    fireValueChange();
                    editValue = "";
                    editMode = false;
                    repaint();
                }
                else {
                    int r = JOptionPane
                            .showConfirmDialog(
                                    this,
                                    "Warning: value out of range allowed by the format.\nCancel editing ?",
                                    "[WheelSwitch error]",
                                    JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        editValue = "";
                        editMode = false;
                        repaint();
                    }
                }
            }
            catch (NumberFormatException n) {
            }
        }

    }

    // Place the components
    synchronized private void placeComponents() {

        int total_width;
        int total_height;
        int i;

        computeDigitSize();

        // Place buttons
        Dimension sz = getSize();

        if (expNumber >0)
            total_width = dz.width * (nbButton + 4);
        else if (fracNumber > 0)
            total_width = dz.width * (nbButton + 2);
        else
            total_width = dz.width * (nbButton + 1);

        
        total_height = dz.height + 2 * dz.width;
        
        off_x = (sz.width - total_width) / 2;
        off_y = (sz.height - total_height) / 2;

        for (i = 0; i < nbButton; i++) {
            int xpos;
            if (i < intNumber) {
                xpos = off_x + (i + 1) * dz.width + 1;
            }
            else if (i < intNumber + fracNumber) {
                xpos = off_x + (i + 2) * dz.width + 1;
            }
            else {
                xpos = off_x + (i + 4) * dz.width + 1;
            }

            // Top buttons
            buttons_up[i].setBounds(xpos, off_y + 2, dz.width - 2, dz.width - 2);

            // Bottom buttons
            int h = dz.width + dz.height + off_y;
            buttons_down[i].setBounds(xpos, h, dz.width - 2, dz.width - 2);
        }

    }

    /* main: Test the wheel switch */
    public static void main(String args[]) {
        
        JFrame f = new JFrame();

        WheelSwitch ws1 = new WheelSwitch();
        WheelSwitch ws2 = new WheelSwitch();
        WheelSwitch ws3 = new WheelSwitch();
        WheelSwitch ws4 = new WheelSwitch();
        WheelSwitch ws5 = new WheelSwitch();
        
        ws1.setFormat("%4.3e");
        ws1.setValue(-42.9995);
        ws1.addWheelSwitchListener(new IWheelSwitchListener() {
            public void valueChange(WheelSwitchEvent e) {
                System.out.println("Value changed ws1:" + e.getValue());
            }
        });

        ws2.setFormat("%6.3f");
        ws2.setValue(-12.5);
        ws2.setFont(new Font("Dialog", Font.BOLD, 50));
        ws2.addWheelSwitchListener(new IWheelSwitchListener() {
            public void valueChange(WheelSwitchEvent e) {
                System.out.println("Value changed ws2:" + e.getValue());
            }
        });

        ws3.setFormat("%6d");
        ws3.setButtonColor(new Color(100, 200, 160));
        ws3.setValue(48.0);
        ws3.setFont(new Font("Lucida Bright", Font.BOLD, 30));
        ws3.setBorder(BorderFactory.createEtchedBorder());
        ws3.addWheelSwitchListener(new IWheelSwitchListener() {
            public void valueChange(WheelSwitchEvent e) {
                System.out.println("Value changed ws3:" + e.getValue());
            }
        });

        ws4.setFormat("%5.2e");
        ws4.setButtonColor(new Color(0, 255, 0));
        ws4.setValue(28.1);
        ws4.setFont(new Font("Lucida Bright", Font.BOLD, 12));
        ws4.setBorder(BorderFactory.createEtchedBorder());
        ws4.addWheelSwitchListener(new IWheelSwitchListener() {
            public void valueChange(WheelSwitchEvent e) {
                System.out.println("Value changed ws4:" + e.getValue());
            }
        });

        ws5.setFormat("%5.2f");
        ws5.setButtonColor(new Color(255, 0, 0));
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
        f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

    }

}

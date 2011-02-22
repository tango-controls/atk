// File:          ATKNumberField.java
// Created:       2001-11-20 15:19:39, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-17 16:24:49, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import fr.esrf.tangoatk.core.INumber;
import com.braju.format.Format;
import fr.esrf.TangoDs.AttrManip;

/**
 * <code>ATKNumberField</code> is a class to handel numeric input.
 * The user can input input numbers in a normal fashion, but this field
 * also supports the notion of wheelswitch, in which the user can use
 * the arrow keys to change the value of the field. Arrow up increases
 * the digit under the cursor, arrow down decreases the digit under the
 * cursor, and arrow left or right moves the cursor.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public class ATKNumberField extends ATKField {
    protected boolean wheelSwitchEnabled = true;
    protected INumber model;
    protected Color   bg;
    protected boolean insertOK = false;

    /**
     * <code>setText</code> inserts the number passed as parameter into
     * the textfield formated with the userformat if it is there, if not
     * with the format of the attribute.
     * @param d a <code>Number</code> value
     */
    public void setText(Number d) {
	
	Object[] o = {d};
	
	if (userFormat != null) {
	    insertOK = true;
	    super.setText(userFormat.format(d));
	    insertOK = false;
	    return;
	}
	
	if (format.indexOf('%') == -1) {
	    super.setText(AttrManip.format(format, d.doubleValue()));
	    return;
	}
	super.setText(Format.sprintf(format, o));
    }
    
    /**
     * <code>setValue</code> sets the numeric value of this field
     *
     * @param d a <code>Number</code> value
     */
    public void setValue(Number d) {
        if (isEditable() && receivedEvent) return;
        receivedEvent = true;
	int dot = getCaret().getDot();
	setText(d);
	getCaret().setDot(dot);
    }

    
    /**
     * <code>getValue</code> returns the value of this field.
     *
     * @return a <code>Number</code> value
     */
    public Number getValue() {
	return new Double(getText());
    }
    
    /**
     * <code>setModel</code> sets the model for this field.
     *
     * @param m an <code>INumber</code> value
     */
    public void setModel(INumber m) {
	model = m;
	receivedEvent = false;
	modelEditable = model.isWriteable();
	super.setEditable(editable && modelEditable);
	Number number = model.getNumber();

	if (number == null ) {
	    return;
	} 
	init();
	setValue(number);
    }
    
    public ATKNumberField() {

    }

    private void init() {

	setText(error);
	bg = getBackground();
	setHorizontalAlignment(JTextField.RIGHT);

	if (!isEditable()) return;

	setCaret(new XORCaret());
	
	Keymap parent = getKeymap();
	Keymap map = JTextComponent.addKeymap("wheelmap", parent);

	KeyStroke up = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
	KeyStroke down = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
	KeyStroke left = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false);
	KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false);
	KeyStroke toggleWheelSwitch = KeyStroke.getKeyStroke(KeyEvent.VK_W,
							     InputEvent.CTRL_MASK, false);
	
	map.addActionForKeyStroke(up, new AbstractAction () {
		public void actionPerformed(ActionEvent e) {
		    upStroke(e);
		}
	    }
				  );

	map.addActionForKeyStroke(down, new AbstractAction () {
		public void actionPerformed(ActionEvent e) {
		    downStroke(e);
		}
	    }
				  );
	map.addActionForKeyStroke(left, new AbstractAction () {
		public void actionPerformed(ActionEvent e) {
		    leftStroke(e);
		}
	    }
				  );
	map.addActionForKeyStroke(right, new AbstractAction () {
		public void actionPerformed(ActionEvent e) {
		    rightStroke(e);
		}
	    }
				  );
	map.addActionForKeyStroke(toggleWheelSwitch, new AbstractAction () {
		public void actionPerformed(ActionEvent e) {
		    setWheelSwitchEnabled(!isWheelSwitchEnabled());
		}
	    }
				  );

	setKeymap(map);

	addMouseListener(new java.awt.event.MouseAdapter() {
		public void mouseEntered(java.awt.event.MouseEvent evt) {
		    getCaret().paint(getGraphics());
		}
	    });
    }


    protected void enter(ActionEvent e) {
	inserting = false;
	newValue(getValue());

    }

    protected void newValue(Number d) {
	try {
	    model.setNumber(d);	     
	} catch (IllegalArgumentException ex) {
	    setText(model.getNumber());
	} // end of try-catch
    }
    

    
    /**
     * <code>setWheelSwitchEnabled</code> sets the enabledness of the
     * wheelswitch feature
     *
     * @param b a <code>boolean</code> value
     */
    public void setWheelSwitchEnabled(boolean b) {
	wheelSwitchEnabled = b;
	if (b) {
	    setToolTipText("Wheel switch enabled, use arrow up or arrow down to adjust the values");
	} else {
	    setToolTipText("Wheel switch disabled, press C-w to enable"); 
	} // end of else
    }

    /**
     * <code>isWheelSwitchEnabled</code> is it enabled?
     *
     * @return a <code>boolean</code> value
     */
    public boolean isWheelSwitchEnabled() {
	return wheelSwitchEnabled;
    }

    protected double getFactor() {
	int i = getCaret().getDot();

	int comma = getText().indexOf('.');
	if (comma == i) return 0;

	int sign = getText().indexOf('-');
	if (sign == i) return 0
;
	sign = getText().indexOf('+');
	if (sign == i) return 0;
	
	if (comma == -1) {
	    i = getText().length() - i;	    
	} else {
	    i = comma - i;
	} 
	if (i > 0) {
	    i--;	    
	} 
	return Math.pow(10, i);
    }

    protected void downStroke(ActionEvent e) {
	if (!isWheelSwitchEnabled()) return;
	changeValue(-1);
    }

    protected void upStroke(ActionEvent e) {
	if (!isWheelSwitchEnabled()) return;
	changeValue(1);
    }

    protected void leftStroke(ActionEvent e) {
	moveLeft();
    }

    protected void rightStroke(ActionEvent e) {
	moveRight();
    }

    protected void moveRight() {
	int dot = getCaret().getDot();
	int comma = getText().indexOf('.');
	++dot;
	if (comma == dot)
	    ++dot;
	
	if (dot >= getText().length()) return;
	getCaret().setDot(dot);
    }

    protected void moveLeft() {
	int dot = getCaret().getDot();
	int comma = getText().indexOf('.');
	-- dot;
	if (comma == dot)
	    --dot;
	
	if (dot < 0) return;

	getCaret().setDot(dot);
    }

    protected void changeValue(int sign) {
	double i = getFactor() * sign;
	int dot = getCaret().getDot();
	int oldComma = getText().indexOf('.');
	int newComma;
	setText(new Double(Double.parseDouble(getText()) + i).toString());
	newComma = getText().indexOf('.');
	dot = dot + newComma - oldComma;
	
	getCaret().setDot(dot);
	newValue(getValue());

    }

	
    protected Document createDefaultModel() {
	return new NumberDocument();
    }
    
    class NumberDocument extends PlainDocument {
       	
	public void insertString(int offs, String str, AttributeSet a)
	    throws BadLocationException {
	    try {
		if (insertOK) {
		    super.insertString(0, str, a);
		    return;
		}
		
		if (str.equals(error)) {
		    super.insertString(0, str, a);
		    return;
		}
		
		if (str.equals("-") || str.equals("+")) {
		    remove(0, getLength());
		    super.insertString(0, str, a);
		    inserting = true;
		    return;
		}
		if (str.equals(".")) {
		    int comma = ATKNumberField.this.getText().indexOf(".");
		    if (comma != -1) {
			remove(comma, 1);
			if (comma < offs) offs--;
		    } 
		    
		    super.insertString(offs, str, a);
		    return;
		}
		
		Double.parseDouble(str);

		if (inserting) {
		    super.insertString(offs, str, a);		    
		} else {
		    inserting = true;
		    remove(0, getLength());
		    super.insertString(0, str, a);
		} 
	    } catch (NumberFormatException e) {

	    } // end of try-catch
	}
    }

    public static void main (String[] args) {
	JFrame f = new JFrame();
	ATKField field = new ATKNumberField();
	field.setFont(new java.awt.Font("Times", 1, 60));
	f.setContentPane(field);
	f.pack();
	f.show();
    } // end of main ()


}

class XORCaret extends DefaultCaret {
    public XORCaret() {
	addChangeListener( new javax.swing.event.ChangeListener() {
		public void stateChanged(javax.swing.event.ChangeEvent e) {
		    getComponent().repaint();
		}
	    }
			   );
    

    }

    public void paint(Graphics g) {
	super.paint(g);
	if (this.isVisible()) {

	    JTextComponent c = getComponent();
	    TextUI ui = c.getUI();
	    Rectangle r = null;
	    int dot = getDot();
	    try {
		r = ui.modelToView(c, dot);
		currentRectangle = r;
		
	    } catch (BadLocationException  e) {
		return;
	    } 
	    
	    g.setColor(Color.red);
	    lastPaintedWidth = currentWidth();
	    g.fillRect(r.x, r.y, lastPaintedWidth, r.height);


	    g.setColor(c.getBackground());

	    try {
		if ((dot < c.getDocument().getLength())) {
		    String s = getComponent().getText(dot, 1);
		    if (!Character.isISOControl(s.charAt(0))) {
			g.drawString(s, r.x, r.y + currentAscent);
			     
		    } 
		} 
	    } catch (BadLocationException  e) {
		    
	    }
	}
    }

    
    public void damage(Rectangle r) {
//  	if (r != null &&
// 	    !r.equals(currentRectangle) &&
// 	    currentRectangle != null) {
	    
// 	    r = currentRectangle;
//  	    System.out.println("damaging(" + (r.x) + ", " + r.y + ", " +
//  			       lastPaintedWidth + ", " + r.height + ")");
//  	    getComponent().repaint(r.x, r.y, lastPaintedWidth, r.height);
//  	} // end of if ()
//	getComponent().repaint();
    }

    protected int currentWidth() {
	Font f = getComponent().getFont();

	if (f != currentFont) {
	    currentFontMetrics =
		Toolkit.getDefaultToolkit().getFontMetrics(f);
	    currentAscent = currentFontMetrics.getAscent();
	}

	String current = null;

	try {
	    current = getComponent().getText(getDot(), 1);
	} catch (BadLocationException e) {
	    // ignore
	} 
		
	char currentChar;

	if (current != null && current.length() > 0) {
	    currentChar = current.charAt(0);
	} else {
	    currentChar = ' ';
	} // end of else

	if (Character.isWhitespace(currentChar)) {
	    return currentFontMetrics.charWidth(' ');
	} else {
	    return currentFontMetrics.charWidth(currentChar);
	} // end of else
    }

    protected int lastPaintedWidth = 0;
    protected Font currentFont = null;
    protected int currentAscent = 0;
    protected FontMetrics currentFontMetrics = null;
    protected Rectangle currentRectangle = null;

    
}

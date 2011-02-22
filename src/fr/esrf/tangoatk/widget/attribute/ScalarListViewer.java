// File:          ScalarListViewer.java
// Created:       2002-07-09 08:47:37, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-17 10:45:46, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.widget.util.ATKFormat;
import com.braju.format.Format;
import fr.esrf.TangoDs.AttrManip;

public class ScalarListViewer extends JPanel {

    String error = "-----";
    GridBagConstraints constraints = new GridBagConstraints();    
    ImageIcon propertyIcon;
    boolean propertyListEditable = false;
    boolean alarmEnabled = true;
    boolean valueEditable = false;
    Insets noinset = new Insets(0, 0, 0, 0);
    Insets inset = new Insets(0, 3, 0, 0);
    ATKFormat userFormat;
    String format = "";
    static final String SPACE = " ";

    public ScalarListViewer() {
	setLayout(new GridBagLayout());
	propertyIcon = fr.esrf.tangoatk.widget.icons.Icons.getPropertyIcon();
    }

    public void setModel(AttributeList list) {
	for (int i = 0; i < list.size(); i++) {
	    Object attribute = list.get(i);
	    if (!(attribute instanceof IScalarAttribute)) continue;

	    add((IScalarAttribute)attribute);
	} 
    }

    public void setUserFormat(ATKFormat format) {
	userFormat = format;
    }

    public ATKFormat getUserFormat() {
	return userFormat;
    }

    void add(IScalarAttribute attribute) {
	JLabel label = new JLabel();
	JLabel value = new JLabel();
	JButton info = new JButton("...");
	value.setOpaque(true);
	label.setFont(getFont());
	value.setFont(getFont());
	info.setFont(getFont());
	label.setHorizontalTextPosition(SwingConstants.RIGHT);
	value.setHorizontalAlignment(SwingConstants.TRAILING);
	
	info.setMargin(new java.awt.Insets(0, 5, 0, 5));
	info.setIcon(propertyIcon);
	value.setBorder(BorderFactory.createLoweredBevelBorder());
	constraints.gridy++;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.gridx = 1;
	constraints.weightx = 0.1;
	add(label, constraints);
	constraints.gridx = 2;
	constraints.weightx = 0.8;

	add(value, constraints);
	constraints.gridx = 3;
	constraints.weightx = 0;
    	constraints.gridx = 3;
	constraints.insets = inset;
	add(info, constraints);
	constraints.insets = noinset;
	ScalarAdapter adapter =
	    new ScalarAdapter(attribute, label, value, info);
    }

    public void setPropertyListEditable(boolean b) {
	propertyListEditable = b;
    }
    
    public boolean isPropertyListEditable() {
	return propertyListEditable;
    }

    public void setValueEditable(boolean b) {
	valueEditable = b;
    }

    public boolean isValueEditable() {
	return valueEditable;
    }
    
    class ScalarAdapter implements INumberScalarListener,
				   IStringScalarListener,
				   IAttributeViewer {
	JLabel label, value;
	JButton info;
	PropertyFrame propertyFrame;
	IScalarAttribute model;
	
	protected void setLabels(JLabel label, JLabel
				 value, JButton info) {
	    this.label = label;
	    this.value = value; 
	    this.info = info;

	    this.info.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
			infoMouseClicked(evt);
		    }
		});

	    this.value.setText(error + " " + error);
	}

	public IAttribute getModel() {
	    return model;
	}

	public boolean isValueEditable() {
	    return model.isWritable() && valueEditable;
	}

	void infoMouseClicked(MouseEvent evt) {
	    if (propertyFrame == null) {
		propertyFrame = new PropertyFrame();
		propertyFrame.setSize(300, 400);
		propertyFrame.setEditable(propertyListEditable);
		propertyFrame.setModel(this);
		propertyFrame.pack();
	    } 
	    propertyFrame.show();
	}

	ScalarAdapter(IScalarAttribute attribute, JLabel label,
		      JLabel value, JButton info) {
	    model = attribute;
	    format = model.getFormat();
	    setLabels(label, value, info);
	    label.setText(attribute.getLabel());

	    if (attribute instanceof INumberScalar) {
		setScalar((INumberScalar)attribute);
	    } else {
		setScalar((IStringScalar)attribute);
	    } 
	}

	protected void setScalar(INumberScalar attribute) {
	    attribute.addNumberScalarListener(this);

	}

	protected void setScalar(IStringScalar attribute) {
	    attribute.addStringScalarListener(this);
	}

	public void numberScalarChange(NumberScalarEvent evt) {
	    Double d = new Double(evt.getValue());
	    String val;
	    if (userFormat != null) {
		val = userFormat.format(d);
	    } else if (format.indexOf('%') == -1) {
		val = AttrManip.format(format, evt.getValue());
	    } else {
		Object[] o = {d};
		val = Format.sprintf(format, o);
	    } // end of else

	    value.setText(val + SPACE +  model.getUnit());
	}

	public void stringScalarChange(StringScalarEvent evt) {
	    String val;
	    String s = evt.getValue();
	    if (userFormat != null) {
		val = userFormat.format(s);
	    } else {
		Object[] o = { s };
		val = Format.sprintf(format, o);
	    } // end of else
	    value.setText(val + SPACE + model.getUnit());
	}

	public void stateChange(String state) {
	    if (!alarmEnabled) return;
	    
	    if (IAttribute.VALID == state) {
		value.setBackground(getBackground());
		
		return;
	    }
	
	    value.setBackground(AttributeStateViewer.getColor4State(state));

	}

	public void stateChange(AttributeStateEvent evt) {
	    stateChange(evt.getState());
	}

	public void errorChange(ErrorEvent evt) {
	    value.setText(error + SPACE + model.getUnit());
	    stateChange(IAttribute.UNKNOWN);
	}
    }

    public static void main (String[] args) throws Exception{
	JFrame f = new JFrame();
	ScalarListViewer slv = new ScalarListViewer();
 	AttributeList list = new AttributeList();

	slv.setFont(new java.awt.Font("Helvetica", 0, 12));
	slv.setPropertyListEditable(true);
	list.add("eas/test-api/1/Att_eas");
	list.setRefreshInterval(60);
	list.startRefresher();
	slv.setModel(list);
	f.setContentPane(slv);
	f.pack();
	f.show();
    } // end of main ()
    
}

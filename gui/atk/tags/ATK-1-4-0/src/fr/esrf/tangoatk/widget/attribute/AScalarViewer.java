/*
 * NumberScalarViewer.java
 *
 * Created on November 21, 2001, 3:35 PM
 */

package fr.esrf.tangoatk.widget.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.*;
import fr.esrf.tangoatk.widget.util.*;
import java.beans.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
/**
 *
 * @author  root 
 */
public class AScalarViewer extends javax.swing.JPanel
    implements IAttributeStateListener, IErrorListener, IAttributeViewer {
    PropertyFrame f;
    IScalarAttribute supermodel;
    Color background = getBackground();
    Trend globalTrend;
    
    /** Creates new form NumberScalarViewer */
    public AScalarViewer() {
        initComponents();
	label.setValueHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
	setUnitBorder(null);
	setLabelBorder(null);
	java.awt.Dimension id = info.getPreferredSize();
	java.awt.Dimension vd = getValue().getPreferredSize();
	id.setSize(id.getWidth(), vd.getHeight() - 1);
	info.setPreferredSize(id);
	info.setMaximumSize(id);    }
    
    public void setFont(Font f) {
	super.setFont(f);
	if(jPanel1==null) return;
	setLabelFont(f);
	setUnitFont(f);
	setValueFont(f);
	info.setFont(f);

    }

    protected void setValueField(ATKField field) {

 	GridBagLayout layout =  (GridBagLayout)jPanel1.getLayout();
 	GridBagConstraints c = layout.getConstraints(value);
 	jPanel1.remove(value);
 	value = field;
 	jPanel1.add(value, 1);
    }

    public void fullStateChange(String state) {
	if ("VALID".equals(state)) {
	    setBackground(background);
	    return;
	}
	setBackground(AttributeStateViewer.getColor4State(state));
    }
	
    public void stateChange(String state) {
	if (!alarmEnabled) return;
	
	if (fullState) {
	    fullStateChange(state);
	    return;
	}
	
	if ("VALID".equals(state)) {
	    value.setState(getBackground());
		    
	    return;
	}
	
	value.setState(AttributeStateViewer.getColor4State(state));

    }

    protected void init(IScalarAttribute model) {

	if (supermodel != null) {
	    supermodel.removeStateListener(this);
	    //	    f = null;
	}
	
	supermodel = model;
	supermodel.addStateListener(this);
	getLabel().setModel(supermodel.getProperty("label"));
        getLabel().setEditable(false);
 	getUnit().setModel(supermodel.getProperty("unit"));
        getUnit().setEditable(false);
	getValue().setFormat(supermodel.getProperty("format").getPresentation());

	javax.swing.border.Border border = getBorder();

	if (border != null
	    && border instanceof javax.swing.border.TitledBorder &&
	    supermodel != null && supermodel.getName() != null) {
	    ((javax.swing.border.TitledBorder)border).setTitle(supermodel.getName());
	}
	stateChange(model.getState());
    }

    boolean fullState = false;
    
    /**
     * Get the value of fullState.
     * @return value of fullState.
     */
    public boolean isFullState() {
	return fullState;
    }
    
    /**
     * Set the value of fullState.
     * @param v  Value to assign to fullState.
     */
    public void setFullState(boolean  v) {
	this.fullState = v;
    }
    
    public void setUserFormat(ATKFormat format) {
	value.setUserFormat(format);
    }

    public ATKFormat getUserFormat() {
	return value.getUserFormat();
    }
    

    public void setLabelFont(Font f) {
	label.setFont(f);
    }

    public Font getLabelFont() {
	return label.getFont();
    }
    
    public void setUnitFont(Font f) {
	unit.setFont(f);
    }

    public Font getUnitFont() {
	return unit.getFont();
    }
    
    public void setValueFont(Font f) {
	value.setFont(f);
    }

    public Font getValueFont() {
	return value.getFont();
    }
    
    public Dimension getLabelPreferredSize() {
	return label.getPreferredSize();
    }

    public void setLabelPreferredSize(Dimension d) {
	label.setPreferredSize(d);
    }

    public Dimension getValuePreferredSize() {
	return value.getPreferredSize();
    }

    public void setValuePreferredSize(Dimension d) {
	value.setPreferredSize(d);
    }

    public void setGlobalTrend(Trend t) {
	globalTrend = t;
    }

    public Trend getGlobalTrend() {
	return globalTrend;
    }


    public Dimension getUnitPreferredSize() {
	return unit.getPreferredSize();
    }

    public void setUnitPreferredSize(Dimension d) {
	unit.setPreferredSize(d);
    }
    
    public void setUnitMaximumLength(int characters) {
	unit.setValueMaximumLength(characters);
    }

    public int getUnitMaximumLength() {
	return unit.getValueMaximumLength();
    }

    public void setLabelMaximumLength(int characters) {
	label.setValueMaximumLength(characters);
    }

    public int getLabelMaximumLength() {
	return label.getValueMaximumLength();
    }

    public void setValueMaximumLength(int characters) {
	value.setMaximumLength(characters);
    }

    public int getValueMaximumLength() {
	return value.getMaximumLength();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
          jPanel1 = new javax.swing.JPanel();
          label = new fr.esrf.tangoatk.widget.properties.PropertyViewer();
          value = new fr.esrf.tangoatk.widget.util.ATKField();
          unit = new fr.esrf.tangoatk.widget.properties.PropertyViewer();
          info = new javax.swing.JButton();
          
          setLayout(new java.awt.GridBagLayout());
          java.awt.GridBagConstraints gridBagConstraints1;
          
          setBorder(new javax.swing.border.TitledBorder("Not Connected"));
          setAlignmentX(0.0F);
          setAlignmentY(0.0F);
          jPanel1.setLayout(new java.awt.GridBagLayout());
          java.awt.GridBagConstraints gridBagConstraints2;
          
          jPanel1.setAlignmentX(0.0F);
          jPanel1.setAlignmentY(0.0F);
          label.setLabelVisible(false);
          label.setValue("label");
          gridBagConstraints2 = new java.awt.GridBagConstraints();
          gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 2);
          gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
          gridBagConstraints2.weighty = 0.1;
          jPanel1.add(label, gridBagConstraints2);
          
          value.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
          value.setText("-------");
          gridBagConstraints2 = new java.awt.GridBagConstraints();
          gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
          gridBagConstraints2.weightx = 0.1;
          jPanel1.add(value, gridBagConstraints2);
          
          unit.setLabelVisible(false);
          unit.setValue("unit");
          gridBagConstraints2 = new java.awt.GridBagConstraints();
          gridBagConstraints2.insets = new java.awt.Insets(0, 3, 0, 0);
          gridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
          jPanel1.add(unit, gridBagConstraints2);
          
          gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 0.1;
        add(jPanel1, gridBagConstraints1);
        
        info.setFont(new java.awt.Font("Dialog", 0, 12));
        info.setText("...");
        info.setAlignmentY(0.0F);
        info.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        info.setMargin(new java.awt.Insets(0, 5, 0, 5));
        info.setMinimumSize(new java.awt.Dimension(0, 0));
        info.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                infoMouseClicked(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(info, gridBagConstraints1);

        
    }//GEN-END:initComponents

    private void infoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoMouseClicked
        // Add your handling code here:
	if (f == null) {
	    f = new PropertyFrame();
	    f.setSize(300, 400);
	    f.setEditable(propertyListEditable);
	    f.setModel(this);
	    f.pack();
	}
	
        f.show();
    }//GEN-LAST:event_infoMouseClicked

    public static void main(String [] args) {
	fr.esrf.tangoatk.core.AttributeList attributeList = new
	    fr.esrf.tangoatk.core.AttributeList();
	final NumberScalarViewer nsv = new NumberScalarViewer();
	nsv.setLabelMaximumLength(20);
	try {
	    final INumberScalar attr = (INumberScalar)attributeList.add("eas/test-api/1/Short_attr_rw");
	    nsv.setValueEditable(false);
	    nsv.setModel(attr);
	    final INumberScalar attr1 = (INumberScalar)attributeList.add("eas/test-api/1/Short_attr_w");
	    nsv.setPropertyListEditable(true);
	    attributeList.startRefresher();
	    int i = 0;
	    new Thread() {
		public void run() {
		    while (true) {
			nsv.setModel(attr1);
			try {
			    Thread.sleep(3000);			     
			} catch (Exception e) {
			    ;
			} // end of try-catch
			

			nsv.setModel(attr);
		    }
		}
	    }.start();
	    

	} catch (Exception e) {
	    System.out.println(e);
	} // end of try-catch
	

        javax.swing.JFrame f = new javax.swing.JFrame();
        f.getContentPane().add(nsv);
        f.pack();
        f.show();
    }


    public void setForeground(java.awt.Color color) {
	super.setForeground(color);
	if(jPanel1==null) return;
	unit.setForeground(color);
	label.setForeground(color);
	value.setForeground(color);
	jPanel1.setForeground(color);
	info.setForeground(color);

    }

    

    public void setBackground(java.awt.Color color) {
	background = color;
	super.setBackground(color);
	if(jPanel1==null) return;
	unit.setBackground(color);
	label.setBackground(color);
	value.setBackground(color);
	jPanel1.setBackground(color);
	info.setBackground(color);
    }
	
    public void setUnitVisible(boolean b) {
	unit.setVisible(b);
    }

    public boolean isUnitVisible() {
	return unit.isVisible();
    }

    public boolean isLabelVisible() {
	return label.isVisible();
    }

    public void setLabelVisible(boolean b) {
	label.setVisible(b);
    }

  
    public void setInfoVisible(boolean b) {
	info.setVisible(b);
    }

    public boolean isInfoVisible() {
	return info.isVisible();
    }

    boolean propertyListEditable = false;
    
    public void setPropertyListEditable(boolean b) {
	propertyListEditable = b;
 	if (f == null) return;	
 	f.setEditable(b);	    
    }
    
    public boolean isPropertyListEditable() {
	return propertyListEditable;
    }

    boolean setter = false;

    public void setSetter(boolean b) {
	setter = b;
	value.setEditable(setter);
    }

    public boolean isSetter() {
	return value.isEditable();
    }
			 
    boolean valueEditable;
    
    public void setValueEditable(boolean b) {
	valueEditable = b;
    }

    public boolean isValueEditable() {
        return valueEditable;
    }

    public void setValueBorder(javax.swing.border.Border border) {
	value.setBorder(border);
    }

    public javax.swing.border.Border getValueBorder() {
	return value.getBorder();
    }

    public void setLabelBorder(javax.swing.border.Border border) {
	label.setBorder(border);
    }

    public javax.swing.border.Border getLabelBorder() {
	return label.getBorder();
    }

    public void setUnitBorder(javax.swing.border.Border border) {
	unit.setBorder(border);
    }

    public javax.swing.border.Border getUnitBorder() {
	return unit.getBorder();
    }

    
    public void setValueOpaque(boolean isOpaque) {
	value.setOpaque(isOpaque);
    }

    public boolean isValueOpaque() {
	return value.isOpaque();
    }
    
    public void setLabelOpaque(boolean isOpaque) {
	label.setOpaque(isOpaque);
    }

    public boolean isLabelOpaque() {
	return label.isOpaque();
    }

    public void setUnitOpaque(boolean isOpaque) {
	unit.setOpaque(isOpaque);
    }

    public boolean isUnitOpaque() {
	return unit.isOpaque();
    }

    public void setOpaque(boolean isOpaque) {
	super.setOpaque(isOpaque);
	if (jPanel1 == null) return;
	setUnitOpaque(isOpaque);
	setLabelOpaque(isOpaque);
	jPanel1.setOpaque(isOpaque);
	
    }

    public boolean isOpaque() {
	return super.isOpaque();
    }
    

    protected PropertyViewer getUnit() {
	return unit;
    }

    protected PropertyViewer getLabel() {
	return label;
    }

    protected ATKField getValue() {
	return value;
    }

    public double getValueWidth() {
	return getWidth(getValue());
    }
    public double getLabelWidth() {
	return getWidth(label);
    }
    public double getUnitWidth() {
	return getWidth(unit);
    }

    public void setLabelWidth(double width) {
	setWidth(label, width);
    }
    public void setUnitWidth(double width) {
	setWidth(unit, width);
    }
    public void setValueWidth(double width) {
	setWidth(getValue(), width);
    }

    static double getWidth(JComponent c) {
	return c.getPreferredSize().getWidth();
    }

    static void setWidth(JComponent c, double width) {
	Dimension d = c.getPreferredSize();
	d.setSize(width, d.getHeight());
	c.setPreferredSize(d);
	c.setMinimumSize(d);
    }

    boolean alarmEnabled = true;
    
    /**
     * Get the value of alarmEnabled.
     * @return value of alarmEnabled.
     */
    public boolean isAlarmEnabled() {
	return alarmEnabled;
    }
    
    /**
     * Set the value of alarmEnabled.
     * @param v  Value to assign to alarmEnabled.
     */
    public void setAlarmEnabled(boolean  v) {
	this.alarmEnabled = v;
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private fr.esrf.tangoatk.widget.properties.PropertyViewer label;
    private fr.esrf.tangoatk.widget.util.ATKField value;
    private fr.esrf.tangoatk.widget.properties.PropertyViewer unit;
    private javax.swing.JButton info;
    // End of variables declaration//GEN-END:variables

    
    public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
        value.setError();
	stateChange("UNKNOWN");
    
    }
    
    public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent
			    attributeStateEvent) {

    	stateChange(attributeStateEvent.getState());    
    }

    public IAttribute getModel() {
	return supermodel;
    }
}

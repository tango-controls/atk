/*
 * IconNumberSpectrumViewer.java
 *
 * Created on March 25, 2002, 4:06 PM
 */

package fr.esrf.tangoatk.widget.attribute;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.tangoatk.core.NumberSpectrumEvent;

/**
 * 
 * @author root
 */
public class IconNumberSpectrumViewer extends javax.swing.JPanel implements ISpectrumListener {
    JLabel[] iconViewers;
    Color[] colors;
    int columns = 4;
    Icon[] icons;
    Icon invalidIcon;
    INumberSpectrum model;
    int rows = 0;
    String[][] texts;
    private double[] oldValue = null;

    /** Creates new form IconNumberSpectrumViewer */
    public IconNumberSpectrumViewer() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {// GEN-BEGIN:initComponents
        setLayout(new java.awt.BorderLayout());

    }// GEN-END:initComponents

    public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {

    }

    public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {

    }

    protected void updateIcon(JLabel icon, int val) {
        try {
            if ( val < icons.length )
                icon.setIcon( icons[val] );
            else
                icon.setIcon( getInvalidIcon() );
        } catch (Exception e) {
            icon.setIcon(getInvalidIcon());
        }

    }

    protected void updateText(JLabel icon, String[] texts, int val) {
        try {
            icon.setText(texts[val]);
        } catch (Exception e) {
            icon.setText("");
        }

    }

    protected void updateColor(JLabel icon, int val) {
        try {
            if ( val < colors.length )
                icon.setBackground( colors[val] );
            else
                icon.setBackground( getBackground() );
        } catch (Exception e) {
            icon.setBackground( getBackground() );
        }
    }



    public void spectrumChange(NumberSpectrumEvent numberSpectrumEvent) {
        double[] value = numberSpectrumEvent.getValue();

        if( ! Arrays.equals( value, oldValue) ){
            removeAll();
            iconViewers = null;
            iconViewers = new JLabel[model.getXDimension()];

            setLayout(new java.awt.GridLayout(rows, columns));

            for (int i = 0; i < iconViewers.length; i++) {
                iconViewers[i] = new JLabel(Integer.toString(i));
                iconViewers[i].setOpaque(true);
                iconViewers[i]
                        .setBorder(BorderFactory.createEtchedBorder());
                add(iconViewers[i]);
            } // end of for ()
            
            
            int length = iconViewers.length;
            for (int i = 0; i < length; i++) {
                int val = (int) value[i];
                updateIcon(iconViewers[i], val);
                if(texts != null)
                    updateText(iconViewers[i], texts[i], val);
                updateColor(iconViewers[i], val);
            } // end of for ()
            updateUI();
            
            oldValue   = (double[])value.clone();
        }
    }

    /**
     * Get the value of colors.
     * @return value of colors.
     */
    public Color[] getColors() {
        return colors;
    }

    /**
     * Set the value of colors.
     * @param v Value to assign to colors.
     */
    public void setColors(Color[] v) {
        this.colors = v;
    }

    /**
     * Get the value of texts.
     * @return value of texts.
     */
    public String[][] getTexts() {
        return texts;
    }

    /**
     * Set the value of texts.
     * @param v Value to assign to texts.
     */
    public void setTexts(String[][] v) {
        this.texts = v;
    }

    /**
     * Get the value of invalidIcon.
     * @return value of invalidIcon.
     */
    public Icon getInvalidIcon() {
        return invalidIcon;
    }

    /**
     * Set the value of invalidIcon.
     * @param v Value to assign to invalidIcon.
     */
    public void setInvalidIcon(Icon v) {
        this.invalidIcon = v;
    }

    /**
     * Get the value of columns.
     * @return value of columns.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Set the value of columns.
     * @param v Value to assign to columns.
     */
    public void setColumns(int v) {
        this.columns = v;
    }

    /**
     * Get the value of rows.
     * @return value of rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Set the value of rows.
     * @param v Value to assign to rows.
     */
    public void setRows(int v) {
        this.rows = v;
    }

    /**
     * Get the value of model.
     * @return value of model.
     */
    public INumberSpectrum getModel() {
        return model;
    }

    /**
     * Set the value of model.
     * @param v Value to assign to model.
     */
    public void setModel(INumberSpectrum v) {
        if (this.model == null || !this.model.equals(model)) {
            if (this.model != null) {
                // Unregistering listener from model
                model.removeSpectrumListener(this);
                removeAll();
                iconViewers = null;
            }

            this.model = v;

            if (this.model != null) {

                iconViewers = new JLabel[model.getMaxXDimension()];

                setLayout(new java.awt.GridLayout(rows, columns));

                for (int i = 0; i < iconViewers.length; i++) {
                    iconViewers[i] = new JLabel(Integer.toString(i));
                    iconViewers[i].setOpaque(true);
                    iconViewers[i].setBorder(BorderFactory.createEtchedBorder());
                    add(iconViewers[i]);
                } // end of for ()

                // Registering listener from model
                model.addSpectrumListener(this);
            }
        }
    }

    public void setIconBorders(javax.swing.border.Border border) {
        for (int i = 0; i < iconViewers.length; i++) {
            iconViewers[i].setBorder(border);
        }
    }

    public Border getIconBorders() {
        return iconViewers[0].getBorder();
    }

    public void setFont(java.awt.Font font) {
        if (iconViewers == null) return;

        for (int i = 0; i < iconViewers.length; i++) {
            iconViewers[i].setFont(font);
        }
    }

    public Font getFont() {
        if (iconViewers == null || iconViewers[0] == null)
            return super.getFont();

        return iconViewers[0].getFont();
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        if (iconViewers == null) return;

        for (int i = 0; i < iconViewers.length; i++) {
            iconViewers[i].setHorizontalAlignment(horizontalAlignment);
        }
    }

    public int getHorizontalAlignment() {
        if (iconViewers == null) return 0;

        return iconViewers[0].getHorizontalAlignment();
    }

    /**
     * Get the value of icons.
     * @return value of icons.
     */
    public Icon[] getIcons() {
        return icons;
    }

    /**
     * Set the value of icons.
     * @param v Value to assign to icons.
     */
    public void setIcons(Icon[] v) {
        this.icons = v;
    }

    public static void main(String[] args) throws Exception {

        String attributeName = "tests/machine/status/stateOfIDs";
        if (args.length > 0)
        {
            attributeName = args[0];
        }

        Icon[] icons = new Icon[13];

        AttributeList list = new AttributeList();
        INumberSpectrum ns = (INumberSpectrum) list.add(attributeName);
        IconNumberSpectrumViewer insv = new IconNumberSpectrumViewer();
        icons[0] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/bulbDisabled.gif"));
        icons[1] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/bulbEnabled.gif"));
        icons[2] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif"));
        icons[3] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledBrownGray.gif"));
        icons[4] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGray.gif"));
        icons[5] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledDarkOrange.gif"));
        icons[6] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledGray.gif"));
        icons[7] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif"));
        icons[8] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledLightOrange.gif"));
        icons[9] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledPink.gif"));
        icons[10] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledRed.gif"));
        icons[11] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif"));
        icons[12] = new ImageIcon(insv.getClass().getResource("/fr/esrf/tangoatk/widget/icons/ledYellow.gif"));

        insv.setColumns(4);
        insv.setIcons(icons);
//        insv.setColors(new Color[]{Color.GREEN,Color.YELLOW,Color.RED,Color.PINK,Color.BLACK,Color.GRAY});

        insv.setModel(ns);
        list.startRefresher();
        JFrame f = new JFrame();
        f.getContentPane().add(insv);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    } // end of main ()

}

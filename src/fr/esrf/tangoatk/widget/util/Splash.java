/*
 * Splash2.java
 *
 * Created on February 28, 2002, 10:37 AM
 */

package fr.esrf.tangoatk.widget.util;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  root
 */
public class Splash extends javax.swing.JWindow {

    /** Creates new form Splash */
    public Splash() {
        initComponents();
	Dimension screenSize =
	    Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = icon.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
	show();
    }

    private void initComponents() {//GEN-BEGIN:initComponents
	icon = new JLabel();
	splashPanel = new SplashPanel();
        setGlassPane(splashPanel);
        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/esrf.gif")));
	java.awt.GridBagConstraints gridBagConstraints2;
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
	getContentPane().setLayout(new java.awt.GridBagLayout());
        getContentPane().add(icon, gridBagConstraints2);
	getGlassPane().setVisible(true);        
        pack();
    }//GEN-END:initComponents


    public void setPanelForeground(java.awt.Color color) {
	super.setForeground(color);
	splashPanel.setForeground(color);
    }
    
    public void setAuthor(String s) {
    }

    public String getAuthor() {
	return "";
    }

    public void setVersion(String s) {
    }

    public String getVersion() {
	return "";
    }
    
    public void setCopyright(String copyright) {
        splashPanel.setCopyright(copyright);
    }
    
    public String getCopyright() {
        return splashPanel.getCopyright();
    }
    
    public void setMessage(String message) {
        splashPanel.setMessage(message);
    }
    
    public String getMessage() {
        return splashPanel.getMessage();
    }
    
    public void setTitle(String title) {
        splashPanel.setTitle(title);
    }
    
    public String getTitle() {
        return splashPanel.getTitle();
    }

    public void initProgress(int i) {
	splashPanel.initProgress();
	splashPanel.setMaxProgress(i);
    }

    public void initProgress() {
	splashPanel.initProgress();
    }
    
    public void setMaxProgress(int i) {
	splashPanel.setMaxProgress(i);
    }

    public void progress(int i) {
	splashPanel.progress(i);
    }
    public void setIndeterminateProgress(boolean b) {
	splashPanel.setIndeterminateProgress(b);
    }
    
    public static void main (String[] args) {
	new Splash().show();
    } // end of main ()


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private SplashPanel splashPanel;
    private javax.swing.JLabel icon;
    // End of variables declaration//GEN-END:variables
    
}


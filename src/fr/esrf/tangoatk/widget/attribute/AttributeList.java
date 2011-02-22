/*At
 * AttributeList.java
 *
 * Created on September 27, 2001, 8:37 AM
 */

package fr.esrf.tangoatk.widget.attribute;
import java.util.*;
import javax.swing.*;
public class AttributeList extends javax.swing.JList {
    /** Creates new customizer AttributeList */
    public AttributeList() {
        initComponents ();
    
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        
        setLayout(new java.awt.BorderLayout());
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        
    }//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // Add your handling code here:
        int i = locationToIndex(evt.getPoint());
	System.out.println("asking viewerList(" + i + ") to show...");
	fireSelectionValueChanged(i, i, false);
    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}

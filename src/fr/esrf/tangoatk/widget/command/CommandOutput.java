/*
 * CommandOutput.java
 *
 * Created on January 16, 2002, 4:52 PM
 */

package fr.esrf.tangoatk.widget.command;
import java.util.*;
/**
 *
 * @author  root
 */
public class CommandOutput extends javax.swing.JPanel {
    
    /** Creates new form CommandOutput */
    public CommandOutput() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
          jScrollPane1 = new javax.swing.JScrollPane();
          jTextArea1 = new javax.swing.JTextArea();
          
          setLayout(new java.awt.GridBagLayout());
          java.awt.GridBagConstraints gridBagConstraints1;
          
          jScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          jTextArea1.setBackground(new java.awt.Color(204, 204, 204));
          jTextArea1.setColumns(40);
          jTextArea1.setEditable(false);
          jTextArea1.setRows(10);
          jScrollPane1.setViewportView(jTextArea1);
          
          gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 0.1;
        gridBagConstraints1.weighty = 0.1;
        add(jScrollPane1, gridBagConstraints1);
        
    }//GEN-END:initComponents

    public void setResult(String result) {
	jTextArea1.setText(result);
    }
	
    public void setResult(java.util.List result)
    {
	jTextArea1.setText("coucou\n"+result);
	return;
	/*
	jTextArea1.setText("");
	if (result == null) return;
	
	for (Iterator i = result.iterator(); i.hasNext();)
	{
	    jTextArea1.append((String)i.next());
	    jTextArea1.append("\n");
	}*/ // end of for ()
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    
}

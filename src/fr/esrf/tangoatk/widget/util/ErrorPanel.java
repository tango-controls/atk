/*
 * ErrorPanel.java
 *
 * Created on April 25, 2002, 2:47 PM
 */

package fr.esrf.tangoatk.widget.util;
import java.awt.event.*;
import javax.swing.JTable;
import javax.swing.table.*;
import java.awt.Component;
/**
 *
 * @author  root
 */
public class ErrorPanel extends javax.swing.JPanel implements fr.esrf.tangoatk.core.IErrorListener,
                                                              fr.esrf.tangoatk.core.ISetErrorListener
{
    ErrorAdapter errorAdapter = new ErrorAdapter();
    /** Creates new form ErrorPanel */
    int selectedRow;

    public ErrorPanel() {
	selectedRow = -1;
        initComponents();

	// this is a really ugly hack!! Please fix this and do it
	// properly - Erik.

 	errorTable.getColumnModel().getColumn(ErrorAdapter.TIME).setMaxWidth(70);
 	errorTable.getColumnModel().getColumn(ErrorAdapter.SEVERITY).setMaxWidth(50);
 	errorTable.getColumnModel().getColumn(ErrorAdapter.SOURCE).setMaxWidth(250);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
          jPopupMenu1 = new javax.swing.JPopupMenu();
          showItem = new javax.swing.JMenuItem();
          stopItem = new javax.swing.JMenuItem();
          startItem = new javax.swing.JMenuItem();
          jSplitPane1 = new javax.swing.JSplitPane();
          jScrollPane2 = new javax.swing.JScrollPane();
          errorTree = new fr.esrf.tangoatk.widget.util.ErrorTree();
          jScrollPane3 = new javax.swing.JScrollPane();
          errorTable = new javax.swing.JTable();
          jToolBar5 = new javax.swing.JToolBar();
          stopButton = new javax.swing.JToggleButton();
          panicBox = new javax.swing.JCheckBox();
          errorBox = new javax.swing.JCheckBox();
          warningBox = new javax.swing.JCheckBox();
          
          showItem.setText("Show Error");
          showItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  showItemActionPerformed(evt);
              }
          });
          
          jPopupMenu1.add(showItem);
          stopItem.setText("Stop");
          stopItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  stopItemActionPerformed(evt);
              }
          });
          
          jPopupMenu1.add(stopItem);
          startItem.setText("Start");
          startItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  startItemActionPerformed(evt);
              }
          });
          
          jPopupMenu1.add(startItem);
          
            setLayout(new java.awt.GridBagLayout());
            java.awt.GridBagConstraints gridBagConstraints1;
            
            jSplitPane1.setDividerSize(4);
            jSplitPane1.setResizeWeight(0.7);
            errorTree.setPreferredSize(new java.awt.Dimension(200, 300));
            jScrollPane2.setViewportView(errorTree);
            
            jSplitPane1.setRightComponent(jScrollPane2);
          
          jScrollPane3.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            errorTable.setModel(errorAdapter);
            errorTable.setPreferredScrollableViewportSize(new java.awt.Dimension(600, 300));
            errorTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    errorTableMousePressed(evt);
                }
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    errorTableMouseReleased(evt);
                }
            });
            
            jScrollPane3.setViewportView(errorTable);
            
            jSplitPane1.setLeftComponent(jScrollPane3);
          
          gridBagConstraints1 = new java.awt.GridBagConstraints();
          gridBagConstraints1.gridx = 0;
          gridBagConstraints1.gridy = 1;
          gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
          gridBagConstraints1.weightx = 0.1;
          gridBagConstraints1.weighty = 0.1;
          add(jSplitPane1, gridBagConstraints1);
          
          stopButton.setText("Stop");
          stopButton.setToolTipText("Stops updating");
          stopButton.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  stopButtonActionPerformed(evt);
              }
          });
          
          jToolBar5.add(stopButton);
          
          panicBox.setSelected(true);
          panicBox.setText("Panic");
          panicBox.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  panicBoxActionPerformed(evt);
              }
          });
          
          jToolBar5.add(panicBox);
          
          errorBox.setSelected(true);
          errorBox.setText("Error");
          errorBox.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  errorBoxActionPerformed(evt);
              }
          });
          
          jToolBar5.add(errorBox);
          
          warningBox.setSelected(true);
          warningBox.setText("Warning");
          warningBox.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  warningBoxActionPerformed(evt);
              }
          });
          
          jToolBar5.add(warningBox);
          
          gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 0.1;
        add(jToolBar5, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void panicBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_panicBoxActionPerformed
        // Add your handling code here:
	errorAdapter.showPanic(panicBox.isSelected());
    }//GEN-LAST:event_panicBoxActionPerformed

    private void errorBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorBoxActionPerformed
        // Add your handling code here:
	errorAdapter.showError(errorBox.isSelected());
    }//GEN-LAST:event_errorBoxActionPerformed

    private void warningBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningBoxActionPerformed
        // Add your handling code here:
	errorAdapter.showWarning(warningBox.isSelected());
    }//GEN-LAST:event_warningBoxActionPerformed


    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_stopButtonActionPerformed
        // Add your handling code here:
	
	stopped = stopButton.getText().equals("Stop");
	if (stopped)
	{
	   stopButton.setText("Resume");
           stopButton.setToolTipText("Restarts updating");
	}
	else
	{
	   stopButton.setText("Stop");
           stopButton.setToolTipText("Stops updating");
 	}
	
    }//GEN-LAST:event_stopButtonActionPerformed

    private void showItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showItemActionPerformed
        // Add your handling code here:
        errorTree.addErrors(errorAdapter.getErrorNumber(selectedRow));
        selectedRow = -1;
    }//GEN-LAST:event_showItemActionPerformed


    private void stopItemActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_stopItemActionPerformed
        // Add your handling code here:
	
        stopped = true;
	stopButton.setText("Resume");
        stopButton.setToolTipText("Restarts updating");

    }//GEN-LAST:event_stopItemActionPerformed

    private void startItemActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_startItemActionPerformed
        // Add your handling code here:
	
        stopped = false;
	stopButton.setText("Stop");
        stopButton.setToolTipText("Stops updating");
	
    }//GEN-LAST:event_startItemActionPerformed

    private void errorTableMouseClicked(MouseEvent mouseevent) {
	selectedRow = errorTable.getSelectedRow();
	if (selectedRow != -1 && mouseevent.isPopupTrigger())
	    jPopupMenu1.show(mouseevent.getComponent(), mouseevent.getX(),
			     mouseevent.getY());
  }

    private void errorTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_errorTableMouseReleased
        // Add your handling code here:
	errorTableMouseClicked(evt);
    }//GEN-LAST:event_errorTableMouseReleased

    private void errorTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_errorTableMousePressed
        // Add your handling code here:
	errorTableMouseClicked(evt);
    }//GEN-LAST:event_errorTableMousePressed

    public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
        if (stopped) return;
	errorAdapter.addError(errorEvent);
    }    
    
    public void setErrorOccured(fr.esrf.tangoatk.core.ErrorEvent errorEvent)
    {
        if (stopped) return;
	errorAdapter.addError(errorEvent);
    }

    protected boolean stopped = false;    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem showItem;
    private javax.swing.JMenuItem stopItem;
    private javax.swing.JMenuItem startItem;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private fr.esrf.tangoatk.widget.util.ErrorTree errorTree;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable errorTable;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToggleButton stopButton;
    private javax.swing.JCheckBox panicBox;
    private javax.swing.JCheckBox errorBox;
    private javax.swing.JCheckBox warningBox;
    // End of variables declaration//GEN-END:variables

}

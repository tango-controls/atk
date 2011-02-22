/** A panel for Extensions editing */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ExtensionTableModel extends AbstractTableModel {

      private String colName[] = { "Name" , "Value" };
      private JDObject[] objs;

      public ExtensionTableModel(JDObject[] model) {
        objs=model;
      }

      public void setRows(Object[][] r) {
        fireTableDataChanged();
      }

      public Class getColumnClass(int columnIndex) {
        if( columnIndex<=1 )
         return String.class;
        else
         return JButton.class;
      }

      public boolean isCellEditable(int row,int col) {
        return col==1;
      }

      public Object getValueAt(int row, int column) {
        if( column==0 )
          return objs[0].getExtendedParamName(row);
        else
          return objs[0].getExtendedParam(row);
      }

      public String getColumnName(int column) {
        return colName[column];
      }

      public int getRowCount() {
        return objs[0].getExtendedParamNumber();
      }

      public int getColumnCount() {
        return colName.length;
      }

      public void setValueAt(Object e,int row,int col) {
        for(int i=0;i<objs.length;i++)
          objs[i].setExtendedParam(row,e.toString());
        JDUtils.modified = true;
      }

}

class JDExtensionPanel extends JPanel implements ActionListener {

  JDObject[] allObjects;
  JComponent invoker;
  Rectangle oldRect;
  JTable    theTable;
  JScrollPane tableView;
  ExtensionTableModel theModel;
  JButton newExtensionBtn;

  public JDExtensionPanel(JDObject[] p, JComponent jc) {

    allObjects = p;
    invoker = jc;

    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 290));

    // ------------------------------------------------------------------------------------
    JPanel extPanel = new JPanel(null);
    extPanel.setBorder(JDUtils.createTitleBorder("Extensions"));
    extPanel.setBounds(5,5,370,280);

    theModel = new ExtensionTableModel(p);
    theTable = new JTable(theModel);
    theTable.setRowHeight(20);

    tableView = new JScrollPane(theTable);
    tableView.setBounds(10,20,350,220);
    extPanel.add(tableView);
    theTable.getColumnModel().getColumn(1).setPreferredWidth(230);

    newExtensionBtn = new JButton("New extension");
    newExtensionBtn.setMargin(new Insets(0, 0, 0, 0));
    newExtensionBtn.setFont(JDUtils.labelFont);
    newExtensionBtn.addActionListener(this);
    newExtensionBtn.setBounds(10, 245, 150, 24);
    extPanel.add(newExtensionBtn);

    add(extPanel);

  }


  // ---------------------------------------------------------
  // Action listener
  // ---------------------------------------------------------
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    int i;
    if( src==newExtensionBtn ) {
      String newExt = JOptionPane.showInputDialog("Enter extension name");
      if( newExt!=null ) {
        for(i=0;i<allObjects.length;i++)
          allObjects[i].addExtension(newExt);
        JDUtils.modified=true;
        theModel.fireTableDataChanged();
      }
    }
  }


}

package fr.esrf.tangoatk.widget.util.chart;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * A class to handle a 2 dimension big JTable with fixed row name and column name.
 */
class JLTable extends JFrame {

  private int wT; // Table width
  private int hT; // Table height

  private Font theFont;

  // Data
  private JScrollPane tableView;
  private JTable theTable = null;
  private TableModel dm;
  private Object[][] theData;
  private String[] colName = null;

  /**
   * Construction
   */
  public JLTable() {

    theData = null;

    wT = 0;
    hT = 0;

    // ------------------------------------------
    // Main table
    // ------------------------------------------

    dm = new TableModel() {

      public void addTableModelListener(TableModelListener l) {
      }

      public void removeTableModelListener(TableModelListener l) {
      }

      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      }

      public Class getColumnClass(int columnIndex) {
        return String.class;
      }

      public boolean isCellEditable(int row, int col) {
        return false;
      }

      public String getColumnName(int column) {
        if (colName != null)
          return colName[column];
        else
          return "";
      }

      public int getRowCount() {
        return hT;
      }

      public int getColumnCount() {
        return wT;
      }

      public Object getValueAt(int row, int column) {
        if (theData != null)
          return theData[row][column];
        else
          return "";
      }

    };

    theFont = new Font("Dialog", Font.PLAIN, 12);

    getContentPane().setLayout(new GridLayout(1,1));
    setTitle("Graph data");

  }

  private void createTable() {

    if (theTable != null) {
      getContentPane().remove(tableView);
    }

    theTable = new JTable(dm);
    theTable.setFont(theFont);
    theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    tableView = new JScrollPane(theTable);

    getContentPane().add(tableView);

  }

  /**
   * Sets the data.
   * @param data Handle to data array.
   * @param colNames Name of columns
   */
  public void setData(Object[][] data, String[] colNames) {

    int nhT;
    int nwT;

    if (data == null) {
      clearData();
      return;
    }

    nhT = data.length;

    if (nhT == 0) {
      clearData();
      return;
    }

    nwT = data[0].length;

    if (nwT == 0) {
      clearData();
      return;
    }


    theData = data;
    colName = colNames;

    //Recreate table if dimension of the model change
    if (theTable == null || nwT != wT || nhT != hT) {
      //System.out.println("Rebuild table");

      wT = nwT;
      hT = nhT;
      createTable();

    } else {
      //System.out.println("Update table");

      TableModelEvent e1 = new TableModelEvent(dm,TableModelEvent.HEADER_ROW);
      theTable.tableChanged(e1);

    }

  }

  /**
   * Clear the table
   */
  public void clearData() {
    // Free data
    theData = null;
    colName = null;
  }

  // Center the window
  public void centerWindow() {
    if( theTable==null )  return;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension scrsize = toolkit.getScreenSize();
    Dimension ts= theTable.getPreferredSize();
    ts.width  += 10;
    ts.height += 20;
    tableView.setPreferredSize(ts);
    pack();
    Dimension appsize = getPreferredSize();
    if( appsize.width>800 ) appsize.width=800;
    if( appsize.height>600 ) appsize.height=600;
    int x = (scrsize.width - appsize.width) / 2;
    int y = (scrsize.height - appsize.height) / 2;
    setBounds(x, y, appsize.width, appsize.height);
  }

}
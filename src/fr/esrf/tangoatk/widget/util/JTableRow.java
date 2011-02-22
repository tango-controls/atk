package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import javax.swing.border.Border;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;

/**
 * A class to handle a 2 dimension big JTable with fixed row name and column name.
 */
public class JTableRow extends JPanel {

  private int         wT; // Table width
  private int         hT; // Table height

  private boolean editable=false;
  private Font theFont;

  // Data
  private JScrollPane tableView;
  private JTable      theTable=null;
  private TableModel  dm;
  private Object[][]  theData;
  private String[]    colName=null;

  //rowName
  private JPanel     rowPanel;
  private JPanel     cornerPanel;
  private JTable     rowTable=null;
  private TableModel dmr;
  private Object[][] rowData=null;

  /**
   * Construction
   */
  public JTableRow() {

    setLayout(new BorderLayout());
    setBorder(null);

    theData    = null;
    rowData    = null;

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
        return Double.class;
      }

      public boolean isCellEditable(int row,int col) {
        return editable;
      }

      public String getColumnName(int column) {
        if(colName!=null) return colName[column];
        else return "";
      }

      public int getRowCount() {
        return hT;
      }

      public int getColumnCount() {
        return wT;
      }

      public Object getValueAt(int row, int column) {
        if(theData!=null) return theData[row][column];
        else              return new Double(Double.NaN);
      }

    };


    // ------------------------------------------
    // row panel
    // ------------------------------------------

    rowPanel = new JPanel();
    rowPanel.setBorder(null);
    rowPanel.setLayout(null);
    rowPanel.setPreferredSize(new Dimension(45,0));
    add(rowPanel,BorderLayout.WEST);

    rowData = null;

    dmr = new TableModel() {

      public void addTableModelListener(TableModelListener l) {}

      public void removeTableModelListener(TableModelListener l) {}

      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      }

      public Class getColumnClass(int columnIndex) {
        return String.class;
      }

      public boolean isCellEditable(int row,int col) {
        return false;
      }

      public String getColumnName(int column) {
        return "";
      }

      public int getRowCount() {
        return hT;
      }

      public int getColumnCount() {
        return 1;
      }

      public Object getValueAt(int row, int column) {
        if(rowData!=null) return rowData[row][column];
        else              return new Double(Double.NaN);
      }


    };

    cornerPanel = new JPanel();
    cornerPanel.setBorder(BorderFactory.createEtchedBorder());
    rowPanel.add(cornerPanel);

    addComponentListener(new ComponentListener() {
       public void componentHidden(ComponentEvent e) {
       }
       public void componentMoved(ComponentEvent e) {
       }
       public void componentResized(ComponentEvent e) {
         placeComponent();
       }
       public void componentShown(ComponentEvent e) {
         placeComponent();
       }
    });

    theFont = new Font("Dialog",Font.PLAIN,12);

  }

  private void createTable() {

    if( theTable!=null ) {
      remove(tableView);
      rowPanel.remove(rowTable);
    }

    theTable = new JTable(dm);
    theTable.setFont(theFont);
    theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    tableView=new JScrollPane(theTable);
    tableView.setPreferredSize(new Dimension(640,480));

    tableView.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
       public void adjustmentValueChanged(AdjustmentEvent e)  {
         placeComponent();
       }
    });

    add(tableView,BorderLayout.CENTER);

    rowTable = new JTable(dmr);
    rowTable.setFont(theFont);
    rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    rowTable.setBackground(getBackground());
    rowTable.setEnabled(false);
    rowPanel.add(rowTable);

  }

  // ----------------------------------------------------
  // Propery stuff
  // ----------------------------------------------------
  public void setEditable(boolean b) {
   editable=b;
  }

  public boolean isEditable() {
   return editable;
  }

  /**
   * Sets the data.
   * @param data Handle to data array.
   * @param startLabelX Horizontal column labeling starting index
   * @param startLabelY Vertical column labeling starting index
   */
  public void setData(Object[][] data,int startLabelX,int startLabelY) {

    int nhT;
    int nwT;
    String[] nColName;
    Object[][] nRowData;

    if( data==null ) {
      clearData();
      return;
    }

    nhT = data.length;

    if( nhT==0 ) {
      clearData();
      return;
    }

    nwT = data[0].length;

    if( nwT==0 ) {
      clearData();
      return;
    }

    // Build col name
    nColName = new String[nwT];
    for(int i=0;i<nwT;i++)
      nColName[i] = Integer.toString(startLabelX+i);

    // Build row name
    nRowData = new Object[nhT][1];
    for(int i=0;i<nhT;i++)
      nRowData[i][0] = Integer.toString(startLabelY+i);

    theData = data;
    rowData = nRowData;
    colName = nColName;

    //Recreate table if dimension of the model change
    if( theTable==null || nwT!=wT || nhT!=hT ) {
      //System.out.println("Rebuild table");

      wT = nwT;
      hT = nhT;
      createTable();
      placeComponent();
      
    } else {
      //System.out.println("Update table");

      TableModelEvent e1 = new TableModelEvent(dm,0,hT-1);
      theTable.tableChanged(e1);
      TableModelEvent e2 = new TableModelEvent(dmr,0,hT-1);
      rowTable.tableChanged(e2);
    }

  }

  /**
   * Clear the table
   */
  public void clearData() {
    // Free data
    theData    = null;
    rowData    = null;
  }

  public void setFont(Font f) {
    super.setFont(f);

    if( theTable!=null ) {
      theFont = f;
      rowTable.setFont(f);
      theTable.setFont(f);
      tableView.revalidate();
      placeComponent();
    }

  }

  public Font getFont() {
    return theFont;
  }

  // ----------------------------------------------------
  // Private stuff
  // ----------------------------------------------------

  private void placeComponent() {
    Dimension d = getSize();
    int hFont = 17;
    cornerPanel.setBounds(-5,-5 ,54,hFont+5);
    Rectangle r = tableView.getViewport().getViewRect();
    rowTable.setBounds(0,-r.y+hFont,45,r.y+(int)d.getHeight());
    rowTable.revalidate();
  }

}
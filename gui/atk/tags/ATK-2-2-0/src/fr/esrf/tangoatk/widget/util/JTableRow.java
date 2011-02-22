package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class to handle a 2 dimension Table with fixed row name and column name, also
 * supports multipage printing.
 */
public class JTableRow extends JPanel implements ActionListener,MouseListener {

  public final static int PRINT_BIG    = 0;
  public final static int PRINT_MEDIUM = 1;
  public final static int PRINT_SMALL  = 2;

  private int         wT; // Table width
  private int         hT; // Table height

  private boolean editable=false;
  private Font    theFont;
  private File    currentFile = null;

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

  //Contextual menu
  private Point      menuLocation;
  private JPopupMenu tableMenu;
  private JMenuItem  selectAllMenuItem;
  private JMenuItem  selectNoneMenuItem;
  private JMenuItem  selectColumnMenuItem;
  private JMenuItem  selectRowMenuItem;
  private JMenuItem  copyMenuItem;
  private JMenuItem  saveMenuItem;
  private JMenuItem  print1MenuItem;
  private JMenuItem  print2MenuItem;
  private JMenuItem  print3MenuItem;

  /**
   * Construction
   */
  public JTableRow() {

    // Default

    setLayout(new BorderLayout());
    setBorder(null);
    theData    = null;
    rowData    = null;
    theFont    = ATKConstant.labelFont;
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
        if(editable && theData!=null) theData[rowIndex][columnIndex] = aValue;
      }

      public Class getColumnClass(int columnIndex) {
        return String.class;
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
        if(theData!=null) {
          return theData[row][column];
        } else {
          return "";
        }
      }

    };

    tableView=new JScrollPane();
    tableView.setPreferredSize(new Dimension(640,480));
    tableView.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
       public void adjustmentValueChanged(AdjustmentEvent e)  {
         placeComponent();
       }
    });
    add(tableView,BorderLayout.CENTER);

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
        else              return "";
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

    // ------------------------------------------
    // Table menu
    // ------------------------------------------
    tableMenu = new JPopupMenu();
    menuLocation = new Point(0,0);

    selectNoneMenuItem = new JMenuItem("Clear selection");
    selectNoneMenuItem.addActionListener(this);
    selectAllMenuItem = new JMenuItem("Select All");
    selectAllMenuItem.addActionListener(this);
    selectRowMenuItem = new JMenuItem("Select current row");
    selectRowMenuItem.addActionListener(this);
    selectColumnMenuItem = new JMenuItem("Select current column");
    selectColumnMenuItem.addActionListener(this);
    copyMenuItem = new JMenuItem("Copy selection to clipboard");
    copyMenuItem.addActionListener(this);
    saveMenuItem = new JMenuItem("Save selection");
    saveMenuItem.addActionListener(this);
    print1MenuItem = new JMenuItem("Print table (Big size)");
    print1MenuItem.addActionListener(this);
    print2MenuItem = new JMenuItem("Print table (Medium size)");
    print2MenuItem.addActionListener(this);
    print3MenuItem = new JMenuItem("Print table (Small size)");
    print3MenuItem.addActionListener(this);

    tableMenu.add(selectNoneMenuItem);
    tableMenu.add(selectAllMenuItem);
    tableMenu.add(selectRowMenuItem);
    tableMenu.add(selectColumnMenuItem);
    tableMenu.add(new JSeparator());
    tableMenu.add(copyMenuItem);
    tableMenu.add(saveMenuItem);
    tableMenu.add(new JSeparator());
    tableMenu.add(print1MenuItem);
    tableMenu.add(print2MenuItem);
    tableMenu.add(print3MenuItem);

  }

  // ----------------------------------------------------
  // Propery stuff
  // ----------------------------------------------------
  /**
   * Sets this table editable.
   * @param b Editable flag
   */
  public void setEditable(boolean b) {
   editable=b;
  }

  /**
   * Returns true if this table is editable
   */
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

    updateTables(nhT,nwT);

  }

  /**
   * Sets the data (No row label).
   * @param data Handle to data array.
   * @param columnNames Column name
   */
  public void setData(Object[][] data,String[] columnNames) {

    int nhT;
    int nwT;

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

    // Remove row panel
    remove(rowPanel);

    theData = data;
    rowData = null;
    colName = columnNames;

    updateTables(nhT,nwT);
  }

  private void updateTables(int nhT,int nwT) {

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
      if(rowTable!=null) {
        TableModelEvent e2 = new TableModelEvent(dmr,0,hT-1);
        rowTable.tableChanged(e2);
      }

    }

  }

  /**
   * Returns the cells at the specified pos or null if the table has no data.
   * @param row Row index
   * @param column Column index
   */
  public Object getObjectAt(int row,int column) {
    if(theData!=null)
      return theData[row][column];
    else
      return null;
  }

  /**
   * Clear the table
   */
  public void
          clearData() {
    // Free data
    theData    = null;
    rowData    = null;
  }

  public void setFont(Font f) {

    super.setFont(f);
    theFont = f;

    if( theTable!=null ) {

      if(rowTable!=null) rowTable.setFont(f);
      theTable.setFont(f);
      tableView.revalidate();
      placeComponent();

    }

  }

  public Font getFont() {
    return theFont;
  }

  /**
   * Adjust the ScrollPane preferredSize according to
   * the table size.
   * @see #adjustColumnSize
   */
  public void adjustSize() {

    if( theTable==null )  return;
    Dimension ts= theTable.getPreferredSize();
    // Unfortunaly the table does not include the column name height
    // We also need to add a 2 pixel margin (ScrollPane bug ???)
    ts.width  += 4;
    ts.height += theTable.getRowHeight() + 4;
    tableView.setPreferredSize(ts);

  }

  /**
   * Adjust column size according to data. Should be
   * called before adjustSize and after setData.
   * @see #adjustSize
   */
  public void adjustColumnSize() {

    if( theTable==null )  return;
    int[] widths = measureColumns(theFont);
    for(int i=0;i<widths.length;i++)
      theTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]+16);

  }

  // ----------------------------------------------------
  // Listeners
  // ----------------------------------------------------
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();

    if(src == copyMenuItem) {

      StringSelection stringSelection = new StringSelection( makeTabbedString() );
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents( stringSelection, null );

    } else if (src==selectAllMenuItem) {

      theTable.selectAll();

    } else if (src==selectNoneMenuItem) {

      theTable.clearSelection();

    } else if (src==selectRowMenuItem) {

      theTable.clearSelection();
      int r = theTable.rowAtPoint(menuLocation);
      theTable.setRowSelectionInterval(r,r);
      theTable.setColumnSelectionInterval(0,wT-1);

    } else if (src==selectColumnMenuItem) {

      theTable.clearSelection();
      int r = theTable.columnAtPoint(menuLocation);
      theTable.setColumnSelectionInterval(r,r);
      theTable.setRowSelectionInterval(0,hT-1);

    } else if ( src == print1MenuItem ) {

      printTable(PRINT_BIG);

    } else if ( src == print2MenuItem ) {

      printTable(PRINT_MEDIUM);

    } else if ( src == print3MenuItem ) {

      printTable(PRINT_SMALL);

    } else if ( src == saveMenuItem ) {

      JFileChooser fc = new JFileChooser(".");
      if(currentFile!=null) fc.setSelectedFile(currentFile);
      int status = fc.showSaveDialog(this);
      if(status==JFileChooser.APPROVE_OPTION) {
        currentFile = fc.getSelectedFile();
        try {
          FileWriter f = new FileWriter(currentFile);
          f.write( makeTabbedString() );
          f.close();
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this,ex,"Error while saving data",JOptionPane.ERROR_MESSAGE);
        }
      }

    }

  }

  public void mouseClicked(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {

    if(e.getButton() == MouseEvent.BUTTON3) {
      menuLocation.x = e.getX();
      menuLocation.y = e.getY();
      tableMenu.show(theTable, e.getX(), e.getY());
    }

  }

  public void mouseReleased(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  // ----------------------------------------------------
  // Private stuff
  // ----------------------------------------------------

  private void createTable() {

    if( theTable!=null ) {
      theTable.removeMouseListener(this);
      if(rowTable!=null)
        rowPanel.remove(rowTable);
    }

    theTable = new JTable(dm);
    theTable.setFont(theFont);
    theTable.setRowSelectionAllowed(true);
    theTable.setColumnSelectionAllowed(true);
    theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    theTable.addMouseListener(this);

    tableView.setViewportView(theTable);

    if(rowData!=null) {
      rowTable = new JTable(dmr);
      rowTable.setFont(theFont);
      rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      rowTable.setBackground(getBackground());
      rowTable.setEnabled(false);
      rowPanel.add(rowTable);
    }

  }

  private void placeComponent() {
    if(rowData!=null) {
      Dimension d = getSize();
      int hFont = 17;
      cornerPanel.setBounds(-5,-5 ,54,hFont+5);
      Rectangle r = tableView.getViewport().getViewRect();
      rowTable.setBounds(0,-r.y+hFont,45,r.y+(int)d.getHeight());
      rowTable.revalidate();
    }
  }

  private String makeTabbedString() {

    int[] cols = theTable.getSelectedColumns();
    int[] rows = theTable.getSelectedRows();

    StringBuffer str = new StringBuffer();
    for (int j = 0; j < rows.length; j++) {
      for (int i = 0; i < cols.length; i++) {
        str.append(theData[rows[j]][cols[i]]);
        if (i < cols.length - 1) str.append('\t');
      }
      str.append('\n');
    }
    return str.toString();

  }

  private int[] measureColumns(Font f) {

    if(theData==null) return new int[0];

    int[]     colWidths = new int[wT];
    int       i,j;

    for(i=0;i<wT;i++) {
      Dimension d = ATKGraphicsUtils.measureString(colName[i],f);
      colWidths[i] = d.width;
    }
    for(i=0;i<wT;i++) {
      for(j=0;j<hT;j++) {
        String value = theData[j][i].toString();
        Dimension d = ATKGraphicsUtils.measureString(value,f);
        if(d.width > colWidths[i]) colWidths[i] = d.width;
      }
    }

    return colWidths;

  }

/**
 * Display the print dialog and sends the table to the printer.
 * @param printSize Font size for printing
 * @see #PRINT_BIG
 * @see #PRINT_MEDIUM
 * @see #PRINT_SMALL
 */
  public void printTable(int printSize) {

    int printerRes = 140; // A bit smaller than on screen
    int halfRes = printerRes / 2;
    int i,j;
    Font prFont;

    if(theData==null)
      return;

    switch(printSize) {
      case PRINT_BIG:
        prFont = new Font("Dialog",Font.PLAIN,16);
        break;
      case PRINT_MEDIUM:
        prFont = new Font("Dialog",Font.PLAIN,12);
        break;
      // Small
      default:
        prFont = new Font("Dialog",Font.PLAIN,8);
        break;
    }

    // Compute column widhts (in pixel)
    int[]     colWidths = measureColumns(prFont);
    int[]     colNameWidths = new int[wT];
    int       ascent    = (int)(ATKGraphicsUtils.getLineMetrics("Page 000",prFont).getAscent()+0.5f);
    Dimension pDim      = ATKGraphicsUtils.measureString("Page 000",prFont);
    int       hFont     = pDim.height;

    for(i=0;i<wT;i++) {
      Dimension d = ATKGraphicsUtils.measureString(colName[i],prFont);
      colNameWidths[i] = d.width;
    }

    // Extimate number of page for A4 format (PORTRAIT)
    double A4Height = (29.7/2.54) * (double)(printerRes - 1) - (3.0*hFont); // margin + col name + page number
    int nbPage = (int)( (double)((hFont+4) * hT) / A4Height ) + 1;

    // Default print settings
    PageAttributes pa = new PageAttributes();
    JobAttributes ja = new JobAttributes();
    pa.setPrintQuality(PageAttributes.PrintQualityType.HIGH);
    pa.setColor(PageAttributes.ColorType.COLOR);
    pa.setMedia(PageAttributes.MediaType.A4);
    pa.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
    pa.setPrinterResolution(printerRes);
    ja.setMaxPage(nbPage);
    ja.setMinPage(1);
    ja.setPageRanges(new int[][] {{1,nbPage}});
    ja.setDialog(JobAttributes.DialogType.NATIVE);

    // Displays print window
    Window parent = ATKGraphicsUtils.getWindowForComponent(this);
    PrintJob printJob;
    if(parent instanceof Frame) {
      printJob = java.awt.Toolkit.getDefaultToolkit().getPrintJob((Frame)parent, "Print table", ja, pa);
    } else {
      Frame dummy = new Frame();
      printJob = java.awt.Toolkit.getDefaultToolkit().getPrintJob(dummy, "Print table", ja, pa);
    }

    if (printJob != null) {

      try {

        // Get page dimension (should be given for printerRes resolution)
        int wp = printJob.getPageDimension().width - printerRes; // 0.5inch margin
        int hp = printJob.getPageDimension().height - printerRes; // 0.5inch margin
        nbPage = 1;
        j=0;

        // Compute the real number of page
        int nbI = ( hp - (hFont+4)*3 ) / (hFont+4);
        int nbP = ( hT / nbI ) + 1;

        while (j < hT) {

          int x  = 0;
          int y  = 0;

          // Print new page
          Graphics g = printJob.getGraphics();
          g.translate(halfRes,halfRes);
          g.setFont(prFont);

          // Print column name
          for (i = 0; i < wT; i++) {
            int xpos = (colWidths[i] - colNameWidths[i]) / 2;
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, colWidths[i] + 6, hFont + 2);
            x += 3;
            g.setColor(Color.BLACK);
            g.drawString(colName[i], x + xpos, y + ascent);
            x += colWidths[i] + 3;
            if (i < wT - 1)
              g.drawLine(x, y, x, y + hFont);
          }
          y += hFont + 2;
          g.drawLine(0, y, x, y);
          g.drawLine(0, y + 1, x, y + 1);
          y += 2;

          // Print rows
          g.setColor(Color.BLACK);
          int j2 = 0;
          while (j < hT && j2 < nbI) {
            x = 0;
            for (i = 0; i < wT; i++) {
              for (i = 0; i < wT; i++) {
                x += 3;
                g.drawString(theData[j][i].toString(), x, y + ascent);
                x += colWidths[i] + 3;
                if (i < wT - 1)
                  g.drawLine(x, y, x, y + hFont);
              }
            }
            y += hFont + 2;
            g.drawLine(0, y, x, y);
            y += 2;
            j++;
            j2++;
          }

          // Draw border
          y -= 2;
          g.drawLine(0, 0, x, 0);
          g.drawLine(x, 0, x, y);
          g.drawLine(0, y, 0, 0);

          // Print page number
          g.drawString("Page " + nbPage + "/" + nbP, wp - pDim.width, hp - hFont + ascent);

          // Go to next page
          g.dispose();
          nbPage++;

        }

        printJob.finalize();
        printJob = null;

      } catch (Exception e) {

        e.printStackTrace();
        JOptionPane.showMessageDialog(parent, "Exception occured while printing\n" + e.getMessage(),
                                      "Print table", JOptionPane.ERROR_MESSAGE);

      }


    }

  }

  /**
   * Test function.
   */
  static public void main(String[] args) {

    JFrame f = new JFrame();

    String[]   cols = new String[6];
    Object[][] data = new Object[200][6];
    for(int i=0;i<cols.length;i++) {

      if(i==0) {
        cols[i] = "Index";
        for(int j=0;j<data.length;j++) {
          data[j][i] = new Double( (double)j );
        }
      } else {
        cols[i] = "Column " + i;
        for(int j=0;j<data.length;j++) {
          data[j][i] = new Double( Math.random() );
        }
      }

    }

    JTableRow t = new JTableRow();
    t.setData(data,cols);
    t.adjustColumnSize();
    f.setContentPane(t);
    //t.adjustSize();
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }

}

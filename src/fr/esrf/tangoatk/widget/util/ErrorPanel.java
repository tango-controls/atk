/*
 * ErrorPanel.java
 */

package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.ISetErrorListener;
import fr.esrf.tangoatk.core.IErrorListener;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

class ErrorPanel extends JPanel implements IErrorListener,ISetErrorListener {

  private ErrorAdapter errorAdapter = new ErrorAdapter();

  private JPopupMenu popupMenu;
  private JMenuItem showItem;
  private JMenuItem stackItem;
  private JMenuItem stopItem;
  private JMenuItem startItem;

  private JPanel filterPanel;
  private JToggleButton stopButton;
  private JButton clearButton;
  private JCheckBox panicBox;
  private JCheckBox errorBox;
  private JCheckBox warningBox;
  private JComboBox sourceCombo;
  private JComboBox sortCombo;

  private JSplitPane errorSplitPane;
  private JScrollPane tableView;
  private JTable errorTable;
  private JScrollPane errorView;
  private ErrorTree errorTree;

  private JFrame stackFrame;
  private JTextArea stackText;

  private int selectedRow;
  private String selectedSource=null;
  private boolean sourceComboUpdate=false;
  private boolean stopped;

  public ErrorPanel() {

    selectedRow = -1;
    stopped = false;
    initComponents();
    errorAdapter.setTimeFormat(new java.text.SimpleDateFormat("dd/MMM HH:mm:ss"));
    errorAdapter.setErrorPanel(this);

  }

  private void initComponents() {

    setLayout(new BorderLayout());

    // ------------ Contextual menu -----------------------------

    popupMenu = new JPopupMenu();
    showItem = new JMenuItem();
    showItem.setText("Show Error");
    showItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showItemActionPerformed();
      }
    });
    popupMenu.add(showItem);

    stackItem = new JMenuItem();
    stackItem.setText("Show Java stack");
    stackItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showJavaStackActionPerformed();
      }
    });
    popupMenu.add(stackItem);

    stopItem = new JMenuItem();
    stopItem.setText("Stop");
    stopItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        stopItemActionPerformed();
      }
    });
    popupMenu.add(stopItem);

    startItem = new JMenuItem();
    startItem.setText("Start");
    startItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        startItemActionPerformed();
      }
    });
    popupMenu.add(startItem);

    // ----------------- Filter panel -----------------------------

    filterPanel = new JPanel();
    FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
    filterPanel.setLayout(fl);
    filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filters",
                                                    TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION,
                                                    ATKConstant.labelFont, Color.BLACK));

    stopButton = new JToggleButton();
    stopButton.setText("Stop");
    stopButton.setToolTipText("Stops updating");
    stopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        stopButtonActionPerformed();
      }
    });
    filterPanel.add(stopButton);

    clearButton = new JButton();
    clearButton.setText("Clear");
    clearButton.setToolTipText("Clear errors");
    clearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearButtonActionPerformed();
      }
    });
    filterPanel.add(clearButton);

    panicBox = new JCheckBox();
    panicBox.setFont(ATKConstant.labelFont);
    panicBox.setSelected(true);
    panicBox.setText("View panic");
    panicBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        panicBoxActionPerformed();
      }
    });
    filterPanel.add(panicBox);

    errorBox = new JCheckBox();
    errorBox.setFont(ATKConstant.labelFont);
    errorBox.setSelected(true);
    errorBox.setText("View error");
    errorBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        errorBoxActionPerformed();
      }
    });
    filterPanel.add(errorBox);

    warningBox = new JCheckBox();
    warningBox.setFont(ATKConstant.labelFont);
    warningBox.setSelected(true);
    warningBox.setText("View warning");
    warningBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        warningBoxActionPerformed();
      }
    });
    filterPanel.add(warningBox);

    JLabel l = new JLabel("  View source");
    l.setFont(ATKConstant.labelFont);
    filterPanel.add(l);

    sourceCombo = new JComboBox();
    sourceCombo.setFont(ATKConstant.labelFont);
    sourceCombo.addItem("All");
    filterPanel.add(sourceCombo);
    sourceCombo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sourceComboActionPerformed();
      }
    });

    l = new JLabel("  Sort by");
    l.setFont(ATKConstant.labelFont);
    filterPanel.add(l);

    sortCombo = new JComboBox();
    sortCombo.setFont(ATKConstant.labelFont);
    sortCombo.addItem("No sort");
    sortCombo.addItem("Time");
    sortCombo.addItem("Severity");
    sortCombo.addItem("Source");
    sortCombo.addItem("Description");
    filterPanel.add(sortCombo);
    sortCombo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sortComboActionPerformed();
      }
    });

    add(filterPanel,BorderLayout.SOUTH);

    // ----------------- Error panel -----------------------------

    errorTable = new JTable(errorAdapter);
    errorTable.setPreferredScrollableViewportSize(new java.awt.Dimension(700, 300));
    errorTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        errorTableMousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        errorTableMouseReleased(evt);
      }
    });
    errorTable.getColumnModel().addColumnModelListener(
        new TableColumnModelListener() {
          public void columnAdded(TableColumnModelEvent e) {}
          public void columnRemoved(TableColumnModelEvent e) {}
          public void columnMoved(TableColumnModelEvent e) {
            // Hack to get the mouse click on column label
            // TODO: Do it properly...
            errorTableColumnClicked(e.getFromIndex());
          }
          public void columnMarginChanged(ChangeEvent e) {}
          public void columnSelectionChanged(ListSelectionEvent e) {}
        }
    );
    tableView = new JScrollPane(errorTable);
    tableView.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    errorTree = new ErrorTree();
    errorView = new JScrollPane(errorTree);
    errorView.setPreferredSize(new java.awt.Dimension(230, 300));

    errorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    errorSplitPane.add(tableView);
    errorSplitPane.add(errorView);
    errorSplitPane.setDividerSize(4);

    add(errorSplitPane,BorderLayout.CENTER);

    // ---------------------------- Stack frame --------------------------------

    stackText = new JTextArea();
    JScrollPane jp = new JScrollPane(stackText);
    stackFrame = new JFrame("Java Stack View");
    stackFrame.setContentPane(jp);

    sizeColumns();
  }

  private void sortComboActionPerformed() {

    int s = sortCombo.getSelectedIndex();
    errorAdapter.setSortedColumn(s-1);

  }

  private void sourceComboActionPerformed() {

    if (!sourceComboUpdate) {

      int s = sourceCombo.getSelectedIndex();
      if (s >= 1) {
        selectedSource = sourceCombo.getSelectedItem().toString();
      } else {
        selectedSource = null;
      }

      errorAdapter.setSourceFilter(selectedSource);

    }

  }

  private void panicBoxActionPerformed() {
    errorAdapter.showPanic(panicBox.isSelected());
  }

  private void errorBoxActionPerformed() {
    errorAdapter.showError(errorBox.isSelected());
  }

  private void warningBoxActionPerformed() {
    errorAdapter.showWarning(warningBox.isSelected());
  }

  private void clearButtonActionPerformed() {
     errorAdapter.clearError();
  }

  private void stopButtonActionPerformed() {

    stopped = stopButton.getText().equals("Stop");
    if (stopped) {
      stopButton.setText("Resume");
      stopButton.setToolTipText("Restarts updating");
    } else {
      stopButton.setText("Stop");
      stopButton.setToolTipText("Stops updating");
    }

  }

  private void showItemActionPerformed() {
    errorTree.addErrors(errorAdapter.getErrorNumber(selectedRow));
    selectedRow = -1;
  }

  private void showJavaStackActionPerformed() {

    Throwable theError = errorAdapter.getErrorAt(selectedRow);
    StackTraceElement[] st = theError.getStackTrace();
    StringBuffer str = new StringBuffer();
    str.append(theError.getClass()+"\n at\n");
    for(int i=0;i<st.length;i++) {
      str.append(st[i]);
      str.append('\n');
    }
    stackText.setText(str.toString());
    ATKGraphicsUtils.centerFrameOnScreen(stackFrame);
    stackFrame.setVisible(true);

  }

  private void stopItemActionPerformed() {

    stopped = true;
    stopButton.setText("Resume");
    stopButton.setToolTipText("Restarts updating");

  }

  private void startItemActionPerformed() {

    stopped = false;
    stopButton.setText("Stop");
    stopButton.setToolTipText("Stops updating");

  }

  private void errorTableColumnClicked(int column) {
    sortCombo.setSelectedIndex(column+1);
  }

  private void errorTableMouseClicked(MouseEvent mouseevent) {
    selectedRow = errorTable.getSelectedRow();
    if (selectedRow != -1 && mouseevent.isPopupTrigger())
      popupMenu.show(mouseevent.getComponent(), mouseevent.getX(),
                       mouseevent.getY());
  }

  private void errorTableMouseReleased(java.awt.event.MouseEvent evt) {
    errorTableMouseClicked(evt);
  }

  private void errorTableMousePressed(java.awt.event.MouseEvent evt) {
    errorTableMouseClicked(evt);
  }

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
    if (stopped) return;
    errorAdapter.addError(errorEvent);
  }

  public void setErrorOccured(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
    if (stopped) return;
    errorAdapter.addError(errorEvent);
  }
  
  public void setErrorBufferSize (int nbErrors)
  {
      errorAdapter.setErrorBufferSize(nbErrors);
  }

  // Tiggered by the ErrorAdapter when the source list change.
  public void sourceChange() {

    int idx=-1;

    sourceComboUpdate = true;

    Vector src = errorAdapter.getAllSource();
    sourceCombo.removeAllItems();
    sourceCombo.addItem("All");
    for(int i=0;i<src.size();i++) {
      if(selectedSource!=null) {
        if(selectedSource.equalsIgnoreCase((String)src.get(i)))
          idx=i;
      }
      sourceCombo.addItem(src.get(i));
    }

    // Source not found anymore
    if(idx==-1) {
      selectedSource=null;
      errorAdapter.setSourceFilter(selectedSource);
    }

    // Reselect source
    sourceCombo.setSelectedIndex(idx+1);

    sourceComboUpdate = false;

  }

  private void sizeColumns() {
    errorTable.getColumnModel().getColumn(ErrorAdapter.TIME).setMaxWidth(140);
    errorTable.getColumnModel().getColumn(ErrorAdapter.TIME).setMinWidth(110);
    errorTable.getColumnModel().getColumn(ErrorAdapter.SEVERITY).setMaxWidth(60);
    errorTable.getColumnModel().getColumn(ErrorAdapter.SOURCE).setMinWidth(150);
    errorTable.getColumnModel().getColumn(ErrorAdapter.DESCRIPTION).setMinWidth(200);
  }

}

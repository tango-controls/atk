// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   ErrorHistory2.java

package fr.esrf.tangoatk.widget.util;

import com.klg.jclass.util.swing.JCSortableTable;
import fr.esrf.Tango.DevError;
import fr.esrf.tangoatk.core.AEntityList;
import fr.esrf.tangoatk.core.ATKEvent;
import fr.esrf.tangoatk.core.ATKException;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IErrorListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;

// Referenced classes of package fr.esrf.tangoatk.widget.util:
//      ErrorTree

public class ErrorHistory2 extends JPanel
  implements IErrorListener
{
  class ErrorAdapter extends AbstractTableModel
  {

    public void addError(ErrorEvent errorevent)
    {
      ErrorEvent errorevent1 = null;
      if (errors.size() > 0)
        errorevent1 = (ErrorEvent)errors.get(errors.size() - 1);
      if (errorevent1 != null && errorevent.getError().toString().equals(errorevent1.getError().toString()))
      {
        errorevent1.setTimeStamp(errorevent.getTimeStamp());
        fireTableRowsUpdated(errors.size() - 1, errors.size() - 1);
        return;
      } else
      {
        errors.add(errorevent);
        fireTableRowsInserted(errors.size() - 2, errors.size() - 1);
        return;
      }
    }

    public DevError[] getErrorNumber(int i)
    {
      Throwable throwable = ((ErrorEvent)errors.get(i)).getError();
      if (!(throwable instanceof ATKException))
        return null;
      else
        return ((ATKException)throwable).getErrors();
    }

    public void setTable(JTable jtable)
    {
      table = jtable;
    }

    public Object getValueAt(int i, int j)
    {
      ErrorEvent errorevent = (ErrorEvent)errors.get(i);
      Throwable throwable = errorevent.getError();
      Object obj = errorevent.getSource();
      switch (j)
      {
      case 0: // '\0'
        return getTime(errorevent);

      case 1: // '\001'
        return getSeverity(errorevent);

      case 2: // '\002'
        return getMessage(errorevent);

      case 3: // '\003'
        return getSource(errorevent);

      case 4: // '\004'
        return getOrigin(errorevent);

      case 5: // '\005'
        return getReason(errorevent);
      }
      return "";
    }

    public String getTime(ErrorEvent errorevent)
    {
      Date date = new Date(errorevent.getTimeStamp());
      return format.format(date);
    }

    public String getSeverity(ErrorEvent errorevent)
    {
      Throwable throwable = errorevent.getError();
      if (!(throwable instanceof ATKException))
        return "";
      else
        return ATKException.severity[((ATKException)throwable).getSeverity()];
    }

    public String getMessage(ErrorEvent errorevent)
    {
      Throwable throwable = errorevent.getError();
      if (!(throwable instanceof ATKException))
        return throwable.toString();
      else
        return ((ATKException)throwable).getDescription();
    }

    public Object getSource(ErrorEvent errorevent)
    {
      return errorevent.getSource();
    }

    public String getOrigin(ErrorEvent errorevent)
    {
      Throwable throwable = errorevent.getError();
      if (!(throwable instanceof ATKException))
        return "";
      else
        return ((ATKException)throwable).getOrigin();
    }

    public String getReason(ErrorEvent errorevent)
    {
      Throwable throwable = errorevent.getError();
      if (!(throwable instanceof ATKException))
        return "";
      else
        return ((ATKException)throwable).getReason();
    }

    public int getRowCount()
    {
      return errors.size();
    }

    public int getColumnCount()
    {
      return columnNames.length;
    }

    public String getColumnName(int i)
    {
      return columnNames[i];
    }

    IErrorListener listener;
    JTable table;
    final int TIME = 0;
    final int SEVERITY = 1;
    final int MESSAGE = 2;
    final int SOURCE = 3;
    final int ORIGIN = 4;
    final int REASON = 5;
    String columnNames[] = {
      "Time", "Severity", "Message", "Source", "Origin", "Reason"
    };
    java.util.List errors;

    ErrorAdapter()
    {
      errors = new Vector();
    }
  }


  public void setTimeFormat(SimpleDateFormat simpledateformat)
  {
    format = simpledateformat;
  }

  public SimpleDateFormat getTimeFormat()
  {
    return format;
  }

  private void initComponents()
  {
    jPopupMenu1 = new JPopupMenu();
    fullItem = new JMenuItem();
    splitPane = new JSplitPane();
    jScrollPane1 = new JScrollPane();
    errorTable = new JCSortableTable();
    jScrollPane2 = new JScrollPane();
    errorTree = new ErrorTree();
    fullItem.setText("View Error");
    fullItem.addActionListener(new _cls1());
    jPopupMenu1.add(fullItem);
    setLayout(new BorderLayout());
    splitPane.setDividerLocation(200);
    splitPane.setDividerSize(1);
    errorTable.setModel(errorAdapter);
    errorTable.addMouseListener(new _cls2());
    jScrollPane1.setViewportView(errorTable);
    splitPane.setLeftComponent(jScrollPane1);
    jScrollPane2.setViewportView(errorTree);
    splitPane.setRightComponent(jScrollPane2);
    add(splitPane, "Center");
  }

  private void fullItemActionPerformed(ActionEvent actionevent)
  {
    errorTree.addErrors(errorAdapter.getErrorNumber(selectedRow));
    selectedRow = -1;
  }

  private void errorTableMouseReleased(MouseEvent mouseevent)
  {
    errorTableMouseClicked(mouseevent);
  }

  private void errorTableMousePressed(MouseEvent mouseevent)
  {
    errorTableMouseClicked(mouseevent);
  }

  private void errorTableMouseClicked(MouseEvent mouseevent)
  {
    selectedRow = errorTable.getSelectedRow();
    if (selectedRow != -1 && mouseevent.isPopupTrigger())
      jPopupMenu1.show(mouseevent.getComponent(), mouseevent.getX(), mouseevent.getY());
  }

  public void errorChange(ErrorEvent errorevent)
  {
    errorAdapter.addError(errorevent);
  }

  public static void main(String args[])
    throws Exception
  {
    AttributeList attributelist = new AttributeList();
    ErrorHistory2 errorhistory2 = new ErrorHistory2();
    JFrame jframe = new JFrame();
    attributelist.setErrorListener(errorhistory2);
    attributelist.add("eas/test-api/1/*");
    attributelist.setRefreshInterval(2000);
    attributelist.startRefresher();
    jframe.setContentPane(errorhistory2);
    jframe.pack();
    jframe.show();
  }

  public ErrorHistory2()
  {
    selectedRow = -1;
    format = new SimpleDateFormat();
    errorAdapter = new ErrorAdapter();
    initComponents();
  }

  int selectedRow;
  SimpleDateFormat format;
  private ErrorAdapter errorAdapter;
  private JPopupMenu jPopupMenu1;
  private JMenuItem fullItem;
  private JSplitPane splitPane;
  private JScrollPane jScrollPane1;
  private JTable errorTable;
  private JScrollPane jScrollPane2;
  private ErrorTree errorTree;




  private class _cls1
    implements ActionListener
  {

    public void actionPerformed(ActionEvent actionevent)
    {
      fullItemActionPerformed(actionevent);
    }

    private final void constructor$0(ErrorHistory2 errorhistory2)
    {
    }

    _cls1()
    {
      constructor$0(ErrorHistory2.this);
    }
  }


  private class _cls2 extends MouseAdapter
  {

    public void mousePressed(MouseEvent mouseevent)
    {
      errorTableMousePressed(mouseevent);
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
      errorTableMouseReleased(mouseevent);
    }

    private final void constructor$0(ErrorHistory2 errorhistory2)
    {
    }

    _cls2()
    {
      constructor$0(ErrorHistory2.this);
    }
  }

}

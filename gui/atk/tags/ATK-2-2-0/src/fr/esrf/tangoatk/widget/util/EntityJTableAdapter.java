// File:          EntityJTableAdapter.java<2>
// Created:       2002-04-25 13:48:15, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-21 14:8:56, assum>
// 
// $Id$
// 
// Description:       
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   EntityJTableAdapter.java

package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.*;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

public class EntityJTableAdapter extends AbstractTableModel
{

  public void addTableModelListener(JTable jtable)
  {
    super.addTableModelListener(jtable);
    System.out.println("Added listener " + jtable);
  }

  public void addEntity(IEntity ientity)
  {
    entities.add(ientity);
    Map map = ientity.getPropertyMap();
    if (!initialized)
    {
      columnNames.addAll(map.values());
      columns = columnNames.size();
    } else
    {
      normalizeProperties(ientity);
    }
    fireTableChanged(new TableModelEvent(this));
    fireTableRowsInserted(0, 1);
  }

  protected void normalizeProperties(IEntity ientity)
  {
    Map map = ientity.getPropertyMap();
    try
    {
      if (map.size() < columns)
      {
        for (int i = 0; i < columns; i++)
        {
          String s = ((Property)columnNames.get(i)).toString();
          if (!map.containsKey(s))
            map.put(s, null);
        }

      }
    }
    catch (NullPointerException nullpointerexception) { }
  }

  public void addEntities(AttributeList attributelist)
  {
    int i = entities.size();
    for (int j = 0; j < attributelist.size(); j++)
    {
      IEntity ientity = (IEntity)attributelist.get(j);
      normalizeProperties(ientity);
      entities.add(attributelist.get(j));
    }

    fireTableRowsInserted(i, entities.size() - 1);
  }

  public void setTable(JTable jtable)
  {
    table = jtable;
  }

  public String getColumnName(int i)
  {
    return columnNames.get(i).toString();
  }

  public int getRowCount()
  {
    return entities.size();
  }

  public int getColumnCount()
  {
    return columns;
  }

  public Property internalGetValueAt(int i, int j)
  {
    Vector vector = new Vector();
    IEntity ientity = (IEntity)entities.get(i);
    if (ientity.getPropertyMap() == null)
    {
      return null;
    } else
    {
      vector.addAll(ientity.getPropertyMap().values());
      return (Property)vector.get(j);
    }
  }

  public Object getValueAt(int i, int j)
  {
    Property aentityproperty = internalGetValueAt(i, j);
    if (aentityproperty == null)
    {
      aentityproperty = (Property)columnNames.get(j);
      if ((aentityproperty instanceof NumberProperty))
        return new Double("NaN");
      else
        return "NULL";
    }
    if ((aentityproperty instanceof StringProperty)  ||
	(aentityproperty instanceof NumberProperty))
    {
      System.out.println("Returning " + aentityproperty.getValue());
      return aentityproperty.getValue();
    } else
    {
      return aentityproperty.getPresentation();
    }
  }

  public void setValueAt(Object obj, int i, int j)
  {
    Property aentityproperty = internalGetValueAt(i, j);
    aentityproperty.setValue(obj);
    aentityproperty.store();
    fireTableCellUpdated(i, j);
  }

  public boolean isCellEditable(int i, int j)
  {
    Property aentityproperty = internalGetValueAt(i, j);
    if (aentityproperty == null)
      return false;
    else
      return aentityproperty.isEditable();
  }

  public Class getColumnClass(int i)
  {
    Property aentityproperty = (Property)columnNames.get(i);
    if ((aentityproperty instanceof StringProperty))
      return java.lang.String.class;
    if ((aentityproperty instanceof NumberProperty))
      return java.lang.Double.class;
    else
      return java.lang.String.class;
  }

  public static void main(String args[])
    throws Exception
  {
    JFrame jframe = new JFrame();
    AttributeList attributelist = new AttributeList();
    EntityJTableAdapter entityjtableadapter = new EntityJTableAdapter();
    entityjtableadapter.addEntity(attributelist.add("eas/test-api/1/Att_sinus"));
    JTable jtable = new JTable(entityjtableadapter);
    jtable.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 70));
    attributelist.add("eas/test-api/1/*");
    JScrollPane jscrollpane = new JScrollPane(jtable);
    jframe.getContentPane().add(jscrollpane);
    jframe.pack();
    jframe.show();
  }

  public EntityJTableAdapter()
  {
    initialized = false;
    entities = new Vector();
    columns = 0;
    columnNames = new Vector();
  }

  JTable table;
  boolean initialized;
  List entities;
  int columns;
  List columnNames;
}

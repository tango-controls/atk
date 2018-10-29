/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.CommandList;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.IEntity;
import fr.esrf.tangoatk.core.command.VoidVoidCommand;
import fr.esrf.tangoatk.widget.command.ConfirmCommandViewer;
import fr.esrf.tangoatk.widget.command.VoidVoidCommandViewer;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author poncet
 */
public class MultiAttAndCmdTableViewer extends MultiScalarTableViewer
{
    
//   protected IEntity[][]        entityModels=null;
//   MultiScalarViewerCellRenderer  scalarViewerRenderer=null;
   
   
   // ---------------------------------------------------
   // Contruction
   // ---------------------------------------------------
   public MultiAttAndCmdTableViewer()
   {
       super();
       tabModel = new MultiAttAndCmdViewerTableModel();
       super.setModel(tabModel);
       scalarViewerRenderer = new MultiAttAndCmdViewerCellRenderer();
       setRowMargin(0);
       //getColumnModel().setColumnMargin(getColumnModel().getColumnMargin()+2);
   }
   
   
   //override the getCellRenderer method of JTable
   public TableCellRenderer getCellRenderer(int row, int column)
   {
       Object obj=tabModel.getValueAt(row, column);
       if (obj != null)
          if (obj instanceof VoidVoidCommandViewer)
              return scalarViewerRenderer;
       
       return super.getCellRenderer(row, column);
   }    



   public void setModelAt( IEntity ient, int r, int c )
   {
       if (ient instanceof IAttribute)
       {
           IAttribute iatt = (IAttribute) ient;
           super.setModelAt(iatt, r, c);
           return;
       }
       
       if ( !(ient instanceof VoidVoidCommand) )
       {
          System.out.println("Unsupported type of model; Only voidVoidCommands and scalar attributes are accepted; setModelAt failed.");
	  return;
       }
       
       if ((r < 0) || (c < 0)) return;
       if ((r >= nbRows) || (c >= nbColumns)) return;
      
       // Here we know that the ient parameter is instanceof VoidVoidCommand
       if (entityModels == null)
       {
          initModels();
       }
       
       clearModelAt(r, c);
       
       if (ient == null)
          return;
	  
       entityModels[r][c] = ient;       
       VoidVoidCommand cmd = (VoidVoidCommand) ient;
       addCmdAt(cmd, r, c);
   }
   
   public void setConfirmCommandAt(ICommand ic, int r, int c, String confirmMsg)
   {
       if (confirmMsg == null)
       {
           setModelAt(ic, r, c);
           return;
       }
       
       if (confirmMsg.isEmpty())
       {
           setModelAt(ic, r, c);
           return;
       }
       
       if ( !(ic instanceof VoidVoidCommand) )
       {
          System.out.println("Unsupported type of model; Only voidVoidCommands and scalar attributes are accepted; setModelAt failed.");
	  return;
       }
       
       if ((r < 0) || (c < 0)) return;
       if ((r >= nbRows) || (c >= nbColumns)) return;
       
       // Here we know that the ient parameter is instanceof VoidVoidCommand
       if (entityModels == null)
       {
          initModels();
       }
       
       clearModelAt(r, c);
       
       entityModels[r][c] = ic;       
       VoidVoidCommand cmd = (VoidVoidCommand) ic;
       addConfirmCmdAt(cmd, r, c, confirmMsg);
       
   }


   private void addCmdAt( VoidVoidCommand cmd, int r, int c )
   { 
       VoidVoidCommandViewer  cmdViewer = new VoidVoidCommandViewer();
       cmdViewer.setFont(getFont());
       cmdViewer.setModel(cmd);
       cmdViewer.setMargin(new Insets(1,1,1,1));
       
//       if ( ((double) rowHeight) < cmdViewer.getPreferredSize().getHeight() )
//          rowHeight = (int) cmdViewer.getPreferredSize().getHeight();
       MultiAttAndCmdViewerTableModel  tm = (MultiAttAndCmdViewerTableModel) tabModel;
       tm.addCommandAt(r,c,cmd,cmdViewer);
   }
   
   private void addConfirmCmdAt( VoidVoidCommand cmd, int r, int c, String msg )
   { 
       ConfirmCommandViewer  cmdViewer = new ConfirmCommandViewer();
       cmdViewer.setFont(getFont());
       cmdViewer.setConfirmDialParent(this);
       cmdViewer.setConfirmTitle("Confirm Command");
       cmdViewer.setConfirmMessage(msg);
       cmdViewer.setModel(cmd);
       cmdViewer.setMargin(new Insets(1,1,1,1));
       
//       if ( ((double) rowHeight) < cmdViewer.getPreferredSize().getHeight() )
//          rowHeight = (int) cmdViewer.getPreferredSize().getHeight();
       MultiAttAndCmdViewerTableModel  tm = (MultiAttAndCmdViewerTableModel) tabModel;
       tm.addCommandAt(r,c,cmd,cmdViewer);
   }
   
   private void initModels()
   {
      if ((nbRows <= 0) || (nbColumns <= 0))
      {
         System.out.println("Please set the number of columns and rows before calling initAttModels.");
	 return;
      }
      
      // The following block of code has been added because NetBeans sets the JTable model
      // just after instantiation of MultiScalarTableViewer. So we should take the occasion
      // of initAttModels to restore the normal MultiScalarViewerTableModel. The best solution would
      // be to override the inherited method setModel but this will lead to a bug in NetBeans IDE
      // The following 6 lines are a workaround to this problem.
      TableModel   tm = super.getModel();
      if (! (tm instanceof MultiAttAndCmdViewerTableModel) )
      {
	  tabModel = new MultiAttAndCmdViewerTableModel();
	  setModel(tabModel);
      }

      entityModels = new IEntity[nbRows][nbColumns];
      for (int i=0; i<nbRows; i++)
          for (int j=0; j<nbColumns; j++)
	       entityModels[i][j] = null;
      if (columnIdents == null)
         columnIdents = new String[nbColumns];
      tabModel.init();
      initColumnHeaderRenderers();
   }
   
   
   
   @Override
   public void clearModelAt( int r, int c )
   {
       if (entityModels == null) return;
       if ((nbRows <= 0) || (nbColumns <= 0))
       {
          System.out.println("Please set the number of columns and rows before calling clearModelAt.");
	  return;
       }
       
       if ((r < 0) || (c < 0)) return;
       if ((r >= nbRows) || (c >= nbColumns)) return;
       
       if (entityModels[r][c] == null) return;
       
       if (entityModels[r][c] instanceof VoidVoidCommand)
       {
           MultiAttAndCmdViewerTableModel tm = (MultiAttAndCmdViewerTableModel) tabModel;
           tm.removeCmdAt(r, c);           
	   entityModels[r][c] = null;
	   return;
       }
       super.clearModelAt(r, c);
       
   }


   @Override
   public void clearModel()
   {
       if (entityModels == null) return;
       
       for (int i=0; i<entityModels.length; i++)
       {
          for (int j=0; j<entityModels[i].length; j++)
	       clearModelAt(i,j);
       }
       
       entityModels = null;
       columnIdents=null;
       rowIdents=null;
       nbRows=0;
       nbColumns=0;

       tabModel.setColumnCount(0);
       tabModel.setRowCount(0);
       
       tabModel = new MultiAttAndCmdViewerTableModel();
       setModel(tabModel);
   }

   @Override
   protected void tableMouseClick(MouseEvent e)
   {
       int  row = getSelectedRow();
       int  col = getSelectedColumn();
       
       if (tabModel.getValueAt(row, col) instanceof VoidVoidCommandViewer)
       {
           VoidVoidCommandViewer cmdv = (VoidVoidCommandViewer)tabModel.getValueAt(row, col);
           cmdv.doClick();
           return;
       }
       
       super.tableMouseClick(e);
       
   }



   
    
            // inner classes
   
   
            class MultiAttAndCmdViewerTableModel extends MultiScalarViewerTableModel
            {
                /**
                 * Creates a new instance of MultiAttAndCmdViewerTableModel
                 */
                MultiAttAndCmdViewerTableModel()
                {
                }

                public boolean isCellEditable(int row, int column)
                {
                    return false;
                }

                void init()
                {
                    if (entityModels == null) return;

                    if (entityModels.length != nbRows)
                        nbRows = entityModels.length;

                    if (entityModels[0].length != nbColumns)
                        nbColumns = entityModels[0].length;

                    if (rowIdents != null)
                        if (rowIdents.length != nbRows)
                            rowIdents = null;

                    if (rowIdents != null)
                    {
                        hasRowLabels = true;

                        String[] colIds = null;
                        if (columnIdents != null)
                        {
                            colIds = new String[columnIdents.length + 1];
                            colIds[0] = " ";
                            for (int j = 0; j < columnIdents.length; j++)
                                colIds[j + 1] = columnIdents[j];
                        }
                        else
                        {
                            colIds = new String[nbColumns + 1];
                            for (int j = 0; j < nbColumns + 1; j++)
                                colIds[j] = " ";
                        }
                        
                        Object[][] tableData = new Object[entityModels.length][entityModels[0].length + 1];
                        setDataVector(tableData, colIds);

                        for (int i = 0; i < nbRows; i++)
                            setValueAt(rowIdents[i], i, 0);
                    }
                    else
                    {
                        this.setDataVector(entityModels, columnIdents);
                    }
                    //this.fireTableStructureChanged();
                    //this.fireTableDataChanged();
                    //doLayout();
                }

                void addCommandAt(int r, int c, ICommand icmd, VoidVoidCommandViewer cmdViewer)
                {
                    int col;
                    VoidVoidCommand  vvc = null;

                    if (icmd instanceof VoidVoidCommand)
                    {
                        vvc = (VoidVoidCommand) icmd;
                    }

                    if (vvc == null)
                    {
                        return;
                    }

                    col = c;
                    if (hasRowLabels)
                    {
                        col = c + 1;
                    }

                    setValueAt(cmdViewer, r, col);

                    ArrayList<Integer> cmdIndexes = new ArrayList<Integer>();
                    cmdIndexes.add(0, new Integer(r));
                    cmdIndexes.add(1, new Integer(col));
                    if (!entityMap.containsKey(icmd))
                    {
                        entityMap.put(icmd, cmdIndexes);
                    }
                    fireTableDataChanged();
                }
                
                void removeCmdAt(int r, int c)
                {
                    int col = c;
                    if (hasRowLabels)
                    {
                        col = c + 1;
                    }

                    Object obj = getValueAt(r, col);

                    if (obj == null)
                    {
                        return;
                    }

                    if (obj instanceof VoidVoidCommandViewer)
                    {
                        VoidVoidCommandViewer vvViewer = (VoidVoidCommandViewer) obj;

                        removeCmdAt(vvViewer, r, col);
                        return;
                    }

                }

                private void removeCmdAt(VoidVoidCommandViewer cmdv, int r, int c)
                {
                    cmdv.setModel((ICommand) null);
                    cmdv = null;
                    setValueAt(null, r, c);
                    fireTableDataChanged();
                }
            }
            

            class MultiAttAndCmdViewerCellRenderer extends MultiScalarViewerCellRenderer
            {

                /**
                 * Creates a new instance of MultiAttAndCmdViewerCellRenderer
                 */
                MultiAttAndCmdViewerCellRenderer()
                {
                }

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column)
                {
                    VoidVoidCommandViewer cmdv;

                    if (value instanceof VoidVoidCommandViewer)
                    {
//                        JPanel jp = new JPanel();
//                        jp.setLayout(new GridBagLayout());
//                        GridBagConstraints gbc = new GridBagConstraints();
//                        gbc.gridx = 0;
//                        gbc.gridy = 0;
//                        gbc.fill = GridBagConstraints.NONE;
//                        cmdv = (VoidVoidCommandViewer) value;
//                        jp.add(cmdv, gbc);
//                        return jp;
                        cmdv = (VoidVoidCommandViewer) value;
                        return cmdv;
                    }
                    else
                    {
                        return (super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
                    }
                }
            }
   
   
    // ---------------------------------------------------
    // Main test fucntion
    // ---------------------------------------------------
    static public void main(String args[])
    {
        IAttribute att;
        ICommand   cmd;
        String[] colLabs =
        {
            "att_un", "att_deux", "att_trois", "att_cinq", "att_six", "att_bool", ""
        };
        String[] rowLabs =
        {
            "jlp/test/1", "jlp/test/2"
        };

        AttributeList attl = new AttributeList();
        CommandList   cmdl = new CommandList();
        JFrame f = new JFrame();
        MultiAttAndCmdTableViewer mstv = new MultiAttAndCmdTableViewer();

        IAttribute[][] attArray = null;
        //mstv.setAlarmEnabled(false);
        mstv.setUnitVisible(false);
        mstv.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14));
        //mstv.getRowIdCellRenderer().setBackground(f.getBackground());
        //mstv.setNbRows(2);
        //mstv.setNbColumns(6);
        //mstv.setRowIdents(rowLabs);
        //mstv.setColumnIdents(colLabs);
        mstv.setNbRows(2);
        mstv.setNbColumns(7);
        mstv.setRowIdents(rowLabs);
        mstv.setColumnIdents(colLabs);
        attArray = new IAttribute[2][6];

        try
        {
            att = (IAttribute) attl.add("jlp/test/1/att_un");
            attArray[0][0] = att;
            mstv.setModelAt(att, 0, 0);
            att = (IAttribute) attl.add("jlp/test/1/att_deux");
            attArray[0][1] = att;
            mstv.setModelAt(att, 0, 1);
            att = (IAttribute) attl.add("jlp/test/1/att_trois");
            attArray[0][2] = att;
            mstv.setModelAt(att, 0, 2);
            att = (IAttribute) attl.add("jlp/test/1/att_cinq");
            //att = (IAttribute) attl.add("fp/test/1/string_scalar");
            attArray[0][3] = att;
            mstv.setModelAt(att, 0, 3);
            att = (IAttribute) attl.add("jlp/test/1/att_six");
            attArray[0][4] = att;
            mstv.setModelAt(att, 0, 4);
            att = (IAttribute) attl.add("jlp/test/1/att_boolean");
            attArray[0][5] = att;
            mstv.setModelAt(att, 0, 5);
            att = (IAttribute) attl.add("jlp/test/2/att_un");
            attArray[1][0] = att;
            mstv.setModelAt(att, 1, 0);
            att = (IAttribute) attl.add("jlp/test/2/att_deux");
            attArray[1][1] = att;
            mstv.setModelAt(att, 1, 1);
            att = (IAttribute) attl.add("jlp/test/2/att_trois");
            attArray[1][2] = att;
            mstv.setModelAt(att, 1, 2);
            att = (IAttribute) attl.add("jlp/test/2/att_cinq");
            //att = (IAttribute) attl.add("fp/test/2/string_scalar");
            attArray[1][3] = att;
            mstv.setModelAt(att, 1, 3);
            att = (IAttribute) attl.add("jlp/test/2/att_six");
            attArray[1][4] = att;
            mstv.setModelAt(att, 1, 4);
            att = (IAttribute) attl.add("jlp/test/2/att_boolean");
            attArray[1][5] = att;
            mstv.setModelAt(att, 1, 5);
            
            cmd = (ICommand) cmdl.add("jlp/test/1/Reset");
            mstv.setModelAt(cmd, 0, 6);
            
            cmd = (ICommand) cmdl.add("jlp/test/2/On");
            mstv.setConfirmCommandAt(cmd, 1, 6, "Do you want to turn On?");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Cannot connect to jlp/test/1");
        }

        mstv.setRowHeight(25);
        mstv.setRowMargin(1);
        TableColumn column = null;
        for (int i = 0; i < colLabs.length; i++)
        {
            column = mstv.getColumnModel().getColumn(i+1);
            if (colLabs[i].isEmpty())
                column.setPreferredWidth(35); //command column is smaller
            else
                column.setPreferredWidth(column.getWidth());
        }

        attl.startRefresher();

        // It is necessary to put the table inside a JScrollPane. The JTable does not
        // display the column names if the JTable is not in a scrollPane!!!
        mstv.setPreferredScrollableViewportSize(new java.awt.Dimension(700, 70));
        JScrollPane scrollPane = new JScrollPane(mstv);

        f.setContentPane(scrollPane);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //mstv.doLayout();
        f.pack();
        f.setVisible(true);
        
        
//        
//        //mstv.setModelAt(attArray[0][2], 0, 2);
//        mstv.setModelAt(attArray[0][2], 2, 0);
//
//        try
//        {
//            System.in.read();
//        }
//        catch (Exception ex)
//        {
//            System.out.println("cannot read");
//        }
//
//        mstv.clearModel();
//        //mstv.setNbRows(6);
//        //mstv.setNbColumns(2);
//        //mstv.setRowIdents(colLabs);
//        //mstv.setColumnIdents(rowLabs); 
//        mstv.setNbRows(2);
//        mstv.setNbColumns(6);
//        mstv.setRowIdents(rowLabs);
//        mstv.setColumnIdents(colLabs);
//
//        mstv.setModelAt(attArray[1][4], 1, 4);
//        //mstv.setModelAt(attArray[1][4], 4, 1);
    }

}

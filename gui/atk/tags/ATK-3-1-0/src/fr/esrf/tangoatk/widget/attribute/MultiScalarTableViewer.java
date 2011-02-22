// File:          MultiScalarTableViewer.java
// Created:       2007-05-09 15:03:37, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;


import java.util.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.Component;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.event.*;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;


/** A MultiScalarTableViewer is a Swing JTable which displays the "read value" of 
 * several scalar attributes each of them in one cell of the table.
 * The table cells are not editable, but a double click on any cell will display a
 * panel to set the attribute value only if the attribute is writable.
 *
 */
public class MultiScalarTableViewer extends JTable
{

   private int                   nbRows=0;
   private int                   nbColumns=0;
   private String[]              columnIdents=null;
   private String[]              rowIdents=null;
   private boolean               alarmEnabled=true;
   private boolean               unitVisible=true;
   private Color                 panelBackground=null;
   private IAttribute[][]        attModels=null;

   private MultiScalarViewerTableModel    tabModel=null;
   private MultiScalarViewerCellRenderer  scalarViewerRenderer=null;
   private RowIdentsCellRenderer          rowIdentsRenderer=null;
   private ColHeaderCellRenderer          colHeadRenderer=null;
   private long                           firstClickTime;
   
   private RootPaneContainer              rpcParent = null;
   private JDialog                        attSetDialWindow=null;
   private ScalarAttributeSetPanel        attSetPanel=null;
   
   private boolean                        noAttModel=true;

   // ---------------------------------------------------
   // Contruction
   // ---------------------------------------------------
   public MultiScalarTableViewer()
   {
       firstClickTime = 0;
       tabModel = new MultiScalarViewerTableModel();
       setModel(tabModel);
       scalarViewerRenderer = new MultiScalarViewerCellRenderer();
       rowIdentsRenderer = new RowIdentsCellRenderer();
       colHeadRenderer = new ColHeaderCellRenderer();
       //panelBackground = rowIdentsRenderer.getBackground();
       panelBackground = new java.awt.Color(235,235,235);
       //setAutoResizeMode(AUTO_RESIZE_OFF);
       setRowMargin(0);
       //getColumnModel().setColumnMargin(getColumnModel().getColumnMargin()+2);
       addMouseListener( new MouseAdapter()
                             {
				 public void mouseClicked(MouseEvent e)
				 {
				     tableMouseDoubleClick(e);
				 }
				 public void mouseEntered(MouseEvent e) {}
				 public void mouseExited(MouseEvent e) {}
				 public void mousePressed(MouseEvent e) {}
				 public void mouseReleased(MouseEvent e) {}
			     }
		       );
	
   }
   
   //override the getCellRenderer method of JTable
   public TableCellRenderer getCellRenderer(int row, int column)
   {
       if ( (nbRows<=0) || (nbColumns<=0) )
          return new MultiScalarViewerCellRenderer();

       if (attModels == null)
          return new MultiScalarViewerCellRenderer();
	  
       if (attModels.length <= 0) 
          return new MultiScalarViewerCellRenderer();
	  
       if (tabModel.getHasRowLabels())
       {
	  if (column == 0)
	     return rowIdentsRenderer;
       }

       Object obj=tabModel.getValueAt(row, column);

       if (obj != null)
	  if (obj instanceof SimpleScalarViewer)
	      return scalarViewerRenderer;

       if (obj != null)
	  if (obj instanceof SimpleEnumScalarViewer)
	      return scalarViewerRenderer;

       if (obj != null)
	  if (obj instanceof BooleanScalarCheckBoxViewer)
	      return scalarViewerRenderer;

       return super.getCellRenderer(row, column);
   } 
   
   //Should override the setModel method of JTable because it is not autorized to call the setModel method of the superclass
   // But overriding this method make a bug in NetBeans IDE when trying to add
   // MultiScalarTableViewer from the palette inside the form editor.
   // the issue has been submitted to http://www.netbeans.org/community/issues.html
   
   //public void setModel(TableModel  tm)
   //{
   //}
   
   public Color getPanelBackground()
   {
      return panelBackground;
   }
   
   public void setPanelBackground(Color bg)
   {
       panelBackground = bg;
       attSetDialWindow.setBackground(panelBackground);
       attSetPanel.setBackground(panelBackground);
   }

   private void tableMouseDoubleClick(MouseEvent e)
   {
       boolean   doubleclick;
       long      clickTime = System.currentTimeMillis();
       long      clickInterval = clickTime-firstClickTime;

       if (clickInterval < 500)
       {
	    // double click
	    firstClickTime = 0;
	    doubleclick = true;
       }
       else 
       {
	   firstClickTime = clickTime;
	   doubleclick = false;
       }
       
       if (doubleclick == false)
	   return;
	  
       if (tabModel.getHasRowLabels())
	  if (getSelectedColumn() == 0)
	     return;
       
       //System.out.println("tableMouseDoubleClicked : row="+getSelectedRow()+" column="+getSelectedColumn());
       doEdit(getSelectedRow(), getSelectedColumn());
   }
   
   private void doEdit(int r, int c )
   {
       int  col = c;
       if (tabModel.getHasRowLabels())
          col = c-1;
	  
       if ((r < 0) || (col < 0)) return;
       if ((r >= nbRows) || (col >= nbColumns)) return;
       
       IAttribute iatt = attModels[r][col];
       if (iatt == null) return;
       if (!iatt.isWritable() ) return;
	
       if (    !(iatt instanceof INumberScalar)
	    && !(iatt instanceof IStringScalar)
	    && !(iatt instanceof IEnumScalar)
	    && !(iatt instanceof IBooleanScalar) )
	   return;
       
       if ( (attSetDialWindow == null) || (attSetPanel == null) )
       {
	     creatScalarSetWindows();	  
       }
       
       if ( (attSetDialWindow == null) || (attSetPanel == null) )
          return;
	  	  
       if (attSetPanel.getAttModel() != iatt)
       {
	  attSetDialWindow.setVisible(false); // To repaint properly when changing attribute type
          attSetPanel.setAttModel(iatt);
	  attSetDialWindow.setTitle(iatt.getName());
	  attSetDialWindow.pack();
       }
       attSetDialWindow.setVisible(true);
   }
   
   
   private void creatScalarSetWindows()
   {
	Component   parent = this;
	while (rpcParent == null)
	{
	    parent = parent.getParent();
	    if (parent == null)
	    {
	       break;
	    }
	    if (parent instanceof RootPaneContainer)
	    {
	        rpcParent = (RootPaneContainer) parent;
		break;
	    }
	}
	
	if (rpcParent != null)
	   System.out.println("MultiScalarTableViewer : the parent class (implementing RootPaneContainer) is : "+rpcParent.getClass().getName());


	if (rpcParent == null)
           attSetDialWindow = new JDialog();
	else
	   if (rpcParent instanceof Frame)
	      attSetDialWindow = new JDialog( (Frame) rpcParent );
	   else
	      if (rpcParent instanceof Dialog)
	         attSetDialWindow = new JDialog( (Dialog) rpcParent );
	      else
	         attSetDialWindow = new JDialog();
		 
	attSetDialWindow.getContentPane().setLayout(new java.awt.GridBagLayout());
        attSetDialWindow.getContentPane().setBackground(panelBackground);
	
	attSetPanel = new ScalarAttributeSetPanel();
        attSetPanel.setBackground(panelBackground);
	attSetPanel.setFont(getFont());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
	gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        attSetDialWindow.getContentPane().add(attSetPanel, gbc);

        JButton dismissButton = new JButton();
        dismissButton.setText("Dismiss");
        dismissButton.addActionListener(new java.awt.event.ActionListener()
			      {
        			  public void actionPerformed(java.awt.event.ActionEvent evt) {
                		      attSetDialWindow.setVisible(false);
        			  }
        		      });
	
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
	gbc.anchor = java.awt.GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        attSetDialWindow.getContentPane().add(dismissButton, gbc);
   }


   // ---------------------------------------------------
   // Property stuff
   // ---------------------------------------------------

   /**
    *<code>getNbRows</code> returns the number of rows
    * 
    * @return a <code>int[]</code> Number of rows
    */
   public int getNbRows ()
   {
       return nbRows;
   }

   /**
    * <code>setNbRows</code> sets the number of rows. The number of rows can only be set
    * when there is no attribute model for the viewer. No call to setModelAt yet.
    *
    * @param int nr
    */
   public void setNbRows (int nr)
   {
      if (nr <= 0) return;

      if (attModels != null) return;
      if (rowIdents != null)
         if (nr != rowIdents.length)
	    return;

      nbRows = nr;
   }
   /**
    *<code>getNbColumns</code> returns the number of columns
    * 
    * @return a <code>int[]</code> Number of columns
    */
   public int getNbColumns ()
   {
       return nbColumns;
   }

   /**
    * <code>setNbColumns</code> sets the number of columns. The number of columns can only be set
    * when there is no attribute model for the viewer. No call to setModelAt yet.
    *
    * @param int nr
    */
   public void setNbColumns (int nc)
   {
      if (nc <= 0) return;

      if (attModels != null) return;
      if (columnIdents != null)
         if (nc != columnIdents.length)
	    return;

      nbColumns = nc;
   }


   /**
    *<code>getColumnIdents</code> returns a String Array corresponding to the column identifiers
    * 
    * @return a <code>String[]</code> value
    */
   public String[] getColumnIdents ()
   {
       return columnIdents;
   }

   /**
    * <code>setColumnIdents</code> sets the table's column identifiers
    * The size of the colIds array must be exactly the same as the number of columns for
    * the attribute models.If the attribute model is not set yet the size of the colIds will change
    * the nbColumns property as well.
    *
    * @param colIds
    */
   public void setColumnIdents (String[] colIds)
   {
      if (colIds == null)
      {
         columnIdents = null;
	 return;
      }

      if (attModels != null)
         if (attModels[0].length != colIds.length)
	    return;

      if (attModels == null)
         nbColumns = colIds.length;

      columnIdents = colIds;
      if ((noAttModel) && (nbRows > 0))
         initAttModels();
   }


   /**
    *<code>getRowIdents</code> returns a String Array corresponding to the row identifiers
    * 
    * @return a <code>String[]</code> value
    */
   public String[] getRowIdents ()
   {
       return rowIdents;
   }

   /**
    * <code>setRowIdents</code> sets the table's row identifiers
    * The RowIdents can only be set when there is no attribute model for the viewer. No call to setModelAt yet.If the attribute model is not set yet the size of the rowIds will change
    * the nbRow sproperty as well.
    *
    * @param rowIds
    */
   public void setRowIdents (String[] rowIds)
   {
      if (rowIds == null)
      {
         rowIdents = null;
	 return;
      }

      if (attModels != null)
         if (attModels.length != rowIds.length)
	    return;

      if (attModels == null)
         nbRows = rowIds.length;

      rowIdents = rowIds;
      if ((noAttModel) && (nbColumns > 0))
         initAttModels();
   }


   /**
    *<code>getAlarmEnabled</code> returns a boolean : true if the quality factor is displayed in the scalarviewers
    * 
    * @return a <code>boolean</code> value
    */
   public boolean getAlarmEnabled ()
   {
       return alarmEnabled;
   }

   /**
    * <code>setAlarmEnabled</code> sets the quality factor display to on or off
    *
    * @param alarm <code>boolean</code> if true the attribute quality factor will be displayed as the background colour of the cell
    */
   public void setAlarmEnabled (boolean alarm)
   {
      if (alarmEnabled == alarm)
	 return;
	 
      alarmEnabled = alarm;
      
      if (attModels == null)
          return;

      for (int i=0; i<attModels.length; i++)
          for (int j=0; j<attModels[i].length; j++)
	  {
	      int col = j;
	      if (tabModel.getHasRowLabels())
	          col = j+1;
	      Object obj = tabModel.getValueAt(i, col);
	      if (obj instanceof SimpleScalarViewer)
	      {
	         SimpleScalarViewer ssv = (SimpleScalarViewer) obj;
		 ssv.setAlarmEnabled(alarmEnabled);
	      }
	  }
   }

   /**
    * Detemines whether the unit is visible
    * @return true if unit is visible
    */
   public boolean getUnitVisible()
   {
      return unitVisible;
   }

   /**
    * Displays or hides the unit.
    * @param b true to display the unit, false otherwise
    */
   public void setUnitVisible(boolean b)
   {
      if (unitVisible == b)
	 return;
	 
      unitVisible = b;

      if (attSetPanel != null) attSetPanel.setUnitVisible(unitVisible);
      
      if (attModels == null)
          return;

      for (int i=0; i<attModels.length; i++)
          for (int j=0; j<attModels[i].length; j++)
	  {
	      int col = j;
	      if (tabModel.getHasRowLabels())
	          col = j+1;
	      Object obj = tabModel.getValueAt(i, col);
	      if (obj instanceof SimpleScalarViewer)
	      {
	         SimpleScalarViewer ssv = (SimpleScalarViewer) obj;
		 ssv.setUnitVisible(unitVisible);
	      }
	  }
   }
   
   public JLabel getRowIdCellRenderer()
   {
       return rowIdentsRenderer;
   }



   public IAttribute[][] getAttModels()
   {
      return attModels;
   }


   public void setModelAt( IAttribute iatt, int r, int c )
   {
       boolean supportedAttribute = false;
       
       if ((nbRows <= 0) || (nbColumns <= 0))
       {
          System.out.println("Please set the number of columns and rows of the table before calling setModelAt.");
	  return;
       }
       
       if (    (iatt instanceof INumberScalar)
            || (iatt instanceof IStringScalar)
            || (iatt instanceof IEnumScalar)
	    || (iatt instanceof IBooleanScalar) )
	    supportedAttribute = true;
	    
       if (!supportedAttribute)
       {
          System.out.println("Unsupported type of attribute; setModelAt failed.");
	  return;
       }
       
       if (attModels == null)
       {
          initAttModels();
       }
       
       if (noAttModel) noAttModel=false;
       
       if ((r < 0) || (c < 0)) return;
       if ((r >= nbRows) || (c >= nbColumns)) return;
       
       clearModelAt(r, c);
       
       if (iatt == null)
          return;
	  
       attModels[r][c] = iatt;
       
       if (iatt instanceof INumberScalar)
       {
           INumberScalar ins = (INumberScalar) iatt;
	   addAttributeAt(ins, r, c);
	   return;
       }
       
       if (iatt instanceof IStringScalar)
       {
           IStringScalar iss = (IStringScalar) iatt;
	   addAttributeAt(iss, r, c);
	   return;
       }
       
       if (iatt instanceof IEnumScalar)
       {
           IEnumScalar ies = (IEnumScalar) iatt;
	   addAttributeAt(ies, r, c);
	   return;
       }
       
       if (iatt instanceof IBooleanScalar)
       {
           IBooleanScalar ibs = (IBooleanScalar) iatt;
	   addAttributeAt(ibs, r, c);
	   return;
       }
   }


   private void addAttributeAt( INumberScalar ins, int r, int c )
   {       
       SimpleScalarViewer  ssViewer = new SimpleScalarViewer();
       ssViewer.setBackgroundColor(getBackground());
       ssViewer.setFont(getFont());
       ssViewer.setModel(ins);
       ssViewer.setAlarmEnabled(alarmEnabled);
       ssViewer.setUnitVisible(unitVisible);
       ssViewer.setHasToolTip(true);
       
       if ( ((double) rowHeight) < ssViewer.getPreferredSize().getHeight() )
          rowHeight = (int) ssViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,ins,ssViewer);
   }


   private void addAttributeAt( IStringScalar iss, int r, int c )
   { 
       SimpleScalarViewer  ssViewer = new SimpleScalarViewer();
       ssViewer.setBackgroundColor(getBackground());
       ssViewer.setFont(getFont());
       ssViewer.setModel(iss);
       ssViewer.setAlarmEnabled(alarmEnabled);
       ssViewer.setHasToolTip(true);
       
       if ( ((double) rowHeight) < ssViewer.getPreferredSize().getHeight() )
          rowHeight = (int) ssViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,iss,ssViewer);
   }


   private void addAttributeAt( IEnumScalar ies, int r, int c )
   { 
       SimpleEnumScalarViewer  enumViewer = new SimpleEnumScalarViewer();
       enumViewer.setBackgroundColor(getBackground());
       enumViewer.setFont(getFont());
       enumViewer.setModel(ies);
       enumViewer.setAlarmEnabled(alarmEnabled);
       
       if ( ((double) rowHeight) < enumViewer.getPreferredSize().getHeight() )
          rowHeight = (int) enumViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,ies,enumViewer);
   }


   private void addAttributeAt( IBooleanScalar ibs, int r, int c )
   { 
       BooleanScalarCheckBoxViewer  boolCbViewer = new BooleanScalarCheckBoxViewer();
       boolCbViewer.setBorderPainted(true);
       boolCbViewer.setBorder(javax.swing.plaf.BorderUIResource.getEtchedBorderUIResource());
       boolCbViewer.setBackground(getBackground());
       boolCbViewer.setFont(getFont());
       boolCbViewer.setAttModel(ibs);
       //boolCbViewer.setQualityEnabled(alarmEnabled);
       boolCbViewer.setTrueLabel(new String());
       boolCbViewer.setFalseLabel(new String());
       boolCbViewer.setHasToolTip(true);
       boolCbViewer.setHorizontalAlignment(SwingConstants.CENTER);

       
       if ( ((double) rowHeight) < boolCbViewer.getPreferredSize().getHeight() )
          rowHeight = (int) boolCbViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,ibs,boolCbViewer);
   }
   
   public void clearModelAt( int r, int c )
   {
       if (attModels == null) return;
       if ((nbRows <= 0) || (nbColumns <= 0))
       {
          System.out.println("Please set the number of columns and rows before calling clearModelAt.");
	  return;
       }
       
       if ((r < 0) || (c < 0)) return;
       if ((r >= nbRows) || (c >= nbColumns)) return;
       
       if (attModels[r][c] == null) return;
       
       if (attModels[r][c] instanceof INumberScalar)
       {
           tabModel.removeAttributeAt(r,c);
	   attModels[r][c] = null;
	   return;
       } 
       
   }


   public void clearModel()
   {
       if (attModels == null) return;
       
       for (int i=0; i<attModels.length; i++)
       {
          for (int j=0; j<attModels[i].length; j++)
	       clearModelAt(i,j);
       }
       
       attModels = null;
       noAttModel=true;
       columnIdents=null;
       rowIdents=null;
       nbRows=0;
       nbColumns=0;

       tabModel.setColumnCount(0);
       tabModel.setRowCount(0);
       
       tabModel = new MultiScalarViewerTableModel();
       setModel(tabModel);
   }


   private void initAttModels()
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
      if (! (tm instanceof MultiScalarViewerTableModel) )
      {
	  tabModel = new MultiScalarViewerTableModel();
	  setModel(tabModel);
      }

      attModels = new IAttribute[nbRows][nbColumns];
      for (int i=0; i<nbRows; i++)
          for (int j=0; j<nbColumns; j++)
	       attModels[i][j] = null;
      if (columnIdents == null)
         columnIdents = new String[nbColumns];
      tabModel.init();
      initColumnHeaderRenderers();
   }
   
   private void initColumnHeaderRenderers()
   {
      if (columnIdents == null)
         return;
      
      for (int i=0; i<columnIdents.length; i++)
      {
          try
	  {
	     TableColumn tc= getColumn(columnIdents[i]);
	     tc.setHeaderRenderer(colHeadRenderer);
	  }
	  catch (IllegalArgumentException iae)
	  {
	  }
      }
   }
    

       class MultiScalarViewerTableModel extends DefaultTableModel
                                         implements INumberScalarListener,
					            IStringScalarListener,
					            IEnumScalarListener,
						    IBooleanScalarListener
       {
	   private boolean                                hasRowLabels = false;
	   private HashMap<IAttribute, Vector<Integer>>   attMap = null;

	   /** Creates a new instance of MSviewerTableModel */
	   MultiScalarViewerTableModel()
	   {
	       attMap = new HashMap<IAttribute, Vector<Integer>> ();
	   }
	   
	   public boolean isCellEditable(int row, int column)
	   {
	       return false;
	   }

	   void init ()
	   {
               if (attModels == null) return;
	       if (attModels.length != nbRows)
		  nbRows = attModels.length;
	       if (attModels[0].length != nbColumns)
		  nbColumns = attModels[0].length;

	       if (rowIdents != null)
		  if (rowIdents.length != nbRows)
	             rowIdents = null;
               if (rowIdents != null)
	       {
		  hasRowLabels = true;
		  
		  String[]  colIds=null;
		  if (columnIdents != null)
		  {
		      colIds = new String[columnIdents.length+1];
		      colIds[0]=" ";
		      for (int j=0; j<columnIdents.length; j++)
		          colIds[j+1] = columnIdents[j];
		  }
                  else
                  {
                      colIds = new String[nbColumns+1];
                      for (int j=0; j<nbColumns+1; j++)
		          colIds[j] =" ";
                  }
		  Object[][] tableData= new Object[attModels.length][attModels[0].length+1];
                  setDataVector(tableData, colIds);
		  
		  for (int i=0; i<nbRows; i++)
		      setValueAt(rowIdents[i], i, 0);
		  
	       }
	       else
	          this.setDataVector(attModels, columnIdents);
               //this.fireTableStructureChanged();
               //this.fireTableDataChanged();
               //doLayout();
	   }

	   void addAttributeAt(int r, int c, IAttribute iatt, SimpleScalarViewer ssViewer)
	   {
              int              col;
	      INumberScalar    ins=null;
	      IStringScalar    iss=null;
	      
	      if (iatt instanceof INumberScalar)
	         ins = (INumberScalar) iatt;
	      else
	         if (iatt instanceof IStringScalar)
		    iss = (IStringScalar) iatt;
	      
	      if ( (ins == null) && (iss == null) )
	         return;
		 
	      if (ins != null)
	         ins.addNumberScalarListener(this);
	      else
	         if (iss != null)
	            iss.addStringScalarListener(this);
	      
	      col = c;
	      if (hasRowLabels)
	         col = c+1;
	      setValueAt(ssViewer, r, col);
	      Vector<Integer>  attIndexes = new Vector<Integer> ();
	      attIndexes.add(0, new Integer(r));
	      attIndexes.add(1, new Integer(col));
	      if (!attMap.containsKey(iatt))
	         attMap.put(iatt, attIndexes);
              fireTableDataChanged();
	   }

	   void addAttributeAt(int r, int c, IAttribute iatt, SimpleEnumScalarViewer enumViewer)
	   {
              int              col;
	      IEnumScalar      ies=null;
	      
	      if (iatt instanceof IEnumScalar)
	         ies = (IEnumScalar) iatt;

	      if (ies == null)
	         return;
	      
	      col = c;
	      if (hasRowLabels)
	         col = c+1;
		 
	      ies.addEnumScalarListener(this);
	      setValueAt(enumViewer, r, col);
	      
	      Vector<Integer>  attIndexes = new Vector<Integer> ();
	      attIndexes.add(0, new Integer(r));
	      attIndexes.add(1, new Integer(col));
	      if (!attMap.containsKey(iatt))
	         attMap.put(iatt, attIndexes);
              fireTableDataChanged();
	   }

	   void addAttributeAt(int r, int c, IAttribute iatt, BooleanScalarCheckBoxViewer boolViewer)
	   {
              int              col;
	      IBooleanScalar   ibs=null;
	      
	      if (iatt instanceof IBooleanScalar)
	         ibs = (IBooleanScalar) iatt;

	      if (ibs == null)
	         return;
	      
	      col = c;
	      if (hasRowLabels)
	         col = c+1;
		 
	      ibs.addBooleanScalarListener(this);
	      setValueAt(boolViewer, r, col);
	      
	      Vector<Integer>  attIndexes = new Vector<Integer> ();
	      attIndexes.add(0, new Integer(r));
	      attIndexes.add(1, new Integer(col));
	      if (!attMap.containsKey(iatt))
	         attMap.put(iatt, attIndexes);
              fireTableDataChanged();
	   }

	   void removeAttributeAt(int r, int c)
	   {
              int col = c;
	      if (hasRowLabels)
		 col = c+1;

	      Object obj = getValueAt(r, col);

	      if (obj == null) return;

	      if (obj instanceof SimpleScalarViewer)
	      {
		  SimpleScalarViewer  ssv = (SimpleScalarViewer) obj;
		  
		  removeAttributeAt(ssv, r, col);
		  return;
	      }

	      if (obj instanceof SimpleEnumScalarViewer)
	      {
		  SimpleEnumScalarViewer  enumv = (SimpleEnumScalarViewer) obj;
		  
		  removeAttributeAt(enumv, r, col);
		  return;
	      }

	      if (obj instanceof BooleanScalarCheckBoxViewer)
	      {
		  BooleanScalarCheckBoxViewer  boolv = (BooleanScalarCheckBoxViewer) obj;
		  
		  removeAttributeAt(boolv, r, col);
		  return;
	      }
	   }
	   
	   private void removeAttributeAt(SimpleScalarViewer ssv, int r, int c)
	   {
	      INumberScalar ins = ssv.getNumberModel();
	      IStringScalar iss = ssv.getStringModel();
	      if (ins != null)
	      {
		 if (attMap.containsKey(ins))
		    attMap.remove(ins);
		 ins.removeNumberScalarListener(this);
	      }
	      else
		 if (iss != null)
		 {
		    if (attMap.containsKey(iss))
		       attMap.remove(iss);
	            iss.removeStringScalarListener(this);
		 }
	      ssv.clearModel();
	      ssv=null;
	      setValueAt(null, r, c);
              fireTableDataChanged();
	   }
	   
	   private void removeAttributeAt(SimpleEnumScalarViewer enumv, int r, int c)
	   {
	      IEnumScalar ies = enumv.getModel();
	      if (ies != null)
	      {
		 if (attMap.containsKey(ies))
		    attMap.remove(ies);
		 ies.removeEnumScalarListener(this);
	      }
	      enumv.clearModel();
	      enumv=null;
	      setValueAt(null, r, c);
              fireTableDataChanged();
	   }
	   
	   private void removeAttributeAt(BooleanScalarCheckBoxViewer boolv, int r, int c)
	   {
	      IBooleanScalar ibs = boolv.getAttModel();
	      if (ibs != null)
	      {
		 if (attMap.containsKey(ibs))
		    attMap.remove(ibs);
		 ibs.removeBooleanScalarListener(this);
	      }
	      boolv.clearModel();
	      boolv=null;
	      setValueAt(null, r, c);
              fireTableDataChanged();
	   }
	   
	   boolean getHasRowLabels()
	   {
	       return hasRowLabels;
	   }
	   
	   
	   // -------------------------------------------------------------
	   // Any attribute listener interface
	   // -------------------------------------------------------------
	   public void stateChange(AttributeStateEvent evt)
	   {
	       IAttribute iatt = (IAttribute) evt.getSource();
	       doUpdateAttCell(iatt);
	   }

	   public void errorChange(ErrorEvent evt)
	   {
	       Object src = evt.getSource();
	       if (src instanceof IAttribute)
	       {
	           IAttribute  ia = (IAttribute) src;
		   doUpdateAttCell(ia);
	       }
	   }
	   
	   // -------------------------------------------------------------
	   // Number scalar listener interface
	   // -------------------------------------------------------------
	   public void numberScalarChange(NumberScalarEvent evt)
	   {
	       INumberScalar ins = evt.getNumberSource();
	       doUpdateAttCell(ins);
	   }
	   
	   // -------------------------------------------------------------
	   // String scalar listener interface
	   // -------------------------------------------------------------
	   public void stringScalarChange(StringScalarEvent evt)
	   {
	       IStringScalar iss = (IStringScalar) evt.getSource();
	       doUpdateAttCell(iss);
	   }
	   
	   // -------------------------------------------------------------
	   // Enum Scalar listener interface
	   // -------------------------------------------------------------
	   public void enumScalarChange(EnumScalarEvent evt)
	   {
	       IEnumScalar ies = (IEnumScalar) evt.getSource();
	       doUpdateAttCell(ies);
	   }
	   
	   // -------------------------------------------------------------
	   // Boolean Scalar listener interface
	   // -------------------------------------------------------------
	   public void booleanScalarChange(BooleanScalarEvent evt)
	   {
	       IBooleanScalar ibs = (IBooleanScalar) evt.getSource();
	       doUpdateAttCell(ibs);
	   }
	   
	   private void doUpdateAttCell(IAttribute  iatt)
	   {
	       if (!attMap.containsKey(iatt))
	          return;
		  
	       Vector<Integer> attIndexes = attMap.get(iatt);
	       if (attIndexes == null)
	          return;
		  
	       if (attIndexes.size() >= 2)
	       {
		  Integer  indObj = attIndexes.get(0);
		  int row = indObj.intValue();
		  
		  indObj = attIndexes.get(1);
		  int col = indObj.intValue();
		  
		  fireTableCellUpdated(row, col);
	       }
	   }

       }
       
       
       class MultiScalarViewerCellRenderer implements TableCellRenderer
       {
	   /** Creates a new instance of MultiScalarViewerCellRenderer */
	   MultiScalarViewerCellRenderer()
	   {
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
                                                	  boolean isSelected, boolean hasFocus, int row, int column)
	   {
	       SimpleScalarViewer             ssv;
	       SimpleEnumScalarViewer         enumv;
	       BooleanScalarCheckBoxViewer    boolv;
	       
	       if (value instanceof SimpleScalarViewer)
	       {
		  ssv = (SimpleScalarViewer) value;
		  return ssv;
	       }
	       
	       if (value instanceof SimpleEnumScalarViewer)
	       {
		  enumv = (SimpleEnumScalarViewer) value;
		  return enumv;
	       }
	       
	       if (value instanceof BooleanScalarCheckBoxViewer)
	       {
		  boolv = (BooleanScalarCheckBoxViewer) value;
		  return boolv;
	       }

	       return new JLabel("Unsupported Class");
	   }
       }
       
       class RowIdentsCellRenderer extends JLabel implements TableCellRenderer
       {
	   /** Creates a new instance of RowIdentsCellRenderer */
	   RowIdentsCellRenderer()
	   {
	       setHorizontalAlignment(LEFT);
               setOpaque(true);
	       setBackground(new java.awt.Color(220,220,220));
	       //setBorder(javax.swing.plaf.BorderUIResource.getRaisedBevelBorderUIResource());
	       setBorder(javax.swing.plaf.BorderUIResource.getEtchedBorderUIResource());
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
                                                	  boolean isSelected, boolean hasFocus, int row, int column)
	   {
	       String    rowId;
	       
	       if (value instanceof String)
	       {
		  rowId = (String) value;
		  setText(rowId);
		  return this;
	       }
	       else
		  return new JLabel("Unsupported row id Class");
	   }
       }
       
       class ColHeaderCellRenderer extends RowIdentsCellRenderer
       {
	   /** Creates a new instance of ColHeaderCellRenderer */
	   ColHeaderCellRenderer()
	   {
	       super();
	       setHorizontalAlignment(CENTER);
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
                                                	  boolean isSelected, boolean hasFocus, int row, int column)
	   {
	       String    colId;
	       
	       if (value instanceof String)
	       {
		  colId = (String) value;
		  setText(colId);
		  return this;
	       }
	       else
		  return new JLabel("Unsupported column header Class");
	   }
       }
   
   
   
   // ---------------------------------------------------
   // Main test fucntion
   // ---------------------------------------------------
   static public void main(String args[])
   {
        IAttribute                att;
	String[]                  colLabs = {"att_un", "att_deux", "att_trois", "att_cinq", "att_six", "att_bool"};
	String[]                  rowLabs = {"jlp/test/1", "jlp/test/2"};
	
	AttributeList             attl = new AttributeList();
	JFrame                    f = new JFrame();
	MultiScalarTableViewer    mstv = new MultiScalarTableViewer();
	
        IAttribute[][]            attArray=null;
	//mstv.setAlarmEnabled(false);
	mstv.setUnitVisible(false);
        mstv.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 16));
	//mstv.getRowIdCellRenderer().setBackground(f.getBackground());
        //mstv.setNbRows(2);
	//mstv.setNbColumns(6);
        //mstv.setRowIdents(rowLabs);
	//mstv.setColumnIdents(colLabs);
        mstv.setNbRows(6);
	mstv.setNbColumns(2);
        mstv.setRowIdents(colLabs);
	mstv.setColumnIdents(rowLabs); 
        attArray = new IAttribute[2][6];
	
	try
	{
	   att = (IAttribute) attl.add("jlp/test/1/att_un");
           attArray[0][0] = att;
	   //mstv.setModelAt(att, 0, 0);
           att = (IAttribute) attl.add("jlp/test/1/att_deux");
           attArray[0][1] = att;
	   //mstv.setModelAt(att, 0, 1);
           att = (IAttribute) attl.add("jlp/test/1/att_trois");
           attArray[0][2] = att;
	   //mstv.setModelAt(att, 0, 2);
           //att = (IAttribute) attl.add("jlp/test/1/att_quatre");
           att = (IAttribute) attl.add("jlp/test/1/att_cinq");
           //att = (IAttribute) attl.add("fp/test/1/string_scalar");
           attArray[0][3] = att;
	   //mstv.setModelAt(att, 0, 3);
           att = (IAttribute) attl.add("jlp/test/1/att_six");
           attArray[0][4] = att;
	   //mstv.setModelAt(att, 0, 4);
           att = (IAttribute) attl.add("jlp/test/1/att_boolean");
           attArray[0][5] = att;
	   //mstv.setModelAt(att, 0, 5);
	   att = (IAttribute) attl.add("jlp/test/2/att_un");
           attArray[1][0] = att;
	   //mstv.setModelAt(att, 1, 0);
           att = (IAttribute) attl.add("jlp/test/2/att_deux");
           attArray[1][1] = att;
	   //mstv.setModelAt(att, 1, 1);
           att = (IAttribute) attl.add("jlp/test/2/att_trois");
           attArray[1][2] = att;
	   //mstv.setModelAt(att, 1, 2);
           //att = (IAttribute) attl.add("jlp/test/2/att_quatre");
           att = (IAttribute) attl.add("jlp/test/2/att_cinq");
           //att = (IAttribute) attl.add("fp/test/2/string_scalar");
           attArray[1][3] = att;
	   //mstv.setModelAt(att, 1, 3);
           att = (IAttribute) attl.add("jlp/test/2/att_six");
           attArray[1][4] = att;
	   //mstv.setModelAt(att, 1, 4);
           att = (IAttribute) attl.add("jlp/test/2/att_boolean");
           attArray[1][5] = att;
	  // mstv.setModelAt(att, 1, 5);
	}
	catch (Exception ex)
	{
           ex.printStackTrace();
	   System.out.println("Cannot connect to jlp/test/1");
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
        //mstv.setModelAt(attArray[0][2], 0, 2);
        mstv.setModelAt(attArray[0][2], 2, 0);

        try
	{
	   System.in.read();
	}
	catch (Exception ex)
	{
	   System.out.println("cannot read");
	}
        
	mstv.clearModel();
        //mstv.setNbRows(6);
	//mstv.setNbColumns(2);
        //mstv.setRowIdents(colLabs);
	//mstv.setColumnIdents(rowLabs); 
        mstv.setNbRows(2);
	mstv.setNbColumns(6);
        mstv.setRowIdents(rowLabs);
	mstv.setColumnIdents(colLabs);
        
        mstv.setModelAt(attArray[1][4], 1, 4);
        //mstv.setModelAt(attArray[1][4], 4, 1);
   }
    

}

/*
 * Trend.java
 *
 * Created on May 13, 2002, 4:28 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.widget.util.IControlee;
import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.widget.attribute.TrendSelectionNode;
import fr.esrf.tangoatk.core.ConnectionException;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  pons
 */

 
public class Trend extends JPanel implements IControlee,ActionListener {

    // Constant

    // Selection type
    public static final int SEL_NONE = 0;
    public static final int SEL_X    = 1;
    public static final int SEL_Y1   = 2;
    public static final int SEL_Y2   = 3;

    //Default Color
    public static final Color[] defaultColor = {
      Color.red ,
      Color.blue ,
      Color.cyan ,
      Color.green ,
      Color.magenta ,
      Color.orange ,
      Color.pink ,
      Color.yellow ,
      Color.black };

    // Local declaration
    JFrame    parent=null;
    
    // Toolbar stuff
    JToolBar    theToolBar;    
    JPopupMenu  toolMenu;
    
    JButton   optionButton;
    JMenuItem optionMenuI;
    JButton   stopButton;
    JMenuItem stopMenuI;
    JButton   startButton;
    JMenuItem startMenuI;
    JButton   loadButton;
    JMenuItem loadMenuI;
    JButton   saveButton;
    JMenuItem saveMenuI;
    JButton   zoomButton;
    JMenuItem zoomMenuI;
    JButton   timeButton;
    JMenuItem timeMenuI;

    JMenuItem showtoolMenuI;
    
    JPanel    innerPanel;


    // Selection tree stuff    
    JScrollPane 	   treeView=null;
    JTree                  mainTree=null;
    DefaultTreeModel       mainTreeModel=null;
    TrendSelectionNode     rootNode=null;
    JPopupMenu             treeMenu;
    JMenuItem	           addXMenuItem;
    JMenuItem	           addY1MenuItem;
    JMenuItem	           addY2MenuItem;
    JMenuItem	           removeMenuItem;
    JMenuItem	           optionMenuItem;
    
    // Chart stuff
    JLChart		   theGraph;

    // The models
    fr.esrf.tangoatk.core.AttributeList attList=null;
    private TrendSelectionNode lastAdded;
    
    // Trend constructor
    public Trend(JFrame parent) {
	this();
	this.parent = parent;
    }
    
    public Trend() {
    
          theToolBar = new JToolBar();
	  toolMenu   = new JPopupMenu();
	  
          optionButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_settings.gif")));
	  optionButton.setToolTipText( "Global settings" );
	  optionMenuI  = new JMenuItem("Global settings");

          stopButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_stop.gif")));
	  stopButton.setToolTipText( "Stop monitoring" );
	  stopMenuI  = new JMenuItem("Stop monitoring");

          startButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_start.gif")));
	  startButton.setToolTipText( "Start monitoring" );
	  startMenuI  = new JMenuItem("Start monitoring");

          loadButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_load.gif")));
	  loadButton.setToolTipText( "Load configuration" );
	  loadMenuI  = new JMenuItem("Load configuration");

          saveButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_save.gif")));
	  saveButton.setToolTipText( "Save configuration" );
	  saveMenuI  = new JMenuItem("Save configuration");

          zoomButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_zoom.gif")));
	  zoomButton.setToolTipText( "Zoom" );
	  zoomMenuI  = new JMenuItem("Zoom");

          timeButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_time.gif")));
	  timeButton.setToolTipText( "Set refresh interval" );
	  timeMenuI  = new JMenuItem("Set refresh interval");

          showtoolMenuI = new JMenuItem("Hide toolbar");
	                                          
          theToolBar.setFloatable(true);

	  loadButton.addActionListener(this);
	  loadMenuI.addActionListener(this);
	  saveButton.addActionListener(this);
	  saveMenuI.addActionListener(this);
          optionButton.addActionListener(this);	  
          optionMenuI.addActionListener(this);	  
          zoomButton.addActionListener(this);	  
          zoomMenuI.addActionListener(this);	  
          stopButton.addActionListener(this);	  
          stopMenuI.addActionListener(this);	  
          startButton.addActionListener(this);	  
          startMenuI.addActionListener(this);	  
          timeButton.addActionListener(this);
          timeMenuI.addActionListener(this);
          showtoolMenuI.addActionListener(this);
          
          theToolBar.add(loadButton);
          theToolBar.add(saveButton);
          theToolBar.add(optionButton);
          theToolBar.add(zoomButton);
          theToolBar.add(startButton);
          theToolBar.add(stopButton);
          theToolBar.add(timeButton);
	  
          toolMenu.add(loadMenuI);
          toolMenu.add(saveMenuI);
          toolMenu.add(optionMenuI);
          toolMenu.add(zoomMenuI);
          toolMenu.add(startMenuI);
          toolMenu.add(stopMenuI);
          toolMenu.add(timeMenuI);
          toolMenu.add(showtoolMenuI);

	  // Create the graph
	  theGraph = new JLChart();
	  theGraph.setBorder(new javax.swing.border.EtchedBorder());
          theGraph.setBackground(new java.awt.Color(180, 180, 180));
	  theGraph.getY1Axis().setAutoScale(true);
	  theGraph.getY2Axis().setAutoScale(true);
	  theGraph.getXAxis().setAutoScale(true);
      theGraph.setDisplayDuration(300000.0); // 5min

	  innerPanel = new JPanel();
	  innerPanel.setLayout( new BorderLayout() );
	  
          setLayout(new BorderLayout());
          add(theToolBar, BorderLayout.NORTH);
          innerPanel.add(theGraph, BorderLayout.CENTER);
          add(innerPanel, BorderLayout.CENTER);	  	          

	  // Create the tree popup menu	  
          treeMenu = new JPopupMenu();
          addXMenuItem   =new JMenuItem("Set to X");
          addY1MenuItem  =new JMenuItem("Add to Y1");
          addY2MenuItem  =new JMenuItem("Add to Y2");
          removeMenuItem =new JMenuItem("Remove");
          optionMenuItem =new JMenuItem("Options");
	  treeMenu.add(addXMenuItem);
	  treeMenu.add(addY1MenuItem);
	  treeMenu.add(addY2MenuItem);
	  treeMenu.add(removeMenuItem);
	  treeMenu.add(optionMenuItem);

          addXMenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
	        TrendSelectionNode selNode = (TrendSelectionNode)mainTree.getSelectionPath().getLastPathComponent();
		INumberScalar m = selNode.getModel();
		if( m!=null ) {
		
	          // Remove X view (Only one view on X)
		  int i=0;
		  boolean found=false;
	          Vector dv = rootNode.getSelectableItems();
	          TrendSelectionNode n=null;
		  while(!found && i<dv.size() ) {
		    n = (TrendSelectionNode)dv.get(i);
		    found = ( n.getSelected()==SEL_X );
		    if(!found) i++;
		  } 
		  if( found ) n.setSelected( SEL_NONE );
		  
		  // Select new view
		  selNode.setSelected(SEL_X);
		  mainTree.repaint();
		  theGraph.repaint();
		}
              }
          });

	  
          addY1MenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
	        TrendSelectionNode selNode = (TrendSelectionNode)mainTree.getSelectionPath().getLastPathComponent();
		INumberScalar m = selNode.getModel();
		if( m!=null ) {
		  selNode.setSelected(SEL_Y1);
		  mainTree.repaint();
		  theGraph.repaint();
		}
              }
          });

          addY2MenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
	        TrendSelectionNode selNode = (TrendSelectionNode)mainTree.getSelectionPath().getLastPathComponent();
		INumberScalar m = selNode.getModel();
		if( m!=null ) {
		  selNode.setSelected(SEL_Y2);
		  mainTree.repaint();
		  theGraph.repaint();
		}
              }
          });

          removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
	        TrendSelectionNode selNode = (TrendSelectionNode)mainTree.getSelectionPath().getLastPathComponent();
		INumberScalar m = selNode.getModel();
		if( m!=null ) {
		  selNode.setSelected(SEL_NONE);
		  mainTree.repaint();
		  theGraph.repaint();
		}
              }
          });
	  
          optionMenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
	        TrendSelectionNode selNode = (TrendSelectionNode)mainTree.getSelectionPath().getLastPathComponent();
		INumberScalar m = selNode.getModel();
		if( m!=null ) {
		  selNode.showOptions();
		}
              }
          });
	  
    }
     
    // Action listener 
    public void actionPerformed(ActionEvent evt) {
    
      Object o = evt.getSource();
      if( o==optionButton || o==optionMenuI ) {
        optionButtonActionPerformed(evt);      
      } else if ( o==stopButton || o==stopMenuI ) {
        attList.stopRefresher();
      } else if ( o==startButton || o==startMenuI ) {
        attList.startRefresher();
      } else if ( o==loadButton || o==loadMenuI ) {
        loadButtonActionPerformed(evt);
      } else if ( o==saveButton || o==saveMenuI ) {
        saveButtonActionPerformed(evt);
      } else if ( o==zoomButton || o==zoomMenuI ) {
        if( !theGraph.isZoomed() ) theGraph.enterZoom();
	else                       theGraph.exitZoom();
      } else if ( o==timeButton || o==timeMenuI ) {
        setRefreshInterval();
      } else if ( o==showtoolMenuI ) {
        boolean b = isButtonBarVisible();
	b = !b;
        setButtonBarVisible(b);
      }
       
    }
        
    public void setButtonBarVisible(boolean b) {
      if( theToolBar!=null ) theToolBar.setVisible(b);
    }

    public boolean isButtonBarVisible() {
      if( theToolBar!=null ) 
        return theToolBar.isVisible();
      else
	return false;
    }	

    private void setRefreshInterval() {

      int old_it = (int)attList.getRefreshInterval();
      String i = JOptionPane.showInputDialog(this,"Enter refresh interval (ms)",(Object)new Integer(old_it));
      if( i!=null ) {
        try {
          int it = Integer.parseInt(i);
          attList.setRefreshInterval(it);
        } catch ( NumberFormatException e ) {
          JOptionPane.showMessageDialog(parent,"Invalid number !","Error",JOptionPane.ERROR_MESSAGE);
        }
      }
      
    }

    /**
     * This <code>setModel</code> which takes an AttributeList as a
     * parameter, will just add the attributes in the list to the list
     * viewer in the Trend. It will not add any of the attributes to the
     * Trend
     * @param list a <code>fr.esrf.tangoatk.core.AttributeList</code> value
     */
     public void setModel(fr.esrf.tangoatk.core.AttributeList list) {
    
    	  // Create the selection tree
	  rootNode = new TrendSelectionNode(theGraph);
	  	  
	  for(int i=0;i<list.size();i++) {
	     lastAdded = rootNode.addItem(theGraph,(INumberScalar)list.get(i),defaultColor[i%defaultColor.length]);
	  }
	  
	  TrendRenderer renderer = new TrendRenderer();
	  	  	  
          mainTreeModel = new DefaultTreeModel(rootNode);
          mainTree = new JTree(mainTreeModel);
          mainTree.setCellRenderer(renderer);
	  mainTree.setEditable(false);
          mainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
          mainTree.setShowsRootHandles(true);
	  mainTree.setRootVisible(true);
          mainTree.setBorder( BorderFactory.createLoweredBevelBorder() );
          treeView = new JScrollPane(mainTree);
          mainTree.addMouseListener(new MouseAdapter() {
	    public void mousePressed(MouseEvent e) {
	      revalidate();
              int selRow  = mainTree.getRowForLocation(e.getX(), e.getY());
              TreePath selPath = mainTree.getPathForLocation(e.getX(), e.getY());
	      if(selRow!=-1) {	   
                if(e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                  if( selPath!=null ) {
		     mainTree.setSelectionPath(selPath);
	             TrendSelectionNode selNode = (TrendSelectionNode)selPath.getLastPathComponent();
		     if( selNode.getModel()!=null ) {	             
		       addXMenuItem.setEnabled(selNode.getSelected()!=SEL_X);
		       addY1MenuItem.setEnabled(selNode.getSelected()!=SEL_Y1);
                       addY2MenuItem.setEnabled(selNode.getSelected()!=SEL_Y2);
                       removeMenuItem.setEnabled(selNode.getSelected()!=SEL_NONE);
                       treeMenu.show(mainTree,e.getX(),e.getY());
		     } else if ( selNode == rootNode ) {
		       
		       if( isButtonBarVisible() )
		         showtoolMenuI.setText("Hide toolbar");
		       else
		         showtoolMenuI.setText("Show toolbar");
		       
		       toolMenu.show(mainTree,e.getX(),e.getY());
		     }
                  }
                }
              }
	    }
          });
	  
          //mainTree.addTreeSelectionListener(treeSelectionlistemner);
	  
	  attList = list;
	  innerPanel.add(treeView, BorderLayout.WEST);
    }

    /**
     * <code>addAttribute</code> will add the INumberScalar to the 
     * Trend. Additional calls to addAttribute will add more INumberScalars
     * to the trend.
     * @param scalar a <code>fr.esrf.tangoatk.core.INumberScalar</code> value
     */
    
    public void addAttribute(String name) {
     INumberScalar scalar;
     
     // Add the attribute in the list
     try {
        
        if (attList == null) {
          attList = new fr.esrf.tangoatk.core.AttributeList();
          attList.add( name );
          setModel(attList);
          attList.setRefreshInterval(1000);
          attList.startRefresher();
        } else {
          int i = attList.size();
	  scalar = (INumberScalar)attList.add(name);
          lastAdded = rootNode.addItem(theGraph,scalar,defaultColor[i%defaultColor.length]);
          mainTreeModel = new DefaultTreeModel(rootNode);
	  mainTree.setModel(mainTreeModel);
        }
	
        TreePath np = new TreePath( lastAdded.getPath() );
        mainTree.setSelectionPath(np);
        mainTree.expandPath(np);
        mainTree.makeVisible(np);
	
     } catch (ConnectionException e) {
	    ;
     }
     
      
     innerPanel.revalidate();
    	
    }

    public void addAttribute(INumberScalar scalar) {
     
     // Add the attribute in the list
     System.out.println(" Adding" + scalar.getName() );
     if (attList == null) {
       attList = new fr.esrf.tangoatk.core.AttributeList();
       attList.add( scalar );
       setModel(attList);
       attList.setRefreshInterval(1000);
       attList.startRefresher();
     } else {
        if( !attList.contains(scalar) ) {
          attList.add( scalar );
          int i = attList.size();
          lastAdded = rootNode.addItem(theGraph,scalar,defaultColor[i%defaultColor.length]);
	  mainTreeModel = new DefaultTreeModel(rootNode);
	  mainTree.setModel(mainTreeModel);	  
	}
     }
     
     TreePath np = new TreePath( lastAdded.getPath() );
     mainTree.setSelectionPath(np);
     mainTree.expandPath(np);
     mainTree.makeVisible(np);
     innerPanel.revalidate();
     
    }

    public void removeAttribute(INumberScalar scalar) {
      
      lastAdded = null;      
      if( attList.contains(scalar) ) {
         System.out.println("Removing " + scalar.getName() );
         rootNode.delItem(scalar);
         attList.removeElement( scalar );
	 mainTreeModel = new DefaultTreeModel(rootNode);
	 mainTree.setModel(mainTreeModel);
         innerPanel.revalidate();      	 
      }
      
    }


    /**
     * <code>setModel</code>
     *
     * @param scalar a <code>fr.esrf.tangoatk.core.INumberScalar</code> value
     * @deprecated use addAttribute instead.
     */
    public void setModel(INumberScalar scalar) {
      throw new IllegalStateException
         ("Please use addAttribute() instead of setModel() ");
    }
	
    public fr.esrf.tangoatk.core.AttributeList getModel() {
      return attList;
    }
    

    private void optionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionButtonActionPerformed
      theGraph.showOptionDialog(null);
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionButtonActionPerformed
      
      int ok = JOptionPane.YES_OPTION;
      JFileChooser chooser = new JFileChooser();
      int returnVal = chooser.showSaveDialog(parent);

      if(returnVal == JFileChooser.APPROVE_OPTION) {    
        File f = chooser.getSelectedFile();
        if( f!=null ) {
           if( f.exists() ) ok=JOptionPane.showConfirmDialog(parent,"Do you want to overwrite "+f.getName()+" ?",
	        "Confirm overwrite",JOptionPane.YES_NO_OPTION);
           if( ok == JOptionPane.YES_OPTION ) {
              saveSetting(f.getAbsolutePath());
           }
        }
      }
      
    }

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionButtonActionPerformed
      
      int ok = JOptionPane.YES_OPTION;
      JFileChooser chooser = new JFileChooser();
      int returnVal = chooser.showOpenDialog(parent);

      if(returnVal == JFileChooser.APPROVE_OPTION) {    
        File f = chooser.getSelectedFile();
        if( f!=null ) {
           if( ok == JOptionPane.YES_OPTION ) {
              String err = loadSetting(f.getAbsolutePath());
	      if( err.length()>0 ) {
                JOptionPane.showMessageDialog(parent,err,"Errors reading " + f.getName(),JOptionPane.ERROR_MESSAGE);
	      }
           }
        }
      }
      
    }

    // ****************************************************************    
    // Return graph configuration as String
    // ****************************************************************    
    public String getSettings() {
    
        int    i;
        String to_write="";
            	
        // General settings
        to_write += "graph_title:\'" + theGraph.getHeader() + "\'\n";
	to_write += "label_visible:" + theGraph.isLabelVisible() + "\n";
	to_write += "graph_background:" + OFormat.color(theGraph.getBackground()) + "\n";
	to_write += "title_font:" + OFormat.font(theGraph.getHeaderFont()) + "\n";
	to_write += "display_duration:" + theGraph.getDisplayDuration() + "\n";
	if( attList!=null) to_write += "refresh_time:" + attList.getRefreshInterval() + "\n";
      
        // xAxis
	to_write += "xgrid:" + theGraph.getXAxis().isGridVisible() + "\n";
	to_write += "xsubgrid:" + theGraph.getXAxis().isSubGridVisible() + "\n";
	to_write += "xgrid_style:" + theGraph.getXAxis().getGridStyle() + "\n";
	to_write += "xmin:" + theGraph.getXAxis().getMinimum() + "\n";
	to_write += "xmax:" + theGraph.getXAxis().getMaximum() + "\n";
	to_write += "xautoscale:" + theGraph.getXAxis().isAutoScale() + "\n";
	to_write += "xcale:" + theGraph.getXAxis().getScale() + "\n";
	to_write += "xformat:" + theGraph.getXAxis().getLabelFormat() + "\n";
	to_write += "xtitle:\'" + theGraph.getXAxis().getName() + "\'\n";
	to_write += "xcolor:" + OFormat.color(theGraph.getXAxis().getAxisColor()) + "\n";
	to_write += "xlabel_font:" + OFormat.font(theGraph.getXAxis().getFont()) + "\n";

        // y1Axis
	to_write += "y1grid:" + theGraph.getY1Axis().isGridVisible() + "\n";
	to_write += "y1subgrid:" + theGraph.getY1Axis().isSubGridVisible() + "\n";
	to_write += "y1grid_style:" + theGraph.getY1Axis().getGridStyle() + "\n";
	to_write += "y1min:" + theGraph.getY1Axis().getMinimum() + "\n";
	to_write += "y1max:" + theGraph.getY1Axis().getMaximum() + "\n";
	to_write += "y1autoscale:" + theGraph.getY1Axis().isAutoScale() + "\n";
	to_write += "y1cale:" + theGraph.getY1Axis().getScale() + "\n";
	to_write += "y1format:" + theGraph.getY1Axis().getLabelFormat() + "\n";
	to_write += "y1title:\'" + theGraph.getY1Axis().getName() + "\'\n";
	to_write += "y1color:" + OFormat.color(theGraph.getY1Axis().getAxisColor()) + "\n";
	to_write += "y1label_font:" + OFormat.font(theGraph.getY1Axis().getFont()) + "\n";

        // y2Axis
	to_write += "y2grid:" + theGraph.getY2Axis().isGridVisible() + "\n";
	to_write += "y2subgrid:" + theGraph.getY2Axis().isSubGridVisible() + "\n";
	to_write += "y2grid_style:" + theGraph.getY2Axis().getGridStyle() + "\n";
	to_write += "y2min:" + theGraph.getY2Axis().getMinimum() + "\n";
	to_write += "y2max:" + theGraph.getY2Axis().getMaximum() + "\n";
	to_write += "y2autoscale:" + theGraph.getY2Axis().isAutoScale() + "\n";
	to_write += "y2cale:" + theGraph.getY2Axis().getScale() + "\n";
	to_write += "y2format:" + theGraph.getY2Axis().getLabelFormat() + "\n";
	to_write += "y2title:\'" + theGraph.getY2Axis().getName() + "\'\n";
	to_write += "y2color:" + OFormat.color(theGraph.getY2Axis().getAxisColor()) + "\n";
	to_write += "y2label_font:" + OFormat.font(theGraph.getY2Axis().getFont()) + "\n";

        // dataViews
	if(rootNode==null) return to_write;
	
	Vector dv = rootNode.getSelectableItems();
	TrendSelectionNode n;
	
	to_write += "dv_number:" + dv.size() + "\n";
	
	for(i=0;i<dv.size();i++) {
	  n = (TrendSelectionNode)dv.get(i);
	  to_write += "dv" + i + "_name:\'" + n.getModelName() + "\'\n";
	  to_write += "dv" + i + "_selected:" + n.getSelected() + "\n";
	  to_write += "dv" + i + "_linecolor:" + OFormat.color(n.getData().getColor()) + "\n";
	  to_write += "dv" + i + "_linewidth:" + n.getData().getLineWidth() + "\n";
	  to_write += "dv" + i + "_linestyle:" + n.getData().getStyle() + "\n";
	  to_write += "dv" + i + "_markercolor:" + OFormat.color(n.getData().getMarkerColor()) + "\n";
	  to_write += "dv" + i + "_markersize:" + n.getData().getMarkerSize() + "\n";
	  to_write += "dv" + i + "_markerstyle:" + n.getData().getMarker() + "\n";
	  to_write += "dv" + i + "_A0:" + n.getData().getA0() + "\n";
	  to_write += "dv" + i + "_A1:" + n.getData().getA1() + "\n";
	  to_write += "dv" + i + "_A2:" + n.getData().getA2() + "\n";
	}
      
        return to_write;
    }
    
    // ****************************************************************    
    // Apply settings
    // Return error string
    // ****************************************************************
    private String applySettings(CfFileReader f) {
    
       String       errBuff="";
       Vector       p;
       int          i,nbDv;

       // Reset display duration (to avoid history reading side FX)
       theGraph.setDisplayDuration(Double.POSITIVE_INFINITY);
      
       //Create a new Attribute List
       fr.esrf.tangoatk.core.AttributeList alist = new fr.esrf.tangoatk.core.AttributeList();
       alist.setFilter(new fr.esrf.tangoatk.core.IEntityFilter () {
		public boolean keep(fr.esrf.tangoatk.core.IEntity entity) {
		    if (entity instanceof fr.esrf.tangoatk.core.INumberScalar) {
			return true;
		    }
		    System.out.println( entity.getName() + "not imported (only NumberScalar!)" ); 
		    return false;
		}
       });
       
       // Get all dataviews       
       p = f.getParam("dv_number");
       if( p!=null ) {
       
         try {
           nbDv = Integer.parseInt( p.get(0).toString() );
         } catch (NumberFormatException e) {
	   errBuff += "dv_number: invalid number\n";       
	   return errBuff;
         }
	 
         // Build attribute list  
         for( i=0;i<nbDv;i++ ) {       
         
	   p = f.getParam( "dv" + i + "_name" );
           if( p==null ) {
	     errBuff += ("Unable to find dv"+i+"_name param\n");
             return errBuff;
           }
	 	 
           try {
	     alist.add(p.get(0).toString());          
           } catch (Exception e) {
	     errBuff += (e.getMessage() + "\n");
           }
	   
         }
	 
         //We have the attList
         //Set the model       
         if( nbDv>0 ) {
	   if( attList!=null ) {
             innerPanel.remove( treeView );
	     treeView = null;
	     mainTree=null;
           }
       
           p = f.getParam( "refresh_time" );
           if( p!=null ) alist.setRefreshInterval(OFormat.getInt(p.get(0).toString()));
           else          alist.setRefreshInterval(1000);
       
           alist.startRefresher();
           setModel( alist );
	 }
	 
       } else {
         nbDv = 0;
       }
       
       innerPanel.revalidate();
       
       // Now we can set up the graph
       // General settings
       p = f.getParam( "graph_title" );
       if( p!=null ) theGraph.setHeader(OFormat.getName(p.get(0).toString()));
       p = f.getParam( "label_visible" );
       if( p!=null ) theGraph.setLabelVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "graph_background" );
       if( p!=null ) theGraph.setBackground(OFormat.getColor(p));
       p = f.getParam( "title_font" );
       if( p!=null ) theGraph.setHeaderFont(OFormat.getFont(p));
       p = f.getParam( "display_duration" );
       if( p!=null ) theGraph.setDisplayDuration(OFormat.getDouble(p.get(0).toString()));

       // xAxis
       JLAxis a = theGraph.getXAxis();
       p = f.getParam( "xgrid" );
       if( p!=null ) a.setGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "xsubgrid" );
       if( p!=null ) a.setSubGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "xgrid_style" );
       if( p!=null ) a.setGridStyle(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "xmin" );
       if( p!=null ) a.setMinimum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "xmax" );
       if( p!=null ) a.setMaximum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "xautoscale" );
       if( p!=null ) a.setAutoScale(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "xcale" );
       if( p!=null ) a.setScale(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "xformat" );
       if( p!=null ) a.setLabelFormat(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "xtitle" );
       if( p!=null ) a.setName(OFormat.getName(p.get(0).toString()));
       p = f.getParam( "xcolor" );
       if( p!=null ) a.setAxisColor(OFormat.getColor(p));
       p = f.getParam( "xlabel_font" );
       if( p!=null ) a.setFont(OFormat.getFont(p));

       // y1Axis
       a = theGraph.getY1Axis();
       p = f.getParam( "y1grid" );
       if( p!=null ) a.setGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y1subgrid" );
       if( p!=null ) a.setSubGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y1grid_style" );
       if( p!=null ) a.setGridStyle(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y1min" );
       if( p!=null ) a.setMinimum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "y1max" );
       if( p!=null ) a.setMaximum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "y1autoscale" );
       if( p!=null ) a.setAutoScale(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y1cale" );
       if( p!=null ) a.setScale(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y1format" );
       if( p!=null ) a.setLabelFormat(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y1title" );
       if( p!=null ) a.setName(OFormat.getName(p.get(0).toString()));
       p = f.getParam( "y1color" );
       if( p!=null ) a.setAxisColor(OFormat.getColor(p));
       p = f.getParam( "y1label_font" );
       if( p!=null ) a.setFont(OFormat.getFont(p));

       // y2Axis
       a = theGraph.getY2Axis();
       p = f.getParam( "y2grid" );
       if( p!=null ) a.setGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y2subgrid" );
       if( p!=null ) a.setSubGridVisible(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y2grid_style" );
       if( p!=null ) a.setGridStyle(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y2min" );
       if( p!=null ) a.setMinimum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "y2max" );
       if( p!=null ) a.setMaximum(OFormat.getDouble(p.get(0).toString()));
       p = f.getParam( "y2autoscale" );
       if( p!=null ) a.setAutoScale(OFormat.getBoolean(p.get(0).toString()));
       p = f.getParam( "y2cale" );
       if( p!=null ) a.setScale(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y2format" );
       if( p!=null ) a.setLabelFormat(OFormat.getInt(p.get(0).toString()));
       p = f.getParam( "y2title" );
       if( p!=null ) a.setName(OFormat.getName(p.get(0).toString()));
       p = f.getParam( "y2color" );
       if( p!=null ) a.setAxisColor(OFormat.getColor(p));
       p = f.getParam( "y2label_font" );
       if( p!=null ) a.setFont(OFormat.getFont(p));
       
       // Select signal and apply dataView options
       if( rootNode==null ) return errBuff;
       Vector dv = rootNode.getSelectableItems();
       TrendSelectionNode n=null;
                     
       for(i=0;i<nbDv;i++) {
         String attName;
         String pref = "dv" + i;
	 p = f.getParam( pref + "_name" );	 
	 attName = p.get(0).toString();
	 
	 p = f.getParam( pref + "_selected" );
         if( p!=null ) {
	   int s = OFormat.getInt( p.get(0).toString() );
	   
	   // Find to node to select
	   int j=0;
	   boolean found=false;
	   while( !found && j<dv.size() ) {
             n=(TrendSelectionNode)dv.get(i);	       
	     found = n.getModelName().equals( attName );
	     if(!found) i++;	     
	   }
	   if( found ) {
	   	     
	     if(s>0) n.setSelected(s);
             JLDataView d = n.getData();
	       	       
	     // Dataview options
             p = f.getParam( pref + "_linecolor" );
             if( p!=null ) d.setColor(OFormat.getColor(p));
             p = f.getParam( pref + "_linewidth" );
             if( p!=null ) d.setLineWidth(OFormat.getInt(p.get(0).toString()));
             p = f.getParam( pref + "_linestyle" );
             if( p!=null ) d.setStyle(OFormat.getInt(p.get(0).toString()));
             p = f.getParam( pref + "_markercolor" );
             if( p!=null ) d.setMarkerColor(OFormat.getColor(p));
             p = f.getParam( pref + "_markersize" );
             if( p!=null ) d.setMarkerSize(OFormat.getInt(p.get(0).toString()));
             p = f.getParam( pref + "_markerstyle" );
             if( p!=null ) d.setMarker(OFormat.getInt(p.get(0).toString()));
             p = f.getParam( pref + "_A0" );
             if( p!=null ) d.setA0(OFormat.getDouble(p.get(0).toString()));
             p = f.getParam( pref + "_A1" );
             if( p!=null ) d.setA1(OFormat.getDouble(p.get(0).toString()));
             p = f.getParam( pref + "_A2" );
             if( p!=null ) d.setA2(OFormat.getDouble(p.get(0).toString()));	 	     
	   }
	 }
       }
       
       return errBuff;      
    } 
    
    // ****************************************************************    
    // Apply a block settings    
    // ****************************************************************    
    public String setSetting(String txt) {
    
       CfFileReader f = new CfFileReader();
       
       // Read and browse the file
       if( !f.parseText(txt) ) {
         return "Trend.setSettings: Failed to parse given text";
       }
       
       return applySettings(f);      
    }
    
    // ****************************************************************    
    // Save the whole graph setting    
    // ****************************************************************    
    public void saveSetting(String filename) {
      int i;
            
      try {        
	FileWriter f = new FileWriter(filename);
        String s = getSettings();	
	f.write(s,0,s.length());
	f.close();	
      } catch( Exception e ) {
        JOptionPane.showMessageDialog(parent,"Failed to write " + filename,"Error",JOptionPane.ERROR_MESSAGE);
      }
      
    }

    // ****************************************************************    
    // Load graph setting    
    // Retrun error string (zero length when succes)
    // ****************************************************************    
    public String loadSetting(String filename) {

       CfFileReader f = new CfFileReader();
       
       // Read and browse the file
       if( !f.readFile(filename) ) {
         return "Failed to read " + filename;
       }
       
       return applySettings(f);
    }
    
    
    
    public Dimension getPreferredSize() {      
      Dimension d = super.getPreferredSize();
      d.height = 0;  
      return d;  
    } 

    // ************************************************
    // Option fonction
    // ************************************************
    public void setLegendVisible(boolean b) {
    }

    public boolean isLegendVisible() {
	return false;
    }
    
    public void setSamplingRate(double rate) {	
    }

    public double getSamplingRate() {
      return 0.0;
    }

    public void setXAxisLength(int length) {
    }

    public int getXAxisLength() {
      return 0;
    }

    public void setLogarithmicScale(boolean logarithmic) {
    }

    public boolean isLogarithmicScale() {
      return false;
    }

    public void setListVisible(boolean b) {
    }

    public boolean isListVisible() {
      return false;
    }

    public void setShowingDeviceNames(boolean b) {
    }

    public boolean isShowingDeviceNames() {
      return false;
    }

    public void setShowingNames(boolean b) {
    }

    public boolean isShowingNames() {
      return false;    
    }

    public JLChart getChart() {
      return theGraph;
    }

    public void ok() {
	getRootPane().getParent().setVisible(false);
    }
    
    // End of variables declaration//GEN-END:variables

    public static void main (String[] args) throws Exception {

	final JFrame f = new JFrame();
	final Trend  t = new Trend();
	
	if( args.length>0 ) {
           String err = t.loadSetting(args[0]);
	   if( err.length()>0 ) {
              JOptionPane.showMessageDialog(null,err,"Errors reading " + f.getName(),JOptionPane.ERROR_MESSAGE);
	   }
	} else {
	  // Create an empty tree
	  fr.esrf.tangoatk.core.AttributeList lst =
	    new fr.esrf.tangoatk.core.AttributeList();
	  t.setModel(lst);	  
	}
	
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	f.setTitle("Trends");
	f.setContentPane(t);
	Image image = Toolkit.getDefaultToolkit().getImage(t.getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_icon.gif"));
	if(image!=null) f.setIconImage(image);	
	f.pack();
	f.setSize(640,480);
	f.show();
	
    } // end of main ()
    
}

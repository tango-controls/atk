/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/*
 * Trend.java Created on May 13, 2002, 4:28 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.IBooleanScalar;
import fr.esrf.tangoatk.core.IEntity;
import fr.esrf.tangoatk.core.IEntityFilter;
import fr.esrf.tangoatk.core.IRefresherListener;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.DeviceFinder;
import fr.esrf.tangoatk.widget.util.IControlee;
import fr.esrf.tangoatk.widget.util.chart.CfFileReader;
import fr.esrf.tangoatk.widget.util.chart.IJLChartActionListener;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLChartActionEvent;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;
import fr.esrf.tangoatk.widget.util.chart.OFormat;
import fr.esrf.tangoatk.widget.util.chart.SearchInfo;

/**
 * based on Trend
 * @author SOLEIL
 */
/** A class to monitor multiple boolean scalar attributes. */
public class BooleanTrend extends JPanel implements IControlee, ActionListener,
        IJLChartActionListener, IRefresherListener
{
    // Constant
    /** Not selected */
    public static final int                            SEL_NONE            = 0;
    /** Selected on Y1 */
    public static final int                            SEL_Y1              = 2;
    /** Selected on Y2 */
    public static final int                            SEL_Y2              = 3;
    /** Device name are displayed whithin chart label when needed */
    public final static int                            DEVICE_LABEL_AUTO   = 0;
    /** Device name are never displayed whithin chart label */
    public final static int                            DEVICE_LABEL_NEVER  = 1;
    /** Device name are always displayed whithin chart label */
    public final static int                            DEVICE_LABEL_ALWAYS = 2;
    // Default Color
    static final Color[]                               defaultColor        = {
            Color.red, Color.blue, Color.cyan, Color.green, Color.magenta,
            Color.orange, Color.pink, Color.yellow, Color.black           };
    // Local declaration
    protected JFrame                                   parent              = null;
    // Toolbar stuff
    protected JToolBar                                 theToolBar;
    protected JPopupMenu                               toolMenu;
    protected JButton                                  optionButton;
    protected JMenuItem                                optionMenuI;
    protected JButton                                  stopButton;
    protected JMenuItem                                stopMenuI;
    protected JButton                                  startButton;
    protected JMenuItem                                startMenuI;
    protected JButton                                  loadButton;
    protected JMenuItem                                loadMenuI;
    protected JButton                                  saveButton;
    protected JMenuItem                                saveMenuI;
    protected JButton                                  zoomButton;
    protected JMenuItem                                zoomMenuI;
    protected JButton                                  timeButton;
    protected JMenuItem                                timeMenuI;
    protected JButton                                  cfgButton;
    protected JMenuItem                                cfgMenuI;
    protected JButton                                  resetButton;
    protected JMenuItem                                resetMenuI;
    protected JMenuItem                                showtoolMenuI;
    protected JPanel                                   innerPanel;
    protected JLabel                                   dateLabel;
    // Selection tree stuff
    protected JScrollPane                              treeView            = null;
    protected JTree                                    mainTree            = null;
    protected DefaultTreeModel                         mainTreeModel       = null;
    protected BooleanTrendSelectionNode                rootNode            = null;
    protected JPopupMenu                               treeMenu;
    protected JMenuItem                                addY1MenuItem;
    protected JMenuItem                                addY2MenuItem;
    protected JMenuItem                                removeMenuItem;
    protected JMenuItem                                optionMenuItem;
    protected JMenuItem                                attOptionMenuItem;
    // Chart stuff
    protected BooleanChart                             theGraph;
    protected String                                   graphTitle          = "";
    protected BooleanConfigPanel                       cfgPanel            = null;
    static protected Point                             framePos            = new Point(
                                                                                   0,
                                                                                   0 );
    static protected Point                             frameDimension      = new Point(
                                                                                   640,
                                                                                   480 );
    // The models
    protected AttributePolledList                      attList             = null;
    protected BooleanTrendSelectionNode                lastAdded           = null;
    protected AttributePolledList                      lastCreatedList     = null;
    protected SimplePropertyFrame                      propFrame           = null;
    protected String                                   lastConfig          = "";
    protected boolean                                  singleDevice        = true;
    protected int                                      isShowingDeviceName = DEVICE_LABEL_AUTO;
    protected static final java.util.GregorianCalendar calendar            = new java.util.GregorianCalendar();
    protected static final java.text.SimpleDateFormat  genFormat           = new java.text.SimpleDateFormat(
                                                                                   "EEE dd/MM/yy HH:mm:ss" );
    protected Map                                      buttonMap;
    /**
     * Corresponds to the button "start monitoring"
     */
    public static final String                         start               = "START";
    /**
     * Corresponds to the button "stop monitoring"
     */
    public static final String                         stop                = "STOP";
    /**
     * Corresponds to the button "Save configuration"
     */
    public static final String                         save                = "SAVE";
    /**
     * Corresponds to the button "Load configuration"
     */
    public static final String                         load                = "LOAD";
    /**
     * Corresponds to the button "Zoom"
     */
    public static final String                         zoom                = "ZOOM";
    /**
     * Corresponds to the button "Set rfresh interval"
     */
    public static final String                         time                = "TIME";
    /**
     * Corresponds to the button "Global settings"
     */
    public static final String                         option              = "OPTION";
    /**
     * Corresponds to the button "Add new attribute"
     */
    public static final String                         config              = "CONFIG";
    /**
     * Corresponds to the button "Reset trend"
     */
    public static final String                         reset               = "RESET";
    protected int                                      timePrecision       = 0;
    protected final static String[]                    labels              = {"true", "false"};
    protected final static double[]                    labelPositions      = {1, 0};
    protected final static double[]                    defaultMinMax       = {-0.01, 1.1};

    protected final static IEntityFilter               booleanFilter       =  new IEntityFilter(){
        public boolean keep(IEntity entity) {
            if (entity instanceof IBooleanScalar) return true;
            return false;
        }
    };

    /**
     * Trend constructor.
     * 
     * @param parent
     *            Parent frame
     */
    public BooleanTrend (JFrame parent)
    {
        this();
        this.parent = parent;
    }

    /**
     * Default constructor.
     */
    public BooleanTrend ()
    {
        buttonMap = new HashMap();
        theToolBar = new JToolBar();
        toolMenu = new JPopupMenu();
        optionButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_settings.gif" ) ) );
        optionButton.setToolTipText( "Global settings" );
        optionMenuI = new JMenuItem( "Global settings" );
        stopButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_stop.gif" ) ) );
        stopButton.setToolTipText( "Stop monitoring" );
        stopMenuI = new JMenuItem( "Stop monitoring" );
        startButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_start.gif" ) ) );
        startButton.setToolTipText( "Start monitoring" );
        startMenuI = new JMenuItem( "Start monitoring" );
        loadButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_load.gif" ) ) );
        loadButton.setToolTipText( "Load configuration" );
        loadMenuI = new JMenuItem( "Load configuration" );
        saveButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_save.gif" ) ) );
        saveButton.setToolTipText( "Save configuration" );
        saveMenuI = new JMenuItem( "Save configuration" );
        zoomButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_zoom.gif" ) ) );
        zoomButton.setToolTipText( "Zoom" );
        zoomMenuI = new JMenuItem( "Zoom" );
        timeButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_time.gif" ) ) );
        timeButton.setToolTipText( "Set refresh interval" );
        timeMenuI = new JMenuItem( "Set refresh interval" );
        cfgButton = new JButton( new ImageIcon( getClass().getResource(
                "/fr/esrf/tangoatk/widget/attribute/trend_config.gif" ) ) );
        cfgButton.setToolTipText( "Add new attribute" );
        cfgMenuI = new JMenuItem( "Add new attribute" );
        resetButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_reset.gif")));
        resetButton.setToolTipText("Reset trend");
        resetMenuI = new JMenuItem("Reset trend");
        showtoolMenuI = new JMenuItem( "Hide toolbar" );
        theToolBar.setFloatable( true );
        loadButton.addActionListener( this );
        loadMenuI.addActionListener( this );
        saveButton.addActionListener( this );
        saveMenuI.addActionListener( this );
        optionButton.addActionListener( this );
        optionMenuI.addActionListener( this );
        zoomButton.addActionListener( this );
        zoomMenuI.addActionListener( this );
        stopButton.addActionListener( this );
        stopMenuI.addActionListener( this );
        startButton.addActionListener( this );
        startMenuI.addActionListener( this );
        timeButton.addActionListener( this );
        timeMenuI.addActionListener( this );
        cfgButton.addActionListener( this );
        cfgMenuI.addActionListener( this );
        resetButton.addActionListener(this);
        resetMenuI.addActionListener(this);
        showtoolMenuI.addActionListener( this );
        theToolBar.add( loadButton );
        theToolBar.add( saveButton );
        theToolBar.add( optionButton );
        theToolBar.add( zoomButton );
        theToolBar.add( startButton );
        theToolBar.add( stopButton );
        theToolBar.add( timeButton );
        theToolBar.add( cfgButton );
        theToolBar.add( resetButton );
        buttonMap.put( load, loadButton );
        buttonMap.put( save, saveButton );
        buttonMap.put( option, optionButton );
        buttonMap.put( zoom, zoomButton );
        buttonMap.put( start, startButton );
        buttonMap.put( stop, stopButton );
        buttonMap.put( time, timeButton );
        buttonMap.put( config, cfgButton );
        buttonMap.put( reset, resetButton );
        toolMenu.add( loadMenuI );
        toolMenu.add( saveMenuI );
        toolMenu.add( optionMenuI );
        toolMenu.add( zoomMenuI );
        toolMenu.add( startMenuI );
        toolMenu.add( stopMenuI );
        toolMenu.add( timeMenuI );
        toolMenu.add( cfgMenuI );
        toolMenu.add( resetMenuI );
        toolMenu.add( showtoolMenuI );
        // Create the graph
        theGraph = new BooleanChart();
        theGraph.setBorder( new javax.swing.border.EtchedBorder() );
        theGraph.setBackground( new java.awt.Color( 180, 180, 180 ) );
        theGraph.getY1Axis().setAutoScale( false );
        theGraph.getY1Axis().setMinimum( defaultMinMax[0] );
        theGraph.getY1Axis().setMaximum( defaultMinMax[1] );
        theGraph.getY1Axis().setLabels( labels, labelPositions );
        theGraph.getY2Axis().setAutoScale( false );
        theGraph.getY2Axis().setMinimum( defaultMinMax[0] );
        theGraph.getY2Axis().setMaximum( defaultMinMax[1] );
        theGraph.getY2Axis().setLabels( labels, labelPositions );
        theGraph.getXAxis().setAutoScale( true );
        theGraph.setDisplayDuration( 300000.0 ); // 5min
        theGraph.addUserAction( "chkShow toolbar" );
        theGraph.addUserAction( "chkShow selection tree" );
        theGraph.addUserAction( "chkShow date" );
        theGraph.addUserAction( "Load configuration" );
        theGraph.addUserAction( "Save configuration" );
        theGraph.addJLChartActionListener( this );
        // refuse displayDuration greater than 1 day, in order to limit memory
        // use
        theGraph.setMaxDisplayDuration( 24 * 60 * 60 * 1000 );
        innerPanel = new JPanel();
        innerPanel.setLayout( new BorderLayout() );
        setLayout( new BorderLayout() );
        add( theToolBar, BorderLayout.NORTH );
        innerPanel.add( theGraph, BorderLayout.CENTER );
        add( innerPanel, BorderLayout.CENTER );
        // Create the tree popup menu
        treeMenu = new JPopupMenu();
        addY1MenuItem = new JMenuItem( "Add to Y1" );
        addY2MenuItem = new JMenuItem( "Add to Y2" );
        removeMenuItem = new JMenuItem( "Remove" );
        optionMenuItem = new JMenuItem( "Graphic properties" );
        attOptionMenuItem = new JMenuItem( "Attribute properties" );
        treeMenu.add( addY1MenuItem );
        treeMenu.add( addY2MenuItem );
        treeMenu.add( removeMenuItem );
        treeMenu.add( optionMenuItem );
        treeMenu.add( attOptionMenuItem );
        addY1MenuItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
                TreePath[] selPaths = mainTree.getSelectionPaths();
                for (int i = 0; i < selPaths.length; i++) {
                    BooleanTrendSelectionNode selNode = 
                        (BooleanTrendSelectionNode) selPaths[i]
                        .getLastPathComponent();
                    if ( selNode.getModel() != null
                            && selNode.getSelected() != SEL_Y1 ) {
                        selNode.setSelected( SEL_Y1 );
                    }
                }
                mainTree.repaint();
                theGraph.repaint();
            }
        } );
        addY2MenuItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed (java.awt.event.ActionEvent evt)
            {
                TreePath[] selPaths = mainTree.getSelectionPaths();
                for (int i = 0; i < selPaths.length; i++) {
                    BooleanTrendSelectionNode selNode = 
                        (BooleanTrendSelectionNode) selPaths[i]
                        .getLastPathComponent();
                    if ( selNode.getModel() != null
                            && selNode.getSelected() != SEL_Y2 ) {
                        selNode.setSelected( SEL_Y2 );
                    }
                }
                mainTree.repaint();
                theGraph.repaint();
            }
        } );
        removeMenuItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed (java.awt.event.ActionEvent evt)
            {
                TreePath[] selPaths = mainTree.getSelectionPaths();
                for (int i = 0; i < selPaths.length; i++) {
                    BooleanTrendSelectionNode selNode = 
                        (BooleanTrendSelectionNode) selPaths[i]
                        .getLastPathComponent();
                    if ( selNode.getModel() != null ) {
                        selNode.setSelected( SEL_NONE );
                    }
                }
                mainTree.repaint();
                theGraph.repaint();
            }
        } );
        optionMenuItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed (java.awt.event.ActionEvent evt)
            {
                BooleanTrendSelectionNode selNode = (BooleanTrendSelectionNode) mainTree
                        .getSelectionPath().getLastPathComponent();
                IBooleanScalar m = selNode.getModel();
                if ( m != null )
                {
                    selNode.showOptions();
                }
            }
        } );
        attOptionMenuItem
                .addActionListener( new java.awt.event.ActionListener() {
                    public void actionPerformed (java.awt.event.ActionEvent evt)
                    {
                        BooleanTrendSelectionNode selNode = (BooleanTrendSelectionNode) mainTree
                                .getSelectionPath().getLastPathComponent();
                        IBooleanScalar m = selNode.getModel();
                        if ( m != null )
                        {
                            if ( propFrame == null ) propFrame = new SimplePropertyFrame();
                            propFrame.setModel( m );
                            propFrame.setVisible( true );
                        }
                    }
                } );
        calendar.setTimeInMillis( System.currentTimeMillis() );
        dateLabel = new JLabel();
        dateLabel.setText( genFormat.format( calendar.getTime() ) );
        dateLabel.setHorizontalAlignment( JLabel.CENTER );
        dateLabel.setVisible( false );
        add( dateLabel, BorderLayout.SOUTH );
    }

    void refreshNode (BooleanTrendSelectionNode n)
    {
        theGraph.repaint();
        mainTreeModel.nodeChanged( n );
    }

    // -------------------------------------------------------------
    // Action listener
    // -------------------------------------------------------------
    public void actionPerformed (ActionEvent evt)
    {
        Object o = evt.getSource();
        if ( o == optionButton || o == optionMenuI )
        {
            optionButtonActionPerformed();
        }
        else if ( o == stopButton || o == stopMenuI )
        {
            attList.stopRefresher();
        }
        else if ( o == startButton || o == startMenuI )
        {
            attList.startRefresher();
        }
        else if ( o == loadButton || o == loadMenuI )
        {
            loadButtonActionPerformed();
        }
        else if ( o == saveButton || o == saveMenuI )
        {
            saveButtonActionPerformed();
        }
        else if ( o == zoomButton || o == zoomMenuI )
        {
            if ( !theGraph.isZoomed() ) theGraph.enterZoom();
            else theGraph.exitZoom();
        }
        else if ( o == timeButton || o == timeMenuI )
        {
            setRefreshInterval();
        }
        else if ( o == cfgButton || o == cfgMenuI )
        {
            if ( cfgPanel == null )
            {
                Window w = ATKGraphicsUtils.getWindowForComponent( this );
                if ( w instanceof Frame ) cfgPanel = new BooleanConfigPanel(
                        (Frame) w, this );
                else if ( w instanceof Dialog ) cfgPanel = new BooleanConfigPanel(
                        (Dialog) w, this );
                else cfgPanel = new BooleanConfigPanel( (Frame) null, this );
            }
            cfgPanel.showPanel();
        }
        else if ( o == showtoolMenuI )
        {
            boolean b = isButtonBarVisible();
            b = !b;
            setButtonBarVisible( b );
        }
        else if (o == resetButton || o == resetMenuI) {
            resetTrend();
        }
    }

    // -------------------------------------------------------------
    // JLChart action listener
    // -------------------------------------------------------------
    public void actionPerformed (JLChartActionEvent evt)
    {
        if ( evt.getName().equals( "Show toolbar" ) )
        {
            setButtonBarVisible( evt.getState() );
        }
        else if ( evt.getName().equals( "Show selection tree" ) )
        {
            setSelectionTreeVisible( evt.getState() );
        }
        else if ( evt.getName().equals( "Show date" ) )
        {
            setDateVisible( evt.getState() );
        }
        else if ( evt.getName().equalsIgnoreCase( "Load configuration" ) )
        {
            loadButtonActionPerformed();
        }
        else if ( evt.getName().equalsIgnoreCase( "Save configuration" ) )
        {
            saveButtonActionPerformed();
        }
    }

    public boolean getActionState (JLChartActionEvent evt)
    {
        if ( evt.getName().equals( "Show toolbar" ) )
        {
            return isButtonBarVisible();
        }
        else if ( evt.getName().equals( "Show selection tree" ) )
        {
            return isSelectionTreeVisible();
        }
        else if ( evt.getName().equals( "Show date" ) )
        {
            return isDateVisible();
        }
        return false;
    }

    public int getTimePrecision ()
    {
        return timePrecision;
    }

    public void setTimePrecision (int timePrecision)
    {
        this.timePrecision = timePrecision;
        if ( theGraph != null ) theGraph.setTimePrecision( timePrecision );
    }

    // -------------------------------------------------------------
    // Refresher listener
    // -------------------------------------------------------------
    public void refreshStep ()
    {
        // All attribute has been read, we can repaint the graph
        if ( isDateVisible() )
        {
            calendar.setTimeInMillis( System.currentTimeMillis() );
            dateLabel.setText( genFormat.format( calendar.getTime() ) );
        }
        theGraph.repaint();
    }

    private void setRefreshInterval ()
    {
        int old_it = attList.getRefreshInterval();
        String i = JOptionPane.showInputDialog( this,
                "Enter refresh interval (ms)", new Integer( old_it ) );
        if ( i != null )
        {
            try
            {
                int it = Integer.parseInt( i );
                attList.setRefreshInterval( it );
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog( parent, "Invalid number !",
                        "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private void updateModel ()
    {
        singleDevice = true;
        if ( attList != null )
        {
            int i;
            // Check if we have a single device
            int sz = attList.size();
            if ( sz > 1 )
            {
                IBooleanScalar s = (IBooleanScalar) attList.get( 0 );
                String dName = s.getDevice().getName();
                for (i = 1; i < sz && singleDevice; i++)
                {
                    s = (IBooleanScalar) attList.get( i );
                    singleDevice = dName.equalsIgnoreCase( s.getDevice()
                            .getName() );
                }
            }
            // Update nodes
            Vector v = rootNode.getSelectableItems();
            for (i = 0; i < v.size(); i++)
                ( (BooleanTrendSelectionNode) v.get( i ) ).refreshNode();
        }
    }

    /**
     * Free any allocated resource and stop refreshing. The internal attribute
     * list is released.
     */
    public void clearModel ()
    {
        setModel( (AttributePolledList) null );
    }

    /**
     * This <code>setModel</code> which takes an AttributePolledList as a
     * parameter, will just add the boolean scalar attributes in the list to the
     * list viewer in the BooleanTrend. It will not add any of the attributes to
     * the BooleanTrend. The list used by the BooleanTrend will be a copy of the
     * given list. If you don't use the list anymore (the on given in
     * parameter), remember to clean it, because this is not the list the
     * BooleanTrand uses (it uses a copy of it)
     * 
     * @param list
     *            an <code>AttributePolledList</code> value
     */
    public void setModel (AttributePolledList list)
    {
        int i;

        // Free old allocated resource
        // ----------------------------------------------------
        theGraph.unselectAll();
        // Remove old listeners and clean former list
        if ( attList != null )
        {
            attList.stopRefresher();
            attList.removeRefresherListener( this );
            attList.clear();
        }
        if ( rootNode != null )
        {
            Vector dv = rootNode.getSelectableItems();
            BooleanTrendSelectionNode n;
            for (i = 0; i < dv.size(); i++)
            {
                n = (BooleanTrendSelectionNode) dv.get( i );
                n.clearModel();
            }
        }
        // Stop refresher on list created by the Trend and clean this list
        if ( lastCreatedList != null )
        {
            lastCreatedList.stopRefresher();
            lastCreatedList.clear();
            lastCreatedList = null;
        }
        // Create the selection tree
        // -------------------------------------------------------
        rootNode = new BooleanTrendSelectionNode( this );
        int j;
        if ( list != null ) {
            for (i = 0; i < list.size(); i++) {
                if ( list.get(i) instanceof IBooleanScalar ) {
                    j = i;
                    lastAdded = rootNode.addItem( this, (IBooleanScalar) list
                            .get( j ), defaultColor[j % defaultColor.length] );
                }
            }
        }
        BooleanTrendRenderer renderer = new BooleanTrendRenderer();
        mainTreeModel = new DefaultTreeModel( rootNode );
        mainTree = new JTree( mainTreeModel );
        mainTree.setCellRenderer( renderer );
        mainTree.setEditable( false );
        mainTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
        mainTree.setShowsRootHandles( true );
        mainTree.setRootVisible( true );
        mainTree.setBorder( BorderFactory.createLoweredBevelBorder() );
        treeView = new JScrollPane( mainTree );
        mainTree.addMouseListener( new MouseAdapter() {
            public void mousePressed (MouseEvent e)
            {
                revalidate();
                int selRow = mainTree.getRowForLocation( e.getX(), e.getY() );
                TreePath[] selPaths = mainTree.getSelectionPaths();
                if ( selRow != -1 )
                {
                    if ( e.getClickCount() == 1
                            && e.getButton() == MouseEvent.BUTTON3 )
                    {
                        if ( selPaths != null && selPaths.length > 0 )
                        {
                            if ( selPaths.length == 1 ) {
                                BooleanTrendSelectionNode selNode = (BooleanTrendSelectionNode) selPaths[0]
                                        .getLastPathComponent();
                                if ( selNode.getModel() != null ) {
                                    addY1MenuItem.setEnabled( selNode
                                            .getSelected() != SEL_Y1 );
                                    addY2MenuItem.setEnabled( selNode
                                            .getSelected() != SEL_Y2 );
                                    removeMenuItem.setEnabled( selNode
                                            .getSelected() != SEL_NONE );
                                    treeMenu
                                            .show( mainTree, e.getX(), e.getY() );
                                }
                                else if ( selNode == rootNode ) {
                                    if ( isButtonBarVisible() ) showtoolMenuI
                                            .setText( "Hide toolbar" );
                                    else showtoolMenuI.setText( "Show toolbar" );
                                    toolMenu
                                            .show( mainTree, e.getX(), e.getY() );
                                }
                            }
                            else {
                                BooleanTrendSelectionNode lastAttributeNode = null;
                                int attributeCount = 0;
                                boolean containsRootNode = false;
                                for (int i = 0; i < selPaths.length; i++) {
                                    BooleanTrendSelectionNode selNode = 
                                        (BooleanTrendSelectionNode) selPaths[i]
                                            .getLastPathComponent();
                                    if ( selNode.getModel() != null ) {
                                        attributeCount++;
                                        lastAttributeNode = selNode;
                                    }
                                    else if ( selNode == rootNode ) {
                                        containsRootNode = true;
                                    }
                                }
                                if ( attributeCount > 0 ) {
                                    if ( attributeCount == 1 ) {
                                        addY1MenuItem
                                                .setEnabled(
                                                        lastAttributeNode
                                                        .getSelected() != SEL_Y1
                                        );
                                        addY2MenuItem
                                                .setEnabled(
                                                        lastAttributeNode
                                                        .getSelected() != SEL_Y2
                                        );
                                        removeMenuItem
                                                .setEnabled(
                                                        lastAttributeNode
                                                        .getSelected() != SEL_NONE
                                        );
                                    }
                                    else {
                                        addY1MenuItem.setEnabled( true );
                                        addY2MenuItem.setEnabled( true );
                                        removeMenuItem.setEnabled( true );
                                    }
                                    treeMenu
                                            .show( mainTree, e.getX(), e.getY() );
                                    lastAttributeNode = null;
                                }
                                else if ( containsRootNode ) {
                                    if ( isButtonBarVisible() ) showtoolMenuI
                                            .setText( "Hide toolbar" );
                                    else showtoolMenuI.setText( "Show toolbar" );
                                    toolMenu
                                            .show( mainTree, e.getX(), e.getY() );
                                }
                            }
                        }
                    }
                }
            }
        } );
        innerPanel.add( treeView, BorderLayout.WEST );
        attList = list;
        if ( attList != null ) attList.addRefresherListener( this );
        updateModel();
    }

    /**
     * <code>addAttribute</code> will add the IBooleanScalar to the BooleanTrend.
     * Additional calls to addAttribute will add more IBooleanScalars to the
     * BooleanTrend.
     * 
     * @param name
     *            Attribute name
     */
    public void addAttribute (String name)
    {
        IBooleanScalar scalar;
        AttributePolledList alist;
        // Add the attribute in the list
        try
        {
            if ( attList == null )
            {
                alist = new AttributePolledList();
                alist.setFilter(booleanFilter);
                alist.add( name );
                setModel( alist );
                lastCreatedList = alist;
                alist.setRefreshInterval( 1000 );
                alist.startRefresher();
            }
            else
            {
                if ( attList.get( name ) == null )
                {
                    scalar = (IBooleanScalar) attList.add( name );
                    int i = attList.size();
                    lastAdded = rootNode.addItem( this, scalar, defaultColor[i
                            % defaultColor.length] );
                    mainTreeModel = new DefaultTreeModel( rootNode );
                    mainTree.setModel( mainTreeModel );
                }
            }
            TreePath np = new TreePath( lastAdded.getPath() );
            mainTree.setSelectionPath( np );
            mainTree.expandPath( np );
            mainTree.makeVisible( np );
        }
        catch (ConnectionException e)
        {
            ;
        }
        updateModel();
        theGraph.repaint();
        innerPanel.revalidate();
    }

    public void addAttribute (IBooleanScalar scalar)
    {
        AttributePolledList alist;
        // Add the attribute in the list
        if ( attList == null )
        {
            alist = new AttributePolledList();
            alist.setFilter(booleanFilter);
            alist.add( scalar );
            setModel( alist );
            lastCreatedList = alist;
            alist.setRefreshInterval( 1000 );
            alist.startRefresher();
        }
        else
        {
            if ( !attList.contains( scalar ) )
            {
                attList.add( scalar );
                int i = attList.size();
                lastAdded = rootNode.addItem( this, scalar, defaultColor[i
                        % defaultColor.length] );
                mainTreeModel = new DefaultTreeModel( rootNode );
                mainTree.setModel( mainTreeModel );
            }
        }
        TreePath np = new TreePath( lastAdded.getPath() );
        mainTree.setSelectionPath( np );
        mainTree.expandPath( np );
        mainTree.makeVisible( np );
        updateModel();// RG comment : I made this addition to correct the
        // Mantis bug 495
        theGraph.repaint();
        innerPanel.revalidate();
    }

    public void removeAttribute (IBooleanScalar scalar)
    {
        lastAdded = null;
        if ( attList.contains( scalar ) )
        {
            System.out.println( "Removing " + scalar.getName() );
            rootNode.delItem( scalar );
            attList.remove( scalar.getName() );
            mainTreeModel = new DefaultTreeModel( rootNode );
            mainTree.setModel( mainTreeModel );
            innerPanel.revalidate();
            updateModel();
        }
    }

    public AttributePolledList getModel ()
    {
        return attList;
    }

    private void optionButtonActionPerformed ()
    {
        theGraph.showOptionDialog();
    }

    /**
     * <code>getExtension</code> returns the extension of a given file, that
     * is the part after the last `.' in the filename.
     * 
     * @param f
     *            a <code>File</code> value
     * @return a <code>String</code> value
     */
    private String getExtension (File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf( '.' );
        if ( i > 0 && i < s.length() - 1 )
        {
            ext = s.substring( i + 1 ).toLowerCase();
        }
        return ext;
    }

    private void saveButtonActionPerformed ()
    {
        int ok = JOptionPane.YES_OPTION;
        JFileChooser chooser = new JFileChooser( "." );
        chooser.addChoosableFileFilter( new FileFilter() {
            public boolean accept (File f)
            {
                if ( f.isDirectory() )
                {
                    return true;
                }
                String extension = getExtension( f );
                if ( extension != null && extension.equals( "txt" ) ) return true;
                return false;
            }

            public String getDescription ()
            {
                return "text files ";
            }
        } );
        if ( lastConfig.length() > 0 ) chooser.setSelectedFile( new File(
                lastConfig ) );
        int returnVal = chooser.showSaveDialog( parent );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            if ( f != null )
            {
                if ( getExtension( f ) == null )
                {
                    f = new File( f.getAbsolutePath() + ".txt" );
                }
                if ( f.exists() ) ok = JOptionPane.showConfirmDialog( parent,
                        "Do you want to overwrite " + f.getName() + " ?",
                        "Confirm overwrite", JOptionPane.YES_NO_OPTION );
                if ( ok == JOptionPane.YES_OPTION )
                {
                    saveSetting( f.getAbsolutePath() );
                }
            }
        }
    }

    private void loadButtonActionPerformed ()
    {
        int ok = JOptionPane.YES_OPTION;
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter( new FileFilter() {
            public boolean accept (File f)
            {
                if ( f.isDirectory() )
                {
                    return true;
                }
                String extension = getExtension( f );
                if ( extension != null && extension.equals( "txt" ) ) return true;
                return false;
            }

            public String getDescription ()
            {
                return "text files ";
            }
        } );
        if ( lastConfig.length() > 0 ) chooser.setSelectedFile( new File(
                lastConfig ) );
        int returnVal = chooser.showOpenDialog( parent );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File f = chooser.getSelectedFile();
            if ( f != null )
            {
                if ( ok == JOptionPane.YES_OPTION )
                {
                    String err = loadSetting( f.getAbsolutePath() );
                    if ( err.length() > 0 )
                    {
                        JOptionPane.showMessageDialog( parent, err,
                                "Errors reading " + f.getName(),
                                JOptionPane.ERROR_MESSAGE );
                    }
                }
            }
        }
    }

    /**
     * returns the configuration as string.
     * 
     * @see #setSetting
     */
    public String getSettings ()
    {
        int i;
        String to_write = "";
        // General settings
        to_write += theGraph.getConfiguration();
        to_write += "toolbar_visible:" + isButtonBarVisible() + "\n";
        to_write += "tree_visible:" + isSelectionTreeVisible() + "\n";
        to_write += "date_visible:" + isDateVisible() + "\n";
        if ( graphTitle.length() > 0 ) to_write += "frame_title:'" + graphTitle
                + "'\n";
        to_write += "window_pos:" + getLocationOnScreen().x + ","
                + getLocationOnScreen().y + "\n";
        to_write += "window_size:" + getSize().width + "," + getSize().height
                + "\n";
        to_write += "show_device_name:" + isShowingDeviceNames() + "\n";
        if ( attList != null ) to_write += "refresh_time:"
                + attList.getRefreshInterval() + "\n";
        // Axis
        to_write += theGraph.getXAxis().getConfiguration( "x" );
        to_write += theGraph.getY1Axis().getConfiguration( "y1" );
        to_write += theGraph.getY2Axis().getConfiguration( "y2" );
        // dataViews
        if ( rootNode == null ) return to_write;
        Vector dv = rootNode.getSelectableItems();
        BooleanTrendSelectionNode n;
        to_write += "dv_number:" + dv.size() + "\n";
        for (i = 0; i < dv.size(); i++)
        {
            n = (BooleanTrendSelectionNode) dv.get( i );
            to_write += "dv" + i + "_name:\'" + n.getModelName() + "\'\n";
            to_write += "dv" + i + "_selected:" + n.getSelected() + "\n";
            to_write += n.getData().getConfiguration( "dv" + i );
        }
        return to_write;
    }

    private String applySettings (CfFileReader f)
    {
        String errBuff = "";
        Vector p;
        int i, nbDv;
        theGraph.setMaxDisplayDuration( Double.POSITIVE_INFINITY );
        // Reset display duration (to avoid history reading side FX)
        theGraph.setDisplayDuration( Double.POSITIVE_INFINITY );
        // Create a new Attribute List
        AttributePolledList alist = new AttributePolledList();
        alist.setFilter(booleanFilter);
        // Get all dataviews
        p = f.getParam( "dv_number" );
        if ( p != null )
        {
            try
            {
                nbDv = Integer.parseInt( p.get( 0 ).toString() );
            }
            catch (NumberFormatException e)
            {
                errBuff += "dv_number: invalid number\n";
                return errBuff;
            }
            // Build attribute list
            for (i = 0; i < nbDv; i++)
            {
                p = f.getParam( "dv" + i + "_name" );
                if ( p == null )
                {
                    errBuff += ( "Unable to find dv" + i + "_name param\n" );
                    return errBuff;
                }
                try
                {
                    alist.add( p.get( 0 ).toString() );
                }
                catch (Exception e)
                {
                    errBuff += ( e.getMessage() + "\n" );
                }
            }
            // We have the attList
            // Set the devicePropertyModel
            if ( nbDv > 0 )
            {
                if ( attList != null )
                {
                    innerPanel.remove( treeView );
                    treeView = null;
                    mainTree = null;
                }
                p = f.getParam( "refresh_time" );
                if ( p != null ) alist.setRefreshInterval( OFormat.getInt( p
                        .get( 0 ).toString() ) );
                else alist.setRefreshInterval( 1000 );
                alist.startRefresher();
                setModel( alist );
                lastCreatedList = alist;
            }
        }
        else
        {
            nbDv = 0;
        }
        innerPanel.revalidate();
        // Now we can set up the graph
        // General settings
        theGraph.applyConfiguration( f );
        theGraph.setMaxDisplayDuration( 24 * 60 * 60 * 1000 );
        p = f.getParam( "toolbar_visible" );
        if ( p != null ) setButtonBarVisible( OFormat.getBoolean( p.get( 0 )
                .toString() ) );
        p = f.getParam( "tree_visible" );
        if ( p != null ) setSelectionTreeVisible( OFormat.getBoolean( p.get( 0 )
                .toString() ) );
        p = f.getParam( "date_visible" );
        if ( p != null ) setDateVisible( OFormat.getBoolean( p.get( 0 )
                .toString() ) );
        p = f.getParam( "frame_title" );
        if ( p != null ) graphTitle = p.get( 0 ).toString();
        p = f.getParam( "window_pos" );
        if ( p != null ) framePos = OFormat.getPoint( p );
        p = f.getParam( "window_size" );
        if ( p != null ) frameDimension = OFormat.getPoint( p );
        p = f.getParam( "show_device_name" );
        if ( p != null ) setShowingDeviceNames( OFormat.getInt( p.get( 0 )
                .toString() ) );
        // Axis
        theGraph.getXAxis().applyConfiguration( "x", f );
        theGraph.getY1Axis().applyConfiguration( "y1", f );
        theGraph.getY2Axis().applyConfiguration( "y2", f );
        // Select signal and apply dataView options
        if ( rootNode == null ) return errBuff;
        Vector dv = rootNode.getSelectableItems();
        BooleanTrendSelectionNode n = null;
        for (i = 0; i < nbDv; i++)
        {
            String attName;
            String pref = "dv" + i;
            p = f.getParam( pref + "_name" );
            attName = p.get( 0 ).toString();
            p = f.getParam( pref + "_selected" );
            if ( p != null )
            {
                int s = OFormat.getInt( p.get( 0 ).toString() );
                // Find to node to select
                int j = 0;
                boolean found = false;
                while (!found && j < dv.size())
                {
                    n = (BooleanTrendSelectionNode) dv.get( j );
                    found = n.getModelName().equals( attName );
                    if ( !found ) j++;
                }
                if ( found )
                {
                    if ( s > 0 ) n.setSelected( s );
                    JLDataView d = n.getData();
                    // Dataview options
                    d.applyConfiguration( pref, f );
                }
            }
        }
        return errBuff;
    }

    /**
     * Apply a configuration.
     * 
     * @param txt
     *            Configuration text.
     * @return An error string or An empty string when succes
     * @see #getSettings
     */
    public String setSetting (String txt)
    {
        CfFileReader f = new CfFileReader();
        // Read and browse the file
        if ( !f.parseText( txt ) )
        {
            return "Trend.setSettings: Failed to parse given text";
        }
        return applySettings( f );
    }

    /**
     * Save settings.
     * 
     * @param filename
     *            file to be saved.
     */
    public void saveSetting (String filename)
    {
        try
        {
            FileWriter f = new FileWriter( filename );
            String s = getSettings();
            f.write( s, 0, s.length() );
            f.close();
            lastConfig = filename;
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog( parent, "Failed to write "
                    + filename, "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     * Load graph settings.
     * 
     * @param filename
     *            file to be read
     * @return An error string or An empty string when succes
     */
    public String loadSetting (String filename)
    {
        CfFileReader f = new CfFileReader();
        // Read and browse the file
        if ( !f.readFile( filename ) )
        {
            return "Failed to read " + filename;
        }
        lastConfig = filename;
        return applySettings( f );
    }

    /** Returns the frame_title field read in the config file. */
    public String getTitle ()
    {
        return graphTitle;
    }

    public Dimension getPreferredSize ()
    {
        Dimension d = super.getPreferredSize();
        d.height = 0;
        return d;
    }

    // ************************************************
    // Option fonction
    // ************************************************
    /**
     * @deprecated use getChart()
     */
    public void setLegendVisible (boolean b)
    {
    }

    /**
     * @deprecated use getChart()
     */
    public boolean isLegendVisible ()
    {
        return false;
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public void setSamplingRate (double rate)
    {
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public double getSamplingRate ()
    {
        return 0.0;
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public void setXAxisLength (int length)
    {
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public int getXAxisLength ()
    {
        return 0;
    }

    /**
     * @deprecated use getChart()
     */
    public void setLogarithmicScale (boolean logarithmic)
    {
    }

    /**
     * @deprecated use getChart()
     */
    public boolean isLogarithmicScale ()
    {
        return false;
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public void setListVisible (boolean b)
    {
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public boolean isListVisible ()
    {
        return false;
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public void setShowingNames (boolean b)
    {
    }

    /**
     * @deprecated no longer used (has no FX)
     */
    public boolean isShowingNames ()
    {
        return false;
    }

    /**
     * Determines whether or not device names (within chart labels) are visible.
     * 
     * @param mode
     *            Device name display mode
     * @see #DEVICE_LABEL_AUTO
     * @see #DEVICE_LABEL_NEVER
     * @see #DEVICE_LABEL_ALWAYS
     * @see #isShowingDeviceNames
     */
    public void setShowingDeviceNames (int mode)
    {
        isShowingDeviceName = mode;
    }

    /**
     * Determines whether or not device names (within chart labels) are visible.
     * 
     * @see #setShowingDeviceNames
     */
    public int isShowingDeviceNames ()
    {
        return isShowingDeviceName;
    }

    boolean displayDeviceNames ()
    {
        switch (isShowingDeviceName)
        {
            case DEVICE_LABEL_ALWAYS:
                return true;
            case DEVICE_LABEL_NEVER:
                return false;
        }
        return !singleDevice;
    }

    /**
     * Displays or hides the toolbar
     * 
     * @param b
     *            Visible flag
     */
    public void setButtonBarVisible (boolean b)
    {
        if ( theToolBar != null ) theToolBar.setVisible( b );
    }

    /**
     * Returns true only if toolbar is visible
     * 
     * @return Visible flag
     */
    public boolean isButtonBarVisible ()
    {
        if ( theToolBar != null ) return theToolBar.isVisible();
        else return false;
    }

    /**
     * Displays or hides the date label
     * 
     * @param b
     *            Visible flag
     */
    public void setDateVisible (boolean b)
    {
        if ( dateLabel != null ) dateLabel.setVisible( b );
    }

    /**
     * Returns true only if datelabel is visible
     * 
     * @return Visible flag
     */
    public boolean isDateVisible ()
    {
        if ( dateLabel != null ) return dateLabel.isVisible();
        else return false;
    }

    /**
     * Displays or hides the selection tree
     * 
     * @param b
     *            Visible flag
     */
    public void setSelectionTreeVisible (boolean b)
    {
        if ( treeView != null )
        {
            treeView.setVisible( b );
            revalidate();
        }
    }

    /**
     * Returns true only if the selection tree is visible
     */
    public boolean isSelectionTreeVisible ()
    {
        if ( treeView != null ) return treeView.isVisible();
        else return false;
    }

    /**
     * Returns a handle to the chart
     * 
     * @return Chart handle
     */
    public JLChart getChart ()
    {
        return theGraph;
    }

    public void ok ()
    {
        getRootPane().getParent().setVisible( false );
    }

    /**
     * Disables the button corresponding to the string
     */
    public void disableButton (String buttonName)
    {
        JButton b = (JButton) buttonMap.get( buttonName );
        if ( b != null ) b.setEnabled( false );
    }

    /**
     * Enables the button corresponding to the string
     */
    public void enableButton (String buttonName)
    {
        JButton b = (JButton) buttonMap.get( buttonName );
        if ( b != null ) b.setEnabled( true );
    }

    /**
     * Adds an attribute to an axis of this trend (or removes the attribute from
     * axis).
     * 
     * @param attributeName
     *            The name of the attribute
     * @param axisSelection
     *            The axis. Can be SEL_X (x axis), SEL_Y1 (y1 axis), SEL_Y2 (y2
     *            axis) or SEL_NONE (removes attribute from axis)
     * @param addToModel
     *            A boolean to say wheather the attribute has to be added in
     *            model or not. If <code>true</code> and the attribute is not
     *            in model, the attribute is added in the trend model.
     * @see #SEL_X
     * @see #SEL_Y1
     * @see #SEL_Y2
     * @see #SEL_NONE
     */
    public void addToAxis (String attributeName, int axisSelection,
            boolean addToModel)
    {
        if ( addToModel )
        {
            try
            {
                if ( getModel() == null )
                {
                    addAttribute( attributeName );
                }
                else if ( getModel().get( attributeName ) == null )
                {
                    addAttribute( attributeName );
                }
            }
            catch (ClassCastException e)
            {
                return;
            }
        }
        if ( rootNode == null ) return;
        int i = 0;
        Vector dv = rootNode.getSelectableItems();
        BooleanTrendSelectionNode attributeNode = null;
        boolean present = false;
        while (!present && i < dv.size())
        {
            attributeNode = (BooleanTrendSelectionNode) dv.get( i );
            present = ( ( attributeNode != null )
                    && ( attributeNode.getModel() != null ) && ( attributeNode
                    .getModel().getName().equals( attributeName ) ) );
            if ( !present ) i++;
        }
        if ( present )
        {
            switch (axisSelection)
            {
                case SEL_Y1:
                case SEL_Y2:
                case SEL_NONE:
                    attributeNode.setSelected( axisSelection );
                    mainTree.repaint();
                    theGraph.repaint();
                default:
                    return;
            }
        }
    }

    /**
     * Adds an attribute to an axis of this trend (or removes the attribute from
     * axis).
     * 
     * @param attribute
     *            The attribute
     * @param axisSelection
     *            The axis. Can be SEL_X (x axis), SEL_Y1 (y1 axis), SEL_Y2 (y2
     *            axis) or SEL_NONE (removes attribute from axis)
     * @param addToModel
     *            A boolean to say wheather the attribute has to be added in
     *            model or not. If <code>true</code> and the attribute is not
     *            in model, the attribute is added in the trend model.
     * @see #SEL_X
     * @see #SEL_Y1
     * @see #SEL_Y2
     * @see #SEL_NONE
     */
    public void addToAxis (IBooleanScalar attribute, int axisSelection,
            boolean addToModel)
    {
        if ( addToModel )
        {
            if ( getModel() == null )
            {
                addAttribute( attribute );
            }
            else if ( getModel().get( attribute.getName() ) == null )
            {
                addAttribute( attribute );
            }
        }
        if ( rootNode == null ) return;
        int i = 0;
        Vector dv = rootNode.getSelectableItems();
        BooleanTrendSelectionNode attributeNode = null;
        boolean present = false;
        while (!present && i < dv.size())
        {
            attributeNode = (BooleanTrendSelectionNode) dv.get( i );
            present = ( ( attributeNode != null )
                    && ( attributeNode.getModel() != null ) && ( attributeNode
                    .getModel().getName().equals( attribute.getName() ) ) );
            if ( !present ) i++;
        }
        if ( present )
        {
            switch (axisSelection)
            {
                case SEL_Y1:
                case SEL_Y2:
                case SEL_NONE:
                    attributeNode.setSelected( axisSelection );
                    mainTree.repaint();
                    theGraph.repaint();
                default:
                    return;
            }
        }
    }

    /**
     * Returns the axis associated with an attribute
     * 
     * @param attributeName
     *            The name of the attribute
     * @return The axis associated with the attribute the value can be:<br>
     *         <ul>
     *         <li>SEL_X (x axis)</li>
     *         <li>SEL_Y1 (y1 axis)</li>
     *         <li>SEL_Y2 (y2 axis)</li>
     *         <li>SEL_NONE (no axis, default value)</li>
     *         </ul>
     * @see #SEL_X
     * @see #SEL_Y1
     * @see #SEL_Y2
     * @see #SEL_NONE
     */
    public int getAxisForAttribute (String attributeName)
    {
        int selectedAxis = SEL_NONE;
        if ( rootNode == null ) return selectedAxis;
        int i = 0;
        Vector dv = rootNode.getSelectableItems();
        BooleanTrendSelectionNode attributeNode = null;
        boolean present = false;
        while (!present && i < dv.size())
        {
            attributeNode = (BooleanTrendSelectionNode) dv.get( i );
            present = ( ( attributeNode != null )
                    && ( attributeNode.getModel() != null ) && ( attributeNode
                    .getModel().getName().equals( attributeName ) ) );
            if ( !present ) i++;
        }
        if ( present )
        {
            selectedAxis = attributeNode.getSelected();
        }
        return selectedAxis;
    }

    /**
     * Returns the dataview associated with an attribute (null if no dataview is
     * associated with the attribute)
     * 
     * @param attributeName
     *            The name of the attribute
     * @return The dataview associated with the attribute (null if no dataview
     *         is associated with the attribute)
     */
    public JLDataView getDataViewForAttribute (String attributeName)
    {
        JLDataView selectedData = null;
        if ( rootNode == null ) return selectedData;
        int i = 0;
        Vector dv = rootNode.getSelectableItems();
        BooleanTrendSelectionNode attributeNode = null;
        boolean present = false;
        while (!present && i < dv.size())
        {
            attributeNode = (BooleanTrendSelectionNode) dv.get( i );
            present = ( ( attributeNode != null )
                    && ( attributeNode.getModel() != null ) && ( attributeNode
                    .getModel().getName().equals( attributeName ) ) );
            if ( !present ) i++;
        }
        if ( present )
        {
            selectedData = attributeNode.getData();
        }
        return selectedData;
    }

    public void setMaxDisplayDuration (double maxDisplay)
    {
        theGraph.setMaxDisplayDuration( maxDisplay );
    }

    public double getMaxDisplayDuration ()
    {
        return theGraph.getMaxDisplayDuration();
    }

    public void resetTrend() {

      if( rootNode!=null ) {
        Vector dv = rootNode.getSelectableItems();
        BooleanTrendSelectionNode n;
        for(int i = 0;i < dv.size(); i++) {
          n = (BooleanTrendSelectionNode)dv.get(i);
          if (n != null && n.getData() != null) {
            n.getData().reset();
          }
        }
        theGraph.repaint();
      }

    }

    public static void main (String[] args) throws Exception {
        final JFrame f = new JFrame();
        final BooleanTrend t = new BooleanTrend();
        String entityName = "jlp/test/1/att_boolean";
        if ( args.length > 0 ) {
            entityName = args[0];
        }
        try {
            t.addToAxis( entityName, BooleanTrend.SEL_Y1, true );
        }
        catch (Exception ex) {
            System.out.println( "Cannot connect to " + entityName );
            System.out.println( "Try to load " + entityName + " as a file" );
            try
            {
                t.loadSetting( entityName );
            }
            catch(Exception e)
            {
                System.out.println("Failed to load " + entityName + " as a file. Exiting...");
                System.exit( -1 );
            }
        }
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        if ( t.getTitle().length() > 0 ) f.setTitle( t.getTitle() );
        else f.setTitle( "BooleanTrends" );
        f.setContentPane( t );
        Image image = Toolkit.getDefaultToolkit().getImage(
                t.getClass().getResource(
                        "/fr/esrf/tangoatk/widget/attribute/trend_icon.gif" ) );
        if ( image != null ) f.setIconImage( image );
        f.pack();
        f.setBounds(
                framePos.x, framePos.y,
                frameDimension.x, frameDimension.y
        );
        f.setVisible( true );
    } // end of main ()
}

class BooleanConfigPanel extends JDialog implements ActionListener
{
    private JButton      addBtn;
    private JButton      closeBtn;
    private BooleanTrend trend;
    private DeviceFinder finder;

    BooleanConfigPanel (Frame parent, BooleanTrend trend)
    {
        super( parent, true );
        initComponents();
        this.trend = trend;
    }

    BooleanConfigPanel (Dialog parent, BooleanTrend trend)
    {
        super( parent, true );
        initComponents();
        this.trend = trend;
    }

    private void initComponents ()
    {
        setTitle( "Add new attribute" );
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BorderLayout() );
        finder = new DeviceFinder( DeviceFinder.MODE_ATTRIBUTE_BOOLEAN_SCALAR );
        innerPanel.add( finder, BorderLayout.CENTER );
        addBtn = new JButton( "Add selected attribute(s)" );
        addBtn.setFont( ATKConstant.labelFont );
        addBtn.addActionListener( this );
        closeBtn = new JButton( "Dismiss" );
        closeBtn.setFont( ATKConstant.labelFont );
        closeBtn.addActionListener( this );
        JPanel btnPanel = new JPanel( new FlowLayout() );
        btnPanel.add( addBtn );
        btnPanel.add( closeBtn );
        innerPanel.add( btnPanel, BorderLayout.SOUTH );
        setContentPane( innerPanel );
    }

    public void actionPerformed (ActionEvent e)
    {
        Object src = e.getSource();
        if ( src == addBtn )
        {
            String[] list = finder.getSelectedNames();
            for (int i = 0; i < list.length; i++)
                trend.addAttribute( list[i] );
        }
        else if ( src == closeBtn )
        {
            setVisible( false );
        }
    }

    void showPanel ()
    {
        ATKGraphicsUtils.centerDialog( this );
        setVisible( true );
    }
}

class BooleanChart extends JLChart
{
    public BooleanChart()
    {
        super();
    }

    protected String[] buildPanelString(SearchInfo si) {

      String[] str = null;

      str = new String[3];
      str[0] = si.dataView.getExtendedName() + " " + si.axis.getAxeName();
      str[1] = "Time= " + JLAxis.formatTimeValue(si.value.x);
      str[2] = "Value= " + (si.value.y == 1);

      return str;

    }

}

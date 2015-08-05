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
 

package fr.esrf.tangoatk.widget.util;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Line;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.Toolbar;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.InputEvent;

import fr.esrf.tangoatk.widget.util.chart.JLAxis;

public class JImageJ extends JPanel implements ActionListener {

    protected BufferedImage       theImage                  = null;
    protected Insets              margin;
    protected int                 xOrg;
    protected int                 yOrg;
    protected boolean             snapToGrid;
    protected int                 grid                      = 16;

    // Axis
    protected JLAxis              xAxis;
    protected int                 xAxisHeight;
    protected int                 xAxisUpMargin;
    protected JLAxis              yAxis;
    protected int                 yAxisWidth;
    protected int                 yAxisRightMargin;

    // Cursor
    protected boolean             crossCursor               = false;
    protected Color               cursorColor               = Color.WHITE;
    protected int                 xCursor                   = -1;
    protected int                 yCursor                   = -1;
    protected Color               roiColor                  = Color.RED;
    protected Color               roiSelectionColor         = Color.BLACK;
    protected Color               roiInsideColor            = Color.MAGENTA;
    protected Color               roiInsideSelectionColor   = 
        new Color(255, 100, 255);
    protected Color               roiOutsideColor           =
        new Color(0, 200, 0);
    protected Color               roiOutsideSelectionColor  = Color.GREEN;


    // ImageJ part
    protected AdvancedImagePlus   imp;
    protected RenderedImageCanvas canvas;
    protected ImageJ              hiddenIJ;
    protected CanvasRenderer      canvasRenderer;
    protected JScrollPane         imagePane;

    // Toolbar
    protected JPanel              toolbarPanel;
    protected ButtonGroup 		  buttonGroup;
    protected JToggleButton       rectangleButton;
    protected JToggleButton       ellipseButton;
    protected JToggleButton       freeHandButton;
    protected JToggleButton       polygonButton;
    protected JToggleButton       lineButton;
    protected JToggleButton       angleButton;
//    protected JToggleButton       pointButton;
    protected JToggleButton       wandButton;
//    protected JToggleButton       textButton;
    protected JToggleButton       zoomButton;
    protected JToggleButton       handButton;
    protected JToggleButton       arrowButton;
    protected JButton             clearButton;
    protected JButton             deleteButton;
    protected JToggleButton       selectedButton;
    protected JButton             innerButton;
    protected JButton             outerButton;
    protected JButton             maskButton;
    protected JButton             invalidateButton;
    protected JButton             resetButton;
    protected JButton             intersectionButton;
    protected JButton             unionButton;
    protected JButton             substractButton;
    protected JButton             xorButton;
    protected JButton             undoButton;
    protected JButton             redoButton;

    // Settings
    protected String              rectangleTitle            = "Rectangle ROI";
    protected String              ellipseTitle              = "Ellipse ROI";
    protected String              freeHandTitle             = "Free Draw ROI";
    protected String              polygonTitle              = "Polygon ROI";
    protected String              lineTitle                 = "Line";
    protected String              angleTitle                = "Angle";
    protected String              wandTitle                 = "Color Zone ROI";
    protected String              zoomTitle                 = "Zoom";
    protected String              handTitle                 = "Scroll Image";
    protected String              selectionTitle            = "Selection Mode";
    protected String              deleteTitle               = "Delete Selected ROI";
    protected String              deleteUndoRedoTitle       = "ROI deletion";
    protected String              clearTitle                = "Delete All ROIs";
    protected String              clearUndoRedoTitle        = "ROI deletion";
    protected String              innerTitle                = "Interior ROI";
    protected String              outerTitle                = "Exterior ROI";
    protected String              maskTitle                 = "Apply Mask";
    protected String              maskUndoRedoTitle         = "mask";
    protected String              invalidateTitle           = "Invalidate ROI";
    protected String              resetTitle                = "Reset Image";
    protected String              resetUndoRedoTitle        = "Reset Image";
    protected String              intersectionTitle         = "ROI intersection";
    protected String              intersectionUndoRedoTitle = "intersection";
    protected String              unionTitle                = "ROI union";
    protected String              unionUndoRedoTitle        = "union";
    protected String              substractTitle            = "ROI substraction";
    protected String              substractUndoRedoTitle    = "substraction";
    protected String              xorTitle                  = "ROI xor";
    protected String              xorUndoRedoTitle          = "xor";
    protected String              undoTitle                 = "Undo";
    protected String              redoTitle                 = "Redo";

    // Undo/redo management
    protected UndoManager         undoManager;
    protected final static int    undoLimit = 1;

    // Messages
    protected String              ignoreROITitle;
    protected String              ignoreROIMessage;
    protected String              ignoreROIContinueMessage;
    protected String              deleteAllROIsTitle;
    protected String              deleteAllROIsMessage;

    protected final static Insets NO_MARGIN = new Insets(0,0,0,0);


    public JImageJ () {
        super();
        undoManager = new UndoManager();
        undoManager.setLimit(undoLimit);
        initMessages();
        // used to initialise the right toolbar which can be connected to ImageCanvas
        hiddenIJ = new ImageJ() {
            @Override
            public void show () {
            }
            @Override
            public void setVisible (boolean b) {
                if(!b) {
                    super.setVisible(b);
                }
            }
        };
        imp = new AdvancedImagePlus();
        initCanvasRenderer();
        canvas = new RenderedImageCanvas(imp);
        canvas.removeMouseListener(canvas);
        canvas.removeMouseMotionListener(canvas);
        imagePane = new JScrollPane(canvasRenderer);
        // called this to create link between imp and canvas, and between toolbar and canvas
        new HiddenWindow(imp, canvas);
        setLayout( new GridBagLayout() );
        initToolbar();
        GridBagConstraints toolbarConstraints = new GridBagConstraints();
        toolbarConstraints.fill = GridBagConstraints.HORIZONTAL;
        toolbarConstraints.gridx = 0;
        toolbarConstraints.gridy = 0;
        toolbarConstraints.weightx = 1;
        toolbarConstraints.weighty = 0;
        add(toolbarPanel, toolbarConstraints);
        GridBagConstraints canvasConstraints = new GridBagConstraints();
        canvasConstraints.fill = GridBagConstraints.BOTH;
        canvasConstraints.gridx = 0;
        canvasConstraints.gridy = 1;
        canvasConstraints.weightx = 1;
        canvasConstraints.weighty = 1;
        add(imagePane, canvasConstraints);
        Toolbar.getInstance().setEnabled(true);
        setBorder(null);
        setBackground( new Color(180, 180, 200) );
        setOpaque(true);
        setMargin( new Insets(5, 5, 5, 5) );
        xOrg = 5;
        yOrg = 5;
        snapToGrid = false;
        yAxis = new JLAxis(canvasRenderer, JLAxis.VERTICAL_LEFT);
        yAxis.setAxisColor(Color.BLACK);
        yAxis.setFont(ATKConstant.labelFont);
        yAxis.setAutoScale(false);
        yAxis.setMinimum(0.0);
        yAxis.setMaximum(100.0);
        yAxis.setVisible(false);
        yAxis.setInverted(false);
        xAxis = new JLAxis(canvasRenderer, JLAxis.HORIZONTAL_UP);
        xAxis.setAxisColor(Color.BLACK);
        xAxis.setFont(ATKConstant.labelFont);
        xAxis.setAutoScale(false);
        xAxis.setMinimum(0.0);
        xAxis.setMaximum(100.0);
        xAxis.setVisible(false);
        Roi.setColor(roiColor);
    }

    /**
     * 
     */
    protected void initCanvasRenderer() {
        canvasRenderer = new CanvasRenderer();
    }

    protected void initToolbar() {
        toolbarPanel = new JPanel();
        toolbarPanel.setBackground( getBackground() );
        Dimension preferredSize = new Dimension(414, 18);
        toolbarPanel.setPreferredSize(preferredSize);
        toolbarPanel.setMinimumSize(preferredSize);
        preferredSize = null;
        toolbarPanel.setLayout(null);

        rectangleButton = new JToggleButton();
        rectangleButton.setMargin(NO_MARGIN);
        rectangleButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        rectangleButton.setToolTipText(rectangleTitle);
        rectangleButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/rectangle.gif"
                        )
                )
        );
        rectangleButton.addActionListener(this);
        rectangleButton.setBounds(0,0,18,18);
        rectangleButton.setBackground(Color.WHITE);
        
        
        toolbarPanel.add(rectangleButton);
        ellipseButton = new JToggleButton();
        ellipseButton.setMargin(NO_MARGIN);
        ellipseButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        ellipseButton.setToolTipText(ellipseTitle);
        ellipseButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/ellipse.gif"
                        )
                )
        );
        ellipseButton.addActionListener(this);
        ellipseButton.setBounds(18,0,18,18);
        ellipseButton.setBackground(Color.WHITE);
        toolbarPanel.add(ellipseButton);
        polygonButton = new JToggleButton();
        polygonButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        polygonButton.setMargin(NO_MARGIN);
        polygonButton.setToolTipText(polygonTitle);
        polygonButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/polygon.gif"
                        )
                )
        );
        polygonButton.addActionListener(this);
        polygonButton.setBounds(36,0,18,18);
        polygonButton.setBackground(Color.WHITE);
        toolbarPanel.add(polygonButton);
        freeHandButton = new JToggleButton();
        freeHandButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        freeHandButton.setMargin(NO_MARGIN);
        freeHandButton.setToolTipText(freeHandTitle);
        freeHandButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/freeHand.gif"
                        )
                )
        );
        freeHandButton.addActionListener(this);
        freeHandButton.setBounds(54,0,18,18);
        freeHandButton.setBackground(Color.WHITE);
        toolbarPanel.add(freeHandButton);
        lineButton = new JToggleButton();
        lineButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        lineButton.setMargin(NO_MARGIN);
        lineButton.setToolTipText(lineTitle);
        lineButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/line.gif"
                        )
                )
        );
        lineButton.addActionListener(this);
        lineButton.setBounds(72,0,18,18);
        lineButton.setBackground(Color.WHITE);
        toolbarPanel.add(lineButton);
        angleButton = new JToggleButton();
        angleButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        angleButton.setMargin(NO_MARGIN);
        angleButton.setToolTipText(angleTitle);
        angleButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/angle.gif"
                        )
                )
        );
        angleButton.addActionListener(this);
        angleButton.setBounds(90,0,18,18);
        angleButton.setBackground(Color.WHITE);
        toolbarPanel.add(angleButton);
        wandButton = new JToggleButton();
        wandButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        wandButton.setMargin(NO_MARGIN);
        wandButton.setToolTipText(wandTitle);
        wandButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/wand.gif"
                        )
                )
        );
        wandButton.addActionListener(this);
        wandButton.setBounds(108,0,18,18);
        wandButton.setBackground(Color.WHITE);
        toolbarPanel.add(wandButton);
        zoomButton = new JToggleButton();
        zoomButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        zoomButton.setMargin(NO_MARGIN);
        zoomButton.setToolTipText(zoomTitle);
        zoomButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/zoom.gif"
                        )
                )
        );
        zoomButton.addActionListener(this);
        zoomButton.setBounds(126,0,18,18);
        zoomButton.setBackground(Color.WHITE);
        toolbarPanel.add(zoomButton);
        handButton = new JToggleButton();
        handButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        handButton.setMargin(NO_MARGIN);
        handButton.setToolTipText(handTitle);
        handButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/hand.gif"
                        )
                )
        );
        handButton.addActionListener(this);
        handButton.setBounds(144,0,18,18);
        handButton.setBackground(Color.WHITE);
        toolbarPanel.add(handButton);
        arrowButton = new JToggleButton();
        arrowButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        arrowButton.setMargin(NO_MARGIN);
        arrowButton.setToolTipText(selectionTitle);
        arrowButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/arrow.gif"
                        )
                )
        );
        arrowButton.addActionListener(this);
        arrowButton.setBounds(162,0,18,18);
        arrowButton.setBackground(Color.WHITE);
        toolbarPanel.add(arrowButton);
        deleteButton = new JButton();
        deleteButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        deleteButton.setMargin(NO_MARGIN);
        deleteButton.setToolTipText(deleteTitle);
        deleteButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/delete.gif"
                        )
                )
        );
        deleteButton.addActionListener(this);
        deleteButton.setBounds(180,0,18,18);
        deleteButton.setBackground(Color.WHITE);
        toolbarPanel.add(deleteButton);
        clearButton = new JButton();
        clearButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        clearButton.setMargin(NO_MARGIN);
        clearButton.setToolTipText(clearTitle);
        clearButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/clear.gif"
                        )
                )
        );
        clearButton.addActionListener(this);
        clearButton.setBounds(198,0,18,18);
        clearButton.setBackground(Color.WHITE);
        toolbarPanel.add(clearButton);
        innerButton = new JButton();
        innerButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        innerButton.setMargin(NO_MARGIN);
        innerButton.setToolTipText(innerTitle);
        innerButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/interiorRoi.png"
                        )
                )
        );
        innerButton.addActionListener(this);
        innerButton.setBounds(216,0,18,18);
        innerButton.setBackground(Color.WHITE);
        toolbarPanel.add(innerButton);
        outerButton = new JButton();
        outerButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        outerButton.setMargin(NO_MARGIN);
        outerButton.setToolTipText(outerTitle);
        outerButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/exteriorRoi.png"
                        )
                )
        );
        outerButton.addActionListener(this);
        outerButton.setBounds(234,0,18,18);
        outerButton.setBackground(Color.WHITE);
        toolbarPanel.add(outerButton);
        maskButton = new JButton();
        maskButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        maskButton.setMargin(NO_MARGIN);
        maskButton.setToolTipText(maskTitle);
        maskButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/mask.gif"
                        )
                )
        );
        maskButton.addActionListener(this);
        maskButton.setBounds(252,0,18,18);
        maskButton.setBackground(Color.WHITE);
        toolbarPanel.add(maskButton);
        invalidateButton = new JButton();
        invalidateButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        invalidateButton.setMargin(NO_MARGIN);
        invalidateButton.setToolTipText(invalidateTitle);
        invalidateButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/invalidate.gif"
                        )
                )
        );
        invalidateButton.addActionListener(this);
        invalidateButton.setBounds(270,0,18,18);
        invalidateButton.setBackground(Color.WHITE);
        toolbarPanel.add(invalidateButton);
        resetButton = new JButton();
        resetButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        resetButton.setMargin(NO_MARGIN);
        resetButton.setToolTipText(resetTitle);
        resetButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/reset.gif"
                        )
                )
        );
        resetButton.addActionListener(this);
        resetButton.setBounds(288,0,18,18);
        resetButton.setBackground(Color.WHITE);
        toolbarPanel.add(resetButton);
        intersectionButton = new JButton();
        intersectionButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        intersectionButton.setMargin(NO_MARGIN);
        intersectionButton.setToolTipText(intersectionTitle);
        intersectionButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/intersection.gif"
                        )
                )
        );
        intersectionButton.addActionListener(this);
        intersectionButton.setBounds(306,0,18,18);
        intersectionButton.setBackground(Color.WHITE);
        toolbarPanel.add(intersectionButton);
        unionButton = new JButton();
        unionButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        unionButton.setMargin(NO_MARGIN);
        unionButton.setToolTipText(unionTitle);
        unionButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/union.gif"
                        )
                )
        );
        unionButton.addActionListener(this);
        unionButton.setBounds(324,0,18,18);
        unionButton.setBackground(Color.WHITE);
        toolbarPanel.add(unionButton);
        substractButton = new JButton();
        substractButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        substractButton.setMargin(NO_MARGIN);
        substractButton.setToolTipText(substractTitle);
        substractButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/substract.gif"
                        )
                )
        );
        substractButton.addActionListener(this);
        substractButton.setBounds(342,0,18,18);
        substractButton.setBackground(Color.WHITE);
        toolbarPanel.add(substractButton);
        xorButton = new JButton();
        xorButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        xorButton.setMargin(NO_MARGIN);
        xorButton.setToolTipText(xorTitle);
        xorButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/xor.gif"
                        )
                )
        );
        xorButton.addActionListener(this);
        xorButton.setBounds(360,0,18,18);
        xorButton.setBackground(Color.WHITE);
        toolbarPanel.add(xorButton);
        undoButton = new JButton();
        undoButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        undoButton.setMargin(NO_MARGIN);
        undoButton.setToolTipText(undoTitle);
        undoButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/undo.gif"
                        )
                )
        );
        undoButton.addActionListener(this);
        undoButton.setBounds(378,0,18,18);
        undoButton.setBackground(Color.WHITE);
        toolbarPanel.add(undoButton);
        redoButton = new JButton();
        redoButton.setBorder( new LineBorder(Color.LIGHT_GRAY,1) );
        redoButton.setMargin(NO_MARGIN);
        redoButton.setToolTipText(redoTitle);
        redoButton.setIcon(
                new ImageIcon(
                        JImage.class.getResource(
                                "/fr/esrf/tangoatk/widget/util/redo.gif"
                        )
                )
        );
        redoButton.addActionListener(this);
        redoButton.setBounds(396,0,18,18);
        redoButton.setBackground(Color.WHITE);
        toolbarPanel.add(redoButton);
        updateUndoRedoButtons();
        
        buttonGroup = new ButtonGroup();
		buttonGroup.add(rectangleButton);
		buttonGroup.add(ellipseButton);
		buttonGroup.add(polygonButton);
		buttonGroup.add(freeHandButton);
		buttonGroup.add(lineButton);
		buttonGroup.add(angleButton);
		buttonGroup.add(wandButton);
		buttonGroup.add(zoomButton);
		buttonGroup.add(handButton);
		buttonGroup.add(arrowButton);
        rectangleButton.doClick();
    }

    protected void updateUndoRedoButtons() {
        String presentation = undoManager.getUndoPresentationName();
        if ( presentation != null && ! "".equals( presentation.trim() ) ) {
            undoButton.setToolTipText(presentation);
        }
        undoButton.setEnabled( undoManager.canUndo() );
        presentation = undoManager.getRedoPresentationName();
        if ( presentation != null && ! "".equals( presentation.trim() ) ) {
            redoButton.setToolTipText(presentation);
        }
        redoButton.setEnabled( undoManager.canRedo() );
    }

    protected void initMessages() {
        ignoreROITitle = "Warning! ROI ignored";
        ignoreROIMessage = "The following ROI will be ignored:\n";
        ignoreROIContinueMessage = "Do you wish to continue ?";
        deleteAllROIsTitle = "Please confirm action";
        deleteAllROIsMessage = "Delete all ROIs ?";
    }

    /**
     * Sets the margin of the JImage
     * 
     * @param i
     *            Image margin
     */
    public void setMargin (Insets i) {
        margin = i;
    }

    /**
     * Returns margin of the image
     * 
     * @return Image margin
     */
    public Insets getMargin () {
        return margin;
    }

    public AdvancedImagePlus getImagePlus () {
        return imp;
    }

    protected void measureAxis () {
        xAxisHeight = 0;
        yAxisWidth = 0;
        xAxisUpMargin = xAxis.getThickness();
        yAxisRightMargin = xAxis.getFontOverWidth() - 1;
        Dimension imageSize = canvas.getPreferredSize();
        int imageWidth = imageSize.width;
        int imageHeight = imageSize.height;
        imageSize = null;
        if ( xAxis.isVisible() ) {
            xAxis.measureAxis(
                    ATKGraphicsUtils.getDefaultRenderContext(),
                    imageWidth,
                    0
            );
            xAxisHeight = xAxis.getThickness();
//            xAxisUpMargin += 7;
        }
        if ( yAxis.isVisible() ) {
            yAxis.measureAxis(
                    ATKGraphicsUtils.getDefaultRenderContext(),
                    0,
                    imageHeight
            );
            yAxisWidth = yAxis.getThickness();
//            yAxisRightMargin = 15;
        }
    }

    protected void paintAxis (Graphics g) {
        Dimension imageSize = canvas.getPreferredSize();
        int imageWidth = imageSize.width;
        int imageHeight = imageSize.height;
        imageSize = null;
        if ( yAxis.isVisible() ) {
            if ( yAxis.getOrientation() == JLAxis.VERTICAL_RIGHT ) {
                yAxis.paintAxisDirect(
                        g,
                        ATKGraphicsUtils.getDefaultRenderContext(),
                        imageWidth - yAxis.getThickness(),
                        0,
                        Color.BLACK,
                        0,
                        0
                );
                if ( yAxis.isDrawOpposite() ) {
                    yAxis.paintAxisOpposite(
                            g,
                            ATKGraphicsUtils.getDefaultRenderContext(),
                            -yAxis.getThickness(),
                            0,
                            Color.BLACK,
                            0,
                            0
                    );
                }
            }
            else {
                yAxis.paintAxisDirect(
                        g,
                        ATKGraphicsUtils.getDefaultRenderContext(),
                        -yAxis.getThickness(),
                        0,
                        Color.BLACK,
                        0,
                        0
                );
                if ( yAxis.isDrawOpposite() ) {
                    yAxis.paintAxisOpposite(
                            g,
                            ATKGraphicsUtils.getDefaultRenderContext(),
                            imageWidth - yAxis.getThickness(),
                            0,
                            Color.BLACK,
                            0,
                            0
                    );
                }
            }
        }
        if ( xAxis.isVisible() ) {
            if ( xAxis.getOrientation() == JLAxis.HORIZONTAL_UP ) {
                xAxis.paintAxisDirect(
                        g,
                        ATKGraphicsUtils.getDefaultRenderContext(),
                        0,
                        0,
                        Color.BLACK,
                        0,
                        0
                );
                if ( xAxis.isDrawOpposite() ) {
                    xAxis.paintAxisOpposite(
                            g,
                            ATKGraphicsUtils.getDefaultRenderContext(),
                            0,
                            imageHeight,
                            Color.BLACK,
                            0,
                            0
                    );
                }
            }
            else {
                xAxis.paintAxisDirect(
                        g,
                        ATKGraphicsUtils.getDefaultRenderContext(),
                        0,
                        imageHeight,
                        Color.BLACK,
                        0,
                        0
                );
                if ( xAxis.isDrawOpposite() ) {
                    xAxis.paintAxisOpposite(
                            g,
                            ATKGraphicsUtils.getDefaultRenderContext(),
                            0,
                            0,
                            Color.BLACK,
                            0,
                            0
                    );
                }
            }
        }
    }

    public Dimension getMinimumSize () {
        Dimension toolbarSize = toolbarPanel.getPreferredSize();
        int toolbarWidth = toolbarSize.width;
//        int toolbarHeight = toolbarSize.height;
//        toolbarSize = null;
//        if (theImage == null || canvas == null) {
//            return new Dimension(toolbarWidth, 200);
//        }
//        else {
//            measureAxis();
//            Dimension imageSize = canvas.getPreferredSize();
//            int imageWidth = imageSize.width;
//            int imageHeight = imageSize.height;
//            imageSize = null;
//            int usefullWidth = imageWidth + margin.right
//                                          + margin.left
//                                          + yAxisWidth
//                                          + yAxisRightMargin;
//            if (usefullWidth < toolbarWidth) {
//                usefullWidth = toolbarWidth;
//            }
//            return new Dimension(
//                    usefullWidth,
//                    imageHeight + margin.top
//                                + margin.bottom
//                                + xAxisHeight
//                                + xAxisUpMargin
//                                + toolbarHeight
//            );
//        }
        return new Dimension(toolbarWidth, 100);

    }

    public Dimension getPreferredSize () {
        return getMinimumSize();
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
            selectedButton = (JToggleButton)e.getSource();
            canvas.setSelectionMode(e.getSource() == arrowButton);
        }

        if (e.getSource() == rectangleButton) {
            Toolbar.getInstance().setTool(Toolbar.RECTANGLE);
            imp.setHandledRoi(null);
        }
        else if (e.getSource() == ellipseButton) {
            Toolbar.getInstance().setTool(Toolbar.OVAL);
        }
        else if (e.getSource() == polygonButton) {
            Toolbar.getInstance().setTool(Toolbar.POLYGON);
        }
        else if (e.getSource() == freeHandButton) {
            Toolbar.getInstance().setTool(Toolbar.FREEROI);
        }
        else if (e.getSource() == lineButton) {
            Toolbar.getInstance().setTool(Toolbar.LINE);
        }
        else if (e.getSource() == angleButton) {
            Toolbar.getInstance().setTool(Toolbar.ANGLE);
        }
//        else if (e.getSource() == pointButton) {
//            Toolbar.getInstance().setTool(Toolbar.POINT);
//        }
        else if (e.getSource() == wandButton) {
            Toolbar.getInstance().setTool(Toolbar.WAND);
        }
//        else if (e.getSource() == textButton) {
//            Toolbar.getInstance().setTool(Toolbar.TEXT);
//        }
        else if (e.getSource() == zoomButton) {
            Toolbar.getInstance().setTool(Toolbar.MAGNIFIER);
        }
        else if (e.getSource() == handButton) {
            Toolbar.getInstance().setTool(Toolbar.HAND);
        }
        else if (e.getSource() == arrowButton) {
            Toolbar.getInstance().setTool(Toolbar.RECTANGLE);
        }
        else if (e.getSource() == deleteButton) {
            deleteSelectedRois();
        }
        else if (e.getSource() == clearButton) {
            deleteAllROIs();
        }
        else if (e.getSource() == innerButton) {
            setSelectedRoisAsInner();
        }
        else if (e.getSource() == outerButton) {
            setSelectedRoisAsOuter();
        }
        else if (e.getSource() == maskButton) {
            applyMask();
        }
        else if (e.getSource() == invalidateButton) {
            invalidateROI();
        }
        else if (e.getSource() == resetButton) {
            resetMask();
        }
        else if (e.getSource() == intersectionButton) {
            intersectROIs();
        }
        else if (e.getSource() == unionButton) {
            unifyROIs();
        }
        else if (e.getSource() == substractButton) {
            substractROIs();
        }
        else if (e.getSource() == xorButton) {
            xorROIs();
        }
        else if (e.getSource() == undoButton) {
            undo();
        }
        else if (e.getSource() == redoButton) {
            redo();
        }
        canvasRenderer.grabFocus();
        repaint();
    }

    public Toolbar getToolbar() {
        return Toolbar.getInstance();
    }

    /**
     * Stes the image to be displayed
     * 
     * @param i Image
     */
    public void setImage (BufferedImage i) {
        if (theImage != i) {
            Image formerImage = theImage;
            theImage = i;
            imp.killRoi();
            imp.setWindow(null);
            if (theImage != null) {
                imp.setImage(theImage);
            }
            if (formerImage != null) {
                formerImage.flush();
                formerImage = null;
            }
            boolean selectionMode = false; 
            if(canvas != null)
            	selectionMode = canvas.isSelectionMode();
            canvas = new RenderedImageCanvas(imp);
            canvas.removeMouseListener(canvas);
            canvas.removeMouseMotionListener(canvas);
            canvas.setSelectionMode(selectionMode);
            // called this to create link between imp and canvas, and between
            // toolbar and canvas
            new HiddenWindow(imp, canvas);
            revalidate();
            repaint();
        }
    }

    public void refreshImage() {
        if (imp != null && theImage != null) {
            Image formerImage = imp.getImage();
            Roi formerRoi = imp.getRoi();
            imp.setImage(theImage);
            imp.setRoi(formerRoi);
            if (formerImage != null) {
                formerImage.flush();
                formerImage = null;
            }
        }
        repaint();
    }

    public void setZoom(double zoom) {
        if (canvas != null) {
            canvas.computeZoom(zoom);
            canvasRenderer.revalidate();
            revalidate();
            repaint();
        }
    }

    public void setZoomRoi(Roi zoomRoi) {
        if (canvas != null) {
            canvas.computeZoom(zoomRoi);
            imp.removeRoi(zoomRoi);
        }
    }

    public void setSelectionRoi(Roi selectionRoi) {
        imp.getSelectedRois().clear();
        imp.setValidatedRoi(null);
        if (selectionRoi != null) {
            imp.removeRoi(selectionRoi);
            Rectangle selectionBounds = selectionRoi.getBounds();
            for (int i = 0; i < imp.getAllRois().size(); i++) {
                Roi roi = imp.getAllRois().get(i);
                Rectangle roiBounds = roi.getBounds();
                if ( selectionBounds.contains(roiBounds) ) {
                    imp.getSelectedRois().add(roi);
                    if ( imp.getInnerRois().contains(roi)
                            || imp.getOuterRois().contains(roi) ) {
                        if (imp.getValidatedRoi() != null) {
                            imp.getSelectedRois().remove(
                                    imp.getValidatedRoi()
                            );
                        }
                        imp.setValidatedRoi(roi);
                    }
                }
            }
        }
    }

    public double getZoom() {
        if (canvas == null) {
            return 1;
        }
        else {
            return canvas.getMagnification();
        }
    }

    /**
     * Returns a handle of the X axis.
     */
    public JLAxis getXAxis () {
        return xAxis;
    }

    /**
     * Returns a handle of the Y axis.
     */
    public JLAxis getYAxis () {
        return yAxis;
    }

    /**
     * Returns a handle to the image displayed
     * 
     * @return Image handle
     */
    public BufferedImage getImage () {
        return theImage;
    }

    /**
     * Returns size of the image (does not include margin)
     *
     * @return Image size
     */
    public Dimension getImageSize () {
        if ( theImage != null ) {
            return new Dimension(
                    theImage.getWidth(),
                    theImage.getHeight()
            );
        }
        else {
            return new Dimension(0, 0);
        }
    }

    /**
     * Return origin of the image within the component
     * 
     * @return X origin in pixel
     */
    public int getXOrigin () {
        return xOrg + yAxis.getThickness();
    }

    /**
     * Return origin of the image within the component
     * 
     * @return Y origin in pixel
     */
    public int getYOrigin () {
        return yOrg + xAxisUpMargin;
    }

    /**
     * Returns the current line selected.
     * 
     * @return null when no selection or a 2 points array
     */
    public Point[] getSelectionPoint () {
        if ( imp != null && imp.getSelectedRois() != null ) {
            for (int i = 0; i < imp.getSelectedRois().size(); i++) {
                if (imp.getSelectedRois().get(i) instanceof Line) {
                    Line line = (Line)imp.getSelectedRois().get(i);
                    Point[] ret = new Point[2];
                    ret[0] = new Point(line.x1, line.y1);
                    ret[1] = new Point(line.x2, line.y2);
                    line = null;
                    return ret;
                }
            }
        }
        return null;
    }

    /**
     * Returns the current ROI bounds.
     * @return null when no ROI or its bounds
     */
    public Rectangle getCurrentRoiBounds() {
        if ( imp != null && imp.getRoi() != null ) {
            Roi roi = imp.getRoi();
            if ( roi != null ) {
                return roi.getBounds();
            }
        }
        return null;
    }

    /**
     * Clears the current selection.
     */
    public void deleteAllROIs() {
        if (imp != null) {
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    deleteAllROIsMessage,
                    deleteAllROIsTitle,
                    JOptionPane.YES_OPTION
            );
            if (ok == JOptionPane.YES_OPTION) {
                Vector<Roi> undoRoiVector = new Vector<Roi>();
                undoRoiVector.addAll( imp.getAllRois() );
                Vector<Roi> undoSelectedRois = new Vector<Roi>();
                undoSelectedRois.addAll( imp.getSelectedRois() );
                Vector<Roi> undoInnerRois = new Vector<Roi>();
                undoInnerRois.addAll( imp.getInnerRois() );
                Vector<Roi> undoOuterRois = new Vector<Roi>();
                undoOuterRois.addAll( imp.getOuterRois() );
                Roi undoValidatedRoi = imp.getValidatedRoi();
                Roi undoHandledRoi = imp.getRoi();
                imp.killRoi();
                imp.getAllRois().clear();
                imp.getInnerRois().clear();
                imp.getOuterRois().clear();
                imp.setValidatedRoi(null);
                undoManager.addEdit(
                        new RoiDefaultUndoableEdit(
                                imp,
                                undoRoiVector,
                                undoSelectedRois,
                                undoInnerRois,
                                undoOuterRois,
                                undoValidatedRoi,
                                undoHandledRoi,
                                clearTitle
                        )
                );
                undoRoiVector = null;
                undoSelectedRois = null;
                undoInnerRois = null;
                undoOuterRois = null;
                undoValidatedRoi = null;
                undoHandledRoi = null;
                updateUndoRedoButtons();
                revalidate();
                repaint();
            }
        }
    }

    public void deleteSelectedRois() {
        if (imp != null) {
            Vector<Roi> undoRoiVector = new Vector<Roi>();
            undoRoiVector.addAll( imp.getAllRois() );
            Vector<Roi> undoSelectedRois = new Vector<Roi>();
            undoSelectedRois.addAll( imp.getSelectedRois() );
            Vector<Roi> undoInnerRois = new Vector<Roi>();
            undoInnerRois.addAll( imp.getInnerRois() );
            Vector<Roi> undoOuterRois = new Vector<Roi>();
            undoOuterRois.addAll( imp.getOuterRois() );
            Roi undoValidatedRoi = imp.getValidatedRoi();
            Roi undoHandledRoi = imp.getRoi();
            imp.deleteSelectedRois();
            undoManager.addEdit(
                    new RoiDefaultUndoableEdit(
                            imp,
                            undoRoiVector,
                            undoSelectedRois,
                            undoInnerRois,
                            undoOuterRois,
                            undoValidatedRoi,
                            undoHandledRoi,
                            deleteUndoRedoTitle
                    )
            );
            undoRoiVector = null;
            undoSelectedRois = null;
            undoInnerRois = null;
            undoOuterRois = null;
            undoValidatedRoi = null;
            undoHandledRoi = null;
            updateUndoRedoButtons();
            revalidate();
            repaint();
        }
    }

    public void setSelectedRoisAsInner() {
        Vector<Roi> selectedRois = imp.getSelectedRois();
        Vector<Roi> innerRois = imp.getInnerRois();
        if (selectedRois != null && innerRois != null) {
            for (int i = 0; i < selectedRois.size(); i++) {
                if ( !innerRois.contains( selectedRois.get(i) ) ) {
                    innerRois.add( selectedRois.get(i) );
                    if ( selectedRois.get(i) == imp.getRoi() ) {
                        imp.setHandledRoi(null);
                    }
                    if (imp.getOuterRois() != null) {
                        imp.getOuterRois().remove( selectedRois.get(i) );
                    }
                    if (i == selectedRois.size() - 1) {
                        imp.setValidatedRoi( selectedRois.get(i) );
                    }
                }
            }
            selectedRois.clear();
            imp.getSelectedRois().add( imp.getValidatedRoi() );
        }
        repaint();
    }

    public void setSelectedRoisAsOuter() {
        Vector<Roi> selectedRois = imp.getSelectedRois();
        Vector<Roi> outerRois = imp.getOuterRois();
        if (selectedRois != null && outerRois != null) {
            for (int i = 0; i < selectedRois.size(); i++) {
                if ( !outerRois.contains( selectedRois.get(i) ) ) {
                    outerRois.add( selectedRois.get(i) );
                    if ( selectedRois.get(i) == imp.getRoi() ) {
                        imp.setHandledRoi(null);
                    }
                    if (imp.getInnerRois() != null) {
                        imp.getInnerRois().remove( selectedRois.get(i) );
                    }
                    if (i == selectedRois.size() - 1) {
                        imp.setValidatedRoi( selectedRois.get(i) );
                    }
                }
            }
            selectedRois.clear();
            imp.getSelectedRois().add( imp.getValidatedRoi() );
        }
        repaint();
    }

    public void applyMask() {
        if (imp.getValidatedRoi() != null && getImage() != null) {
            ImageProcessor undoProcessor = imp.getProcessor();
            Roi roi = imp.getValidatedRoi();
            ImageProcessor ip = new ColorProcessor( getImage() );
            imp.setHandledRoi(roi);
            ip.setRoi(roi);
            ip.setColor( Toolbar.getBackgroundColor() );
            Rectangle bounds = roi.getBounds();
            Dimension size = getImageSize();
            int roiX = Math.max(bounds.x, 0);
            if (size.width > 0 && roiX > size.width - 1) {
                roiX = size.width - 1;
            }
            int roiY = Math.max(bounds.y, 0);
            if (size.height > 0 && roiY > size.height - 1) {
                roiY = size.height - 1;
            }
            int roiWidth = Math.min(
                    size.width - roiX,
                    bounds.x + bounds.width - roiX
            );
            int roiHeight = Math.min(
                    size.height - roiY,
                    bounds.y + bounds.height - roiY
            );
            bounds.x = roiX;
            bounds.y = roiY;
            bounds.width = roiWidth;
            bounds.height = roiHeight;

            if ( imp.getOuterRois().contains(roi) ) {
                ip.fill( roi.getMask() );
            }
            else {
                ip.setRoi( 0, 0, bounds.x, imp.getHeight() );
                ip.fill();
                ip.setRoi( bounds.x, 0, bounds.width, bounds.y );
                ip.fill();
                ip.setRoi(
                        bounds.x+bounds.width,
                        0,
                        imp.getWidth() - bounds.x - bounds.width,
                        imp.getHeight()
                );
                ip.fill();
                ip.setRoi(
                        bounds.x,
                        bounds.y + bounds.height,
                        bounds.width,
                        imp.getHeight() - bounds.y - bounds.height
                );
                ip.fill();
                for (int x = 0; x < bounds.width; x++) {
                    for (int y = 0; y < bounds.height; y++) {
                        if ( !roi.contains(x+bounds.x, y+bounds.y) ) {
                            ip.drawPixel(x+bounds.x, y+bounds.y);
                        }
                    }
                }
            }
            size = null;
            ip.setColor( Toolbar.getForegroundColor() );
            imp.setProcessor(null, ip);
            undoManager.addEdit(
                    new MaskUndoableEdit(
                            imp,
                            undoProcessor,
                            maskUndoRedoTitle
                    ) 
            );
            undoProcessor = null;
            updateUndoRedoButtons();
            repaint();
            if (imp.getRoi() == roi) {
                imp.setHandledRoi(null);
            }
        }
    }

    public void resetMask() {
        if (imp != null && theImage != null) {
            ImageProcessor undoProcessor = imp.getProcessor();
            refreshImage();
            undoManager.addEdit(
                   new MaskUndoableEdit(
                           imp,
                           undoProcessor,
                           resetUndoRedoTitle
                   ) 
            );
            undoProcessor = null;
            updateUndoRedoButtons();
        }
    }

    public void invalidateROI() {
        Roi roi = imp.getValidatedRoi();
        if (roi != null) {
            imp.getInnerRois().remove(roi);
            imp.getOuterRois().remove(roi);
            imp.setValidatedRoi(null);
        }
        repaint();
    }

    /**
     * Sets the current selection.
     * @param _x1 Top left corner x coordinate
     * @param _y1 Top left corner y coordinate
     * @param _x2 Bottom right corner x coordinate
     * @param _y2 Bottom right corner y coordinate
     */
    public void setSelection(int _x1, int _y1, int _x2, int _y2) {
        if (imp != null) {
            imp.getAllRois().clear();
            Roi roi = new Roi(_x1, _y1, _x2-_x1, _y2-_y1);
            imp.setRoi(roi);
        }
    }

    public int transformX(int x) {
        if (canvas == null) {
            return x;
        }
        else {
            return canvas.offScreenX(x);
        }
    }

    public int transformY(int y) {
        if (canvas == null) {
            return y;
        }
        else {
            return canvas.offScreenY(y);
        }
    }

    /**
     * Checks out whether it is possible to combine ROIs
     * @return true if it is possible, false otherwise.
     */
    protected boolean canCombineROIs() {
        int ok = JOptionPane.NO_OPTION;
        int selectionCount = imp.getSelectedRois().size();
        Roi validatedRoi = imp.getValidatedRoi();
        Vector<Integer> indexVector = new Vector<Integer>();
        if (selectionCount > 1) {
            for (int i = 0; i < imp.getSelectedRois().size(); i++) {
                if (imp.getSelectedRois().get(i) == validatedRoi
                        || imp.getSelectedRois().get(i) instanceof Line) {
                    indexVector.add( new Integer(i+1) );
                    selectionCount--;
                }
            }
            if (indexVector.size() == 0) {
                ok = JOptionPane.YES_OPTION;
            }
            else if (selectionCount > 1) {
                StringBuffer messageBuffer = new StringBuffer(
                        ignoreROIMessage
                );
                messageBuffer.append( indexVector.get(0) );
                for (int i = 1; i < indexVector.size(); i++) {
                    messageBuffer.append(", ");
                    messageBuffer.append( indexVector.get(i) );
                }
                messageBuffer.append("\n");
                messageBuffer.append(ignoreROIContinueMessage);
                ok = JOptionPane.showConfirmDialog(
                        this,
                        messageBuffer.toString(),
                        ignoreROITitle,
                        JOptionPane.YES_NO_OPTION
                );
                messageBuffer = null;
            }
        }
        validatedRoi = null;
        return (ok == JOptionPane.YES_OPTION);
    }

    public void intersectROIs() {
        if ( canCombineROIs() ) {
            Vector<Roi> undoRoiVector = new Vector<Roi>();
            undoRoiVector.addAll( imp.getAllRois() );
            Vector<Roi> undoSelectedRois = new Vector<Roi>();
            undoSelectedRois.addAll( imp.getSelectedRois() );
            Vector<Roi> undoInnerRois = new Vector<Roi>();
            undoInnerRois.addAll( imp.getInnerRois() );
            Vector<Roi> undoOuterRois = new Vector<Roi>();
            undoOuterRois.addAll( imp.getOuterRois() );
            Roi undoValidatedRoi = imp.getValidatedRoi();
            Roi undoHandledRoi = imp.getRoi();
            Vector<Roi> ignoredRoi = new Vector<Roi>();
            for (int i = 0; i < imp.getSelectedRois().size(); i++) {
                if (imp.getSelectedRois().get(i) == imp.getValidatedRoi()
                        || imp.getSelectedRois().get(i) instanceof Line) {
                    ignoredRoi.add( imp.getSelectedRois().get(i) );
                }
            }
            if (ignoredRoi.size() > 0) {
                imp.getSelectedRois().removeAll(ignoredRoi);
            }
            Roi firstRoi = imp.getSelectedRois().get(0);
            ShapeRoi mainShape;
            if (firstRoi instanceof ShapeRoi) {
                mainShape = (ShapeRoi)firstRoi;
            }
            else {
                mainShape = new ShapeRoi(firstRoi);
            }
            firstRoi = null;
            for ( int i = 1; i < imp.getSelectedRois().size(); i++ ) {
                ShapeRoi shapeToCombine;
                Roi roiToCombine = imp.getSelectedRois().get(i);
                if (roiToCombine instanceof ShapeRoi) {
                    shapeToCombine = (ShapeRoi)roiToCombine;
                }
                else  {
                    shapeToCombine = new ShapeRoi(roiToCombine);
                }
                roiToCombine = null;
                mainShape = mainShape.and(shapeToCombine);
                shapeToCombine = null;
                if (mainShape == null) {
                    break;
                }
                Rectangle mainShapeBounds = mainShape.getBounds();
                if (mainShapeBounds.width == 0 || mainShapeBounds.height == 0) {
                    mainShape = null;
                    mainShapeBounds = null;
                    break;
                }
                mainShapeBounds = null;
            }
            imp.getAllRois().removeAll( imp.getSelectedRois() );
            imp.getSelectedRois().clear();
            imp.setRoi((Roi)null);
            imp.getSelectedRois().addAll(ignoredRoi);
            if (mainShape != null) {
                imp.getSelectedRois().add(mainShape);
                imp.getAllRois().add(mainShape);
                imp.setRoi(mainShape);
            }
            undoManager.addEdit(
                    new RoiDefaultUndoableEdit(
                            imp,
                            undoRoiVector,
                            undoSelectedRois,
                            undoInnerRois,
                            undoOuterRois,
                            undoValidatedRoi,
                            undoHandledRoi,
                            intersectionUndoRedoTitle
                    )
            );
            ignoredRoi.clear();
            ignoredRoi = null;
            undoRoiVector = null;
            undoSelectedRois = null;
            undoInnerRois = null;
            undoOuterRois = null;
            undoValidatedRoi = null;
            undoHandledRoi = null;
            updateUndoRedoButtons();
            canvasRenderer.repaint();
        }
    }

    public void unifyROIs() {
        if ( canCombineROIs() ) {
            Vector<Roi> undoRoiVector = new Vector<Roi>();
            undoRoiVector.addAll( imp.getAllRois() );
            Vector<Roi> undoSelectedRois = new Vector<Roi>();
            undoSelectedRois.addAll( imp.getSelectedRois() );
            Vector<Roi> undoInnerRois = new Vector<Roi>();
            undoInnerRois.addAll( imp.getInnerRois() );
            Vector<Roi> undoOuterRois = new Vector<Roi>();
            undoOuterRois.addAll( imp.getOuterRois() );
            Roi undoValidatedRoi = imp.getValidatedRoi();
            Roi undoHandledRoi = imp.getRoi();
            Vector<Roi> ignoredRoi = new Vector<Roi>();
            for (int i = 0; i < imp.getSelectedRois().size(); i++) {
                if (imp.getSelectedRois().get(i) == imp.getValidatedRoi()
                        || imp.getSelectedRois().get(i) instanceof Line) {
                    ignoredRoi.add( imp.getSelectedRois().get(i) );
                }
            }
            if (ignoredRoi.size() > 0) {
                imp.getSelectedRois().removeAll(ignoredRoi);
            }
            Roi firstRoi = imp.getSelectedRois().get(0);
            ShapeRoi mainShape;
            if (firstRoi instanceof ShapeRoi) {
                mainShape = (ShapeRoi)firstRoi;
            }
            else {
                mainShape = new ShapeRoi(firstRoi);
            }
            firstRoi = null;
            for ( int i = 1; i < imp.getSelectedRois().size(); i++ ) {
                ShapeRoi shapeToCombine;
                Roi roiToCombine = imp.getSelectedRois().get(i);
                if (roiToCombine instanceof ShapeRoi) {
                    shapeToCombine = (ShapeRoi)roiToCombine;
                }
                else  {
                    shapeToCombine = new ShapeRoi(roiToCombine);
                }
                roiToCombine = null;
                mainShape = mainShape.or(shapeToCombine);
                shapeToCombine = null;
                if (mainShape == null) {
                    break;
                }
                Rectangle mainShapeBounds = mainShape.getBounds();
                if (mainShapeBounds.width == 0 || mainShapeBounds.height == 0) {
                    mainShape = null;
                    mainShapeBounds = null;
                    break;
                }
                mainShapeBounds = null;
            }
            imp.getAllRois().removeAll( imp.getSelectedRois() );
            imp.getSelectedRois().clear();
            imp.setRoi((Roi)null);
            imp.getSelectedRois().addAll(ignoredRoi);
            if (mainShape != null) {
                imp.getSelectedRois().add(mainShape);
                imp.getAllRois().add(mainShape);
                imp.setRoi(mainShape);
            }
            undoManager.addEdit(
                    new RoiDefaultUndoableEdit(
                            imp,
                            undoRoiVector,
                            undoSelectedRois,
                            undoInnerRois,
                            undoOuterRois,
                            undoValidatedRoi,
                            undoHandledRoi,
                            unionUndoRedoTitle
                    )
            );
            ignoredRoi.clear();
            ignoredRoi = null;
            undoRoiVector = null;
            undoSelectedRois = null;
            undoInnerRois = null;
            undoOuterRois = null;
            undoValidatedRoi = null;
            undoHandledRoi = null;
            updateUndoRedoButtons();
            canvasRenderer.repaint();
        }
    }

    public void substractROIs() {
        if ( canCombineROIs() ) {
            Vector<Roi> undoRoiVector = new Vector<Roi>();
            undoRoiVector.addAll( imp.getAllRois() );
            Vector<Roi> undoSelectedRois = new Vector<Roi>();
            undoSelectedRois.addAll( imp.getSelectedRois() );
            Vector<Roi> undoInnerRois = new Vector<Roi>();
            undoInnerRois.addAll( imp.getInnerRois() );
            Vector<Roi> undoOuterRois = new Vector<Roi>();
            undoOuterRois.addAll( imp.getOuterRois() );
            Roi undoValidatedRoi = imp.getValidatedRoi();
            Roi undoHandledRoi = imp.getRoi();
            Vector<Roi> ignoredRoi = new Vector<Roi>();
            for (int i = 0; i < imp.getSelectedRois().size(); i++) {
                if (imp.getSelectedRois().get(i) == imp.getValidatedRoi()
                        || imp.getSelectedRois().get(i) instanceof Line) {
                    ignoredRoi.add( imp.getSelectedRois().get(i) );
                }
            }
            if (ignoredRoi.size() > 0) {
                imp.getSelectedRois().removeAll(ignoredRoi);
            }
            Roi firstRoi = imp.getSelectedRois().get(0);
            ShapeRoi mainShape;
            if (firstRoi instanceof ShapeRoi) {
                mainShape = (ShapeRoi)firstRoi;
            }
            else {
                mainShape = new ShapeRoi(firstRoi);
            }
            firstRoi = null;
            for ( int i = 1; i < imp.getSelectedRois().size(); i++ ) {
                ShapeRoi shapeToCombine;
                Roi roiToCombine = imp.getSelectedRois().get(i);
                if (roiToCombine instanceof ShapeRoi) {
                    shapeToCombine = (ShapeRoi)roiToCombine;
                }
                else  {
                    shapeToCombine = new ShapeRoi(roiToCombine);
                }
                roiToCombine = null;
                mainShape = mainShape.not(shapeToCombine);
                shapeToCombine = null;
                if (mainShape == null) {
                    break;
                }
                Rectangle mainShapeBounds = mainShape.getBounds();
                if (mainShapeBounds.width == 0 || mainShapeBounds.height == 0) {
                    mainShape = null;
                    mainShapeBounds = null;
                    break;
                }
                mainShapeBounds = null;
            }
            imp.getAllRois().removeAll( imp.getSelectedRois() );
            imp.getSelectedRois().clear();
            imp.setRoi((Roi)null);
            imp.getSelectedRois().addAll(ignoredRoi);
            if (mainShape != null) {
                imp.getSelectedRois().add(mainShape);
                imp.getAllRois().add(mainShape);
                imp.setRoi(mainShape);
            }
            undoManager.addEdit(
                    new RoiDefaultUndoableEdit(
                            imp,
                            undoRoiVector,
                            undoSelectedRois,
                            undoInnerRois,
                            undoOuterRois,
                            undoValidatedRoi,
                            undoHandledRoi,
                            substractUndoRedoTitle
                    )
            );
            ignoredRoi.clear();
            ignoredRoi = null;
            undoRoiVector = null;
            undoSelectedRois = null;
            undoInnerRois = null;
            undoOuterRois = null;
            undoValidatedRoi = null;
            undoHandledRoi = null;
            updateUndoRedoButtons();
            canvasRenderer.repaint();
        }
    }

    public void xorROIs() {
        if ( canCombineROIs() ) {
            Vector<Roi> undoRoiVector = new Vector<Roi>();
            undoRoiVector.addAll( imp.getAllRois() );
            Vector<Roi> undoSelectedRois = new Vector<Roi>();
            undoSelectedRois.addAll( imp.getSelectedRois() );
            Vector<Roi> undoInnerRois = new Vector<Roi>();
            undoInnerRois.addAll( imp.getInnerRois() );
            Vector<Roi> undoOuterRois = new Vector<Roi>();
            undoOuterRois.addAll( imp.getOuterRois() );
            Roi undoValidatedRoi = imp.getValidatedRoi();
            Roi undoHandledRoi = imp.getRoi();
            Vector<Roi> ignoredRoi = new Vector<Roi>();
            for (int i = 0; i < imp.getSelectedRois().size(); i++) {
                if (imp.getSelectedRois().get(i) == imp.getValidatedRoi()
                        || imp.getSelectedRois().get(i) instanceof Line) {
                    ignoredRoi.add( imp.getSelectedRois().get(i) );
                }
            }
            if (ignoredRoi.size() > 0) {
                imp.getSelectedRois().removeAll(ignoredRoi);
            }
            Roi firstRoi = imp.getSelectedRois().get(0);
            ShapeRoi mainShape;
            if (firstRoi instanceof ShapeRoi) {
                mainShape = (ShapeRoi)firstRoi;
            }
            else {
                mainShape = new ShapeRoi(firstRoi);
            }
            firstRoi = null;
            for ( int i = 1; i < imp.getSelectedRois().size(); i++ ) {
                ShapeRoi shapeToCombine;
                Roi roiToCombine = imp.getSelectedRois().get(i);
                if (roiToCombine instanceof ShapeRoi) {
                    shapeToCombine = (ShapeRoi)roiToCombine;
                }
                else  {
                    shapeToCombine = new ShapeRoi(roiToCombine);
                }
                roiToCombine = null;
                mainShape = mainShape.xor(shapeToCombine);
                shapeToCombine = null;
                if (mainShape == null) {
                    break;
                }
                Rectangle mainShapeBounds = mainShape.getBounds();
                if (mainShapeBounds.width == 0 || mainShapeBounds.height == 0) {
                    mainShape = null;
                    mainShapeBounds = null;
                    break;
                }
                mainShapeBounds = null;
            }
            imp.getAllRois().removeAll( imp.getSelectedRois() );
            imp.getSelectedRois().clear();
            imp.setRoi((Roi)null);
            imp.getSelectedRois().addAll(ignoredRoi);
            if (mainShape != null) {
                imp.getSelectedRois().add(mainShape);
                imp.getAllRois().add(mainShape);
                imp.setRoi(mainShape);
            }
            undoManager.addEdit(
                    new RoiDefaultUndoableEdit(
                            imp,
                            undoRoiVector,
                            undoSelectedRois,
                            undoInnerRois,
                            undoOuterRois,
                            undoValidatedRoi,
                            undoHandledRoi,
                            xorUndoRedoTitle
                    )
            );
            ignoredRoi.clear();
            ignoredRoi = null;
            undoRoiVector = null;
            undoSelectedRois = null;
            undoInnerRois = null;
            undoOuterRois = null;
            undoValidatedRoi = null;
            undoHandledRoi = null;
            updateUndoRedoButtons();
            canvasRenderer.repaint();
        }
    }

    public void undo() {
        if ( undoManager.canUndo() ) {
            try {
                undoManager.undo();
            }
            catch (CannotUndoException cue) {
                // nothing to do. Should never happen.
            }
        }
        updateUndoRedoButtons();
    }

    public void redo() {
        if ( undoManager.canRedo() ) {
            try {
                undoManager.redo();
            }
            catch (CannotRedoException cre) {
                // nothing to do. Should never happen.
            }
        }
        updateUndoRedoButtons();
    }

    public Color getRoiColor () {
        return roiColor;
    }

    public void setRoiColor (Color selectionColor) {
        this.roiColor = selectionColor;
        Roi.setColor(selectionColor);
    }

    /**
     * @return the roiSelectionColor
     */
    public Color getRoiSelectionColor () {
        return roiSelectionColor;
    }

    /**
     * @param roiSelectionColor the roiSelectionColor to set
     */
    public void setRoiSelectionColor (Color roiSelectionColor) {
        this.roiSelectionColor = roiSelectionColor;
    }

    /**
     * @return the roiInsideColor
     */
    public Color getRoiInsideColor () {
        return roiInsideColor;
    }

    /**
     * @param roiInsideColor the roiInsideColor to set
     */
    public void setRoiInsideColor (Color roiInsideColor) {
        this.roiInsideColor = roiInsideColor;
    }

    /**
     * @return the roiInsideSelectionColor
     */
    public Color getRoiInsideSelectionColor () {
        return roiInsideSelectionColor;
    }

    /**
     * @param roiInsideSelectionColor the roiInsideSelectionColor to set
     */
    public void setRoiInsideSelectionColor (Color roiInsideSelectionColor) {
        this.roiInsideSelectionColor = roiInsideSelectionColor;
    }

    /**
     * @return the roiOutsideColor
     */
    public Color getRoiOutsideColor () {
        return roiOutsideColor;
    }

    /**
     * @param roiOutsideColor the roiOutsideColor to set
     */
    public void setRoiOutsideColor (Color roiOutsideColor) {
        this.roiOutsideColor = roiOutsideColor;
    }

    /**
     * @return the roiOutsideSelectionColor
     */
    public Color getRoiOutsideSelectionColor () {
        return roiOutsideSelectionColor;
    }

    /**
     * @param roiOutsideSelectionColor the roiOutsideSelectionColor to set
     */
    public void setRoiOutsideSelectionColor (Color roiOutsideSelectionColor) {
        this.roiOutsideSelectionColor = roiOutsideSelectionColor;
    }

    /**
     * @return the image renderer
     */
    public JComponent getImageRenderer () {
        return canvasRenderer;
    }

    /**
     * @return the imagePane
     */
    public JScrollPane getImagePane () {
        return imagePane;
    }

    /**
     * @return the ignoreValidatedROITitle
     */
    public String getIgnoreROITitle () {
        return ignoreROITitle;
    }

    /**
     * @param ignoreROITitle the ignoreROITitle to set
     */
    public void setIgnoreROITitle (String ignoreROITitle) {
        this.ignoreROITitle = ignoreROITitle;
    }

    /**
     * @param ignoreValidatedROITitle the ignoreValidatedROITitle to set
     */
    public void setIgnoreValidatedROITitle (String ignoreValidatedROITitle) {
        this.ignoreROITitle = ignoreValidatedROITitle;
    }

    /**
     * @return the ignoreValidatedROIMessage
     */
    public String getIgnoreROIMessage () {
        return ignoreROIMessage;
    }

    /**
     * @param ignoreValidatedROIMessage the ignoreValidatedROIMessage to set
     */
    public void setIgnoreValidatedROIMessage (String ignoreValidatedROIMessage) {
        this.ignoreROIMessage = ignoreValidatedROIMessage;
    }

    /**
     * @return the ignoreValidatedROIContinueMessage
     */
    public String getIgnoreROIContinueMessage () {
        return ignoreROIContinueMessage;
    }

    /**
     * @param ignoreValidatedROIContinueMessage the ignoreValidatedROIContinueMessage to set
     */
    public void setIgnoreValidatedROIContinueMessage (
            String ignoreValidatedROIContinueMessage) {
        this.ignoreROIContinueMessage = ignoreValidatedROIContinueMessage;
    }

    /**
     * @return the deleteAllROIsTitle
     */
    public String getDeleteAllROIsTitle () {
        return deleteAllROIsTitle;
    }

    /**
     * @param deleteAllROIsTitle the deleteAllROIsTitle to set
     */
    public void setDeleteAllROIsTitle (String deleteAllROIsTitle) {
        this.deleteAllROIsTitle = deleteAllROIsTitle;
    }

    /**
     * @return the deleteAllROIsMessage
     */
    public String getDeleteAllROIsMessage () {
        return deleteAllROIsMessage;
    }

    /**
     * @param deleteAllROIsMessage the deleteAllROIsMessage to set
     */
    public void setDeleteAllROIsMessage (String deleteAllROIsMessage) {
        this.deleteAllROIsMessage = deleteAllROIsMessage;
    }

    /**
     * @return the toolbarPanel
     */
    public JPanel getToolbarPanel () {
        return toolbarPanel;
    }

    /**
     * @return the rectangleButton
     */
    public JToggleButton getRectangleButton () {
        return rectangleButton;
    }

    /**
     * @return the ellipseButton
     */
    public JToggleButton getEllipseButton () {
        return ellipseButton;
    }

    /**
     * @return the freeHandButton
     */
    public JToggleButton getFreeHandButton () {
        return freeHandButton;
    }

    /**
     * @return the polygonButton
     */
    public JToggleButton getPolygonButton () {
        return polygonButton;
    }

    /**
     * @return the lineButton
     */
    public JToggleButton getLineButton () {
        return lineButton;
    }

    /**
     * @return the angleButton
     */
    public JToggleButton getAngleButton () {
        return angleButton;
    }

    /**
     * @return the wandButton
     */
    public JToggleButton getWandButton () {
        return wandButton;
    }

    /**
     * @return the zoomButton
     */
    public JToggleButton getZoomButton () {
        return zoomButton;
    }

    /**
     * @return the arrowButton
     */
    public JToggleButton getArrowButton () {
        return arrowButton;
    }

    /**
     * @return the clearButton
     */
    public JButton getClearButton () {
        return clearButton;
    }

    /**
     * @return the deleteButton
     */
    public JButton getDeleteButton () {
        return deleteButton;
    }

    /**
     * @return the selectedButton
     */
    public JToggleButton getSelectedButton () {
        return selectedButton;
    }

    /**
     * @return the innerButton
     */
    public JButton getInnerButton () {
        return innerButton;
    }

    /**
     * @return the outerButton
     */
    public JButton getOuterButton () {
        return outerButton;
    }

    /**
     * @return the maskButton
     */
    public JButton getMaskButton () {
        return maskButton;
    }

    /**
     * @return the invalidateButton
     */
    public JButton getInvalidateButton () {
        return invalidateButton;
    }

    /**
     * @return the resetButton
     */
    public JButton getResetButton () {
        return resetButton;
    }

    /**
     * @return the intersectionButton
     */
    public JButton getIntersectionButton () {
        return intersectionButton;
    }

    /**
     * @return the unionButton
     */
    public JButton getUnionButton () {
        return unionButton;
    }

    /**
     * @return the substractButton
     */
    public JButton getSubstractButton () {
        return substractButton;
    }

    /**
     * @return the xorButton
     */
    public JButton getXorButton () {
        return xorButton;
    }

    /**
     * @return the rectangleTitle
     */
    public String getRectangleTitle () {
        return rectangleTitle;
    }

    /**
     * @param rectangleTitle the rectangleTitle to set
     */
    public void setRectangleTitle (String rectangleTitle) {
        this.rectangleTitle = rectangleTitle;
        rectangleButton.setToolTipText(rectangleTitle);
    }

    /**
     * @return the ellipseTitle
     */
    public String getEllipseTitle () {
        return ellipseTitle;
    }

    /**
     * @param ellipseTitle the ellipseTitle to set
     */
    public void setEllipseTitle (String ellipseTitle) {
        this.ellipseTitle = ellipseTitle;
        ellipseButton.setToolTipText(ellipseTitle);
    }

    /**
     * @return the freeHandTitle
     */
    public String getFreeHandTitle () {
        return freeHandTitle;
    }

    /**
     * @param freeHandTitle the freeHandTitle to set
     */
    public void setFreeHandTitle (String freeHandTitle) {
        this.freeHandTitle = freeHandTitle;
        freeHandButton.setToolTipText(freeHandTitle);
    }

    /**
     * @return the polygonTitle
     */
    public String getPolygonTitle () {
        return polygonTitle;
    }

    /**
     * @param polygonTitle the polygonTitle to set
     */
    public void setPolygonTitle (String polygonTitle) {
        this.polygonTitle = polygonTitle;
        polygonButton.setToolTipText(polygonTitle);
    }

    /**
     * @return the lineTitle
     */
    public String getLineTitle () {
        return lineTitle;
    }

    /**
     * @param lineTitle the lineTitle to set
     */
    public void setLineTitle (String lineTitle) {
        this.lineTitle = lineTitle;
        lineButton.setToolTipText(lineTitle);
    }

    /**
     * @return the angleTitle
     */
    public String getAngleTitle () {
        return angleTitle;
    }

    /**
     * @param angleTitle the angleTitle to set
     */
    public void setAngleTitle (String angleTitle) {
        this.angleTitle = angleTitle;
        angleButton.setToolTipText(angleTitle);
    }

    /**
     * @return the wandTitle
     */
    public String getWandTitle () {
        return wandTitle;
    }

    /**
     * @param wandTitle the wandTitle to set
     */
    public void setWandTitle (String wandTitle) {
        this.wandTitle = wandTitle;
        wandButton.setToolTipText(wandTitle);
    }

    /**
     * @return the zoomTitle
     */
    public String getZoomTitle () {
        return zoomTitle;
    }

    /**
     * @param zoomTitle the zoomTitle to set
     */
    public void setZoomTitle (String zoomTitle) {
        this.zoomTitle = zoomTitle;
        zoomButton.setToolTipText(zoomTitle);
    }

    /**
     * @return the selectionTitle
     */
    public String getSelectionTitle () {
        return selectionTitle;
    }

    /**
     * @param selectionTitle the selectionTitle to set
     */
    public void setSelectionTitle (String selectionTitle) {
        this.selectionTitle = selectionTitle;
        selectedButton.setToolTipText(selectionTitle);
    }

    /**
     * @return the deleteTitle
     */
    public String getDeleteTitle () {
        return deleteTitle;
    }

    /**
     * @param deleteTitle the deleteTitle to set
     */
    public void setDeleteTitle (String deleteTitle) {
        this.deleteTitle = deleteTitle;
        deleteButton.setToolTipText(deleteTitle);
    }

    /**
     * @return the deleteUndoRedoTitle
     */
    public String getDeleteUndoRedoTitle () {
        return deleteUndoRedoTitle;
    }

    /**
     * @param deleteUndoRedoTitle the deleteUndoRedoTitle to set
     */
    public void setDeleteUndoRedoTitle (String deleteUndoRedoTitle) {
        this.deleteUndoRedoTitle = deleteUndoRedoTitle;
    }

    /**
     * @return the clearTitle
     */
    public String getClearTitle () {
        return clearTitle;
    }

    /**
     * @param clearTitle the clearTitle to set
     */
    public void setClearTitle (String clearTitle) {
        this.clearTitle = clearTitle;
        clearButton.setToolTipText(clearTitle);
    }

    /**
     * @return the clearUndoRedoTitle
     */
    public String getClearUndoRedoTitle () {
        return clearUndoRedoTitle;
    }

    /**
     * @param clearUndoRedoTitle the clearUndoRedoTitle to set
     */
    public void setClearUndoRedoTitle (String clearUndoRedoTitle) {
        this.clearUndoRedoTitle = clearUndoRedoTitle;
    }

    /**
     * @return the innerTitle
     */
    public String getInnerTitle () {
        return innerTitle;
    }

    /**
     * @param innerTitle the innerTitle to set
     */
    public void setInnerTitle (String innerTitle) {
        this.innerTitle = innerTitle;
        innerButton.setToolTipText(innerTitle);
    }

    /**
     * @return the outerTitle
     */
    public String getOuterTitle () {
        return outerTitle;
    }

    /**
     * @param outerTitle the outerTitle to set
     */
    public void setOuterTitle (String outerTitle) {
        this.outerTitle = outerTitle;
        outerButton.setToolTipText(outerTitle);
    }

    /**
     * @return the maskTitle
     */
    public String getMaskTitle () {
        return maskTitle;
    }

    /**
     * @param maskTitle the maskTitle to set
     */
    public void setMaskTitle (String maskTitle) {
        this.maskTitle = maskTitle;
        maskButton.setToolTipText(maskTitle);
    }

    /**
     * @return the maskUndoRedoTitle
     */
    public String getMaskUndoRedoTitle () {
        return maskUndoRedoTitle;
    }

    /**
     * @param maskUndoRedoTitle the maskUndoRedoTitle to set
     */
    public void setMaskUndoRedoTitle (String maskUndoRedoTitle) {
        this.maskUndoRedoTitle = maskUndoRedoTitle;
    }

    /**
     * @return the invalidateTitle
     */
    public String getInvalidateTitle () {
        return invalidateTitle;
    }

    /**
     * @param invalidateTitle the invalidateTitle to set
     */
    public void setInvalidateTitle (String invalidateTitle) {
        this.invalidateTitle = invalidateTitle;
        invalidateButton.setToolTipText(invalidateTitle);
    }

    /**
     * @return the resetTitle
     */
    public String getResetTitle () {
        return resetTitle;
    }

    /**
     * @param resetTitle the resetTitle to set
     */
    public void setResetTitle (String resetTitle) {
        this.resetTitle = resetTitle;
        resetButton.setToolTipText(resetTitle);
    }

    /**
     * @return the resetUndoRedoTitle
     */
    public String getResetUndoRedoTitle () {
        return resetUndoRedoTitle;
    }

    /**
     * @param resetUndoRedoTitle the resetUndoRedoTitle to set
     */
    public void setResetUndoRedoTitle (String resetUndoRedoTitle) {
        this.resetUndoRedoTitle = resetUndoRedoTitle;
    }

    /**
     * @return the intersectionTitle
     */
    public String getIntersectionTitle () {
        return intersectionTitle;
    }

    /**
     * @param intersectionTitle the intersectionTitle to set
     */
    public void setIntersectionTitle (String intersectionTitle) {
        this.intersectionTitle = intersectionTitle;
        intersectionButton.setToolTipText(intersectionTitle);
    }

    /**
     * @return the intersectionUndoRedoTitle
     */
    public String getIntersectionUndoRedoTitle () {
        return intersectionUndoRedoTitle;
    }

    /**
     * @param intersectionUndoRedoTitle the intersectionUndoRedoTitle to set
     */
    public void setIntersectionUndoRedoTitle (String intersectionUndoRedoTitle) {
        this.intersectionUndoRedoTitle = intersectionUndoRedoTitle;
    }

    /**
     * @return the unionTitle
     */
    public String getUnionTitle () {
        return unionTitle;
    }

    /**
     * @param unionTitle the unionTitle to set
     */
    public void setUnionTitle (String unionTitle) {
        this.unionTitle = unionTitle;
        unionButton.setToolTipText(unionTitle);
    }

    /**
     * @return the unionUndoRedoTitle
     */
    public String getUnionUndoRedoTitle () {
        return unionUndoRedoTitle;
    }

    /**
     * @param unionUndoRedoTitle the unionUndoRedoTitle to set
     */
    public void setUnionUndoRedoTitle (String unionUndoRedoTitle) {
        this.unionUndoRedoTitle = unionUndoRedoTitle;
    }

    /**
     * @return the substractTitle
     */
    public String getSubstractTitle () {
        return substractTitle;
    }

    /**
     * @param substractTitle the substractTitle to set
     */
    public void setSubstractTitle (String substractTitle) {
        this.substractTitle = substractTitle;
        substractButton.setToolTipText(substractTitle);
    }

    /**
     * @return the substractUndoRedoTitle
     */
    public String getSubstractUndoRedoTitle () {
        return substractUndoRedoTitle;
    }

    /**
     * @param substractUndoRedoTitle the substractUndoRedoTitle to set
     */
    public void setSubstractUndoRedoTitle (String substractUndoRedoTitle) {
        this.substractUndoRedoTitle = substractUndoRedoTitle;
    }

    /**
     * @return the xorTitle
     */
    public String getXorTitle () {
        return xorTitle;
    }

    /**
     * @param xorTitle the xorTitle to set
     */
    public void setXorTitle (String xorTitle) {
        this.xorTitle = xorTitle;
        xorButton.setToolTipText(xorTitle);
    }

    /**
     * @return the xorUndoRedoTitle
     */
    public String getXorUndoRedoTitle () {
        return xorUndoRedoTitle;
    }

    /**
     * @param xorUndoRedoTitle the xorUndoRedoTitle to set
     */
    public void setXorUndoRedoTitle (String xorUndoRedoTitle) {
        this.xorUndoRedoTitle = xorUndoRedoTitle;
    }

    /**
     * @return the undoTitle
     */
    public String getUndoTitle () {
        return undoTitle;
    }

    /**
     * @param undoTitle the undoTitle to set
     */
    public void setUndoTitle (String undoTitle) {
        this.undoTitle = undoTitle;
        undoButton.setToolTipText(undoTitle);
    }

    /**
     * @return the redoTitle
     */
    public String getRedoTitle () {
        return redoTitle;
    }

    /**
     * @param redoTitle the redoTitle to set
     */
    public void setRedoTitle (String redoTitle) {
        this.redoTitle = redoTitle;
        redoButton.setToolTipText(redoTitle);
    }

    protected void scrollTo(int x, int y) {
        imagePane.setIgnoreRepaint(true);
        imagePane.getViewport().setIgnoreRepaint(true);
        JScrollBar hbar = imagePane.getHorizontalScrollBar();
        JScrollBar vbar = imagePane.getVerticalScrollBar();
        if (hbar != null) {
            if ( ( x < hbar.getMaximum() ) && ( x > hbar.getMinimum() ) ) {
                hbar.setValue(x);
            }
            else if ( x >= hbar.getMaximum() ) {
                hbar.setValue( hbar.getMaximum() );
            }
            else {
                hbar.setValue( hbar.getMinimum() );
            }
        }
        if (vbar != null) {
            if ( ( y < vbar.getMaximum() ) && ( y > vbar.getMinimum() ) ) {
                vbar.setValue(y);
            }
            else if ( y >= vbar.getMaximum() ) {
                vbar.setValue( vbar.getMaximum() );
            }
            else {
                vbar.setValue( vbar.getMinimum() );
            }
        }
        imagePane.setIgnoreRepaint(false);
        imagePane.getViewport().setIgnoreRepaint(false);
        imagePane.repaint();
    }

    public void zoomIn() {
        canvas.zoomInNoTranslation(0, 0);
        canvasRenderer.revalidate();
        imagePane.revalidate();
        repaint();
    }

    public void zoomOut() {
        canvas.zoomOutNoTranslation(0, 0);
        canvasRenderer.revalidate();
        imagePane.revalidate();
        repaint();
    }



    /*===============================================================*
     *                    Protected Sub-classes                      *
     *===============================================================*/


    protected class HiddenWindow extends ImageWindow {

        public HiddenWindow (ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            remove(ic);
        }

        public HiddenWindow (String title) {
            super(title);
        }

        public HiddenWindow (ImagePlus imp) {
            super(imp);
        }

        @Override
        public void show () {}

        @Override
        public void setVisible (boolean b) {
            if (!b) {
                super.setVisible( b );
            }
        }

    }

    /**
     * An ImageCanvas which can manage AdvancedImagePlus and its multi Roi. This
     * Component will also take care of Graphics translation when necessary.
     * 
     * @author GIRARDOT
     */
    protected class RenderedImageCanvas extends ImageCanvas {

        protected boolean selectionMode = false;

        public RenderedImageCanvas (ImagePlus imp) {
            super(imp);
        }

        @Override
        public void setCursor (Cursor cursor) {
            if (imp instanceof AdvancedImagePlus
                    && !((AdvancedImagePlus)imp).isAllowRoiCreation() ) {
                cursor = defaultCursor;
            }
            super.setCursor(cursor);
            if (canvasRenderer != null) {
                canvasRenderer.setCursor(cursor);
            }
        }

        @Override
        public void setCursor (int sx, int sy, int ox, int oy) {
            super.setCursor(ox, oy, ox, oy);
        }

        public Cursor getDefaultCursor() {
            return defaultCursor;
        }

        @Override
        public Graphics getGraphics () {
            if (canvasRenderer == null) {
                return super.getGraphics();
            }
            else {
                Graphics g = canvasRenderer.getGraphics();
//                canvasRenderer.translateGraphics(g);
                return g;
            }
        }

        @Override
        public boolean isShowing () {
            if (canvasRenderer != null) {
                return canvasRenderer.isShowing();
            }
            return super.isShowing();
        }

        @Override
        public synchronized void add (PopupMenu popup) {
            //do nothing (Popup managed in NumberImageJViewer)
        }

        @Override
        protected void handlePopupMenu (MouseEvent e) {
            //do nothing (Popup managed in NumberImageJViewer)
        }

        @Override
        public int offScreenX(int sx) {
            // Used because of Image Translation
            int result = super.offScreenX(sx);
            if (canvasRenderer != null) {
                result = (int)(canvasRenderer.antiTranslateX(sx)/magnification);
            }
            result += srcRect.x;
            return (int)result;
        }

        @Override
        public double offScreenXD(int sx) {
            // Used because of Image Translation
            double result = super.offScreenXD(sx);
            if (canvasRenderer != null) {
                result = canvasRenderer.antiTranslateX(sx)/magnification;
            }
            result += srcRect.x;
            return result;
        }

        @Override
        public int offScreenY(int sy) {
            // Used because of Image Translation
            int result = super.offScreenY(sy);
            if (canvasRenderer != null) {
                result = (int)(canvasRenderer.antiTranslateY(sy)/magnification);
            }
            result += srcRect.y;
            return result;
        }

        @Override
        public double offScreenYD(int sy) {
            // Used because of Image Translation
            double result = super.offScreenYD(sy);
            if (canvasRenderer != null) {
                result = canvasRenderer.antiTranslateY(sy)/magnification;
            }
            result += srcRect.y;
            return result;
        }

        /**
         * Method used to find the Roi to select, corresponding to given
         * coordinates
         * 
         * @param x
         *            X-coordinate
         * @param y
         *            Y-coordinate
         * @param keepSelection
         *            boolean to know whether to keep previous selected Roi
         *            selected
         */
        public void findSelectedRoi (int x, int y, boolean keepSelection) {
            findRoiSelected(x, y, keepSelection);
        }

        @Deprecated
        public void findRoiSelected (int x, int y, boolean keepSelection) {
            if (imp instanceof AdvancedImagePlus) {
                Roi closestRoi = findRoiAtPoint(x, y);

                Roi validatedRoi = ((AdvancedImagePlus)imp).getValidatedRoi();
                Vector<Roi> selectedRois = ((AdvancedImagePlus)imp).getSelectedRois();
                Vector<Roi> innerRois = ((AdvancedImagePlus)imp).getInnerRois();
                Vector<Roi> outerRois = ((AdvancedImagePlus)imp).getOuterRois();
                boolean roiSelected = false;
                boolean alreadySelected = false;

                if (closestRoi != null) {
                    roiSelected = true;
                    alreadySelected = selectedRois.contains(closestRoi);
                }

                if (keepSelection) {
                    if (roiSelected) {
                        boolean isValidatedRoi = innerRois.contains(closestRoi) || outerRois.contains(closestRoi);
                        if (alreadySelected) {
                            //remove it from selection
                            selectedRois.remove(closestRoi);
                            if (!isValidatedRoi) {
                                ((AdvancedImagePlus)imp).setHandledRoi(closestRoi);
                            }
                            if (closestRoi == validatedRoi) {
                                ((AdvancedImagePlus)imp).setValidatedRoi(null);
                            }
                        }
                        else {
                            selectedRois.add(closestRoi);
                            if (isValidatedRoi) {
                                //only one validated roi can be selected at a time
                                if (validatedRoi != null && closestRoi != validatedRoi) {
                                    selectedRois.remove(validatedRoi);
                                }
                                ((AdvancedImagePlus)imp).setValidatedRoi(closestRoi);
                            }
                            else {
                                ((AdvancedImagePlus)imp).setHandledRoi(closestRoi);
                            }
                        }
                    }
                }
                else {
                    selectedRois.clear();
                    ((AdvancedImagePlus)imp).setValidatedRoi(null);//only inner or outer roi
                    ((AdvancedImagePlus)imp).setHandledRoi(null);//validated roi cannot be handled

                    if (roiSelected) {
                        selectedRois.add(closestRoi);
                        if ( innerRois.contains(closestRoi) || outerRois.contains(closestRoi) ) {
                            ((AdvancedImagePlus)imp).setValidatedRoi(closestRoi);
                        }
                        else {
                            ((AdvancedImagePlus)imp).setHandledRoi(closestRoi);
                        }
                    }
                }
            }
        }

        /**
         * Method used to find the Roi which is corresponding to the given coordinates
         * 
         * @param x
         * @param y
         * @return the roi pointed if any, null otherwise
         */
        private Roi findRoiAtPoint(int x, int y) {
            Roi closestRoi = null;
            int minDistance = Integer.MAX_VALUE;
            for (Roi currentRoi : ((AdvancedImagePlus)imp).getAllRois()) {
                if ( currentRoi != null && currentRoi.getState() != Roi.CONSTRUCTING) {
                    if ( currentRoi.isHandle((int)(x*magnification), (int)(y*magnification)) >= 0 ) {
                        closestRoi = currentRoi;
                        break;
                    }
                    else if ( currentRoi.contains(x, y) ) {
                        Rectangle r = currentRoi.getBounds();
                        // Look for the closest Roi :
                        // This is the Roi whose bounds sides distance to
                        // point and bounds center distance to point are the
                        // smallest
                        int distanceX = Math.min(
                                Math.abs(r.x - x),
                                Math.abs(r.x+r.width - (x+1))
                        );
//                            distanceX = Math.min(
//                                    Math.abs( (int)r.getCenterX() - x ),
//                                    distanceX
//                            );
                        int distanceY = Math.min(
                                Math.abs(r.y - y),
                                Math.abs(r.y+r.width - (y+1))
                        );
//                            distanceY = Math.min(
//                                    Math.abs( (int)r.getCenterY() - y ),
//                                    distanceY
//                            );
                        int distance = Math.min(distanceX, distanceY);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestRoi = currentRoi;
                        }
                    }
                }
            }
            return closestRoi;
        }

        /**
         * Method used to find the Roi which can be moved/resized by mouse,
         * corresponding to given coordinates
         *
         * @param x
         *            X-coordinate
         * @param y
         *            Y-coordinate
         */
        public void findHandledRoi(int x, int y) {
            if (imp instanceof AdvancedImagePlus) {
                Roi closestRoi = findRoiAtPoint(x, y);

                Vector<Roi> innerRois = ((AdvancedImagePlus) imp).getInnerRois();
                Vector<Roi> outerRois = ((AdvancedImagePlus) imp).getOuterRois();
                boolean isValidatedRoi = innerRois.contains(closestRoi)
                || outerRois.contains(closestRoi);

                if (!isValidatedRoi) {
                    //we don't allow validated roi's modification
                    if (closestRoi != null) {
                        ((AdvancedImagePlus) imp).setHandledRoi(closestRoi);
                    }
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if ( e.getClickCount()==2
                 && ( handCursor.equals( getCursor() ) 
                      || defaultCursor.equals( getCursor() ) 
                    )
               ) {
                // Used because of Image Translation
                imp.killRoi();
                int sx = e.getX();
                int sy = e.getY();
                int x = offScreenX(sx);
                int y = offScreenY(sy);
                boolean keepSelection = ( e.isShiftDown()
                        || e.isControlDown()
                );
                findSelectedRoi(x, y, keepSelection);
            }
            else {
                super.mouseClicked(e);
            }
            repaint();
        }

        @Override
        public void mousePressed (MouseEvent e) {
            int tool = Toolbar.getToolId();
            if ((tool==Toolbar.POLYGON||tool==Toolbar.POLYLINE||tool==Toolbar.ANGLE)) {
                if (imp.getRoi()!=null && imp.getRoi().getState() != Roi.CONSTRUCTING) {
                    imp.killRoi();
                }
            }
            int sx = e.getX();
            int sy = e.getY();
            int x = offScreenX(sx);
            int y = offScreenY(sy);
            if ( isSelectionMode()
                    || ( imp instanceof AdvancedImagePlus
                         && !((AdvancedImagePlus)imp).isAllowRoiCreation() ) ) {
                if ( e.isAltDown() ) {
                    ((AdvancedImagePlus)imp).setAllowRoiCreation(true);
                }
                else {
                    ((AdvancedImagePlus)imp).setAllowRoiCreation(false);
                    findHandledRoi(x, y);
                }
            }
            if ( Toolbar.getToolId() == Toolbar.MAGNIFIER ) {
                // We don't want to have left click to zoom and right click to
                // unzoom, because right click is used for Popup Menu.
                // However, we want to be able to adapt zoom to a Roi
                Toolbar.getInstance().setTool(Toolbar.RECTANGLE);
            }
            x *= magnification;
            y *= magnification;
            if ( imp.getRoi() != null
                 && imp.getRoi().isHandle(x, y) >= 0
                 && ( imp.getRoi().getState() == Roi.CONSTRUCTING
                      || isSelectionMode()
                      || ( imp instanceof AdvancedImagePlus 
                           && !((AdvancedImagePlus)imp).isAllowRoiCreation()
                         )
                    )
                ) {
                // Used because of Image Translation
                super.mousePressed(
                        new MouseEvent(
                                e.getComponent(),
                                e.getID(),
                                e.getWhen(),
                                e.getModifiersEx(),
                                x,
                                y,
                                e.getClickCount(),
                                e.isPopupTrigger(),
                                e.getButton()
                        )
                );
            }
            else {
                super.mousePressed(e);
            }
            repaint();
        }

        @Override
        public void mouseReleased (MouseEvent e) {
            int sx = e.getX();
            int sy = e.getY();
            int x = (int)(offScreenX(sx)*canvas.getMagnification());
            int y = (int)(offScreenY(sy)*canvas.getMagnification());
            if (selectedButton == zoomButton) {
                if ( imp.getRoi() != null ) {
                    setZoomRoi( imp.getRoi() );
                }
                Toolbar.getInstance().setTool(Toolbar.MAGNIFIER);
            }
            else if ( isSelectionMode()
                    && (imp instanceof AdvancedImagePlus)
                    && ( ((AdvancedImagePlus)imp).isAllowRoiCreation() ) ) {
                setSelectionRoi( imp.getRoi() );
                ((AdvancedImagePlus)imp).setAllowRoiCreation(false);
            }
            else if ( imp.getRoi() != null
                    && imp.getRoi().isHandle(x, y) >= 0 ) {
                // Used because of Image Translation
                super.mouseReleased(
                        new MouseEvent(
                                e.getComponent(),
                                e.getID(),
                                e.getWhen(),
                                e.getModifiersEx(),
                                x,
                                y,
                                e.getClickCount(),
                                e.isPopupTrigger(),
                                e.getButton()
                        )
                );
            }
            else {
                super.mouseReleased(e);
            }
            if (imp.getRoi() != null
                    && imp.getRoi().getState() != Roi.CONSTRUCTING) {
                Roi.previousRoi = null;
                imp.setRoi((Roi)null);
                Roi.previousRoi = null;
            }
        }

        @Override
        public void mouseMoved (MouseEvent e) {
            if ( !isSelectionMode() ) {
                // Used because of Image Translation
                int sx = e.getX();
                int sy = e.getY();
                int x = offScreenX(sx);
                int y = offScreenY(sy);
                Dimension size = getImageSize();
                if (x >= size.width) {
                    int difference = 1 + x - size.width;
                    x -= difference;
                    sx -= difference*magnification;
                }
                if (y >= size.height) {
                    int difference = 1 + y - size.height;
                    y -= difference;
                    sy -= difference*magnification;
                }
                if ( sx < getXOrigin() ) {
                    sx = getXOrigin();
                    x = offScreenX(sx);
                }
                if ( sy < getYOrigin() ) {
                    sy = getYOrigin();
                    y = offScreenY(sy);
                }
                if ( imp.getRoi() != null
                        && imp.getRoi().getState() != Roi.CONSTRUCTING
                        && imp.getRoi().isHandle(x, y) >= 0 ) {
                    super.mouseMoved(
                            new MouseEvent(
                                    e.getComponent(),
                                    e.getID(),
                                    e.getWhen(),
                                    e.getModifiersEx(),
                                    x,
                                    y,
                                    e.getClickCount(),
                                    e.isPopupTrigger(),
                                    e.getButton()
                            )
                    );
                }
                else {
                    super.mouseMoved(
                            new MouseEvent(
                                    e.getComponent(),
                                    e.getID(),
                                    e.getWhen(),
                                    e.getModifiersEx(),
                                    sx,
                                    sy,
                                    e.getClickCount(),
                                    e.isPopupTrigger(),
                                    e.getButton()
                            )
                    );
                }
            }
        }

        /**
         * @return the selectionMode
         */
        public boolean isSelectionMode () {
            return selectionMode;
        }

        /**
         * @param selectionMode the selectionMode to set
         */
        public void setSelectionMode (boolean selectionMode) {
            this.selectionMode = selectionMode;
            if (imp instanceof AdvancedImagePlus) {
                ((AdvancedImagePlus)imp).setAllowRoiCreation(!selectionMode);
            }
        }

        public void zoomInNoTranslation (int x, int y) {
            super.zoomIn(x, y);
            JImageJ.this.revalidate();
        }

        public void zoomOutNoTranslation (int x, int y) {
            super.zoomOut(x, y);
            JImageJ.this.revalidate();
        }

        @Override
        public void zoomIn (int x, int y) {
            int ox = offScreenX(x);
            int oy = offScreenY(y);
            super.zoomIn(ox, oy);
            revalidate();
        }

        @Override
        public void zoomOut (int x, int y) {
            int ox = offScreenX(x);
            int oy = offScreenY(y);
            super.zoomOut(ox, oy);
            revalidate();
        }

        @Override
        public void unzoom () {
            super.unzoom();
            revalidate();
        }

        public void computeZoom(double zoom) {
            setMagnification(zoom);
            int newWidth = (int)(imageWidth*zoom);
            int newHeight = (int)(imageHeight*zoom);
            Dimension newSize = canEnlarge(newWidth, newHeight);
            if (newSize!=null) {
                setDrawingSize(newSize.width, newSize.height);
                if (newSize.width!=newWidth || newSize.height!=newHeight) {
                    reAdjustSourceRect(zoom);
                }
                else {
                    setMagnification(zoom);
                }
            }
            else {
                reAdjustSourceRect(zoom);
            }
            repaint();
        }

        public void computeZoom(Roi roi) {
            if (roi != null) {
                Rectangle roiBounds = roi.getBounds();
                double roiWidth = roiBounds.width;
                double roiHeight = roiBounds.height;
                Rectangle paneBounds = imagePane.getVisibleRect();
                Dimension currentSize = getImageSize();
                double paneWidth = paneBounds.width;
                double paneHeight = paneBounds.height;
                paneBounds = null;
                double newMag = Math.min(
                        (paneWidth/roiWidth),
                        (paneHeight/roiHeight)
                );
                newMag = getLowerZoomLevel(newMag+0.0000000000000001);
                int newWidth = (int)(currentSize.width*newMag);
                int newHeight = (int)(currentSize.height*newMag);
                Dimension newSize = new Dimension(newWidth, newHeight);
                setDrawingSize(newSize.width, newSize.height);
                setMagnification(newMag);
                revalidate();
                repaint();
                canvasRenderer.revalidate();
                Rectangle rect = roi.getBounds();
                Rectangle visible = new Rectangle(
                        (int)(rect.x*newMag),
                        (int)(rect.y*newMag),
                        (int)(rect.width*newMag),
                        (int)(rect.height*newMag)
                );
                imagePane.revalidate();
                scrollTo(visible.x, visible.y);
                rect = null;
                visible = null;
            }
        }

        protected void reAdjustSourceRect(double newMag) {
            int w = (int)Math.round(dstWidth/newMag);
            if (w*newMag<dstWidth) w++;
            int h = (int)Math.round(dstHeight/newMag);
            if (h*newMag<dstHeight) h++;
            srcRect = new Rectangle(0, 0, w, h);
            setMagnification(newMag);
        }

        @Override
        protected Dimension canEnlarge (int newWidth, int newHeight) {
            return new Dimension(newWidth, newHeight);
        }

    }

    /**
     * JComponent used to render the ImageCanvas. This JComponent manages
     * graphics translation for JLAxis use.
     * 
     * @author GIRARDOT
     */
    protected class CanvasRenderer extends JComponent
                                   implements MouseListener,
                                   MouseMotionListener,
                                   MouseWheelListener,
                                   KeyListener {
        protected int xMouse, yMouse;
        protected Stroke selectionStroke = new BasicStroke(2.0f);

        public CanvasRenderer() {
            super();
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            addKeyListener(this);
        }

        public double antiTranslateX(double x) {
            measureAxis();
            Dimension d = getSize();
            int imageWidth = canvas.getPreferredSize().width;
            xOrg = (d.width
                     - ( imageWidth + yAxisWidth + yAxisRightMargin )
                   ) / 2;
            return x - xOrg - yAxisWidth;
        }

        public int antiTranslateX(int x) {
            measureAxis();
            Dimension d = getSize();
            int imageWidth = canvas.getPreferredSize().width;
            xOrg = (d.width
                     - ( imageWidth + yAxisWidth + yAxisRightMargin )
                   ) / 2;
            return x - xOrg - yAxisWidth;
        }

        public double antiTranslateY(double y) {
            measureAxis();
            Dimension d = getSize();
            int imageHeight = canvas.getPreferredSize().height;
            yOrg = (d.height
                    - ( imageHeight + xAxisHeight + xAxisUpMargin )
                  ) / 2;
            return y - yOrg - xAxisUpMargin;
        }

        public int antiTranslateY(int y) {
            measureAxis();
            Dimension d = getSize();
            int imageHeight = canvas.getPreferredSize().height;
            yOrg = (d.height
                    - ( imageHeight + xAxisHeight + xAxisUpMargin )
                  ) / 2;
            return y - yOrg - xAxisUpMargin;
        }

        public void translateGraphics(Graphics g) {
            measureAxis();
            Dimension d = getSize();
            Dimension imageSize = canvas.getPreferredSize();
            int imageWidth = imageSize.width;
            int imageHeight = imageSize.height;
            xOrg = (d.width
                    - ( imageWidth + yAxisWidth + yAxisRightMargin )
            ) / 2;
            yOrg = (d.height
                    - ( imageHeight + xAxisHeight + xAxisUpMargin )
            ) / 2;
            g.translate(xOrg + yAxisWidth, yOrg + xAxisUpMargin);
        }

        public MouseEvent antiTranslateMouseEvent (MouseEvent e) {
            measureAxis();
            Dimension d = getSize();
            Dimension imageSize = canvas.getPreferredSize();
            int imageWidth = imageSize.width;
            int imageHeight = imageSize.height;
            xOrg = (d.width
                    - ( imageWidth + yAxisWidth + yAxisRightMargin )
            ) / 2;
            yOrg = (d.height
                    - ( imageHeight + xAxisHeight + xAxisUpMargin )
            ) / 2;
            return new MouseEvent(
                    e.getComponent(),
                    e.getID(),
                    e.getWhen(),
                    e.getModifiers(),
                    e.getX() - xOrg - yAxisWidth,
                    e.getY() - yOrg - xAxisUpMargin,
                    e.getClickCount(),
                    e.isPopupTrigger(),
                    e.getButton()
            );
        }

        public MouseEvent translateMouseEvent (MouseEvent e) {
            measureAxis();
            Dimension d = getSize();
            Dimension imageSize = canvas.getPreferredSize();
            int imageWidth = imageSize.width;
            int imageHeight = imageSize.height;
            xOrg = (d.width
                     - ( imageWidth + yAxisWidth + yAxisRightMargin )
                   ) / 2;
            yOrg = (d.height
                     - ( imageHeight + xAxisHeight + xAxisUpMargin )
                   ) / 2;
            return new MouseEvent(
                    e.getComponent(),
                    e.getID(),
                    e.getWhen(),
                    e.getModifiers(),
                    e.getX() + xOrg + yAxisWidth,
                    e.getY() + yOrg + xAxisUpMargin,
                    e.getClickCount(),
                    e.isPopupTrigger(),
                    e.getButton()
            );
        }

        public void mouseClicked (MouseEvent e) {
            if (canvas != null) {
                canvas.mouseClicked(e);
            }
        }

        public void mouseEntered (MouseEvent e) {
            if (canvas != null) {
                canvas.mouseEntered(e);
            }
        }

        public void mouseExited (MouseEvent e) {
            if (canvas != null) {
                canvas.mouseExited(e);
            }
        }

        public void mousePressed (MouseEvent e) {
            grabFocus();
            xMouse = e.getX();
            yMouse = e.getY();
            if (canvas != null) {
                if (Toolbar.getToolId() != Toolbar.HAND) {
                    canvas.mousePressed(e);
                    repaint();
                }
            }
        }

        public void mouseReleased (MouseEvent e) {
            if (canvas != null) {
                canvas.mouseReleased(e);
                repaint();
            }
        }

        public void mouseDragged (MouseEvent e) {
            if (Toolbar.getToolId() == Toolbar.HAND) {
                int xDelta = xMouse - e.getX();
                int yDelta = yMouse - e.getY();
                if (xDelta > 0) {
                    xDelta = Math.min(xDelta, 8);
                }
                else {
                    xDelta = Math.max(xDelta, -8);
                }
                if (yDelta > 0) {
                    yDelta = Math.min(yDelta, 8);
                }
                else {
                    yDelta = Math.max(yDelta, -8);
                }
                JScrollBar hbar = imagePane.getHorizontalScrollBar();
                JScrollBar vbar = imagePane.getVerticalScrollBar();
                int x = 0, y = 0;
                if (hbar != null) {
                    x = hbar.getValue();
                }
                if (vbar != null) {
                    y = vbar.getValue();
                }
                hbar = null;
                vbar = null;
                x += xDelta;
                y += yDelta;
                scrollTo(x, y);
            }
            if (canvas != null) {
                canvas.mouseDragged(e);
                repaint();
            }
        }

        public void mouseMoved (MouseEvent e) {
            if (canvas != null) {
                canvas.mouseMoved(e);
                repaint();
            }
        }

        public void mouseWheelMoved (MouseWheelEvent e) {
            if (Toolbar.getToolId() == Toolbar.MAGNIFIER) {
                double currentMag = getZoom();
                double x = e.getX() / currentMag;
                double y = e.getY() / currentMag;
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                }
                else {
                    zoomOut();
                }
                x *= getZoom();
                y *= getZoom();
                revalidate();
                repaint();
                Rectangle rect = imagePane.getVisibleRect();
                x -= rect.getCenterX();
                y -= rect.getCenterY();
                scrollTo( (int)Math.rint(x), (int)Math.rint(y) );
            }
        }

        @Override
        public void paint (Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            Stroke oldStroke = g2d.getStroke();
            if (canvas != null && theImage != null) {
                adaptAxes();
                Dimension d = getSize();
                if ( isOpaque() ) {
                    g2d.setColor( getBackground() );
                    g2d.fillRect( 0, 0, d.width, d.height );
                }
                translateGraphics(g2d);
                canvas.paint(g2d);
                paintAxis(g2d);
                Vector<Roi> innerRois = imp.getInnerRois();
                Vector<Roi> outerRois = imp.getOuterRois();
                Vector<Roi> selectedRois = imp.getSelectedRois();
                boolean manySelected = (selectedRois.size() > 1);
                for (Roi roi : imp.getAllRois()) {
                    if (roi != null) {
                        int selectedIndex = selectedRois.indexOf(roi);
                        if ( selectedIndex > -1 ) {
                            if (manySelected) {
                                g2d.setColor(Color.WHITE);
                                Rectangle bounds = roi.getBounds();
                                int x = bounds.x + bounds.width/2;
                                int y = bounds.y + bounds.height/2;
                                g2d.drawString(
                                        Integer.toString(selectedIndex + 1),
                                        (int)( x*canvas.getMagnification() ),
                                        (int)( y*canvas.getMagnification() )
                                );
                            }

                            g2d.setStroke(selectionStroke);
                            if ( innerRois.contains( roi ) ) {
                                Roi.setColor(roiInsideSelectionColor);
                            }
                            else if ( outerRois.contains( roi ) ) {
                                Roi.setColor(roiOutsideSelectionColor);
                            }
                            else {
                                Roi.setColor(roiSelectionColor);
                            }
                        }
                        else {
                            g2d.setStroke(oldStroke);
                            if ( innerRois.contains( roi ) ) {
                                Roi.setColor(roiInsideColor);
                            }
                            else if ( outerRois.contains( roi ) ) {
                                Roi.setColor(roiOutsideColor);
                            }
                            else {
                                Roi.setColor(roiColor);
                            }
                        }
                        roi.draw(g2d);
                    }
                }
            }
        }

        /**
         * 
         */
        protected void adaptAxes() {
            if ( xAxis.isAutoScale() ) {
                xAxis.setAutoScale(false);
                xAxis.setMinimum(0);
                xAxis.setMaximum( theImage.getWidth() );
                xAxis.setAutoScale(true);
            }
            if ( yAxis.isAutoScale() ) {
                yAxis.setAutoScale(false);
                yAxis.setMinimum(0);
                yAxis.setMaximum( theImage.getHeight() );
                yAxis.setAutoScale(true);
            }
        }

        @Override
        public Dimension getPreferredSize () {
            if (canvas != null) {
                Dimension size = canvas.getPreferredSize();
                measureAxis();
                size.width += yAxisWidth + yAxisRightMargin;
                size.height += xAxisHeight + xAxisUpMargin;
                return size;
            }
            return super.getPreferredSize();
        }

        @Override
        public Dimension getMinimumSize () {
            return getPreferredSize();
        }

        public void keyPressed (KeyEvent e) {
            Rectangle bounds = null;
            Roi roi = imp.getRoi();
            if (roi != null && roi.getState() != Roi.CONSTRUCTING) {
                bounds = roi.getBounds();
            }
            switch( e.getKeyCode() ) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    deleteSelectedRois();
                    break;
                case KeyEvent.VK_ESCAPE:
                    getArrowButton().doClick();
                    canvas.setCursor( canvas.getDefaultCursor() );
                    break;
                case KeyEvent.VK_CONTROL:
                    break;
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_ADD:
                    if (Toolbar.getToolId() == Toolbar.MAGNIFIER) {
                        zoomIn();
                    }
                    break;
                case KeyEvent.VK_MINUS:
                case KeyEvent.VK_SUBTRACT:
                    if (Toolbar.getToolId() == Toolbar.MAGNIFIER) {
                        zoomOut();
                    }
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT:
                    // TODO This test is necessary because of a focus problem.
                	// A click outside the selected ROI makes it loose focus although it remains selected
                    // (?! -> imp.getRoi() == null) so keyboard doesn't operate
                    if (bounds != null) {
                        int onMask = InputEvent.ALT_DOWN_MASK;
                        if ((e.getModifiersEx() & onMask) == onMask) {
                            roi.nudgeCorner(e.getKeyCode());
                        }
                        else {
                            roi.nudge(e.getKeyCode());
                        }
                    }
                    break;
                default:
                    hiddenIJ.keyPressed(e);
            }
            roi = null;
            bounds = null;
            repaint();
        }

        public void keyReleased (KeyEvent e) {
            hiddenIJ.keyReleased(e);
            repaint();
        }

        public void keyTyped (KeyEvent e) {
            hiddenIJ.keyTyped(e);
            repaint();
        }

    }

}

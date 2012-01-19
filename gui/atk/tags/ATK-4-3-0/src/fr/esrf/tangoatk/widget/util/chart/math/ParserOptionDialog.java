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
 *   The package  fr.esrf.tangoatk.widget.util.chart.math has been added to
 *   extend the JLChart's features with the mathematique expressions calculation
 * 
 *   Author        :   SOLEIL Control team (Raphael Girardot)
 *   Original      :   January 2007
 *  
 */

package fr.esrf.tangoatk.widget.util.chart.math;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import fr.esrf.tangoatk.widget.util.chart.*;

class PossibleNamesLister extends JDialog implements MouseListener
{
    protected JLabel      lastSelected    = null;
    protected JLabel      destination     = null;
    protected JLabel[]    names           = null;
    protected JPanel      mainPanel       = null;
    protected JScrollPane scroller        = null;
    protected int         prefWidth       = 0;
    protected int         prefHeight      = 0;

    final static Color    selectionColor  = new Color(255, 160, 200);
    final static Color    mouseOverColor  = new Color(200, 180, 255);
    final static Border   labelBorder     = new LineBorder(Color.BLACK, 1);
    final static Border   selectionBorder = new LineBorder(selectionColor, 1);
    final static int      maxWidth        = 400;
    final static int      maxHeight       = 400;

    public PossibleNamesLister(String[] nameList, JDialog parent)
    {
        super(parent, "Choose variable:", true);
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        if (nameList != null)
        {
            names = new JLabel[nameList.length];
            for (int i = 0; i < nameList.length; i++)
            {
                if (nameList[i] != null)
                {
                    names[i] = new JLabel( new String(nameList[i]) );
                }
                else
                {
                    names[i] = new JLabel( "" );
                }
                names[i].setBorder(labelBorder);
                if (names[i].getPreferredSize().width > prefWidth)
                {
                    prefWidth = names[i].getPreferredSize().width;
                }
                prefHeight += 25;
            }
            for (int i = 0; i < names.length; i++)
            {
                names[i].setBounds(0, 25*i, prefWidth, 25);
                mainPanel.add(names[i]);
                names[i].addMouseListener(this);
            }
            mainPanel.setPreferredSize( new Dimension(prefWidth, prefHeight) );
        }
        scroller = new JScrollPane(mainPanel);
        setContentPane(scroller);
        mainPanel.setBackground(Color.WHITE);
        scroller.setBackground(Color.WHITE);
        scroller.getViewport().setBackground(Color.WHITE);
        this.setBackground(Color.WHITE);
        initBounds();
        this.setResizable(false);
    }

    public void initBounds()
    {
        int width = prefWidth + 30 < maxWidth ? prefWidth + 30 : maxWidth;
        int height = prefHeight + 30 < maxHeight ? prefHeight + 30 : maxHeight;
        int x = getParent().getX() - (prefWidth + 50);
        int y = getParent().getY();
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        this.setBounds(
                x, y,
                width, height
        );
    }

    public void mouseClicked (MouseEvent e)
    {
        if (e.getClickCount() > 1)
        {
            if ( lastSelected != null )
            {
                lastSelected.setBorder(labelBorder);
                lastSelected.setOpaque(false);
                lastSelected.setBackground(Color.WHITE);
                lastSelected.repaint();
            }
            setVisible(false);
        }
    }

    public void mouseEntered (MouseEvent e)
    {
        ((JLabel)e.getSource()).setBackground(mouseOverColor);
        ((JLabel)e.getSource()).setOpaque(true);
        ((JLabel)e.getSource()).repaint();
    }

    public void mouseExited (MouseEvent e)
    {
        ((JLabel)e.getSource()).setOpaque(false);
        ((JLabel)e.getSource()).setBackground(Color.WHITE);
        ((JLabel)e.getSource()).repaint();
    }

    public void mousePressed (MouseEvent e)
    {
        if ( lastSelected != null 
                && lastSelected != (JLabel)e.getSource() )
        {
            lastSelected.setBorder(labelBorder);
            lastSelected.setOpaque(false);
            lastSelected.setBackground(Color.WHITE);
            lastSelected.repaint();
        }
        ((JLabel)e.getSource()).setBackground(selectionColor);
        ((JLabel)e.getSource()).setBorder(selectionBorder);
        ((JLabel)e.getSource()).setOpaque(true);
        ((JLabel)e.getSource()).repaint();
        lastSelected = (JLabel)e.getSource();
        if (destination != null)
        {
            destination.setText( ( (JLabel)e.getSource() ).getText() );
            destination.setToolTipText( ( (JLabel)e.getSource() ).getText() );
        }
    }

    public void mouseReleased (MouseEvent e)
    {
        //nothing to do
    }

    public void dispose()
    {
        if (names != null)
        {
            for (int i = 0; i < names.length; i++)
            {
                names[i] = null;
            }
            names = null;
        }
        lastSelected = null;
    }

    public JLabel getDestination ()
    {
        return destination;
    }

    public void setDestination (JLabel destination)
    {
        this.destination = destination;
    }
}

class VariablePanel extends JPanel implements MouseListener
{
    protected String              expression     = null;
    protected JLabel[]            variableTitles = null;
    protected JLabel[]            variableNames  = null;
    protected JLabel              lastSelected   = null;
    protected boolean             x              = false;
    protected JDialog             parent;
    protected final static Border textBorder     = new EtchedBorder();
    protected PossibleNamesLister lister         = null;

    public VariablePanel(String[] names, JDialog parent)
    {
        super();
        this.setLayout(null);
        this.parent = parent;
        lister = new PossibleNamesLister(names, parent);
    }

    public void generateVariables()
    {
        this.removeAll();
        int width = 250, height = 0;
        if (expression.indexOf("x1") != -1)
        {
            x = false;
            int count = 0;
            while(true)
            {
                if (expression.indexOf("x" + (count+1)) != -1)
                {
                    count++;
                }
                else
                {
                    break;
                }
            }
            variableTitles = new JLabel[count];
            variableNames = new JLabel[count];
            for (int i = 0; i < count; i++)
            {
                variableTitles[i] = new JLabel("x" + (i+1) + ":");
                variableTitles[i].setBounds( 0, i*25, 30, 25 );
                variableTitles[i].setMaximumSize(variableTitles[i].getPreferredSize());
                this.add(variableTitles[i]);
                variableNames[i] = new JLabel("");
                variableNames[i].setBounds( 40, i*25, 210, 25 );
                variableNames[i].setBackground(Color.WHITE);
                variableNames[i].setBorder(textBorder);
                variableNames[i].setOpaque(true);
                variableNames[i].addMouseListener(this);
                variableNames[i].setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
                this.add(variableNames[i]);
                height += 25;
            }
        }
        else
        {
            if (expression.replaceAll("x", "_x_").replaceAll("e_x_p", "exp").indexOf("_x_") != -1)
            {
                x = true;
                variableTitles = new JLabel[1];
                variableNames = new JLabel[1];
                variableTitles[0] = new JLabel("x:");
                variableTitles[0].setBounds( 0, 0, 30, 25 );
                variableTitles[0].setMaximumSize(variableTitles[0].getPreferredSize());
                this.add(variableTitles[0]);
                variableNames[0] = new JLabel("");
                variableNames[0].setBounds( 40, 0, 210, 25 );
                variableNames[0].setBackground(Color.WHITE);
                variableNames[0].setBorder(textBorder);
                variableNames[0].setOpaque(true);
                variableNames[0].addMouseListener(this);
                variableNames[0].setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
                this.add(variableNames[0]);
                height += 25;
            }
            else
            {
                x = false;
                variableTitles = new JLabel[0];
                variableNames = new JLabel[0];
            }
        }
        this.setPreferredSize( new Dimension(width, height) );
        revalidate();
        repaint();
    }

    public void mouseClicked (MouseEvent e)
    {
        //nothing to do
    }

    public void mouseEntered (MouseEvent e)
    {
        //nothing to do
    }

    public void mouseExited (MouseEvent e)
    {
        //nothing to do
    }

    public void mousePressed (MouseEvent e)
    {
        lister.setDestination( (JLabel)e.getSource() );
        lister.initBounds();
        lister.setVisible(true);
    }

    public void mouseReleased (MouseEvent e)
    {
        //nothing to do
    }

    public String getExpression ()
    {
        return expression;
    }

    public void setExpression (String expression)
    {
        this.expression = expression;
    }

    public void dispose()
    {
        expression = null;
        if (variableNames != null)
        {
            for (int i = 0; i < variableNames.length; i++)
            {
                variableNames[i] = null;
                variableTitles[i] = null;
            }
            variableNames = null;
            variableTitles = null;
        }
        lastSelected = null;
        parent = null;
    }

    public boolean isX ()
    {
        return x;
    }

    public String[] getVariables()
    {
        String[] variables;
        if (variableNames == null)
        {
            variables = new String[0];
        }
        else
        {
            variables = new String[variableNames.length];
            for (int i = 0; i < variableNames.length; i++)
            {
                variables[i] = new String(variableNames[i].getText());
            }
        }
        return variables;
    }
}

public class ParserOptionDialog extends JLDataViewOption
{
    protected boolean             isValidated  = true;
    protected JButton             cancelButton;
    protected JLDataView          dataView;
    protected JTextField          expressionField;
    protected JLabel              expressionLabel;
    protected JTextField          dvNameField;
    protected JLabel              dvNameLabel;
    protected JComboBox           axisBox;
    protected JLabel              axisLabel;
    protected int                 selectedAxis = -1;
    protected JButton             generateButton;
    protected JButton             helpButton;
    protected VariablePanel       variablePanel = null;
    protected JScrollPane         variableScrollPane = null;

    public ParserOptionDialog (JDialog parent, JLChart chart, JLDataView v) {
        super( parent, chart, v );
        initComponents();
        dataView = v;
        addWindowListener();
        setTitle();
    }

    public ParserOptionDialog (JFrame parent, JLChart chart, JLDataView v) {
        super( parent, chart, v );
        initComponents();
        dataView = v;
        addWindowListener();
        setTitle();
    }

    protected void initComponents () {
        JPanel innerPanel = (JPanel) this.getContentPane();
        JPanel newPanel = new JPanel();
        Vector<JLDataView> views;
        boolean canSetOnX = true;
        if (chart instanceof StaticChartMathExpression) {
            views = ( (StaticChartMathExpression) chart ).prepareViews();
            canSetOnX = ( (StaticChartMathExpression) chart )
                    .isCanPutExpressionOnX();
        }
        else {
            views = new Vector<JLDataView>();
            if ( chart.getXAxis().isXY() ) {
                views.addAll(chart.getXAxis().getViews());
            }
            views.addAll( chart.getY1Axis().getViews() );
            views.addAll( chart.getY2Axis().getViews() );
        }
        String[] variables = new String[views.size()];
        for (int i = 0; i < views.size(); i++) {
            variables[i] = ( (JLDataView)views.get(i) ).getName();
        }
        views.clear();
        views = null;
        variablePanel = new VariablePanel(variables, this);
        variables = null;
        newPanel.setLayout( null );
        expressionLabel = new JLabel( "Enter your expression:" );
        expressionLabel.setFont(GraphicsUtils.getLabelFont());
        expressionField = new JTextField( "" );
        cancelButton = new JButton( "Cancel" );
        cancelButton.setMargin(new Insets(0,0,0,0));
        cancelButton.addMouseListener( this );
        expressionLabel.setBounds( 0, 5, 140, 25 );
        expressionField.setBounds( 140, 5, 130, 25 );
        dvNameLabel = new JLabel( "Enter your DataView name:" );
        dvNameLabel.setFont(GraphicsUtils.getLabelFont());
        dvNameField = new JTextField( "" );
        dvNameLabel.setBounds( 0, 35, 160, 25 );
        dvNameField.setBounds( 160, 35, 110, 25 );
        helpButton = new JButton("?");
        helpButton.setToolTipText("About Expressions");
        helpButton.setMargin(new Insets(0,0,0,0));
        helpButton.setBounds( 0, 65, 25, 25 );
        helpButton.addMouseListener(this);
        generateButton = new JButton("Generate Variables");
        generateButton.setMargin(new Insets(0,0,0,0));
        generateButton.addMouseListener(this);
        generateButton.setBounds( 80, 65, 190, 25 );
        variableScrollPane = new JScrollPane(variablePanel);
        variableScrollPane.setBounds( 0, 90, 270, 215 );
        variableScrollPane.setBorder( BorderFactory.createTitledBorder(new EtchedBorder(), "Variables") );
        axisLabel = new JLabel( "Axis:" );
        axisBox = new JComboBox();
        axisBox.addItem("Y1");
        axisBox.addItem("Y2");
        if (canSetOnX) {
            axisBox.addItem("X");
        }
        axisLabel.setBounds( 0, 315, 50, 25 );
        axisBox.setBounds( 50, 315, 50, 25 );
        innerPanel.setBounds( 0, 340, 270, 310 );
        cancelButton.setBounds( 5, 280, 80, 25);
        closeBtn.setText( "Ok" );
        innerPanel.remove( nameLabel );
        innerPanel.add( cancelButton );
        newPanel.add( expressionLabel );
        newPanel.add( expressionField );
        newPanel.add( dvNameLabel );
        newPanel.add( dvNameField );
        newPanel.add( variableScrollPane );
        newPanel.add( helpButton );
        newPanel.add( generateButton );
        newPanel.add( axisLabel );
        newPanel.add( axisBox );
        newPanel.add( innerPanel );
        setResizable( false );
        newPanel.setPreferredSize( new Dimension( 270, 650 ) );
        this.setContentPane( newPanel );
    }

    protected void addWindowListener () {
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing (WindowEvent e) {
                isValidated = false;
                setVisible( false );
            }
        } );
    }

    protected void setTitle () {
        setTitle( "Expression Evaluation" );
    }

    public void mouseClicked (MouseEvent e) {
        if ( e.getSource() == cancelButton ) {
            isValidated = false;
            setVisible( false );
        }
        else if ( e.getSource() == closeBtn ) {
            isValidated = true;
            selectedAxis = axisBox.getSelectedIndex();
            if ( dvNameField.getText() == null
                    || "".equals( dvNameField.getText().trim() ) ) {
                dataView.setName( expressionField.getText().trim() );
            }
            else {
                dataView.setName( dvNameField.getText().trim() );
            }
            setVisible( false );
        }
        else if ( e.getSource() == generateButton ) {
            variablePanel.setExpression( expressionField.getText() );
            variablePanel.generateVariables();
        }
        else if ( e.getSource() == helpButton ) {
            new ParserHelpDialog( this ).setVisible( true );
        }
        else super.mouseClicked( e );
    }

    public boolean isX () {
        return variablePanel.isX();
    }

    public String[] getVariables () {
        return variablePanel.getVariables();
    }

    public void dispose () {
        cancelButton = null;
        dataView = null;
        expressionField = null;
        expressionLabel = null;
        super.dispose();
    }
}

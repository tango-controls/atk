/*
 *   The package  fr.esrf.tangoatk.widget.util.chart.math has been added to
 *   extend the JLChart's features with the mathematique expressions calculation
 * 
 *   Author        :   SOLEIL Control team (Raphael Girardot)
 *   Original      :   January 2007
 *  
 */

package fr.esrf.tangoatk.widget.util.chart.math;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

import fr.esrf.tangoatk.widget.util.chart.*;

public class StaticChartMathExpression extends JLChart implements
        IJLChartActionListener
{
    protected final static String EVALUATE_EXPRESSION = "Evaluate an expression";

    /** Used with expressions management to put the DataView representing the expression on Y1 axis */
    public final static int Y1_AXIS = 0;

    /** Used with expressions management to put the DataView representing the expression on Y2 axis */
    public final static int Y2_AXIS = 1;

    /** Used with expressions management to put the DataView representing the expression on X axis */
    public final static int X_AXIS  = 2;

    public StaticChartMathExpression ()
    {
        super();
        addUserAction(EVALUATE_EXPRESSION);
        addJLChartActionListener(this);
    }

    /**
     * Display the expression dialog.
     */
    public void showExpressionDialog ()
    {
        JLDataView expressionDataView = new JLDataView();
        Object dlgParent = getRootPane().getParent();
        ParserOptionDialog optionDlg;
        if ( dlgParent instanceof JDialog )
        {
            optionDlg = new ParserOptionDialog(
                    (JDialog) dlgParent,
                    this,
                    expressionDataView
            );
        }
        else if ( dlgParent instanceof JFrame )
        {
            optionDlg = new ParserOptionDialog(
                    (JFrame) dlgParent,
                    this,
                    expressionDataView
            );
        }
        else
        {
            optionDlg = new ParserOptionDialog(
                    (JFrame) null,
                    this,
                    expressionDataView
            );
        }
        ATKGraphicsUtils.centerDialog( optionDlg );
        optionDlg.setVisible( true );
        boolean ok = optionDlg.isValidated;
        int selectedAxis = optionDlg.selectedAxis;
        if ( ok )
        {
            applyExpressionToChart(
                    optionDlg.expressionField.getText().trim(),
                    expressionDataView,
                    selectedAxis,
                    optionDlg.getVariables(),
                    optionDlg.isX()
            );
        }
        optionDlg.dispose();
        optionDlg = null;
    }

    /**
     * Call this method to evaluate an expression and have the result
     * represented by a DataView you previously parametered
     * 
     * @param expression
     *            The String representing your expression
     * @param expressionDataView
     *            The DataView in which you want to put your expression
     *            evaluation result
     * @param selectedAxis
     *            The axis on which you want to put your DataView
     * @see #X_AXIS
     * @see #Y1_AXIS
     * @see #Y2_AXIS
     */
    public void applyExpressionToChart (String expression,
            JLDataView expressionDataView, int selectedAxis,
            String[] variables, boolean x)
    {
        Vector views = new Vector();
        if ( getXAxis().isXY() ) views.addAll( getXAxis().getViews() );
        views.addAll( getY1Axis().getViews() );
        views.addAll( getY2Axis().getViews() );
        ExpressionParser parser = new ExpressionParser( variables.length, expression );
        parser.setX( x );
        parser.setPrecision( getTimePrecision() );
        for (int i = 0; i < variables.length; i++)
        {
            for (int v = 0; v < views.size(); v++)
            {
                if ( variables[i].equals( ( (JLDataView) views.get( v ) ).getName() ) )
                {
                    parser.add( i, (JLDataView) views.get( v ) );
                    break;
                }
            }
        }
        if ( selectedAxis == 2 )
        {
            getXAxis().addDataView( parser.buildDataView( expressionDataView ) );
            if ( !getXAxis().isVisible() )
            {
                getXAxis().setVisible( true );
                getXAxis().setAutoScale( true );
            }
        }
        else if ( selectedAxis == 1 )
        {
            getY2Axis().addDataView( parser.buildDataView( expressionDataView ) );
            if ( !getY2Axis().isVisible() )
            {
                getY2Axis().setVisible( true );
                getY2Axis().setAutoScale( true );
            }
        }
        else
        {
            getY1Axis().addDataView( parser.buildDataView( expressionDataView ) );
            if ( !getY1Axis().isVisible() )
            {
                getY1Axis().setVisible( true );
                getY1Axis().setAutoScale( true );
            }
        }
        repaint();
        parser.clean();
        parser = null;
    }

    public void actionPerformed (JLChartActionEvent evt)
    {
        if ( EVALUATE_EXPRESSION.equals( evt.getName() ) )
        {
            showExpressionDialog();
        }
    }

    public boolean getActionState (JLChartActionEvent evt)
    {
        return false;
    }

    public static void main(String[] args)
    {
        final JFrame f = new JFrame();
        final StaticChartMathExpression chart = new StaticChartMathExpression();

        // Initialise chart properties
        chart.setHeaderFont(new Font("Times", Font.BOLD, 18));
        chart.setLabelFont(new Font("Times", Font.BOLD, 12));
        chart.setHeader("Test DataView");

        chart.getXAxis().setAutoScale(true);
        chart.getXAxis().setName("Value");
        chart.getXAxis().setGridVisible(true);
        chart.getXAxis().setSubGridVisible(true);
        chart.getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
        chart.getY1Axis().setGridVisible(true);
        chart.getY1Axis().setSubGridVisible(true);
        chart.getY2Axis().setVisible(false);
        chart.getY2Axis().setName("unit");
        
        // Initialise axis properties
        chart.getY1Axis().setName("mA");
        chart.getY1Axis().setAutoScale(false);
        chart.getY1Axis().setMinimum(-120);
        chart.getY1Axis().setMaximum(0);
        chart.getY1Axis().setLabels(
                new String[] {"-120","-100","-80","-60","-40","-20","0"} ,
                new double[] {-120,-100,-80,-60,-40,-20,0}
        );

        if (args.length > 0)
        {
          chart.loadDataFile(args[0]);
        }
        else
        {
            final JLDataView v1 = new JLDataView();
            final JLDataView v2 = new JLDataView();

            v1.add(-6, -10.0);
            v1.add(-5, -15.0);
            v1.add(-4, 17.0);
            v1.add(-3, 21.0);
            v1.add(-2, 22.0);
            v1.add(-1, 24.0);
            v1.add(0, 98.0);
            v1.add(1, Double.NaN);
            v1.add(2, 21.0);
            v1.add(3, 99.0);
            v1.add(4, 50.0);
            v1.add(5, 40.0);
            v1.add(6, 30.0);
            v1.add(7, 20.0);
            v1.setMarker(JLDataView.MARKER_CIRCLE);
            v1.setStyle(JLDataView.STYLE_DASH);
            v1.setName("Le signal 1");
            v1.setUnit("std");
            v1.setClickable(true);
            v1.setUserFormat("%5.2f");
            chart.getY1Axis().addDataView(v1);

            v2.add(-6, -10.0);
            v2.add(-5, -5.0);
            v2.add(-4, 7.0);
            v2.add(-3, 11.0);
            v2.add(-2, 12.0);
            v2.add(-1, 14.0);
            v2.add(0, 78.0);
            v2.add(1, Double.NaN);
            v2.add(2, 22.0);
            v2.add(3, 55.0);
            v2.add(4, 42.0);
            v2.add(5, 11.0);
            v2.add(6, 47.0);
            v2.add(7, 12.0);
            v2.setName("Le signal 2");
            v2.setUnit("std");
            v2.setColor(Color.blue);
            v2.setLineWidth(3);
            v2.setFillColor(Color.orange);
            v2.setFillStyle(JLDataView.FILL_STYLE_SOLID);
            v2.setViewType(JLDataView.TYPE_BAR);
            chart.getY2Axis().addDataView(v2);
        }

        JPanel bot = new JPanel();
        bot.setLayout(new FlowLayout());

        JButton b = new JButton("Exit");
        b.addMouseListener( new MouseAdapter() {
            public void mouseClicked (MouseEvent e)
            {
                System.exit( 0 );
            }
        } );

        bot.add(b);

        JButton c = new JButton("Options");
        c.addMouseListener( new MouseAdapter() {
            public void mouseClicked (MouseEvent e)
            {
                chart.showOptionDialog();
            }
        } );

        bot.add(c);

        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(chart, BorderLayout.CENTER);
        f.getContentPane().add(bot, BorderLayout.SOUTH);
        f.setSize(400, 300);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

}

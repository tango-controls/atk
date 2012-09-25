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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.chart.AdvancedJLChart;
import fr.esrf.tangoatk.widget.util.chart.CfFileReader;
import fr.esrf.tangoatk.widget.util.chart.IJLChartActionListener;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChartActionEvent;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;
import fr.esrf.tangoatk.widget.util.chart.OFormat;

public class StaticChartMathExpression extends AdvancedJLChart
                                       implements IJLChartActionListener {

    protected final static String EVALUATE_EXPRESSION = "Evaluate an expression";

    /** Used to put a DataView on Y1 axis */
    public final static int Y1_AXIS = 0;

    /** Used to put a DataView on Y2 axis */
    public final static int Y2_AXIS = 1;

    /** Used to put a DataView on X axis */
    public final static int X_AXIS  = 2;

    /** Used to allow or not the use of X axis JLDataView with expressions */
    private boolean useXViewsWithExpressions = true;

    /** Used to allow or not to put expressions on X Axis */
    private boolean canPutExpressionOnX = true;

    // Used to save expressions in files, but can be used to dynamically update
    // the expression JLDataViews.
    // Key : The expression JLDataView
    // Associated value : Object[]
    //   0 : Integer <-> axis
    //   1 : String  <-> expression
    //   2 : Boolean <-> x
    //   3..length-1 : variables
    protected HashMap<JLDataView,Object[]> expressionMap;

    public StaticChartMathExpression () {
        super();
        expressionMap = new HashMap<JLDataView,Object[]>();
        addUserAction(EVALUATE_EXPRESSION);
        addJLChartActionListener(this);
    }

    /**
     * Display the expression dialog.
     */
    public void showExpressionDialog () {
        JLDataView expressionDataView = new JLDataView();
        Object dlgParent = getRootPane().getParent();
        ParserOptionDialog optionDlg;
        if (dlgParent instanceof JDialog) {
            optionDlg = new ParserOptionDialog(
                    (JDialog) dlgParent,
                    this,
                    expressionDataView
            );
        }
        else if (dlgParent instanceof JFrame) {
            optionDlg = new ParserOptionDialog(
                    (JFrame) dlgParent,
                    this,
                    expressionDataView
            );
        }
        else {
            optionDlg = new ParserOptionDialog(
                    (JFrame) null,
                    this,
                    expressionDataView
            );
        }
        ATKGraphicsUtils.centerDialog(optionDlg);
        optionDlg.setVisible(true);
        boolean ok = optionDlg.isValidated;
        int selectedAxis = optionDlg.selectedAxis;
        if (ok) {
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
     *            The String representing your expression. It must not be null.
     *            Example: "cos(x1) + 2*sin(x2)/exp(x3)"
     * @param expressionDataView
     *            The JLDataView in which you want to put your expression
     *            evaluation result. It can be null. In this case, a JLDataView
     *            is automatically created.
     * @param selectedAxis
     *            The axis on which you want to put your DataView. It can be
     *            <code>X_AXIS</code>, <code>Y1_AXIS</code> or
     *            <code>Y2_AXIS</code>
     * @param variables
     *            A String[] representing the dataview names associated with
     *            your variables in order of the variables index. Example : You
     *            have two variables x1 and x2 in your expression. x1 is
     *            associated with the JLDataView named "theCurve", and x2 with
     *            the JLDataView named "theBar". Then, variables must be
     *            {"theCurve", "theBar"}.
     * @param x
     *            A boolean to know whether your expression looks like "f(x)".
     *            If your expression looks like "f(x1,...,xn)" then set x to
     *            <code>false</code>. If it looks like "f(x)", set x to
     *            <code>true</code>.
     * @see #X_AXIS
     * @see #Y1_AXIS
     * @see #Y2_AXIS
     * @return The JLDataView used to draw the expression result (the one given
     *         in parameter if not null, the automatically created one
     *         otherwise). Returns <code>null</code> if you try to put an
     *         expression on x axis when it is not allowed
     */
    public JLDataView applyExpressionToChart (String expression,
            JLDataView expressionDataView, int selectedAxis,
            String[] variables, boolean x) {

        JLDataView resultView;

        if ( selectedAxis == X_AXIS && !isCanPutExpressionOnX() ) {
            return null;
        }

        synchronized(expressionMap) {

            //--Cleaning when necessary--//
            if ( expressionDataView != null
                    && expressionMap.containsKey(expressionDataView) ) {
                Object[] parameters = expressionMap.get(expressionDataView);
                Integer axis = (Integer) parameters[0];
                switch(axis.intValue()) {
                    case X_AXIS :
                        getXAxis().removeDataView(expressionDataView);
                        break;
                    case Y1_AXIS :
                        getY1Axis().removeDataView(expressionDataView);
                        break;
                    case Y2_AXIS :
                        getY2Axis().removeDataView(expressionDataView);
                        break;
                }
                expressionMap.remove(expressionDataView);
                expressionDataView.reset();
            }
            //--END Cleaning when necessary--//

            //--Registering Expression View--//
            resultView = applyExpression( expression,
                    expressionDataView, variables, x );
            Object[] parameters = new Object[variables.length + 3];
            parameters[0] = new Integer(selectedAxis);
            parameters[1] = expression;
            parameters[2] = new Boolean(x);
            for (int i = 0; i < variables.length; i++) {
                parameters[i+3] = variables[i];
            }
            expressionMap.put(resultView, parameters);
            //--END Registering Expression View--//

        } // end synchronized(expressionMap)

        //--Adding Expression View to Axis--//
        switch(selectedAxis) {
            case X_AXIS:
                getXAxis().addDataView(resultView);
                if ( !getXAxis().isVisible() ) {
                    getXAxis().setVisible(true);
                    getXAxis().setAutoScale(true);
                }
                break;
            case Y1_AXIS:
                getY1Axis().addDataView(resultView);
                if ( !getY1Axis().isVisible() ) {
                    getY1Axis().setVisible(true);
                    getY1Axis().setAutoScale(true);
                }
                break;
            case Y2_AXIS:
                getY2Axis().addDataView(resultView);
                if ( !getY2Axis().isVisible() ) {
                    getY2Axis().setVisible(true);
                    getY2Axis().setAutoScale(true);
                }
                break;
        }
        //--END Adding Expression View to Axis--//
        repaint();
        return resultView;
    }

    //Calculates the expression dataView.
    //Can be usefull to update an already existing expression dataview.
    //If you want to do so, you should clean your dataview first.
    protected JLDataView applyExpression (String expression,
            JLDataView expressionDataView, String[] variables, boolean x) {

        JLDataView resultView = null;

        Vector<JLDataView> views = prepareViews();

        ExpressionParser parser = new ExpressionParser(
                variables.length,
                expression
        );
        parser.setX(x);
        parser.setPrecision( getTimePrecision() );
        for (int i = 0; i < variables.length; i++) {
            for (int v = 0; v < views.size(); v++) {
                if ( variables[i].equals( views.get(v).getName() ) ) {
                    parser.add( i, (JLDataView) views.get(v) );
                    break;
                }
            }
        }
        views.clear();
        views = null;

        resultView = parser.buildDataView(expressionDataView);
        parser.clean();
        parser = null;

        return resultView;
    }

    public Vector<JLDataView> prepareViews() {
        Vector<JLDataView> views = new Vector<JLDataView>();
        if ( getXAxis().isXY() && isUseXViewsWithExpressions() ) {
            views.addAll( getXAxis().getViews() );
        }
        views.addAll( getY1Axis().getViews() );
        views.addAll( getY2Axis().getViews() );

        return views;
    }

    public synchronized void clearExpressions() {
        Set<JLDataView> keySet = expressionMap.keySet();
        Iterator<JLDataView> keyIterator = keySet.iterator();
        while (keyIterator.hasNext()) {
            JLDataView data = keyIterator.next();
            Object[] parameters = expressionMap.get(data);
            Integer axis = (Integer)parameters[0];
            switch(axis.intValue()) {
                case X_AXIS :
                    getXAxis().removeDataView(data);
                    break;
                case Y1_AXIS :
                    getY1Axis().removeDataView(data);
                    break;
                case Y2_AXIS :
                    getY2Axis().removeDataView(data);
                    break;
            }
        }
        expressionMap.clear();
    }

    // Tries to update all the expression JLDataViews associated with the
    // JLDataView given in parameter
    protected synchronized void updateExpressions(JLDataView view) {
        Vector<JLDataView> expressions = new Vector<JLDataView>();
        expressions.addAll( expressionMap.keySet() );
        for (int i = 0; i < expressions.size(); i++) {
            JLDataView expressionView = expressions.get(i);
            Object[] parameters = expressionMap.get(expressionView);
            if (parameters != null) {
                for (int j = 3; j < parameters.length; j++) {
                    if (parameters[j].equals( view.getName() )) {
                        String expression = (String)parameters[1];
                        Boolean x = (Boolean)parameters[2];
                        String[] variables = new String[parameters.length - 3];
                        for (int k = 0; k < variables.length; k++) {
                            variables[k] = (String)parameters[k+3];
                        }
                        // Commit Change
                        expressionView.reset();
                        applyExpression(
                                expression,
                                expressionView,
                                variables,
                                x.booleanValue()
                        );
                        updateExpressions(expressionView);
                        break;
                    }
                }
            }
        }
    }

    public void actionPerformed (JLChartActionEvent evt) {
        if ( EVALUATE_EXPRESSION.equals( evt.getName() ) ) {
            showExpressionDialog();
        }
    }

    public boolean getActionState (JLChartActionEvent evt) {
        return false;
    }

    public String getConfiguration() {
      String to_write = super.getConfiguration();

      to_write += "expressions:" + expressionMap.size() + "\n";
      Set<JLDataView> keySet = expressionMap.keySet();
      Iterator<JLDataView> keyIterator = keySet.iterator();
      int i = 0;
      while (keyIterator.hasNext()) {
          JLDataView keyView = keyIterator.next();
          Object[] expressionData = expressionMap.get( keyView );
          to_write += "expression_" + i + "_name:\'"
                    + keyView.getName() + "\'\n";
          to_write += "expression_" + i + "_axis:"
                    + ( (Integer) expressionData[0] ).intValue() + "\n";
          to_write += "expression_" + i + "_expression:\'"
                    + ( (String) expressionData[1] ) + "\'\n";
          to_write += "expression_" + i + "_isX:"
                    + ( (Boolean) expressionData[2] ).booleanValue() + "\n";
          to_write += "expression_" + i + "_variables:"
                    + (expressionData.length - 3) + "\n";
          for (int j = 0; j < expressionData.length - 3; j++) {
              to_write += "expression_" + i + "_variable_" + j + ":\'"
                        + ((String)expressionData[j + 3]) + "\'\n";
          }
          to_write += keyView.getConfiguration( "expression_" + i );
          i++;
      }

      return to_write;
    }

    public void applyConfiguration(CfFileReader f) {

        super.applyConfiguration( f );
 
        Vector p;

        // Expression settings
        p = f.getParam("expressions");
        int expressionSize = 0;
        if (p != null) {
            expressionSize = OFormat.getInt(p.get(0).toString());
        }
        for (int i = 0; i < expressionSize; i++) {

            JLDataView expressionView = new JLDataView();
            expressionView.applyConfiguration( "expression_" + i, f );

            p = f.getParam( "expression_" + i + "_name" );
            if (p != null) {
                expressionView.setName( p.get( 0 ).toString() );
            }

            int axis = -1;
            p = f.getParam( "expression_" + i + "_axis" );
            if (p != null) {
                axis = OFormat.getInt(p.get(0).toString());
            }

            String expression = "";
            p = f.getParam( "expression_" + i + "_expression" );
            if (p != null) {
                expression = p.get( 0 ).toString();
            }

            boolean x = false;
            p = f.getParam( "expression_" + i + "_isX" );
            if (p != null) {
                x = OFormat.getBoolean(p.get(0).toString());
            }

            int variablesCount = 0;
            p = f.getParam( "expression_" + i + "_variables" );
            if (p != null) {
                variablesCount = OFormat.getInt(p.get(0).toString());
            }

            String[] variables = new String[variablesCount];
            for (int j = 0; j < variablesCount; j++) {
                String variable = "";
                p = f.getParam( "expression_" + i + "_variable_" + j );
                if (p != null) {
                    variable = p.get(0).toString();
                }
                variables[j] = new String(variable);
                variable = null;
            }

            if (axis != -1) {
                applyExpressionToChart(
                        expression,
                        expressionView,
                        axis,
                        variables,
                        x
                );
            }
        }
    }

    protected void reset (boolean showConfirmDialog) {
        super.reset(showConfirmDialog);
        expressionMap.clear();
    }

    public static void main (String[] args) {
        final JFrame f = new JFrame();
        final StaticChartMathExpression chart = new StaticChartMathExpression();

        // Initialise chart properties
        chart.setHeaderFont(new Font("Times", Font.BOLD, 18));
        chart.setLabelFont(new Font("Times", Font.BOLD, 12));
        chart.setHeader("Test DataView");

        chart.getXAxis().setAutoScale(true);
        chart.getXAxis().setGridVisible(true);
        chart.getXAxis().setSubGridVisible(true);
        chart.getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
        chart.getY1Axis().setAutoScale(true);
        chart.getY1Axis().setGridVisible(true);
        chart.getY1Axis().setSubGridVisible(true);
        chart.getY2Axis().setVisible(false);
        chart.setUseXViewsWithExpressions(false);
        chart.setCanPutExpressionOnX(false);

        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        }
        else {
            JFileChooser chooser = new JFileChooser(".");
            chooser.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String extension = null;
                    String s = f.getName();
                    int i = s.lastIndexOf('.');
                    if (i > 0 && i < s.length() - 1) {
                        extension = s.substring(i + 1).toLowerCase();
                    }
                    if (extension != null && extension.equals("txt"))
                        return true;
                    return false;
                }

                public String getDescription() {
                    return "text files ";
                }
            });
            chooser.setDialogTitle(
                    "Load Graph Data (Text file with TAB separated fields)"
            );
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                fileName = file.getAbsolutePath();
            }
            else {
                fileName = "";
                System.exit( 0 );
            }
        }

        chart.reset(false);
        chart.loadDataFile(fileName);

        JPanel bot = new JPanel();
        bot.setLayout(new FlowLayout());

        JButton b = new JButton("Exit");
        b.addMouseListener( new MouseAdapter() {
            public void mouseClicked (MouseEvent e) {
                System.exit(0);
            }
        } );

        bot.add(b);

        JButton c = new JButton("Options");
        c.addMouseListener( new MouseAdapter() {
            public void mouseClicked (MouseEvent e) {
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

    /**
     * @return the useXViewsWithExpressions
     */
    public boolean isUseXViewsWithExpressions () {
        return useXViewsWithExpressions;
    }

    /**
     * @param useXViewsWithExpressions the useXViewsWithExpressions to set
     */
    public void setUseXViewsWithExpressions (boolean useXViewsWithExpressions) {
        this.useXViewsWithExpressions = useXViewsWithExpressions;
    }

    /**
     * @return the canSetExpressionOnX
     */
    public boolean isCanPutExpressionOnX () {
        return canPutExpressionOnX;
    }

    /**
     * @param canSetExpressionOnX the canSetExpressionOnX to set
     */
    public void setCanPutExpressionOnX (boolean canSetExpressionOnX) {
        this.canPutExpressionOnX = canSetExpressionOnX;
    }

    @Override
    public void removeDataView (JLDataView view) {
        if (view != null) {
            super.removeDataView(view);
            if ( expressionMap.containsKey(view) ) {
                expressionMap.remove(view);
                view.reset();
                updateExpressions(view);
            }
        }
    }

}

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
import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

class SupportedFunctionsTableModel extends DefaultTableModel
{
    public SupportedFunctionsTableModel ()
    {
        super();
    }

    public int getColumnCount ()
    {
        return 2;
    }

    public int getRowCount ()
    {
        return 21;
    }

    public boolean isCellEditable (int rowIndex, int columnIndex)
    {
        return false;
    }

    public Object getValueAt (int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
        {
            switch(rowIndex)
            {
                case 0 :
                    return "Sine";
                case 1 :
                    return "Cosine";
                case 2 :
                    return "Tangent";
                case 3 :
                    return "Arc Sine";
                case 4 :
                    return "Arc Cosine";
                case 5 :
                    return "Arc Tangent";
                case 6 :
                    return "Arc Tangent (with 2 parameters)";
                case 7 :
                    return "Hyperbolic Sine";
                case 8 :
                    return "Hyperbolic Cosine";
                case 9 :
                    return "Hyperbolic Tangent";
                case 10 :
                    return "Inverse Hyperbolic Sine";
                case 11 :
                    return "Inverse Hyperbolic Cosine";
                case 12 :
                    return "Inverse Hyperblic Tangent";
                case 13 :
                    return "Natural Logarithm";
                case 14 :
                    return "Logarithm base 10";
                case 15 :
                    return "Exponential (e^x)";
                case 16 :
                    return "Absolute Value";
                case 17 :
                    return "Modulus";
                case 18 :
                    return "Square Root";
                case 19 :
                    return "Sum";
                case 20 :
                    return "If";
                default :
                    return null;
            }
        }
        else if (columnIndex == 1)
        {
            switch(rowIndex)
            {
                case 0 :
                    return "sin(x)";
                case 1 :
                    return "cos(x)";
                case 2 :
                    return "tan(x)";
                case 3 :
                    return "asin(x)";
                case 4 :
                    return "acos(x)";
                case 5 :
                    return "atan(x)";
                case 6 :
                    return "atan2(x2, x1)";
                case 7 :
                    return "sinh(x)";
                case 8 :
                    return "cosh(x)";
                case 9 :
                    return "tanh(x)";
                case 10 :
                    return "asinh(x)";
                case 11 :
                    return "acosh(x)";
                case 12 :
                    return "atanh(x)";
                case 13 :
                    return "ln(x)";
                case 14 :
                    return "log(x)";
                case 15 :
                    return "exp(x)";
                case 16 :
                    return "abs(x)";
                case 17 :
                    return "mod(x1, x2) = x1 % x2";
                case 18 :
                    return "sqrt(x)";
                case 19 :
                    return "sum(x1, x2, x3)";
                case 20 :
                    return "if(cond, trueval, falseval)";
                default :
                    return null;
            }
        }
        else return null;
    }

    public String getColumnName (int columnIndex)
    {
        switch(columnIndex)
        {
            case 0 :
                return "Function Name";
            case 1 :
                return "Representation";
            default :
                return null;
        }
    }
}

class SupportedOperatorsTableModel extends DefaultTableModel
{
    public SupportedOperatorsTableModel ()
    {
        super();
    }

    public int getColumnCount ()
    {
        return 2;
    }

    public int getRowCount ()
    {
        return 12;
    }

    public boolean isCellEditable (int rowIndex, int columnIndex)
    {
        return false;
    }

    public Object getValueAt (int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
        {
            switch(rowIndex)
            {
                case 0 :
                    return "Power";
                case 1 :
                    return "Boolean Not";
                case 2 :
                    return "Unary Plus, Unary Minus";
                case 3 :
                    return "Modulus";
                case 4 :
                    return "Division";
                case 5 :
                    return "Multiplication";
                case 6 :
                    return "Addition, Substraction";
                case 7 :
                    return "Less or Equal, More or Equal";
                case 8 :
                    return "Less Than, Greater Than";
                case 9 :
                    return "Equal, Not Equal";
                case 10 :
                    return "Boolean And";
                case 11 :
                    return "Boolean Or";
                default :
                    return null;
            }
        }
        else if (columnIndex == 1)
        {
            switch(rowIndex)
            {
                case 0 :
                    return "^";
                case 1 :
                    return "!";
                case 2 :
                    return "+x, -x";
                case 3 :
                    return "%";
                case 4 :
                    return "/";
                case 5 :
                    return "*";
                case 6 :
                    return "+, -";
                case 7 :
                    return "<=, >=";
                case 8 :
                    return "<, >";
                case 9 :
                    return "!=, ==";
                case 10 :
                    return "&&";
                case 11 :
                    return "||";
                default :
                    return null;
            }
        }
        else return null;
    }

    public String getColumnName (int columnIndex)
    {
        switch(columnIndex)
        {
            case 0 :
                return "Operator Name";
            case 1 :
                return "Representation";
            default :
                return null;
        }
    }
}

public class ParserHelpDialog extends JDialog
{
    protected JLabel[] introLabel;
    protected JLabel operatorLabel, functionLabel;
    protected JTable operatorTable, functionTable;
    protected JScrollPane operatorScrollPane, functionScrollPane;
    protected JPanel mainPanel;
    protected final static Font expressionTitleFont = new Font("Times New Roman", Font.BOLD, 20);

    protected final static Dimension bestDim = new Dimension(610, 745);

    public ParserHelpDialog (JDialog parent)
    {
        super(parent, "About Expressions", true);
        initComponents();
        addComponents();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    protected void initComponents()
    {
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);

        introLabel = new JLabel[6];
        introLabel[0] = new JLabel("How to evaluate an expression:");
        introLabel[0].setBounds(5, 0, 605, 17);
        introLabel[1] = new JLabel("Enter your expression with variable names as parameter");
        introLabel[1].setBounds(5, 22, 605, 17);
        introLabel[2] = new JLabel("Your expression must look like 'f(x)' if there is one variable,");
        introLabel[2].setBounds(5, 39, 605, 17);
        introLabel[3] = new JLabel("or 'f(x1,...,xn)' if there are n variables");
        introLabel[3].setBounds(5, 56, 605, 17);
        introLabel[4] = new JLabel("After that, click on [Generate Variables] button to associate names with your variables.");
        introLabel[4].setBounds(5, 73, 605, 17);
        introLabel[5] = new JLabel("Your expressions will then be evaluated following these supported operators and functions:");
        introLabel[5].setBounds(5, 90, 605, 17);
        operatorLabel = new JLabel("Supported Operators");
        operatorLabel.setForeground(Color.BLUE);
        operatorLabel.setFont(expressionTitleFont);
        operatorLabel.setBounds(5, 110, 605, 25);
        functionLabel = new JLabel("Supported Functions");
        functionLabel.setForeground(Color.BLUE);
        functionLabel.setFont(expressionTitleFont);
        functionLabel.setBounds(5, 360, 605, 25);

        operatorTable = new JTable(new SupportedOperatorsTableModel());
        operatorTable.setEnabled(false);
        operatorTable.getTableHeader().setResizingAllowed(false);
        operatorTable.getTableHeader().setReorderingAllowed(false);
        functionTable = new JTable(new SupportedFunctionsTableModel());
        functionTable.setEnabled(false);
        functionTable.getTableHeader().setResizingAllowed(false);
        functionTable.getTableHeader().setReorderingAllowed(false);

        operatorScrollPane = new JScrollPane(operatorTable);
        operatorScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 211));
        operatorScrollPane.setBounds(5, 140, 605, 211);
        operatorScrollPane.setBackground(Color.WHITE);
        operatorScrollPane.getViewport().setBackground(Color.WHITE);
        functionScrollPane = new JScrollPane(functionTable);
        functionScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 355));
        functionScrollPane.setPreferredSize(new Dimension(500, 355));
        functionScrollPane.setBounds(5, 390, 605, 355);
        functionScrollPane.setBackground(Color.WHITE);
        functionScrollPane.getViewport().setBackground(Color.WHITE);
    }

    protected void addComponents()
    {
        for (int i = 0; i < introLabel.length; i++)
        {
            mainPanel.add(introLabel[i]);
        }
        mainPanel.add(operatorLabel);
        mainPanel.add(operatorScrollPane);
        mainPanel.add(functionLabel);
        mainPanel.add(functionScrollPane);
    }

    protected void initBounds()
    {
        mainPanel.setPreferredSize(bestDim);
        this.setContentPane(new JScrollPane(mainPanel));
        this.setSize(bestDim.width + 30, 600);
        this.setResizable(false);
        int x = getParent().getX() + getParent().getWidth() - (bestDim.width + 50);
        if (x < 0) x = 0;
        int y = getParent().getY() + getParent().getHeight() - (bestDim.height + 50);
        if (y < 0) y = 0;
        this.setLocation(x, y);
    }

    public void setVisible(boolean visible)
    {
        if (visible)
        {
            initBounds();
        }
        super.setVisible(visible);
    }

}

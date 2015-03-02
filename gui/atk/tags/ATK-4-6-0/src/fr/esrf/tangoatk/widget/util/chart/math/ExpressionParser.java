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

import org.nfunk.jep.JEP;

import fr.esrf.tangoatk.widget.util.chart.*;

//based on TabbedLine (from JLChart)
public class ExpressionParser
{
    protected JLDataView[] dv;
    protected DataList[]   dl;
    protected int          anno;
    protected int          precision = 0;
    protected JEP          parser;
    protected String       expression;
    protected boolean      x = false;

    public ExpressionParser (int nb, String expr)
    {
        dv = new JLDataView[nb];
        dl = new DataList[nb];
        parser = new JEP();
        parser.addStandardConstants();
        parser.addStandardFunctions();
        expression = expr;
    }

    public void setPrecision (int milliseconds)
    {
        precision = milliseconds;
    }

    public void add (int id, JLDataView v)
    {
        dv[id] = v;
        dl[id] = v.getData();
    }

    protected double getMinTime ()
    {
        double r = Double.MAX_VALUE;
        for (int i = 0; i < dl.length; i++)
        {
            if ( dl[i] != null )
            {
                if ( dl[i].x < r ) r = dl[i].x;
            }
        }
        return r;
    }

    protected void initialize ()
    {
        if (x)
        {
            parser.addVariable("x", Double.NaN);
        }
        else
        {
            for (int i = 0; i < dv.length; i++)
            {
                parser.addVariable( "x" + (i+1), Double.NaN );
            }
        }
    }

    protected double[] getNextValues ()
    {
        double t0 = getMinTime();
        // Test end of data
        if ( t0 == Double.MAX_VALUE ) return null;
        double[] result = new double[2];
        result[0] = t0;
        if (x)
        {
            if ( dl[0] != null )
            {
                if ( ( dl[0].x >= t0 - precision )
                        && ( dl[0].x <= t0 + precision ) )
                {
                    parser.removeVariable( "x" );
                    parser.addVariable( "x", dl[0].y );
                    dl[0] = dl[0].next;
                }
            }
        }
        else
        {
            for (int i = 0; i < dl.length; i++)
            {
                if ( dl[i] != null )
                {
                    if ( ( dl[i].x >= t0 - precision )
                            && ( dl[i].x <= t0 + precision ) )
                    {
                        parser.removeVariable( "x" + (i+1) );
                        parser.addVariable( "x" + (i+1), dl[i].y );
                        dl[i] = dl[i].next;
                    }
                }
            }
        }
        parser.parseExpression( expression );
        result[1] = parser.getValue();
        return result;
    }

    public JLDataView buildDataView (JLDataView initDv)
    {
        initialize();
        JLDataView dataView = initDv;
        if ( initDv == null )
        {
            dataView = new JLDataView();
            dataView.setName( expression );
        }
        double[] nextValues = this.getNextValues();
        while (nextValues != null)
        {
            dataView.add( nextValues[0], nextValues[1] );
            nextValues = this.getNextValues();
        }
        return dataView;
    }

    public void clean ()
    {
        if ( dv != null )
        {
            for (int i = 0; i < dv.length; i++)
            {
                dv[i] = null;
                dl[i] = null;
            }
            dv = null;
            dl = null;
        }
        parser = null;
        expression = null;
    }

    public boolean isX ()
    {
        return x;
    }

    public void setX (boolean x)
    {
        this.x = x;
    }
}

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
 
package fr.esrf.tangoatk.widget.image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.braju.format.Format;

import fr.esrf.tangoatk.widget.util.JTableRow;
import fr.esrf.tangoatk.widget.util.chart.DataList;
import fr.esrf.tangoatk.widget.util.chart.IJLChartListener;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLChartEvent;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;

public class LineProfilerPanel extends JPanel implements IJLChartListener, ActionListener {

    protected JPanel cfgPanel;
    protected JLChart theGraph;
    protected JLDataView theDataY;
    protected JTableRow theTable = null;
    protected Font panelFont;

    protected JCheckBox tableCheck;
    protected JLabel minLabel;
    protected JTextField minText;
    protected JLabel maxLabel;
    protected JTextField maxText;
    protected JLabel avgLabel;
    protected JTextField avgText;
    protected JLabel stdLabel;
    protected JTextField stdText;

    protected static String[]   colName  = {"Index", "Value"};
    protected static String[][] emptyStr = {{"", ""}};

    public LineProfilerPanel() {

      setLayout(new BorderLayout());

      // -----------------------------------------------
      // Cfg panel
      // -----------------------------------------------
      panelFont = new Font("Dialog", Font.PLAIN, 11);

      cfgPanel = new JPanel();
      cfgPanel.setLayout(null);
      cfgPanel.setPreferredSize(new Dimension(0, 25));
      add(cfgPanel, BorderLayout.SOUTH);

      tableCheck = new JCheckBox("View table");
      tableCheck.setSelected(false);
      tableCheck.setFont(panelFont);
      tableCheck.setBounds(5, 3, 80, 20);
      tableCheck.addActionListener(this);
      cfgPanel.add(tableCheck);

      minLabel = new JLabel("Min");
      minLabel.setFont(panelFont);
      minLabel.setHorizontalAlignment(JLabel.RIGHT);
      minLabel.setBounds(85, 3, 30, 20);
      cfgPanel.add(minLabel);
      minText = new JTextField("");
      minText.setMargin(new Insets(0,0,0,0));
      minText.setFont(panelFont);
      minText.setBounds(120, 3, 60, 20);
      cfgPanel.add(minText);

      maxLabel = new JLabel("Max");
      maxLabel.setFont(panelFont);
      maxLabel.setHorizontalAlignment(JLabel.RIGHT);
      maxLabel.setBounds(180, 3, 30, 20);
      cfgPanel.add(maxLabel);
      maxText = new JTextField("");
      maxText.setMargin(new Insets(0, 0, 0, 0));
      maxText.setFont(panelFont);
      maxText.setBounds(215, 3, 60, 20);
      cfgPanel.add(maxText);

      avgLabel = new JLabel("Avg");
      avgLabel.setFont(panelFont);
      avgLabel.setHorizontalAlignment(JLabel.RIGHT);
      avgLabel.setBounds(275, 3, 30, 20);
      cfgPanel.add(avgLabel);
      avgText = new JTextField("");
      avgText.setMargin(new Insets(0, 0, 0, 0));
      avgText.setFont(panelFont);
      avgText.setBounds(310, 3, 60, 20);
      cfgPanel.add(avgText);

      stdLabel = new JLabel("Std");
      stdLabel.setFont(panelFont);
      stdLabel.setHorizontalAlignment(JLabel.RIGHT);
      stdLabel.setBounds(370, 3, 30, 20);
      cfgPanel.add(stdLabel);
      stdText = new JTextField("");
      stdText.setMargin(new Insets(0, 0, 0, 0));
      stdText.setFont(panelFont);
      stdText.setBounds(405, 3, 60, 20);
      cfgPanel.add(stdText);

      // -----------------------------------------------
      // Graph
      // -----------------------------------------------

      theGraph = new JLChart();
      theGraph.setBorder(new javax.swing.border.EtchedBorder());
      theGraph.getXAxis().setAutoScale(true);
      theGraph.getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
      theGraph.getXAxis().setGridVisible(true);
      theGraph.getXAxis().setLabelFormat(JLAxis.DECINT_FORMAT);

      theDataY = new JLDataView();
      theGraph.getY1Axis().setAutoScale(true);
      theGraph.getY1Axis().addDataView(theDataY);
      theGraph.getY1Axis().setGridVisible(true);
      theGraph.setPreferredSize(new Dimension(600, 400));
      theGraph.setMinimumSize(new Dimension(600, 400));
      theGraph.setHeaderFont(new Font("Dialog",Font.BOLD,18));
      theGraph.setJLChartListener(this);
      add(theGraph, BorderLayout.CENTER);


      theTable = new JTableRow();
      theTable.setPreferredSize(new Dimension(170, 0));
      theTable.setVisible(false);
      add(theTable, BorderLayout.EAST);

    }


    private void refreshTable() {

      if (theTable.isVisible()) {

        colName[0] = theGraph.getXAxis().getName();
        colName[1] = theGraph.getY1Axis().getName();

        String[][] dv;
        if( theDataY.getDataLength() > 0 ) {
          dv = new String[theDataY.getDataLength()][2];
          DataList dly = theDataY.getData();

          for (int i = 0; i < theDataY.getDataLength(); i++) {
            dv[i][0] = Integer.toString((int)dly.x);
            dv[i][1] = Double.toString(dly.y);
            dly = dly.next;
          }
        } else {
          dv = emptyStr;
        }
        theTable.setData(dv,colName);
      }

    }

    public void setData(double[] v, int startIndexing) {

      theDataY.reset();

      if (v != null) {

        double sum  = 0.0;
        double sum2 = 0.0;
        double avg  = 0.0;
        double std  = 0.0;
        double lgth = (double)v.length;

        for (int i = 0; i < v.length; i++) {
          theDataY.add((double) (i + startIndexing), v[i]);
          sum  += v[i];
        }
        avg = sum/lgth;

        for (int i = 0; i < v.length; i++) {
          sum2  += (v[i]-avg)*(v[i]-avg);
        }
        std = Math.sqrt(sum2/lgth);

        minText.setText(Double.toString(theDataY.getMinimum()));
        maxText.setText(Double.toString(theDataY.getMaximum()));
        Double avgD = new Double(avg);
        Double stdD = new Double(std);
        avgText.setText(Format.sprintf("%.2f",new Double[]{avgD}));
        stdText.setText(Format.sprintf("%.2f",new Double[]{stdD}));

      } else {

        minText.setText("");
        maxText.setText("");
        avgText.setText("");
        stdText.setText("");
        
      }

      theGraph.repaint();
      refreshTable();
    }

    // -------------------------------------------------------------
    // Action listener
    // -------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == tableCheck) {
        theTable.setVisible(tableCheck.isSelected());
        refreshTable();
        revalidate();
      }
    }

    // -------------------------------------------------------------
    // Chart listener
    // -------------------------------------------------------------
    public String[] clickOnChart(JLChartEvent evt) {

      String[] ret = new String[2];
      ret[0] = theGraph.getXAxis().getName() + " = " + evt.getTransformedXValue();
      ret[1] = theDataY.getName() + " = " + evt.getTransformedYValue();

      return ret;
    }


    public JLChart getChart() {
      return theGraph;
    }

}

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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.esrf.tangoatk.widget.attribute.NumberImageViewer;
import fr.esrf.tangoatk.widget.util.ATKFormat;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.JTableRow;
import fr.esrf.tangoatk.widget.util.chart.DataList;
import fr.esrf.tangoatk.widget.util.chart.IJLChartListener;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLChartEvent;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;

public class LineProfilerPanel extends JPanel implements IJLChartListener, ActionListener, ChangeListener {

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
    protected String userFormat="";
    protected JLabel srcLabel=null;
    protected JTextField srcText=null;
    protected JSpinner srcSpin=null;

    protected static String[]   colName  = {"Index", "Value"};
    protected static String[][] emptyStr = {{"", ""}};

    private NumberImageViewer parent;
    private int id;
    private boolean isSpinEdit;

    public LineProfilerPanel() {
      this(null);
    }

    public LineProfilerPanel(NumberImageViewer parent) {

      this.parent = parent;

      setLayout(new BorderLayout());

      // -----------------------------------------------
      // Cfg panel
      // -----------------------------------------------
      panelFont = new Font("Dialog", Font.PLAIN, 11);

      cfgPanel = new JPanel();
      cfgPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
      add(cfgPanel, BorderLayout.SOUTH);

      tableCheck = new JCheckBox("View table");
      tableCheck.setSelected(false);
      tableCheck.setFont(panelFont);
      tableCheck.addActionListener(this);
      cfgPanel.add(tableCheck);

      minLabel = new JLabel("Min");
      minLabel.setFont(panelFont);
      minLabel.setHorizontalAlignment(JLabel.RIGHT);
      cfgPanel.add(minLabel);
      minText = new JTextField("");
      minText.setPreferredSize(new Dimension(60,20));
      minText.setMargin(new Insets(0, 0, 0, 0));
      minText.setFont(panelFont);
      cfgPanel.add(minText);

      maxLabel = new JLabel("Max");
      maxLabel.setFont(panelFont);
      maxLabel.setHorizontalAlignment(JLabel.RIGHT);
      cfgPanel.add(maxLabel);
      maxText = new JTextField("");
      maxText.setPreferredSize(new Dimension(60,20));
      maxText.setMargin(new Insets(0, 0, 0, 0));
      maxText.setFont(panelFont);
      cfgPanel.add(maxText);

      avgLabel = new JLabel("Avg");
      avgLabel.setFont(panelFont);
      avgLabel.setHorizontalAlignment(JLabel.RIGHT);
      cfgPanel.add(avgLabel);
      avgText = new JTextField("");
      avgText.setPreferredSize(new Dimension(60,20));
      avgText.setMargin(new Insets(0, 0, 0, 0));
      avgText.setFont(panelFont);
      cfgPanel.add(avgText);

      stdLabel = new JLabel("Std");
      stdLabel.setFont(panelFont);
      stdLabel.setHorizontalAlignment(JLabel.RIGHT);
      cfgPanel.add(stdLabel);
      stdText = new JTextField("");
      stdText.setPreferredSize(new Dimension(60,20));
      stdText.setMargin(new Insets(0, 0, 0, 0));
      stdText.setFont(panelFont);
      cfgPanel.add(stdText);

      if(parent!=null) {
        srcLabel = new JLabel("Source");
        srcLabel.setPreferredSize(new Dimension(60, 20));
        srcLabel.setFont(panelFont);
        srcLabel.setHorizontalAlignment(JLabel.RIGHT);
        cfgPanel.add(srcLabel);
        srcText = new JTextField("");
        srcText.setEditable(true);
        srcText.setPreferredSize(new Dimension(120, 20));
        srcText.setMargin(new Insets(0, 0, 0, 0));
        srcText.setFont(panelFont);
        srcText.addActionListener(this);
        cfgPanel.add(srcText);
        srcSpin = new JSpinner(new SpinnerNumberModel(0,0,65536,1));
        srcSpin.setVisible(false);
        srcSpin.setPreferredSize(new Dimension(120, 20));
        srcSpin.addChangeListener(this);
        cfgPanel.add(srcSpin);
      }

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
      id = 1;
      isSpinEdit = false;
    }

    public void setId(int id) {
      this.id = id;
    }

    public void setSource(String label,Object value) {
      if(srcLabel!=null) {
        srcLabel.setText(label);
      }

      if(value instanceof String) {
        if(srcText!=null) {
          if(!srcText.hasFocus())
            srcText.setText((String)value);
          srcSpin.setVisible(false);
          srcText.setVisible(true);
        }
      }

      if(value instanceof Integer) {
        if(srcSpin!=null)
          if(!srcSpin.hasFocus()) {
            isSpinEdit = true;
            srcSpin.setValue(value);
            isSpinEdit = false;
          }
        srcSpin.setVisible(true);
        srcText.setVisible(false);
      }

    }

    public void setFormat(String format) {
      userFormat = format;
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
        minText.setCaretPosition(0);
        maxText.setText(Double.toString(theDataY.getMaximum()));
        maxText.setCaretPosition(0);
        Double avgD = new Double(avg);
        Double stdD = new Double(std);
        avgText.setText(String.format("%.2f",avgD));
        avgText.setCaretPosition(0);
        stdText.setText(String.format("%.2f",stdD));
        stdText.setCaretPosition(0);

      } else {

        minText.setText("");
        maxText.setText("");
        avgText.setText("");
        stdText.setText("");
        
      }

      theGraph.repaint();
      refreshTable();
    }

  public void setData(double[] v, double xgain, double xoffset) {

    theDataY.reset();

    if (v != null) {

      double sum  = 0.0;
      double sum2 = 0.0;
      double avg  = 0.0;
      double std  = 0.0;
      double lgth = (double)v.length;

      for (int i = 0; i < v.length; i++) {
        theDataY.add((double)(i)*xgain + xoffset, v[i]);
        sum  += v[i];
      }
      avg = sum/lgth;

      for (int i = 0; i < v.length; i++) {
        sum2  += (v[i]-avg)*(v[i]-avg);
      }
      std = Math.sqrt(sum2/lgth);

      minText.setText(Double.toString(theDataY.getMinimum()));
      minText.setCaretPosition(0);
      maxText.setText(Double.toString(theDataY.getMaximum()));
      maxText.setCaretPosition(0);
      Double avgD = new Double(avg);
      Double stdD = new Double(std);
      avgText.setText(String.format("%.2f",avgD));
      avgText.setCaretPosition(0);
      stdText.setText(String.format("%.2f",stdD));
      stdText.setCaretPosition(0);

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

      Object src = e.getSource();

      if (src == tableCheck) {
        theTable.setVisible(tableCheck.isSelected());
        refreshTable();
        revalidate();
      } else if (src==srcText) {
        parent.setSourceFromProfile(srcText.getText(),id);
      }

    }

    @Override
    public void stateChanged(ChangeEvent e) {

      Object src = e.getSource();

      if (src==srcSpin) {
        if(!isSpinEdit)
          parent.setSourceFromProfile(srcSpin.getValue().toString(),id);
      }

    }

    // -------------------------------------------------------------
    // Chart listener
    // -------------------------------------------------------------
    public String[] clickOnChart(JLChartEvent evt) {

      String[] ret = new String[2];
      ret[0] = theGraph.getXAxis().getName() + " = " + evt.getTransformedXValue();

      if( userFormat.length()>0 ) {
        ret[1] = theGraph.getY1Axis().getName() + " = " + ATKFormat.format(userFormat, evt.getTransformedYValue());
      } else {
        ret[1] = theGraph.getY1Axis().getName() + " = " + evt.getTransformedYValue();
      }

      return ret;
    }


    public JLChart getChart() {
      return theGraph;
    }



}

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
 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fr.esrf.tangoatk.widget.util.chart.*;


public class ChartExample3 extends JFrame {


  public ChartExample3() {

    final JLChart chart = new JLChart();
    final JLDataView xData;
    final JLDataView yData1;
    final double startTime;

    // Initialise chart properties
    chart.setHeaderFont(new Font("Times", Font.BOLD, 18));
    chart.setHeader("Real time XY Monitoring");
    chart.setDisplayDuration(60000);

    // Initialise axis properties
    chart.getY1Axis().setName("X value");
    chart.getY1Axis().setAutoScale(true);

    chart.getXAxis().setAutoScale(true);
    chart.getXAxis().setName("Y value");

    // Build dataviews

    yData1 = new JLDataView();
    yData1.setName("XY plot1");
    yData1.setColor(new Color(200, 0, 0));
    yData1.setMarkerColor(new Color(100, 0, 0));
    yData1.setLineWidth(1);
    yData1.setMarker(JLDataView.MARKER_DOT);
    yData1.setMarkerSize(4);
    chart.getY1Axis().addDataView(yData1);

    xData = new JLDataView();
    xData.setName("X view");
    chart.getXAxis().addDataView(xData);

    startTime = (double) (System.currentTimeMillis()) / 1000.0;

    // Update thread

    new Thread() {
      public void run() {
        while (true) {

          double now = (double) (System.currentTimeMillis()) / 1000.0;
          double t1 = 2 * Math.PI * ((now - startTime) / 30.0);
          double t2 = 2 * Math.PI * ((now - startTime) / 300.0);

          // Use the addData method
          chart.addData(xData, now * 1000.0, Math.sin(t1) * Math.sin(t2));
          chart.addData(yData1, now * 1000.0, Math.sin(t1 + 2.0) * Math.sin(t2));

          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }

        }
      }
    }.start();

    // GUI

    JPanel bot = new JPanel();
    bot.setLayout(new FlowLayout());

    JButton b = new JButton("Exit");
    b.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        System.exit(0);
      }
    });

    bot.add(b);

    JButton c = new JButton("Graph options");
    c.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        chart.showOptionDialog();
      }
    });

    bot.add(c);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(chart, BorderLayout.CENTER);
    getContentPane().add(bot, BorderLayout.SOUTH);

    setSize(640, 480);
    setTitle("Chart Example 3");
    setVisible(true);

  }


  public static void main(String[] args) {
    final ChartExample3 f = new ChartExample3();
  }


}

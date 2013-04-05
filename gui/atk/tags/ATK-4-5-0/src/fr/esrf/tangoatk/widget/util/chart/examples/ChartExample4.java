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


public class ChartExample4 extends JFrame implements IJLChartListener {

  final JLDataView yData;
  
  public ChartExample4() {

    final JLChart chart = new JLChart();
    final int nbValue=50;
    
    // Initialise chart properties
    chart.setHeaderFont(new Font("Times", Font.BOLD, 18));
    chart.setHeader("Spectrum Monitoring");
    chart.setJLChartListener(this); // To customize the value popup

    // Initialise axis properties
    chart.getY1Axis().setName("dB");
    chart.getY1Axis().setAutoScale(true);

    chart.getXAxis().setAutoScale(true);
    chart.getXAxis().setName("Frequency (Hz)");
    chart.getXAxis().setAnnotation(JLAxis.VALUE_ANNO);

    // Build dataviews
        
    yData=new JLDataView();
    yData.setName("Amplitude");
    yData.setUnit("dB");
    yData.setColor(new Color(200,0,0));
    yData.setLineWidth(2);
    
    chart.getY1Axis().addDataView(yData);
    
    // Update thread
        
    new Thread() {
      public void run() {
        while( true ) {
	
	  yData.reset();
	  
	  for(int i=0;i<nbValue;i++) {	  
	    double v = 2*Math.PI*( (double)i/20.0  );
	    yData.add( (double)i*50.0 , 
	                Math.abs(
			  Math.sin(v)*
			  Math.exp(-(double)i/10.0)*
			  (1.0 + Math.random()/5.0) )
		      );	  
	  }
	  
	  // Commit change
	  chart.repaint();
	      
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

    setSize(640,480);
    setTitle("Chart Example 4");
    setVisible(true);
  
  }
  
  // Customize the value popup
  public String[] clickOnChart(JLChartEvent e) {  
     String[] ret = new String[2];
     ret[0] = "Frequency= " + e.getTransformedXValue() + " Hz";
     ret[1] = "Amplitude= " + e.getTransformedYValue() + " " + yData.getUnit();
     return ret;    
  }

  public static void main(String[] args) {
    final ChartExample4 f = new ChartExample4();
  }

  
}

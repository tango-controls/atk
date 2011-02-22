import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import fr.esrf.tangoatk.widget.util.chart.*;


public class ChartExample2 extends JFrame {

  
  public ChartExample2() {
  
    final JLChart chart = new JLChart();
    final JLDataView temperature;
    final JLDataView humidity;
    
    // Initialise chart properties
    chart.setHeaderFont(new Font("Times", Font.BOLD, 18));
    chart.setHeader("Real Time Monitoring");
    chart.setDisplayDuration(3600000);

    // Initialise axis properties
    chart.getY1Axis().setName("deg C");
    chart.getY1Axis().setAutoScale(false);
    chart.getY1Axis().setMinimum(0.0);
    chart.getY1Axis().setMaximum(40.0);

    chart.getY2Axis().setName("%");
    chart.getY2Axis().setAutoScale(false);
    chart.getY2Axis().setMinimum(0.0);
    chart.getY2Axis().setMaximum(100.0);

    chart.getXAxis().setAutoScale(true);
    chart.getXAxis().setName("Time");

    // Build dataviews
        
    temperature=new JLDataView();
    temperature.setName("Temperature");
    temperature.setUnit("deg C");
    temperature.setColor(new Color(200,0,0));
    temperature.setLineWidth(2);
    chart.getY1Axis().addDataView(temperature);
    
    humidity=new JLDataView();
    humidity.setName("Humidity");
    humidity.setUnit("%");
    humidity.setColor(Color.blue);
    humidity.setLineWidth(2);
    chart.getY2Axis().addDataView(humidity);

    // Update thread
    // Simulate temperature and humidity reading every 2 sec
    
    new Thread() {
      public void run() {
        while( true ) {
	
	  double time = (double)(System.currentTimeMillis());	      
	  double t = Math.random()*1.0 + 24.0;
	  double h = Math.random()*5.0 + 50.0;
	  
	  // Use the addData method.
	  chart.addData(temperature,time,t);
	  chart.addData(humidity,time,h);
      
          try {
            Thread.sleep(2000);
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
    setTitle("Chart Example 2");
    setVisible(true);
  
  }
      

  public static void main(String[] args) {
    final ChartExample2 f = new ChartExample2();
  }

  
}

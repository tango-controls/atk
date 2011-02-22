package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.widget.util.chart.JLAxis;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;

/** A labeled color gradient viewer (vertical orientation only) */
public class JGradientViewer extends JComponent {

  private Gradient gradient;
  private int[] colorMap;
  private JLAxis axis;

  private int barWidth=20;

  /**
   * Construct a default black and white gradient.
   */
  public JGradientViewer() {
    setLayout(null);
    setBorder(null);
    setOpaque(true);
    gradient = new Gradient();
    gradient.buidColorGradient();
    colorMap = gradient.buildColorMap(256);
    axis = new JLAxis(this,JLAxis.VERTICAL_RIGHT);
    axis.setAutoScale(false);
    axis.setAnnotation(JLAxis.VALUE_ANNO);
    axis.setMinimum(0.0);
    axis.setMaximum(100.0);
  }

  /**
   * Return the current gradient.
   * @see #setGradient
   */
  public Gradient getGradient() {
    return gradient;
  }

  /**
   * Sets the gradient to be displayed.
   * @param gradient Gradient object
   */
  public void setGradient(Gradient gradient) {
    this.gradient = gradient;
    colorMap = this.gradient.buildColorMap(256);
  }

  /**
   * Returns a Handle to the axis.
   */
  public JLAxis getAxis() {
    return axis;
  }

  /** Returns the current bar width */
  public int getBarWidth() {
    return barWidth;
  }

  /** Sets the bar thickness. */
  public void setBarWidth(int barWidth) {
    this.barWidth = barWidth;
  }

  public void paint(Graphics g) {

    Dimension d = getSize();

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, d.width, d.height);
    }

    int bw,startX = 0;
    boolean axisVisible = false;

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    FontRenderContext frc = g2.getFontRenderContext();

    if (d.height <= 20 || d.width <= 0)
      return;

    axis.measureAxis(g, frc, 0, d.height - 21);

    if (d.width < barWidth) {
      bw = d.width;
      startX = 0;
    } else {
      bw = barWidth;
      if (d.width < barWidth + axis.getThickness()) {
        startX = (d.width - barWidth) / 2;
      } else {
        startX = (d.width - (barWidth + axis.getThickness())) / 2;
        axisVisible = true;
      }
    }

    double r = 256.0 / (double) (d.height - 20);
    for (int i = 10; i < d.height - 10; i++) {
      int id = (int) (r * (double) (i - 10));

      if (id <= 255)
        g.setColor(new Color(colorMap[255 - id]));
      else
        g.setColor(new Color(colorMap[0]));

      g.drawLine(startX, i, startX + bw, i);
    }

    if (axisVisible)
      axis.paintAxisDirect(g, frc, startX + bw, 10, Color.BLACK, 0, 0);




  }

  public Dimension getMinimumSize() {
    return new Dimension(0,20);
  }

  public static void main(String args[]) {
    final JFrame f = new JFrame();
    final JGradientViewer gv = new JGradientViewer();
    gv.setPreferredSize(new Dimension(50,200));
    gv.getAxis().setMinimum(1e-9);
    gv.getAxis().setMaximum(1e-6);
    gv.getAxis().setScale(JLAxis.LOG_SCALE);
    f.setContentPane(gv);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);
  }

}

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/** A light weigth viewer which display a number scalar attribute on a level meter.
 * Here is an example of use:
 * <p>
 * <pre>
 * fr.esrf.tangoatk.core.AttributeList attributeList = new
 *    fr.esrf.tangoatk.core.AttributeList();
 * ScalarLevelMeterViewer slmv = new ScalarLevelMeterViewer();
 * INumberScalar model = (INumberScalar) attributeList.add("jlp/test/1/att_quatre");
 * slmv.setModel(model);
 * attributeList.startRefresher();
 * </pre>
 */

class LabelItem {

  LabelItem(double x,String label) {
    this.x = x;
    this.label = label;
  }

  public double x;
  public String label;

}

public class ScalarLevelMeterViewer extends JComponent implements INumberScalarListener {

  private INumberScalar numberModel = null;
  private double min;
  private double max;
  private double value = Double.NaN;
  private Color  viewBackground = Color.WHITE;
  private int nbDiv = 30;
  private Vector<LabelItem> labels;
  private boolean drawGradient=true;

  public ScalarLevelMeterViewer() {

    setLayout(null);
    setOpaque(true);
    setPreferredSize(new Dimension(200,100));
    min = 0.0;
    max = 100.0;
    labels = new Vector<LabelItem>();
    setFont(new java.awt.Font("Dialog", Font.PLAIN, 10));

    // default labels
    addLabel(0.0, "0%");
    addLabel(0.25, "25%");
    addLabel(0.5, "50%");
    addLabel(0.75, "75%");
    addLabel(1.0, "100%");

    numberModel = null;

  }

  /**
   * Sets wether draw or not the gradient background
   * @param draw draw flag
   */
  public void setDrawGradient(boolean draw) {
    this.drawGradient = draw;
  }

  /**
   * Sets the minimum of the level meter
   * @param min Minimum value
   */
  public void setMin(double min) {
    this.min = min;
  }
  public double getMin() {
    return this.min;
  }

  /**
   * Sets the maximum of the level meter
   * @param max Miaxmum value
   */
  public void setMax(double max) {
    this.max = max;
  }
  public double getMax() {
    return this.max;
  }

  /**
   * Sets the view meter background color
   * @param c background color
   */
  public void setViewBackground(Color c) {
    viewBackground = c;
  }

  /**
   * Returns the view meter background color
   */
  public Color getViewBackground() {
    return viewBackground;
  }

  /**
   * Add a label
   * @param x position on the view meter in pourcent)
   * @param label Label to display
   */
  public void addLabel(double x,String label) {
    LabelItem l = new LabelItem(x,label);
    labels.add(l);
  }

  /**
   * Clears label
   */
  public void clearLabel() {
    labels.clear();
  }


  public void numberScalarChange(NumberScalarEvent evt) {
    value = evt.getValue();
    repaint();
  }

  public void stateChange(AttributeStateEvent evt)
  {
  }

  public void errorChange(ErrorEvent evt) {
    value = Double.NaN;
    repaint();
  }

  public void paint(Graphics g) {

    int w = getWidth();
    int h = getHeight();

    if(isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, w, h);
    }

    // Draw the view background
    int nbPoint = (nbDiv+1)*2;
    int xPoint[] = new int[nbPoint];
    int yPoint[] = new int[nbPoint];

    double xCenter = (double)w / 2.0;
    double yCenter = 2.0 * (double)h + 10.0;
    double r = 2.0*(double)h - 20.0;
    double rIn = 2.0*(double)h - (double)h/3.0 - 20.0;
    double rOut = 2.0*(double)h - 10.0;
    double alpha = Math.asin((xCenter - 30.0) / r);
    double deltaA = 2.0*alpha / (double)nbDiv;
    double angle = -alpha;

    for(int i=0;i<=nbDiv;i++) {

      xPoint[i] = (int)(r*Math.sin(angle) + xCenter);
      xPoint[nbPoint-1-i]  = (int)(rIn*Math.sin(angle) + xCenter);

      yPoint[i] = (int)(-r*Math.cos(angle) + yCenter);
      yPoint[nbPoint-1-i]  = (int)(-rIn*Math.cos(angle) + yCenter);

      angle = angle + deltaA;

    }

    Graphics2D g2 = (Graphics2D) g;

    if( drawGradient ) {
      GradientPaint gp =  new GradientPaint(0.0f,0.0f,Color.GREEN,(float)(w-50),1.0f,Color.ORANGE,false);
      g2.setPaint(gp);
    } else {
      g.setColor(viewBackground);
    }
    g.fillPolygon(xPoint,yPoint,nbPoint);

    g.setColor(getForeground());
    g.drawPolygon(xPoint,yPoint,nbPoint);

    // Draw label
    for(int i=0;i<labels.size();i++) {

      angle = alpha * (2.0*labels.get(i).x - 1.0);
      int x1 = (int)(r*Math.sin(angle) + xCenter);
      int y1 = (int)(-r*Math.cos(angle) + yCenter);
      int x2 = (int)(rOut*Math.sin(angle) + xCenter);
      int y2 = (int)(-rOut*Math.cos(angle) + yCenter);
      g.drawLine(x1,y1,x2,y2);

      g.setFont(getFont());
      int ws = ATKGraphicsUtils.measureString(labels.get(i).label,getFont()).width;
      g.drawString(labels.get(i).label,x2-ws/2,y2-3);

    }

    // Draw jauge
    if(!Double.isNaN(value)) {

      if(value<min) {
        angle = -alpha;
      } else if (value>max) {
        angle = alpha;
      } else {
        angle = alpha * (2.0*(value-min)/(max-min) - 1.0);
      }

      Stroke old = g2.getStroke();
      BasicStroke bs = new BasicStroke(2);
      if (bs != null) g2.setStroke(bs);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

      // Draw
      int x1 = (int)(r*Math.sin(angle) + xCenter);
      int y1 = (int)(-r*Math.cos(angle) + yCenter);
      int x2 = (int)(rIn*Math.sin(angle) + xCenter);
      int y2 = (int)(-rIn*Math.cos(angle) + yCenter);
      g.drawLine(x1,y1,x2,y2);

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
      //restore default stroke
      g2.setStroke(old);

    }

    paintBorder(g);

  }

  public void clearModel() {

    if(numberModel!=null) {
      numberModel.removeNumberScalarListener(this);
      numberModel=null;
    }

  }

  public void setModel(INumberScalar model) {

    clearModel();

    if (model != null) {
      numberModel = model;
      numberModel.addNumberScalarListener(this);
      numberModel.refresh();
    }

  }


  /**
   * Test function
   * @param args Not used
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();
    ScalarLevelMeterViewer slmv = new ScalarLevelMeterViewer();
    String attributeName = "jlp/test/1/att_un";
    if (args != null && args.length > 0) {
        attributeName = args[0];
    }
    IAttribute attribute = null;
    try {
        attribute = (IAttribute) attributeList.add(attributeName);
        //attribute = (IAttribute) attributeList.add("tango://pcantares:12345/fp/dev/01#dbase=no"+"/Float_attr");
    }
    catch(Exception e) {
        attribute = null;
    }
    if (attribute instanceof INumberScalar) {
      slmv.setModel( (INumberScalar)attribute );
    } else {
        System.err.println(attributeName + " is not a valid attribute or is not available");
        System.exit(1);
    }
    slmv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    slmv.setForeground(java.awt.Color.BLACK);
    slmv.clearLabel();
    slmv.addLabel(0.0, "-1");
    slmv.addLabel(0.25, "");
    slmv.addLabel(0.5, "0");
    slmv.addLabel(0.75, "");
    slmv.addLabel(1.0, "1");
    slmv.setMin(-1.0);
    slmv.setMax(1.0);
    attribute.refresh();
    JFrame f = new JFrame();
    f.setContentPane(slmv);
    f.pack();
    f.setVisible(true);
    attributeList.startRefresher();

  } // end of main ()


}

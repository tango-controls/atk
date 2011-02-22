import javax.swing.*;
import java.io.IOException;
import java.util.Vector;
import java.awt.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawEditor;
import fr.esrf.tangoatk.widget.util.jdraw.JDObject;
import fr.esrf.tangoatk.widget.util.jdraw.JDAxis;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

public class BarGraph extends JFrame {

  JDrawEditor theGraph;
  JDObject    graphArea;
  JDAxis      graphAxis;
  JDObject[]  allBars = new JDObject[8];
  String[]    barList = {"Cell1","Cell3","Cell4","Cell8","Cell9","Cell10","Cell15","Cell19"};

  public BarGraph() {

    // Creates a JDrawEditor in MODE_PLAY
    theGraph = new JDrawEditor(JDrawEditor.MODE_PLAY);

    // Loads the JDraw file
    try {
      theGraph.loadFile("bargraph.jdw");
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this,e.getMessage(),"Error loading file",JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }

    // Retreives a handle to the graph area.
    // We will use this to repaint the appropriate area on update
    // and to calculate bar position.
    graphArea = getObject("masterView");

    // Gets the axis handle in order
    // to retreive the min and max value.
    graphAxis = (JDAxis)getObject("Axis");

    // Retreives bars handles.
    for(int i=0;i<barList.length;i++)
     allBars[i] = getObject(barList[i]);

    // Update thread, simulates correction reading
    new Thread() {
      public void run() {
        while (true) {

          // Updates bars
          for(int i=0;i<barList.length;i++) {
            double h = Math.random() / 5.0 - 0.1; /* -0.1 to 0.1 */
            updateBar(i,h);
          }

          // Repaints the area
          graphArea.refresh();

          // Sleeps a while
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {}

        }
      }
    }.start();

    // Enables autoZoom
    theGraph.setAutoZoom(true);

    setContentPane(theGraph);
    setTitle("BarGraph");
  }

  /**
   * Returns the JDObject having the given name.
   * @param name Name to search
   */
  public JDObject getObject(String name) {

    Vector objs = theGraph.getObjectsByName(name,false);
    if(objs.size()==0) {
      System.out.print("Error , no object named '" + name + "' found." );
      System.exit(0);
    } else if(objs.size()>1) {
      System.out.print("Warning , more than one object having the name : " + name +" found ,getting first..." );
    }
    return (JDObject)objs.get(0);

  }

  /**
   * Updates the bar according to the given value.
   * @param idx Bar index
   * @param x Bar value (From -0.1 to 0.1)
   */
  public void updateBar(int idx,double x) {

    // Calculates absolute summit coordinates
    // Max => gRect.y
    // Min => gRect.y + gRect.heigth
    Rectangle gRect = graphArea.getBoundRect();
    double y = (double)gRect.y;
    double h = (double)gRect.height;
    double max = graphAxis.getMax();
    double min = graphAxis.getMin();
    double nPos = (min-x)*h / (max-min) + (y+h);
    // Moves the summit (control point) of the shape.
    // Hint: To know which summit to move, you
    // can right click on a summit within the
    // editor and you will see the summit Id.
    allBars[idx].moveSummitV(5,nPos);
    // We do not call refresh() here because repaint
    // will be done by a refresh on the graphArea.

  }

  /**
   * Main function.
   */
  public static void main(String[] args) {
    final BarGraph bg = new BarGraph();
    ATKGraphicsUtils.centerFrameOnScreen(bg);
    bg.setVisible(true);
  }

}

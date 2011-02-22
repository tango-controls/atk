import fr.esrf.tangoatk.widget.util.jdraw.JDrawEditor;
import fr.esrf.tangoatk.widget.util.jdraw.JDLabel;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

import javax.swing.*;

public class HelloWorld extends JFrame {

  JDrawEditor theGraph;
  JDLabel     label;

  public HelloWorld() {
    // Creates a JDrawEditor in MODE_PLAY.
    theGraph = new JDrawEditor(JDrawEditor.MODE_PLAY);
    // Creates a JDLabel
    label = new JDLabel("myLabel","Hello World",5,5);
    // Adds the label to the editor.
    theGraph.addObject(label);
    // Sizes the editor according to the size of the drawing.
    theGraph.computePreferredSize();

    setContentPane(theGraph);
  }

  public static void main(String[] args) {
    final HelloWorld hw = new HelloWorld();
    ATKGraphicsUtils.centerFrameOnScreen(hw);
    hw.setVisible(true);
  }

}

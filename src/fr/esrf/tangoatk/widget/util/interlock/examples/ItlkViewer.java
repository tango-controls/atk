/** An application example that uses the NetEditor */

import fr.esrf.tangoatk.widget.util.interlock.NetEditorListener;
import fr.esrf.tangoatk.widget.util.interlock.NetEditor;
import fr.esrf.tangoatk.widget.util.interlock.NetObject;

import javax.swing.*;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.*;

public class ItlkViewer extends JFrame implements NetEditorListener {

  ItlkNetViewer  itlkNetViewer;   // The viewer

  /** Main constructor */
  public ItlkViewer() {

    // Construct the viewer
    itlkNetViewer = new ItlkNetViewer(this);
    itlkNetViewer.addEditorListener(this);

    // Load the network file
    try {
      itlkNetViewer.loadFile("interlock.net");
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Cannot load interlock.net\n" + ex.getMessage() , "Error", JOptionPane.ERROR_MESSAGE);
    }

    setTitle("Interlock Simulator");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(itlkNetViewer);
    pack();

  }
  
  // -------------------------------------------------------------------
  // The Editor listener
  // -------------------------------------------------------------------
  public void valueChanged(NetEditor src) {}
  public void sizeChanged(NetEditor src,Dimension d) {}
  public void cancelCreate(NetEditor src) {}
  public void linkClicked(NetEditor src,NetObject obj,int childIdx,MouseEvent e) {}
  public void objectClicked(NetEditor src,NetObject obj,MouseEvent e) {
    itlkNetViewer.swapItlkState(obj);
  }

  /** Main function */
  public static void main(String[] args) {

    final ItlkViewer f = new ItlkViewer();
    f.setVisible(true);

  }

}
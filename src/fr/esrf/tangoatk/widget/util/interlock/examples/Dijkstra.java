/** An application example that uses the NetEditor */
import javax.swing.*;
import java.io.IOException;
import java.awt.*;

public class Dijkstra extends JFrame  {

  DjNetViewer  netViewer;   // The viewer

  /** Main constructor */
  public Dijkstra() {

    // Construct the viewer
    netViewer = new DjNetViewer(this);

    // Load the network file
    try {
      netViewer.loadFile("dijkstra.net");
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Cannot load dijkstra.net\n" + ex.getMessage() , "Error", JOptionPane.ERROR_MESSAGE);
    }

    setTitle("Dijkstra");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(netViewer);
    pack();

  }

  /** Main function */
  public static void main(String[] args) {

    final Dijkstra f = new Dijkstra();
    f.setVisible(true);

  }

}
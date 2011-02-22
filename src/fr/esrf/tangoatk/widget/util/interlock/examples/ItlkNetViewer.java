
import fr.esrf.tangoatk.widget.util.interlock.NetEditor;
import javax.swing.*;
import java.io.IOException;

/** A class which override the ItlkNetEditor to build an Interlock Simulator viewer */
public class ItlkNetViewer extends ItlkNetEditor {

  public ItlkNetViewer(JFrame parent) {

    super(parent);
    setEditable(false);

  }

  /** Overload load file to check the root */
  public void loadFile(String fileName) throws IOException {

    super.loadFile(fileName);
    if( getRoot()==null ) {
      clearObjects();
      throw new IOException("No VCC found in this net file.");
    }

  }

}

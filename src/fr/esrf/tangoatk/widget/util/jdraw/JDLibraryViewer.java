package fr.esrf.tangoatk.widget.util.jdraw;

import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Displays a JDraw file in a library view (only selection and clipboard operations allowed).
 * <p>Example of use: (This example shows how to create a custom editor and add
 * a component library)
 * <pre>
 * public class VacEdit extends JDrawEditorFrame {
 *
 *  private JButton libButton;
 *  private JDLibraryViewer libViewer;
 *  private JDrawEditor ed = new JDrawEditor(JDrawEditor.MODE_EDIT);
 *  private JDrawEditor py = new JDrawEditor(JDrawEditor.MODE_PLAY);
 *
 *public VacEdit() {
 *
 *  ed = new JDrawEditor(JDrawEditor.MODE_EDIT);
 *  py = new JDrawEditor(JDrawEditor.MODE_PLAY);
 *
 *  String libPath = System.getProperty("LIBPATH", "null");
 *  if( libPath.equals("null") )
 *   System.out.println("Warning LIBPATH is not defined.");
 *
 *  // Customize the editor
 *  libViewer = new JDLibraryViewer(libPath+"/jvacuum_lib.jdw",ed);
 *  libViewer.setTitle("ESRF vacuum library");
 *  ATKGraphicsUtils.centerFrameOnScreen(libViewer);
 *
 *  libButton = new JButton(new ImageIcon(getClass().getResource("/jvacuum/vac_button.gif")));
 *  libButton.setPressedIcon(new ImageIcon(getClass().getResource("/jvacuum/vac_button_push.gif")));
 *  libButton.setToolTipText("ESRF vacuum library");
 *  libButton.setMargin(new Insets(3,3,3,3));
 *  libButton.setBorder(null);
 *
 *  libButton.addActionListener(this);
 *  editToolBar.add(new JLabel(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/jdraw_separator.gif"))));
 *  editToolBar.add(libButton);
 *
 *  setAppTitle("JVacuum Editor 1.0");
 *  setEditor(ed);
 *  setPlayer(py);
 *
 *}
 *
 *public void actionPerformed(ActionEvent e) {
 *
 *  Object src = e.getSource();
 *  if( src== libButton ) {
 *    libViewer.setVisible(true);
 *  } else {
 *    super.actionPerformed(e);
 *  }
 *
 *}
 *
 *
 *public static void main(String[] args) {
 *
 *  VacEdit v = new VacEdit();
 *  ATKGraphicsUtils.centerFrameOnScreen(v);
 *  v.setVisible(true);
 *
 *}
 *
 *}
 * </pre>
 */
public class JDLibraryViewer extends JFrame implements ActionListener {

  JDrawEditor libViewer;
  JDrawEditor invoker;
  JPanel      controlPanel;
  JButton     copyButton;
  JButton     closeButton;

  public JDLibraryViewer(String libName,JDrawEditor invoker) {

    this.invoker = invoker;
    Container pane = getContentPane();

    pane.setLayout(new BorderLayout());

    // Library view
    libViewer = new JDrawEditor(JDrawEditor.MODE_LIB);
    try {
      libViewer.loadFile(libName);
    } catch(IOException e) {
      System.out.println("Cannot load library:\n" + e.getMessage());
    }
    libViewer.computePreferredSize();
    libViewer.setBorder(BorderFactory.createEtchedBorder());
    pane.add(libViewer,BorderLayout.CENTER);

    // Control panel
    controlPanel=new JPanel();
    copyButton=new JButton("Copy");
    copyButton.setFont(ATKConstant.labelFont);
    copyButton.addActionListener(this);
    closeButton=new JButton("Close");
    closeButton.setFont(ATKConstant.labelFont);
    closeButton.addActionListener(this);
    controlPanel.add(copyButton);
    controlPanel.add(closeButton);
    pane.add(controlPanel,BorderLayout.SOUTH);

  }

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();
    if( src == closeButton ) {
      setVisible(false);
    } else if (src == copyButton) {
      if(invoker!=null)
        invoker.addObjectToClipboard(libViewer.getSelectedObjects());
    }

  }

  public static void main(String[] args) {

    JDLibraryViewer libView = new JDLibraryViewer(
            "Z:/segfs/blcdas/appli/vacuum/xvacuum/LOOX_files/Lib_Xvacuum.g",
            null);
    libView.setTitle("ESRF Vacuum Library");
    libView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ATKGraphicsUtils.centerFrameOnScreen(libView);
    libView.setVisible(true);

  }

}

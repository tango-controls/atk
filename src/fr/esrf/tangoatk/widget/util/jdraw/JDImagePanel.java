/** A panel for JDPolyline private properties */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JDImagePanel extends JPanel implements ActionListener {

  private JTextField filenameLabel;
  private JButton filenameBtn;

  private JLabel  widthLabel;
  private JLabel  heightLabel;
  private JButton resetBtn;

  JDImage[] allObjects;
  JComponent invoker;
  Rectangle oldRect;

  public JDImagePanel(JDImage[] p, JComponent jc) {

    allObjects = p;
    invoker = jc;

    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 300));

    // ------------------------------------------------------------------------------------
    JPanel imgPanel = new JPanel(null);
    imgPanel.setBorder(JDUtils.createTitleBorder("Filename"));
    imgPanel.setBounds(5,5,370,60);

    filenameLabel = new JTextField(p[0].getFileName());
    filenameLabel.setFont(JDUtils.labelFont);
    filenameLabel.setEditable(false);
    filenameLabel.setBorder(null);
    filenameLabel.setCaretPosition(p[0].getFileName().length());
    filenameLabel.setForeground(JDUtils.labelColor);
    filenameLabel.setBounds(10, 20, 310, 25);
    imgPanel.add(filenameLabel);

    filenameBtn = new JButton();
    filenameBtn.setText("...");
    filenameBtn.setMargin(new Insets(0, 0, 0, 0));
    filenameBtn.setFont(JDUtils.labelFont);
    filenameBtn.setBounds(325, 20, 30, 24);
    filenameBtn.addActionListener(this);
    imgPanel.add(filenameBtn);

    add(imgPanel);

    // ------------------------------------------------------------------------------------
    JPanel dimPanel = new JPanel(null);
    dimPanel.setBorder(JDUtils.createTitleBorder("Dimension"));
    dimPanel.setBounds(5,75,370,115);

    widthLabel = new JLabel("Width: " + p[0].getImageWidth());
    widthLabel.setBounds(10, 20, 240, 25);
    widthLabel.setFont(JDUtils.labelFont);
    dimPanel.add(widthLabel);

    heightLabel = new JLabel("Height: " + p[0].getImageHeight());
    heightLabel.setBounds(10, 50, 240, 25);
    heightLabel.setFont(JDUtils.labelFont);
    dimPanel.add(heightLabel);

    resetBtn= new JButton();
    resetBtn.setText("Reset to original size");
    resetBtn.setMargin(new Insets(0, 0, 0, 0));
    resetBtn.setFont(JDUtils.labelFont);
    resetBtn.setBounds(10, 80, 180, 24);
    resetBtn.addActionListener(this);
    dimPanel.add(resetBtn);

    add(dimPanel);

  }

  private void initRepaint() {
    oldRect = allObjects[0].getRepaintRect();
    for (int i = 1; i < allObjects.length; i++)
      oldRect = oldRect.union(allObjects[i].getRepaintRect());
  }

  private void repaintObjects() {
    Rectangle newRect = allObjects[0].getRepaintRect();
    for (int i = 1; i < allObjects.length; i++)
      newRect = newRect.union(allObjects[i].getRepaintRect());
    invoker.repaint(newRect.union(oldRect));
  }


  // ---------------------------------------------------------
  // Action listener
  // ---------------------------------------------------------
  public void actionPerformed(ActionEvent e) {
    int i;
    initRepaint();
    Object src = e.getSource();
    if (src == filenameBtn) {

      JFileChooser chooser = new JFileChooser(".");
      String[] exts={"gif","png","jpg"};
      chooser.addChoosableFileFilter(new JDFileFilter("Image file",exts));
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        for(i=0;i<allObjects.length;i++)
          allObjects[i].setFileName(chooser.getSelectedFile().getAbsolutePath());
        JDUtils.modified=true;
      }

    } else if ( src == resetBtn ) {

      for(i=0;i<allObjects.length;i++)
        allObjects[i].resetToOriginalSize();
      JDUtils.modified=true;

    }
    repaintObjects();
  }



}

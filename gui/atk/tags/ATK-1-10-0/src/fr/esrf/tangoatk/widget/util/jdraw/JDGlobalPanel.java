package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

class JDGlobalPanel extends JPanel implements ActionListener {

  private JLabel backgroundLabel;
  private JButton backgroundButton;

  JDrawEditor invoker;

  public JDGlobalPanel(JDrawEditor jc) {

    invoker = jc;

    setForeground(JDUtils.labelColor);
    setFont(JDUtils.labelFont);
    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(280, 70));

    // -----------------------------------------------------------------
    JPanel colorPanel = new JPanel(null);
    colorPanel.setBorder(JDUtils.createTitleBorder("Colors"));
    colorPanel.setBounds(5, 5, 270, 55);

    backgroundLabel = JDUtils.createLabel("Background");
    backgroundLabel.setBounds(5, 20, 180, 24);
    colorPanel.add(backgroundLabel);
    backgroundButton = new JButton("");
    backgroundButton.setMargin(new Insets(0, 0, 0, 0));
    backgroundButton.setBackground(invoker.getBackground());
    backgroundButton.setForeground(Color.BLACK);
    backgroundButton.addActionListener(this);
    backgroundButton.setBounds(200, 20, 60, 24);
    colorPanel.add(backgroundButton);

    add(colorPanel);

  }

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();

    if (src == backgroundButton) {
      Color c = JColorChooser.showDialog(this, "Choose background color", invoker.getBackground());
      if (c != null) {
        invoker.setBackground(c);
        backgroundButton.setBackground(c);
      }
    }

  }

}

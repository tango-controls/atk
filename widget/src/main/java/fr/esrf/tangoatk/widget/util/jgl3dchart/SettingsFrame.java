/*
 *  Copyright (C) :     2002,2003,2004,2005,2006,2007,2008,2009
 *                      European Synchrotron Radiation Facility
 *                      BP 220, Grenoble 38043
 *                      FRANCE
 *
 *  This file is part of Tango.
 *
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.esrf.tangoatk.widget.util.jgl3dchart;

import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.Gradient;
import fr.esrf.tangoatk.widget.util.JGradientEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class AxisPanel extends JPanel implements ActionListener, KeyListener {

  private JGL3DAxis  axis;
  private JGL3DChart chart;

  private JPanel     scalePanel;
  private JLabel     MinLabel;
  private JTextField MinText;
  private JLabel     MaxLabel;
  private JTextField MaxText;
  private JCheckBox  AutoScaleCheck;

  private JPanel     settingPanel;
  private JCheckBox  VisibleCheck;
  private JComboBox  FormatCombo;
  private JLabel     FormatLabel;
  private JLabel     TitleLabel;
  private JTextField TitleText;
  private JLabel     ColorLabel;
  private JLabel     ColorView;
  private JButton    ColorBtn;
  private JLabel     TickLabel;
  private JTextField TickText;
  private JLabel     TitleOffsetLabel;
  private JTextField TitleOffsetText;

  private JPanel     affTransformPanel;
  private JLabel     offsetLabel;
  private JTextField offsetText;
  private JLabel     gainLabel;
  private JTextField gainText;

  public AxisPanel(JGL3DAxis axis,JGL3DChart chart) {

    this.axis = axis;
    this.chart = chart;
    setLayout(null);

    scalePanel = new JPanel();
    scalePanel.setLayout(null);
    scalePanel.setBorder(Utils.createTitleBorder("Scale"));
    add(scalePanel);

    MinLabel = new JLabel("Min");
    MinLabel.setFont(Utils.labelFont);
    MinText = new JTextField();
    MinLabel.setForeground(Utils.fColor);
    MinLabel.setEnabled(!axis.isAutoScale());
    MinText.setText(Double.toString(axis.getMinimum()));
    MinText.setEditable(true);
    MinText.setEnabled(!axis.isAutoScale());
    MinText.setMargin(Utils.zInset);
    MinText.addKeyListener(this);

    MaxLabel = new JLabel("Max");
    MaxLabel.setFont(Utils.labelFont);
    MaxText = new JTextField();
    MaxLabel.setForeground(Utils.fColor);
    MaxLabel.setHorizontalAlignment(JLabel.RIGHT);
    MaxLabel.setEnabled(!axis.isAutoScale());
    MaxText.setText(Double.toString(axis.getMaximum()));
    MaxText.setEditable(true);
    MaxText.setEnabled(!axis.isAutoScale());
    MaxText.setMargin(Utils.zInset);
    MaxText.addKeyListener(this);

    AutoScaleCheck = new JCheckBox("Auto scale");
    AutoScaleCheck.setFont(Utils.labelFont);
    AutoScaleCheck.setForeground(Utils.fColor);
    AutoScaleCheck.setSelected(axis.isAutoScale());
    AutoScaleCheck.addActionListener(this);

    scalePanel.add(MinLabel);
    scalePanel.add(MinText);
    scalePanel.add(MaxLabel);
    scalePanel.add(MaxText);
    scalePanel.add(AutoScaleCheck);

    MinLabel.setBounds(10, 20, 35, 25);
    MinText.setBounds(50, 20, 90, 25);
    MaxLabel.setBounds(145, 20, 40, 25);
    MaxText.setBounds(190, 20, 90, 25);
    AutoScaleCheck.setBounds(5, 50, 275, 25);
    scalePanel.setBounds(5,10,290,85);

    settingPanel = new JPanel();
    settingPanel.setLayout(null);
    settingPanel.setBorder(Utils.createTitleBorder("Axis settings"));
    add(settingPanel);

    VisibleCheck = new JCheckBox("Visible");
    VisibleCheck.setFont(Utils.labelFont);
    VisibleCheck.setForeground(Utils.fColor);
    VisibleCheck.setSelected(axis.isVisible());
    VisibleCheck.setToolTipText("Display/Hide the axis");
    VisibleCheck.addActionListener(this);

    FormatCombo = new JComboBox();
    FormatCombo.setFont(Utils.labelFont);
    FormatCombo.addItem("Automatic");
    FormatCombo.addItem("Scientific");
    FormatCombo.addItem("Time (hh:mm:ss)");
    FormatCombo.addItem("Decimal int");
    FormatCombo.addItem("Hexadecimal int");
    FormatCombo.addItem("Binary int");
    FormatCombo.addItem("Scientific int");
    FormatCombo.addItem("Date");
    FormatCombo.setSelectedIndex(axis.getLabelFormat());
    FormatCombo.addActionListener(this);

    FormatLabel = new JLabel("Label format");
    FormatLabel.setFont(Utils.labelFont);
    FormatLabel.setForeground(Utils.fColor);

    TitleLabel = new JLabel("Title");
    TitleLabel.setFont(Utils.labelFont);
    TitleLabel.setForeground(Utils.fColor);
    TitleText = new JTextField();
    TitleText.setEditable(true);
    TitleText.setText(axis.getName());
    TitleText.setMargin(Utils.zInset);
    TitleText.addKeyListener(this);

    ColorLabel = new JLabel("Label color");
    ColorLabel.setFont(Utils.labelFont);
    ColorLabel.setForeground(Utils.fColor);
    ColorView = new JLabel("");
    ColorView.setOpaque(true);
    ColorView.setBorder(BorderFactory.createLineBorder(Color.black));
    ColorView.setBackground(axis.getLabelColor());
    ColorBtn = new JButton("...");
    ColorBtn.addActionListener(this);
    ColorBtn.setMargin(Utils.zInset);

    TickLabel = new JLabel("Tick spacing");
    TickLabel.setFont(Utils.labelFont);
    TickLabel.setForeground(Utils.fColor);
    TickText = new JTextField();
    TickText.setEditable(true);
    TickText.setText(Integer.toString(axis.getTickSpacing()));
    TickText.setMargin(Utils.zInset);
    TickText.addKeyListener(this);

    TitleOffsetLabel = new JLabel("Title offset");
    TitleOffsetLabel.setFont(Utils.labelFont);
    TitleOffsetLabel.setForeground(Utils.fColor);
    TitleOffsetText = new JTextField();
    TitleOffsetText.setEditable(true);
    TitleOffsetText.setText(Double.toString(axis.getTitleOffset()));
    TitleOffsetText.setMargin(Utils.zInset);
    TitleOffsetText.addKeyListener(this);

    settingPanel.add(VisibleCheck);
    settingPanel.add(FormatCombo);
    settingPanel.add(FormatLabel);
    settingPanel.add(TitleLabel);
    settingPanel.add(TitleText);
    settingPanel.add(ColorLabel);
    settingPanel.add(ColorView);
    settingPanel.add(ColorBtn);
    settingPanel.add(TickLabel);
    settingPanel.add(TickText);
    settingPanel.add(TitleOffsetLabel);
    settingPanel.add(TitleOffsetText);

    FormatLabel.setBounds(10, 20, 100, 25);
    FormatCombo.setBounds(115, 20, 165, 25);
    TitleLabel.setBounds(10, 50, 100, 25);
    TitleText.setBounds(115, 50, 165, 25);
    ColorLabel.setBounds(10, 80, 100, 25);
    ColorView.setBounds(115, 80, 130, 25);
    ColorBtn.setBounds(250, 80, 30, 25);
    TickLabel.setBounds(10, 110, 100, 25);
    TickText.setBounds(115, 110, 165, 25);
    TitleOffsetLabel.setBounds(10, 140, 100, 25);
    TitleOffsetText.setBounds(115, 140, 165, 25);
    VisibleCheck.setBounds(5, 170, 280, 25);
    settingPanel.setBounds(5,100,290,205);

    affTransformPanel = new JPanel();
    affTransformPanel.setLayout(null);
    affTransformPanel.setBorder(Utils.createTitleBorder("Coordinates transform"));
    add(affTransformPanel);

    offsetLabel = new JLabel("Offset");
    offsetLabel.setFont(Utils.labelFont);
    offsetLabel.setForeground(Utils.fColor);
    offsetText = new JTextField();
    offsetText.setEditable(true);
    offsetText.setText(Double.toString(axis.getOffsetTransform()));
    offsetText.setMargin(Utils.zInset);
    offsetText.addKeyListener(this);

    gainLabel = new JLabel("Gain");
    gainLabel.setFont(Utils.labelFont);
    gainLabel.setForeground(Utils.fColor);
    gainText = new JTextField();
    gainText.setEditable(true);
    gainText.setText(Double.toString(axis.getGainTransform()));
    gainText.setMargin(Utils.zInset);
    gainText.addKeyListener(this);

    affTransformPanel.add(offsetLabel);
    affTransformPanel.add(offsetText);
    affTransformPanel.add(gainLabel);
    affTransformPanel.add(gainText);

    offsetLabel.setBounds(10, 20, 100, 25);
    offsetText.setBounds(115, 20, 165, 25);
    gainLabel.setBounds(10, 50, 100, 25);
    gainText.setBounds(115, 50, 165, 25);
    affTransformPanel.setBounds(5,310,290,90);

  }

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();

    if (src == AutoScaleCheck) {

      boolean b = AutoScaleCheck.isSelected();
      axis.setAutoScale(b);
      MinLabel.setEnabled(!b);
      MinText.setEnabled(!b);
      MaxLabel.setEnabled(!b);
      MaxText.setEnabled(!b);

    } else if (src == FormatCombo) {

      int s = FormatCombo.getSelectedIndex();
      axis.setLabelFormat(s);
      chart.repaint();

    } else if (e.getSource() == ColorBtn) {

      Color c = JColorChooser.showDialog(this, "Choose axis Color", axis.getLabelColor());
      if (c != null) {
        axis.setLabelColor(c);
        ColorView.setBackground(c);
        chart.repaint();
      }

    } else if (e.getSource() == VisibleCheck) {

      axis.setVisible(VisibleCheck.isSelected());
      chart.repaint();

    }

  }

  public void keyPressed(KeyEvent e) {}

   public void keyTyped(KeyEvent e) {}

  public void keyReleased(KeyEvent e) {

    Object src = e.getSource();

    if ((src == MinText || src == MaxText) && !axis.isAutoScale()) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {

        try {

          double min = Double.parseDouble(MinText.getText());
          double max = Double.parseDouble(MaxText.getText());

          if (max <= min) {
            error("Min must be strictly lower than max.");
            return;
          }

          //if (pAxis.getScale() == JLAxis.LOG_SCALE) {
          //  if (min <= 0 || max <= 0) {
          //    error("Min and max must be strictly positive with logarithmic scale.");
          //    return;
          //  }
          //}

          axis.setMinimum(min);
          axis.setMaximum(max);
          MinText.setCaretPosition(0);
          MaxText.setCaretPosition(0);

        } catch (NumberFormatException err) {
          error("Min or Max: malformed number.");
        }

      }

      // ------------------------------------------------------------
    } else if (src == TitleText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        axis.setName(TitleText.getText());
        TitleText.setCaretPosition(0);
        chart.repaint();
      }

    } else if (src == TickText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {

        try {

          int tick = Integer.parseInt(TickText.getText());

          axis.setTickSpacing(tick);
          TickText.setCaretPosition(0);
          chart.repaint();

        } catch (NumberFormatException err) {
          error("Tick spacing, malformed number.");
        }

      }

    } else if (src == TitleOffsetText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {

        try {

          double tOff = Double.parseDouble(TitleOffsetText.getText());

          axis.setTitleOffset(tOff);
          TitleOffsetText.setCaretPosition(0);
          chart.repaint();

        } catch (NumberFormatException err) {
          error("Title offset, malformed number.");
        }

      }

    } else if (src == offsetText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {

        try {

          double Off = Double.parseDouble(offsetText.getText());

          axis.setOffsetTransform(Off);
          offsetText.setCaretPosition(0);

        } catch (NumberFormatException err) {
          error("Offset, malformed number.");
        }

      }

    } else if (src == gainText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {

        try {

          double gain = Double.parseDouble(gainText.getText());

          axis.setGainTransform(gain);
          gainText.setCaretPosition(0);

        } catch (NumberFormatException err) {
          error("Gain, malformed number.");
        }

      }

    }


  }

  private void error(String m) {
     JOptionPane.showMessageDialog(this, m, "Chart options error",
       JOptionPane.ERROR_MESSAGE);
   }

}

/**
 * Panel to edit the colormap
 */
class GradientPanel extends JPanel implements ActionListener {

  private JGL3DChart      chart;
  private JGradientEditor gradViewer;
  private JButton         gradButton;

  public GradientPanel(JGL3DChart chart) {

    this.chart = chart;
    setLayout(null);

    gradViewer = new JGradientEditor();
    gradViewer.setGradient(chart.getGradient());
    gradViewer.setEditable(false);
    gradViewer.setToolTipText("Display the image using this colormap");
    gradViewer.setBounds(10, 20, 240, 20);
    add(gradViewer);

    gradButton = new JButton();
    gradButton.setText("...");
    gradButton.setToolTipText("Edit colormap");
    gradButton.setMargin(new Insets(0, 0, 0, 0));
    gradButton.setBounds(250, 20, 20, 20);
    gradButton.addActionListener(this);
    add(gradButton);

  }

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if( src==gradButton ) {
      showGradientEditor();
    }
  }

  private void showGradientEditor() {

    Gradient g = JGradientEditor.showDialog(this, chart.getGradient());
    if (g != null) {
      gradViewer.setGradient(g);
      gradViewer.repaint();
      chart.setGradient(g);
    }

  }

}

/**
 * Settings frame class for the JGL3DChart
 */
public class SettingsFrame extends JFrame implements ActionListener {

  private JGL3DChart chart;

  // Axis panel
  private AxisPanel xPanel;
  private AxisPanel yPanel;
  private AxisPanel zPanel;
  private GradientPanel gPanel;

  private JTabbedPane tabPane;
  private JButton closeBtn;

  SettingsFrame(JGL3DChart parent) {

    chart = parent;

    JPanel innerPane = new JPanel((LayoutManager)null);

    xPanel = new AxisPanel(chart.getXAxis(),chart);
    yPanel = new AxisPanel(chart.getYAxis(),chart);
    zPanel = new AxisPanel(chart.getZAxis(),chart);
    gPanel = new GradientPanel(chart);

    // Global frame construction
    tabPane = new JTabbedPane();
    tabPane.add("X axis", xPanel);
    tabPane.add("Y axis", yPanel);
    tabPane.add("Z axis", zPanel);
    tabPane.add("Gradient", gPanel);

    innerPane.add(tabPane);

    closeBtn = new JButton();
    closeBtn.setText("Close");
    innerPane.add(closeBtn);

    tabPane.setBounds(5, 5, 300, 440);
    closeBtn.setBounds(225, 450, 80, 25);

    closeBtn.addActionListener(this);

    innerPane.setPreferredSize(new Dimension(310,480));
    setContentPane(innerPane);
    setResizable(false);

    setContentPane(innerPane);
    setTitle("Chart options");
    pack();
    ATKGraphicsUtils.centerFrame(chart,this);

  }

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if(src==closeBtn) {
      setVisible(false);
      dispose();
    }
  }

}

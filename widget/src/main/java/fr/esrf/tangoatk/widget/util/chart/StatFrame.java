package fr.esrf.tangoatk.widget.util.chart;

import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.JTableRow;
import org.omg.CORBA.ValueDefPOATie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * JLChart statistic frame
 */
public class StatFrame extends JFrame {

  private JPanel    innerPanel;
  private JPanel    btnPanel;
  private Vector<JLDataView> views;
  private JLChart parent;

  private javax.swing.JLabel atLabel;
  private javax.swing.JLabel avgLabel;
  private javax.swing.JTextField avgText;
  private javax.swing.JPanel dummyPanel0;
  private javax.swing.JPanel dummyPanel1;
  private javax.swing.JPanel dummyPanel2;
  private javax.swing.JPanel dummyPanel3;
  private javax.swing.JPanel dummyPanel4;
  private javax.swing.JPanel dummyPanel5;
  private javax.swing.JPanel dummyPanel6;
  private javax.swing.JTextField maxAtText;
  private javax.swing.JLabel maxLabel;
  private javax.swing.JTextField maxText;
  private javax.swing.JTextField minAtText;
  private javax.swing.JLabel minLabel;
  private javax.swing.JTextField minText;
  private javax.swing.JLabel rangeLabel;
  private javax.swing.JTextField rangeText;
  private javax.swing.JLabel rmsLabel;
  private javax.swing.JTextField rmsText;
  private javax.swing.JPanel statPanel;
  private javax.swing.JLabel stdLabel;
  private javax.swing.JTextField stdText;
  private javax.swing.JLabel valueLabel;

  public StatFrame(JLChart parent,Vector<JLDataView> views) {

    this.views = views;
    this.parent = parent;

    innerPanel = new JPanel();
    innerPanel.setLayout(new BorderLayout());


    java.awt.GridBagConstraints gridBagConstraints;

    statPanel = new javax.swing.JPanel();
    minLabel = new javax.swing.JLabel();
    maxLabel = new javax.swing.JLabel();
    rangeLabel = new javax.swing.JLabel();
    avgLabel = new javax.swing.JLabel();
    stdLabel = new javax.swing.JLabel();
    rmsLabel = new javax.swing.JLabel();
    valueLabel = new javax.swing.JLabel();
    atLabel = new javax.swing.JLabel();
    minText = new javax.swing.JTextField();
    maxText = new javax.swing.JTextField();
    rangeText = new javax.swing.JTextField();
    minAtText = new javax.swing.JTextField();
    maxAtText = new javax.swing.JTextField();
    avgText = new javax.swing.JTextField();
    stdText = new javax.swing.JTextField();
    rmsText = new javax.swing.JTextField();
    dummyPanel0 = new javax.swing.JPanel();
    dummyPanel1 = new javax.swing.JPanel();
    dummyPanel2 = new javax.swing.JPanel();
    dummyPanel3 = new javax.swing.JPanel();
    dummyPanel4 = new javax.swing.JPanel();
    dummyPanel5 = new javax.swing.JPanel();
    dummyPanel6 = new javax.swing.JPanel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    Font textFont = new java.awt.Font("Dialog", Font.PLAIN, 14);

    statPanel.setBackground(new java.awt.Color(0, 0, 0));
    java.awt.GridBagLayout statPanelLayout = new java.awt.GridBagLayout();
    statPanel.setLayout(statPanelLayout);

    minLabel.setBackground(new java.awt.Color(204, 204, 204));
    minLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    minLabel.setText("Minimum");
    minLabel.setToolTipText("");
    minLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(minLabel, gridBagConstraints);

    maxLabel.setBackground(new java.awt.Color(204, 204, 204));
    maxLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    maxLabel.setText("Maximum");
    maxLabel.setToolTipText("");
    maxLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(maxLabel, gridBagConstraints);

    rangeLabel.setBackground(new java.awt.Color(204, 204, 204));
    rangeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    rangeLabel.setText("Range");
    rangeLabel.setToolTipText("");
    rangeLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(rangeLabel, gridBagConstraints);

    avgLabel.setBackground(new java.awt.Color(204, 204, 204));
    avgLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    avgLabel.setText("Average");
    avgLabel.setToolTipText("");
    avgLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(avgLabel, gridBagConstraints);

    stdLabel.setBackground(new java.awt.Color(204, 204, 204));
    stdLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    stdLabel.setText("Standard Deviation");
    stdLabel.setToolTipText("");
    stdLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(stdLabel, gridBagConstraints);

    rmsLabel.setBackground(new java.awt.Color(204, 204, 204));
    rmsLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    rmsLabel.setText("RMS");
    rmsLabel.setToolTipText("");
    rmsLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.ipady = 3;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(rmsLabel, gridBagConstraints);

    valueLabel.setBackground(new java.awt.Color(204, 204, 204));
    valueLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    valueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    valueLabel.setText("Value");
    valueLabel.setToolTipText("");
    valueLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
    statPanel.add(valueLabel, gridBagConstraints);

    atLabel.setBackground(new java.awt.Color(204, 204, 204));
    atLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    atLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    atLabel.setText("At (X=)");
    atLabel.setToolTipText("");
    atLabel.setOpaque(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(atLabel, gridBagConstraints);

    minText.setEditable(false);
    minText.setBackground(new java.awt.Color(255, 255, 255));
    minText.setFont(textFont); // NOI18N
    minText.setText("-----");
    minText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
    statPanel.add(minText, gridBagConstraints);

    maxText.setEditable(false);
    maxText.setBackground(new java.awt.Color(255, 255, 255));
    maxText.setFont(textFont); // NOI18N
    maxText.setText("-----");
    maxText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
    statPanel.add(maxText, gridBagConstraints);

    rangeText.setEditable(false);
    rangeText.setBackground(new java.awt.Color(255, 255, 255));
    rangeText.setFont(textFont); // NOI18N
    rangeText.setText("-----");
    rangeText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
    statPanel.add(rangeText, gridBagConstraints);

    minAtText.setEditable(false);
    minAtText.setBackground(new java.awt.Color(255, 255, 255));
    minAtText.setFont(textFont); // NOI18N
    minAtText.setText("-----");
    minAtText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(minAtText, gridBagConstraints);

    maxAtText.setEditable(false);
    maxAtText.setBackground(new java.awt.Color(255, 255, 255));
    maxAtText.setFont(textFont); // NOI18N
    maxAtText.setText("-----");
    maxAtText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(maxAtText, gridBagConstraints);

    avgText.setEditable(false);
    avgText.setBackground(new java.awt.Color(255, 255, 255));
    avgText.setFont(textFont); // NOI18N
    avgText.setText("-----");
    avgText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
    statPanel.add(avgText, gridBagConstraints);

    stdText.setEditable(false);
    stdText.setBackground(new java.awt.Color(255, 255, 255));
    stdText.setFont(textFont); // NOI18N
    stdText.setText("-----");
    stdText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
    statPanel.add(stdText, gridBagConstraints);

    rmsText.setEditable(false);
    rmsText.setBackground(new java.awt.Color(255, 255, 255));
    rmsText.setFont(textFont); // NOI18N
    rmsText.setText("-----");
    rmsText.setBorder(null);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 200;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
    statPanel.add(rmsText, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(dummyPanel0, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(dummyPanel1, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(dummyPanel2, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(dummyPanel3, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
    statPanel.add(dummyPanel4, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    statPanel.add(dummyPanel5, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    statPanel.add(dummyPanel6, gridBagConstraints);

    innerPanel.add(statPanel,BorderLayout.CENTER);

    btnPanel = new JPanel();
    btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    innerPanel.add(btnPanel,BorderLayout.SOUTH);

    JButton refreshButton = new JButton("Refresh");
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refreshStat();
      }
    });
    btnPanel.add(refreshButton);

    JButton dismissButton = new JButton("Dismiss");
    dismissButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    btnPanel.add(dismissButton);

    setContentPane(innerPanel);

    String vTitle = "[All]";
    if(views.size()==1)
      vTitle = "[" + views.get(0).getName() + "]";

    refreshStat();

    setTitle("Statistics " + vTitle);
    ATKGraphicsUtils.centerFrame(parent,this);
    setVisible(true);

  }

  private String formatX(double x) {

    if (parent.getXAxis().getAnnotation() == JLAxis.TIME_ANNO) {
      return JLAxis.formatTimeValue(x);
    } else {
      return Double.toString(x);
    }

  }

  private void refreshStat() {

    double[][] values;
    int nbData = 0;

    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    double minX = 0.0;
    double maxX = 0.0;
    double avg = 0.0;
    double sum = 0.0;
    double sum2 = 0.0;
    double rms = 0.0;
    double std = 0.0;

    // First pass
    for(int i=0;i<views.size();i++) {
      DataList l = views.get(i).getData();
      while(l!=null) {
        if( !Double.isNaN(l.x) && !Double.isNaN(l.y) ) {
          nbData++;
          if(l.y<min) {
            min = l.y;
            minX = l.x;
          }
          if(l.y>max) {
            max = l.y;
            maxX = l.x;
          }
          sum += l.y;
          sum2 += (l.y*l.y);
        }
        l = l.next;
      }
    }

    if(nbData>0) {

      double N = (double)nbData;

      avg = sum / N;
      rms = Math.sqrt( sum2/N );
      sum2 = 0.0;

      for(int i=0;i<views.size();i++) {
        DataList l = views.get(i).getData();
        while(l!=null) {
          if( !Double.isNaN(l.x) && !Double.isNaN(l.y) ) {
            sum2 += (l.y - avg) * (l.y - avg);
          }
          l = l.next;
        }
      }

      std = Math.sqrt( sum2 / N );

      minText.setText( Double.toString(min) );
      minAtText.setText( formatX(minX) );
      maxText.setText( Double.toString(max) );
      maxAtText.setText( formatX(maxX) );
      rangeText.setText( Double.toString(max-min) );
      avgText.setText( Double.toString(avg) );
      stdText.setText( Double.toString(std) );
      rmsText.setText( Double.toString(rms) );

    } else {

      minText.setText("-----");
      minAtText.setText("");
      maxText.setText("-----");
      maxAtText.setText("");
      rangeText.setText("-----");
      avgText.setText("-----");
      stdText.setText("-----");
      rmsText.setText("-----");

    }

  }

}

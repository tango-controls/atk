package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.attribute.Trend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Simple trend frame (number scalar only)
 */
public class SimpleTrend extends JFrame {

  AttributePolledList attList;
  Trend trend;

  public SimpleTrend(String attName) {

    setTitle(attName);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    trend = new Trend(this);
    trend.setPreferredSize(new Dimension(800,600));
    setContentPane(trend);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exitPanel();
      }
    });


    IEntity att;
    attList = new AttributePolledList();
    try {
      att = attList.add(attName);
    } catch (ConnectionException e1) {
      ErrorPane.showErrorMessage(this,attName,e1);
      return;
    }

    if(! (att instanceof INumberScalar) ) {
      JOptionPane.showMessageDialog(this,"SimpleTrend supports only number scalar");
      return;
    }

    trend.setModel(attList);
    trend.setSelectionTreeVisible(false);
    trend.addToAxis(attName,Trend.SEL_Y1,false);
    attList.startRefresher();
    ATKGraphicsUtils.centerFrameOnScreen(this);
    setVisible(true);

  }

  private void exitPanel() {
    attList.clear();
    attList.stopRefresher();
    dispose();
  }

  public static void main(String args[]) {
    SimpleTrend t = new SimpleTrend("srdiag/bpm/c01-10/SA_Sum");
  }

}

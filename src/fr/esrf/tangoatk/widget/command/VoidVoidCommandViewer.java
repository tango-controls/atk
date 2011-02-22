// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   VoidVoidCommandViewer.java

package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VoidVoidCommandViewer extends JPanel
  implements IResultListener
{

  private void initComponents()
  {
    executeButton = new JButton();
    setLayout(new BorderLayout());
    executeButton.setText("command-name");
    executeButton.addActionListener(new _cls1());
    add(executeButton, "Center");
  }

  private void executeButtonActionPerformed(ActionEvent actionevent)
  {
    model.execute();
  }

  public void resultChange(ResultEvent resultevent)
  {
  }

  public void errorChange(ErrorEvent errorevent)
  {
  }

  public void setModel(ICommand icommand)
  {
    model = icommand;
    executeButton.setText(model.getNameSansDevice());
    executeButton.setToolTipText(model.getDevice().toString());
  }

  public void setCommandLabel(String label)
  {
	executeButton.setText(label);
  }

  public ICommand getModel()
  {
    return model;
  }

  public void setEnabled(boolean flag)
  {
    executeButton.setEnabled(flag);
  }

  public boolean isEnabled()
  {
    return executeButton.isEnabled();
  }

  public VoidVoidCommandViewer()
  {
    initComponents();
  }

  ICommand model;
  private JButton executeButton;


  private class _cls1
    implements ActionListener
  {

    public void actionPerformed(ActionEvent actionevent)
    {
      executeButtonActionPerformed(actionevent);
    }

    private final void constructor$0(VoidVoidCommandViewer voidvoidcommandviewer)
    {
    }

    _cls1()
    {
      constructor$0(VoidVoidCommandViewer.this);
    }
  }

}

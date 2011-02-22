// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   CommandList.java

package fr.esrf.tangoatk.widget.command;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.event.*;

public class CommandList extends JList {

    private void initComponents()
    {
	setLayout(new BorderLayout());
	addMouseListener(new _cls1());
    }

    private void formMouseClicked(MouseEvent mouseevent) {
	int i = locationToIndex(mouseevent.getPoint());
	System.out.println("asking viewerList(" + i + ") to show...");
	fireSelectionValueChanged(i, i, false);
    }

    public CommandList() {
	initComponents();
    }


    private class _cls1 extends MouseAdapter {

	public void mouseClicked(MouseEvent mouseevent) {
	    formMouseClicked(mouseevent);
	}

	private final void constructor$0(CommandList commandlist) {
	}

	_cls1() {
	    constructor$0(CommandList.this);
	}
    }

}

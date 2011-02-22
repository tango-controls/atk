// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   DropTable.java

package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.IEntity;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.PrintStream;
import javax.swing.JTable;
import javax.swing.table.TableModel;

// Referenced classes of package fr.esrf.TangoATK.Widget.Util:
//      EntityJTableAdapter

public class DropTable extends JTable
{
  class DTListener
    implements DropTargetListener
  {

    private boolean isDragFlavorSupported(DropTargetDragEvent droptargetdragevent)
    {
      return true;
    }

    private DataFlavor chooseDropFlavor(DropTargetDropEvent droptargetdropevent)
    {
      DataFlavor dataflavor = DataFlavor.stringFlavor;
      return dataflavor;
    }

    private boolean isDragOk(DropTargetDragEvent droptargetdragevent)
    {
      if (!isDragFlavorSupported(droptargetdragevent))
      {
        System.out.println("isDragOk:no flavors chosen");
        return false;
      }
      int i = droptargetdragevent.getDropAction();
      System.out.print("dt drop action " + i);
      System.out.println(" my acceptable actions " + acceptableActions);
      return (i & acceptableActions) != 0;
    }

    public void dragEnter(DropTargetDragEvent droptargetdragevent)
    {
      System.out.println("dtlistener dragEnter");
      if (!isDragOk(droptargetdragevent))
      {
        System.out.println("enter not ok");
        droptargetdragevent.rejectDrag();
        return;
      } else
      {
        System.out.println("dt enter: accepting " + droptargetdragevent.getDropAction());
        droptargetdragevent.acceptDrag(droptargetdragevent.getDropAction());
        return;
      }
    }

    public void dragOver(DropTargetDragEvent droptargetdragevent)
    {
      if (!isDragOk(droptargetdragevent))
      {
        System.out.println("dtlistener dragOver not ok");
        droptargetdragevent.rejectDrag();
        return;
      } else
      {
        System.out.println("dt over: accepting");
        droptargetdragevent.acceptDrag(droptargetdragevent.getDropAction());
        return;
      }
    }

    public void dropActionChanged(DropTargetDragEvent droptargetdragevent)
    {
      if (!isDragOk(droptargetdragevent))
      {
        System.out.println("dtlistener changed not ok");
        droptargetdragevent.rejectDrag();
        return;
      } else
      {
        System.out.println("dt changed: accepting" + droptargetdragevent.getDropAction());
        droptargetdragevent.acceptDrag(droptargetdragevent.getDropAction());
        return;
      }
    }

    public void dragExit(DropTargetEvent droptargetevent)
    {
      System.out.println("dtlistener dragExit");
    }

    public void drop(DropTargetDropEvent droptargetdropevent)
    {
      System.out.println("dtlistener drop");
      DataFlavor dataflavor = chooseDropFlavor(droptargetdropevent);
      if (dataflavor == null)
      {
        System.err.println("No flavor match found");
        droptargetdropevent.rejectDrop();
        return;
      }
      System.err.println("Chosen data flavor is " + dataflavor.getMimeType());
      int i = droptargetdropevent.getDropAction();
      int j = droptargetdropevent.getSourceActions();
      System.out.println("drop: sourceActions: " + j);
      System.out.println("drop: dropAction: " + i);
      if ((j & acceptableActions) == 0)
      {
        System.err.println("No action match found");
        droptargetdropevent.rejectDrop();
        return;
      }
      Object obj = null;
      try
      {
        droptargetdropevent.acceptDrop(acceptableActions);
        obj = droptargetdropevent.getTransferable().getTransferData(dataflavor);
        if (obj == null)
          throw new NullPointerException();
      }
      catch (Throwable throwable)
      {
        System.err.println("Couldn't get transfer data: " + throwable.getMessage());
        throwable.printStackTrace();
        droptargetdropevent.dropComplete(false);
        return;
      }
      System.out.println("Got data: " + obj.getClass().getName());
      if (obj instanceof IEntity)
      {
        IEntity ientity = (IEntity)obj;
        ((EntityJTableAdapter)getModel()).addEntity(ientity);
        System.out.println("got " + ientity);
      } else
      {
        System.out.println("drop: rejecting");
        droptargetdropevent.dropComplete(false);
        return;
      }
      droptargetdropevent.dropComplete(true);
    }

    DTListener()
    {
    }
  }


  public DropTable()
  {
    acceptableActions = 1;
    dtListener = new DTListener();
    dropTarget = new DropTarget(this, acceptableActions, dtListener, true);
  }

  public DropTable(TableModel tablemodel)
  {
    super(tablemodel);
    acceptableActions = 1;
    dtListener = new DTListener();
    dropTarget = new DropTarget(this, acceptableActions, dtListener, true);
  }

  public DropTable(int i)
  {
    acceptableActions = 1;
    if (i != 0 && i != 1 && i != 2 && i != 3 && i != 0x40000000)
    {
      throw new IllegalArgumentException("action" + i);
    } else
    {
      acceptableActions = i;
      dtListener = new DTListener();
      dropTarget = new DropTarget(this, acceptableActions, dtListener, true);
      return;
    }
  }

  private DropTarget dropTarget;
  private DropTargetListener dtListener;
  private int acceptableActions;

}

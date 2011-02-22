
package fr.esrf.tangoatk.widget.util.jdraw;

import java.util.Vector;

class UndoBuffer  {

  UndoPattern[] undoBuffer;
  String name;

  UndoBuffer(Vector objects,String n) {
    undoBuffer = new UndoPattern[objects.size()];
    for(int i=0;i<objects.size();i++)
      undoBuffer[i] = ((JDObject)objects.get(i)).getUndoPattern();
    name=n;
  }

  UndoBuffer(JDObject[] objects,String n) {
    undoBuffer = new UndoPattern[objects.length];
    for(int i=0;i<objects.length;i++)
      undoBuffer[i] = objects[i].getUndoPattern();
    name=n;
  }

  Vector rebuild() {

    Vector children=new Vector();
    for(int i=0;i<undoBuffer.length;i++) {
      switch(undoBuffer[i].JDclass) {
        case UndoPattern._JDEllipse:
          children.add(new JDEllipse(undoBuffer[i]));
          break;
        case UndoPattern._JDGroup:
          children.add(new JDGroup(undoBuffer[i]));
          break;
        case UndoPattern._JDLabel:
          children.add(new JDLabel(undoBuffer[i]));
          break;
        case UndoPattern._JDLine:
          children.add(new JDLine(undoBuffer[i]));
          break;
        case UndoPattern._JDPolyline:
          children.add(new JDPolyline(undoBuffer[i]));
          break;
        case UndoPattern._JDRectangle:
          children.add(new JDRectangle(undoBuffer[i]));
          break;
        case UndoPattern._JDRoundRectangle:
          children.add(new JDRoundRectangle(undoBuffer[i]));
          break;
        case UndoPattern._JDSpline:
          children.add(new JDSpline(undoBuffer[i]));
          break;
        case UndoPattern._JDImage:
          children.add(new JDImage(undoBuffer[i]));
          break;
        default:
          System.out.println("!!! WARNING Undo failure !!!");
      }
    }

    return children;
  }

  UndoPattern get(int id) {
    return undoBuffer[id];
  }

  String getName() {
    return name;
  }


}

package fr.esrf.tangoatk.widget.util.interlock;

import java.util.Vector;

class UndoBuffer  {

  private UndoPattern[] undoBuffer;
  private String name;

  /**
   * Create a backup of the given set of ItlkObjects.
   * @param objects ItlkObjects to be backuped
   * @param n backup's name
   */
  UndoBuffer(Vector objects,String n) {

    int i,sz = objects.size();

    // Reset index
    for(i=0;i<sz;i++)
      ((NetObject)objects.get(i)).setIndex(i);

    // Create the undo pattern
    undoBuffer = new UndoPattern[objects.size()];
    for(i=0;i<objects.size();i++)
      undoBuffer[i] = ((NetObject)objects.get(i)).getUndoPattern();
    name=n;
    
  }

  /**
   * Rebuild the set of NetObject backuped.
   * @return Reconstructed array of ItlkObjects
   */
  NetObject[] rebuild() {

    int i,j;
    int sz = undoBuffer.length;
    NetObject[] objects=new NetObject[sz];
    NetObject pObj,cObj;

    // Rebuild objects
    for(i=0;i<sz;i++) objects[i]=new NetObject(undoBuffer[i]);

    // Rebuild link
    for(i=0;i<sz;i++) {
      for(j=0;j<undoBuffer[i].childList.length;j++) {
        pObj = objects[i];
        cObj = objects[undoBuffer[i].childList[j]];
        pObj.addChild(cObj);
      }
    }

    return objects;
  }

  /**
   * Return the name of this backup
   * @return Name
   */
  String getName() {
    return name;
  }

}

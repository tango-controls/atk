include ../../../../../../../.make-include

ALL = $(TARGET)/NetObjectDlg.class \
      $(TARGET)/NetEditor.class \
      $(TARGET)/NetEditorListener.class \
      $(TARGET)/NetFileFilter.class \
      $(TARGET)/NetFileLoader.class \
      $(TARGET)/NetObject.class \
      $(TARGET)/NetEditorDlg.class \
      $(TARGET)/NetUtils.class \
      $(TARGET)/NetShape.class \
      $(TARGET)/NetEditorFrame.class \
      $(TARGET)/UndoBuffer.class \
      $(TARGET)/UndoPattern.class \
      $(TARGET)/XpssFileLoader.class

all: gifs shapes utils

jdoc: icons

utils: $(ALL)
	$(J)

gifs:
	(cd gif && $(MAKE) all)

shapes:
	(cd shape && $(MAKE) all)

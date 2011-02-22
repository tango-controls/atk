include ../../../../../../../.make-include

ALL = $(TARGET)/GraphicsUtils.class \
	$(TARGET)/FileFinder.class \
	$(TARGET)/JDrawable.class \
	$(TARGET)/JDRotatable.class \
	$(TARGET)/JDrawableList.class \
	$(TARGET)/JDBrowserPanel.class \
	$(TARGET)/JDEllipse.class \
	$(TARGET)/JDEllipsePanel.class \
	$(TARGET)/JDExtensionPanel.class \
	$(TARGET)/JDExtensionEditor.class \
	$(TARGET)/JDFileFilter.class \
	$(TARGET)/JDFileLoader.class \
	$(TARGET)/JDGlobalPanel.class \
	$(TARGET)/JDGradientDialog.class \
	$(TARGET)/JDGradientViewer.class \
	$(TARGET)/JDGroup.class \
	$(TARGET)/JDGroupEditorView.class \
	$(TARGET)/JDImage.class \
	$(TARGET)/JDImagePanel.class \
	$(TARGET)/JDLabel.class \
	$(TARGET)/JDLabelPanel.class \
	$(TARGET)/JDLine.class \
	$(TARGET)/JDLinePanel.class \
	$(TARGET)/JDAxis.class \
	$(TARGET)/JDAxisPanel.class \
	$(TARGET)/JDBar.class \
	$(TARGET)/JDBarPanel.class \
	$(TARGET)/JDSlider.class \
	$(TARGET)/JDSliderPanel.class \
	$(TARGET)/JDMouseAdapter.class \
	$(TARGET)/JDMouseEvent.class \
	$(TARGET)/JDMouseListener.class \
	$(TARGET)/JDObject.class \
	$(TARGET)/JDObjectPanel.class \
	$(TARGET)/JDPolyConvert.class \
	$(TARGET)/JDPolyline.class \
	$(TARGET)/JDPolylinePanel.class \
	$(TARGET)/JDSwingObject.class \
	$(TARGET)/JDSwingPanel.class \
	$(TARGET)/JDRectangle.class \
	$(TARGET)/JDRectangular.class \
	$(TARGET)/JDRoundRectangle.class \
	$(TARGET)/JDRoundRectanglePanel.class \
	$(TARGET)/JDSpline.class \
	$(TARGET)/JDTransformPanel.class \
	$(TARGET)/JDTreeNode.class \
	$(TARGET)/JDTreeNodeRenderer.class \
	$(TARGET)/JDUtils.class \
	$(TARGET)/JDValueListener.class \
	$(TARGET)/JDValueMappingPanel.class \
	$(TARGET)/JDValuePanel.class \
	$(TARGET)/JDValueProgram.class \
	$(TARGET)/JDrawEditor.class \
	$(TARGET)/JDCreationMenu.class \
	$(TARGET)/JDrawEditorFrame.class \
	$(TARGET)/JDrawEditorListener.class \
	$(TARGET)/JLXFileLoader.class \
	$(TARGET)/JLXObject.class \
	$(TARGET)/JLXPath.class \
	$(TARGET)/JLXStyle.class \
	$(TARGET)/LXFileLoader.class \
	$(TARGET)/LXObject.class \
	$(TARGET)/JDLibraryViewer.class \
	$(TARGET)/UndoBuffer.class \
	$(TARGET)/UndoPattern.class

all: gifs utils

jdoc: icons

utils: $(ALL)
	$(J)

gifs:
	(cd gif && $(MAKE) all)

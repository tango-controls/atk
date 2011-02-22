
include ../../../../../../.make-include

ALL = 	$(TARGET)/IImageControl.class               \
	$(TARGET)/IImageControlPanel.class          \
	$(TARGET)/IImageManipulator.class           \
	$(TARGET)/IImagePanel.class                 \
	$(TARGET)/IImageManipulatorPanel.class      \
	$(TARGET)/IRasterControl.class              \
	$(TARGET)/IRasterConverter.class            \
	$(TARGET)/ConvolveFilter.class              \
	$(TARGET)/IImageViewer.class                \
	$(TARGET)/RoiControlPanel.class             \
	$(TARGET)/RasterControl.class               \
	$(TARGET)/GrayscaleColorConverter.class     \
	$(TARGET)/BWRaster.class                    \
	$(TARGET)/ImageController.class             \
	$(TARGET)/ImageViewer.class                 \


all: $(ALL)
	$(J)


iv: all
	$(JAVA) fr.esrf.tangoatk.widget.image.ImageViewer

cf: all
	$(JAVA) fr.esrf.tangoatk.widget.image.ConvolveFilter
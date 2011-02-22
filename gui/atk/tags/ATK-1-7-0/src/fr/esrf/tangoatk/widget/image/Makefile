
include ../../../../../../.make-include

ALL = 	$(TARGET)/IImageManipulator.class           \
	$(TARGET)/IImagePanel.class                 \
	$(TARGET)/IRasterConverter.class            \
	$(TARGET)/ConvolveFilter.class              \
	$(TARGET)/IImageViewer.class                \
	$(TARGET)/RoiControlPanel.class             \
	$(TARGET)/GrayscaleColorConverter.class     \
	$(TARGET)/BWRaster.class                    \
	$(TARGET)/ImageController.class             \
	$(TARGET)/ImageViewer.class                 \
	$(TARGET)/LineProfilerViewer.class          \

all:	image icons

image: $(ALL)
	$(J)

iv: all
	$(JAVA) fr.esrf.tangoatk.widget.image.ImageViewer
cf: all
	$(JAVA) fr.esrf.tangoatk.widget.image.ConvolveFilter

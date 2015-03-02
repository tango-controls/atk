include ../../../../../../../.make-include

ALL = $(TARGET)/Utils.class \
	$(TARGET)/JGL3DAxis.class \
	$(TARGET)/JGL3DChart.class \
	$(TARGET)/JGL3DView.class \
	$(TARGET)/LabelInfo.class \
	$(TARGET)/SettingsFrame.class \
	$(TARGET)/VERTEX3D.class

all: gifs utils

jdoc: icons

utils: $(ALL)
	$(J)

gifs:
	(cd gif && $(MAKE) all)

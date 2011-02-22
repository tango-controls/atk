#BEANINFO =	StatusViewerBeanInfo.java
#		StateViewerBeanInfo.java
#            DeviceViewerBeanInfo.java \


include ../../../../../../.make-include

ALL =   $(TARGET)/IDevicePopUp.class \
	  $(TARGET)/StatusViewer.class      \
	  $(TARGET)/StateViewer.class       \
	  $(TARGET)/SimpleStateViewer.class       \
        $(TARGET)/DeviceViewer.class \
        $(TARGET)/SingletonStatusViewer.class \
        $(TARGET)/Tree.class \
	$(TARGET)/DeviceStateLightViewer.class \

all: tree-stuff devices beaninfo icons

devices: $(ALL)
	$(J)

tree-stuff:
	(cd tree && $(MAKE) all)

manifest: manifest-file

dslv: $(ALL)
	$(JAVA) fr.esrf.tangoatk.widget.device.DeviceStateLightViewer jlp/test/1


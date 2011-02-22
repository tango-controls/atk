BEANINFO = StateViewerBeanInfo.java       \

#            StatusViewerBeanInfo.java      \
#            DeviceViewerBeanInfo.java \


include ../../../../../../.make-include

ALL =   $(TARGET)/IDevicePopUp.class \
	$(TARGET)/StatusViewer.class      \
	$(TARGET)/StateViewer.class       \
        $(TARGET)/DeviceViewer.class \
        $(TARGET)/SingletonStatusViewer.class \

all: devices beaninfo icons

devices: $(ALL)
	$(J)


manifest: manifest-file

run:
	$(JAVA) fr.esrf.TangoATK.Widget.Device.DeviceViewer


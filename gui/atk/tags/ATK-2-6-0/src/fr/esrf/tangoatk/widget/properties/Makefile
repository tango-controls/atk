BEANINFO=LabelViewerBeanInfo.java \
	UnitViewerBeanInfo.java \


include ../../../../../../.make-include


ALL =  	$(TARGET)/PropertyViewer.class \
	$(TARGET)/PropertyListAdapter.class \
	$(TARGET)/PropertyListViewer2.class \
	$(TARGET)/LabelViewer.class \
	$(TARGET)/DevicePropertyDialog.class \
	$(TARGET)/DevicePropertyListPanel.class \
	$(TARGET)/UnitViewer.class \


all: properties beaninfo

properties: $(ALL)
	$(J)


manifest: manifest-file

run: all
	$(JAVA) fr.esrf.tangoatk.widget.properties.PropertyListViewer2

# BEANINFO=PropertyListViewerBeanInfo.java \
#          PropertyFrameBeanInfo.java \


include ../../../../../../.make-include


ALL =  	$(TARGET)/PropertyViewer.class \
	$(TARGET)/PropertyListAdapter.class \
	$(TARGET)/PropertyListViewer2.class \


all: properties beaninfo

properties: $(ALL)
	$(J)


manifest: manifest-file

run: all
	$(JAVA) fr.esrf.tangoatk.widget.properties.PropertyListViewer2

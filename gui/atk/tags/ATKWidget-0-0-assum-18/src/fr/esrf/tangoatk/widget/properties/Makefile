# BEANINFO=PropertyViewerBeanInfo.java\
#          PropertyListViewerBeanInfo.java \
#          PropertyFrameBeanInfo.java \


include ../../../../../../.make-include


ALL =  	$(TARGET)/PropertyViewer.class \
	$(TARGET)/PropertyListAdapter.class \
	$(TARGET)/PropertyListViewer2.class \


all: properties beaninfo

properties: $(ALL)
	$(J)

ci:
	@cvs ci Makefile `echo $(ALL) \
                     | sed 's|$(TARGET)/||g' |  \
                     sed 's|\.class|.java|g'`


manifest: manifest-file

run: all
	$(JAVA) fr.esrf.tangoatk.widget.properties.PropertyListViewer2
BEANINFO =	SynopticFileViewerBeanInfo.java 

include ../../../../../../.make-include


ALL = $(TARGET)/TangoSynopticHandler.class \
      $(TARGET)/SynopticFileViewer.class \
      $(TARGET)/DeviceSynopticViewer.class \
      $(TARGET)/DeviceSynopticFrame.class
      

all: jdraw beaninfo icons

jdraw: $(ALL)
	echo $(CLASSPATH)
	$(J)

synoptic: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler
	
manifest: manifest-file

sfv: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jdraw.SynopticFileViewer

dsv: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jdraw.DeviceSynopticViewer

dsf: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jdraw.DeviceSynopticFrame

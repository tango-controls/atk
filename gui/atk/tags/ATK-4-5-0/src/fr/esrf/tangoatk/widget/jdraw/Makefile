BEANINFO =	SynopticFileViewerBeanInfo.java 

include ../../../../../../.make-include


ALL = $(TARGET)/TangoSynopticHandler.class \
      $(TARGET)/SynopticFileViewer.class \
      $(TARGET)/DeviceSynopticViewer.class \
      $(TARGET)/DeviceSynopticFrame.class \
      $(TARGET)/SimpleSynopticAppli.class \
      $(TARGET)/../util/jdraw/JDrawEditorFrame.class
      

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

synApp: all
	echo $(CLASSPATH)
	$(JAVA) fr.esrf.tangoatk.widget.jdraw.SimpleSynopticAppli "/segfs/tango/jclient/JLinac/jdraw_file/jlinac-tango.jdw"

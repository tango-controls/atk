BEANINFO = VoidVoidCommandViewerBeanInfo.java \
	   CommandComboViewerBeanInfo.java 

#            CommandOutputBeanInfo.java \
#            ScalarCommandInputBeanInfo.java \


include ../../../../../../.make-include


ALL =	$(TARGET)/PropertyFrame.class         \
	$(TARGET)/CommandList.class           \
	$(TARGET)/VoidVoidCommandViewer.class \
	$(TARGET)/IInput.class                \
	$(TARGET)/ScalarCommandInput.class    \
	$(TARGET)/CommandTableInputAdapter.class   \
	$(TARGET)/CommandInputOutputFactory.class   \
	$(TARGET)/ArrayInput.class            \
	$(TARGET)/NoInput.class               \
	$(TARGET)/SimpleCommandOutput.class	\
	$(TARGET)/CommandOutput.class         \
	$(TARGET)/SimpleCommandViewer.class   \
	$(TARGET)/AnyCommandViewer.class   \
	$(TARGET)/CommandComboViewer.class    \

all: commands beaninfo icons

commands: $(ALL)
	$(J)

manifest: manifest-file

ci:
	@cvs ci Makefile `echo $(ALL) \
                     | sed 's|$(TARGET)/||g' |  \
                     sed 's|\.class|.java|g'`


run:
	$(JAVA) fr.esrf.tangoatk.widget.command.SimpleCommandViewer

ai:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.ArrayInput
	

combo:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.CommandComboViewer

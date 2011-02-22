BEANINFO = VoidVoidCommandViewerBeanInfo.java \
	   CommandComboViewerBeanInfo.java \
	   CommandButtonViewerBeanInfo.java 

#            CommandOutputBeanInfo.java \
#            ScalarCommandInputBeanInfo.java \


include ../../../../../../.make-include


ALL =	$(TARGET)/PropertyFrame.class         \
	$(TARGET)/CommandList.class           \
	$(TARGET)/VoidVoidCommandViewer.class \
	$(TARGET)/IInput.class                \
	$(TARGET)/NoInput.class               \
	$(TARGET)/ArrayCommandInput.class	\
	$(TARGET)/CommandArrayInputAdapter.class \
	$(TARGET)/ScalarCommandInput.class    \
	$(TARGET)/TableCommandInput.class	\
	$(TARGET)/CommandTableInputAdapter.class   \
	$(TARGET)/CommandInputOutputFactory.class   \
	$(TARGET)/SimpleCommandOutput.class	\
	$(TARGET)/CommandOutput.class         \
	$(TARGET)/SimpleCommandViewer.class   \
	$(TARGET)/AnyCommandViewer.class   \
	$(TARGET)/CommandComboViewer.class    \
	$(TARGET)/CommandMenuViewer.class    \
	$(TARGET)/CommandButtonViewer.class    \
	$(TARGET)/ThreeStateSwitchCommandViewer.class

all: commands beaninfo icons

commands: $(ALL)
	$(J)

manifest: manifest-file



run:
	$(JAVA) fr.esrf.tangoatk.widget.command.SimpleCommandViewer

ai:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.ArrayCommandInput
	

combo:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.CommandComboViewer
	

cmdBut:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.CommandButtonViewer

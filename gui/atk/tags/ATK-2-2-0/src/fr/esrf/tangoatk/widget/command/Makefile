BEANINFO = VoidVoidCommandViewerBeanInfo.java \
	   CommandComboViewerBeanInfo.java \
	   CommandButtonViewerBeanInfo.java \
	   OnOffSwitchCommandViewerBeanInfo.java \
	   OnOffCheckBoxCommandViewerBeanInfo.java \
	   OptionComboCommandViewerBeanInfo.java \
	   ConfirmCommandViewerBeanInfo.java

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
	$(TARGET)/ThreeStateSwitchCommandViewer.class \
	$(TARGET)/OnOffSwitchCommandViewer.class  \
	$(TARGET)/OnOffCheckBoxCommandViewer.class \
	$(TARGET)/OptionComboCommandViewer.class \
	$(TARGET)/ScalarCommandViewer.class \
	$(TARGET)/ConfirmCommandViewer.class

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
	

onOffsw:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.OnOffSwitchCommandViewer
	

onOffch:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.OnOffCheckBoxCommandViewer
	

option:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.OptionComboCommandViewer
	

confirm:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.ConfirmCommandViewer
	
	

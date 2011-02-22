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
	$(TARGET)/CommandInputAdapter.class   \
	$(TARGET)/CommandInputFactory.class   \
	$(TARGET)/ArrayInput.class            \
	$(TARGET)/NoInput.class               \
	$(TARGET)/CommandOutput.class         \
	$(TARGET)/SimpleCommandViewer.class   \
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
	$(JAVA) fr.esrf.TangoATK.Widget.Command.SimpleCommandViewer

ai:	all
	$(JAVA) fr.esrf.tangoatk.widget.command.ArrayInput

include ../../../../../.make-include


EXCEPTIONS = $(TARGET)/ATKException.class \
	    $(TARGET)/AttributeReadException.class \
	    $(TARGET)/AttributeSetException.class \
	    $(TARGET)/AttributeErrorException.class \
	    $(TARGET)/ConnectionException.class \
	    $(TARGET)/DeviceException.class \
	    $(TARGET)/CommandExecuteException.class \


ERRORHANDLER = 	$(TARGET)/ErrorHandlerAdapter.class \



DEVICE = $(TARGET)/DeviceFactory.class \
         $(TARGET)/Device.class 


INTERFACES = $(TARGET)/INumber.class   \
	$(TARGET)/IDevice.class      \
	$(TARGET)/IEntityFilter.class \
	$(TARGET)/IString.class \
        $(TARGET)/IDeviceApplication.class \
	$(TARGET)/IErrorListener.class \
	$(TARGET)/ISetErrorListener.class \
	$(TARGET)/IErrorHandler.class \
	$(TARGET)/ILogListener.class \
	$(TARGET)/IStateListener.class \
	$(TARGET)/IAttributeStateListener.class \
        $(TARGET)/IStringScalarListener.class \
	$(TARGET)/INumberScalarListener.class \
	$(TARGET)/IStatusListener.class \
	$(TARGET)/IImageListener.class \
        $(TARGET)/ISpectrumListener.class \
	$(TARGET)/IResultListener.class \
	$(TARGET)/Refresher.class \
	$(TARGET)/IRefreshee.class \
	$(TARGET)/IEntity.class \
	$(TARGET)/IAttribute.class \
	$(TARGET)/IScalarAttribute.class \
	$(TARGET)/ICommand.class \
	$(TARGET)/IEntityCollection.class \
	$(TARGET)/Property.class \
	$(TARGET)/IDeviceListener.class \
	$(TARGET)/IFactory.class \
        $(TARGET)/IStringScalar.class \
	$(TARGET)/INumberScalar.class \
        $(TARGET)/INumberSpectrum.class \
        $(TARGET)/INumberImage.class \
        $(TARGET)/IAttributeScalarHistory.class \
        $(TARGET)/INumberScalarHistory.class \
        $(TARGET)/IStringScalarHistory.class \
        $(TARGET)/IStringSpectrum.class \
        $(TARGET)/IStringSpectrumListener.class \
	$(TARGET)/IRefresherListener.class \
	$(TARGET)/INonAttrNumberSpectrum.class \
	$(TARGET)/INonAttrSpectrumListener.class \
        $(TARGET)/IBooleanImage.class \
        $(TARGET)/IBooleanSpectrum.class \
        $(TARGET)/IBooleanScalar.class \
        $(TARGET)/IBooleanImageListener.class \
        $(TARGET)/IBooleanSpectrumListener.class \
        $(TARGET)/IBooleanScalarListener.class \
        $(TARGET)/IDevStateScalar.class \
        $(TARGET)/IDevStateScalarListener.class \


PROPERTIES = $(TARGET)/Property.class \
             $(TARGET)/NumberProperty.class \
	     $(TARGET)/StringProperty.class \
	     $(TARGET)/WritableProperty.class \
	     $(TARGET)/FormatProperty.class \
	     $(TARGET)/DisplayLevelProperty.class \
	     $(TARGET)/TypeProperty.class \


EVENTS =  $(TARGET)/AtkEventListenerList.class \
          $(TARGET)/EventSupport.class \
	      $(TARGET)/ATKEvent.class \
          $(TARGET)/NumberScalarEvent.class \
          $(TARGET)/StringScalarEvent.class \
          $(TARGET)/NumberImageEvent.class \
          $(TARGET)/NumberSpectrumEvent.class \
          $(TARGET)/StateEvent.class \
          $(TARGET)/StatusEvent.class \
          $(TARGET)/ErrorEvent.class \
          $(TARGET)/ResultEvent.class \
          $(TARGET)/AttributeStateEvent.class \
          $(TARGET)/StringSpectrumEvent.class \
          $(TARGET)/NonAttrNumberSpectrumEvent.class \
          $(TARGET)/BooleanImageEvent.class \
          $(TARGET)/BooleanSpectrumEvent.class \
          $(TARGET)/BooleanScalarEvent.class \
          $(TARGET)/DevStateScalarEvent.class \


ABSTRACTS = $(TARGET)/AEntityList.class \
	    $(TARGET)/AEntityFactory.class \





LISTS = $(TARGET)/AttributeList.class \
	$(TARGET)/AttributePolledList.class \
	$(TARGET)/CommandList.class \


PRE = $(INTERFACES) $(ABSTRACTS) $(DEVICE) $(ERRORHANDLER) \
      $(EXCEPTIONS) $(EVENTS)  $(PROPERTIES) 


POST = $(LISTS)

pre: $(PRE)
	$(J)

post: $(POST)
	$(J)


run: all
	$(JAVA) fr.esrf.tangoatk.core.NumberImage


commands: 
	(cd command && $(MAKE) all)

attributes:
	(cd attribute && $(MAKE) all)

utils: 
	(cd util && $(MAKE) all)

core: pre commands attributes post


all:  utils core beaninfo


attribute-clean:
	(cd attribute && $(MAKE) class-clean)

command-clean:
	(cd command && $(MAKE) class-clean)

util-clean:
	(cd util && $(MAKE) class-clean)

clean: class-clean util-clean command-clean attribute-clean


logTest: 	$(TARGET)/logTest.class \

runlog:
	$(JAVA) logTest

tgd: $(TARGET)/TimeGetDevice.class

runtgd:
	$(JAVA) fr.esrf.TangoATK.Core.TimeGetDevice

makedep:
	export CLASSPATH=/usr/local/java/jre/lib/rt.jar:$(CLASSPATH); \
	echo $(CLASSPATH) ; \
	jikes +M *.java 

exceptiondoc:
	$(AUTODIA) -o exceptiondoc.xml $(PACKAGE).ExecuteFailedException \
	$(PACKAGE).ReadValueException \
	$(PACKAGE).InitializationException 


errorhandlerdoc: 
	$(AUTODIA) -o errorhandlerdoc.xml $(PACKAGE).Log4jConfigurator \
	$(PACKAGE).BasicErrorHandler \


devicedoc: errorhandlerdoc
	$(AUTODIA) -o devicedoc $(PACKAGE).Device

interfacedoc: devicedoc
	$(AUTODIA) -o interfacedoc.xml $(PACKAGE).IErrorListener \
	$(PACKAGE).ISetErrorListener \
	$(PACKAGE).IRefresher \
	$(PACKAGE).IEntity \
	$(PACKAGE).IFactory \
	$(PACKAGE).IEntityCollection \


abstractdoc: devicefactorydoc
	$(AUTODIA) -o abstractdoc.xml $(PACKAGE).AAttribute \
	$(PACKAGE).ACommand \
	$(PACKAGE).AEntityFactory \
	$(PACKAGE).AEntityList \


attributedoc: abstracts
	$(AUTODIA) -o attributedoc.xml $(PACKAGE).AScalarAttribute \
	$(PACKAGE).IScalarListener \
	$(PACKAGE).INumberScalarListener\
	$(PACKAGE).StringScalar \
	$(PACKAGE).ANumberScalar \
	$(PACKAGE).ShortScalar \
	$(PACKAGE).LongScalar \
	$(PACKAGE).DoubleScalar \

commanddoc: abstractdoc
	$(AUTODIA) -o commanddoc.xml $(PACKAGE).ACommand \
	$(PACKAGE).AGenericCommand	


devicefactorydoc: interfacedoc
	$(AUTODIA) -o devicefactorydoc.xml $(PACKAGE).DeviceFactory 



doc:	commanddoc attributedoc


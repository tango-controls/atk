include .make-include

# dont forget to edit $(ATK_RELDIR)/* files
# to set the major and minor releases and set the build number to zero
ifdef ATK_RELDIR
ATK-MAJOR := $(shell cat $(ATK_RELDIR)/MAJOR)
ATK-MINOR := $(shell cat $(ATK_RELDIR)/MINOR)
ATK-BUILD := $(shell cat $(ATK_RELDIR)/BUILD)
endif

ifeq "$(ATK-BUILD)" ""
     ATK-BUILD:=0
endif
     

ifdef ATK-MAJOR
ATK-VERSION:=$(ATK-MAJOR).$(ATK-MINOR).$(ATK-BUILD)
ATK-RELEASE:=ATK-$(subst .,-,$(ATK-VERSION))
endif




all: util core widget


atk-release:
ifdef ATK-VERSION
	@echo "You are building the ATK version : $(ATK-VERSION)"
	($(CVSSF) tag $(ATK-RELEASE) configure		\
                                .make-include.in	\
                               configure.in	\
                               Makefile &&		\
	 (cd lib &&                                     \
	 $(CVSSF) tag $(ATK-RELEASE) printf.jar jep.jar jepext.jar ij.jar jogl.jar gluegen-rt.jar ) && \
	 (cd src/fr/esrf/tangoatk/ &&			\
         $(CVSSF) tag -R $(ATK-RELEASE) core) &&        \
         (cd src/fr/esrf/tangoatk/ &&			\
         $(CVSSF) tag $(ATK-RELEASE) util)) &&		\
	 (cd src/fr/esrf/tangoatk/ &&			\
         $(CVSSF) tag -R $(ATK-RELEASE) widget)
else
	@echo "Sorry you cannot perform atk-release; ATK_RELDIR should be set!"
endif


core:
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) all)


widgetutil:
	(cd src/fr/esrf/tangoatk/widget/util && $(MAKE) all)

widget:
	(cd src/fr/esrf/tangoatk/widget/ && $(MAKE) all)

util:
	(cd src/fr/esrf/tangoatk/util/ && $(MAKE) all)

test: core-test widget-test

core-test:
	(cd src/fr/esrf/tangoatk/core/t && $(MAKE) test)

widget-test:



clean:
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) clean); \
	(cd src/fr/esrf/tangoatk/widget/ && $(MAKE) clean)


install: 
	install -g dserver lib/ATKCore-$(ATK-VERSION).jar              \
                           lib/ATKWidget-$(ATK-VERSION).jar          \
                           /segfs/tango/release/java/lib              \
	&& (cd /segfs/tango/release/java/lib                        \
            && ln -sf ATKCore-$(ATK-VERSION).jar ATKCore.jar           \
            && ln -sf ATKWidget-$(ATK-VERSION).jar ATKWidget.jar)    \


install-doc: 
	cp -va  doc/* /segfs/tango/doc/www/tango/tango_doc/atk_doc/ 



jar: core-jar  widget-jar



core-jar: core-manifest
	@(cd lib &&                             \
	 $(JAR) xf printf.jar &&      \
	 $(JAR) xf jep.jar &&   \
	 $(JAR) xf jepext.jar &&   \
	 $(JAR) xf ij.jar &&   \
	 $(JAR) xf jogl.jar &&   \
	 $(JAR) xf gluegen-rt.jar &&   \
         if [ -e $(COREMANIFEST) ]; then       \
            $(JAR) cmf $(COREMANIFEST) ATKCore-$(ATK-VERSION).jar fr/esrf/tangoatk/core fr/esrf/tangoatk/util com/braju org/lsmp org/nfunk ij com/sun javax/media; \
         else                                           \
            $(JAR) cf ATKCore-$(ATK-VERSION).jar fr/esrf/tangoatk/core fr/esrf/tangoatk/util com/braju org/lsmp org/nfunk ij com/sun javax/media;                   \
         fi)
	$(call verifyjar,/tmp/core/jar,ATKCore-$(ATK-VERSION))
	@(rm -rf /tmp/core)

widget-jar: widget-manifest
	(cd lib && $(JAR) cmf $(WIDGETMANIFEST) ATKWidget-$(ATK-VERSION).jar fr/esrf/tangoatk/widget); \
	$(call verifyjar,/tmp/widget/jar,ATKWidget-$(ATK-VERSION))
	@(rm -rf /tmp/widget)

verify-widget-jar: widget-jar



desktop:
	@(cd src/com/tomtessier/scrollabledesktop && $(MAKE) all)

desktop-jar: desktop
	@(cd lib &&                             \
	$(JAR) cf  jscrollpane.jar com/tomtessier/scrollabledesktop;\
        )

jdoc:
	(cd src && $(MAKE) jdoc)


manifest: core-manifest widget-manifest

core-manifest:
	echo "Manifest-Version: 1.0" > $(COREMANIFEST) ;  \
	echo "" >> $(COREMANIFEST) ;                       \
	echo "" >> $(COREMANIFEST) ;                       \
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) manifest.mf) ; \

widget-manifest:
	echo "Manifest-Version: 1.0" > $(WIDGETMANIFEST) ;    \
	echo "" >> $(WIDGETMANIFEST) ;                        \
	echo "" >> $(WIDGETMANIFEST) ;                        \
	(cd src/fr/esrf/tangoatk/widget/ && $(MAKE) manifest); \




beanbox:
	$(JAVA) sun.beanbox.BeanBoxFrame





include .make-include
BUILDDIR=/tmp
PROG=runapp
CVSROOT=/segfs/tango/cvsroot
RELDIR=/segfs/tango/tools/jclient_framework/atkbuild

# dont forget to edit /segfs/tango/tools/jclient_framework/atkbuild files
# to set the major and minor releases and set the build number to zero
ATK-MAJOR := $(shell cat $(RELDIR)/MAJOR)
ATK-MINOR := $(shell cat $(RELDIR)/MINOR)
ATK-BUILD := $(shell cat $(RELDIR)/BUILD)


ifeq "$(ATK-BUILD)" ""
     ATK-BUILD:=0
endif

ATK-VERSION:=$(ATK-MAJOR).$(ATK-MINOR).$(ATK-BUILD)

ATK-RELEASE:=ATK-$(subst .,-,$(ATK-VERSION))

all: util core widget 

#atk-release:
#	echo $(ATK-VERSION)
#	echo $(ATK-RELEASE)
#	expr $(ATK-BUILD) + 1 > $(RELDIR)/BUILD
atk-release:
	(cvs tag $(ATK-RELEASE) configure		\
                                .make-include.in	\
                                Makefile &&		\
	 (cd src/fr/esrf/tangoatk/ &&			\
         cvs tag -R $(ATK-RELEASE) core) &&		\
         (cd src/fr/esrf/tangoatk/ &&			\
         cvs tag $(ATK-RELEASE) util)) &&		\
	 (cd src/fr/esrf/tangoatk/ &&			\
         cvs tag -R $(ATK-RELEASE) widget) &&		\
	 expr $(ATK-BUILD) + 1 > $(RELDIR)/BUILD

core:
	(cd src/fr/esrf/tangoatk/core/ && $(MAKE) all)


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
                           lib/jscrollpane.jar                          \
                           /segfs/tango/lib/java/                       \
	&& (cd /segfs/tango/lib/java/                                   \
            && ln -sf ATKCore-$(ATK-VERSION).jar ATKCore.jar           \
            && ln -sf ATKWidget-$(ATK-VERSION).jar ATKWidget.jar)    \


install-doc: 
	cp -va  doc/* /segfs/tango/doc/www/tango/tango_doc/atk_doc/ 


# 
checkin:
	cvs ci

jar: core-jar  widget-jar



core-jar: core-manifest
	@(cd lib &&                             \
         if [ -e $(COREMANIFEST) ]; then       \
            $(JAR) cmf $(COREMANIFEST) ATKCore-$(ATK-VERSION).jar fr/esrf/tangoatk/core fr/esrf/tangoatk/util; \
         else                                           \
            $(JAR) cf ATKCore-$(ATK-VERSION).jar fr/esrf/tangoatk/core fr/esrf/tangoatk/util;                   \
         fi)
	$(call verifyjar,/tmp/core/jar,ATKCore-$(ATK-VERSION))

widget-jar: widget-manifest
	(cd lib && $(JAR) cmf $(WIDGETMANIFEST) ATKWidget-$(ATK-VERSION).jar fr/esrf/tangoatk/widget); \
	$(call verifyjar,/tmp/widget/jar,ATKWidget-$(ATK-VERSION))

verify-widget-jar: widget-jar



desktop:
	@(cd src/com/tomtessier/scrollabledesktop && $(MAKE) all)

desktop-jar: desktop
	@(cd lib &&                             \
	$(JAR) cf  jscrollpane.jar com/tomtessier/scrollabledesktop;\
        )

tag:
	cvs tag -c `cat .version` src 

jdoc:
	(cd src && $(MAKE) jdoc)

etags:
	find src  -name "*.java" | xargs etags

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

build:
	(cd $(BUILDDIR); \
	cvs -d $(CVSROOT) co -r $(ATK-RELEASE) jclient_framework; \
	(cd jclient_framework/ATK; export JAVA_HOME=$(JAVA_HOME) && \
	./configure && make core && make core-test && make util && \
	make widget && make widget-test &&\
	make core-jar && make widget-jar))

ci:
	cvs ci 



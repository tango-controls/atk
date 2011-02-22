include .make-include

PROG=runapp

all:
	(cd src && $(MAKE) all)

clean:
	(cd src && $(MAKE) clean)

install: all 
	(cd readline/native && make install)
	echo '#!/bin/sh' > $(PROG)
	echo '' >> $(PROG)
	echo 'export ATKHOME=' $(ATKHOME) >> $(PROG)
	echo 'export JAVA='$(JAVA) >> $(PROG)
	echo ''   >> $(PROG)
	echo 'export CLASSPATH='$(CLASSPATH) >> $(PROG)
	echo '' >> $(PROG)
	echo $(JAVA) '$$@' >> $(PROG)
	chmod a+x $(PROG)

jar: all test manifest 
	(cd lib && $(JAR) cmf $(MANIFEST) TangoATK.jar fr)

tag:
	cvs tag -c `cat .version`
test:
	(cd src && $(MAKE) test)

jdoc:
	(cd src && $(MAKE) jdoc)

etags:
	find src  -name "*.java" | xargs etags

manifest:
	@rm $(MANIFEST) 2>/dev/null ;                  \
	echo "Manifest-Version: 1.0" >> $(MANIFEST) ;  \
	echo "" >> $(MANIFEST) ;                       \
	echo "" >> $(MANIFEST) ;                       \
	(cd src && $(MAKE) manifest)

beanbox:
	$(JAVA) sun.beanbox.BeanBoxFrame

verifyjar: jar
	@pwd=`pwd`;               \
	jardir=/tmp/tangoatk ;   \
	mkdir -p $$jardir;        \
	cd $$jardir ;             \
	$(JAR) xf $(JTARGETDIR)/TangoATK.jar > /dev/null ; \
	beans=`grep "Name" META-INF/MANIFEST.MF | sed 's/Name: //g' | sed s'/
/ /g'`; \
	error=0; \
	for bean in $$beans; do \
	    beaninfo=`echo $$bean | sed 's/.class/BeanInfo.class/g'`; \
	    if [ ! -e $$bean ]; then    \
		error=1; \
		echo "$$bean is not the jar file!"; \
	    fi; \
	    if [ ! -e $$beaninfo ]; then  \
		error=1; \
	        echo "$$beaninfo is not in the jar file!"; \
	    fi; \
        done  ; \
	rm -rf $$jardir; \
	exit $$error; \

include ../.make-include

jdoc:
	find . \( -name "*.java" -a -not -path "*/crap/*" \) | grep -v examples | grep -v interlock/shape > ./classes
	touch $(JDOCDIR)/package-list
	$(JAVADOC) -overview overview.html -d $(JDOCDIR) @classes
	@echo "-------------------------------------"
	@echo `wc -l classes` documented.
	@echo "-------------------------------------"

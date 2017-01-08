GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	 $(JC) $(JFLAGS) $*.java

CLASSES = \
        MarvelProxy.java \
        MarvelHandler.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	 $(RM) *.class

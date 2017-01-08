GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	 $(JC) $(JFLAGS) $*.java

CLASSES = \
        MarvelProxy.java \
        MarvelHandler.java \
	MarvelThread.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	 $(RM) *.class

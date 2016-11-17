# Welcome to TangoATK.
## Getting started

TangoATK is a collection of java-classes to help build applications
which interact with Tango-devices.

This document does assumes some prior knowledge:

* Tango - it is assumed that you already have a clear understanding of devices, their attributes and their commands.
* Java - There are many excellent tutorials on java, please read them. [O'Reilly](http://java.oreilly.com) have quite a few books on the subject.
* JDK - The Java Development Kit. You are expected to know how to set up a class-path and how to compile and run a java-program.

### Central concepts

TangoATK is not built in a device-centric way. What this means is
that in terms of TangoATK, a device is something that lurks in the
background, but very seldom something that is worked with directly.

Instead we have chosen to look at the attributes and the commands as
the central building blocks. This means that you can get a reference
to an attribute by asking for it. You do not need a reference to the
attributes device to obtain the attribute. The same goes for commands.

Attributes and commands need to be stored somewhere, and for that we
have supplied the `AttributeList~ and
the `CommandList`. These objects take the name of a
command or attribute, fetch them and store them.


### I've got a running device-server, what do I do?

First of all it is important to understand the ideas behind TangoATK.
TangoATK is buildt using several standard [patterns](http://directory.google.com/Top/Computers/Programming/Methodologies/Patterns_and_Anti-Patterns)
where the most prominent one is the [Model View Controller](http://www.google.com/search?hl=en&lr=&safe=off&q=model+view+controller&btnG=Google+Search), or MVC for short. TangoATK supplies the model
and the view, and it is the task of the application programmer to
create the controller.

#### The Model

The model-part of TangoATK is found in the [fr.esrf.tangoatk.core](src/fr/esrf/tangoatk/core/package-summary.html)
package. What we find here are non-graphic classes which are used to
get a hold of devices, attributes, and commands. The objects
representing attributes, devices, and commands, are made to
distribute events to listeners which connect to them. This means that
when eg an attribute changes its value, an event is sent to all
registered listeners of that attribute.


#### The View

The view-part of TangoATK is found in the packages under [fr.esrf.tangoatk.widget](src/fr/esrf/tangoatk/widget/package-summary.html)
There are specific viewers for attributes, commands, and devices.
There are also widgets for viewing errors and such.

#### The Controller

The job of the controller, which normally is the application, is to
connect models and views, and responding to user input.

##### Connecting models and views

Connecting a view to a model is done by calling
the `setModel` method on a view with the model you want to
connect to it as argument:

```java
INumberScalar      model;
SimpleScalarViewer viewer;
AttributeList      list;

list = new AttributeList();
// initialize the model and the viewer, more on that later...
model = (INumberScalar)list.add("my/device/name/numberscalar");

// connect them
viewer.setModel(model);
```

Normally the `setModel` method in a viewer takes care of
adding the viewer to the list of listeners of the model.


### Dependencies

TangoATK is a modular toolkit built on top of other packages. This
means that you will need to have the following packages in your
classpath:

    * TangORB.jar, a library implementing the underlying Tango infrastructure

In addition to this, you'll also need the two jars for TangoATK:

    * ATKCore.jar, the non graphical part of the toolkit
    * ATKWidget.jar, all the viewers and graphical stuff.

Normally you will find all these in your favourite lib/java directory
in your tangodistribution, so assuming $TANGO is pointing to where
you installed tango, this command should set up your CLASSPATH:
```
$ export CLASSPATH=$TANGO/lib/java/TangORB.jar:$TANGO/lib/java/ATKCore.jar:$TANGO/lib/java/ATKWidget.jar
```
It is also worth noting that TangoATK is for the moment made with and for jdk1.4,
but we invite you to use jdk1.5 today because future releases of ATK will be built
with jdk1.5 and might be incompatible with jdk1.4. So you better fetch that from [http://java.sun.com](http://java.sun.com/)
 while you're at it.




### A simple example

TangoATK is attribute and command centric, that is, you can obtain
attributes and commands from any device and mix them without having
to know about their devices. The main entry-points into TangoATK are
the CommandList and the AttributeList, they live in fr.esrf.TangoATK.Core

```java
import fr.esrf.TangoATK.Core.*;


public class FirstExample {


    public static void main (String[] args) {
	AttributeList list = new AttributeList();
	IAttribute attribute;

	// add an attribute to the list
	// AttributeList returs the last attribute added as an
	// IEntity, so we need to cast it to an IAttribute.
	// The AttributeList can throw a ConnectionException if
	// it is not treated nicely, so you need to catch that.
	try {
	    attribute = (IAttribute)list.add("eas/test-api/1/Short_attr");
	    System.out.println("Attribute " + attribute + " added OK");
	} catch (ConnectionException connEx) {
	    System.out.println("Failed to add attributes");
	} // end of try-catch


    } // end of main ()

}

```

AttributeList and CommandList also accept wildcard-entries and
String-arrays as arguments to their add methods.

### A graphic example

```java
import fr.esrf.TangoATK.Core.AttributeList;
import fr.esrf.TangoATK.Core.INumberScalar;
import fr.esrf.TangoATK.Core.ConnectionException;
import fr.esrf.TangoATK.Widget.attribute.SimpleScalarViewer;
import javax.swing.JFrame;

public class SecondExample {

    public static void main (String[] args) {
	AttributeList list = new AttributeList();
	SimpleScalarViewer viewer = new SimpleScalarViewer();
	INumberScalar attribute ;

	try {
	    // This time we need to know what kind of attribute AttributeList
	    // returns. By prior knowledge, I know that the attribute
	    // I'm adding is some sort of a numeric scalar, and therefore
	    // I treat it as a INumberScalar, since all numeric scalars are
	    // INumberScalars.
	    attribute = (INumberScalar)list.add("eas/test-api/1/Att_sinus");

	    // This is the fun part. Here my viewer is informed of who
	    // its model is. The viewer then connects itself to the model,
	    // and things start to happen.
	    viewer.setModel(attribute);

	    // Another fun part of the AttributeList is that it has a
	    // refreshing mechanism, which automagically refreshes any
	    // attributes in the list and makes them send out events.
	    list.startRefresher();

	    // Boring old swing stuff, add the viewer to a frames content-
	    // pane and have the frame show itself.
	    javax.swing.JFrame frame = new javax.swing.JFrame();
	    frame.getContentPane().add(viewer);
	    frame.pack();
	    frame.show();
	} catch (ConnectionException e) {
	    System.out.println("Couldn't add attribute: " + e);
	} // end of try-catch
    }
}
```
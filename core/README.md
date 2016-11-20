This package contains the model part of TangoATK. Core has no knowledge of
its viewers, core only knows how to send events to its listeners.

Most users will only need to access


* AttributeList
* CommandList
* DeviceFactory

to get things started.

# Overview

Most of the code in this package is written to facilitate the
handling of Attributes and Commands. Normally, users of this package
should not try to instanciate attributes and commands by themselves,
but rather use the AttributeList and the CommandList respectively.
For more finegrained control, a CommandFactory and an
AttributeFactory is provided.

## Communication model

The communication model of TangoATK is based on the model used by
java-beans, that is, objects register themselves as listeners to
other object's events. All the listener-interfaces in core are
derived from <code>IErrorListener</code>, so that all listeners are
forced to implement some kind of errorhandling.

## Properties

Both commands and attributes have properties attached to them. Some
properties are common to both commands and attributes, and thus the
hierarchy of classes representing the properties work with both
commands and properties.


There are two ways of obtaining properties from a command or
attribute. Either by calling `getPropertyMap()` which
returns a `java.util.Map` with all propeties, or by
calling specialized methods in the objects, such
as `getName()` which returns the value for a particular property.


## Overview of the attribute hierarchy

The Attributes in TangoATK come in different flavors. As an
abstraction over Tango, all numeric types are coerced into doubles,
and there exists no String types for images, which
leaves us with:

* NumberImage, represented by the interface INumberImage
* NumberSpectrum, represented by the interface INumberSpectrum
* NumberScalar, represented by the interface INumberScalar
* StringScalar, represented by the intervace IStringScalar
* StringSpectrum, represented by the intervace IStringSpectrum
* DevStateScalar, represented by the interface IDevStateScalar
* BooleanScalar, represented by the interface IBooleanScalar
* BooleanSpectrum, represented by the interface IBooleanSpectrum
* BooleanImage, represented by the interface IBooleanImage

All of the interfaces inherit from IAttribute, which is the interface
that defines an attribute.

## Overview of the command hierarchy

Commands are defined by the ICommand inteface, and implemented in the
classes:

* VoidVoidCommand 
* VoidScalarCommand 
* VoidArrayCommand 
* ScalarVoidCommand
* BooleanVoidCommand
* StringVoidCommand
* ScalarScalarCommand
* ScalarArrayCommand
* ArrayVoidCommand
* ArrayScalarCommand
* ArrayArrayCommand

To interpret these names, the type of the input parameter is given as
the first part of the class name, the type of the output parameter is
given as the second part of the class name.

So a command which is of type ScalarArrayCommand takes a scalar value
as input and returns an array as output.

### How to obtain the results of a command

As with everything else in TangoATK, the commands also work with
events, that is, every time a command is executed, it sends out
a `ResultEvent` to all its listeners with the result of
the command.

### The Finer points of commands

To make life easier for the programmer, all types commands accept a
`java.util.List` as input. A command which doesn't take
any input will silently ignore the input, a command which takes a
scalar as input will use only the first value in the list. Array
command will parse the list as best it can.

Regarding input, a command will accept any input to any command,
there are no compiletime checks to make sure a string is not passed
to a command accepting a double allthough this error will be caught
at runtime.
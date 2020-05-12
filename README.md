## ODang
#### _Created by Charles Dang, Marius Kleppe Larnøy and Giovanni Orozco_
ODang is an object-oriented language written in Java that compiles to JavaScript. 

## Getting Started 
You can either: 
1. Download the ODang.jar and run it with `java -jar ODang.jar`
2. Download the source and run it with Maven using `mvn exec:java -D exec.mainClass=ODang`


## Features
Odang implements object-oriented classes and subtyping into JavaScript using prototype based inheritence.
Notable features:
* Object-oriented classes
* Subtyping
* Strong types


## Syntax
ODang uses a Java-like syntax

*Hello World in ODang*
```java
class Foo {
  println("Hello World");
}
```

## Limitations
* No garbage collection
* Limited set of types
* Unlike Java there are no access modifiers, everything is implicitly public 


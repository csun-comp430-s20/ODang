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
ODang uses a Java-like syntax, with some simplifications

Legal types: `int`, `boolean`, `String`, ClassType

*Class Definition*
```
class Foo {}
class Foo extends Bar {}
```
*Class Instance Creation*
```
Foo bar = new Foo();
```
*Method Declaration*
```
int plusOne(int x) {
  return x + 1;
}
```

*Field declarations and access*
```
class Foo {
  int x = 2;
}
class Bar {
  Foo obj = new Foo();
  int y = obj.x + 2;
}
```
## Limitations
* No garbage collection
* Limited set of types
* Unlike Java there are no access modifiers, everything is implicitly public 


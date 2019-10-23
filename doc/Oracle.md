## ![Oracle](oracle.png) Java SE Documentation
Converted by me to markdown - [original page](https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/) (not finished)

# Java Scripting Programmer's Guide

- [Who is the Java Scripting API For?](#Who-is-the-Java-Scripting-API-For?)
- [Scripting Package](#Scripting-Package)
- [Examples](#Examples)
  - ["Hello, World"](#"Hello,-World")
  - [Evaluating a Script File](#Evaluating-a-Script-File)
  - [Script Variables](#Script-Variables)
  - [Invoking Script Functions and Methods](#Invoking-Script-Functions-and-Methods)
  - [Implementing Java Interfaces by Scripts](#Implementing-Java-Interfaces-by-Scripts)
  - [Multiple Scopes for Scripts](#Multiple-Scopes-for-Scripts)
- [JavaScript Script Engine](#JavaScript-Script-Engine)
- [JavaScript to Java Communication](#JavaScript-to-Java-Communication)
  - [Importing Java Packages, Classes](#Importing-Java-Packages,-Classes)
  - [Creating and Using Java Arrays](#Creating-and-Using-Java-Arrays)
  - [Implementing Java Interfaces](#Implementing-Java-Interfaces)
  - [Overload Resolution](#Overload-Resolution)
- [Implementing Your Own Script Engine](#Implementing-Your-Own-Script-Engine)
- [References](#References)

## Who is the Java Scripting API For?

Some useful characteristics of scripting languages are:
- **Convenience**: Most scripting languages are dynamically typed. You can usually create new variables without declaring the variable type, and you can reuse variables to store objects of different types. Also, scripting languages tend to perform many type conversions automatically, for example, converting the number 10 to the text "10" as necessary.
- **Developing rapid prototypes**: You can avoid the edit-compile-run cycle and just use edit-run!
- **Application extension/customization**: You can "externalize" parts of your application - like configuration scripts, business logic/rules and math expressions for financial applications.
- **"Command line" shells for applications** -for debugging, runtime/deploy time configuration etc. Most applications have a web-based GUI configuaration tool these days. But sysadmins/deployers frequently prefer command line tools. Instead of inventing ad-hoc scripting language for that purpose, a "standard" scripting language can be used.

The Java™ Scripting API is a scripting language indepedent framework for using script engines from Java code. With the Java Scripting API, it is possible to write customizable/extendable applications in the Java language and leave the customization scripting language choice to the end user. The Java application developer need not choose the extension language during development. If you write your application with JSR-223 API, then your users can use any JSR-223 compliant scripting language.

[top](#)
___

## Scripting Package

The Java Scripting functionality is in the [`javax.script`](https://docs.oracle.com/javase/7/docs/api/javax/script/package-summary.html) package. This is a relatively small, simple API. The starting point of the scripting API is the `ScriptEngineManager` class. A ScriptEngineManager object can discover script engines through the jar file service discovery mechanism. It can also instantiate ScriptEngine objects that interpret scripts written in a specific scripting language. The simplest way to use the scripting API is as follows:

1. Create a `ScriptEngineManager` object.
2. Get a `ScriptEngine` object from the manager.
3. Evaluate script using the `ScriptEngine`'s eval methods.

Now, it is time to look at some sample code. While it is not mandatory, it may be useful to know a bit of JavaScript to read these examples.


[top](#)
___

## Examples

### "Hello, World"

From the `ScriptEngineManager` instance, we request a JavaScript engine instance using `getEngineByName` method. On the script engine, the `eval` method is called to execute a given String as JavaScript code! For brevity, in this as well as in subsequent examples, we have not shown exception handling. There are checked and runtime exceptions thrown from `javax.script` API. Needless to say, you have to handle the exceptions appropriately.

```java
import javax.script.*;
public class EvalScript {
    public static void main(String[] args) throws Exception {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        engine.eval("print('Hello, World')");
    }
}
```

[top](#)
___

### Evaluating a Script File
In this example, we call the `eval` method that accepts `java.io.Reader` for the input source. The script read by the given reader is executed. This way it is possible to execute scripts from files, URLs and resources by wrapping the relevant input stream objects as readers.

```java
import javax.script.*;
public class EvalFile {
    public static void main(String[] args) throws Exception {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from given file - specified by first argument
        engine.eval(new java.io.FileReader(args[0]));
    }
}
```

Let us assume that we have the file named "test.js" with the following text:

```java
println("This is hello from test.js");
```

We can run the above Java as `java EvalFile test.js`

[top](#)
___

Script Variables
When you embed script engines and scripts with your Java application, you may want to expose your application objects as global variables to scripts. This example demonstrates how you can expose your application objects as global variables to a script. We create a java.io.File in the application and expose the same as a global variable with the name "file". The script can access the variable - for example, it can call public methods on it. Note that the syntax to access Java objects, methods and fields is dependent on the scripting language. JavaScript supports the most "natural" Java-like syntax.

public class ScriptVars {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        File f = new File("test.txt");
        // expose File object as variable to script
        engine.put("file", f);

        // evaluate a script string. The script accesses "file"
        // variable and calls method on it
        engine.eval("print(file.getAbsolutePath())");
    }
}



[top](#)
___

Invoking Script Functions and Methods
Sometimes you may want to call a specific scripting function repeatedly - for example, your application menu functionality might be implemented by a script. In your menu's action event handler you may want to call a specific script function. The following example demonstrates invoking a specific script function from Java code.

import javax.script.*;

public class InvokeScriptFunction {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // JavaScript code in a String
        String script = "function hello(name) { print('Hello, ' + name); }";
        // evaluate script
        engine.eval(script);

        // javax.script.Invocable is an optional interface.
        // Check whether your script engine implements or not!
        // Note that the JavaScript engine implements Invocable interface.
        Invocable inv = (Invocable) engine;

        // invoke the global function named "hello"
        inv.invokeFunction("hello", "Scripting!!" );
    }
}
If your scripting language is object based (like JavaScript) or object-oriented, then you can invoke a script method on a script object.

import javax.script.*;

public class InvokeScriptMethod {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // JavaScript code in a String. This code defines a script object 'obj'
        // with one method called 'hello'.
        String script = "var obj = new Object(); obj.hello = function(name) { print('Hello, ' + name); }";
        // evaluate script
        engine.eval(script);

        // javax.script.Invocable is an optional interface.
        // Check whether your script engine implements or not!
        // Note that the JavaScript engine implements Invocable interface.
        Invocable inv = (Invocable) engine;

        // get script object on which we want to call the method
        Object obj = engine.get("obj");

        // invoke the method named "hello" on the script object "obj"
        inv.invokeMethod(obj, "hello", "Script Method !!" );
    }
}


[top](#)
___

Implementing Java Interfaces by Scripts
Instead of calling specific script functions from Java, sometimes it is convenient to implement a Java interface by script functions or methods. Also, by using interfaces we can avoid having to use the javax.script API in many places. We can get an interface implementor object and pass it to various Java APIs. The following example demonstrates implementing the java.lang.Runnable interface with a script.


import javax.script.*;

public class RunnableImpl {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // JavaScript code in a String
        String script = "function run() { println('run called'); }";

        // evaluate script
        engine.eval(script);

        Invocable inv = (Invocable) engine;

        // get Runnable interface object from engine. This interface methods
        // are implemented by script functions with the matching name.
        Runnable r = inv.getInterface(Runnable.class);

        // start a new thread that runs the script implemented
        // runnable interface
        Thread th = new Thread(r);
        th.start();
    }
}

If your scripting language is object-based or object-oriented, it is possible to implement a Java interface by script methods on script objects. This avoids having to call script global functions for interface methods. The script object can store the "state" associated with the interface implementor.



import javax.script.*;

public class RunnableImplObject {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // JavaScript code in a String
        String script = "var obj = new Object(); obj.run = function() { println('run method called'); }";

        // evaluate script
        engine.eval(script);

        // get script object on which we want to implement the interface with
        Object obj = engine.get("obj");

        Invocable inv = (Invocable) engine;

        // get Runnable interface object from engine. This interface methods
        // are implemented by script methods of object 'obj'
        Runnable r = inv.getInterface(obj, Runnable.class);

        // start a new thread that runs the script implemented
        // runnable interface
        Thread th = new Thread(r);
        th.start();
    }
}


[top](#)
___

Multiple Scopes for Scripts
In the script variables example, we saw how to expose application objects as script global variables. It is possible to expose multiple global "scopes" for scripts. A single scope is an instance of javax.script.Bindings. This interface is derived from java.util.Map<String, Object>. A scope a set of name-value pairs where name is any non-empty, non-null String. Multiple scopes are supported by javax.script.ScriptContext interface. A script context supports one or more scopes with associated Bindings for each scope. By default, every script engine has a default script context. The default script context has atleast one scope called "ENGINE_SCOPE". Various scopes supported by a script context are available through getScopes method.



import javax.script.*;

public class MultiScopes {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        engine.put("x", "hello");
        // print global variable "x"
        engine.eval("println(x);");
        // the above line prints "hello"

        // Now, pass a different script context
        ScriptContext newContext = new SimpleScriptContext();
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        // add new variable "x" to the new engineScope
        engineScope.put("x", "world");

        // execute the same script - but this time pass a different script context
        engine.eval("println(x);", newContext);
        // the above line prints "world"
    }
}



[top](#)
___

JavaScript Script Engine
Oracle's implementation of JDK 7 is co-bundled with the Mozilla Rhino based JavaScript engine which can be used in conjunction with javax.script (JSR-223) API.

This directory and subdirectories contain the Rhino sources with Oracle changes. Mozilla Rhino sources are licensed under Mozilla Public License Version 1.1. You may obtain a copy of this License at http://www.mozilla.org/MPL. Please note that the sources in com.sun.script package and sub-packages are not covered under MPL.

Feature changes in JavaScript implementation
Most of the Rhino implementation is included but there are a few of components of Rhino are not included / changed mainly due to footprint and security reasons. These features are:
JavaScript-to-bytecode compilation (also called "optimizer"): The optimizer is disabled when security manager is present. When security manager is not used, System property "rhino.opt.level" can be defined in the range [-1, 9]. By default, the value is set to -1 which means optimizer is disabled.
Rhino's JavaAdapter has been overridden. JavaAdapter is the feature by which Java class can be extended by JavaScript and Java interfaces may be implemented by JavaScript. We have replaced Rhino's JavaAdapter with our own implementation of JavaAdapter. In our implementation, only single Java interface may be implemented by a JavaScript object. For example, the following works as expected.
       var v = new java.lang.Runnable() {
                    run: function() { print('hello'); }
               }
       v.run();
In most cases, JavaAdapter is used to implement single interface with Java anonymizer class-like syntax. The uses of JavaAdapter to extend a Java class or to implement multiple interfaces are very rare.
The Rhino command line tools (Rhino shell, debugger etc) are not included.

[top](#)
___

JavaScript to Java Communication
For the most part, accessing Java classes, objects and methods is straightforward. In particular field and method access from JavaScript is the same as it is from Java. We highlight important aspects of JavaScript Java access here. For more details, please refer to http://www.mozilla.org/rhino/scriptjava.html. The following examples are JavaScript snippets accessing Java. This section requires knowledge of JavaScript. This section can be skipped if you are planning to use some other JSR-223 scripting language rather than JavaScript.


[top](#)
___

Importing Java Packages, Classes
The built-in functions importPackage and importClass can be used to import Java packages and classes.


// Import Java packages and classes
// like import package.*; in Java
importPackage(java.awt);
// like import java.awt.Frame in Java
importClass(java.awt.Frame);
// Create Java Objects by "new ClassName"
var frame = new java.awt.Frame("hello");
// Call Java public methods from script
frame.setVisible(true);
// Access "JavaBean" properties like "fields"
print(frame.title);

The Packages global variable can be used to access Java packages. Examples: Packages.java.util.Vector, Packages.javax.swing.JFrame. Please note that "java" is a shortcut for "Packages.java". There are equivalent shortcuts for javax, org, edu, com, net prefixes, so pratically all JDK platform classes can be accessed without the "Packages" prefix.

Note that java.lang is not imported by default (unlike Java) because that would result in conflicts with JavaScript's built-in Object, Boolean, Math and so on.

importPackage and importClass functions "pollute" the global variable scope of JavaScript. To avoid that, you may use JavaImporter.


// create JavaImporter with specific packages and classes to import

var SwingGui = new JavaImporter(javax.swing,
                            javax.swing.event,
                            javax.swing.border,
                            java.awt.event);
with (SwingGui) {
    // within this 'with' statement, we can access Swing and AWT
    // classes by unqualified (simple) names.

    var mybutton = new JButton("test");
    var myframe = new JFrame("test");
}



[top](#)
___

Creating and Using Java Arrays
While creating a Java object is the same as in Java, to create Java arrays in JavaScript we need to use Java reflection explicitly. But once created the element access or length access is the same as in Java. Also, a script array can be used when a Java method expects a Java array (auto conversion). So in most cases we don't have to create Java arrays explicitly.


// create Java String array of 5 elements
var a = java.lang.reflect.Array.newInstance(java.lang.String, 5);

// Accessing elements and length access is by usual Java syntax
a[0] = "scripting is great!";
print(a.length);



[top](#)
___

Implementing Java Interfaces
A Java interface can be implemented in JavaScript by using a Java anonymous class-like syntax:


var r  = new java.lang.Runnable() {
    run: function() {
        print("running...\n");
    }
};

// "r" can be passed to Java methods that expect java.lang.Runnable
var th = new java.lang.Thread(r);
th.start();

When an interface with a single method is expected, you can pass a script function directly.(auto conversion)


function func() {
     print("I am func!");
}

// pass script function for java.lang.Runnable argument
var th = new java.lang.Thread(func);
th.start();


[top](#)
___

Overload Resolution
Java methods can be overloaded by argument types. In Java, overload resolution occurs at compile time (performed by javac). When calling Java methods from a script, the script interpreter/compiler needs to select the appropriate method. With the JavaScript engine, you do not need to do anything special - the correct Java method overload variant is selected based on the argument types. But, sometimes you may want (or have) to explicitly select a particular overload variant.


var out = java.lang.System.out;

// select a particular println function
out\["println(java.lang.Object)"\]("hello");

More details on JavaScript's Java method overload resolution is at Java Method Overloading and LiveConnect 3


[top](#)
___

Implementing Your Own Script Engine
We will not cover implementation of JSR-223 compliant script engines in detail. Minimally, you need to implement the javax.script.ScriptEngine and javax.script.ScriptEngineFactory interfaces. The abstract class javax.script.AbstractScriptEngine provides useful defaults for a few methods of the ScriptEngine interface.


[top](#)
___

References
JSR-223 Scripting for the Java Platform
Java Method Overloading and LiveConnect 3
Rhino:JavaScript for Java
Scripting Java (from JavaScript)

[top](#)
___

<a href="https://docs.oracle.com/javase/7/docs/legal/cpyr.html">Copyright © 1993</a>, 2018, Oracle and/or its affiliates. All rights reserved.
<a style="float: right" href="http://docs.oracle.com/javase/feedback.html">Contact Us</a>

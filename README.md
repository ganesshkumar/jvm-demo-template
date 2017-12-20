# jvm-demo-template
> Create OpenFaaS templates for JVM languages

This is a demo of how to create [OpenFaas](https://github.com/openfaas/faas) template for any jvm language with after burn.

Note: For details on after burn refer to 'fast fork' section of [faas/watchdog](https://github.com/openfaas/faas/tree/fast_fork/watchdog#optimizing-for-performance)


## Dependency
> afterburn-java-parser

I have created a java based after burn [parser](https://github.com/ganesshkumar/afterburn-java-parser) that will
* read the incoming request from watchdog over `System.in`
* parse the incoming request, extracts the header and body
* call the function with the request body and method(http method)
* write the function's response to the `System.out`

You can find its artifacts on [Bintray](https://bintray.com/ganesshkumar/openfaas/afterburn-java-parser).

## Creating java template
There are many ways to create a java template. In this tutorial we will see how to create one using the `afterburn-java-parser`

### 1. Using the jar
You can refer to `template/demo-template-jar` folder for the final template.

* In this method we are consuming the `afterburn-java-parser` jar directly.
I have downloaded the `afterburn-java-parser-1.0.0-rc1.jar` from the Bintray and copied it to `libs` folder of the template.
* Next, I created a simple java class `Main.java` that binds the `System.in` and `System.out` to the parser
```
public static void main(String[] args) throws IOException {
    DataInputStream inputStream = new DataInputStream(System.in);
    BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(System.out));

    Parser parser = new Parser();

    while(true) {
        parser.acceptIncoming(inputStream, outputWriter);
    }
}
```
* The `function` folder is a gradle project that defines the function at `function.Handler` class
* The heavy lifting of binding all these pieces is done by Dockerfile
  * Building the function project
  ```
  ADD function /root/src/function
  WORKDIR /root/src/function
  RUN ./gradlew build
  ```
  * Building the `Main` class(placing afterburn-java-parser and function in the classpath)
  ```
  ADD Main.java /root/src/
  ADD libs /root/src/libs
  WORKDIR /root/src
  RUN javac -cp .:libs/afterburn-java-parser-1.0.0-rc1.jar:function/build/libs/java8-gradle-1.0.jar Main.java
  ```
  * Setting the `fprocess` to be run during the execution of the container
  ```
  ENV fprocess="java -cp .:libs/afterburn-java-parser-1.0.0-rc1.jar:function/build/libs/java8-gradle-1.0.jar Main"
  ```

### 2. Using the gradle project
You can refer to `template/demo-template-using-gradle` folder for the final template.

The above method is simple but many may not prefer the presence of jar in their project and wants to fetch it during the build time.
In this method the template itself is a gradle project.

* In our template's gradle project, we have added the gradle shadow plugin to produce a fat jar
```
plugins {
  id 'com.github.johnrengelman.shadow' version '2.0.2'
}
```
* To build the template project, the Dockerfile uses
```
ADD . /root/src/
WORKDIR /root/src
RUN ./gradlew clean shadow
```
This produces a fat jar in `build/libs` folder.
* In the Dockerfile the `fprocess` is set to
```
ENV fprocess="java -jar jvm-demo-template-1.0.0-all.jar"
```

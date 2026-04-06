---
# vim:spell
title: Write safe native code for JVM with Rust
author: Mateusz Rzeszutek
date: 14th May 2026
aspectratio: 169
theme: Copenhagen
colortheme: seahorse
fonttheme: professionalfonts
fontsize: 12pt
urlcolor: blue
linkstyle: bold
---

# Who am I?

::: columns

:::: column
![OTel](img/opentelemetry-horizontal-color.png)\

![Elastic](img/elastic.png)\
::::

:::: column
I’m an emeritus maintainer of
the [OpenTelemetry Instrumentation for Java](https://github.com/open-telemetry/opentelemetry-java-instrumentation).

Currently working for Elastic as part of the Core JVM Infra team.
::::

:::

# What we're going to talk about

- Why even use native code in the first place;
- A little bit about the "old" way of using native code, i.e Java Native Interface (_JNI_);
- Then, the Foreign Function and Memory (_FFM_) API, added recently in JDK 22;
- And how to write the native parts in Rust, not just plain C.

# Why use native extensions in Java?

- Performance
  - Low level memory control: custom memory layouts, pointer arithmetics
  - Single Instruction Multiple Data (_SIMD_)
  - Predictability, avoiding GC overhead
- Code reuse
  - Native libraries with thin Java API layer
- System-level capabilities
  - Low level system APIs (e.g. Netty and `epoll`/`kqueue`)

# Why NOT use native extensions in Java?

- Complexity: need separate binaries per OS/architecture
- Complexity: C/C++ quirks and undefined behaviors
- Complexity: memory management and ownership
- Debugging is a major pain
- You can crash the JVM easily
- There is an extra overhead when crossing native/JVM boundary, and JVM optimizations won't apply
- And lots of others...

# The old way: JNI

## Java Native Method

```java
class JniExample {
  static native String sayHello(String name);
}
```

# The old way: JNI

## Generate C/C++ headers

```sh
javac -h hello_native/src \
  src/main/java/org/example/JniExample.java
```

## Implement C/C++ native method

```c
JNIEXPORT jstring JNICALL Java_org_example_JniExample_sayHello(
        JNIEnv *, jclass, jstring) {
  // ...
}
```

# The old way: JNI

## Set library path

```sh
java -Djava.library.path=/path/to/cpp/output \
  -jar jniexample.jar
```

## Load native library

```java
System.loadLibrary("hello_native");
```

## 
$\rightarrowtail IDE$

# JNI: the good, the bad and the ugly

::: columns

:::: column

## Good

- Access to native ecosystems
- Performance (in case of careful usage)
- Low-level control

::::

:::: column

## Bad, ugly

- Unsafe, very easy to shoot yourself in the foot
- Tricky interactions with GC, memory management, ownership
- The glue code (between JVM and native) is in C/C++
- Java exceptions vs error codes
- Lack of JVM optimisations, extra overhead when crossing boundary
- And so on ...

::::

:::

# Java Foreign Function and Memory API

# FFM vs JNI

# FFM examples

# Passing Java code to Rust: HTTP server

# Thank you!

::: columns

:::: column

## Presentation sources

<https://github.com/mateuszrzeszutek/write-safe-native-code-for-jvm-with-rust>

::::

:::: column

## QR Code

![QR](img/presentation-repo-qr.png){height=75%}\

::::

:::

# Q&A


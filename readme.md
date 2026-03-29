# Write safe native code for JVM with Rust

In this talk we will talk about how to glue JVM with one of my favorite languages as of late, Rust.

We will:

- Compare JNI (Java Native Interface) and the newly added FFM (Foreign Function and Memory) API;
- Introduce the FFM API;
- Run native code examples for both;
- Show what to pay attention to when using Rust as the implementation language of the native part.

All with running code examples and unit tests.

## Tags

java, jvm, rust, ffm, jni

## Building slides

```sh
make install-deps
make
```

## Installing necessary tools

Make sure mise is installed (e.g. `brew install mise`, `sudo dnf -y install mise`).

```sh
mise install
```

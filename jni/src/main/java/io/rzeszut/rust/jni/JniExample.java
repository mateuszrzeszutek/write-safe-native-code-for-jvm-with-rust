package io.rzeszut.rust.jni;

public class JniExample {

  static native String sayHello(String name);

  static void main() {
    System.loadLibrary("hello_native");
    System.out.println("[Java] " + sayHello("world") + "!");
  }
}

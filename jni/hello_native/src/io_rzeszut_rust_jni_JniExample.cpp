#include <iostream>
#include <print>
#include <string>

#include "io_rzeszut_rust_jni_JniExample.hpp"

JNIEXPORT jstring JNICALL Java_io_rzeszut_rust_jni_JniExample_sayHello(
    JNIEnv *env, jclass cls, jstring name) {

  const char *c_name = env->GetStringUTFChars(name, nullptr);
  if (c_name == nullptr) {
    return nullptr;
  }

  std::println(std::cout, "[C++] name length: {}", env->GetStringUTFLength(name));
  std::cout << std::flush;

  std::string hello = "Hello ";
  hello.append(c_name);
  env->ReleaseStringUTFChars(name, c_name);
  return env->NewStringUTF(hello.c_str());
}

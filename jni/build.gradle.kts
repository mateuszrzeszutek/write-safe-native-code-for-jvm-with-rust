plugins {
  id("java-conventions")
  application
}

tasks {
  withType(JavaExec::class) {
    dependsOn(":jni:hello_native:assemble")
  }
}

application {
  mainClass = "io.rzeszut.rust.jni.JniExample"
  applicationDefaultJvmArgs += listOf(
    "--enable-native-access=ALL-UNNAMED",
    "-Djava.library.path=${project(":jni:hello_native").projectDir}/build/lib/main/debug"
  )
}

import org.gradle.internal.jvm.Jvm

plugins {
  `cpp-library`
}

library {
  source.from(file("src"))
}

tasks.withType(CppCompile::class.java).configureEach {
  val javaHome = Jvm.current().javaHome
  compilerArgs.add("-std=c++23")
  compilerArgs.add("-I${javaHome.absolutePath}/include")
  compilerArgs.add("-I${javaHome.absolutePath}/include/darwin")
  compilerArgs.add("-I${javaHome.absolutePath}/include/linux")
}

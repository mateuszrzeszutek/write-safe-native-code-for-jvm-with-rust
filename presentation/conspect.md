<!-- vim:spell
-->

# Who am I?

...

# What we're going to talk about

I made this talk cause I wanted to somehow tie together Java and the JVM, which I've worked with for quite a long time; and Rust, which has recently become one of my favorite new languages.
Rust's guiding principle is safety and lack of undefined behavior, which makes is a really good choice for system-level language, that you might want to use to implement the native bits of your system in.

In this talk, we'll dive a bit deeper into why you'd want to use native extensions in the first place -- and why not as well.
We'll explore the "old" way of doing things, the Java Native Interface, then the "new" way -- the Foreign Function and Memory API, a recent addition.
Lastly, we'll talk about how to integrate Rust code with the JVM.

# Why use native extensions in Java?

Java/JVM really allows you do to most things without even touching native stuff, the times when you actually _need_ to do that probably account for less than 1% of Java projects out there.

That being said, there are several good reasons for why you'd want to use native extensions.

1. Let's start with the classic one: performance. Some of the projects that decide to use native extensions do that because they can squeeze out extra performance from that.
  - Embedded databases are often written in C/C++ (DuckDB, SQLite, RocksDb) and offer a thin Java bindings via JNI. 
  - Databases make use of custom memory layouts and vectorization.
  - While vectorization is something that's currently being worked on in the JVM as part of the Vector API, it's still an incubating API.
2. The other one is code reuse: there are tools/libraries that were already written in C/C++/Rust and it doesn't make any sense to rewrite them in Java -- this'd incur a lot of engineering effort, and most likely introduce a ton of new bugs while doing that.
  - Embedded databases are a good example of that.
  - But it's not only that, there are also bindings for compression algorithms (like zstd), OpenSSL, cryptography libraries etc.
3. Another common reason is the possibility to use low-level system APIs. Netty is a good example of that, it uses `epoll` on Linux or `kqueue` on MacOS/Darwin for native event loops.

In a way, they all mostly are about various facets of performance: whether it's about very specific memory layouts, reusing already written code, or calling system libraries, or any other reason.

**TODO**: how does Elasticsearch use native code?

Still, using native code does not automatically mean improved performance: as usual, there are trade-offs, there is some overhead when crossing the JVM/native boundary (e.g. native calls cannot be inlined, are not subject to JIT). If you want to use native extensions in Java, you should have good performance tests that validate that they don't actually make things worse.

# Why NOT use native extensions in Java?

There are tons of reasons why you'd want to avoid doing anything native.

- Using native extensions increases the overall complexity significantly, on multiple different fronts:
  - You have to compile and distribute different native binaries per OS and/or architecture
  - You might need to have different native code per OS/architecture
  - You have to deal with complexity related to working in a system-level language, whether it's C or C++ or Rust or Zig, they all have their quirks and things you need to be careful about
  - You have to be careful with memory management and ownership
- And all of that will be a pain because if your native code fails, you're gonna get a segfault -- no exception stacktraces, no thread dumps, or anything else that would normally help you debug the issue that JVM provides.
- And, you can very easily cause the whole JVM process to crash.
- And even if you are very careful about your native code, there still is extra overhead when you cross the native/JVM boundary, so even if it's all correct, it still might be less efficient than it'd be if you simply implemented that in good ole' Java.

Native extensions are a powerful tool, but one you have to be very careful with when you're using it. Let's take a look at the "old" way of interacting with native extensions in the JVM.

# The old way: JNI

Alright, how do you work with JNI? You probably have seen methods marked as `native`, this is the Java interface that marks the call to the native counterpart.

Say we have that `sayHello` method here, based on that class, you can generate the C/C++ header file that you'll have to provide an implementation for.
You do that via the `javac -h` command, and it'll generate a file that contains a similar C++ function definition to what you can see here.
As you can see, the name of the function is pretty much the path to the Java native method, including package and class name.
It receives a string as a parameter, and returns a string as well.
We'll go into a little more detail in a minute or so, once we switch to the code.

Then, you have to load the native library that you just implemented, and to do that, you call the `System.loadLibrary` method.
You have to tell the JVM where to look for the library, hence you have to set the java.library.path system property.

Going to the code now.

# JNI: the good, the bad and the ugly

To sum up: there are a few good sides to JNI, I've talked about most of them in the general context of native extensions, JNI shares most of the good sides.

JNI has lots of pitfalls specific to it though:
- It's very unsafe, it basically hands you a couple pointers and wishes "good luck". It's very easy to introduce all sorts of memory related bugs: double free, use-after-free, leaking both native C memory as well as JVM-managed heap memory, pinning references, and so on.
- Most often JNI is used as a bridge between already existing native libraries/APIs and the JVM. The fact that you have to write all the glue code in C/C++, and not any of the much safer JVM languages, makes the glue code attract much more bugs. Combine that with the raw pointer style API, and you have quite a thrilling mix.
- Most C or system level APIs signalize errors by returning error codes -- or setting a global, like `errno`. This is way less friendly/elegant than throwing an exception, and that makes error checking a bit of a pain. And, you probably want to throw exceptions for your Java code anyways.
- As mentioned before, native extensions aren't free, and they introduce an overhead whenever you cross the boundary. You also lose some of the JVM optimizations.

The list could go on really, but what's important here is that at least couple of these problems are fixed/improved upon in the Foreign Function and Memory API. Let's take a closer look at it, and see how it improves the JNI model of native extension development.


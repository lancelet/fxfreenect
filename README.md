fxfreenect
==========

JavaFX and ScalaFX wrapper for libfreenect.

Note on Platforms
-----------------

In theory, fxfreenect can be compiled under OS X, Windows and Linux.  However,
currently, during early development, it has only been configured to compile
under OS X Mavericks (10.9).  It is not difficult to target other platforms
though, and pull requests are very welcome.

Compiling under OS X
--------------------

These instructions are for compiling under OS X Mavericks (10.9).

1. Make sure that you have OS X Mavericks and the XCode developer tools
installed.  This [answer](http://stackoverflow.com/a/18216866) on 
StackOverflow explains how to install the developer tools.
2. Install `git`, `autoconf`, `automake`, `cmake`, `maven` and `sbt`.  These
tools can be installed using the [HomeBrew](http://brew.sh) package manager
as follows:
```

brew install git autoconf automake cmake maven sbt
```
3. Clone the git repository:
```

cd < some appropriate directory on your machine >
git clone https://github.com/lancelet/fxfreenect.git
```
4. Build the native library and Java JNA wrapper:
```

cd fxfreenect/native-libs
./build-osx.sh
```
Then confirm that the required files are present in the `build/osx/lib` 
directory:
```

ls build/osx/lib
freenect-0.0.1-SNAPSHOT.jar  libfreenect.0.1.2.dylib
```
Both the jar file and the dylib file must be present.
5. Compile the JavaFX / ScalaFX wrappers and demonstration applications using
SBT:
```

sbt
compile
```

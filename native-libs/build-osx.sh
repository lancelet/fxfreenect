#!/bin/bash
#
# COMPILES NATIVE LIBRARIES UNDER OS X
#
# REQUIRES:
#  (OS X developer tools)
#  autoconf
#  automake
#  cmake
#  maven

# Source paths.
LIBUSB_SRC_DIR=$(pwd)/libusbx
LIBFREENECT_SRC_DIR=$(pwd)/libfreenect

# Build paths.
# These directories specify locations in which native files will be built.
BUILD_DIR=$(pwd)/build
BUILD_PREFIX=$BUILD_DIR/install-prefix
BUILD_LIBUSB=$BUILD_DIR/build-libusbx
BUILD_LIBFREENECT=$BUILD_DIR/build-libfreenect
BUILD_LIBFREENECT_JAVA=$BUILD_DIR/build-libfreenect-java
BUILD_LIB=$BUILD_DIR/osx/lib

# Paths to toolchain.
# These are some packages which are required for building (not an exhaustive
# list).  If any of these files are missing, they might be installed using
# HomeBrew, MacPorts or Fink.
BIN_AUTOCONF=$(which autoconf)
BIN_AUTOMAKE=$(which automake)
BIN_CMAKE=$(which cmake)
BIN_MVN=$(which mvn)

# Check requirements
# This just makes sure that the files above are found, and that OSX Maverics
# (10.9) is being used.  If you are NOT compiling under Maverics, you may 
# need to uncomment the check.  Please send a pull request if you build 
# successfully on a different OS X version.
if [ -z BIN_AUTOCONF ]; then echo "autoconf was not found!";    exit -1; fi
if [ -z BIN_AUTOMAKE ]; then echo "automake was not found!";    exit -1; fi
if [ -z BIN_CMAKE ];    then echo "cmake was not found!";       exit -1; fi
if [ -z BIN_MVN ];      then echo "maven (mvn) was not found!"; exit -1; fi
osx_prod_ver="`sw_vers | grep ProductVersion | sed -e 's/ProductVersion:[[:space:]]//g'`"
if [ "$osx_prod_ver" != "10.9" ]; then
  echo "This script has only been tested on OS X Mavericks 10.9"
  echo "Please comment out this check in the script to continue!"
  exit -1
fi

# Create build directories
echo "=== CREATING BUILD DIRECTORIES ==="
mkdir -pv $BUILD_PREFIX $BUILD_LIBUSB $BUILD_LIBFREENECT $BUILD_LIBFREENECT_JAVA \
          $BUILD_LIB

# Build libusb.
# The library is copied into its build directory since it doesn't really have
# a good option for out-of-source-tree compilation, and we don't want to
# pollute the git submodule with build files.
# We build only the static version of libusb, which will be included statically
# within the dynamic libfreenect.
echo "=== COPYING LIBUSB ==="
cp -R $LIBUSB_SRC_DIR/* $BUILD_LIBUSB/.
pushd $BUILD_LIBUSB
echo "=== CONFIGURING LIBUSB ==="
./autogen.sh
./configure --enable-shared=no --prefix=$BUILD_PREFIX
echo "=== MAKING LIBUSB ==="
make -j6
make install
popd

# Build libfreenect (native library).
# Libfreenect allows an out-of-source-tree build, so we use that.
# Libfreenect is built as a shared library, but it statically includes libusb.
pushd $BUILD_LIBFREENECT
echo "=== CONFIGURING LIBFREENECT (NATIVE) ==="
cmake \
  -DLIBUSB_1_INCLUDE_DIR=$BUILD_PREFIX/include/libusb-1.0 \
  -DLIBUSB_1_LIBRARY=$BUILD_PREFIX/lib/libusb-1.0.a \
  -DCMAKE_INSTALL_PREFIX=$BUILD_PREFIX \
  -DBUILD_C_SYNC=OFF \
  -DBUILD_EXAMPLES=OFF \
  -DBUILD_FAKENECT=OFF \
  -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_SHARED_LINKER_FLAGS='-framework IOKit -framework CoreFoundation -lobjc' \
  -G "Unix Makefiles" \
  $LIBFREENECT_SRC_DIR
echo "=== MAKING LIBFREENECT (NATIVE) ==="
make -j6
make install
popd

# Build libfreenect Java wrapper.
# We turn off running Maven tests to get a build without errors.
echo "=== COPYING LIBFREENECT (JAVA) ==="
cp -R $LIBFREENECT_SRC_DIR/wrappers/java/* $BUILD_LIBFREENECT_JAVA/.
pushd $BUILD_LIBFREENECT_JAVA
echo "=== MAKING LIBFREENECT (JAVA) ==="
mvn -Dmaven.test.skip=true package
popd

# Copy required libraries to their special location (BUILD_LIB).
echo "=== COPYING LIBRARIES (to $BUILD_LIB) ==="
cp $BUILD_PREFIX/lib/libfreenect.0.1.2.dylib $BUILD_LIB/.
ln -s libfreenect.0.1.2.dylib $BUILD_LIB/libfreenect.dylib
cp $BUILD_LIBFREENECT_JAVA/target/freenect-0.0.1-SNAPSHOT.jar $BUILD_LIB/.

# Clean up
echo "=== CLEANING UP ==="
rm -rf $BUILD_PREFIX
rm -rf $BUILD_LIBUSB
rm -rf $BUILD_LIBFREENECT
rm -rf $BUILD_LIBFREENECT_JAVA

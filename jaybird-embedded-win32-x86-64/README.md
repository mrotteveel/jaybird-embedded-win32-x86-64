# Firebird Embedded Windows x86-64 for Jaybird

This is an experimental library to provide Firebird embedded on the classpath of 
Java applications. It requires Jaybird 5.0.0 or higher.

For now, we only provide releases for Windows x86-64, but we will try to release
a variant for Linux x86-64 in the future.

Build information
-----------------

### Version ###

The version has 4 components. The first three are the Firebird version that
sourced the libraries (eg 4.0.2). The last part is a 'build' identifier, which
should usually be 0. The 'build' identifier may be incremented for patches or
new platforms added. 
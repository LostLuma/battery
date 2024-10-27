Battery
=======

Java 8+ JNI wrapper to the [starship battery](https://crates.io/crates/starship-battery) crate.

Usage
-----

The library can be installed from the `releases` repository on [maven.lostluma.net](https://maven.lostluma.net/#/releases/net/lostluma/battery) in two variants:

- `default`: Downloads, validates, and caches the dynamic library on demand. Saves bandwidth and disk space.
- `bundled`: Contains the dynamic library for all platforms. Recommended for situations in which first startup may be offline.

The dynamic library is comes prebuilt for the following platforms:

|         | aarch64 | amd64 | riscv64 |
| ------- | ------- | ----- | ------- |
| Linux   | yes     | yes   | yes     |
| MacOS   | yes     | yes   |         |
| Windows | yes     | yes   |         |

Misc
----

This is my first time using both Rust and JNI :)

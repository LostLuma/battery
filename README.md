# Battery

Java 8+ JNI wrapper to the [starship battery](https://crates.io/crates/starship-battery) crate.

## Usage

The library can be installed from the `releases` repository on [maven.lostluma.net](https://maven.lostluma.net/#/releases/net/lostluma/battery) in two variants:

- `default`: Downloads, validates, and caches the dynamic library on demand. Saves bandwidth and disk space.
- `bundled`: Contains the dynamic library for all platforms. Recommended if first application startup may be offline.

The dynamic library is comes prebuilt for the following platforms:

|         | aarch64 | amd64 | riscv64 |
| ------- | ------- | ----- | ------- |
| Linux   | yes     | yes   | yes     |
| MacOS   | yes     | yes   |         |
| Windows | yes     | yes   |         |

Running on other platforms is also possible, however some manual setup is required:  
First build the project with Cargo, then set the `battery.natives.path` system property to the file path.

## Misc

This is my first time using both Rust and JNI :)

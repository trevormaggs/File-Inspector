# Java FileSystem Inspection Library

A lightweight Java library for inspecting files and directories and retrieving filesystem information such as file type, size, timestamps, permissions, and platform-specific attributes.

It provides a simple common API while also allowing access to additional Windows and POSIX filesystem information when available.

## Features

-   **Simple file inspection:** Use `FileInspector` as the main entry point for inspecting files and directories.
-   **Windows file attributes:** Access Windows attributes such as Hidden, System, Compressed, Encrypted, Sparse, and more through `DosView`.
-   **POSIX/Unix information:** Access Unix/Linux/macOS metadata including UID, GID, file modes, and permission strings through `PosixView`.
-   **Platform-specific information:** Use the `.as(Class<T> type)` method to access additional information when it is supported by the filesystem.
-   **Diagnostic output:** Built-in `toString()` methods provide readable summaries of file metadata.
-   **Java 8 compatible:** Written for Java 8 with no external dependencies.

## Architecture

The library is built on four core pillars:

1. **`FileAttributes` (Interface):** The common contract for all file nodes.
2. **`FileInspector` (Static Factory):** Detects the filesystem capabilities and returns the appropriate implementation.
3. **`AbstractFileNode` (Base Class):** Handles shared logic like path normalisation and attribute snapshots.
4. **`DosView` & `PosixView` (Capability Interfaces):** Provide access to deep, platform-specific attributes.

## How It Works

The library provides a common `FileAttributes` interface for basic file
information.

`FileInspector` examines the file and returns an appropriate implementation. Additional platform-specific information can be accessed through `DosView` or `PosixView` when supported.

This keeps the basic API simple while still providing access to detailed filesystem information.

## Requirements

-   Java 8 or later
-   Windows for Windows-specific `DosView` attributes
-   A POSIX-compatible filesystem for `PosixView` information

## Installation

Simply include the `filesystem` package source files in your Java 8 project. No external JARs are required.

## Usage

### Basic Inspection

``` java
import filesystem.*;
import java.io.IOException;

try
{
    FileAttributes node = FileInspector.inspect("data/archive.zip");

    // Print the formatted diagnostic summary
    System.out.println(node.toString());
}
catch (IOException e)
{
    e.printStackTrace();
}
```

### Windows File Attributes

Windows-specific information is available through `DosView` when supported:

``` java
Optional<DosView> dos = node.as(DosView.class);

if (dos.isPresent())
{
    DosView view = dos.get();

    if (view.isCompressed())
    {
        System.out.println("NTFS Compression is enabled.");
    }

    System.out.println("Attributes String: " + view.getAttributesString());
}
```

### POSIX Metadata

POSIX information is available through `PosixView` when supported:

``` java
Optional<PosixView> nix = node.as(PosixView.class);

if (nix.isPresent())
{
    PosixView view = nix.get();

    System.out.println("Permissions: " + view.getPermissionsString());
    System.out.println("Numeric UID: " + view.getUID());
}
```

## API Reference

### FileInspector

-   `static FileAttributes inspect(Path path, boolean followSymlink)`
-   `static FileAttributes inspect(String pathString)`

### Common Attributes (`FileAttributes`)

-   `getName()`: Returns the filename.
-   `getOriginalPath()`: Returns the path as originally provided.
-   `getAbsolutePath()`: Returns the normalised, absolute path.
-   `getRealPath()`: Resolves symbolic links to the final target on disk.
-   `as(Class<T> type)`: Returns an `Optional<T>` for a supported specialised view.
-   `toString()`: Returns a multi-line, formatted diagnostic summary.

## Changelog

### April 2026

-   Migrated to static factory pattern.
-   Implemented `Optional` adapters.
-   Simplified Win32 constants to short-hex format (`0x1` vs. `0x00000001`).
-   Added aligned `StringBuilder` diagnostic output in `toString()`.

## Author

Developed by **Trevor Maggs**.

## Licence

Internal / Proprietary

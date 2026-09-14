# Java FileSystem Inspection Library

A high-quality, comprehensive Java 8 library for cross-platform filesystem attribute inspection. This library provides a unified API for accessing deep platform-specific file metadata (Windows DOS attributes and POSIX permissions) using a type-safe **Static Factory** and **Adapter** pattern.

## Features

- **Static Factory Design:** Central entry point via `FileInspector` for automatic OS/Filesystem detection.
- **Deep Windows Support:** Access to 20+ Win32 file attribute constants (Hidden, System, Compressed, Encrypted, Sparse, etc.) via `DosView`, now using optimised short-hex literals.
- **POSIX Excellence:** Comprehensive Unix/Linux/macOS metadata including numeric UID/GID, octal modes, and permission strings.
- **Fluent Adapter Pattern:** Use the `.as(Class<T> type)` method to safely downcast to platform-specific views without risky `instanceof` checks.
- **Diagnostic Formatting:** Built-in `toString()` overrides provide beautifully aligned, human-readable summaries of all file metadata.
- **Java 8 Compatibility:** Written strictly for Java 8 standards, requiring no external dependencies.

## Architecture

The library is built on four core pillars:

1. **`FileAttributes` (Interface):** The common contract for all file nodes.
2. **`FileInspector` (Static Factory):** Detects the filesystem capabilities and returns the appropriate implementation.
3. **`AbstractFileNode` (Base Class):** Handles shared logic like path normalisation and attribute snapshots.
4. **`DosView` & `PosixView` (Capability Interfaces):** Provide access to deep, platform-specific attributes.

## Installation

Simply include the `filesystem` package source files in your Java 8 project. No external JARs are required.

## Usage

### Basic Inspection & Diagnostic Output
```java
import filesystem.*;
import java.io.IOException;

try
{
    FileAttributes node = FileInspector.inspect("data/archive.zip");
    // Print the beautifully formatted diagnostic summary
    System.out.println(node.toString());
} 
catch (IOException e)
{
    e.printStackTrace();
}
```

### Advanced Windows Attributes
```java
Optional<DosView> dos = node.as(DosView.class);
if (dos.isPresent())
{
    DosView view = dos.get();
    if (view.isCompressed())
    {
        System.out.println("NTFS Compression is enabled.");
    }
    // Accessing the cleaned-up hex mask
    System.out.println("Attributes String: " + view.getAttributesString());
}
```

### POSIX Metadata
```java
Optional<PosixView> nix = node.as(PosixView.class);
if (nix.isPresent())
{
    PosixView view = nix.get();
    System.out.println("Permissions: " + view.getPermissionsString()); // e.g., -rwxr-xr-x
    System.out.println("Numeric UID: " + view.getUID());
}
```

## API Reference

### FileInspector
- `static FileAttributes inspect(Path path, boolean followSymlink)`
- `static FileAttributes inspect(String pathString)`

### Common Attributes (FileAttributes)
- `getName()`: Returns the filename.
- `getOriginalPath()`: Returns the path as originally provided.
- `getAbsolutePath()`: Returns the normalised, absolute path.
- `getRealPath()`: Resolves symbolic links to the final target on disk.
- `as(Class<T> type)`: Returns an `Optional<T>` adapter for specialised views.
- `toString()`: Returns a multi-line, formatted diagnostic summary.

## Requirements

- Java 8 or later
- Windows for `DosView` functionality
- POSIX-compatible filesystem for `PosixView` functionality

## Changelog
- **April 2026:** - Migrated to static factory pattern.
    - Implemented `Optional` adapters.
    - Simplified Win32 constants to short-hex format (`0x1` vs `0x00000001`).
    - Added aligned `StringBuilder` diagnostic output in `toString()`.

## Author
Developed by **Trevor Maggs**.

## Licence
Internal / Proprietary

---
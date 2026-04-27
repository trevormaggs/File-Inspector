# Java FileSystem Inspection Library

A high-quality, comprehensive Java 8 library for cross-platform filesystem attribute inspection. This library provides a unified API for accessing deep platform-specific file metadata (Windows DOS attributes and POSIX permissions) using a type-safe **Static Factory** and **Adapter** pattern.

## Features

- **Static Factory Design:** Centralised entry point via `FileInspector` for automatic OS/Filesystem detection.
- **Deep Windows Support:** Access to 20+ Win32 file attribute constants (Hidden, System, Compressed, Encrypted, Sparse, etc.) via `DosView`.
- **POSIX Excellence:** Comprehensive Unix/Linux/macOS metadata including numeric UID/GID, octal modes, and permission strings.
- **Fluent Adapter Pattern:** Use the `.as(Class<T> type)` method to safely downcast to platform-specific views without risky `instanceof` checks.
- **Robust Path Handling:** Internal normalisation and absolute path tracking alongside original path preservation.
- **Java 8 Compatibility:** Written strictly for Java 8 standards (no lambdas or functional interfaces).

## Architecture

The library is built on four core pillars:

1. **`FileAttributes` (Interface):** The common contract for all file nodes.
2. **`FileInspector` (Static Factory):** Detects the filesystem capabilities and returns the appropriate implementation.
3. **`AbstractFileNode` (Base Class):** Handles shared logic like path normalisation and attribute snapshots.
4. **`DosView` & `PosixView` (Capability Interfaces):** Provide access to deep, platform-specific attributes.



## Installation

Simply include the `filesystem` package source files in your Java 8 project.

## Usage

### Basic Inspection
```java
import filesystem.*;
import java.io.IOException;

try
{
    AbstractFileNode node = FileInspector.inspect("config.xml");
    System.out.println("File: " + node.getName());
    System.out.println("Size: " + node.size() + " bytes");
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
    System.out.println("Raw Attribute Mask: " + view.getAttributesMask());
}
```

### POSIX Metadata
```java
Optional<PosixView> nix = node.as(PosixView.class);
if (nix.isPresent())
{
    PosixView view = nix.get();
    System.out.println("Owner UID: " + view.getUID());
    System.out.println("Group GID: " + view.getGID());
    System.out.println("Permissions: " + view.getPermissions()); // e.g., rwxr-xr-x
}
```

## API Reference

### FileInspector
- `static AbstractFileNode inspect(String name)`
- `static AbstractFileNode inspect(Path path, boolean followSymlink)`

### FileAttributes
- `getName()`: Returns the filename.
- `getPath()`: Returns the path as originally provided to the factory.
- `getAbsolutePath()`: Returns the normalised, absolute path.
- `getRealPath()`: Resolves symbolic links to the final target on disk.
- `as(Class<T> type)`: Returns an `Optional<T>` adapter for specialised views.
- `brokenSymLink()`: Reliably detects dead symbolic links by comparing snapshot attributes with real-time disk state.

## Change Logs
- **April 2026:** Migrated to static factory pattern, implemented `Optional` adapters, and added comprehensive Win32 attribute bitmasking.

## Author
Developed by **Trevor Maggs**.

## Licence
Internal / Proprietary

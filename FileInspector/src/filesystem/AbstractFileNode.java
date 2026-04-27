package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

/**
 * A base implementation of the {@link FileAttributes} interface. This class provides a
 * platform-independent snapshot of file metadata captured at the time of instantiation. It handles
 * path normalisation and serves as the parent for OS-specific implementations.
 *
 * @author Trevor Maggs
 * @version 1.0
 * @since 27 April 2026
 */
public abstract class AbstractFileNode implements FileAttributes
{
    protected final Path fpath;
    protected final Path originalPath;
    protected final BasicFileAttributes attrs;
    protected final LinkOption[] options;

    /**
     * Constructs a new instance used to capture a snapshot of the file's attributes.
     *
     * @param path
     *        the path to the file to inspect
     * @param followSymlink
     *        if true, follows symbolic links to the target file, or if false, retrieves attributes
     *        for the link itself.
     * @throws IOException
     *         if the file does not exist or cannot be accessed
     */
    protected AbstractFileNode(Path path, boolean followSymlink) throws IOException
    {
        this.originalPath = path;
        this.fpath = path.toAbsolutePath().normalize();
        this.options = followSymlink ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        this.attrs = Files.readAttributes(path, BasicFileAttributes.class, options);
    }

    /**
     * Retrieves the original path provided at construction.
     *
     * @return the original {@link Path} object, which may be relative or absolute
     */
    @Override
    public Path getOriginalPath()
    {
        return originalPath;
    }

    /**
     * Retrieves the absolute and normalised version of the path.
     *
     * @return The absolute {@link Path} of the file
     */
    @Override
    public Path getAbsolutePath()
    {
        return this.fpath;
    }

    /**
     * Retrieves the name of the file or directory.
     *
     * @return the simple filename string. Returns an empty string if the path represents the root
     *         directory
     */
    @Override
    public String getName()
    {
        return (fpath.getFileName() == null ? "" : fpath.getFileName().toString());
    }

    /**
     * Retrieves the size of the file.
     *
     * @return the size of the file in bytes. Note that for directories, this value is
     *         platform-dependent and does not reflect the sum of its contents
     */
    @Override
    public long size()
    {
        return attrs.size();
    }

    /**
     * Resolves the actual location of the file on disk.
     *
     * If the file is a symbolic link, this method attempts to find the final target. If resolution
     * fails due to an I/O error, the absolute normalised path is returned.
     *
     * @return a {@link Path} representing the real, absolute location of the file
     */
    @Override
    public Path getRealPath()
    {
        try
        {
            if (attrs.isSymbolicLink())
            {
                return fpath.toRealPath();
            }
        }
        catch (IOException exc)
        {
            // Silently fail and fallback to fpath
        }

        return fpath;
    }

    /**
     * Retrieves the string representation of the real path.
     *
     * @return the absolute path string resolved to its real location
     */
    @Override
    public String toRealPathString()
    {
        return getRealPath().toString();
    }

    /**
     * Checks if the file attributes were successfully captured.
     *
     * @return {@code true} if the internal attribute snapshot is present
     */
    @Override
    public boolean exists()
    {
        return (attrs != null);
    }

    /**
     * Checks if the path represents a directory.
     *
     * @return {@code true} if the file is a directory
     */
    @Override
    public boolean isDirectory()
    {
        return attrs.isDirectory();
    }

    /**
     * Checks if the path represents a regular file.
     *
     * @return {@code true} if the file is a regular file (not a directory, link, or device)
     */
    @Override
    public boolean isRegularFile()
    {
        return attrs.isRegularFile();
    }

    /**
     * Checks if the file is a special system object.
     *
     * @return {@code true} if the file is an "other" type, such as a pipe, socket, or device
     */
    @Override
    public boolean isOtherFile()
    {
        return attrs.isOther();
    }

    /**
     * Checks if the file is a symbolic link.
     *
     * @return {@code true} if the path points to a symbolic link
     */
    @Override
    public boolean isSymLink()
    {
        return attrs.isSymbolicLink();
    }

    /**
     * Identifies symbolic links that point to non-existent targets.
     *
     * @return {@code true} if the file is a symbolic link but its target cannot be found on the
     *         filesystem
     */
    @Override
    public boolean brokenSymLink()
    {
        return (attrs.isSymbolicLink() && Files.notExists(fpath));
    }

    /**
     * Retrieves the time the file was created.
     *
     * @return the creation time expressed in milliseconds since the epoch
     */
    @Override
    public long creationTime()
    {
        return attrs.creationTime().toMillis();
    }

    /**
     * Retrieves the time the file was last accessed.
     *
     * @return the last access time expressed in milliseconds since the epoch
     */
    @Override
    public long lastAccessTime()
    {
        return attrs.lastAccessTime().toMillis();
    }

    /**
     * Retrieves the time the file content was last modified.
     *
     * @return the last modified time expressed in milliseconds since the epoch
     */
    @Override
    public long lastModifiedTime()
    {
        return attrs.lastModifiedTime().toMillis();
    }

    /**
     * Retrieves a unique identifier for the file if supported by the filesystem.
     *
     * @return an opaque {@link Object} representing the file key, or {@code null}
     *
     *         TODO: Come back to it and make it more simpler to explain its use
     */
    @Override
    public Object fileKey()
    {
        return attrs.fileKey();
    }

    /**
     * Attempts to cast this node to a specific view interface (e.g., DosView or PosixView).
     *
     * @param <T>
     *        the type of view requested
     * @param type
     *        the class of the view to attempt to cast to
     * @return an {@link Optional} containing the view if compatible, otherwise empty
     */
    @Override
    public <T> Optional<T> as(Class<T> type)
    {
        return type.isInstance(this) ? Optional.of(type.cast(this)) : Optional.empty();
    }
}
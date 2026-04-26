package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public abstract class AbstractFileNode implements FileAttributes
{
    protected final Path fpath;
    protected final Path originalPath;
    protected final BasicFileAttributes attrs;

    protected AbstractFileNode(Path path) throws IOException
    {
        this(path, Files.readAttributes(path, BasicFileAttributes.class));
    }

    protected AbstractFileNode(Path path, BasicFileAttributes attrs)
    {
        this.originalPath = path;
        this.fpath = path.toAbsolutePath().normalize();
        this.attrs = attrs;
    }

    @Override
    public Path getPath()
    {
        return originalPath;
    }

    @Override
    public String getName()
    {
        return (fpath.getFileName() == null ? "" : fpath.getFileName().toString());
    }

    @Override
    public long size()
    {
        return attrs.size();
    }

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
            // Do nothing
        }

        return fpath;
    }

    @Override
    public String toRealPathString()
    {
        return getRealPath().toString();
    }

    @Override
    public boolean exists()
    {
        return (attrs != null);
    }

    @Override
    public boolean isDirectory()
    {
        return attrs.isDirectory();
    }

    @Override
    public boolean isRegularFile()
    {
        return attrs.isRegularFile();
    }

    @Override
    public boolean isOtherFile()
    {
        return attrs.isOther();
    }

    @Override
    public boolean isSymLink()
    {
        return attrs.isSymbolicLink();
    }

    @Override
    public boolean brokenSymLink()
    {
        return (attrs.isSymbolicLink() && Files.notExists(fpath));
    }

    @Override
    public long creationTime()
    {
        return attrs.creationTime().toMillis();
    }

    @Override
    public long lastAccessTime()
    {
        return attrs.lastAccessTime().toMillis();
    }

    @Override
    public long lastModifiedTime()
    {
        return attrs.lastModifiedTime().toMillis();
    }

    @Override
    public Object fileKey()
    {
        return attrs.fileKey();
    }

    @Override
    public <T> Optional<T> as(Class<T> type)
    {
        return type.isInstance(this) ? Optional.of(type.cast(this)) : Optional.empty();
    }

    public Path getAbsolutePath()
    {
        return this.fpath;
    }
}
package filesystem;

import java.nio.file.Path;
import java.util.Optional;

public interface FileAttributes
{
    public Path getOriginalPath();
    public Path getAbsolutePath();
    public String getName();
    public long size();
    public Path getRealPath();
    public String toRealPathString();
    public boolean exists();
    public boolean isDirectory();
    public boolean isRegularFile();
    public boolean isOtherFile();
    public boolean isSymLink();
    public boolean brokenSymLink();
    long creationTime();
    long lastAccessTime();
    long lastModifiedTime();
    Object fileKey();
    public <T> Optional<T> as(Class<T> type);
}
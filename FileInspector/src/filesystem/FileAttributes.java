package filesystem;

import java.nio.file.Path;
import java.util.Optional;

interface FileAttributes
{
    Path getOriginalPath();
    Path getAbsolutePath();
    String getName();
    long size();
    Path getRealPath();
    String toRealPathString();
    String getPermissionsString();
    boolean exists();
    boolean isDirectory();
    boolean isRegularFile();
    boolean isOtherFile();
    boolean isSymLink();
    boolean brokenSymLink();
    long creationTime();
    long lastAccessTime();
    long lastModifiedTime();
    Object fileKey();
    <T> Optional<T> as(Class<T> type);
}
package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;

/**
 * A POSIX-specific implementation of a file node, providing metadata for Unix-like systems.
 * 
 * <p>
 * This class retrieves standard POSIX attributes, including owner, group, permissions and leverages
 * the {@code unix:*} attribute view to extract low-level system data such as numeric UIDs, GIDs,
 * and the raw file mode bit-mask.
 * </p>
 * 
 * @author Trevor Maggs
 * @version 1.0
 * @since 27 April 2026
 */
public final class PosixNode extends AbstractFileNode implements PosixView
{
    private final PosixFileAttributes posixAttrs;
    private final Map<String, Object> attrMap;

    /**
     * Package-private constructor used by the {@code FileInspector} factory.
     * 
     * @param path
     *        the path to the file or directory
     * @param followSymlink
     *        whether to follow symbolic links during attribute capture
     * 
     * @throws IOException
     *         if the file is inaccessible or the filesystem is not POSIX-compliant
     */
    PosixNode(Path path, boolean followSymlink) throws IOException
    {
        super(path, followSymlink);

        this.posixAttrs = (PosixFileAttributes) this.attrs;
        this.attrMap = Files.readAttributes(path, "unix:*", this.options);
    }

    /**
     * Returns the name of the user that owns the file.
     * 
     * @return the owner name
     */
    @Override
    public String getOwner()
    {
        return posixAttrs.owner().getName();
    }

    /**
     * Returns the name of the group that owns the file.
     * 
     * @return the group name
     */
    @Override
    public String getGroup()
    {
        return posixAttrs.group().getName();
    }

    /**
     * Returns the permissions of the file in rwxrwxrwx string format.
     * 
     * @return the string representation of POSIX permissions
     */
    @Override
    public String getPermissions()
    {
        return PosixFilePermissions.toString(posixAttrs.permissions());
    }

    /**
     * Returns the raw numeric mode (permissions and type bits) of the file, typically represented
     * in octal (e.g., 0755).
     * 
     * @return the integer mode, for example: 0100644 for a regular file
     */
    @Override
    public int getMode()
    {
        return (int) attrMap.getOrDefault("mode", 0);
    }

    /**
     * Retrieves the numeric User ID (UID) of the owner.
     * 
     * @return the UID integer, or {@link #UNKNOWN_ID} if not available
     */
    @Override
    public int getUID()
    {
        return (int) attrMap.getOrDefault("uid", UNKNOWN_ID);
    }

    /**
     * Retrieves the numeric Group ID (GID) of the group.
     * * @return the GID integer, or {@link #UNKNOWN_ID} if not available
     */
    @Override
    public int getGID()
    {
        return (int) attrMap.getOrDefault("gid", UNKNOWN_ID);
    }

    /**
     * Extracts the file type character by applying the {@code S_IFMT} mask to the raw file mode.
     * 
     * @return a POSIX style character representing the POSIX file type (e.g., 'd', 'l', '-')
     */
    @Override
    public char toPosixTypeChar()
    {
        int type = (getMode() & S_IFMT);

        switch (type)
        {
            case S_IFDIR:
                return 'd';
            case S_IFLNK:
                return 'l';
            case S_IFCHR:
                return 'c';
            case S_IFBLK:
                return 'b';
            case S_IFSOCK:
                return 's';
            case S_IFIFO:
                return 'p';
            case S_IFREG:
                return '-';
            default:
                return '?';
        }
    }

    /**
     * Generates a full 10-character POSIX permission string.
     * 
     * <p>
     * Example output: {@code -rwxr-xr-x}
     * </p>
     * 
     * @return the full type and POSIX permission string
     */
    @Override
    public String getPermissionsString()
    {
        int mode = getMode();
        StringBuilder sb = new StringBuilder(10);

        sb.append(toPosixTypeChar());

        // User
        sb.append((mode & S_IRUSR) != 0 ? 'r' : '-');
        sb.append((mode & S_IWUSR) != 0 ? 'w' : '-');
        sb.append((mode & S_IXUSR) != 0 ? 'x' : '-');

        // Group
        sb.append((mode & S_IRGRP) != 0 ? 'r' : '-');
        sb.append((mode & S_IWGRP) != 0 ? 'w' : '-');
        sb.append((mode & S_IXGRP) != 0 ? 'x' : '-');

        // Others
        sb.append((mode & S_IROTH) != 0 ? 'r' : '-');
        sb.append((mode & S_IWOTH) != 0 ? 'w' : '-');
        sb.append((mode & S_IXOTH) != 0 ? 'x' : '-');

        return sb.toString();
    }

    /**
     * Generates a formatted diagnostic summary of the POSIX attributes.
     * 
     * @return a formatted string containing all POSIX metadata
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String sectionHeader = isDirectory() ? "POSIX Directory Attributes" : "POSIX File Attributes";
        String nameLabel = isDirectory() ? "[Directory Name]" : "[File Name]";

        sb.append(String.format("[%s]%n", sectionHeader));
        sb.append(String.format("  %-20s %s%n", nameLabel, getName()));
        sb.append(String.format("  %-20s %s%n", "[Real Path]", toRealPathString()));
        sb.append(String.format("  %-20s %s%n", "[Directory]", isDirectory()));
        sb.append(String.format("  %-20s %s%n", "[Regular File]", isRegularFile()));
        sb.append(String.format("  %-20s %s%n", "[Other File]", isOtherFile()));
        sb.append(String.format("  %-20s %s%n", "[Symlink]", isSymLink()));
        sb.append(String.format("  %-20s %s%n", "[Broken Link]", brokenSymLink()));
        sb.append(String.format("  %-20s %s bytes%n", "[File size]", size()));

        // POSIX Specific
        sb.append(String.format("  %-20s %s%n", "[Owner]", getOwner()));
        sb.append(String.format("  %-20s %s%n", "[Group]", getGroup()));
        sb.append(String.format("  %-20s %d:%d%n", "[UID:GID]", getUID(), getGID()));
        sb.append(String.format("  %-20s %s (%04o)%n", "[Permissions]", getPermissionsString(), getMode()));

        return sb.toString();
    }
}
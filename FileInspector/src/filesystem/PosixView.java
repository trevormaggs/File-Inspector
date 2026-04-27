package filesystem;

/**
 * Provides a comprehensive view of POSIX-specific file attributes (Unix, Linux, macOS). This
 * interface exposes standard POSIX attributes such as owner, group, and permissions, as well as
 * extended information like numeric UIDs, GIDs, and the raw octal mode.
 */
public interface PosixView
{
    // --- File Type Constants (Octal) ---
    int S_IFMT = 0170000; // Bit mask for the file type bit field
    int S_IFSOCK = 0140000; // Socket
    int S_IFLNK = 0120000; // Symbolic link
    int S_IFREG = 0100000; // Regular file
    int S_IFBLK = 0060000; // Block device
    int S_IFDIR = 0040000; // Directory
    int S_IFCHR = 0020000; // Character device
    int S_IFIFO = 0010000; // FIFO

    // --- Permission Constants (Octal) ---
    int S_IRUSR = 00400; // User Read
    int S_IWUSR = 00200; // User Write
    int S_IXUSR = 00100; // User Execute

    int S_IRGRP = 00040; // Group Read
    int S_IWGRP = 00020; // Group Write
    int S_IXGRP = 00010; // Group Execute

    int S_IROTH = 00004; // Other Read
    int S_IWOTH = 00002; // Other Write
    int S_IXOTH = 00001; // Other Execute

    /**
     * Returns the name of the user that owns the file.
     * 
     * @return the owner name
     */
    String getOwner();

    /**
     * Returns the name of the group that owns the file.
     * 
     * @return the group name
     */
    String getGroup();

    /**
     * Returns the permissions of the file in rwxrwxrwx string format.
     * 
     * @return the string representation of POSIX permissions
     */
    String getPermissions();

    /**
     * Returns the raw numeric mode (permissions and type bits) of the file. typically represented
     * in octal (e.g., 0755).
     * 
     * @return the integer mode
     */
    int getMode();

    /**
     * Returns the numeric User Identifier (UID) of the file owner.
     * 
     * @return the integer UID
     */
    int getUID();

    /**
     * Returns the numeric Group Identifier (GID) of the file group.
     * 
     * @return the integer GID
     */
    int getGID();

    /**
     * Returns the standard character representation of the file type. (e.g., 'd' for directory, 'l'
     * for link, '-' for regular file).
     * 
     * @return the POSIX type character
     */
    char toPosixTypeChar();

    /**
     * Returns the human-readable string representation of the file type and permissions, for
     * example: "-rwxr-xr-x"
     * 
     * @return the POSIX permission string
     */
    String getPermissionsString();
}
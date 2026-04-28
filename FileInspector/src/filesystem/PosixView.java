package filesystem;

/**
 * Provides a comprehensive view of POSIX-specific file attributes (Unix, Linux, macOS).
 * 
 * <p>
 * This interface exposes standard POSIX metadata and low-level mode bit-masks, such as owner,
 * group, and permissions, as well as extended information like numeric UIDs, GIDs, and the raw
 * octal mode. The numeric constants and bitwise logic used here are based on the standard Unix
 * {@code <sys/stat.h>} C header definitions.
 * </p>
 * 
 * @see <a href="https://github.com/openbsd/src/blob/master/sys/sys/stat.h">OpenBSD sys/stat.h
 *      source</a>
 * @author Trevor Maggs
 * @version 1.0
 * @since 27 April 2026
 */
public interface PosixView
{
    /** Standard value returned when a numeric ID (UID/GID) cannot be retrieved */
    int UNKNOWN_ID = -1;

    /* --- File Type Constants (Octal) --- */
    int S_IFMT = 0170000; // Bit mask for the file type bit field
    int S_IFSOCK = 0140000; // Socket
    int S_IFLNK = 0120000; // Symbolic link
    int S_IFREG = 0100000; // Regular file
    int S_IFBLK = 0060000; // Block device
    int S_IFDIR = 0040000; // Directory
    int S_IFCHR = 0020000; // Character device
    int S_IFIFO = 0010000; // FIFO

    /* --- Permission Constants (Octal) --- */
    int S_IRUSR = 00400; // User Read
    int S_IWUSR = 00200; // User Write
    int S_IXUSR = 00100; // User Execute

    int S_IRGRP = 00040; // Group Read
    int S_IWGRP = 00020; // Group Write
    int S_IXGRP = 00010; // Group Execute

    int S_IROTH = 00004; // Other Read
    int S_IWOTH = 00002; // Other Write
    int S_IXOTH = 00001; // Other Execute

    String getOwner();
    String getGroup();
    String getPermissions();
    int getMode();
    int getUID();
    int getGID();
    char toPosixTypeChar();
    String getPermissionsString();
}
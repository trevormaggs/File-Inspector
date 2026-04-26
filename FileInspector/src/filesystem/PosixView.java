package filesystem;

/**
 * Provides a comprehensive view of POSIX-specific file attributes (Unix, Linux, macOS). This
 * interface exposes standard POSIX attributes such as owner, group, and permissions, as well as
 * extended information like numeric UIDs, GIDs, and the raw octal mode.
 */
public interface PosixView
{
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
     * Returns the standard character representation of the file type. (e.g., 'd' for directory, 'l'
     * for link, '-' for regular file).
     * 
     * @return the POSIX type character
     */
    char toPosixTypeChar();

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
     * Returns the raw numeric mode (permissions and type bits) of the file. typically represented
     * in octal (e.g., 0755).
     * 
     * @return the integer mode
     */
    int getMode();
}
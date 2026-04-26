package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;

public final class PosixNode extends AbstractFileNode implements PosixView
{
    private final PosixFileAttributes posixAttrs;
    private final Map<String, Object> attrMap;

    public PosixNode(Path path, boolean followSymlinks) throws IOException
    {
        this(path, followSymlinks, (followSymlinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS}));
    }

    private PosixNode(Path path, boolean follow, LinkOption[] options) throws IOException
    {
        this(path, Files.readAttributes(path, PosixFileAttributes.class, options), options);
    }

    private PosixNode(Path path, PosixFileAttributes attrs, LinkOption[] options) throws IOException
    {
        super(path, attrs);

        this.posixAttrs = attrs;
        this.attrMap = Files.readAttributes(path, "unix:*", options);
    }

    @Override
    public String getOwner()
    {
        return posixAttrs.owner().getName();
    }

    @Override
    public String getGroup()
    {
        return posixAttrs.group().getName();
    }

    @Override
    public String getPermissions()
    {
        return PosixFilePermissions.toString(posixAttrs.permissions());
    }

    /**
     * Returns the standard character representation of the file type.
     * 
     * <p>
     * Supported characters:
     * <ul>
     * 
     * <li>'d' - Directory</li>
     * <li>'l' - Symbolic Link</li>
     * <li>'s' - Socket</li>
     * <li>'p' - Named Pipe (FIFO)</li>
     * <li>'b' - Block Special Device</li>
     * <li>'c' - Character Special Device</li>
     * <li>'-' - Regular File</li>
     * </ul>
     * 
     * @return the POSIX type character
     */
    @Override
    public char toPosixTypeChar()
    {
        if (posixAttrs.isDirectory())
        {
            return 'd';
        }

        else if (posixAttrs.isSymbolicLink())
        {
            return 'l';
        }

        if (posixAttrs.isOther())
        {
            if ((boolean) attrMap.getOrDefault("isSocket", false))
            {
                return 's';
            }

            else if ((boolean) attrMap.getOrDefault("isFifo", false))
            {
                return 'p';
            }

            else if ((boolean) attrMap.getOrDefault("isBlockDevice", false))
            {
                return 'b';
            }

            else if ((boolean) attrMap.getOrDefault("isCharacterDevice", false))
            {
                return 'c';
            }
        }

        return '-';
    }

    @Override
    public int getUID()
    {
        return (int) attrMap.get("uid");
    }

    @Override
    public int getGID()
    {
        return (int) attrMap.get("gid");
    }

    @Override
    public int getMode()
    {
        return (int) attrMap.get("mode");
    }
}
package rsb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Manifest;

/**
 * Provides version information about RSB.
 *
 * @author jwienke
 */
public final class Version {

    private static final Version INSTANCE = new Version();

    private String versionString;
    private String lastCommit;

    private Version() {
        super();

        // get implementation version from jar if available
        final Package pack = this.getClass().getPackage();
        if (pack == null || pack.getImplementationVersion() == null) {
            this.versionString = "unknown";
        } else {
            this.versionString = getClass().getPackage()
                    .getImplementationVersion();
        }

        // get last commit from jar Manifest if available
        final Manifest manifest = this.getManifest();
        if (manifest == null
                || manifest.getMainAttributes().getValue("Last-Commit") == null) {
            this.lastCommit = "archive";
        } else {
            this.lastCommit = manifest.getMainAttributes().getValue(
                    "Last-Commit");
        }

    }

    private Manifest getManifest() {
        final Class<?> clazz = this.getClass();
        final String className = clazz.getSimpleName() + ".class";
        final URL classResource = clazz.getResource(className);
        if (classResource == null) {
            return null;
        }
        final String classPath = classResource.toString();
        if (!classPath.startsWith("jar")) {
            // class not from JAR
            return null;
        }
        final String manifestPath = classPath.substring(0,
                classPath.lastIndexOf('!') + 1)
                + "/META-INF/MANIFEST.MF";
        try {
            return new Manifest(new URL(manifestPath).openStream());
        } catch (final MalformedURLException e) {
            return null;
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the only version instance available
     */
    public static Version getInstance() {
        return INSTANCE;
    }

    /**
     * Returns a string describing this version in the usual major.minor.patch
     * format.
     *
     * @return version string or <code>"unknown"</code> if not able to determine
     */
    public String getVersionString() {
        return this.versionString;
    }

    /**
     * Returns a string describing the last commit on the project resulting in
     * this version.
     *
     * @return commit string in repo-specific format or <code>"archive"</code>
     *         if not known.
     */
    public String getLastCommit() {
        return this.lastCommit;
    }

}

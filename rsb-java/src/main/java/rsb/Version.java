/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
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

    private static final String LAST_COMMIT_ENTRY = "Last-Commit";

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
                || manifest.getMainAttributes().getValue(LAST_COMMIT_ENTRY) == null) {
            this.lastCommit = "archive";
        } else {
            this.lastCommit = manifest.getMainAttributes().getValue(
                    LAST_COMMIT_ENTRY);
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

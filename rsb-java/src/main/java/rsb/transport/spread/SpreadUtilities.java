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
package rsb.transport.spread;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import rsb.Scope;

/**
 * Utility methods for the spread-based transport.
 *
 * @author jwienke
 */
public final class SpreadUtilities {

    /**
     * Maximum length of a spread group name.
     */
    public static final int MAX_GROUP_NAME_LENGTH = 31;

    /**
     * Mask to extract the first byte of an integer.
     */
    private static final int BYTE_EXTRACTION_MASK = 0xFF;

    /**
     * Length of an MD5 hash.
     */
    private static final int MD5SUM_LENGTH = 16;

    /**
     * Mutex to synchronize on when using the MD5 message digest, which is a
     * global variable.
     */
    private static final Object MD5_DIGEST_MUTEX = new Object();

    private SpreadUtilities() {
        // prevent initialization of helper class
        super();
    }

    /**
     * Converts a {@link Scope} to an md5 hashed group name used in the RSB
     * spread protocol as spread groups.
     *
     * @param scope
     *            scope to create group name
     * @return truncated md5 hash to fit into a spread group
     */
    // AvoidThrowingRawExceptionTypes: just a safety network for something that
    // should never happen
    @SuppressWarnings({ "PMD.UseStringBufferForStringAppends",
            "PMD.AvoidThrowingRawExceptionTypes" })
    public static String spreadGroupName(final Scope scope) {

        byte[] sum;
        synchronized (MD5_DIGEST_MUTEX) {

            try {
                final MessageDigest digest = MessageDigest.getInstance("md5");
                digest.reset();
                digest.update(scope.toString().getBytes());
                sum = digest.digest();
            } catch (final NoSuchAlgorithmException e) {
                assert false : "There must be an md5 algorith available";
                throw new RuntimeException("Unable to find md5 algorithm", e);
            }

        }
        assert sum.length == MD5SUM_LENGTH;

        final StringBuilder hexString = new StringBuilder();
        for (final byte element : sum) {
            String hexRep = Integer.toHexString(BYTE_EXTRACTION_MASK & element);
            if (hexRep.length() == 1) {
                hexRep = '0' + hexRep;
            }
            hexString.append(hexRep);
        }

        return hexString.toString().substring(0, MAX_GROUP_NAME_LENGTH);

    }

}

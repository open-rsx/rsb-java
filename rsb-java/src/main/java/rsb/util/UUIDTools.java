/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * UUID helper functions.
 *
 * @author swrede
 */
public final class UUIDTools {

    /**
     * Length of a byte array representing a UUID.
     */
    public static final int UUID_BYTE_REP_LENGTH = 16;

    private static Object synchronizer = new Object();
    private static MessageDigest digester = null;

    private UUIDTools() {
        // prevent initialization of helper class
        super();
    }

    /**
     * Generates name-based URIs according to Version 5 (SHA-1).
     *
     * @param namespace
     *            Namespace UUID
     * @param name
     *            Actual name to be encoded in UUID
     * @return Byte buffer with V5 UUID
     */
    // AvoidThrowingRawExceptionTypes: just a safety network for something that
    // should never happen
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public static UUID
            getNameBasedUUID(final UUID namespace, final String name) {

        synchronized (synchronizer) {

            // hash initialization
            if (digester == null) {
                try {
                    digester = MessageDigest.getInstance("SHA-1");
                } catch (final NoSuchAlgorithmException e) {
                    assert false;
                    throw new RuntimeException(
                            "Couldn't instantiate SHA-1 algorithm", e);
                }
            }

            // the actual hashing
            digester.reset();
            digester.update(UUIDTools.toByteArray(namespace));
            digester.update(name.getBytes());

            byte[] bytes =
                    Arrays.copyOfRange(digester.digest(), 0,
                            UUID_BYTE_REP_LENGTH);

            bytes = setVersionAndVariant(bytes);

            // mask byte and set corresponding bits
            // select clock_seq_hi_and_reserved (octet 8)

            // return byte buffer
            return fromByteArray(bytes);

        }

    }

    // CHECKSTYLE.OFF: MagicNumber - we don not want to make this code more
    // clumsy than necessary for typical bit constants.

    /**
     * Updates byte vector with version 5 information and return the updated
     * byte array.
     *
     * @param bytes
     *            vector to update
     * @return updated byte vector
     */
    // LongVariable: no natural way to shorten variables respecting their
    // original name
    @SuppressWarnings({ "PMD.UseVarargs", "PMD.LongVariable",
            "PMD.LinguisticNaming" })
    private static byte[] setVersionAndVariant(final byte[] bytes) {
        // Constants and bit manipulation from:
        // http://svn.apache.org/repos/asf/commons/sandbox/id/trunk/

        // Byte position of the clock sequence and reserved field
        final short timeHiAndVersionByte6 = 6;
        // Byte position of the clock sequence and reserved field
        final short clockSeqHiAndReservedByte8 = 8;

        // Version five constant for indicating UUID version
        // Different to above mentioned code, otherwise version
        // information is still misleading (set to 3)
        final int versionFive = 5;
        bytes[timeHiAndVersionByte6] &= 0x0F;
        bytes[timeHiAndVersionByte6] |= versionFive << 4;

        // Set variant
        bytes[clockSeqHiAndReservedByte8] &= 0x3F; // 0011 1111
        bytes[clockSeqHiAndReservedByte8] |= 0x80; // 1000 0000

        return bytes;
    }

    /**
     * Creates an ID from a byte representation.
     *
     * @param bytes
     *            byte representation of the id.
     * @return the generated {@link UUID} instance
     */
    @SuppressWarnings("PMD.UseVarargs")
    public static UUID fromByteArray(final byte[] bytes) {

        assert bytes.length == UUID_BYTE_REP_LENGTH;

        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < UUID_BYTE_REP_LENGTH / 2; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = UUID_BYTE_REP_LENGTH / 2; i < UUID_BYTE_REP_LENGTH; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }

    /**
     * Returns the bytes representing the id.
     *
     * @param id
     *            {@link UUID} instance to serialize
     * @return byte representing the id (length 16)
     */
    @SuppressWarnings("PMD.ShortVariable")
    public static byte[] toByteArray(final UUID id) {

        final long msb = id.getMostSignificantBits();
        final long lsb = id.getLeastSignificantBits();
        final byte[] buffer = new byte[UUID_BYTE_REP_LENGTH];

        for (int i = 0; i < UUID_BYTE_REP_LENGTH / 2; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = UUID_BYTE_REP_LENGTH / 2; i < UUID_BYTE_REP_LENGTH; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;

    }

    // CHECKSTYLE.ON: MagicNumber

}

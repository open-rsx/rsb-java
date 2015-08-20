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
package rsb.util;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;

/**
 * A class with helper methods to convert different byte array representations.
 *
 * @author jwienke
 */
public final class ByteHelpers {

    private ByteHelpers() {
        // prevent initialization of utility class
    }

    /**
     * Converts a {@link ByteBuffer} instance to a plain byte array. This method
     * respects whether the buffer is read-only or has an underlying array
     * available. In cases where an array is present and can be accessed, this
     * array will be returned. Otherwise a copy is created.
     *
     * @param buffer
     *            the buffer to convert
     * @return array with contents of the buffer. Should not be modified usually
     */
    public static byte[] byteBufferToArray(final ByteBuffer buffer) {

        if (buffer.hasArray()) {
            return buffer.array();
        }

        final byte[] array = new byte[buffer.remaining()];
        buffer.get(array);
        return array;

    }

    /**
     * Converts a {@link ByteBuffer} to a {@link ByteString}.
     *
     * @param buffer
     *            buffer to convert
     * @return {@link ByteString} instance with the same contents. Might be
     *         coupled to the passed in {@link ByteBuffer} and reflect changes
     *         on that buffer.
     */
    public static ByteString buteBufferToByteString(final ByteBuffer buffer) {
        return ByteString.copyFrom(buffer);
    }

}

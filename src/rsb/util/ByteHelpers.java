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

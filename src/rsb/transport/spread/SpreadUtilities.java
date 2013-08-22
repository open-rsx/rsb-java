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

    private static final Object MD5_DIGEST_MUTEX = new Object();

    /**
     * Maximum length of a spread group name.
     */
    public static final int MAX_GROUP_NAME_LENGTH = 31;

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
    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
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
        assert sum.length == 16;

        final StringBuilder hexString = new StringBuilder();
        for (final byte element : sum) {
            String hexRep = Integer.toHexString(0xFF & element);
            if (hexRep.length() == 1) {
                hexRep = '0' + hexRep;
            }
            hexString.append(hexRep);
        }

        return hexString.toString().substring(0, MAX_GROUP_NAME_LENGTH);

    }

}

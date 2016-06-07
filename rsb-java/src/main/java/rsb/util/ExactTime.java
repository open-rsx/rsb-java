/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 CoR-Lab, Bielefeld University
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

import java.util.logging.Level;
import java.util.logging.Logger;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.annotations.Out;
import jnr.ffi.annotations.Transient;

/**
 * Utility class to access the underlying system clock with improved precision
 * compared to the standard Java library capabilities. Various native methods
 * are probed for accessing the time source. If none of them works, this class
 * falls back to {@link System#currentTimeMillis()}, resulting in a reduced
 * precision of the returned time.
 *
 * @author jwienke
 */
public final class ExactTime {

    private static final Logger LOG =
            Logger.getLogger(ExactTime.class.getName());

    private static Implementation implementation;

    /**
     * Interface for internal implementation variants which may provide the time
     * at a higher resolution.
     *
     * @author jwienke
     */
    interface Implementation {

        long currentTimeMicros();

        boolean works();

    }

    /**
     * Fallback implementation based on {@link System#currentTimeMillis()}.
     *
     * @author jwienke
     */
    private static class FallbackImplementation implements Implementation {

        private static final int MSEC_TO_MUSEC = 1000;

        @Override
        public long currentTimeMicros() {
            return System.currentTimeMillis() * MSEC_TO_MUSEC;
        }

        @Override
        public boolean works() {
            return true;
        }

    }

    /**
     * Implementation for POSIX compliant systems using a foreign function
     * interface.
     *
     * @author jwienke
     */
    private static class JnrPosixImplementation implements Implementation {

        private static final long MILLION = 1000000L;
        private final LibC libc;
        private final Runtime runtime;

        public static final class Timeval extends Struct {

            // CHECKSTYLE.OFF: MemberName|VisibilityModifier - required
            // interface
            public final time_t tv_sec = new time_t();
            public final Signed32 tv_usec = new Signed32();
            // CHECKSTYLE.ON: MemberName|VisibilityModifier

            Timeval(final Runtime runtime) {
                super(runtime);
            }

        }

        @SuppressWarnings("PMD.ShortClassName")
        public interface LibC {

            int gettimeofday(@Out @Transient Timeval timeval, Pointer unused);

        }

        JnrPosixImplementation() {
            this.libc = LibraryLoader.create(LibC.class).load("c");
            this.runtime = Runtime.getRuntime(this.libc);
        }

        @Override
        public long currentTimeMicros() {
            final Timeval timeval = new Timeval(this.runtime);
            this.libc.gettimeofday(timeval, null);
            return timeval.tv_sec.get() * MILLION + timeval.tv_usec.get();
        }

        @Override
        public boolean works() {
            final long value = currentTimeMicros();
            final Timeval timeval = new Timeval(this.runtime);
            this.libc.gettimeofday(timeval, null);
            return value > 0 && timeval.tv_usec.get() < MILLION;
        }

    }

    static {
        try {
            final Implementation candidate = new JnrPosixImplementation();
            if (candidate.works()) {
                implementation = candidate;
            } else {
                LOG.log(Level.FINE,
                        "Candidate {0} indicates that it doesn't work correctly. "
                                + "Discarding.",
                        candidate);
            }
        } catch (final Exception e) { // NOPMD - be safe to not fail anywhere
            LOG.log(Level.FINE,
                    "Unable to use JnrPosixImplementation due to an Exception",
                    e);
        } catch (final Error e) { // NOPMD - be safe to not fail anywhere
            LOG.log(Level.FINE,
                    "Unable to use JnrPosixImplementation due to an error", e);
        }
        if (implementation == null) {
            LOG.fine("No specific implementation worked. "
                    + "Using fallback implementation");
            implementation = new FallbackImplementation();
        }
    }

    private ExactTime() {
        // prevent utility class instantiation
    }

    /**
     * Returns the current system time in microsecond precision using best
     * effort. In case no available native implementation provides this
     * resolution, a reduced one is returned. Check {@link #isFallback()} to get
     * information on this case.
     *
     * @return Unix timestamp for the system time in microseconds
     */
    public static long currentTimeMicros() {
        return implementation.currentTimeMicros();
    }

    /**
     * In case no implementation is known that provides accurate time
     * information for the underlying platform, this method returns
     * <code>true</code>.
     *
     * @return <code>true</code> if no accurate time can be provided.
     */
    public static boolean isFallback() {
        return implementation.getClass() == FallbackImplementation.class;
    }

}

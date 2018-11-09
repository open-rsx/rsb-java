/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2018 CoR-Lab, Bielefeld University
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
package rsb.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import org.junit.Test;

import rsb.Factory;
import rsb.Handler;
import rsb.Listener;
import rsb.ListenerCreateArgs;
import rsb.ReaderCreateArgs;
import rsb.RsbTestCase;
import rsb.RSBException;
import rsb.Scope;
import rsb.config.ParticipantConfig;

public class ReaderTest extends RsbTestCase {

    @Test
    public void listenerSetUpCorrectly(@Mocked final Factory factory,
            @Mocked final Listener listener) throws Exception {
        final ParticipantConfig config = new ParticipantConfig();
        final ReaderCreateArgs args = new ReaderCreateArgs().setScope(
                new Scope("/reader/test")).setConfig(config);

        final Reader reader = new Reader(args, factory);

        new Verifications() {
            {
                ListenerCreateArgs listenerArgs;
                // CHECKSTYLE.OFF: InnerAssignment - required for mockit
                factory.createListener(listenerArgs = withCapture());
                // CHECKSTYLE.ON: InnerAssignment

                assertEquals(args.getScope(), listenerArgs.getScope());
                assertEquals(args.getConfig(), listenerArgs.getConfig());
                assertEquals(reader, listenerArgs.getParent());
            }
        };
    }

    @Test
    public void readReturnsNull(@Mocked final Factory factory)
            throws Exception {
        final ParticipantConfig config = new ParticipantConfig();
        final ReaderCreateArgs args = new ReaderCreateArgs().setScope(
                new Scope("/reader/test/nothing/here")).setConfig(config);

        final Reader reader = new Reader(args, factory);
        reader.activate();

        assertNull(reader.read());

        reader.deactivate();
    }

    @Test(expected = IllegalStateException.class)
    public void readIllegalBeforeActivate(@Mocked final Factory factory)
            throws Exception {
        final ParticipantConfig config = new ParticipantConfig();
        final ReaderCreateArgs args = new ReaderCreateArgs().setScope(
                new Scope("/strange/thing")).setConfig(config);

        final Reader reader = new Reader(args, factory);
        reader.read();
    }

    @Test(expected = RSBException.class)
    public void interruptedExceptionWhileAddingHandler(
            @Mocked final Factory factory,
            @Mocked final Listener listener) throws Exception {

        final ParticipantConfig config = new ParticipantConfig();
        final ReaderCreateArgs args = new ReaderCreateArgs().setScope(
                new Scope("/test/interrupt")).setConfig(config);

        new Expectations() {
            {
                factory.createListener((ListenerCreateArgs) withNotNull());
                result = listener;

                listener.activate();

                listener.addHandler((Handler) withNotNull(), true);
                result = new InterruptedException();
            }
        };

        try {
            final Reader reader = new Reader(args, factory);
            reader.activate();
        } finally {
            // should have been re-interrupted
            assertTrue(Thread.currentThread().interrupted());
            // the previous call also clears the flag such that the following
            // tests can continue without problems.
        }
    }

}

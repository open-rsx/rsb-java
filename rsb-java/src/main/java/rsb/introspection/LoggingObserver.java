/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb.introspection;

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantObserver;

/**
 * Observer class to log participant creation and destruction.
 *
 * @author swrede
 */
public class LoggingObserver implements ParticipantObserver {

    private static final Logger LOG = Logger.getLogger(LoggingObserver.class
            .getName());

    @Override
    public void created(final Participant participant,
            final ParticipantCreateArgs<?> args) {
        LOG.log(Level.INFO,
                "New participant created: {0} at {1} with parent {2}",
                new Object[] {
                    participant.getId(),
                    participant.getScope(),
                    args.getParent(),
                });
    }

    @Override
    public void destroyed(final Participant participant) {
        LOG.log(Level.INFO, "Participant removed: {0} at {1}",
                new Object[] {
                    participant.getId(),
                    participant.getScope(),
                });
    }

}

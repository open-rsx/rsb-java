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
package rsb;

/**
 * Interface for observing {@link Participant} creation and destruction.
 *
 * @author swrede
 * @author jwienke
 * @author jmoringe
 */
public interface ParticipantObserver {

    /**
     * Will be called in case a new {@link Participant} has just been created.
     *
     * @param participant
     *            the newly created participant
     * @param args
     *            the arguments used for creating this participant
     */
    void created(Participant participant, ParticipantCreateArgs<?> args);

    /**
     * Will be called in case a {@link Participant} is about to be destroyed.
     *
     * @param participant
     *            the participant that will be destroyed
     */
    void destroyed(Participant participant);

}

/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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

import java.io.Closeable;

/**
 * Interface for objects which require an explicit activation and deactivation.
 * Users of such objects should ensure that {@link #deactivate()} is called when
 * the object is not needed anymore. Otherwise, destruction behavior is
 * undefined.
 *
 * If not stated otherwise, activation and deactivation are not thread-safe.
 *
 * If not stated otherwise, objects may only survive one activation -
 * deactivation cycle and may not be reactivatable in an ordered way.
 *
 * @author swrede
 * @author jwienke
 */
public interface Activatable extends Closeable {
    // We implement Closeable instead of AutoCloseable for Java 7
    // compatibility.

    /**
     * Activates all resources that belong to a specific object.
     *
     * @throws RSBException
     *             generic error related to RSB
     * @throws IllegalStateException
     *             Might be thrown by implementations to indicate that the
     *             respective instance is already active
     */
    void activate() throws RSBException;

    /**
     * Deactivate all resources that are owned by a specific object in order to
     * correctly tear down. The method should only return once the object is
     * completely deactivated including possible background threads.
     *
     * @throws RSBException
     *             generic error related to RSB
     * @throws IllegalStateException
     *             Might be thrown by implementations to indicate that the
     *             respective instance is not active
     * @throws InterruptedException
     *             interrupted while waiting for proper deactivation. Object
     *             might be in an undefined state now
     */
    void deactivate() throws RSBException, InterruptedException;

    /**
     * Tells whether this class is currently active or not.
     *
     * @return <code>true</code> if active
     */
    boolean isActive();

    /**
     * A utility base class which can be used to implement a state pattern which
     * fulfills the {@link Activatable} interface. Classes implementing
     * {@link Activatable} can create own state classes derived from this one to
     * inherit the default behavior for {@link #activate()} and
     * {@link #deactivate()}.
     *
     * The base implementations of {@link #activate()} and {@link #deactivate()}
     * always throw an {@link IllegalStateException}.
     *
     * @author jwienke
     */
    abstract class State {

        public void activate() throws RSBException {
            throw new IllegalStateException(
                    "activate() cannot be called in state "
                            + this.getClass().getSimpleName());
        }

        public void deactivate() throws RSBException, InterruptedException {
            throw new IllegalStateException(
                    "deactivate() cannot be called in state "
                            + this.getClass().getSimpleName());
        }

        public abstract boolean isActive();

    }

}

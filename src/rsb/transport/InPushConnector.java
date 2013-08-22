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
package rsb.transport;

/**
 * Interface for receiving connectors which asynchronously notify handlers about
 * newly received events.
 *
 * @author jwienke
 */
public interface InPushConnector extends InConnector {

    /**
     * Adds a handler which will be notified about newly received events.
     *
     * This method must not be called after calling {@link #activate()}.
     *
     * @param handler
     *            the handler to notify
     */
    void addHandler(EventHandler handler);

    /**
     * Removes a registered handler so that it won't be notified anymore.
     *
     * This method must not be called after calling {@link #activate()}.
     *
     * @param handler
     *            the handler to remove
     * @return <code>true</code> if the given handler was removed, else
     *         <code>false</code>, i.e. in case that handler was never
     *         registered.
     */
    boolean removeHandler(EventHandler handler);

}

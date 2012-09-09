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
package rsb.transport.spread;

import java.util.logging.Logger;

import spread.SpreadMessage;

/**
 * SpreadMessageConverter encapsulates the handling of regular spread messages
 * and considers Membership messages.
 *
 * TODO (de-)multiplex large messages here
 *
 * @author swrede
 *
 */
class SpreadMessageConverter {

	public final static Logger LOG = Logger.getLogger(SpreadMessageConverter.class.getName());

	/**
	 * Returns a {@link DataMessage} if presented by sm, else returns
	 * <code>null</code>.
	 *
	 * @param sm
	 *            message to analyze
	 * @return DataMessage or <code>null</code> if not contained in sm
	 */
	public DataMessage process(final SpreadMessage sm) {
		DataMessage data = null;
		if (sm.isMembership()) {
			// TODO think about meaningful handling of membership messages
			// and print further info
			LOG.fine("Received membership message for group: "
					+ sm.getMembershipInfo().getGroup());

		} else {
			try {
				data = DataMessage.convertSpreadMessage(sm);
			} catch (SerializeException e) {
				LOG.warning("Error de-serializing SpreadMessage: " + e.getMessage());
			}
		}
		// TODO in this design, following objects must be capable of handling
		// null objects!
		// better approach would be to encapsulate logical changes to the
		// unified event bus
		// in internal events
		return data;
	}

}

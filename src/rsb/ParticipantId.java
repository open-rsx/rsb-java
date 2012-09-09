/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

import java.util.UUID;

import rsb.util.UUIDTools;

/**
 * This class serves as a Uniform Resource Name to identify participants
 * in an RSB system. At present, the URN is based on an UUID that is
 * unique for each participant instance.
 *
 * @author swrede
 * @author jwienke
 */
public class ParticipantId {

	private UUID id;

	/**
	 * Creates a new random id.
	 */
	public ParticipantId() {
		id = UUID.randomUUID();
	}

	/**
	 * Creates an ID from a byte representation.
	 *
	 * @param bytes
	 *            byte representation of the id.
	 */
	public ParticipantId(byte[] bytes) {
		this.id = UUIDTools.fromByteArray(bytes);
	}

	/**
	 * Parses an id from its string form generated with {@link #toString()}.
	 *
	 * @param sid
	 *            string representation
	 * @throws IllegalArgumentException
	 *             invalid string format
	 */
	public ParticipantId(String sid) {
		id = UUID.fromString(sid);
	}

	@Override
	public String toString() {
		return id.toString();
	}

	/**
	 * Returns the bytes representing the id.
	 *
	 * @return byte representing the id (length 16)
	 */
	public byte[] toByteArray() {
		return UUIDTools.toByteArray(id);
	}


	@Override
	public int hashCode() {
		return id.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ParticipantId other = (ParticipantId) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	protected UUID getUUID() {
			return id;
	}



}

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

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import rsb.util.ByteHelpers;
import spread.SpreadGroup;
import spread.SpreadMessage;

/**
 * Encapsulates a message containing data to be sent via spread.
 *
 * @author swrede
 */
public class DataMessage {

    // TODO Support larger message frames, currently the assumption is that
    // messages are not longer than MAX_MESSAGE_LENGTH
    // TODO add GroupName length checks

    // CHECKSTYLE.OFF: DeclarationOrderCheck - no chance to cure this here as
    // MAX_MESSAGE_LENGTH requires the private constant

    /* from spread.SpreadConnection */
    @SuppressWarnings("PMD.LongVariable")
    private static final int SPREAD_OVERHEAD_OFFSET = 20000;

    // CHECKSTYLE.OFF: MagicNumber - we are calculating a constant. No need to
    // warn here
    /**
     * Maximum length in bytes of messages that can be sent via spread.
     */
    @SuppressWarnings("PMD.LongVariable")
    public static final int MAX_MESSAGE_LENGTH =
            140000 - SPREAD_OVERHEAD_OFFSET;
    // CHECKSTYLE.ON: MagicNumber

    /* decorated spread message */
    private SpreadMessage msg = new SpreadMessage();

    // CHECKSTYLE.ON: DeclarationOrderCheck

    static DataMessage convertSpreadMessage(final SpreadMessage msg)
            throws SerializeException {
        if (msg.isMembership()) {
            throw new SerializeException(
                    "MembershipMessage received but DataMessage expected!");
        } else {
            final DataMessage dataMessage = new DataMessage();
            dataMessage.msg = msg;
            return dataMessage;
        }
    }

    private void checkSize(final ByteBuffer buffer) throws SerializeException {
        if (buffer.limit() == 0
                || buffer.limit() > DataMessage.MAX_MESSAGE_LENGTH) {
            throw new SerializeException(
                    "Invalid Length of SpreadMessage (either null or larger than "
                            + DataMessage.MAX_MESSAGE_LENGTH + " bytes)");
        }
    }

    /**
     * Returns the spread groups this message will be sent to.
     *
     * @return array of group names
     */
    public Set<String> getGroups() {
        final Set<String> groups = new HashSet<String>();
        for (final SpreadGroup group : this.msg.getGroups()) {
            groups.add(group.toString());
        }
        return groups;
    }

    /**
     * Adds a group this message should be sent to.
     *
     * @param group
     *            the new group
     */
    public void addGroup(final String group) {
        this.msg.addGroup(group);
    }

    /**
     * Tells whether this message shall be sent to the requested group.
     *
     * @param name
     *            name of the group to test for
     * @return <code>true</code> if this message will be sent to that group
     */
    public boolean isForGroup(final String name) {
        for (int i = 0; i < this.msg.getGroups().length; i++) {
            if (this.msg.getGroups()[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the data to send with this message.
     *
     * @param buffer
     *            data buffer, not <code>null</code>
     * @throws SerializeException
     *             buffer cannot be serialized
     */
    public void setData(final ByteBuffer buffer) throws SerializeException {
        this.checkSize(buffer);
        this.msg.setData(ByteHelpers.byteBufferToArray(buffer));
    }

    /**
     * Sets the data to send with this message.
     *
     * @param buffer
     *            data buffer, not <code>null</code>
     * @throws SerializeException
     *             buffer cannot be serialized
     */
    @SuppressWarnings("PMD.UseVarargs")
    public void setData(final byte[] buffer) throws SerializeException {
        this.msg.setData(buffer);
    }

    /**
     * Returns the data this message will contain.
     *
     * @return byte buffer instance, not <code>null</code>
     */
    public ByteBuffer getData() {
        return ByteBuffer.wrap(this.msg.getData());
    }

    /**
     * Sets the self discard flag for this message so that it will not be
     * delivered to the sender by the spread daemon.
     */
    public void enableSelfDiscard() {
        this.msg.setSelfDiscard(true);
    }

    /**
     * Returns the constructed {@link SpreadMessage} instance.
     *
     * @return constructed instance
     * @throws SerializeException
     *             message cannot be created because information is missing
     */
    public SpreadMessage getSpreadMessage() throws SerializeException {
        // TODO bad exception type for the expressed meaning
        // TODO there should also be a way to get a message that is not fully
        // constructed yet
        if (this.msg.getGroups().length == 0) {
            throw new SerializeException(
                    "Receiver information is missing in DataMessage");
        }
        return this.msg;
    }

}

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

import spread.SpreadMessage;

/**
 * @author swrede
 */
public class DataMessage {

    // TODO Support larger message frames, currently the assumption is that
    // messages are not longer than MAX_MESSAGE_LENGTH
    // TODO add GroupName length checks

    /* from spread.SpreadConnection */
    @SuppressWarnings("PMD.LongVariable")
    private static final int SPREAD_OVERHEAD_OFFSET = 20000;
    /**
     * Maximum length in bytes of messages that can be sent via spread.
     */
    @SuppressWarnings("PMD.LongVariable")
    public static final int MAX_MESSAGE_LENGTH = 140000 - SPREAD_OVERHEAD_OFFSET;

    /* decorated spread message */
    SpreadMessage msg = new SpreadMessage();

    public DataMessage() {
        super();
    }

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

    public String[] getGroups() {
        final String[] groups = new String[this.msg.getGroups().length];
        for (int i = 0; i < this.msg.getGroups().length; i++) {
            groups[i] = this.msg.getGroups()[i].toString();
        }
        return groups;
    }

    @SuppressWarnings("PMD.UseVarargs")
    public void setGroups(final String[] grp) {
        for (final String element : grp) {
            this.msg.addGroup(element);
        }
    }

    public void addGroup(final String grp) {
        this.msg.addGroup(grp);
    }

    public boolean inGroup(final String name) {
        for (int i = 0; i < this.msg.getGroups().length; i++) {
            if (this.msg.getGroups()[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void setData(final ByteBuffer buffer) throws SerializeException {
        this.checkSize(buffer);
        this.msg.setData(buffer.array());
    }

    @SuppressWarnings("PMD.UseVarargs")
    public void setData(final byte[] buffer) throws SerializeException {
        this.msg.setData(buffer);
    }

    public ByteBuffer getData() {
        return ByteBuffer.wrap(this.msg.getData());
    }

    public void enableSelfDiscard() {
        this.msg.setSelfDiscard(true);
    }

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

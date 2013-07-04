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

/**
 * Specification of desired quality of service settings for sending and
 * receiving events. Specification given here are required "at least". This
 * means concrete port instances can implement "better" QoS specs without any
 * notification to the clients. Better is decided by the integer value of the
 * specification enums. Higher values mean better services.
 * 
 * @author jwienke
 */
public class QualityOfServiceSpec {

    /**
     * Possible ordering requirements for events.
     * 
     * @author jwienke
     */
    public enum Ordering {
        /**
         * Events are received in no specific order.
         */
        UNORDERED,
        /**
         * Events are received in the same order as sent by a sender. This is
         * only valid for the connection of one sender to one receiver. No
         * assumptions are placed on other receivers or events of other senders.
         */
        ORDERED;
    }

    /**
     * Possible requirements on the reliability of events.
     * 
     * @author jwienke
     */
    public enum Reliability {
        /**
         * Events may be dropped.
         */
        UNRELIABLE,
        /**
         * Events will always be received, otherwise an error is generated.
         */
        RELIABLE;
    }

    private final Ordering ordering;
    private final Reliability reliability;

    /**
     * Default specs with {@link Reliability#RELIABLE} and
     * {@link Ordering#UNORDERED}.
     */
    public QualityOfServiceSpec() {
        this.ordering = Ordering.UNORDERED;
        this.reliability = Reliability.RELIABLE;
    }

    /**
     * Constructor with specified settings.
     * 
     * @param ordering
     *            desired ordering
     * @param reliability
     *            desired reliability
     */
    public QualityOfServiceSpec(final Ordering ordering,
            final Reliability reliability) {
        this.ordering = ordering;
        this.reliability = reliability;
    }

    /**
     * Returns the desired ordering setting.
     * 
     * @return requested ordering setting
     */
    public Ordering getOrdering() {
        return this.ordering;
    }

    /**
     * Returns the desired reliability setting.
     * 
     * @return requested reliability setting
     */
    public Reliability getReliability() {
        return this.reliability;
    }

    @Override
    public int hashCode() {
        return 7 * this.reliability.hashCode() + 13 * this.ordering.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof QualityOfServiceSpec)) {
            return false;
        }
        final QualityOfServiceSpec other = (QualityOfServiceSpec) obj;
        return this.reliability.equals(other.reliability)
                && this.ordering.equals(other.ordering);
    }

    @Override
    public String toString() {
        return "QualityOfServiceSpec[ordering = " + this.ordering
                + ", reliability = " + this.reliability + "]";
    }

}

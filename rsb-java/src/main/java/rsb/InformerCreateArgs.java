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
 * {@link ParticipantCreateArgs} for {@link Informer} instances. The default
 * data type is assumed to be {@link Object}.
 *
 * @author jwienke
 * @author jmoringe
 */
// We want method chaining here, silence linguist
@SuppressWarnings("PMD.LinguisticNaming")
public class InformerCreateArgs extends
        ParticipantCreateArgs<InformerCreateArgs> {

    /**
     * The data type sent by an informer. Defaults to {@link Object} as a
     * generic type.
     */
    private Class<?> type = Object.class;

    /**
     * Returns the type of data to be sent by the new informer.
     *
     * @return class of data to send
     */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * Sets the type of data to be sent by the new informer.
     *
     * @param type
     *            class of data to send, not <code>null</code>
     * @return this instance for method chaining
     */
    public InformerCreateArgs setType(final Class<?> type) {
        this.type = type;
        return this;
    }

}

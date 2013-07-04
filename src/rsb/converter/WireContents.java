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
package rsb.converter;

/**
 * A wrapper around the contents to be placed on the wire.
 * 
 * @author jwienke
 * @param <WireType>
 *            serialization type of the wire contents
 */
public class WireContents<WireType> {

    private final WireType serialization;
    private final String wireSchema;

    /**
     * Constructs a new wrapper around serialized data.
     * 
     * @param serialization
     *            the serialized data
     * @param wireSchema
     *            the wire schema identifier of the serialized data
     */
    public WireContents(final WireType serialization, final String wireSchema) {
        this.serialization = serialization;
        this.wireSchema = wireSchema;
    }

    /**
     * Returns the contents for the wire in their serialized form.
     * 
     * @return serialized contents
     */
    public WireType getSerialization() {
        return this.serialization;
    }

    /**
     * Returns the identifier of the wire schema that was used to serialize the
     * contents.
     * 
     * @return wire schema identifier
     */
    public String getWireSchema() {
        return this.wireSchema;
    }

}

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
 * A wrapper around deserialized data that contains the unspecific
 * {@link Object} instance with a class object describing its type.
 * 
 * TODO is the unused generics type really required?
 * 
 * @author jwienke
 * @author swrede
 * @param <WireType>
 *            the kind of wire this user data originate from
 */
public class UserData<WireType> {

    private final Object data;
    private final Class<?> typeInfo;

    public UserData(final Object data, final Class<?> typeInfo) {
        this.data = data;
        this.typeInfo = typeInfo;
    }

    /**
     * Returns the deserialized data.
     * 
     * @return deserialized data
     */
    public Object getData() {
        return this.data;
    }

    /**
     * Class object describing the type of the deserialized data.
     * 
     * @return class type info
     */
    public Class<?> getTypeInfo() {
        return this.typeInfo;
    }

}

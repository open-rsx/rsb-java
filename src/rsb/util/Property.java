/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014, Johannes Wienke
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
package rsb.util;

/**
 * Representation of a single value inside {@link Properties} with support
 * for interpretation of value strings in different formats.
 *
 * @author jwienke
 */
public class Property {

    private final String value;

    /**
     * Creates a new property with the value given as a string.
     *
     * @param value
     *            value string
     */
    public Property(final String value) {
        this.value = value.trim();
    }

    /**
     * Returns the string representation of the property value.
     *
     * @return string value
     */
    public String asString() {
        return this.value;
    }

    /**
     * Interprets the value string as a boolean.
     *
     * @return <code>true</code> if the property value contains a string
     *         indicating <code>true</code>, else <code>false</code>
     */
    public boolean asBoolean() {
        return this.value.equalsIgnoreCase("true")
                || this.value.equalsIgnoreCase("yes")
                || this.value.equalsIgnoreCase("on")
                || this.value.equals("1");
    }

    /**
     * Tries to interpret the value as an integer.
     *
     * @return integer representation of the property value
     * @throws NumberFormatException
     *             property value cannot be interpreted as a number
     */
    public int asInteger() {
        return Integer.parseInt(this.value);
    }

    /**
     * Tries to interpret the value as a long integer.
     *
     * @return long integer representation of the property value
     * @throws NumberFormatException
     *             property value cannot be interpreted as a number
     */
    public long asLong() {
        return Long.parseLong(this.value);
    }

    /**
     * Tries to interpret the value as a float.
     *
     * @return float representation of the property value
     * @throws NumberFormatException
     *             property value cannot be interpreted as a number
     */
    public float asFloat() {
        return Float.parseFloat(this.value);
    }

    /**
     * Tries to interpret the value as a double.
     *
     * @return double representation of the property value
     * @throws NumberFormatException
     *             property value cannot be interpreted as a number
     */
    public double asDouble() {
        return Double.parseDouble(this.value);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + this.value + "]";
    }

    @Override
    public int hashCode() {
        // CHECKSTYLE.OFF: AvoidInlineConditionals - this method is more
        // readable with the inline conditionals
        final int prime = 31;
        int result = 1;
        result =
                prime
                        * result
                        + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
        // CHECKSTYLE.ON: AvoidInlineConditionals
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Property)) {
            return false;
        }
        final Property other = (Property) obj;
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

}

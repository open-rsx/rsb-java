/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011, 2012 Jan Moringen
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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A class which reads configuration options from different sources and presents
 * them using key value pairs.
 *
 * Property keys are hierarchically organized with a . as a separator for the
 * path.
 *
 * Multiple threads can read properties in parallel. However, writing needs to
 * be synchronized as well as the provided loading methods.
 *
 * @author swrede
 * @author jwienke
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Properties implements Iterable<Entry<String, Property>> {

    private final Map<String, Property> propertiesByName =
            new HashMap<String, Property>();

    /**
     * Creates a new instance without loading anything. The instance will
     * initially be empty.
     */
    public Properties() {
        // just for the documentation string
    }

    /**
     * Dumps the properties to the presented stream.
     *
     * @param stream
     *            the stream to print on
     */
    public void dumpProperties(final PrintStream stream) {
        for (final String key : this.propertiesByName.keySet()) {
            stream.println(key + " => '" + this.propertiesByName.get(key) + "'");
        }
    }

    /**
     * Resets this instances to empty and afterwards re-initiates value parsing.
     */
    public void reset() {
        this.propertiesByName.clear();
    }

    /**
     * Returns the value of the specified property in case it exists.
     *
     * @param key
     *            the property name
     * @return value of that property
     * @throws InvalidPropertyException
     *             property with the given name is unknown
     */
    public Property getProperty(final String key) {
        if (this.propertiesByName.containsKey(key)) {
            return this.propertiesByName.get(key);
        } else {
            throw new InvalidPropertyException(
                    "Trying to get unknown property '" + key
                            + "' in Properties.getProperty()");
        }
    }

    /**
     * Returns the value of a specified property or the specified default in
     * case the property does not exist.
     *
     * @param key
     *            the property name
     * @param defaultValue
     *            default value to return in case the property does not exist.
     *            In case this is not a string, {@link Object#toString()} will
     *            be called to convert to a string representation.
     * @return value of that property or default
     * @param <TargetType>
     *            the type of the default value.
     */
    public <TargetType> Property getProperty(final String key,
            final TargetType defaultValue) {
        try {
            return getProperty(key);
        } catch (final InvalidPropertyException e) {
            return new Property(defaultValue.toString());
        }
    }

    /**
     * Sets a property. key and value are always trimmed before insertion.
     *
     * @param key
     *            key of the property
     * @param value
     *            value of the property
     */
    public void setProperty(final String key, final String value) {
        this.propertiesByName.put(key.trim(), new Property(value));
    }

    /**
     * Returns a set of know property names.
     *
     * @return set of known property keys
     */
    public Set<String> getAvailableKeys() {
        return this.propertiesByName.keySet();
    }

    /**
     * Indicates whether the property with the given key is available or not.
     *
     * @param key
     *            key of the property to test
     * @return <code>true</code> if the property exists, else <code>false</code>
     */
    public boolean hasProperty(final String key) {
        return this.propertiesByName.containsKey(key);
    }

    /**
     * Returns a new properties instance with all properties with keys starting
     * with the specified prefix. Property key separation with dots is expected.
     * This means that in case the desired prefix is "a.test" only exactly this
     * key and keys starting with string form "a.test." are returned.
     *
     * @param desiredPrefix
     *            key prefix to include in the new set.
     * @return Properties instance with all properties matching the desired
     *         prefix. This also includes the property with a key directly
     *         reflecting the specified prefix
     */
    public Properties filter(final String desiredPrefix) {
        final Properties filtered = new Properties();
        for (final Entry<String, Property> entry : this.propertiesByName
                .entrySet()) {
            if (entry.getKey().equals(desiredPrefix)
                    || entry.getKey().startsWith(desiredPrefix + ".")) {
                filtered.setProperty(entry.getKey(), entry.getValue()
                        .asString());
            }
        }
        return filtered;
    }

    /**
     * Removes all properties with the given keys.
     *
     * @param keys
     *            set of keys to remove
     */
    public void remove(final Set<String> keys) {
        for (final String key : keys) {
            this.propertiesByName.remove(key);
        }
    }

    /**
     * Removes the property with the given key.
     *
     * @param key
     *            key to remove
     */
    public void remove(final String key) {
        this.propertiesByName.remove(key);
    }

    /**
     * Merges the passed properties into this instance by adding and/or
     * overwriting all properties found in that instance.
     *
     * @param properties
     *            properties to merge into this instance
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void merge(final Properties properties) {
        for (final Entry<String, Property> entry : properties.propertiesByName
                .entrySet()) {
            this.propertiesByName.put(entry.getKey(), new Property(entry
                    .getValue().asString()));
        }
    }

    @Override
    public Iterator<Entry<String, Property>> iterator() {
        return this.propertiesByName.entrySet().iterator();
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append("[properties=");
        builder.append(this.propertiesByName.toString());
        builder.append(']');

        return builder.toString();

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
                        + ((this.propertiesByName == null) ? 0
                                : this.propertiesByName.hashCode());
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
        if (!(obj instanceof Properties)) {
            return false;
        }
        final Properties other = (Properties) obj;
        if (this.propertiesByName == null) {
            if (other.propertiesByName != null) {
                return false;
            }
        } else if (!this.propertiesByName.equals(other.propertiesByName)) {
            return false;
        }
        return true;
    }

}

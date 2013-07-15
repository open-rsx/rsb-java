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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
// expected here with the different type accessors
@SuppressWarnings("PMD.TooManyMethods")
public class Properties {

    private final static Logger LOG = Logger.getLogger(Properties.class
            .getName());

    private final Map<String, Property> propertiesByName = new HashMap<String, Property>();

    /**
     * Representation of a single value inside {@link Properties} with support
     * for interpretation of value strings in different formats.
     *
     * @author jwienke
     */
    public static class Property {

        private final String value;

        Property(final String value) {
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
     * Creates a new instance without loading anything. The instance will
     * initially be empty.
     */
    public Properties() {
        // just for the documentation string
    }

    /**
     * Resets this instances to empty and afterwards re-initiates value parsing.
     */
    public void reset() {
        this.propertiesByName.clear();
    }

    /**
     * Loads properties from the default sources. These are several config files
     * and environment variables.
     *
     * The instance is not cleared from existing properties before. If you need
     * this, use {@link #reset()}.
     *
     * @return this instance, now loaded
     */
    public Properties load() {

        // parse rsb.conf files in standard locations
        this.loadFiles();

        // parse environment
        this.loadEnv();

        return this;

    }

    private void loadFiles() {
        // Read configuration properties in the following order:
        // 1. from /etc/rsb.conf, if the file exists
        // 2. from ${HOME}/.config/rsb.conf, if the file exists
        // 3. from $(pwd)/rsb.conf, if the file exists
        loadFileIfAvailable(new File("/etc/rsb.conf"));

        final String homeDir = System.getProperty("user.home");
        loadFileIfAvailable(new File(homeDir + "/.config/rsb.conf"));

        final String workDir = System.getProperty("user.dir");
        loadFileIfAvailable(new File(workDir + "/rsb.conf"));

    }

    /**
     * Loads a config file if the file exists. Reading errors are ignored.
     *
     * The instance is not cleared before loading the file.
     *
     * @param file
     *            the file to read, might not exist
     * @return this instance with loaded properties
     */
    public Properties loadFileIfAvailable(final File file) {
        try {
            if (file.exists()) {
                this.loadFile(file);
            }
        } catch (final IOException ex) {
            LOG.log(Level.SEVERE, "Caught IOException trying to read " + "'"
                    + file.getPath() + "'", ex);
        }
        return this;
    }

    /**
     * Reads options from a config file. Assumes that the file exists.
     *
     * The instance is not cleared before loading from the file.
     *
     * @param file
     *            the file to read
     * @return this instance
     * @throws IllegalArgumentException
     *             if the file does not exist
     * @throws IOException
     *             error reading the file
     */
    // we don't care about speed here
    @SuppressWarnings("PMD.SimplifyStartsWith")
    public Properties loadFile(final File file) throws IOException {

        if (!file.exists()) {
            throw new IllegalArgumentException("The file '" + file
                    + "' does not exist.");
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        try {

            String section = "";
            while (reader.ready()) {
                String line = reader.readLine();
                int index = line.indexOf('#');
                if (index != -1) {
                    line = line.substring(0, index);
                }
                line = line.trim();
                if ("".equals(line)) {
                    continue;
                }
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.substring(1, line.length() - 1);
                    continue;
                }
                index = line.indexOf('=');
                if (index == -1) {
                    LOG.warning("Illegal line in configuration file: `" + line
                            + "'");
                    continue;
                }
                final String key = section + "."
                        + line.substring(0, index).trim();
                final String value = line.substring(index + 1).trim();
                this.setProperty(key, value);
            }

        } finally {
            reader.close();
        }

        return this;

    }

    /**
     * Loads properties from the environment variable.
     *
     * The instance is not cleared before loading.
     *
     * @return this instance
     */
    public Properties loadEnv() {
        this.parseMap(System.getenv());
        return this;
    }

    private void parseMap(final Map<String, String> env) {
        for (final Entry<String, String> entry : env.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith("RSB_")) {
                this.setProperty(key.substring(4).replace("_", ".")
                        .toLowerCase(), entry.getValue());
            }
        }
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

}

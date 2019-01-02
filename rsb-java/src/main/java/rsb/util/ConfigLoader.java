/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class which loads the RSB configuration from different sources and stores
 * it in a {@link Properties} object.
 *
 * @author jwienke
 * @author jmoringe
 */
public class ConfigLoader {

    /**
     * Name of the environment variable that can be used to enable
     * configuration debugging.
     */
    private static final String CONFIG_DEBUG_VARIABLE = "RSB_CONFIG_DEBUG";

    // CHECKSTYLE.OFF: RegexpSinglelineJava - desired here
    private static final String DEBUG_INDENT_1 = "  ";
    private static final String DEBUG_INDENT_2 = "     ";
    // CHECKSTYLE.ON: RegexpSinglelineJava

    // File-related things.

    /**
     * Name of the environment variable that can be used to specify an
     * alternate list of configuration files.
     */
    private static final String CONFIG_FILES_VARIABLE = "RSB_CONFIG_FILES";

    private static final String CONFIG_FILE_KEY_SYSTEM = "%system";
    private static final String CONFIG_FILE_KEY_PREFIX = "%prefix";
    private static final String CONFIG_FILE_KEY_USER = "%user";
    private static final String CONFIG_FILE_KEY_PWD = "%pwd";

    private static final String ETC_DIRECTORY = "etc";
    private static final String CONFIG_FILE_NAME = "rsb.conf";

    /**
     * By default, read configuration properties in the following order:
     * 1. from /etc/rsb.conf, if the file exists
     * 2. from ${HOME}/.config/rsb.conf, if the file exists
     * 3. from $(pwd)/rsb.conf, if the file exists
     */
    private static final List<String> DEFAULT_CONFIG_FILES =
            Arrays.asList(CONFIG_FILE_KEY_PREFIX,
                    CONFIG_FILE_KEY_USER,
                    CONFIG_FILE_KEY_PWD);

    // Environment variable things.
    private static final String ENV_SEPARATOR = "_";

    // Not source-specific things.
    private static final String KEY_SEPARATOR = ".";

    private static final Logger LOG = Logger.getLogger(ConfigLoader.class
            .getName());

    private final List<String> fileSpecs;

    private final String prefix;
    private final String environmentPrefix;

    private final boolean debug;

    /**
     * Creates a new instance with specific parameters.
     *
     * @param prefix
     *            installation prefix to search for the config files in
     *            <code>prefix/etc/rsb.conf</code>.
     * @param environmentPrefix
     *            prefix for environment variables
     */
    public ConfigLoader(final String prefix, final String environmentPrefix) {
        this.fileSpecs = computeConfigFileCascade();
        this.prefix = prefix;
        this.environmentPrefix = environmentPrefix;
        this.debug = System.getenv().containsKey(CONFIG_DEBUG_VARIABLE);
    }

    /**
     * Uses "/" as the config file prefix and "RSB_" as the environment variable
     * prefix.
     */
    public ConfigLoader() {
        this("/", "RSB_");
    }

    private static List<String> computeConfigFileCascade() {
        if (System.getenv().containsKey(CONFIG_FILES_VARIABLE)) {
            final String raw = System.getenv().get(CONFIG_FILES_VARIABLE);
            return Arrays.asList(raw.split(":"));
        } else {
            return DEFAULT_CONFIG_FILES;
        }
    }

    /**
     * Loads properties from the default sources. These are several config files
     * and environment variables.
     *
     * The passed in instance is not cleared from existing properties before. If
     * you need this, use {@link Properties#reset()}.
     *
     * @param results
     *            the instance to fill with new properties
     * @return the result instance, now loaded
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public Properties load(final Properties results) {

        LOG.log(Level.FINE, "Loading properties into {0}", results);
        if (this.debug) {
            System.out.println("Configuring with sources (lowest priority first)");
        }

        // Load rsb.conf files
        this.loadFiles(results);

        // Load environment variables
        this.loadEnv(results);

        return results;

    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void loadFiles(final Properties results) {

        LOG.log(Level.FINE,
                "Loading properties for default config files into {0}", results);

        if (this.debug) {
            System.out.println(DEBUG_INDENT_1 + "1. Configuration files");
        }
        int index = 0;
        for (final String spec : this.fileSpecs) {
            resolveAndLoadFile(index++, spec, results);
        }
    }

    private void resolveAndLoadFile(final int index,
                                    final String spec,
                                    final Properties results) {
        if (spec.equals(CONFIG_FILE_KEY_SYSTEM)) {
            loadFileIfAvailable(index,
                                new File(File.separator + ETC_DIRECTORY
                                         + File.separator + CONFIG_FILE_NAME),
                                "System wide config file",
                                results);
        } else if (spec.equals(CONFIG_FILE_KEY_PREFIX)) {
            loadFileIfAvailable(index,
                                new File(this.prefix
                                         + File.separator + ETC_DIRECTORY
                                         + File.separator + CONFIG_FILE_NAME),
                                "Prefix wide config file",
                                results);
        } else if (spec.equals(CONFIG_FILE_KEY_USER)) {
            final String homeDir = System.getProperty("user.home");
            loadFileIfAvailable(index,
                                new File(homeDir
                                         + File.separator + ".config"
                                         + File.separator + CONFIG_FILE_NAME),
                                "User config file",
                                results);
        } else if (spec.equals(CONFIG_FILE_KEY_PWD)) {
            final String workDir = System.getProperty("user.dir");
            loadFileIfAvailable(index,
                                new File(workDir
                                         + File.separator + CONFIG_FILE_NAME),
                                "Current directory file",
                                results);
        } else {
            loadFileIfAvailable(index,
                                new File(spec),
                                "User specified config file",
                                results);
        }
    }

    /**
     * Loads a config file if the file exists. Reading errors are ignored.
     *
     * The passed in instance is not cleared before loading the file.
     *
     * @param index
     *            the index of the file in the configuration file
     *            cascade
     * @param file
     *            the file to read, might not exist
     * @param description
     *            a description of the file being loaded
     * @param results
     *            the instance to fill with new properties
     * @return the result instance with loaded properties
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public Properties loadFileIfAvailable(final int index,
                                          final File file,
                                          final String description,
                                          final Properties results) {
        LOG.log(Level.FINE,
                "Loading properties from {0} into {1} if available.",
                new Object[] { file, results });
        try {
            final boolean exists = file.exists();
            if (this.debug) {
                final StringBuffer text = new StringBuffer(DEBUG_INDENT_2);
                text.append(index + 1 + ". " + description + " \"" + file + "\"");
                if (exists) {
                    text.append(" exists");
                } else {
                    text.append(" does not exist");
                }
                System.out.println(text);
            }
            if (exists) {
                LOG.log(Level.FINE, "{0} does exist, loading.", file);
                this.loadFile(file, results);
            }
        } catch (final IOException ex) {
            LOG.log(Level.SEVERE,
                    String.format("Caught IOException trying to read '%s'.",
                            file.getPath()), ex);
        }
        return results;
    }

    /**
     * Reads options from a config file. Assumes that the file exists.
     *
     * The passed in instance is not cleared before loading from the file.
     *
     * @param file
     *            the file to read
     * @param results
     *            the instance to fill with new properties
     * @return the instance with properties
     * @throws IllegalArgumentException
     *             if the file does not exist
     * @throws IOException
     *             error reading the file
     */
    // we don't care about speed here
    @SuppressWarnings("PMD.SimplifyStartsWith")
    public Properties loadFile(final File file, final Properties results)
            throws IOException {

        LOG.log(Level.FINE, "Loading properties from file {0} into {1}",
                new Object[] { file, results });

        if (!file.exists()) {
            throw new IllegalArgumentException("The file '" + file
                    + "' does not exist.");
        }

        final BufferedReader reader =
                new BufferedReader(new InputStreamReader(new FileInputStream(
                        file)));
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
                    LOG.log(Level.WARNING,
                            "Illegal line in configuration file: `{0}`", line);
                    continue;
                }
                final String key =
                        section + KEY_SEPARATOR
                                + line.substring(0, index).trim();
                final String value = line.substring(index + 1).trim();
                LOG.log(Level.FINER,
                        "Parsed key {0} to be {1}. Setting in result object.",
                        new Object[] { key, value });
                results.setProperty(key, value);
            }

        } finally {
            reader.close();
        }

        return results;

    }

    /**
     * Loads properties from the environment variable.
     *
     * The passed in instance is not cleared before loading.
     *
     * @param results
     *            the instance to fill with new properties
     * @return the passed in instance
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public Properties loadEnv(final Properties results) {
        LOG.log(Level.FINE, "Loading properties from environment into {0}",
                results);
        if (this.debug) {
            // CHECKSTYLE.OFF: RegexpSinglelineJava - desired here
            System.out.println("  2. Environment variables with prefix RSB_");
            // CHECKSTYLE.ON
        }
        this.parseMap(System.getenv(), results);
        return results;
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void parseMap(final Map<String, String> env,
            final Properties results) {
        for (final Entry<String, String> entry : env.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (key.startsWith(this.environmentPrefix)) {
                final String propsKey =
                        key.substring(this.environmentPrefix.length())
                                .replace(ENV_SEPARATOR, KEY_SEPARATOR)
                                .toLowerCase(Locale.US);
                if (this.debug) {
                    System.out.println(DEBUG_INDENT_2 + key
                                       + " (mapped to " + propsKey + ") -> " + value);
                }
                LOG.log(Level.FINER, "Parsed from map: {0} = {1}",
                        new Object[] { propsKey, value });
                results.setProperty(propsKey, value);
            }
        }
    }

}

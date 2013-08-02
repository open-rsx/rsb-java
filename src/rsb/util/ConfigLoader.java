package rsb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
 */
public class ConfigLoader {

    private static final String ENV_SEPARATOR = "_";

    private static final String KEY_SEPARATOR = ".";

    private static final Logger LOG = Logger.getLogger(ConfigLoader.class
            .getName());

    private final String prefix;
    private final String environmentPrefix;

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
        this.prefix = prefix;
        this.environmentPrefix = environmentPrefix;
    }

    /**
     * Uses "/" as the config file prefix and "RSB_" as the environment variable
     * prefix.
     */
    public ConfigLoader() {
        this("/", "RSB_");
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
    public Properties load(final Properties results) {

        // parse rsb.conf files in standard locations
        this.loadFiles(results);

        // parse environment
        this.loadEnv(results);

        return results;

    }

    private void loadFiles(final Properties results) {
        // Read configuration properties in the following order:
        // 1. from /etc/rsb.conf, if the file exists
        // 2. from ${HOME}/.config/rsb.conf, if the file exists
        // 3. from $(pwd)/rsb.conf, if the file exists
        loadFileIfAvailable(new File(this.prefix + "/etc/rsb.conf"), results);

        final String homeDir = System.getProperty("user.home");
        loadFileIfAvailable(new File(homeDir + "/.config/rsb.conf"), results);

        final String workDir = System.getProperty("user.dir");
        loadFileIfAvailable(new File(workDir + "/rsb.conf"), results);

    }

    /**
     * Loads a config file if the file exists. Reading errors are ignored.
     *
     * The passed in instance is not cleared before loading the file.
     *
     * @param file
     *            the file to read, might not exist
     * @param results
     *            the instance to fill with new properties
     * @return the result instance with loaded properties
     */
    public Properties loadFileIfAvailable(final File file,
            final Properties results) {
        try {
            if (file.exists()) {
                this.loadFile(file, results);
            }
        } catch (final IOException ex) {
            LOG.log(Level.SEVERE,
                    String.format("Caught IOException trying to read  '%s'.",
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
                            "Illegal line in configuration file: `{}`", line);
                    continue;
                }
                final String key =
                        section + KEY_SEPARATOR
                                + line.substring(0, index).trim();
                final String value = line.substring(index + 1).trim();
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
    public Properties loadEnv(final Properties results) {
        this.parseMap(System.getenv(), results);
        return results;
    }

    private void parseMap(final Map<String, String> env,
            final Properties results) {
        for (final Entry<String, String> entry : env.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(this.environmentPrefix)) {
                results.setProperty(
                        key.substring(this.prefix.length())
                                .replace(ENV_SEPARATOR, KEY_SEPARATOR)
                                .toLowerCase(Locale.US), entry.getValue());
            }
        }
    }

}

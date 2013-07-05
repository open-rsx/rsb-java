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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.logging.Logger;

public class Properties {

    private final static Logger log = Logger.getLogger(Properties.class
            .getName());

    private final static String[] boolValues = { "FALSE", "TRUE", "YES", "NO",
            "true", "false", "yes", "no", "1", "0" };

    private static void dumpProperties() {
        for (final String key : singleton.propsMap.keySet()) {
            log.info(key + " => '" + singleton.propsMap.get(key).getValue()
                    + "'");
        }
    }

    private void trimProperties() {
        for (final ValidProperty v : singleton.propsMap.values()) {
            v.value = v.value.trim();
        }
    }

    private class ValidProperty {

        protected String value;
        protected String regex;

        protected ValidProperty(final String v) {
            this.value = v;
            this.regex = "";
        }

        public String getValue() {
            return this.value;
        }

        public boolean setValidValue(final String v) {
            if (this.regex != "") {
                if (!v.matches(this.regex)) {
                    return false;
                }
            }
            this.value = v;
            return true;
        }
    }

    private class IntegerProperty extends ValidProperty {

        public IntegerProperty(final String v) {
            super("");
            this.regex = "[0-9]+";
            if (!this.setValidValue(v)) {
                this.value = "0";
            }
        }
    }

    private class InSetProperty extends ValidProperty {

        public InSetProperty(final String default_, final String[] vals) {
            super("");
            this.regex = "";
            for (int i = 0; i < vals.length; i++) {
                this.regex += vals[i];
                if (i < vals.length - 1) {
                    this.regex += "|";
                }
            }
            if (!this.setValidValue(default_)) {
                this.value = vals[0];
            }
        }
    }

    private class BooleanProperty extends InSetProperty {

        BooleanProperty(final String v) {
            super(v, boolValues);
        }

        @Override
        public String getValue() {
            if (this.value.equalsIgnoreCase("TRUE")
                    || this.value.equalsIgnoreCase("YES")
                    || this.value.equals("1")) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
    }

    protected java.util.HashMap<String, ValidProperty> propsMap = null;

    protected static Properties singleton = null;

    protected Properties() {
        this.initializeMap();
    }

    private Manifest getManifest() {
        final Class<?> clazz = this.getClass();
        final String className = clazz.getSimpleName() + ".class";
        final URL classResource = clazz.getResource(className);
        if (classResource == null) {
            return null;
        }
        final String classPath = classResource.toString();
        if (!classPath.startsWith("jar")) {
            // class not from JAR
            return null;
        }
        final String manifestPath = classPath.substring(0,
                classPath.lastIndexOf("!") + 1)
                + "/META-INF/MANIFEST.MF";
        try {
            return new Manifest(new URL(manifestPath).openStream());
        } catch (final MalformedURLException e) {
            return null;
        } catch (final IOException e) {
            return null;
        }
    }

    protected void initializeMap() {
        this.propsMap = new java.util.HashMap<String, ValidProperty>();
        if (this.getClass().getPackage() != null
                && this.getClass().getPackage().getImplementationVersion() != null) {
            this.propsMap.put("RSB.Version", new ValidProperty(this.getClass()
                    .getPackage().getImplementationVersion()));
        } else {
            this.propsMap.put("RSB.Version", new ValidProperty("unknown"));
        }
        final Manifest manifest = this.getManifest();
        if (manifest != null
                && manifest.getMainAttributes().getValue("Last-Commit") != null) {
            this.propsMap.put("RSB.LastCommit", new ValidProperty(manifest
                    .getMainAttributes().getValue("Last-Commit")));
        } else {
            this.propsMap.put("RSB.LastCommit", new ValidProperty("archive"));
        }

        this.propsMap.put("RSB.Properties.Dump", new BooleanProperty("FALSE"));
        this.propsMap.put("RSB.LogAppender", new ValidProperty("CERR"));
        this.propsMap.put("RSB.Network.Interface", new ValidProperty("UNSET"));
        this.propsMap.put("RSB.ThreadPool.Size", new IntegerProperty("1"));
        this.propsMap.put("RSB.ThreadPool.SizeMax", new IntegerProperty("10"));
        this.propsMap.put("RSB.ThreadPool.QueueSize", new IntegerProperty(
                "10000"));

        this.propsMap.put("Spread.Path", new ValidProperty(""));
        this.propsMap.put("Spread.StartDaemon", new BooleanProperty("TRUE"));

        // New Properties
        final String[] reliabilityValues = { "RELIABLE", "UNRELIABLE" };
        this.propsMap.put("qualityofservice.reliability", new InSetProperty(
                "RELIABLE", reliabilityValues));
        final String[] orderedValues = { "ORDERED", "UNORDERED" };
        this.propsMap.put("qualityofservice.ordering", new InSetProperty(
                "ORDERED", orderedValues));

        final String[] errorValues = { "LOG", "PRINT", "EXIT" };
        this.propsMap.put("errorhandling.onhandlererror", new InSetProperty(
                "LOG", errorValues));

        this.propsMap.put("transport.inprocess.enabled", new BooleanProperty(
                "FALSE"));

        this.propsMap.put("transport.spread.enabled", new BooleanProperty(
                "TRUE"));
        this.propsMap.put("transport.spread.port", new IntegerProperty("4803"));
        this.propsMap.put("transport.spread.host", new ValidProperty(
                "localhost"));
        this.propsMap.put("transport.spread.retry", new IntegerProperty("50"));
        this.propsMap.put("transport.spread.tcpnodelay", new BooleanProperty(
                "TRUE"));
        this.propsMap.put("transport.spread.converter.java.utf-8-string",
                new ValidProperty("String"));
        this.propsMap.put("transport.spread.converter.java.string",
                new ValidProperty("String"));
        this.propsMap.put("transport.spread.converter.java.ascii-string",
                new ValidProperty("String"));
    }

    /**
     * Factory method for Properties singleton. Initializes default
     * configuration, loads the RSB config files from standard locations and
     * parses RSB environment variables.
     * 
     * @return the singleton instance
     */
    public static Properties getInstance() {
        if (singleton == null) {
            // properties set at a later point will overwrite already existing
            // ones
            // constructor initializes property map with defaults
            singleton = new Properties();

            // further initialization code (files, env)
            singleton.initialize();
        }
        return singleton;
    }

    /**
     * Reset properties to default values as specified by environment on
     * Singleton instance.
     * 
     * @return new singleton instance
     */
    public synchronized Properties reset() {
        // constructor initializes property map with defaults
        singleton.initializeMap();

        // further initialization code (files, env)
        singleton.initialize();

        return singleton;
    }

    /**
     */
    protected void initialize() {
        // parse rsb.conf files in standard locations
        this.loadFiles();

        // parse environment
        this.loadEnv();

        this.trimProperties();

        // be verbose or not
        if (this.getPropertyAsBool("RSB.Properties.Dump")) {
            dumpProperties();
        }
    }

    /**
     */
    protected void loadFiles() {
        // Read configuration properties in the following order:
        // 1. from /etc/rsb.conf, if the file exists
        // 2. from ${HOME}/.config/rsb.conf, if the file exists
        // 3. from $(pwd)/rsb.conf, if the file exists
        try {
            this.loadFile("/etc/rsb.conf");
        } catch (final FileNotFoundException ex) {
        } catch (final IOException ex) {
            System.err.println("Caught IOException trying to read "
                    + "/etc/rsb.conf: " + ex.getMessage());
        }

        final String homeDir = System.getProperty("user.home");
        try {
            this.loadFile(homeDir + "/.config/rsb.conf");
        } catch (final FileNotFoundException ex) {
        } catch (final IOException ex) {
            System.err.println("Caught IOException trying to read " + homeDir
                    + "/.config/rsb.conf: " + ex.getMessage());
        }

        final String workDir = System.getProperty("user.dir");
        try {
            this.loadFile(workDir + "/rsb.conf");
        } catch (final FileNotFoundException ex) {
        } catch (final IOException ex) {
            System.err.println("Caught IOException trying to read " + workDir
                    + "rsb.conf: " + ex.getMessage());
        }
    }

    public void loadFile(final String fn) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fn)));
        try {

            String section = "";
            while (reader.ready()) {
                String line = reader.readLine();
                int index = line.indexOf('#');
                if (index != -1) {
                    line = line.substring(0, index);
                }
                line = line.trim();
                if (line.equals("")) {
                    continue;
                }
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.substring(1, line.length() - 1);
                    continue;
                }
                index = line.indexOf('=');
                if (index == -1) {
                    System.err.println("Illegal line in configuration file: `"
                            + line + "'");
                    continue;
                }
                final String key = section + "."
                        + line.substring(0, index).trim();
                if ((key.startsWith("transport.spread.converter") && !key
                        .startsWith("transport.spread.converter.java"))
                        || (key.startsWith("transport") && !key
                                .startsWith("transport.spread"))
                        || (key.startsWith("plugins") && !key
                                .startsWith("plugins.java"))) {
                    continue;
                }
                final String value = line.substring(index + 1).trim();
                try {
                    this.setProperty(key, value);
                } catch (final InvalidPropertyException ex) {
                    System.err
                            .println("An exception occurred reading configuration from file '"
                                    + fn + "': " + ex.getMessage());
                }
            }

        } finally {
            reader.close();
        }

    }

    public void loadEnv() {
        this.parseMap(System.getenv());
    }

    protected void parseMap(final Map<String, String> env) {
        final Set<String> keys = env.keySet();
        final Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            final String key = it.next();
            if (key.startsWith("RSB_")) {
                try {
                    this.setProperty(key.substring(4).replace("_", ".")
                            .toLowerCase(), env.get(key));
                } catch (final InvalidPropertyException ex) {
                    System.err
                            .println("An exception occurred reading configuration from environment: "
                                    + ex.getMessage());
                }
            }
        }
    }

    public String getProperty(final String key) {
        if (this.propsMap.containsKey(key)) {
            return this.propsMap.get(key).getValue();
        } else {
            throw new InvalidPropertyException(
                    "Trying to get unknown property '" + key
                            + "' in Properties.getProperty()");
        }
    }

    public int getPropertyAsInt(final String key) {
        if (this.propsMap.containsKey(key)) {
            try {
                return Integer.parseInt(this.propsMap.get(key).getValue());
            } catch (final NumberFormatException e) {
                throw new InvalidPropertyException(
                        "Conversion of property '"
                                + key
                                + "' to integer failed in Properties.getPropertyAsInt()");
            }
        }
        throw new InvalidPropertyException("Trying to get unknown property '"
                + key + "' in Properties.getPropertyAsInt()");
    }

    public boolean getPropertyAsBool(final String key) {
        if (this.propsMap.containsKey(key)) {
            return this.propsMap.get(key).getValue() == "TRUE";
        }
        throw new InvalidPropertyException("Trying to get unknown property '"
                + key + "' in Properties.getPropertyAsBool()");
    }

    public void setProperty(final String key, final String value)
            throws InvalidPropertyException {
        // ignore RSC settings
        if (key.startsWith("rsc")) {
            return;
        }

        if (this.propsMap.containsKey(key)) {
            if (!this.propsMap.get(key).setValidValue(value)) {
                throw new InvalidPropertyException("Trying to set property '"
                        + key + "' to invalid value '" + value
                        + "' in Properties.setProperty()");
            }
        } else {
            throw new InvalidPropertyException(
                    "Trying to set unkown property '" + key
                            + "' in Properties.setProperty()");
        }
    }

}

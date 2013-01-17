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

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        for (String key : singleton.propsMap.keySet()) {
            log.info(key + " => '" + singleton.propsMap.get(key).getValue()
                     + "'");
        }
    }

    private void trimProperties() {
        for (ValidProperty v : singleton.propsMap.values()) {
            v.value = v.value.trim();
        }
    }

    private class ValidProperty {
        protected String value;
        protected String regex;

        protected ValidProperty(String v) {
            value = v;
            regex = "";
        }

        public String getValue() {
            return value;
        }

        public boolean setValidValue(String v) {
            if (regex != "") {
                if (!v.matches(regex)) {
                    return false;
                }
            }
            value = v;
            return true;
        }
    }

    private class IntegerProperty extends ValidProperty {
        public IntegerProperty(String v) {
            super("");
            regex = "[0-9]+";
            if (!setValidValue(v)) {
                value = "0";
            }
        }
    }

    private class InSetProperty extends ValidProperty {
        public InSetProperty(String default_, String[] vals) {
            super("");
            regex = "";
            for (int i = 0; i < vals.length; i++) {
                regex += vals[i];
                if (i < vals.length - 1) {
                    regex += "|";
                }
            }
            if (!setValidValue(default_))
                value = vals[0];
        }
    }

    private class BooleanProperty extends InSetProperty {
        BooleanProperty(String v) {
            super(v, boolValues);
        }

        public String getValue() {
            if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("YES")
                || value.equals("1")) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
    }

    protected java.util.HashMap<String, ValidProperty> propsMap = null;

    protected static Properties singleton = null;

    protected Properties() {
        initializeMap();
    }

    private Manifest getManifest() {
        Class<?> clazz = getClass();
        String className = clazz.getSimpleName() + ".class";
        URL classResource = clazz.getResource(className);
        if (classResource == null) {
            return null;
        }
        String classPath = classResource.toString();
        if (!classPath.startsWith("jar")) {
            // class not from JAR
            return null;
        }
        String manifestPath = classPath.substring(0,
                classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
        try {
            return new Manifest(new URL(manifestPath).openStream());
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    protected void initializeMap() {
        propsMap = new java.util.HashMap<String, ValidProperty>();
        if (getClass().getPackage() != null
                && getClass().getPackage().getImplementationVersion() != null) {
            propsMap.put("RSB.Version", new ValidProperty(getClass()
                    .getPackage().getImplementationVersion()));
        } else {
            propsMap.put("RSB.Version", new ValidProperty("unknown"));
        }
        Manifest manifest = getManifest();
        if (manifest != null
                && manifest.getMainAttributes().getValue("Last-Commit") != null) {
            propsMap.put("RSB.LastCommit", new ValidProperty(manifest
                    .getMainAttributes().getValue("Last-Commit")));
        } else {
            propsMap.put("RSB.LastCommit", new ValidProperty("archive"));
        }

        propsMap.put("RSB.Properties.Dump", new BooleanProperty("FALSE"));
        propsMap.put("RSB.LogAppender", new ValidProperty("CERR"));
        propsMap.put("RSB.Network.Interface", new ValidProperty("UNSET"));
        propsMap.put("RSB.ThreadPool.Size", new IntegerProperty("1"));
        propsMap.put("RSB.ThreadPool.SizeMax", new IntegerProperty("10"));
        propsMap.put("RSB.ThreadPool.QueueSize", new IntegerProperty("10000"));

        propsMap.put("Spread.Path", new ValidProperty(""));
        propsMap.put("Spread.StartDaemon", new BooleanProperty("TRUE"));

        // New Properties
        String[] reliabilityValues = { "RELIABLE", "UNRELIABLE" };
        propsMap.put("qualityofservice.reliability", new InSetProperty(
                                                                       "RELIABLE", reliabilityValues));
        String[] orderedValues = { "ORDERED", "UNORDERED" };
        propsMap.put("qualityofservice.ordering", new InSetProperty("ORDERED",
                                                                    orderedValues));

        String[] errorValues = { "LOG", "PRINT", "EXIT" };
        propsMap.put("errorhandling.onhandlererror", new InSetProperty("LOG",
                                                                       errorValues));

        propsMap.put("transport.inprocess.enabled", new BooleanProperty("FALSE"));

        propsMap.put("transport.spread.enabled", new BooleanProperty("TRUE"));
        propsMap.put("transport.spread.port", new IntegerProperty("4803"));
        propsMap.put("transport.spread.host", new ValidProperty("localhost"));
        propsMap.put("transport.spread.retry", new IntegerProperty("50"));
        propsMap.put("transport.spread.tcpnodelay", new BooleanProperty("TRUE"));
        propsMap.put("transport.spread.converter.java.utf-8-string",
                     new ValidProperty("String"));
        propsMap.put("transport.spread.converter.java.string",
                     new ValidProperty("String"));
        propsMap.put("transport.spread.converter.java.ascii-string",
                     new ValidProperty("String"));
    }

	/**
	 * Factory method for Properties singleton.
	 * Initializes default configuration, loads the 
	 * RSB config files from standard locations
	 * and parses RSB environment variables.
	 * 
	 */
    public static Properties getInstance() {
        if (singleton == null) {
            // properties set at a later point will overwrite already existing ones
        	// constructor initializes property map with defaults
            singleton = new Properties();

            // further initialization code (files, env)
            singleton.initialize();
        }
        return singleton;
    }
    
    
    /**
     * Reset properties to default values as specified by environment
     * on Singleton instance.
     * 
     */
    public synchronized Properties reset() {
    	// constructor initializes property map with defaults
        singleton.initializeMap();

        // further initialization code (files, env)
        singleton.initialize();
        
        return singleton;
    }  	

	/**
	 * 
	 */
	protected void initialize() {
		// parse rsb.conf files in standard locations
		loadFiles();
		
		// parse environment
		loadEnv();
		
		trimProperties();
		
		// be verbose or not
		if (getPropertyAsBool("RSB.Properties.Dump")) {
		    dumpProperties();
		}
	}

	/**
	 * 
	 */
	protected void loadFiles() {
        // Read configuration properties in the following order:
        // 1. from /etc/rsb.conf, if the file exists
        // 2. from ${HOME}/.config/rsb.conf, if the file exists
        // 3. from $(pwd)/rsb.conf, if the file exists
		try {
		    loadFile("/etc/rsb.conf");
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		    System.err.println("Caught IOException trying to read "
		                       + "/etc/rsb.conf: " + ex.getMessage());
		}

		final String homeDir = System.getProperty("user.home");
		try {
		    loadFile(homeDir + "/.config/rsb.conf");
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		    System.err.println("Caught IOException trying to read "
		                       + homeDir + "/.config/rsb.conf: " + ex.getMessage());
		}

		final String workDir = System.getProperty("user.dir");
		try {
		    loadFile(workDir + "/rsb.conf");
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		    System.err.println("Caught IOException trying to read "
		                       + workDir + "rsb.conf: " + ex.getMessage());
		}
	}

    public void loadFile(String fn) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
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
                String key = section + "." + line.substring(0, index).trim();
                if ((key.startsWith("transport.spread.converter") && !key
                        .startsWith("transport.spread.converter.java"))
                        || (key.startsWith("transport") && !key
                                .startsWith("transport.spread"))
                        || (key.startsWith("plugins") && !key
                                .startsWith("plugins.java"))) {
                    continue;
                }
                String value = line.substring(index + 1).trim();
                try {
                    setProperty(key, value);
                } catch (InvalidPropertyException ex) {
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
        parseMap(System.getenv());
    }

    protected void parseMap(Map<String, String> env) {
        Set<String> keys = env.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.startsWith("RSB_")) {
                try {
                    setProperty(key.substring(4).replace("_", ".")
                                .toLowerCase(), env.get(key));
                } catch (InvalidPropertyException ex) {
                    System.err
                        .println("An exception occurred reading configuration from environment: "
                                 + ex.getMessage());
                }
            }
        }
    }

    public String getProperty(String key) {
        if (propsMap.containsKey(key)) {
            return propsMap.get(key).getValue();
        } else {
            throw new InvalidPropertyException(
                                               "Trying to get unknown property '" + key
                                               + "' in Properties.getProperty()");
        }
    }

    public int getPropertyAsInt(String key) {
        if (propsMap.containsKey(key)) {
            try {
                return Integer.parseInt(propsMap.get(key).getValue());
            } catch (NumberFormatException e) {
                throw new InvalidPropertyException(
                                                   "Conversion of property '"
                                                   + key
                                                   + "' to integer failed in Properties.getPropertyAsInt()");
            }
        }
        throw new InvalidPropertyException("Trying to get unknown property '"
                                           + key + "' in Properties.getPropertyAsInt()");
    }

    public boolean getPropertyAsBool(String key) {
        if (propsMap.containsKey(key)) {
            return propsMap.get(key).getValue() == "TRUE";
        }
        throw new InvalidPropertyException("Trying to get unknown property '"
                                           + key + "' in Properties.getPropertyAsBool()");
    }

    public void setProperty(String key, String value)
        throws InvalidPropertyException {
    	// ignore RSC settings
    	if (key.startsWith("rsc")) return;
    	
        if (propsMap.containsKey(key)) {
            if (!propsMap.get(key).setValidValue(value)) {
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

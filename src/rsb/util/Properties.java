package rsb.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Properties {

	private final static Logger log = Logger.getLogger(Properties.class.getName());	
	
	private static void dumpProperties() {
		for (String key : singleton.propsMap.keySet() ) {
			log.info(key + " => '" + singleton.propsMap.get(key).getValue() + "'");
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
			if(regex != "") {
				if(!v.matches(regex)) {
					return false;
				}
			}
			value = v;
			return true;
		}
	}
	private class IntegerProperty extends ValidProperty {
		public IntegerProperty(String v){
			super("");
			regex = "[0-9]+";
			if(!setValidValue(v)) {
				value = "0";
			}
		}
	}
	private class InSetProperty extends ValidProperty {
		public InSetProperty(){
			super("");
		}
		protected void setValueSet(String[] vals) {
			regex = "";
			for(int i=0; i<vals.length; i++){
				regex += vals[i];
				if(i<vals.length-1) {
					regex += "|";
				}
			}
		}
	}
	
	private class BooleanProperty extends InSetProperty {
		String[] lvls = {"TRUE", "FALSE", "YES", "NO", "true", "false", "yes", "no", "1", "0"};
		BooleanProperty(String v){
			super();
			setValueSet(lvls);
			if(!setValidValue(v)) {
				value = "FALSE";
			}
		}
		public String getValue() {
			if(value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("YES") || value.equals("1")) {
				return "TRUE";
			} else {
				return "FALSE";
			}
		}
	}
	
	protected java.util.HashMap<String, ValidProperty> propsMap = null;
	
	protected static Properties singleton = null;
	
	protected Properties() {
		resetDefaults();
	}
	
	public void resetDefaults() {
		propsMap = new java.util.HashMap<String, ValidProperty>();
		propsMap.put("RSB.Version", new ValidProperty("0.1"));
//		String localhost = "localhost";
//		try {
//			localhost = InetAddress.getLocalHost().getHostName();
//		} catch (UnknownHostException e) {			
//			e.printStackTrace();
//		}
		propsMap.put("RSB.Properties.Dump", new BooleanProperty("FALSE"));
		propsMap.put("RSB.LogAppender", new ValidProperty("CERR"));
		propsMap.put("RSB.Network.Interface", new ValidProperty("UNSET"));
		propsMap.put("RSB.ThreadPool.Size",  new IntegerProperty("1"));
		propsMap.put("RSB.ThreadPool.SizeMax", new IntegerProperty("10"));
		propsMap.put("RSB.ThreadPool.QueueSize", new IntegerProperty("10000"));
		
		propsMap.put("Spread.Port", new IntegerProperty("4803"));
		propsMap.put("Spread.Host", new ValidProperty("localhost"));
		propsMap.put("Spread.Path", new ValidProperty(""));
		propsMap.put("Spread.StartDaemon", new BooleanProperty("TRUE"));
		propsMap.put("Spread.TcpNoDelay", new BooleanProperty("TRUE"));
		
	}

	public static Properties getInstance() {
		if(singleton == null) {
			// Read configuration properties in the following order:
			//  1. from .rsbrc in user's home directory, if exists 
			//  2. from .rsbrc in working directory, if exists
			//  3. from environment
			// properties set at a later point will overwrite already existing ones
			singleton = new Properties();
			String workDir = System.getProperty("user.dir");
			String homeDir = System.getProperty("user.home");
			try {
				singleton.loadFile(homeDir + "/.xcfrc");
				System.out.println("Found .rsbrc in home directory '" + homeDir + "'");
			} catch(FileNotFoundException ex) {
				System.out.println("No .rsbrc found in home directory '" + homeDir + "'");
			} catch(IOException ex) {
				System.err.println("Caught IOException trying to read " + homeDir + "/.xcfrc: " + ex.getMessage());
			}
			try {
				singleton.loadFile(workDir + "/.rsbrc");
				System.out.println("Found .rsbrc in working directory '" + workDir + "'");
			} catch(FileNotFoundException ex) {
				System.out.println("No .rsbrc found in working directory '" + workDir + "'");
			} catch(IOException ex) {
				System.err.println("Caught IOException trying to read " + workDir + "/.xcfrc: " + ex.getMessage());
			}
			singleton.loadEnv();
			singleton.trimProperties();
			if (singleton.getPropertyAsBool("RSB.Properties.Dump")) {
				dumpProperties();
			}
		}
		return singleton;
	}
	
	protected void loadFile(String fn) throws IOException {
		FileInputStream s = new FileInputStream(fn);
		java.util.Properties p = new java.util.Properties();
		p.load(s);
		Enumeration<?> keys = p.propertyNames();
		while(keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			try {
				setProperty(key, p.getProperty(key));
			} catch(InvalidPropertyException ex) {
				System.err.println("An exception occurred reading configuration from file '" + fn + "': " + ex.getMessage());
			}
		}
	}
	
	public void loadEnv() {
		parseMap(System.getenv());
	}

	protected void parseMap(Map<String,String> env) {
		Set<String> keys = env.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(key.startsWith("RSB_") || key.startsWith("Spread_")) {
				try {
					// System.out.println("Found key in environment: " + key);
					setProperty(key.replace("_", "."), env.get(key));
				} catch(InvalidPropertyException ex) {
					System.err.println("An exception occurred reading configuration from environment: " + ex.getMessage());
				}
			}
		}
	}
	
	public String getProperty(String key) {
		if(propsMap.containsKey(key)) {
			return propsMap.get(key).getValue();
		} else {
			throw new InvalidPropertyException("Trying to get unknown property '" + key + "' in Properties.getProperty()");
		}
	}
	
	public int getPropertyAsInt(String key) {
		if(propsMap.containsKey(key)) {
			try {
				return Integer.parseInt(propsMap.get(key).getValue());
			} catch (NumberFormatException e) {
				throw new InvalidPropertyException("Conversion of property '" + key + "' to integer failed in Properties.getPropertyAsInt()");
			}
		}
		throw new InvalidPropertyException("Trying to get unknown property '" + key + "' in Properties.getPropertyAsInt()");
	}
	
	public boolean getPropertyAsBool(String key) {
		if(propsMap.containsKey(key)) {
			return propsMap.get(key).getValue() == "TRUE";
		}
		throw new InvalidPropertyException("Trying to get unknown property '" + key + "' in Properties.getPropertyAsBool()");
	}
	
	public void setProperty(String key, String value) throws InvalidPropertyException {
		if(propsMap.containsKey(key)) {
			if(!propsMap.get(key).setValidValue(value)) {
				throw new InvalidPropertyException("Trying to set property '" + key + "' to invalid value '" + value + "' in Properties.setProperty()");
			}
		} else {
			throw new InvalidPropertyException("Trying to set unkown property '" + key + "' in Properties.setProperty()");
		}
	}
	
}

package rsb.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import nu.xom.converters.DOMConverter;

import org.xml.sax.SAXException;

import rsb.transport.XOPData;

/**
 * <p>
 * This class is the central access point for XML schemas. It is implemented
 * according to the singleton pattern, since schemas are usually loaded at
 * startup and do not change afterwards. Schemas are stored under a unique name,
 * which can be arbitrarily chosen by the application developer. When an
 * <code>XOM Document</code> is passed to the <code>SchemaRegistry</code> for
 * validation, it must specify the schema names to validate against in the
 * attributes xsi:noNamespaceSchemaLocation and/or xsi:schemaLocation.
 * </p>
 * 
 * @author jschaefe
 *
 */

public class SchemaValidator {

	private static final Logger log = Logger.getLogger(SchemaValidator.class.getName()); 
	
	protected static SchemaValidator instance = null;
	private static DocumentBuilder builder = null;
	private Map<String, Schema> schemas = null;
	
	
	protected SchemaValidator() {
		schemas = new HashMap<String, Schema>();
	}
	
	public static SchemaValidator getInstance() {
		if(instance == null) {
			instance = new SchemaValidator();
		}
		return instance;
	}
	
	protected static DocumentBuilder getDocumentBuilder() {
		if(builder == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				log.severe("Could not create DocumentBuilder: " + e.getMessage());
				assert(false);
			}
		}
		return builder;
	}

	public void addSchema(String name, String filename) throws SyntaxException {
		if(schemas.containsKey(name)) {
			log.warning("Schema '" + name + "' is already set and will be replaced from file '" + filename + "'");
		}
		File schemaFile = new File(filename);
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = factory.newSchema(schemaFile);
			schemas.put(name, schema);
		} catch (SAXException e) {
			throw new SyntaxException("Schema '" + name + "' in file '" + filename + "' caused a parsing exception: " + e.getMessage());
		}
	}
	
	public void removeSchema(String name) {
		schemas.remove(name);
	}
	
	public Schema getSchema(String name) throws SchemaNotFoundException {
		if(schemas.containsKey(name)) {
			return schemas.get(name);
		}
		throw new SchemaNotFoundException("Schema '" + name + "' could not be found in registry");
	}

	/**
	 * Tries to validate the <code>XOM Document</code> of the passed in 
	 * <code>XOPData</code> against all schemas specified in it. If the
	 * <code>Document</code> is not set, it is always considered invalid.
	 * Else, if the <code>Document</code> is set, but no schemas are specified,
	 * it is always considered valid. 
	 * The method looks for schema names in all attributes named
	 * <code>xsi:noNamespaceSchemaLocation</code> or
	 * <code>xsi:schemaLocation</code>. The former may contain a single schema
	 * name, the latter a space separated list of namespace/name pairs. All
	 * names found will be used to retrieve the corresponding schemas from the
	 * registry and validate the <code>Document</code> against them. In case a
	 * name does not refer to a schema, an exception is thrown.
	 * 
	 * @param xop
	 * @throws ValidationFailedException if the XML document is not valid.
	 */
	public void validate(XOPData xop) throws ValidationFailedException {
		Document doc = xop.getDocument();
		if(doc == null) throw new ValidationFailedException("Validation failed: Document is null", xop);
		// extract schemas from XML
		// TODO this should be done by XOPData
		Nodes noNsLocs = doc.query("//@xsi:noNamespaceSchemaLocation", new XPathContext("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		Nodes nsLocs = doc.query("//@xsi:schemaLocation", new XPathContext("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		if(nsLocs.size() == 0 && noNsLocs.size() == 0) return;
		LinkedList<String> schemas = new LinkedList<String>();
		for(int i=0; i<noNsLocs.size(); i++) {
			Node n = noNsLocs.get(i);
			String noNsSchema = n.getValue();
			schemas.add(noNsSchema);
		}
		for(int i=0; i<nsLocs.size(); i++) {
			Node n = nsLocs.get(i);
			String[] nsSchemas = n.getValue().split(" ");
			for(int j=1; j<nsSchemas.length; j+=2) {
				schemas.add(nsSchemas[j]);
			}
		}
		// validate XOPData against all schemas defined in it
		for(String name : schemas) {
			Schema schema = getSchema(name);
			Validator validator = schema.newValidator();
			org.w3c.dom.Document w3doc = xop.getDocumentAsDOM(getDocumentBuilder().getDOMImplementation());
			try {
				validator.validate(new DOMSource(w3doc));
			} catch (SAXException e) {
				throw new ValidationFailedException("Validation of Document failed: " + e.getMessage(), xop);
			} catch (IOException e) {
				throw new ValidationFailedException("Validation of Document failed: " + e.getMessage(), xop);
			}
		}
	}
	
}

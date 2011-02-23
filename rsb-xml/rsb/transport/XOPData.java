/**
 * 
 */
package rsb.transport;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import rsb.xml.DocumentBuilder;
import rsb.xml.SyntaxException;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XMLException;
import nu.xom.converters.DOMConverter;

/**
 * <p>
 * The class <code>XOPData</code> provides an <code>XOP</code> like data
 * container as the general data type to be used in <code>XCF</code>
 * communication. Each instance of this class can hold an <code>XML</code>
 * document alongside any number of binary <code>Attachments</code>.
 * <code>Attachments</code> are referenced by URIs, which are simply arbitrary
 * <code>Strings</code>. URIs have to be unique per <code>XOPData</code>
 * instance, each name references exactly one <code>Attachment</code>.
 * </p>
 * <p>
 * The <code>XML</code> document is stored using an <code>XOM Document</code>.
 * <code>XOPData</code> offers convenience constructors and setters/getters for
 * <code>String</code> and <code>org.w3c.dom.Document</code> representations of
 * <code>XML</code> documents.
 * </p>
 * @see Attachment 
 * @author swrede
 *
 */
public class XOPData {

	private static final Logger log = Logger.getLogger(XOPData.class.getName());
	protected Document doc = null;
	protected Map<String, Attachment> attachments = new java.util.HashMap<String, Attachment>();

	public static XOPData fromString(String d) throws SyntaxException {
		try {
			return new XOPData(DocumentBuilder.createDocument(d));
		} catch (ValidityException e) {
			log.warning("Caught ValidityException trying to set XOPData's document from string.");
			throw new SyntaxException("Could not create XOPData from XML string. Parsing failed: " + e.getMessage());
		} catch (ParsingException e) {
			throw new SyntaxException("Could not create XOPData from XML string. Parsing failed: " + e.getMessage());
		}
	}

	/**
	 * Constructs an <code>XOPData</code> container from a given
	 * <code>XML</code> document string. XOPData tries to parse the string into
	 * an <code>XOM Document</code> and throws corresponding exceptions if
	 * validation fails or the <code>XML</code> is malformed.
	 * 
	 * @param d The <code>XML</code> document in string representation.
	 * @throws ValidationFailedException thrown if validation of document fails 
	 * @throws SyntaxException thrown if document is not well-formed
	 */
	@Deprecated
	public XOPData(String d) throws SyntaxException {
		setDocument(d);
	}

	/**
	 * Constructs an empty <code>XOPData</code> container.
	 * The new object will contain neither an <code>XML</code> document nor any
	 * <code>Attachments</code>.
	 * A call to <code>XOPData.getDocumentAsText()</code> will return
	 * <code>null</code> before a document is set.
	 */
	public XOPData() {
	}

	public XOPData(XOPData xop) {
		doc = xop.doc;
		attachments = xop.attachments;
	}

	/**
	 * Constructs an <code>XOPData</code> container from an existing <code>XOM
	 * Document</code>. <code>XOM</code> is the preferred XML toolkit for use
	 * with XCF.
	 */
	public XOPData(Document d) {
		setDocument(d);
	}

	/**
	 * Constructs an <code>XOPData</code> container from an existing
	 * <code>org.w3c.dom.Document</code>. The <code>Document</code> will be
	 * converted internally to an <code>XOM Document</code>, which imposes a
	 * certain overhead. It is recommended to use <code>XOM Documents</code> for
	 * higher performance.
	 * @throws SyntaxException if the provided document is not well-formed 
	 */
	public XOPData(org.w3c.dom.Document d) throws SyntaxException {
		setDocument(d);
	}

	/**
	 * Sets this <code>XOPData</code>'s <code>XML</code> document to the
	 * provided <code>XML</code> string. <code>XOPData</code> tries to parse the
	 * string into an <code>XOM Document</code> right away and throws
	 * corresponding exceptions if validation fails or the document is not
	 * well-formed
	 * 
	 * @param d <code>String</code> containing the <code>XML</code> document
	 * @see XOPData#getDocument()
	 */
	public void setDocument(String d) throws SyntaxException {
		try {
			doc = DocumentBuilder.createDocument(d);
		} catch (ValidityException e) {
			log.warning("Caught ValidityException trying to set XOPData's document from string.");
			throw new SyntaxException("Could not create XOPData from XML string. Parsing failed: " + e.getMessage());
		} catch (ParsingException e) {
			throw new SyntaxException("Could not create XOPData from XML string. Parsing failed: " + e.getMessage());
		}
	}

	/**
	 * Sets this <code>XOPData</code>'s <code>XOM Document</code>.
	 * <code>XOM</code> is the preferred <code>XML</code> toolkit to be used
	 * with <code>XCF</code>.
	 * 
	 * @param d the <code>XOM Document</code> to be used by this
	 * <code>XOPData</code>.
	 */
	public void setDocument(Document d) {
		doc = d;
	}

	/**
	 * Sets this <code>XOPData</code>'s <code>XOM Document</code> from the
	 * provided <code>org.w3c.dom.Document</code>. The document is converted
	 * internally to an <code>XOM Document</code> which imposes a certain
	 * overhead. It is recommended to use <code>XOM Documents</code> for higher
	 * performance.
	 * 
	 * @param d an <code>org.w3c.dom.Document</code> to be used by this
	 * <code>XOPData</code>
	 * @throws SyntaxException if the provided document is not well-formed
	 */
	public void setDocument(org.w3c.dom.Document d) throws SyntaxException {
		try {
			doc = DOMConverter.convert(d);
		} catch (XMLException e) {
			throw new SyntaxException("Could not create XOPData from malformed DOM Document: " + e.getMessage());
		}
	}

	/**
	 * @return the <code>XOM Document</code> contained in this
	 * <code>XOPData</code> or <code>null</code> if not set.
	 */
	public Document getDocument() {
		return doc;
	}

	/**
	 * Converts the <code>XML</code> document contained in this
	 * <code>XOPData</code> to an <code>org.w3c.dom.Document</code> and returns
	 * it. An <code>org.w3c.dom.DOMImplementation</code> has to be provided for
	 * the conversion.
	 * 
	 * @param impl an <code>org.w3c.dom.DOMImplementation</code> to be used for
	 * the conversion.
	 * @return the <code>XML</code> document contained in this
	 * <code>XOPData</code> converted to an <code>org.w3c.dom.Document</code> or
	 * <code>null</code> if the document is not set.
	 */
	public org.w3c.dom.Document getDocumentAsDOM(org.w3c.dom.DOMImplementation impl) {
		if (doc != null) {
			return DOMConverter.convert(doc, impl);
		}
		return null;
	}

	/**
	 * Returns the <code>String</code> representation of the contained
	 * <code>XOM</code> document.
	 * @return <code>XML</code> document string
	 */
	public String getDocumentAsText() {
		if (doc != null) {
			return doc.toXML();
		}
		return null;
	}

	/**
	 * Adds an <code>Attachment</code> identified by URI to this
	 * <code>XOPData</code>'s list.
	 * An already existing <code>Attachment</code> identified by the same URI
	 * will be replaced and deleted.
	 * @param uri <code>String</code> identifying this <code>Attachment</code>
	 * @param a <code>Attachment</code> to be added
	 * @see XOPData#getAttachment(String)
	 * @see Attachment
	 */
	public void addAttachment(String uri, Attachment a) {
		attachments.put(uri, a);
	}

	/**
	 * Returns the <code>Attachment</code> identified by URI.
	 * @param uri <code>String</code> identifying the <code>Attachment</code>
	 * @return The corresponding <code>Attachment</code> from the list
	 * @throws AttachmentNotFoundException thrown if no <code>Attachment</code>
	 * is found for the specified uri.
	 * @see XOPData#addAttachment(String, Attachment)
	 * @see Attachment
	 */
	public Attachment getAttachment(String uri) {
		if (attachments.containsKey(uri)) {
			return attachments.get(uri);
		} else {
			throw new AttachmentNotFoundException("No Attachment found for uri '" + uri + "'");
		}
	}

	/**
	 * Returns an <code>Iterator</code> running over all URIs of this
	 * <code>XOPData</code>'s <code>Attachment</code>s.
	 * @return <code>Iterator</code> over all URIs in this <code>XOPData</code>
	 */
	public Iterator<String> getUriIterator() {
		return attachments.keySet().iterator();
	}

	/**
	 * Checks whether this <code>XOPData</code> instance contains an xml string
	 * or any <code>Attachment</code>s.
	 * @return true if neither <code>XML</code> string nor any
	 * <code>Attachment</code>s are set.
	 */
	public boolean isEmpty() {
		return doc == null && attachments.size() == 0;
	}

	/**
	 * Assigns the contents of the passed <code>XOPData</code> to this instance.
	 * This does not perform a deep copy, this <code>XOPData</code> instance
	 * just references the assigned contents. Changes in one instance
	 * are reflected to the other.
	 * @param xop2 <code>XOPData</code> instance from which to assign the
	 * contents to this instance
	 */
	public void assign(XOPData xop2) {
		this.doc = xop2.doc;
		this.attachments = xop2.attachments;
	}

	/**
	 * Checks whether this <code>XOPData</code> has an <code>Attachment</code>
	 * identified by the specified URI.
	 * @param uri <code>String</code> identifying an <code>Attachment</code>
	 * @return <code>true</code> if an <code>Attachment</code> was found for
	 * the given URI
	 */
	public boolean hasAttachment(String uri) {
		if (attachments.containsKey(uri)) {
			return true;
		}
		return false;
	}
	public static final String DBXML_NS = "http://www.sleepycat.com/2002/dbxml";

	public String getXcfId() {
		Attribute a = doc.getRootElement().getAttribute("id", DBXML_NS);
		if (a == null) {
			return null;
		} else {
			return a.getValue();
		}
	}

	@Override
	public String toString() {
		return "XOPData[id=" + getXcfId() + ", root=" +
			this.doc.getRootElement().getLocalName() +
			", attachments=" + this.attachments.keySet() +
			"]";
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof XOPData)) {
			return false;
		}

		XOPData otherXOP = (XOPData) other;
		return doc.toXML().equals(otherXOP.doc.toXML()) && attachmentsEquals(otherXOP.attachments);
	}

	private boolean attachmentsEquals(Map<String, Attachment> other) {
		for (String key : attachments.keySet()) {
			Attachment a = other.get(key);
			if (a == null) {
				return false;
			}
			if (!Arrays.equals(attachments.get(key).getValue(), a.getValue())) {
				return false;
			}
		}
		return true;
	}
	/*
	TODO Implement schema support in XOPData
	public void addSchema(String name) {

	}

	public void addSchema(String namespace, String name) {

	}
	
	public void removeSchema(String name) {

	}

	public void removeSchema(String namespace, String name) {

	}
	
	public Vector<String> getSchemas() {

	}
	
	public Vector<String> getSchemas(String namespace) {

	}
	 */
}

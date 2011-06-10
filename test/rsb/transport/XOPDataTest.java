/**
 * 
 */
package rsb.transport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nu.xom.Document;
import nu.xom.Nodes;

import org.junit.Test;
import org.xml.sax.InputSource;

import rsb.xml.SyntaxException;

/**
 * @author swrede
 * 
 */
public class XOPDataTest {

	String newline = System.getProperty("line.separator");
	final String input = "<?xml version=\"1.0\"?>" + newline + "<testnode />"
			+ newline;

	/**
	 * Test method for
	 * {@link net.sf.xcf.transport.XOPData#XOPData(java.lang.String)}.
	 * 
	 * @throws SyntaxException
	 */
	@Test
	public void testXOPDataString() throws SyntaxException {
		XOPData xop;
		// FIXME this fails under windows, probably because of different
		// newline handling
		// Suggestion: use an XOPData to parse the xml string and get an
		// adequate string from getDocumentAsString()
		xop = XOPData.fromString(input);
		assertEquals(input, xop.getDocumentAsText());

		try {
			xop = XOPData.fromString("test");
		} catch (SyntaxException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testAddBinary() {
		XOPData xop = new XOPData();
		try {
			xop = XOPData.fromString(input);
		} catch (SyntaxException e) {
			e.printStackTrace();
			fail("SyntaxException: " + e.getMessage());
		}
		// TODO move URI as parameter in Attachment and
		// change XOP Data accordingly
		Attachment a = new Attachment();
		byte[] b = { 1, 7, 8, 2, 3 };
		a.setValue(b);
		xop.addAttachment("Byte Array", a);
	}

	/**
	 * Test method for {@link net.sf.xcf.transport.XOPData#XOPData()}.
	 */
	@Test
	public void testXOPData() {
		XOPData xop = new XOPData();
		assertNull(xop.getDocumentAsText());
		assertNull(xop.getDocument());
	}

	/**
	 * Test method for
	 * {@link net.sf.xcf.transport.XOPData#setDocument(java.lang.String)}.
	 * 
	 * @throws SyntaxException
	 */
	@Test
	public void testSetDoc() throws SyntaxException {
		XOPData xop = new XOPData();
		xop.setDocument(input);
		// FIXME this fails under windows, probably because of different newline
		// handling
		// Suggestion: use an XOPData to parse the xml string and get an
		// adequate string from getDocumentAsString()
		assertEquals(input, xop.getDocumentAsText());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testNonXML() {
		XOPData xop = new XOPData();
		try {
			xop.setDocument("invalid");
			fail("Expected SyntaxException not thrown...");
		} catch (Exception e) {
			assertTrue(e instanceof SyntaxException);
		}
		try {
			new XOPData("invalid");
			fail("Expected SyntaxException not thrown...");
		} catch (Exception e) {
			assertTrue(e instanceof SyntaxException);
		}
	}

	/**
	 * Test method for {@link net.sf.xcf.transport.XOPData#getDocumentAsText()}.
	 * 
	 * @throws SyntaxException
	 */
	@Test
	public void testGetDocumentAsText() throws SyntaxException {
		XOPData xop;
		// FIXME this fails under windows, probably because of different
		// newline handling
		// Suggestion: use an XOPData to parse the xml string and get an
		// adequate string from getDocumentAsString()
		xop = XOPData.fromString(input);
		assertEquals(input, xop.getDocumentAsText());
	}

	@Test
	public void testAssign() throws SyntaxException {
		XOPData xop1 = XOPData
				.fromString("<?xml version=\"1.0\"?>\n<TESTNODE />\n");
		XOPData xop2 = new XOPData();
		xop2.assign(xop1);
		assertSame(xop1.doc, xop2.doc);
		assertSame(xop1.attachments, xop2.attachments);
	}

	@Test
	public void testDOMDocument() throws Throwable {
		// from DOM to XOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(
				"<?xml version=\"1.0\"?>\n<NODE />\n");
		InputSource is = new InputSource(reader);
		org.w3c.dom.Document document = builder.parse(is);
		XOPData xop = new XOPData(document);
		Document doc = xop.getDocument();
		Nodes ns = doc.query("/NODE");
		assertEquals(1, ns.size());
		ns = doc.query("//*");
		assertEquals(1, ns.size());
		// from XOM to DOM
		XOPData xop2 = XOPData
				.fromString("<?xml version=\"1.0\"?>\n<NODE />\n");
		org.w3c.dom.Document doc2 = xop2.getDocumentAsDOM(builder
				.getDOMImplementation());
		assertEquals("NODE", doc2.getDocumentElement().getNodeName());
		assertEquals(0, doc2.getDocumentElement().getChildNodes().getLength());
	}

	@Test
	public void testEquals() throws SyntaxException {
		String xml1 = "<?xml version=\"1.0\"?>" + "<node1 attr1=\"val1\">"
				+ "  <node2 attr2=\"val2\"/>" + "  <node3 attr3=\"val3\"/>"
				+ "</node1>";
		String xml2 = "<?xml version=\"1.0\"?>" + "<node1 attr1=\"val1\">"
				+ "  <node2 attr2=\"val2\"></node2>"
				+ "  <node3 attr3=\"val3\"/>" + "</node1>";
		String xml3 = "<?xml version=\"1.0\"?>" + "<node1 attr1=\"val1\">"
				+ "  <node3 attr3=\"val3\"/>" + "  <node2 attr3=\"val2\"/>"
				+ "</node1>";
		XOPData xop1 = XOPData.fromString(xml1);
		XOPData xop2 = XOPData.fromString(xml2);
		XOPData xop3 = XOPData.fromString(xml3);
		assertTrue(xop1.equals(xop1));
		assertTrue(xop1.equals(xop2));
		assertTrue(xop2.equals(xop1));
		assertFalse(xop1.equals(xop3));
		assertFalse(xop1.equals(null));
		byte[] data1 = new byte[1000];
		byte[] data2 = new byte[1000];
		byte[] data3 = new byte[1000];
		for (int i = 0; i < 1000; i++) {
			byte b = (byte) Math.round(Math.random() * 255);
			data1[i] = b;
			data2[i] = b;
			data3[i] = (byte) Math.round(Math.random() * 255);
		}
		Attachment a1 = new Attachment();
		Attachment a2 = new Attachment();
		Attachment a3 = new Attachment();
		a1.setValue(data1);
		a2.setValue(data2);
		a3.setValue(data3);
		xop1.addAttachment("attachment", a1);
		xop2.addAttachment("attachment", a2);
		xop3.addAttachment("attachment", a3);
		assertTrue(xop1.equals(xop1));
		assertTrue(xop1.equals(xop2));
		assertFalse(xop1.equals(xop3));
		xop2.attachments.remove("attachment");
		xop2.addAttachment("other", a2);
		assertFalse(xop1.equals(xop2));
		xop2.attachments.remove("other");
		xop2.addAttachment("attachment", a3);
		assertFalse(xop1.equals(xop2));
	}

}

/**
 * 
 */
package rsb.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.transport.XOPData;

/**
 * @author rgaertne
 * 
 */
public class XPathMatchingTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link net.sf.xcf.xml.XPath#validate()}.
	 * 
	 * @throws SyntaxException
	 */
	@Test
	public void testIsXPathMatching() throws SyntaxException {
		XOPData doc = XOPData
				.fromString("<?xml version=\"1.0\" ?><Hello value=\"universe\" xmlns:dbxml=\"http://www.sleepycat.com/2002/dbxml\" dbxml:id=\"1\"/>");
		XPath xpath = new XPath("//*");
		assertTrue("false", xpath.match(doc));

		xpath = new XPath("/Hello");
		assertTrue(xpath.match(doc));

		xpath = new XPath("//False");
		assertFalse(xpath.match(doc));

		xpath = new XPath("//*[@dbxml:id=\"1\"]", new RSBNamespaceContext(
				"dbxml", "http://www.sleepycat.com/2002/dbxml"));
		assertTrue(xpath.match(doc));

		xpath = new XPath("//*[@dbxml:id=\"2\"]", new RSBNamespaceContext(
				"dbxml", "http://www.sleepycat.com/2002/dbxml"));
		assertFalse(xpath.match(doc));

		doc = XOPData
				.fromString("<OBJECT dbxml:id=\"81\" xmlns:dbxml=\"http://www.sleepycat.com/2002/dbxml\"><GENERATOR name=\"BOOST_DETECTOR\" timestamp=\"0123456789\"/>"
						+ "<CLASS>-1</CLASS><REGION image=\"00003050\"><RECTANGLE><COORDS h=\"24\" w=\"50\" x=\"580\" y=\"302\"/></RECTANGLE></REGION></OBJECT>");
		xpath = new XPath("/OBJECT[boolean(GENERATOR/@timestamp)]");
		assertTrue(xpath.match(doc));

		xpath = new XPath("/OBJECT[not(boolean(GENERATOR/@timestamp))]");
		assertFalse(xpath.match(doc));

		xpath = new XPath(
				"/OBJECT[GENERATOR[@name=\"BOOST_DETECTOR\"] and REGION[@image=\"00003050\"]]");
		assertTrue(xpath.match(doc));

		xpath = new XPath(
				"/OBJECT[GENERATOR[@name=\"BOOST_DETECTOR\"] and REGION[@image<=3050 and @image>=3040]]");
		assertTrue(xpath.match(doc));

		xpath = new XPath(
				"/OBJECT[GENERATOR[@name=\"BOOST_DETECTOR\"]]/REGION[@image=\"00003050\"]/RECTANGLE/COORDS");
		assertTrue(xpath.match(doc));

		xpath = new XPath(
				"/OBJECT[GENERATOR[@name=\"BOOST_DETECTOR\"]]/REGION[@image=\"00003050\"]/RECTANGLE/COORDS/@x");
		assertTrue(xpath.match(doc));

		xpath = new XPath("/*/CLASS/parent::*");
		assertTrue(xpath.match(doc));

		/*
		 * skeleton for further tests xpath = new XPath("");
		 * assertFalse(xpath.match(doc));
		 */

	}

}

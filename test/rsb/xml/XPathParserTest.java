package rsb.xml;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import rsb.xml.XPathNotSupportedException;

/**
 * 
 */

/**
 * @author rgaertne
 *
 */
public class XPathParserTest {

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
	 * Test method for {@link net.sf.xcf.xml.XPathParser#XPathParser(java.lang.String)}.
	 */
	@Ignore
	@Test
	public void XPathParser() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link net.sf.xcf.xml.XPathParser#toXPathExpression()}.
	 */
	@Test
	public void testToXPathExpression() {
		XPathParser parser = new XPathParser("//*", 1);
		String filter = "[@dbxml:id=\"1\"]";
		try {
		//parser.setDbXmlId(1);
		XPath xpath = parser.toXPathExpression();
		assertEquals("/*"+filter, xpath.getExpression());
		
		parser = new XPathParser("/", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/*"+filter,xpath.getExpression());
		
		parser = new XPathParser("/", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/*"+filter,xpath.getExpression());
		
		parser = new XPathParser("/*", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/*"+filter,xpath.getExpression());
		
		parser = new XPathParser("/element-name", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element-name"+filter,xpath.getExpression());
		
		parser = new XPathParser("/element-name", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element-name"+filter,xpath.getExpression());
		
		parser = new XPathParser(new XPath("/element[@attribute=\"test\"]"), 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element[@dbxml:id=\"1\" and @attribute=\"test\"]",xpath.getExpression());
		
		parser = new XPathParser("/element-name[@attribute=\"test\"]/one[@test]", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element-name[@dbxml:id=\"1\" and @attribute=\"test\"]/one[@test]",xpath.getExpression());
		
		parser = new XPathParser("element-name", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element-name"+filter,xpath.getExpression());
		
		parser = new XPathParser("element-name/*", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element-name"+filter+"/*",xpath.getExpression());
		
		parser = new XPathParser("element-name[@attribute=\"test\"]/one[@test]", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element-name[@dbxml:id=\"1\" and @attribute=\"test\"]/one[@test]",xpath.getExpression());
		
		parser = new XPathParser("element-name[@attribute=\"test\"]", 1);
		xpath = parser.toXPathExpression();
		assertEquals("/element-name[@dbxml:id=\"1\" and @attribute=\"test\"]",xpath.getExpression());
		} catch (XPathNotSupportedException e) {
			e.printStackTrace();
			assertTrue("xpath wrong", false);
		}
		// think about not yet supported xpaths
		try {
			
		} catch (Exception e) {
			assertTrue(true);
		}
	}

}

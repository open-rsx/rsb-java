/**
 * 
 */
package rsb.xml;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import nu.xom.XPathException;

import org.jaxen.JaxenException;
import org.jaxen.expr.Expr;
import org.jaxen.xom.XOMXPath;
import org.xml.sax.InputSource;

import rsb.transport.XOPData;


/**
 * At present, this class is deprecated, because it is broken by design.
 * It is just for compatibility purposes still available and implementing
 * the XPath interface, which is now used in all external XCF API calls.
 * Therefore, the correct class to use is the net.sf.xcf.xml.XPath class
 * that provides an easy interface for Jaxen/XOM-based XPath objects.
 * 
 * @author swrede
 *
 */
@Deprecated
public class XPathExpression implements javax.xml.xpath.XPath {
	
	private XPathContext ctx;
	private XOMXPath xp;
	
	public XPathExpression(String xpath) {
		ctx = new XPathContext();
		parseXPath(xpath);
	}	
	
	public XPathContext getCtx() {
		return ctx;
	}

	public void setCtx(XPathContext ctx) {
		this.ctx = ctx;
	}

	public String getExpression() {
		return xp.toString();
	}
	
	public void setExpression(String expression) {
		parseXPath(expression);
	}

	public XPathExpression(String xpath, XPathContext c) {
		parseXPath(xpath);
		ctx = c;
	}
	
	public boolean hasFilterOnRoot(String filter) {
		Expr expr = xp.getRootExpr();
		String expression = expr.getText();
		int rootEnd = expression.indexOf("/", 1);
		if (rootEnd == -1){
			rootEnd = expression.length();
		}
		String root = expression.substring(0, rootEnd-1);
		String filterContent = filter.substring(1, filter.length()-1);
		if (root.contains(filterContent)) {
			return true;
		}
		return false;
	}
	
	private void parseXPath(String xpath) {
		try {
			xp = new XOMXPath(xpath);
		} catch (JaxenException e) {
			throw new XPathException(e.getMessage());
		}
	}
	
	public boolean match(XOPData xop) {
		if (xop == null) {
			return false;
		}
		else {
			Document doc = xop.getDocument();
			return match(doc);
		}
	}

	public boolean match(Document document) {
		if (document == null) {
			return false;
		}
		Nodes result = document.query(this.getExpression(),this.getCtx());
		if (result.size() == 0) {
			return false;
		}
		return true;
	}

	public javax.xml.xpath.XPathExpression compile(String expression) throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String evaluate(String expression, Object item) throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String evaluate(String expression, InputSource source) throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object evaluate(String expression, Object item, QName returnType) throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object evaluate(String expression, InputSource source, QName returnType) throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamespaceContext getNamespaceContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public XPathFunctionResolver getXPathFunctionResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	public XPathVariableResolver getXPathVariableResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	public void reset() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented!");			
	}

	public void setNamespaceContext(NamespaceContext nsContext) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented!");		
	}

	public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented!");		
	}

	public void setXPathVariableResolver(XPathVariableResolver resolver) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented!");		
	}
}

/**
 * 
 */
package rsb.xml;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import nu.xom.Document;
import nu.xom.XPathException;

import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.expr.Expr;
import org.jaxen.xom.XOMXPath;
import org.xml.sax.InputSource;

import rsb.transport.XOPData;

/**
 * The XPath class encapuslates the construction and use of Jaxen/XOM-based
 * XPath objects with an easy-to-use API. Although we use this class also
 * internally, for instance in the event listener infrastructure, it shall not
 * be necessary for developers to use exactly this XPath implementation.
 * 
 * @author swrede
 */
public class XPath implements javax.xml.xpath.XPath {

	// private XPathContext ctx;
	private XOMXPath xp;
	private NamespaceContext namespaces;

	public XPath(String xpath) {
		// ctx = new XPathContext();
		parseXPath(xpath);
	}

	//
	// public XPathContext getCtx() {
	// return ctx;
	// }
	//
	// public void setCtx(XPathContext ctx) {
	// this.ctx = ctx;
	// }

	public XPath(String xpath, RSBNamespaceContext ctx) {
		// TODO Auto-generated constructor stub
		namespaces = ctx;
		parseXPath(xpath);
	}

	public String getExpression() {
		return xp.toString();
	}

	public void setExpression(String expression) {
		parseXPath(expression);
	}

	// public XPath(String xpath, XPathContext c) {
	// parseXPath(xpath);
	// }

	public boolean hasFilterOnRoot(String filter) {
		Expr expr = xp.getRootExpr();
		String expression = expr.getText();
		int rootEnd = expression.indexOf("/", 1);
		if (rootEnd == -1) {
			rootEnd = expression.length();
		}
		String root = expression.substring(0, rootEnd - 1);
		String filterContent = filter.substring(1, filter.length() - 1);
		if (root.contains(filterContent)) {
			return true;
		}
		return false;
	}

	private void parseXPath(String xpath) {
		try {
			xp = new XOMXPath(xpath);
			xp.setNamespaceContext(new org.jaxen.NamespaceContext() {
				public String translateNamespacePrefixToUri(String prefix) {
					// TODO move this to RSBNamespaceContext
					if (prefix == null) {
						throw new IllegalArgumentException(
								"No prefix provided!");
						// } else if
						// (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
						// return "http://code.cor-lab.org/rsb";
					} else if (prefix.equals("rsb")) {
						return "http://code.cor-lab.org/rsb";
					} else if (prefix.equals("xcf")) {
						return "http://xcf.sf.net";
					} else if (namespaces != null) {
						return namespaces.translateNamespacePrefixToUri(prefix);
					} else
						return XMLConstants.NULL_NS_URI;
				}
			});
		} catch (JaxenException e) {
			throw new XPathException(e.getMessage());
		}
	}

	public boolean match(XOPData xop) {
		if (xop == null) {
			return false;
		} else {
			Document doc = xop.getDocument();
			return match(doc);
		}
	}

	public boolean match(Document document) {
		if (document == null) {
			return false;
		}

		try {
			List<?> result = xp.selectNodes(document);

			// Nodes result =
			// document.query(this.getExpression(),this.getCtx());
			if (result.size() == 0) {
				return false;
			}
			return true;

		} catch (JaxenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public javax.xml.xpath.XPathExpression compile(String expression)
			throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String evaluate(String expression, Object item)
			throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String evaluate(String expression, InputSource source)
			throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object evaluate(String expression, Object item, QName returnType)
			throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object evaluate(String expression, InputSource source,
			QName returnType) throws XPathExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.xml.namespace.NamespaceContext getNamespaceContext() {
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
		namespaces = nsContext;
	}

	public void setXPathFunctionResolver(XPathFunctionResolver resolver) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented!");
	}

	public void setXPathVariableResolver(XPathVariableResolver resolver) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented!");
	}

	@Override
	public void setNamespaceContext(
			javax.xml.namespace.NamespaceContext nsContext) {
		// TODO Auto-generated method stub

	}
}

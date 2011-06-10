/**
 * 
 */
package rsb.xml;

/**
 * @author rgaertne
 */
public class XPathParser {

	// XPathParser sets dbxmil:id attribute to an XPath
	XPath xp;
	int id;

	public XPathParser(String xpath, int id) {
		xp = new XPath(xpath);
		this.id = id;
	}

	public XPathParser(XPath xpath, int id) {
		this.xp = xpath;
		this.id = id;
	}

	public XPathParser(XPath xpath) {
		this.xp = xpath;
	}

	public void setDbXmlId(int id) {
		this.id = id;
	}

	public XPath toXPathExpression() throws XPathNotSupportedException {
		return toXPathExpression("[@dbxml:id=\"" + id + "\"]");
	}

	// this is done for further development, we "just" need to verify that the
	// filter is valid
	private XPath toXPathExpression(String filter)
			throws XPathNotSupportedException {
		String path = xp.getExpression();
		String dbxmlIdFilter = filter;
		StringBuffer xpath = new StringBuffer(path);
		if (xpath.charAt(0) == '/' && xpath.length() == 1) {
			xpath.append("*" + dbxmlIdFilter);
		} else if (xpath.charAt(0) == '/' && hasFilterOnRoot(path) == false) {
			int rootEnd = xpath.indexOf("/", 1);
			switch (rootEnd) {
			case -1:
				rootEnd = xpath.length();
				if (xpath.charAt(1) == '*') {
					rootEnd = 2;
				}
				xpath.insert(rootEnd, dbxmlIdFilter);
				break;
			case 1:
				if (rootEnd == xpath.length() - 1) {
					throw new XPathNotSupportedException("XPath " + path
							+ " currently not supported");
					// xpath.insert(1,dbxmlIdFilter);
					// xpath.insert(xpath.length(),"*");
				} else
					xpath.insert(xpath.length(), dbxmlIdFilter);
				xpath.deleteCharAt(0);
				break;
			default:
				xpath.insert(rootEnd, dbxmlIdFilter);
				break;

			}
		} else if (xpath.charAt(0) == '/' && hasFilterOnRoot(path) == true) {
			int filterStart = xpath.indexOf("[", 1);
			xpath.insert(filterStart + 1, "@dbxml:id=\"" + id + "\" and ");
		} else if (xpath.charAt(0) != '/' && hasFilterOnRoot(path) == false) {
			int rootEnd = xpath.indexOf("/", 1);
			switch (rootEnd) {
			case -1:
				rootEnd = xpath.length();
			default:
				xpath.insert(rootEnd, dbxmlIdFilter);
				// sets root tag
				xpath.insert(0, "/");
				break;
			}
		} else if (xpath.charAt(0) != '/' && hasFilterOnRoot(path) == true) {
			int filterStart = xpath.indexOf("[", 1);
			xpath.insert(filterStart + 1, "@dbxml:id=\"" + id + "\" and ");
			xpath.insert(0, "/");
		} else {
			throw new XPathNotSupportedException("XPath " + path
					+ " currently not supported");
		}

		return new XPath(xpath.toString(), new RSBNamespaceContext("dbxml",
				"http://www.sleepycat.com/2002/dbxml"));
	}

	private boolean hasFilterOnRoot(String xpath) {
		int rootEnd = xpath.indexOf("/", 1);
		if (rootEnd == -1) {
			rootEnd = xpath.length();
		}
		String root = xpath.substring(0, rootEnd - 1);
		if (root.contains("[")) {
			return true;
		}
		return false;

	}

}

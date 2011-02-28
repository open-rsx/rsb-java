/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

import rsb.RSBEvent;
import rsb.transport.XOPData;
import rsb.xml.SyntaxException;
import rsb.xml.XPath;

/**
 *
 * @author swrede
 */
public class XPathFilter extends AbstractFilter {
    
	XPath xpath;
	
    public XPathFilter (XPath xpath) {
        super("XPathFilter");
        this.xpath = xpath; 
    }
    
    @Override
    public RSBEvent transform(RSBEvent e) {
    	// TODO introduce XML Type to make this faster and more robust
    	XOPData xop = null;
    	try {
			 xop = XOPData.fromString((String) e.getData());
		} catch (SyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		if (xpath.match(xop)) {
			return e;
		}
		return null;
    }
	

	@Override
	public boolean equals(Filter that) {
		return that instanceof XPathFilter
		&& xpath.equals(((XPathFilter) that).xpath);
	}
	
	/*
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
	}		

}

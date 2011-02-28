/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

import rsb.RSBEvent;
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
    	throw new RuntimeException("Not implemented yet!");
//    	public boolean match(XOPData xop) {
//    		if (xop == null) {
//    			return false;
//    		}
//    		else {
//    			Document doc = xop.getDocument();
//    			return match(doc);
//    		}
//    	}
//
//    	public boolean match(Document document) {
//    		if (document == null) {
//    			return false;
//    		}
//    		Nodes result = document.query(this.getExpression(),this.getCtx());
//    		if (result.size() == 0) {
//    			return false;
//    		}
//    		return true;
//    	}    	
//        return e;
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

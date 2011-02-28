/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

/**
 *
 * @author swrede
 */
public interface FilterObserver {
	
	// TODO change this to dynamic dispatch for extensibilty
	//      Example: XPathFilter

	public void notify(AbstractFilter e, FilterAction a);
    public void notify(ScopeFilter e, FilterAction a);
    public void notify(TypeFilter e, FilterAction a);
    public void notify(IdentityFilter e, FilterAction a);
}

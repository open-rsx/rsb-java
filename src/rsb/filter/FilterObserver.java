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
	
	public String getInfo();
    public void filterAdded(ScopeFilter e);
    public void filterAdded(TypeFilter e);
    public void filterAdded(IdentityFilter e);
    public void filterRemoved(ScopeFilter e);
    public void filterRemoved(TypeFilter e);
    public void filterRemoved(IdentityFilter e);
}

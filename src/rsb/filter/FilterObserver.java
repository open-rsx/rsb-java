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

	public void notify(AbstractFilter filter, FilterAction action);
    public void notify(ScopeFilter filter, FilterAction action);
    public void notify(TypeFilter filter, FilterAction action);
    public void notify(OriginFilter filter, FilterAction action);
}

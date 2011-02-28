/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rsb.transport;

import rsb.RSBEvent;
import rsb.filter.AbstractFilterObserver;

/**
 *
 * @author swrede
 */
public abstract class AbstractPort extends AbstractFilterObserver implements Port {

	// TODO refactor this to observer pattern, remove firect coupling
	protected Router r;
	
    public void push(RSBEvent e) {
    }
    
    public void setRouter(Router r) {
    	this.r = r;
    }
    
    public Router getRouter() {
    	return r;
    }    

}

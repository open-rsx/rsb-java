/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.transport.spread;

import rsb.transport.TransportFactory;

/**
 *
 * @author swrede
 */
public class SpreadFactory extends TransportFactory {

	private SpreadWrapper sw = null;
	
	SpreadWrapper getSpreadWrapper() {
		if (sw == null) {
			synchronized(SpreadFactory.class) {
				sw = new SpreadWrapper();
			}
		}
		return sw;
	}
	
	@Override
	public Port createPort() {
		// TODO check port multiplicity 
		return new Port(new SpreadWrapper());
		//return new Port(this.getSpreadWrapper());
	}
	
}

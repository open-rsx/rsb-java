/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.transport.spread;

import rsb.transport.Port;
import rsb.transport.TransportFactory;
import rsb.transport.convert.StringConverter;

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
		SpreadPort sp = new SpreadPort(new SpreadWrapper());
		sp.addConverter("string", new StringConverter());
		return sp;
	}
	
}

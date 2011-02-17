/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.transport;

import rsb.transport.spread.SpreadFactory;


/**
 *
 * @author swrede
 */
public abstract class TransportFactory {

    public static TransportFactory getInstance() {
        return new SpreadFactory();
    };

    public Port createPort() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

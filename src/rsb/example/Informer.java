/**
 * 
 */
package rsb.example;

import rsb.InitializeException;
import rsb.Publisher;
import rsb.RSBEvent;
import rsb.RSBException;
import rsb.transport.TransportFactory;

/**
 * @author swrede
 *
 */
public class Informer {

	/**
	 * @param args
	 * @throws InitializeException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InitializeException, InterruptedException {
		TransportFactory tf = TransportFactory.getInstance();
		Publisher p = new Publisher("rsb://example/informer", tf);
		p.activate();
		RSBEvent e = new RSBEvent();
		for (int i = 0; i < 100; i++) {
			e.generateID();
			e.setData("<message uuid=\""+e.getUuid()+"\" val=\"Hello World!\" nr="+i+"\"/>");			
			p.send(e);
			Thread.sleep(1);
		}
		p.deactivate();
	}

}

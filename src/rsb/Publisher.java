package rsb;

import java.util.UUID;
import java.util.logging.Logger;

import rsb.transport.Router;
import rsb.transport.TransportFactory;
import rsb.util.Timestamp;


/**
 * This class offers a method to send messages to any registered Subscribers.
 * 1:m-communication (called Publish-Subscribe) is one of the basic
 * communication patterns offered by RSB. Each Publisher instance must be given
 * a unique name which is used by Subscriber instances to connect to the
 * Publisher. The Publisher also offers a method to retrieve all Subscribers
 * currently registered at the instance.
 * 
 * @author swrede
 * @author rgaertne
 * @author jschaefe
 *
 */
public class Publisher implements RSBObject {
	
	private final static Logger log = Logger.getLogger(Publisher.class.getName()); 

	/* publisher's name within it's scope */
	protected String name;
	
	/* receiver scope URI */
//	protected XcfUri receiverUri;
	
//	PublisherInterceptDistributor interceptManager;
	
	protected PublisherState state;

	/* transport factory object */
//	protected TransportFactory transportFactory;
	
	/* transport router */
	protected Router router;

	protected class PublisherStateInactive extends PublisherState {

		protected PublisherStateInactive(Publisher ctx) {
			super(ctx);
		}
		
		protected void activate() throws InitializeException {
			router.activate();
			p.state = new PublisherStateActive(p);
		}
		
	}
	
	protected class PublisherStateActive extends PublisherState {

		protected PublisherStateActive(Publisher ctx) {
			super(ctx);
		}
		
		protected void deactivate() {
			router.deactivate();
			p.state = new PublisherStateInactive(p);
		}

		protected RSBEvent send(RSBEvent e) {		
			e.setUri(name);
			e.ensureID();
			// TODO move event creation into factory?
		//	e.getMetadata().setSenderUri(uri);
		//	e.getMetadata().setReceiverUri(receiverUri);
			Timestamp now = new Timestamp();
			//e.getMetadata().setCreatedAt(now);
			//e.getMetadata().setUpdatedAt(now);
			router.publishSync(e);
			return e;
		}
		
	}
	
	public Publisher(String name, TransportFactory tfac) {
		state = new PublisherStateInactive(this);
		this.name = name;
//		manager = m;
//		uri = new XcfUri(Configurator.getInstance().getInterfaceLocalUri(name));
//		this.transportFactory = tfac;
		router = new Router(tfac);
		// find the root scope
		// TO DO this has to be set in a better way from configuration
//		XcfUri current = uri.getScope();
//		while(current.getParentScope() != null) {
//			current = current.getParentScope();
//		}
//		receiverUri = current;
		// TODO what about multiple targets?
		//receiverUri = new XcfUri(Configurator.getInstance().getFirstInterfaceRemoteUri(name));
		log.fine("New publisher instance with URI " + name + " created"); //+ uri.getUri());
	}

	/**
	 * Returns name of the publisher
	 * @return name of Publisher as a String
	 */
	public String getName() {
		return name;
	}
	
	public synchronized void activate() throws InitializeException {
		state.activate();
	}
	
	public synchronized void deactivate() {
		state.deactivate();
	}
	
	/**
	 * Send a message to all subscribed participants. 
	 */
	public synchronized void send(RSBEvent e) {
		state.send(e);
	}

}
	


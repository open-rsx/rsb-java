package rsb;

import java.util.logging.Logger;


public abstract class PublisherState<T> {

	private final static Logger log = Logger.getLogger(PublisherState.class.getName());
	
	protected Publisher<T> p; 
	
	protected PublisherState (Publisher<T> ctx) {
		p = ctx;
	}
	
	protected void activate() throws InitializeException {
		log.warning("invalid state exception during activate call");
		throw new InvalidStateException("publisher not activated");
	}
	
	protected void deactivate() {
		log.warning("invalid state exception during deactivate call");
		throw new InvalidStateException("publisher not activated");
	}
	
	protected RSBEvent send(RSBEvent e) {
		log.warning("invalid state exception during call to send");
		throw new InvalidStateException("publisher not activated");
	}
	
	protected RSBEvent send(T d) {
		log.warning("invalid state exception during call to send");
		throw new InvalidStateException("publisher not activated");
	}	
	
}

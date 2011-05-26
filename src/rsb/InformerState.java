package rsb;

import java.util.logging.Logger;


public abstract class InformerState<T> {

	private final static Logger log = Logger.getLogger(InformerState.class.getName());

	protected Informer<T> p;

	protected InformerState (Informer<T> ctx) {
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

	protected Event send(Event e) {
		log.warning("invalid state exception during call to send");
		throw new InvalidStateException("publisher not activated");
	}

	protected Event send(T d) {
		log.warning("invalid state exception during call to send");
		throw new InvalidStateException("publisher not activated");
	}

}

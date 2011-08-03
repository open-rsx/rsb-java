package rsb;

import java.util.logging.Logger;

public abstract class InformerState<T> {

	private final static Logger LOG = Logger.getLogger(InformerState.class
			.getName());

	protected Informer<T> ctx;

	protected InformerState(Informer<T> ctx) {
		this.ctx = ctx;
	}

	protected void activate() throws InitializeException {
		LOG.warning("invalid state exception during activate call");
		throw new InvalidStateException("informer not activated");
	}

	protected void deactivate() {
		LOG.warning("invalid state exception during deactivate call");
		throw new InvalidStateException("informer not activated");
	}

	protected Event send(Event e) throws RSBException {
		LOG.warning("invalid state exception during call to send");
		throw new InvalidStateException("informer not activated");
	}

	protected Event send(T d) throws RSBException {
		LOG.warning("invalid state exception during call to send");
		throw new InvalidStateException("informer not activated");
	}

}

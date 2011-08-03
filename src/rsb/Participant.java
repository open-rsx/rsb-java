package rsb;

import rsb.transport.PortConfiguration;
import rsb.transport.Router;
import rsb.transport.TransportFactory;

/**
 * Base class for all bus participants with an associated scope. 
 * Mainly holds references to the router and configuration-level
 * objects.
 * 
 * @author jwienke
 * @author swrede
 * 
 */
public abstract class Participant implements RSBObject {

	private ParticipantId id = new ParticipantId();
	private Scope scope;
	private TransportFactory transportFactory;
	private Router router;

	/**
	 * Creates a new participant on the specified scope.
	 * 
	 * @param scope
	 *            scope of the participant
	 * @param transportFactory
	 *            the factory used for transports for this {@link Participant}
	 * @param portConfig
	 *            type of ports to create for this participant
	 */
	protected Participant(Scope scope, TransportFactory transportFactory,
			PortConfiguration portConfig) {
		if (scope == null) {
			throw new IllegalArgumentException(
					"Scope of a participant must not be null.");
		}
		if (transportFactory == null) {
			throw new IllegalArgumentException(
					"TransportFactory of a participant must not be null.");
		}
		this.scope = scope;
		this.transportFactory = transportFactory;
		router = new Router(this.transportFactory, portConfig);
	}

	/**
	 * Returns the unique ID of this participant.
	 * 
	 * @return the unique id of the participant
	 */
	public ParticipantId getId() {
		return id;
	}

	/**
	 * Returns the scope of this participant.
	 * 
	 * @return scope of the participant, not <code>null</code>
	 */
	public Scope getScope() {
		return scope;
	}

	/**
	 * Returns the {@link TransportFactory} used for this participant.
	 * 
	 * @return instance not <code>null</code>
	 */
	protected TransportFactory getTransportFactory() {
		return transportFactory;
	}

	/**
	 * Returns the router used for this participant.
	 * 
	 * @return router used for this participant, not <code>null</code>
	 */
	protected Router getRouter() {
		return router;
	}

}

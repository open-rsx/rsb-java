package rsb.eventprocessing;

import rsb.Participant;
import rsb.Activatable;
import rsb.transport.Connector;

/**
 * Abstract interface for classes setting up event receiving or sending routes
 * from {@link Participant} instances to the concrete transport implementations.
 *
 * @author jwienke
 *
 * @param <ConnectorType>
 *            The type of {@link Connector} instances used by implementing
 *            classes
 */
public interface RouteConfigurator<ConnectorType extends Connector> extends
        Activatable {

    /**
     * Adds a connector which will subsequently be used for for sending events.
     *
     * This method must only be called before the object is being activated
     * using {@link #activate()}.
     *
     * @param connector
     *            connector to add
     */
    void addConnector(ConnectorType connector);

    /**
     * Removes a connector, which will not receive events for sending
     * afterwards.
     *
     * This method must only be called before the object is being activated
     * using {@link #activate()}.
     *
     * @param connector
     *            the connector to remove
     * @return <code>true</code> if the connector was previously installed and
     *         hence remove now, else <code>false</code>
     */
    boolean removeConnector(ConnectorType connector);

}

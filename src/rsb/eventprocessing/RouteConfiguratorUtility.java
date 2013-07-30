package rsb.eventprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.Scope;
import rsb.transport.Connector;

/**
 * A utility class that can be used via composition to implement some of the
 * methods required by the {@link RouteConfigurator} interface.
 *
 * @author jwienke
 *
 * @param <ConnectorType>
 *            type of connectors
 */
public class RouteConfiguratorUtility<ConnectorType extends Connector> {

    private static final Logger LOG = Logger
            .getLogger(RouteConfiguratorUtility.class.getName());

    private final List<ConnectorType> connectors = new ArrayList<ConnectorType>();
    private final Scope scope;
    private boolean active = false;

    public RouteConfiguratorUtility(final Scope scope) {
        this.scope = scope;
    }

    public void activate() throws RSBException {

        synchronized (this) {

            assert !this.active;
            if (this.active) {
                LOG.warning("Will not activate because I was already active");
                return;
            }

            for (final ConnectorType connector : this.connectors) {
                LOG.finer("Activating connector " + connector);
                connector.setScope(this.scope);
                connector.activate();
            }

            this.active = true;

        }

    }

    public void deactivate() throws RSBException, InterruptedException {

        synchronized (this) {

            assert this.active;
            if (!this.active) {
                LOG.warning("Will not deactivate because I was not active");
                return;
            }

            for (final ConnectorType connector : this.connectors) {
                LOG.finer("Deactivating connector " + connector);
                connector.deactivate();
            }

            this.active = false;

        }

    }

    public boolean isActive() {
        return this.active;
    }

    public void addConnector(final ConnectorType connector) {
        this.connectors.add(connector);
    }

    public boolean removeConnector(final ConnectorType connector) {
        return this.connectors.remove(connector);
    }

    public List<ConnectorType> getConnectors() {
        return this.connectors;
    }

}

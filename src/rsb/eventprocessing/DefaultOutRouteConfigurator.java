package rsb.eventprocessing;

import java.util.logging.Logger;

import rsb.Event;
import rsb.RSBException;
import rsb.Scope;
import rsb.transport.OutConnector;

/**
 * Class implementing the basic out route configuration based on a push
 * strategy.
 *
 * @author jwienke
 */
public class DefaultOutRouteConfigurator implements OutRouteConfigurator {

    private static final Logger LOG = Logger
            .getLogger(DefaultOutRouteConfigurator.class.getName());

    private final RouteConfiguratorUtility<OutConnector> utility;

    public DefaultOutRouteConfigurator(final Scope scope) {
        this.utility = new RouteConfiguratorUtility<OutConnector>(scope);
    }

    @Override
    public void activate() throws RSBException {
        this.utility.activate();
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        this.utility.deactivate();
    }

    @Override
    public boolean isActive() {
        return this.utility.isActive();
    }

    @Override
    public void addConnector(final OutConnector connector) {
        this.utility.addConnector(connector);
    }

    @Override
    public boolean removeConnector(final OutConnector connector) {
        return this.utility.removeConnector(connector);
    }

    @Override
    public void publishSync(final Event event) throws RSBException {
        for (final OutConnector connector : this.utility.getConnectors()) {
            LOG.finer("Pushing event to connector " + connector);
            connector.push(event);
        }
    }

    // TODO method for setting quality of service specs on the connectors

}

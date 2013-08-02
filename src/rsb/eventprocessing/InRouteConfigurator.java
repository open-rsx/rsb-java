package rsb.eventprocessing;

import rsb.filter.Filter;
import rsb.transport.InConnector;

/**
 * Implementing classes provide outgoing communication routes for
 * {@link rsb.Participant}s.
 *
 * @author jwienke
 * @param <ConnectorType>
 *            The type of {@link rsb.transport.Connector} instances used by the
 *            implementing classes
 */
public interface InRouteConfigurator<ConnectorType extends InConnector> extends
        RouteConfigurator<ConnectorType> {

    /**
     * Called in case a new filter was added and should be reflected by this
     * route.
     *
     * @param filter
     *            the added filter
     */
    void filterAdded(Filter filter);

    /**
     * Called in a case a filter should be removed from the route.
     *
     * @param filter
     *            the filter to remove
     * @return <code>true</code> if the filter was already active and was
     *         removed now, else <code>false</code>
     */
    boolean filterRemoved(Filter filter);

}

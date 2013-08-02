package rsb.transport;

import rsb.filter.FilterObserver;

/**
 * Implements receiving {@link rsb.Event}s from a specific transport.
 *
 * @author jwienke
 */
public interface InConnector extends Connector, FilterObserver {
    // the composition of interfaces is the important aspect here
}

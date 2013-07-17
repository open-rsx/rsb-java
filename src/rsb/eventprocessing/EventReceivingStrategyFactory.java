package rsb.eventprocessing;

import rsb.InitializeException;

/**
 * An abstract factory for creating {@link EventReceivingStrategy} instances.
 *
 * @author jwienke
 */
public interface EventReceivingStrategyFactory {

    /**
     * Create a new instance.
     *
     * @return the instance
     * @throws InitializeException
     *             error initializing the strategy
     */
    EventReceivingStrategy create() throws InitializeException;

}

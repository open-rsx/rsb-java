package rsb.eventprocessing;

import rsb.InitializeException;

/**
 * An {@link EventReceivingStrategyFactory} for
 * {@link UnorderedParallelEventReceivingStrategy} instances.
 *
 * @author jwienke
 */
public class UnorderedParallelFactory implements EventReceivingStrategyFactory {

    @Override
    public EventReceivingStrategy create() throws InitializeException {
        return new UnorderedParallelEventReceivingStrategy();
    }

}

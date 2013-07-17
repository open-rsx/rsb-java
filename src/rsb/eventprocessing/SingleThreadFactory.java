package rsb.eventprocessing;

import rsb.InitializeException;

/**
 * An {@link EventReceivingStrategyFactory} for
 * {@link SingleThreadEventReceivingStrategy} instances.
 *
 * @author jwienke
 */
public class SingleThreadFactory implements EventReceivingStrategyFactory {

    @Override
    public EventReceivingStrategy create() throws InitializeException {
        return new SingleThreadEventReceivingStrategy();
    }

}

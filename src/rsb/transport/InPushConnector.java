package rsb.transport;

/**
 * Interface for receiving connectors which asynchronously notify handlers about
 * newly received events.
 * 
 * @author jwienke
 */
public interface InPushConnector extends InConnector {

    /**
     * Adds a handler which will be notified about newly received events.
     * 
     * @param handler
     *            the handler to notify
     */
    void addHandler(EventHandler handler);

    /**
     * Removes a registered handler so that it won't be notified anymore.
     * 
     * @param handler
     *            the handler to remove
     * @return <code>true</code> if the given handler was removed, else
     *         <code>false</code>, i.e. in case that handler was never
     *         registered.
     */
    boolean removeHandler(EventHandler handler);

}

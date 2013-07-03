package rsb.patterns;

import rsb.Event;

/**
 * A {@link Callback} implements the functionality of a single method of a
 * {@link LocalServer}. Implementations of such classes have to be provided by
 * RSB client programs.
 * 
 * Clients usually should not implement this interface directly. Instead use one
 * of the specialized implementing classes like {@link DataCallback} or
 * {@link EventCallback}.
 * 
 * @author jwienke
 */
public interface Callback {

    /**
     * Method to invoke the {@link Callback}'s functionality. This method can be
     * considered an internal implementation detail.
     * 
     * @param request
     *            the request received from a {@link RemoteServer} method call
     * @return an event with the result data of a method
     * @throws Throwable
     *             any exception type may be thrown
     */
    Event internalInvoke(Event request) throws Throwable;

}

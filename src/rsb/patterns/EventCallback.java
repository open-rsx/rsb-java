package rsb.patterns;

import java.lang.Throwable;

import rsb.Event;

/**
 * Implementations of this interface are used to provide the behavior
 * of exposed methods.
 *
 * @author jmoringe
 */
interface EventCallback {

    /**
     * This method is called to invoke the actual behavior of an
     * exposed method.
     *
     * @param request The argument passed to the associated method by
     * the remote caller.
     * @return A result that should be returned to the remote caller
     * as the result of the calling the method.
     * @throw Throwable Can throw anything.
     */
    public Event invoke(Event request) throws Throwable;
};
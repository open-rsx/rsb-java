package rsb.patterns;

/**
 * Objects of this class implement and make available methods of a
 * local server.
 *
 * The actual behavior of methods is implemented by invoking arbitrary
 * user-supplied functions.
 *
 * @author jmoringe
 */
class LocalMethod extends Method {
    /**
     * Create a new LocalMethod object that is exposed under the
     * name @a name by @a server.
     *
     * @param server
     *            The local server object to which the method will be
     *            associated.
     * @param name
     *            The name of the method.
     */
    public LocalMethod(Server server, String name) {
	super(server, name);
    }
};
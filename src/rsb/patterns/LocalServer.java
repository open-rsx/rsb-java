package rsb.patterns;

/**
 * TODO(jmoringe): document
 *
 * Created:
 *
 * @author Jan Moringen
 * @version
 * @since
 */
public class LocalServer extends Server {

    /**
     * Create a new LocalServer object that provides its methods under
     * the scope @a scope.
     *
     * @param scope
     *            The common super-scope under which the methods of
     *            the newly created server should be provided.
     * @param transportFactory
     *            TODO
     * @param portConfig
     *            TODO
     */
    public LocalServer(Scope	         scope,
		       TransportFactory  transportFactory,
		       PortConfiguration portConfig) {
	super(scope, transportFactory, portConfig);
    }

};
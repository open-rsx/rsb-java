package rsb.patterns;

import rsb.RSBObject;
import rsb.Listener;
import rsb.Informer;

/**
 * Objects of this class are methods which are associated to a local
 * or remote server. Within a server, each method has a unique name.
 *
 * This class is primarily intended as a superclass for local and
 * remote method classes.
 *
 * @author jmoringe
 */
public abstract class Method implements RSBObject {

    private Server   server;
    private String   name;
    private Informer informer;
    private Listener listener;

    public Method(Server server, String name) {
	this.server = server;
	this.name   = name;
    }

    /**
     * Return the Server object to which this method is associated.
     *
     * @return The Server object.
     */
    public Server getServer() {
	return server;
    }

    /**
     * Return the name of this method.
     *
     * @return The name of this method.
     */
    public String getName() {
	return name;
    }

    /**
     * Return the Informer object associated to this method.
     *
     * The Informer object may be created lazily.
     *
     * @return The Informer object.
     */
    public Informer getInformer() {
	if (informer == null) {
	    //informer = makeInformer();
	}
	return informer;
    }

    /**
     * Return the Listener object associated to this method.
     *
     * The Listener object may be created lazily.
     *
     * @return The Listener object.
     */
    public Listener getListener() {
	if (listener == null) {
	    //listener = makeListener();
	}
	return listener;
    }

    @Override
    public void deactivate() {
	// Deactivate informer and listener if necessary.
	if (informer != null) {
	    informer.deactivate();
	    informer = null;
	}
	if (listener != null) {
	    listener.deactivate();
	    listener = null;
	}
    }

    @Override
    public String toString() {
	return "Method[name=" + getName() + "]";
    }

};
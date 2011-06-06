package rsb;

/**
 * A factory for RSB client-level objects. This class is a Singleton.
 * 
 * @author jwienke
 */
public class Factory {

	/**
	 * The singleton instance.
	 */
	private static Factory instance = new Factory();

	/**
	 * Private constructor to ensure Singleton.
	 */
	private Factory() {
	}

	/**
	 * Returns the one and only instance of this class.
	 * 
	 * @return singleton factory instance
	 */
	public static Factory getInstance() {
		return instance;
	}

	/**
	 * Creates a new informer instance.
	 * 
	 * @param <T>
	 *            type of the data sent by this informer
	 * @param scope
	 *            scope of the informer
	 * @param type
	 *            type identifier of the informer
	 * @return new informer instance
	 */
	public <T> Informer<T> createInformer(Scope scope, String type) {
		return new Informer<T>(scope, type);
	}

	/**
	 * Creates a new informer instance.
	 * 
	 * @param <T>
	 *            type of the data sent by this informer
	 * @param scope
	 *            scope of the informer
	 * @return new informer instance
	 */
	public <T> Informer<T> createInformer(Scope scope) {
		return new Informer<T>(scope);
	}

	/**
	 * Creates a new listener instance.
	 * 
	 * @param scope
	 *            scope of the listener
	 * @return new listener
	 */
	public Listener createListener(Scope scope) {
		return new Listener(scope);
	}

}

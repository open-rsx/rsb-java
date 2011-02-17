/**
 * 
 */
package rsb;

/**
 * @author swrede
 *
 */
public interface RSBObject {
	
	/*
	 * Activates all network resources that belong to
	 * a specific object.
	 */
	public void activate() throws RSBException;
	
	/*
	 * Deactivate all network resources that are owned
	 * by a specific object in order to reactivate it.
	 */
	public void deactivate() throws RSBException;
}

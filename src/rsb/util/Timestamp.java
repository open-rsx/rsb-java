/**
 * 
 */
package rsb.util;

/**
 * @author swrede
 *
 */
public class Timestamp {
	// TODO improve Timestamp class, think about Timeval
	
	public long stamp = System.currentTimeMillis();
	
	public Timestamp() {
		// do nothing
	}
	
	public Timestamp(long s) {
		this.stamp = s;
	}

	public static Timestamp fromString(String s) {
		try {
			long l = Long.parseLong(s);
			return new Timestamp(l);
		} catch (Exception e) {
			// TODO log warning or error here
			e.printStackTrace();
			return null;
		}
	}
	
}

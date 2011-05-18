/**
 * 
 */
package rsb.transport.spread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import rsb.InitializeException;

/**
 * @author swrede
 * 
 */
public class SpreadWrapperTest {

	/**
	 * Test method for
	 * {@link rsb.transport.spread.SpreadWrapper#SpreadWrapper()}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testSpreadWrapper() throws InitializeException {
		SpreadWrapper spread = new SpreadWrapper();
		assertNotNull(spread);
		assertEquals(SpreadWrapper.State.DEACTIVATED, spread.getStatus());
		spread.activate();
		assertEquals(SpreadWrapper.State.ACTIVATED, spread.getStatus());
	}

}

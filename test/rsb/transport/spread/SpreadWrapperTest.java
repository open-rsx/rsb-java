/**
 * 
 */
package rsb.transport.spread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import rsb.InitializeException;

/**
 * @author swrede
 *
 */
public class SpreadWrapperTest {
	
	/**
	 * Test method for {@link rsb.transport.spread.SpreadWrapper#SpreadWrapper()}.
	 */
	@Test
	public void testSpreadWrapper() {
		SpreadWrapper spread = new SpreadWrapper();
		assertNotNull(spread);		
		assertEquals(SpreadWrapper.State.DEACTIVATED,spread.getStatus());		
		System.out.println(spread.getSpreadhost().toString());
		try {
			spread.activate();
		} catch (InitializeException e) {			
			e.printStackTrace();
			fail("Spread Activation failed");
		}
		assertEquals(SpreadWrapper.State.ACTIVATED,spread.getStatus());
	}

}

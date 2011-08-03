package rsb;

import org.junit.Test;

public class EventIdTest {

//	@Test
//	public void testHashCode() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testId() {
		// TODO implement id test
	}

//	@Test
//	public void testEqualsObject() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetAsUUID() {
//		fail("Not yet implemented");
//	}
	
//	@Test
//	public void testUnsignedInteger() {
//		int i = 0;
//		// ...
//		long l = i & 0xffffffffL;
//		assertEquals(l, 0);
//		i = Integer.MAX_VALUE;
//		System.out.println(i);
//		l = (i+1) & 0xffffffffL;
//		long exp = (long) Integer.MAX_VALUE+1;
//		System.out.println(exp);
//		assertEquals(l, exp);
//		// overrun
//		i = i+5;
//		System.out.println("i++:" + i);		
//		l = i & 0xffffffffL;
//		System.out.println(l);
//		assertEquals(l, 1);
//		// otherwise we cannot deal with larger event sequence id's
//		// however we are not allowed to send larger than 32bit ints to wire
//		// from the pbuf doc:
//		// [1] In Java, unsigned 32-bit and 64-bit integers are represented 
//		// using their signed counterparts, with the top bit simply being 
//		// stored in the sign bit.
//		// TODO
//		// store uint32 internally in Integer --> may result in negative val
//		// update internal integer by incrementing --> everything stays in valid uint32 range
//		// in getSequenceNumber --> return Long to report "positive" sequence numbers
//	}	

}

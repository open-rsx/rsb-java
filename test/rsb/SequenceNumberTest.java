package rsb;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.transport.SequenceNumber;

public class SequenceNumberTest {

	@Test
	public void testOverflowUsage() {        
		// check max value
		assertEquals(((long) 2*Integer.MAX_VALUE+1),Long.parseLong("4294967295"));
		assertEquals(SequenceNumber.MAX_VALUE, Long.parseLong("4294967295"));
		
		// test int overflow in use
		SequenceNumber i = new SequenceNumber(Integer.MAX_VALUE);
		assertEquals(i.get(), Integer.MAX_VALUE);
		i.incrementAndGet();
		assertEquals(i.get(), (long) Integer.MAX_VALUE+1);		
		
		// test uint32 overflow in use
		i = new SequenceNumber(SequenceNumber.MAX_VALUE);
		assertEquals(i.get(), Long.parseLong("4294967295"));
		i.incrementAndGet();
		assertEquals(0,i.get());
	}

	@Test
	public void testOverflowInit() {
		// integer overflow in constructor
		SequenceNumber i = new SequenceNumber(Integer.MAX_VALUE+1);
		assertEquals(Integer.MAX_VALUE+1, Integer.MIN_VALUE);
		assertEquals(i.get(), (long) Integer.MAX_VALUE+1);
		i.incrementAndGet();
	}	
	
}

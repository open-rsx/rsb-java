package rsb;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link MetaData}.
 * 
 * @author jwienke
 */
public class MetaDataTest {

	@Test
	public void testConstruction() {

		MetaData meta = new MetaData();
		assertTrue(meta.getCreateTime() >= System.nanoTime() / 1000 - 100000);
		assertTrue(meta.getCreateTime() <= System.nanoTime() / 1000);
		assertEquals(0, meta.getReceiveTime());
		assertEquals(0, meta.getDeliverTime());
		assertEquals(0, meta.getSendTime());
		assertTrue(meta.userTimeKeys().isEmpty());

	}

	@Test
	public void testSenderId() {
		Id id = new Id();
		MetaData meta = new MetaData();
		meta.setSenderId(id);
		assertEquals(id, meta.getSenderId());
	}

	@Test
	public void testDefaultTimesAuto() {

		MetaData meta = new MetaData();
		meta.setCreateTime(0);
		meta.setSendTime(0);
		meta.setReceiveTime(0);
		meta.setDeliverTime(0);

		assertTrue(meta.getCreateTime() > System.nanoTime() / 1000 - 100000);
		assertTrue(meta.getCreateTime() <= System.nanoTime() / 1000);
		assertTrue(meta.getSendTime() > System.nanoTime() / 1000 - 100000);
		assertTrue(meta.getSendTime() <= System.nanoTime() / 1000);
		assertTrue(meta.getReceiveTime() > System.nanoTime() / 1000 - 100000);
		assertTrue(meta.getReceiveTime() <= System.nanoTime() / 1000);
		assertTrue(meta.getDeliverTime() > System.nanoTime() / 1000 - 100000);
		assertTrue(meta.getDeliverTime() <= System.nanoTime() / 1000);

	}

	@Test
	public void testDefaultTimesManual() {

		MetaData meta = new MetaData();
		long time = 13123;
		meta.setCreateTime(time);
		meta.setSendTime(time);
		meta.setReceiveTime(time);
		meta.setDeliverTime(time);

		assertEquals(time, meta.getCreateTime());
		assertEquals(time, meta.getSendTime());
		assertEquals(time, meta.getReceiveTime());
		assertEquals(time, meta.getDeliverTime());

	}

	@Test
	public void testUserTimes() {

		MetaData meta = new MetaData();
		String key = "afdadfasfd";
		long time = 213123;

		assertFalse(meta.hasUserTime(key));
		meta.setUserTime(key, time);
		assertTrue(meta.hasUserTime(key));
		assertEquals(time, meta.getUserTime(key));
		long updatedTime = 634545;
		meta.setUserTime(key, updatedTime);
		assertEquals(updatedTime, meta.getUserTime(key));

		assertEquals(1, meta.userTimeKeys().size());
		assertTrue(meta.userTimeKeys().contains(key));

		try {
			meta.getUserTime("unknown");
			fail();
		} catch (IllegalArgumentException e) {
		}

		String autoKey = "auto";
		meta.setUserTime(autoKey, 0);

		assertTrue(meta.getUserTime(autoKey) > System.nanoTime() / 1000 - 100000);
		assertTrue(meta.getUserTime(autoKey) <= System.nanoTime() / 1000);

	}

	@Test
	public void testUserInfos() {

		MetaData meta = new MetaData();
		String key = "afdadfasfd";
		String value = "213123";

		assertFalse(meta.hasUserInfo(key));
		meta.setUserInfo(key, value);
		assertTrue(meta.hasUserInfo(key));
		assertEquals(value, meta.getUserInfo(key));
		String updatedInfo = "foobar";
		meta.setUserInfo(key, updatedInfo);
		assertEquals(updatedInfo, meta.getUserInfo(key));

		assertEquals(1, meta.userInfoKeys().size());
		assertTrue(meta.userInfoKeys().contains(key));

		try {
			meta.getUserInfo("unknown");
			fail();
		} catch (IllegalArgumentException e) {
		}

	}

	@Test
	public void testComparison() {

		MetaData meta1 = new MetaData();
		MetaData meta2 = new MetaData();
		assertFalse(meta1.equals(meta2)); // distinct times + no UUIDs
		meta2.setCreateTime(meta1.getCreateTime());
		assertTrue(meta1.equals(meta2));
		
		meta1.setSenderId(new Id());
		assertFalse(meta1.equals(meta2));
		assertFalse(meta2.equals(meta1));

		meta2.setSenderId(meta1.getSenderId());
		assertEquals(meta1, meta2); // identical

		meta1.setSenderId(new Id());
		assertFalse(meta1.equals(meta2)); // distinct UUIDs, again

		meta1 = new MetaData();
		meta2 = new MetaData();
		meta2.setSenderId(meta1.getSenderId());
		meta2.setCreateTime(meta1.getCreateTime());
		assertEquals(meta1, meta2);
		meta2.setUserInfo("foo", "bar");
		assertFalse(meta1.equals(meta2));
		meta1.setUserInfo("foo", "bar");
		assertEquals(meta1, meta2);

	}
}

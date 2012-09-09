/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
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
		assertTrue(meta.getCreateTime() >= System.currentTimeMillis() * 1000 - 100000);
		assertTrue(meta.getCreateTime() <= System.currentTimeMillis() * 1000);
		assertEquals(0, meta.getReceiveTime());
		assertEquals(0, meta.getDeliverTime());
		assertEquals(0, meta.getSendTime());
		assertTrue(meta.userTimeKeys().isEmpty());

	}

	@Test
	public void testDefaultTimesAuto() {

		MetaData meta = new MetaData();
		meta.setCreateTime(0);
		meta.setSendTime(0);
		meta.setReceiveTime(0);
		meta.setDeliverTime(0);

		assertTrue(meta.getCreateTime() > System.currentTimeMillis() * 1000 - 100000);
		assertTrue(meta.getCreateTime() <= System.currentTimeMillis() * 1000);
		assertTrue(meta.getSendTime() > System.currentTimeMillis() * 1000 - 100000);
		assertTrue(meta.getSendTime() <= System.currentTimeMillis() * 1000);
		assertTrue(meta.getReceiveTime() > System.currentTimeMillis() * 1000 - 100000);
		assertTrue(meta.getReceiveTime() <= System.currentTimeMillis() * 1000);
		assertTrue(meta.getDeliverTime() > System.currentTimeMillis() * 1000 - 100000);
		assertTrue(meta.getDeliverTime() <= System.currentTimeMillis() * 1000);

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

		assertTrue(meta.getUserTime(autoKey) > System.currentTimeMillis() * 1000 - 100000);
		assertTrue(meta.getUserTime(autoKey) <= System.currentTimeMillis() * 1000);

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
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
		MetaData meta2 = new MetaData();
		assertFalse(meta1.equals(meta2)); // distinct times + no UUIDs
		meta2.setCreateTime(meta1.getCreateTime());
		assertTrue(meta1.equals(meta2));

		meta1 = new MetaData();
		meta2 = new MetaData();
		meta2.setCreateTime(meta1.getCreateTime());
		assertEquals(meta1, meta2);
		meta2.setUserInfo("foo", "bar");
		assertFalse(meta1.equals(meta2));
		meta1.setUserInfo("foo", "bar");
		assertEquals(meta1, meta2);

	}
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for {@link MetaData}.
 *
 * @author jwienke
 */
public class MetaDataTest {

    private static final int MILLIS_TO_MICROS = 1000;
    private static final int ALLOWED_DELTA_MICROS = 100000;

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void construction() {

        final MetaData meta = new MetaData();
        assertTrue(meta.getCreateTime() >= System.currentTimeMillis()
                * MILLIS_TO_MICROS - ALLOWED_DELTA_MICROS);
        assertTrue(meta.getCreateTime() <= System.currentTimeMillis()
                * MILLIS_TO_MICROS);
        assertEquals(0, meta.getReceiveTime());
        assertEquals(0, meta.getDeliverTime());
        assertEquals(0, meta.getSendTime());
        assertTrue(meta.userTimeKeys().isEmpty());

    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void defaultTimesAuto() {

        final MetaData meta = new MetaData();
        meta.setCreateTime(0);
        meta.setSendTime(0);
        meta.setReceiveTime(0);
        meta.setDeliverTime(0);

        assertTrue(meta.getCreateTime() > System.currentTimeMillis()
                * MILLIS_TO_MICROS - ALLOWED_DELTA_MICROS);
        assertTrue(meta.getCreateTime() <= System.currentTimeMillis()
                * MILLIS_TO_MICROS);
        assertTrue(meta.getSendTime() > System.currentTimeMillis()
                * MILLIS_TO_MICROS - ALLOWED_DELTA_MICROS);
        assertTrue(meta.getSendTime() <= System.currentTimeMillis()
                * MILLIS_TO_MICROS);
        assertTrue(meta.getReceiveTime() > System.currentTimeMillis()
                * MILLIS_TO_MICROS - ALLOWED_DELTA_MICROS);
        assertTrue(meta.getReceiveTime() <= System.currentTimeMillis()
                * MILLIS_TO_MICROS);
        assertTrue(meta.getDeliverTime() > System.currentTimeMillis()
                * MILLIS_TO_MICROS - ALLOWED_DELTA_MICROS);
        assertTrue(meta.getDeliverTime() <= System.currentTimeMillis()
                * MILLIS_TO_MICROS);

    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void defaultTimesManual() {

        final MetaData meta = new MetaData();
        final long time = 13123;
        meta.setCreateTime(time);
        meta.setSendTime(time);
        meta.setReceiveTime(time);
        meta.setDeliverTime(time);

        assertEquals(time, meta.getCreateTime());
        assertEquals(time, meta.getSendTime());
        assertEquals(time, meta.getReceiveTime());
        assertEquals(time, meta.getDeliverTime());

    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void userTimes() {

        final MetaData meta = new MetaData();
        final String key = "afdadfasfd";
        final long time = 213123;

        assertFalse(meta.hasUserTime(key));
        meta.setUserTime(key, time);
        assertTrue(meta.hasUserTime(key));
        assertEquals(time, meta.getUserTime(key));
        final long updatedTime = 634545;
        meta.setUserTime(key, updatedTime);
        assertEquals(updatedTime, meta.getUserTime(key));

        assertEquals(1, meta.userTimeKeys().size());
        assertTrue(meta.userTimeKeys().contains(key));

    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownUserTimeException() {
        final MetaData meta = new MetaData();
        meta.getUserTime("unknown");
    }

    @Test
    public void autoTimeStampUserTime() {
        final MetaData meta = new MetaData();
        final String autoKey = "auto";
        meta.setUserTime(autoKey, 0);

        assertTrue(meta.getUserTime(autoKey) > System.currentTimeMillis()
                * MILLIS_TO_MICROS - ALLOWED_DELTA_MICROS);
        assertTrue(meta.getUserTime(autoKey) <= System.currentTimeMillis()
                * MILLIS_TO_MICROS);
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void userInfos() {

        final MetaData meta = new MetaData();
        final String key = "afdadfasfdxxx";
        final String value = "213123";

        assertFalse(meta.hasUserInfo(key));
        meta.setUserInfo(key, value);
        assertTrue(meta.hasUserInfo(key));
        assertEquals(value, meta.getUserInfo(key));
        final String updatedInfo = "foobar";
        meta.setUserInfo(key, updatedInfo);
        assertEquals(updatedInfo, meta.getUserInfo(key));

        assertEquals(1, meta.userInfoKeys().size());
        assertTrue(meta.userInfoKeys().contains(key));

    }

    @Test(expected = IllegalArgumentException.class)
    public void userInfosUnknownException() {
        final MetaData meta = new MetaData();
        meta.getUserInfo("unknown-info");
    }

    // we want to test equals and call orderings here explicitly
    @SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
    @Test
    public void comparison() throws InterruptedException {

        MetaData meta1 = new MetaData();
        Thread.sleep(ALLOWED_DELTA_MICROS / MILLIS_TO_MICROS);
        MetaData meta2 = new MetaData();
        assertFalse(meta1.equals(meta2)); // distinct times + no UUIDs
        meta2.setCreateTime(meta1.getCreateTime());
        assertTrue(meta1.equals(meta2));

        meta1 = new MetaData();
        meta2 = new MetaData();
        meta2.setCreateTime(meta1.getCreateTime());
        assertEquals(meta1, meta2);
        final String infoKey = "foo";
        final String infoValue = "bar";
        meta2.setUserInfo(infoKey, infoValue);
        assertFalse(meta1.equals(meta2));
        meta1.setUserInfo(infoKey, infoValue);
        assertEquals(meta1, meta2);

    }
}

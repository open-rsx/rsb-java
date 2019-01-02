/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
package rsb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import rsb.RsbTestCase;

public class PropertiesTest extends RsbTestCase {

    private static final String KEY = "Spread.Path";
    private static final String VALUE = "blaaaa";

    @Test
    public void setGetProperty() {
        final Properties prop = new Properties();
        prop.setProperty(KEY, VALUE);
        assertEquals(VALUE, prop.getProperty(KEY).asString());
    }

    @Test
    public void getDefault() {
        final Properties prop = new Properties();
        final String def = "blaablaa";
        assertEquals(def, prop.getProperty(KEY, def).asString());
    }

    @Test
    public void setPortPropertyTwice() {
        final Properties prop = new Properties();
        prop.setProperty(KEY, VALUE);
        assertEquals(VALUE, prop.getProperty(KEY).asString());
        final String value2 = VALUE + "test";
        prop.setProperty(KEY, value2);
        assertEquals(value2, prop.getProperty(KEY).asString());
    }

    @Test
    public void reset() {
        final Properties prop = new Properties();
        prop.setProperty(KEY, VALUE);
        prop.reset();
        assertTrue(prop.getAvailableKeys().isEmpty());
        assertFalse(prop.hasProperty(KEY));
    }

    @Test
    public void defaultValueNumber() {
        final Properties prop = new Properties();
        final int defaultValue = 42;
        assertEquals(defaultValue, prop.getProperty(KEY, defaultValue)
                .asInteger());
    }

    @Test
    public void defaultValueBoolean() {
        final Properties prop = new Properties();
        assertTrue(prop.getProperty(KEY, true).asBoolean());
        assertFalse(prop.getProperty(KEY, false).asBoolean());
    }

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void filter() {

        final Properties props = new Properties();

        // add some options which we want to retrieve later
        final String desiredPrefix = "a.test";
        final String[] desired = new String[] {
            desiredPrefix + ".foo",
            desiredPrefix + ".bar.bla", desiredPrefix,
        };
        for (final String key : desired) {
            props.setProperty(key, "blaaa");
        }

        // add some stuff we do not want
        for (final String key : new String[] {
                "a",
                "other.root",
                "bar.bla",
                "a.testing",
                }) {
            props.setProperty(key, "blubb");
        }

        final Properties filtered = props.filter(desiredPrefix);
        assertEquals(new HashSet<String>(Arrays.asList(desired)),
                filtered.getAvailableKeys());

    }

    @Test
    public void merge() {

        final Properties target = new Properties();
        target.setProperty(KEY, VALUE);

        final String notExistingKey = "Ido.not.exist";
        final String notExistingValue = "gjhfghsFA";
        final String duplicatedValue = "567567fghfdgser";
        final Properties toMerge = new Properties();
        toMerge.setProperty(KEY, duplicatedValue);
        toMerge.setProperty(notExistingKey, notExistingValue);

        target.merge(toMerge);

        assertTrue(target.hasProperty(KEY));
        assertTrue(target.hasProperty(notExistingKey));
        assertEquals(duplicatedValue, target.getProperty(KEY).asString());
        assertEquals(notExistingValue, target.getProperty(notExistingKey)
                .asString());

    }

}

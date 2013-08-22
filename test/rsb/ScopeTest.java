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

import java.util.List;

import org.junit.Test;

// CHECKSTYLE.OFF: MultipleStringLiterals - we are testing parsing code.
//                 Contains a lot of strings.
/**
 * Test case for {@link Scope}.
 *
 * @author jwienke
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals" })
public class ScopeTest {

    @Test
    public void parsingRoot() {

        final Scope root = new Scope("/");
        assertEquals(0, root.getComponents().size());

    }

    @Test
    public void parsingSingleComponent() {

        final Scope onePart = new Scope("/test/");
        assertEquals(1, onePart.getComponents().size());
        assertEquals("test", onePart.getComponents().get(0));

    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void parsingManuParts() {

        // CHECKSTYLE.OFF: MagicNumber - hand calculated
        final Scope manyParts = new Scope("/this/is/a/dumb3/test/");
        assertEquals(5, manyParts.getComponents().size());
        assertEquals("this", manyParts.getComponents().get(0));
        assertEquals("is", manyParts.getComponents().get(1));
        assertEquals("a", manyParts.getComponents().get(2));
        assertEquals("dumb3", manyParts.getComponents().get(3));
        assertEquals("test", manyParts.getComponents().get(4));
        // CHECKSTYLE.ON: MagicNumber - hand calculated

    }

    @Test
    public void parsingWithoutSlash() {

        // also ensure that the shortcut syntax works
        final Scope shortcut = new Scope("/this/is");
        assertEquals(2, shortcut.getComponents().size());
        assertEquals("this", shortcut.getComponents().get(0));
        assertEquals("is", shortcut.getComponents().get(1));

    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void parsingErrorEmpty() {
        new Scope("");
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void parsingErrorEmptySpace() {
        new Scope(" ");
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void parsingErrorSpace() {
        new Scope("/with space/does/not/work/");
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void parsingErrorSpecialChar() {
        new Scope("/with/do#3es/not43as/work/");
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void parsingErrorDoubleSlash() {
        new Scope("/this//is/not/allowed/");
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void parsingErrorEmptyComponent() {
        new Scope("/this/ /is/not/allowed/");
    }

    @Test
    public void stringRepresentation() {

        assertEquals("/", new Scope("/").toString());
        assertEquals("/foo/", new Scope("/foo/").toString());
        assertEquals("/foo/bar/", new Scope("/foo/bar/").toString());
        assertEquals("/foo/bar/", new Scope("/foo/bar").toString());

    }

    @Test
    public void equality() {

        assertEquals(new Scope("/"), new Scope("/"));
        assertFalse(new Scope("/").equals(new Scope("/foo/")));
        assertFalse(new Scope("/foo/").equals(new Scope("/")));

    }

    @Test
    public void concat() {

        assertEquals(new Scope("/"), new Scope("/").concat(new Scope("/")));
        assertEquals(new Scope("/a/test/"),
                new Scope("/").concat(new Scope("/a/test/")));
        assertEquals(new Scope("/a/test/"),
                new Scope("/a/test/").concat(new Scope("/")));
        assertEquals(new Scope("/a/test/example"),
                new Scope("/a/test/").concat(new Scope("/example/")));

    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void hierarchyComparison() {

        assertTrue(new Scope("/a/").isSubScopeOf(new Scope("/")));
        assertTrue(new Scope("/a/b/c/").isSubScopeOf(new Scope("/")));
        assertTrue(new Scope("/a/b/c/").isSubScopeOf(new Scope("/a/b/")));
        assertFalse(new Scope("/a/b/c/").isSubScopeOf(new Scope("/a/b/c/")));
        assertFalse(new Scope("/a/b/c/").isSubScopeOf(new Scope("/a/b/c/d/")));
        assertFalse(new Scope("/a/x/c/").isSubScopeOf(new Scope("/a/b/")));

        assertTrue(new Scope("/").isSuperScopeOf(new Scope("/a/")));
        assertTrue(new Scope("/").isSuperScopeOf(new Scope("/a/b/c/")));
        assertTrue(new Scope("/a/b/").isSuperScopeOf(new Scope("/a/b/c/")));
        assertFalse(new Scope("/a/b/c/").isSuperScopeOf(new Scope("/a/b/c/")));
        assertFalse(new Scope("/a/b/c/d/").isSuperScopeOf(new Scope("/a/b/c/")));
        assertFalse(new Scope("/b/").isSuperScopeOf(new Scope("/a/b/c/")));

    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    public void superScope() {

        assertEquals(0, new Scope("/").superScopes(false).size()); // NOPMD:
                                                                   // false
                                                                   // positive

        // CHECKSTYLE.OFF: MagicNumber - hand calculated
        List<Scope> supers = new Scope("/this/is/a/test/").superScopes(false);
        assertEquals(4, supers.size());
        assertEquals(new Scope("/"), supers.get(0));
        assertEquals(new Scope("/this/"), supers.get(1));
        assertEquals(new Scope("/this/is/"), supers.get(2));
        assertEquals(new Scope("/this/is/a/"), supers.get(3));

        supers = new Scope("/").superScopes(true);
        assertEquals(1, supers.size());
        assertEquals(new Scope("/"), supers.get(0));

        supers = new Scope("/this/is/a/test/").superScopes(true);
        assertEquals(5, supers.size());
        assertEquals(new Scope("/"), supers.get(0));
        assertEquals(new Scope("/this/"), supers.get(1));
        assertEquals(new Scope("/this/is/"), supers.get(2));
        assertEquals(new Scope("/this/is/a/"), supers.get(3));
        assertEquals(new Scope("/this/is/a/test/"), supers.get(4));
        // CHECKSTYLE.OFF: MagicNumber - hand calculated

    }

}
// CHECKSTYLE.ON: MultipleStringLiterals

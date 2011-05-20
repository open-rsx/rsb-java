/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * Test case for {@link Scope}.
 * 
 * @author jwienke
 */
public class ScopeTest {

	@Test
	public void parsing() {

		Scope root = new Scope("/");
		assertEquals(0, root.getComponents().size());

		Scope onePart = new Scope("/test/");
		assertEquals(1, onePart.getComponents().size());
		assertEquals("test", onePart.getComponents().get(0));

		Scope manyParts = new Scope("/this/is/a/dumb3/test/");
		assertEquals(5, manyParts.getComponents().size());
		assertEquals("this", manyParts.getComponents().get(0));
		assertEquals("is", manyParts.getComponents().get(1));
		assertEquals("a", manyParts.getComponents().get(2));
		assertEquals("dumb3", manyParts.getComponents().get(3));
		assertEquals("test", manyParts.getComponents().get(4));

		// also ensure that the shortcut syntax works
		Scope shortcut = new Scope("/this/is");
		assertEquals(2, shortcut.getComponents().size());
		assertEquals("this", shortcut.getComponents().get(0));
		assertEquals("is", shortcut.getComponents().get(1));

	}

	@Test(expected = IllegalArgumentException.class)
	public void parsingErrorEmpty() {
		new Scope("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void parsingErrorEmptySpace() {
		new Scope(" ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void parsingErrorSpace() {
		new Scope("/with space/does/not/work/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void parsingErrorSpecialChar() {
		new Scope("/with/do#3es/not43as/work/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void parsingErrorDoubleSlash() {
		new Scope("/this//is/not/allowed/");
	}

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

	@Test
	public void testHierarchyComparison() {

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

	@Test
	public void superScope() {

		assertEquals(0, new Scope("/").superScopes(false).size());

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

	}

}

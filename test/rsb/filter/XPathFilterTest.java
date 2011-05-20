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
package rsb.filter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import rsb.Event;
import rsb.xml.XPath;

/**
 * @author swrede
 * 
 */
public class XPathFilterTest {

	/**
	 * Test method for {@link rsb.filter.XPathFilter#transform(rsb.Event)}.
	 */
	@Test
	public void testTransform() {
		String xml[] = new String[2];
		xml[0] = "<msg nr=\"1\"/>";
		xml[1] = "<msg nr=\"2\"/>";
		Event e = new Event("string");
		e.setData(xml[0]);
		XPathFilter xpf = new XPathFilter(new XPath("/msg[@nr=\"1\"]"));
		assertNotNull(xpf.transform(e));
		e.setData(xml[1]);
		assertNull(xpf.transform(e));
	}

}

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

import org.junit.Test;

import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;

/**
 * @author jwienke
 */
public class QualityOfServiceSpecTest {

	@Test
	public void defaultSettings() {
		QualityOfServiceSpec spec = new QualityOfServiceSpec();
		assertEquals(QualityOfServiceSpec.Ordering.UNORDERED,
				spec.getOrdering());
		assertEquals(QualityOfServiceSpec.Reliability.RELIABLE,
				spec.getReliability());
	}

	@Test
	public void constructor() {
		final QualityOfServiceSpec.Ordering ordering = Ordering.ORDERED;
		final QualityOfServiceSpec.Reliability reliability = Reliability.UNRELIABLE;
		QualityOfServiceSpec spec = new QualityOfServiceSpec(ordering,
				reliability);
		assertEquals(ordering, spec.getOrdering());
		assertEquals(reliability, spec.getReliability());
	}

	@Test
	public void comparison() {
		assertEquals(new QualityOfServiceSpec(), new QualityOfServiceSpec());
		assertFalse(new QualityOfServiceSpec().equals(new QualityOfServiceSpec(
				Ordering.ORDERED, Reliability.RELIABLE)));
		assertFalse(new QualityOfServiceSpec().equals(new QualityOfServiceSpec(
				Ordering.UNORDERED, Reliability.UNRELIABLE)));
	}

}

/** ============================================================
 *
 * This file is part of the RSB project.
 *
 * Copyright (C) 2011 Jan Moringen <jmoringe@techfak.uni-bielefeld.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * ============================================================  */

package rsb.filter;

import rsb.Scope;
import rsb.ParticipantId;
import rsb.Event;

import rsb.filter.OriginFilter;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for the {@link rsb.filter.OriginFilter} class.
 *
 * @author jmoringe
 */
public class OriginFilterTest {
        @Test
        public void testTransform() {
                ParticipantId origin1 = new ParticipantId();
                Event e1 = new Event(new Scope("/images"), String.class, "bla");
		e1.setId(origin1, 234);

                ParticipantId origin2 = new ParticipantId();
                Event e2 = new Event(new Scope("/images"), String.class, "bla");
		e2.setId(origin2, 0);

		OriginFilter f1 = new OriginFilter(origin1);
		assertTrue(f1.transform(e1) != null);
                assertTrue(f1.transform(e2) == null);

                OriginFilter f2 = new OriginFilter(origin1, true);
		assertTrue(f2.transform(e1) == null);
                assertTrue(f2.transform(e2) != null);
        }
};

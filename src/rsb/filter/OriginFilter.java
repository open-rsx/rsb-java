/**
 * ============================================================
 *
 * This file is part of the RSBJava project.
 *
 * Copyright (C) 2011 Jan Moringen
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
 * ============================================================
 */

package rsb.filter;

import rsb.ParticipantId;
import rsb.Event;
import rsb.EventId;

/**
 * Events matched by this filter have to originate from a particular
 * participant.
 *
 * @author swrede
 * @author jmoringe
 */
public class OriginFilter extends AbstractFilter {

        ParticipantId origin;
	boolean invert = false;
	
        public OriginFilter (ParticipantId origin, boolean invert) {
                super(IdentityFilter.class);
                this.origin = origin;
                this.invert = invert;
        }

        public OriginFilter (ParticipantId origin) {
                this(origin, false);
        }
    
        public ParticipantId getOrigin() {
                return origin;
        }
    
        public boolean isInverted() {
                return invert;
        }

        @Override
	public Event transform(Event e) {
                boolean matches = e.getSenderId().equals(origin);
                matches = invert ? !matches : matches;
                if (matches) {
                        return e;
                } else {
                        return null;
                }
	}

	/*
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
	}	

	public void skip(EventId id) {
		super.skip(id);
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof OriginFilter
                        && origin.equals(((OriginFilter) that).origin)
                        && (invert == ((OriginFilter) that).invert);
	}
	
}

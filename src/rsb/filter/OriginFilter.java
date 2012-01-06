/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 Jan Moringen
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
                super(OriginFilter.class);
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

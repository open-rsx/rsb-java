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
package rsb.transport.spread;

import java.util.logging.Logger;

import spread.SpreadMessage;

/**
 * SpreadMessageConverter encapsulates the handling of regular spread messages
 * and considers Membership messages.
 * 
 * TODO (de-)multiplex large messages here
 * 
 * @author swrede
 * 
 */
class SpreadMessageConverter {

	public final static Logger LOG = Logger.getLogger(SpreadMessageConverter.class.getName());

	/**
	 * Returns a {@link DataMessage} if presented by sm, else returns
	 * <code>null</code>.
	 * 
	 * @param sm
	 *            message to analyze
	 * @return DataMessage or <code>null</code> if not contained in sm
	 */
	public DataMessage process(final SpreadMessage sm) {
		DataMessage data = null;
		if (sm.isMembership()) {
			// TODO think about meaningful handling of membership messages
			// and print further info
			LOG.fine("Received membership message for group: "
					+ sm.getMembershipInfo().getGroup());

		} else {
			try {
				data = DataMessage.convertSpreadMessage(sm);
			} catch (SerializeException e) {
				LOG.warning("Error de-serializing SpreadMessage: " + e.getMessage());
			}
		}
		// TODO in this design, following objects must be capable of handling
		// null objects!
		// better approach would be to encapsulate logical changes to the
		// unified event bus
		// in internal events
		return data;
	}

}
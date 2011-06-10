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

	Logger log = Logger.getLogger(SpreadMessageConverter.class.getName());

	/**
	 * Returns a {@link DataMessage} if presented by sm, else returns
	 * <code>null</code>.
	 * 
	 * @param sm
	 *            message to analyze
	 * @return DataMessage or <code>null</code> if not contained in sm
	 */
	public DataMessage process(SpreadMessage sm) {
		DataMessage dm = null;
		if (sm.isMembership()) {
			// TODO think about meaningful handling of membership messages
			// and print further info
			log.info("Received membership message for group: "
					+ sm.getMembershipInfo().getGroup());

		} else {
			try {
				dm = DataMessage.convertSpreadMessage(sm);
			} catch (SerializeException e) {
				e.printStackTrace();
				SpreadWrapper.log.warning("Error de-serializing SpreadMessage");
			}
		}
		// TODO in this design, following objects must be capable of handling
		// null objects!
		// better approach would be to encapsulate logical changes to the
		// unified event bus
		// in internal events
		return dm;
	}

}
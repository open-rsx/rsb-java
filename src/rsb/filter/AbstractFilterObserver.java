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

import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class AbstractFilterObserver implements FilterObserver {

	protected final static Logger LOG = Logger.getLogger(AbstractFilterObserver.class.getCanonicalName());
	
	/* (non-Javadoc)
	 * @see rsb.filter.FilterObserver#notify(rsb.filter.AbstractFilter, rsb.filter.FilterAction)
	 */
	@Override
	public void notify(AbstractFilter event, FilterAction action) {
		LOG.fine("AbstractFilterObser::notify(AbstractFilter e, FilterAction a) called");
	}

	/* (non-Javadoc)
	 * @see rsb.filter.FilterObserver#notify(rsb.filter.ScopeFilter, rsb.filter.FilterAction)
	 */
	@Override
	public void notify(ScopeFilter event, FilterAction action) {
		LOG.fine("AbstractFilterObser::notify(ScopeFilter e, FilterAction a) called");
	}

	/* (non-Javadoc)
	 * @see rsb.filter.FilterObserver#notify(rsb.filter.TypeFilter, rsb.filter.FilterAction)
	 */
	@Override
	public void notify(TypeFilter event, FilterAction action) {
		LOG.fine("AbstractFilterObser::notify(TypeFilter e, FilterAction a) called");

	}

	/* (non-Javadoc)
	 * @see rsb.filter.FilterObserver#notify(rsb.filter.OriginFilter, rsb.filter.FilterAction)
	 */
	@Override
	public void notify(OriginFilter event, FilterAction action) {
		LOG.fine("IdentityFilterObser::notify(TypeFilter e, FilterAction a) called");
	}

}

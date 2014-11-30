/**
 * ============================================================
 *
 * This file is a part of the rsb.git.java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb.introspection;


/**
 * @author swrede
 *
 */
public class DummyHostInfo extends HostInfo {

    /* (non-Javadoc)
     * @see rsb.introspection.HostInfo#getId()
     */
    @Override
    String getId() {
        // TODO Auto-generated method stub
        return "Trusty-VM-SW";
    }

    /* (non-Javadoc)
     * @see rsb.introspection.HostInfo#getHostname()
     */
    @Override
    String getHostname() {
        // TODO Auto-generated method stub
        return "macbook-pro-sw";
    }

}

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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class PortableHostInfo extends CommonHostInfo {

    private static final Logger LOG = Logger.getLogger(PortableHostInfo.class.getName());

    public PortableHostInfo() {
        super();
        this.initialize();
    }

    private void initialize() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        // Get name returns something like 6460@AURORA. Where the value
        // after the @ symbol is the hostname.
        final String jvmName = runtime.getName();
        try {
            this.hostname = jvmName.split("@")[1];
        } catch (final ArrayIndexOutOfBoundsException e) {
           LOG.log(Level.INFO, "Exception when parsing hostname (RuntimeMXBean.getName()==" + jvmName + ")", e);
           this.hostname = "unknown";
        }

        // try to calculate hostId via the network
        try {
            this.id = getHostIdInet();
            if (this.hostname.equalsIgnoreCase("unknown")) {
                this.hostname = this.id;
            }
            return;
        } catch (final IOException e) {
            LOG.warning("Unexpected I/O exception when getting MAC address: " + e.getMessage());
            e.printStackTrace();
        }
        // try to get local hostname via network
        this.id = getLocalHostName();
        if (this.id==null) {
            // last resort: get hostname via ManagementBean
            this.id = this.getHostname();
        }
    }

}

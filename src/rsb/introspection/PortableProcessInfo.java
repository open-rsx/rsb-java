/**
 * ============================================================
 *
 * This file is a part of the rsb-java project
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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cross-platform plain Java implementation of process info interface.
 *
 * @author swrede
 */
public class PortableProcessInfo extends CommonProcessInfo {

    private static final Logger LOG = Logger
            .getLogger(PortableProcessInfo.class.getName());

    public PortableProcessInfo() {
        super();
        this.initialize();
    }

    private void initialize() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        // Get name returns something like 6460@AURORA. Where the value
        // before the @ symbol is the PID.
        final String jvmName = runtime.getName();
        try {
            this.pid = Integer.valueOf(jvmName.split("@")[0]);
        } catch (final NumberFormatException e) {
            LOG.log(Level.INFO,
                    "Exception when parsing pid (RuntimeMXBean.getName()=="
                            + jvmName + ")", e);
        }

        this.startTime = runtime.getStartTime();

        this.name = "java-" + System.getProperty("java.runtime.version");
        // Returns the input arguments passed to the Java virtual machine which
        // does
        // not include the arguments to the main method. This method returns an
        // empty
        // list if there is no input argument to the Java virtual machine.
        this.arguments = runtime.getInputArguments();
    }

}

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

import rsb.util.OSDetector;

/**
 * @author swrede
 *
 */
// TODO rename to Common*
// TODO extract interface
public abstract class HostInfo {

    enum MachineType {
        x86,
        x86_64,
        UNKNOWN
    }

    protected String id;
    protected String hostname;
    protected String softwareType;
    protected MachineType machineType;

    public HostInfo() {
        this.softwareType = OSDetector.getOSFamily().name().toLowerCase();
        this.machineType = readMachineType();
    }

    public String getId() {
        return this.id;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getSoftwareType() {
        return this.softwareType;
    }

    public MachineType getMachineType() {
        return this.machineType;
    }

    protected MachineType readMachineType() {
        // TODO check better way to get CPU architecture
        //      or at least make sure that these keys are correct
        final String identifier = System.getProperty("os.arch");
        if (identifier.contains("x86") || identifier.contains("i386")) {
            return MachineType.x86;
        } else if (identifier.startsWith("amd64")) {
            return MachineType.x86_64;
        } else {
            return MachineType.UNKNOWN;
        }
    }

}

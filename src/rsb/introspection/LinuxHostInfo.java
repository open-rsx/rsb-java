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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.logging.Logger;



/**
 * Linux-specific implementation of HostInfo class. Tries to compute and cache
 * a unique machine id (and hostname information) from the following locations:
 *   1. /etc/machine-id
 *   2. /var/lib/dbus/machine-id
 *   3. MAC adress of network interface
 *   4. local hostname
 *
 * @author swrede
 * @author ssharma
 *
 */
public class LinuxHostInfo extends HostInfo {

    private static final Logger LOG = Logger.getLogger(LinuxHostInfo.class.getName());

    private static final String PATH_ETC_MACHINE_ID = "/etc/machine-id";
    private static final String PATH_VAR_LIB_DBUS_MACHINE_ID = "/var/lib/dbus/machine-id";

    public LinuxHostInfo() {
        super();
        initialize(PATH_ETC_MACHINE_ID, PATH_VAR_LIB_DBUS_MACHINE_ID);
    }

    public LinuxHostInfo(final String machineIdPath1, final String machineIdPath2) {
        initialize(machineIdPath1, machineIdPath2);
    }

    private void initialize(final String machineIdPath1, final String machineIdPath2) {
        this.id = readHostId(machineIdPath1,machineIdPath2);
        this.hostname = getLocalHostName();
    }

    private String readHostId(final String machineIdPath1, final String machineIdPath2) {
        final File f1 = new File(machineIdPath1);
        final File f2 = new File(machineIdPath2);
        // first option: read machineId files
        try {
            if (f1.exists() && !f1.isDirectory()) {
                return readMachineId(f1);
            } else if (f2.exists() && !f2.isDirectory()) {
                return readMachineId(f2);
            }
        } catch (final IOException e) {
            LOG.warning("Unexpected I/O exception when accessing machineId file: " + e.getMessage());
            e.printStackTrace();
        }
        // otherwise try to calculate hostId via the network
        try {
            getHostIdInet();
        } catch (final IOException e) {
            LOG.warning("Unexpected I/O exception when getting MAC address: " + e.getMessage());
            e.printStackTrace();
        }
        // last resort: return local hostname
        return getLocalHostName();
    }

    private String readMachineId(final File file) throws IOException {
        String machineId = "N/A";
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            machineId = reader.readLine();
        } catch (final IOException exception) {
            LOG.warning("Could not read MachineId from path: " + file.toString());
            exception.printStackTrace();
        } finally {
            reader.close();
        }
        return machineId;
    }

    private String getHostIdInet() throws IOException {
        InetAddress ip;
        final StringBuilder sb;

        ip = InetAddress.getLocalHost();
        // creates problem when ip address is not resolved
        final NetworkInterface network = NetworkInterface.getByInetAddress(ip);

        final byte[] mac = network.getHardwareAddress();

        if (mac == null) {
            throw new IOException(
                    "Could not read MAC adress via NetworkInterface class.");
        }

        sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i],
                    (i < mac.length - 1) ? "-" : ""));
        }

        return sb.toString();
    }

    private String getLocalHostName() {
        String host = "N/A";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            LOG.warning("Exception while getting hostName via InetAddress: " + e.getMessage());
            e.printStackTrace();
        }
        return host;
    }
}

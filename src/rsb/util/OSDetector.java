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
package rsb.util;


/**
 * Utility class to determine type of operating system.
 *
 * @author swrede
 *
 */
public class OSDetector {

    public enum OS_FAMILY {
        LINUX,
        WINDOWS,
        MACOSX,
        UNKNOWN
    }

    public static OS_FAMILY getOSFamily() {
        final String identifier = System.getProperty("os.name");
        if (identifier.startsWith("Windows")) {
            return OS_FAMILY.WINDOWS;
        } else if (identifier.startsWith("Linux")) {
            return OS_FAMILY.LINUX;
        } else if (identifier.startsWith("Mac")) {
            return OS_FAMILY.MACOSX;
        } else {
            return OS_FAMILY.UNKNOWN;
        }
    }

}

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

import java.util.logging.Logger;

import org.junit.Test;


/**
 * @author swrede
 *
 */
public class OSDetectorTest {

    private static Logger LOG = Logger.getLogger(OSDetectorTest.class.getName());

    /**
     * Test method for {@link rsb.util.OSDetector#getOSFamily()}.
     */
    @Test
    public void testGetOSFamily() {
        LOG.info("Detected " + OSDetector.getOSFamily().name() + " OS family.");
    }
}

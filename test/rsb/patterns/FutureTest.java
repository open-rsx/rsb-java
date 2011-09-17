/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

package rsb.patterns;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

import org.junit.Test;

import rsb.patterns.Future;

/**
 * @author jmoringe
 *
 */
public class FutureTest {

    final static private Logger LOG = Logger.getLogger(FutureTest.class.getName());

    @Test
    public void testSimpleGet() throws ExecutionException, TimeoutException {
	Future<Integer> future = new Future();
	future.complete(1);
	assertTrue(future.get() == 1);
    }

    @Test(expected=CancellationException.class)
    public void testCancel() throws ExecutionException, TimeoutException {
	Future<Integer> future = new Future();
	future.cancel(true);
	future.get();
    }

    @Test(expected=TimeoutException.class)
    public void testTimeout() throws ExecutionException, TimeoutException {
	Future<Integer> future = new Future();
	future.get(10);
    }



}

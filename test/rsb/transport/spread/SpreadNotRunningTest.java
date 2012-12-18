/**
 * ============================================================
 *
 * This file is a part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.patterns.DataCallback;
import rsb.patterns.LocalServer;
import rsb.util.Properties;

/**
 * Test for situations where no connection
 * to a Spread daemon is available.
 * 
 * Pre-Condition: Spread is not running
 * 
 * @author swrede
 *
 */
public class SpreadNotRunningTest {
	
	@BeforeClass
	public static void prepare() {
		Properties prop = Properties.getInstance();
		//prop.setProperty("transport.socket.enabled", "false");
		prop.setProperty("transport.spread.enabled", "true");
		prop.setProperty("transport.spread.retry", "1");				
	}
	
	@AfterClass
	public static void restore() {
		Properties.getInstance().reset();
	}
	
	@Ignore
	@Test(expected=InitializeException.class)
	public void testInformer() throws InitializeException {
		Factory factory = Factory.getInstance();
		Informer<Object> inf = factory.createInformer("/foo");
		inf.activate();
	}
	
	@Ignore
	@Test(expected=InitializeException.class)
	public void testServer() throws InitializeException {
		Factory factory = Factory.getInstance();
		LocalServer server = factory.createLocalServer("/bar");
		server.addMethod("foobar", new DataCallback<String, String>() {
			@Override
			public String invoke(String request) throws Throwable {
				return "foobar";
			}
		});
		server.activate();
	}

}

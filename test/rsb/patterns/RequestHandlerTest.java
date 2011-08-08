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

import org.junit.Test;

import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 *
 */
public class RequestHandlerTest {

	/**
	 * Test method for {@link rsb.patterns.RequestHandler#handleEvent(rsb.Event)}.
	 * @throws InitializeException 
	 */
	@Test
	public void testHandleEvent() throws InitializeException {
		Factory factory = Factory.getInstance();
		Server server = factory.createLocalServer(new Scope("/example/server"));		
		ReplyCallback callback = new ReplyCallback();
		LocalMethod method = new LocalMethod(server, "test");
		RequestHandler<String, String> handler = new RequestHandler<String, String>(method, callback);
		assertNotNull(handler);
		method.activate();
		Event request = new Event(String.class);
		request.setData("testtesttest");
		request.setSenderId(new ParticipantId());
		request.setMethod("REQUEST");
		request.setSequenceNumber(1);
		handler.handleEvent(request);
		assertTrue(callback.wasCalled());
		handler.handleEvent(request);
	}

}

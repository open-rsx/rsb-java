/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
package rsb.event;

import java.util.EventListener;

import rsb.RSBEvent;

/**
 * XCF uses an event listener mechanism to deal with the various events raised
 * by the framework's basic communication patterns. XcfEventListener is the
 * topmost interface in the listener hierarchy. There are specialized listener
 * (and event) types for Publish/Subscribe, Server/RemoteServer and the
 * ActiveMemory. In Publish/Subscribe PublishEvents are raised whenever the
 * Subscriber receives a message from the Publisher. In Server/RemoteServer
 * a CallbackEvent (respectively a OneWayCallbackEvent for methods that do not
 * return anything) is raised by the Server whenever a RemoteServer calls a
 * method. The Memory raises various events, e.g. when a query returns results
 * asynchronously. 
 * 
 * @author swrede
 * @see RSBEvent
 */
 
public interface RSBEventListener<T extends RSBEvent> extends EventListener {

    public void handleEvent(T e);
    
//	// TODO Check if setXPath makes sense here
//	public void setXPath(XPath xpath);
//	public XPath getXPath();
//	public String getName();
	
    // TODO check if this is a common property of all listeners
	//public void setRouter(Router r);

}

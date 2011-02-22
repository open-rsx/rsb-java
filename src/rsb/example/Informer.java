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
package rsb.example;

import rsb.InitializeException;
import rsb.Publisher;
import rsb.RSBEvent;
import rsb.transport.TransportFactory;

/**
 * @author swrede
 *
 */
public class Informer {

	/**
	 * @param args
	 * @throws InitializeException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InitializeException, InterruptedException {
		TransportFactory tf = TransportFactory.getInstance();
		Publisher p = new Publisher("rsb://example/informer", tf);
		p.activate();
		RSBEvent e = new RSBEvent("string");
		for (int i = 0; i < 100; i++) {
			e.generateID();
			e.setData("<message uuid=\""+e.getUuid()+"\" val=\"Hello World!\" nr="+i+"\"/>");			
			p.send(e);
			Thread.sleep(1);
		}
		p.deactivate();
	}

}

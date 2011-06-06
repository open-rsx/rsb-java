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

import rsb.Factory;
import rsb.InitializeException;
import rsb.Scope;

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
	public static void main(String[] args) throws InitializeException,
			InterruptedException {
		rsb.Informer<String> p = Factory.getInstance().createInformer(
				new Scope("/example/informer"));
		p.activate();
		for (int i = 0; i < 100; i++) {
			p.send("<message val=\"Hello World!\" nr=\"" + i + "\"/>");
		}
		p.deactivate();
	}

}

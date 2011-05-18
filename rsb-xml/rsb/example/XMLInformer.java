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
import rsb.transport.XOPData;
import rsb.xml.SyntaxException;

/**
 * @author swrede
 */
public class XMLInformer {

	/**
	 * @param args
	 * @throws InitializeException
	 * @throws InterruptedException
	 * @throws SyntaxException
	 */
	public static void main(String[] args) throws InitializeException,
			InterruptedException, SyntaxException {
		Publisher<String> p = new Publisher<String>("rsb://example/informer");
		p.activate();
		for (int i = 0; i < 100; i++) {
			XOPData xop = XOPData
					.fromString("<message val=\"Hello World!\" nr=\"" + i
							+ "\"/>");
			p.send(xop.getDocumentAsText());
		}
		p.deactivate();
	}

}

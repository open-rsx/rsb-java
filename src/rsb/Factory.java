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

package rsb;

import rsb.patterns.LocalServer;
import rsb.patterns.RemoteServer;

/**
 * A factory for RSB client-level objects. This class is a Singleton.
 *
 * @author jwienke
 */
public final class Factory {

	/**
	 * The singleton instance.
	 */
	private static Factory instance = new Factory();

	/**
	 * Private constructor to ensure Singleton.
	 */
	private Factory() {
	}

	/**
	 * Returns the one and only instance of this class.
	 *
	 * @return singleton factory instance
	 */
	public static Factory getInstance() {
		return instance;
	}

	/**
	 * Creates a new informer instance.
	 *
	 * @param <T>
	 *            type of the data sent by this informer
	 * @param scope
	 *            scope of the informer
	 * @param type
	 *            type identifier of the informer
	 * @return new informer instance
	 */
	public <T> Informer<T> createInformer(Scope scope, String type) {
		return new Informer<T>(scope, type);
	}

	/**
	 * Creates a new informer instance.
	 *
	 * @param <T>
	 *            type of the data sent by this informer
	 * @param scope
	 *            scope of the informer
	 * @return new informer instance
	 */
	public <T> Informer<T> createInformer(Scope scope) {
		return new Informer<T>(scope);
	}

	/**
	 * Creates a new listener instance.
	 *
	 * @param scope
	 *            scope of the listener
	 * @return new listener
	 */
	public Listener createListener(Scope scope) {
		return new Listener(scope);
	}

    /**
     * Creates a new LocalServer object which exposes methods under
     * the scope @a scope.
     *
     * @param scope
     *            The scope under which methods of the LocalServer
     *            object should be exposed.
     * @return The new LocalServer object.
     */
    public LocalServer createLocalServer(Scope scope) {
	return new LocalServer(scope);
    }

    /**
     * Creates a new RemoteServer object which is suitable for calling
     * methods provided by a remote server under the scope @a scope.
     *
     * @param scope
     *            The scope under which a remote server provides its
     *            methods.
     * @return The new RemoteServer object.
     */
    public RemoteServer createRemoteServer(Scope scope) {
	return new RemoteServer(scope);
    }

}

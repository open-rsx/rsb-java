/**
 * ============================================================
 *
 * This file is part of the RSBJava project
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

import java.lang.Throwable;

/**
 * Implementations of this interface are used to provide the behavior
 * of exposed methods.
 *
 * @author jmoringe
 */
public interface DataCallback<T, U> {

    /**
     * This method is called to invoke the actual behavior of an
     * exposed method.
     *
     * @param U The argument passed to the associated method by the
     * remote caller.
     * @return A result that should be returned to the remote caller
     * as the result of the calling the method.
     * @throw Throwable Can throw anything.
     */
    public T invoke(U request) throws Throwable;
};
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
package rsb.transport;

import rsb.util.Holder;

/**
 * This class represents a converter interface for a wire format T.
 * Implementations may support one or more domain types for (de-)serialization
 * to T and back to a specific object type referenced through the typeinfo
 * parameter.
 * 
 * @author swrede
 */
public interface Converter<T> {

	public Holder<T> serialize(String typeInfo, Object obj);

	public Holder<Object> deserialize(String typeInfo, T buffer);

}

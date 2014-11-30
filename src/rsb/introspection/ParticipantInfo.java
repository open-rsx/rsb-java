/**
 * ============================================================
 *
 * This file is a part of the rsb.git.java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb.introspection;

import rsb.ParticipantId;
import rsb.Scope;


/**
 * Internal data holder for participant information.
 * Used within introspection model.
 *
 * @author swrede
 * @author ssharma
 *
 */
public class ParticipantInfo {

    private final String kind;
    private final ParticipantId id;
    private final ParticipantId parentId;
    private final Scope scope;
    private final String type;

    public ParticipantInfo(final String kind, final ParticipantId id,
            final ParticipantId parentId, final Scope scope, final String type) {
        this.kind = kind;
        this.id = id;
        this.parentId = parentId;
        this.scope = scope;
        this.type = type;
    }

    public String getKind() {
        return this.kind;
    }

    public ParticipantId getId() {
        return this.id;
    }

    public ParticipantId getParentId() {
        return this.parentId;
    }

    public Scope getScope() {
        return this.scope;
    }

    public String getType() {
        return this.type;
    }

}

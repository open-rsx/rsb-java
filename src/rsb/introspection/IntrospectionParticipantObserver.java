/**
 * ============================================================
 *
 * This file is a part of the rsb-java project
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

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantObserver;
import rsb.RSBException;

/**
 * Observer instance connecting the creation / deconstruction of participants to
 * the introspection mechanism.
 *
 * @author swrede
 */
public class IntrospectionParticipantObserver implements ParticipantObserver {

    private final static Logger LOG = Logger
            .getLogger(IntrospectionParticipantObserver.class.getName());

    private final IntrospectionModel model;
    private ProtocolHandler protocol;

    public IntrospectionParticipantObserver() {
        this.model = new IntrospectionModel();
        this.protocol = new ProtocolHandler(this.model);
    }

    public IntrospectionParticipantObserver(final IntrospectionModel model,
            final ProtocolHandler protocol) {
        assert model != null;
        assert protocol != null;
        this.model = model;
        this.protocol = protocol;
    }

    public void activate() throws RSBException {
        assert this.protocol != null;
        this.protocol.activate();
        LOG.fine("IntrospectionParticipantObserver activated");
    }

    public void deactivate() {
        if (this.protocol != null) {
            try {
                this.protocol.deactivate();
            } catch (final RSBException e) {
                LOG.log(Level.WARNING,
                        "Exception upon deactivation of introspection protocol",
                        e);
            } catch (final InterruptedException e) {
                LOG.log(Level.WARNING,
                        "Exception upon deactivation of introspection protocol",
                        e);
            }
        }
    }

    @Override
    public void created(final Participant participant,
            final ParticipantCreateArgs<?> args) {
        if (participant.getScope().isSubScopeOf(ProtocolHandler.BASE_SCOPE)) {
            return;
        }
        synchronized (this) {
            if (!this.protocol.isActive()) {
                // lazy instantiation of protocol handler due to otherwise
                // recursive factory calls
                try {
                    this.protocol.activate();
                    this.model.setProtocolHandler(this.protocol);
                } catch (final RSBException e) {
                    LOG.log(Level.WARNING,
                            "Exception during creation of introspection protocol",
                            e);
                    // if introspection participants cannot be created, we
                    // probably have a serious problem anyway
                    this.protocol = new ProtocolHandler(this.model);
                    this.model.setProtocolHandler(this.protocol);
                    assert false;
                }
            }
        }

        this.model.addParticipant(participant, args.getParent());
    }

    @Override
    public void destroyed(final Participant participant) {
        synchronized (this) {
            if ((this.model != null)
                    && (!participant.getScope().isSubScopeOf(
                            ProtocolHandler.BASE_SCOPE))) {
                this.model.removeParticipant(participant);
            }
        }
    }

}

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
 *
 */
public class IntrospectionParticipantObserver implements ParticipantObserver {

    private final static Logger LOG = Logger.getLogger(IntrospectionParticipantObserver.class.getName());

    private final IntrospectionModel model;
    private final ProtocolHandler protocol;

    public IntrospectionParticipantObserver() {
        this.model = new IntrospectionModel();
        this.protocol = new ProtocolHandler(this.model);
        this.model.setProtocolHandler(this.protocol);
    }

    public IntrospectionParticipantObserver(final IntrospectionModel model, final ProtocolHandler protocol) {
        this.model = model;
        this.protocol = protocol;
    }

    public void activate() throws RSBException {
        if (this.protocol!=null) {
            this.protocol.activate();
            LOG.fine("IntrospectionParticipantObserver activated");
        }
    }

    public void deactivate() {
        if (this.protocol!=null) {
            try {
                this.protocol.deactivate();
            } catch (final RSBException e) {
                e.printStackTrace();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void created(final Participant participant,
            final ParticipantCreateArgs<?> args) {
        if (!participant.getScope().isSubScopeOf(ProtocolHandler.BASE_SCOPE)) {
            synchronized (this) {
                if (!this.protocol.isActive()) {
                    // lazy instantiation of protocol handler due to otherwise
                    // recursive factory calls
                    try {
                        this.protocol.activate();
                    } catch (final RSBException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    this.model.setProtocolHandler(this.protocol);
                }
            }
            if (this.model != null) {
                this.model.addParticipant(participant, args.getParent());
            }
        }

    }

    @Override
    public void destroyed(final Participant participant) {
        if ((this.model!=null) && (!participant.getScope().isSubScopeOf(ProtocolHandler.BASE_SCOPE))) {
            this.model.removeParticipant(participant);
        }
    }

}

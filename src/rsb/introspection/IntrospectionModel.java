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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import rsb.Participant;


/**
 * Implementation of RSB-based introspection protocol.
 * Supports hello, bye and survey messages.
 *
 * @author swrede
 * @author ssharma
 *
 */
public class IntrospectionModel {

    private static final Logger LOG = Logger.getLogger(IntrospectionModel.class.getName());

    private final List<ParticipantInfo> participants = new LinkedList<ParticipantInfo>();
    private final ProcessInfo processInfo;
    private ProtocolHandler protocol;
    private final HostInfo hostInfo;

    public IntrospectionModel() {
        // TODO add switch for OS-specific process model implementations
        this.processInfo = new LinuxProcessInfo();
        this.hostInfo = new LinuxHostInfo();
    }

    public void setProtocolHandler(final ProtocolHandler protocol) {
        this.protocol = protocol;
    }

    public ProcessInfo getProcessInfo() {
        return this.processInfo;
    }

    public HostInfo getHostInfo() {
        return this.hostInfo;
    }

    public List<ParticipantInfo> getParticipants() {
        return this.participants;
    }

    public ParticipantInfo getParticipant(final java.util.UUID id) {
        ParticipantInfo participant = null;
        synchronized (this.participants) {
            for (final ParticipantInfo it : this.participants) {
                if (it.getId().toString().equals(id.toString())) {
                    participant = it;
                    break;
                }
            }
        }

        if (participant == null) {
            LOG.warning("Couldn't find participant with ID: " + id);
        }
        return participant;
    }

    public void addParticipant(final Participant participant,
            final Participant parent) {
        LOG.info("Adding " + participant.getKind().toUpperCase() + " " + participant.getId() + " at " + participant.getScope() + " with parent: " + parent);
        final ParticipantInfo info =
                new ParticipantInfo(participant.getKind(), participant.getId(),
                        (parent != null ? parent.getId() : null),
                        participant.getScope(), "N/A"); // TODO Add type support
                                                        // in participants
        synchronized (this.participants) {
            this.participants.add(info);
        }
        this.protocol.sendHello(info);
    }

    public boolean removeParticipant(final Participant participant) {
        LOG.info("Removing " + participant.getKind().toUpperCase() + " " + participant.getId() + " at " + participant.getScope());
        ParticipantInfo info = null;
        synchronized (this.participants) {
            for (final Iterator<ParticipantInfo> it =
                    this.participants.iterator(); it.hasNext();) {
                final ParticipantInfo participantInfo = it.next();
                if (participantInfo.getId() == participant.getId()) {
                    info = participantInfo;
                    this.participants.remove(participantInfo);
                    break;
                }
            }
        }

        if (info != null) {
            this.protocol.sendBye(info);
        } else {
            LOG.fine("Trying to remove unknown participant " + participant);
        }

        LOG.fine(this.participants.size() + " participant(s) remain(s)");

        return !this.participants.isEmpty();
    }

}

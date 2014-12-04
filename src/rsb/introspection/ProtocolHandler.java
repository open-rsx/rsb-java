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

import java.util.logging.Logger;

import rsb.AbstractEventHandler;
import rsb.Activatable;
import rsb.Event;
import rsb.Factory;
import rsb.Informer;
import rsb.Listener;
import rsb.RSBException;
import rsb.Scope;
import rsb.Version;
import rsb.introspection.HostInfo.MACHINE_TYPE;
import rsb.patterns.LocalServer;
import rsb.protocol.introspection.ByeType.Bye;
import rsb.protocol.introspection.HelloType.Hello;
import rsb.protocol.operatingsystem.HostType.Host;
import rsb.protocol.operatingsystem.ProcessType.Process;

import com.google.protobuf.ByteString;


/**
 * Implementation of the introspection protocol
 * using RSB patterns.
 *
 * @author swrede
 * @author ssharma
 *
 */
public class ProtocolHandler extends AbstractEventHandler implements Activatable {

    private static final Logger LOG = Logger.getLogger(ProtocolHandler.class.getName());

    static final Scope BASE_SCOPE = new Scope("/__rsb/introspection");

    protected IntrospectionModel model;

    // receives introspection queries about participants
    private Listener listener;
    // broadcasts introspection information to interested RSB participants
    Informer<?> informer;
    // server to answer echo requests
    LocalServer infoServer;

    protected class EchoCallback extends rsb.patterns.EventCallback {

        @Override
        public Event invoke(final Event request) throws Throwable {
            request.getMetaData().setUserTime("request.send",
                    request.getMetaData().getSendTime());
            request.getMetaData().setUserTime("request.receive",
                    request.getMetaData().getReceiveTime());
            return request;
        }

    }

    public ProtocolHandler(final IntrospectionModel model) {
        this.model = model;
    }

    @Override
    public void activate() throws RSBException {
        // set up listener and informer pair for basic introspection protocol
        this.listener = Factory.getInstance().createListener(BASE_SCOPE.concat(new Scope("/participants/")));
        this.listener.activate();
        this.informer = Factory.getInstance().createInformer(BASE_SCOPE.concat(new Scope("/participants/")));
        this.informer.activate();

        // set up server for echo method
        final Scope serverScope= BASE_SCOPE.concat(new Scope("/hosts/" + this.model.getHostInfo().getId() + "/" + this.model.getProcessInfo().getPid()));
        this.infoServer = Factory.getInstance().createLocalServer(serverScope);
        this.infoServer.activate();
        this.infoServer.addMethod("echo", new EchoCallback());

        try {
            this.listener.addHandler(this, false);
        } catch (final InterruptedException e) {
            throw new RSBException(e);
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        if (this.listener!=null) {
            this.listener.deactivate();
        }
        if (this.informer!=null) {
            this.informer.deactivate();
        }
        if (this.infoServer!=null) {
            this.infoServer.deactivate();
        }
    }


    @Override
    public void handleEvent(final Event query) {
        LOG.fine("Processing introspection query: " + query);
        // if empty data field, either SURVEY or REQUEST
        if (query.getData()==null) {
            if (query.getMethod().equals("SURVEY")) {
                handleSurvey(query);
            } else if (query.getMethod().equals("REQUEST")) {
                handleRequest(query);
            } else {
                // Protocol error
                LOG.warning("Introspection query not understood, must be either SURVEY or REQUEST: "
                        + query);
            }
        } else if (query.getData().toString().equalsIgnoreCase("ping")) {
            // Process Ping
            // TODO check thread safety
            synchronized (this.model.getParticipants()) {
                for (final ParticipantInfo it : this.model.getParticipants()) {
                    this.sendPong(it, query);
                }
            }
        }
    }

    private void handleRequest(final Event event) {
        final String idString = event.getScope().getComponents().get(event.getScope().getComponents().size() - 1);
        final java.util.UUID id = java.util.UUID.fromString(idString);

        final ParticipantInfo participant = this.model.getParticipant(id);

        if (participant!=null) {
            this.sendHello(participant, event);
        }
    }

    private void handleSurvey(final Event event) {
        // TODO check thread safety
        synchronized (this.model.getParticipants()) {
            for (final ParticipantInfo it : this.model.getParticipants()) {
                this.sendHello(it, event);
            }
        }
    }

    void sendHello(final ParticipantInfo participant) {
        sendHello(participant, null);
    }

    void sendHello(final ParticipantInfo participant, final rsb.Event query) {

        final Hello.Builder helloBuilder = Hello.newBuilder();

        // Add participant information.
        helloBuilder.setId(ByteString.copyFrom(participant.getId()
                .toByteArray()));
        if (!(participant.getParentId() == null)) {
            helloBuilder.setParent(ByteString.copyFrom(participant
                    .getParentId().toByteArray()));
        }
        helloBuilder.setKind(participant.getKind());
        if (!participant.getType().equalsIgnoreCase("N/A")) {
            helloBuilder.setType(participant.getType());
        }
        helloBuilder.setScope(participant.getScope().toString());

        // Add process information.
        final Process.Builder processBuilder = helloBuilder.getProcessBuilder();
        processBuilder.setId(String.valueOf(this.model.getProcessInfo().getPid()));
        processBuilder.setProgramName(this.model.getProcessInfo().getProgramName());
        processBuilder.setStartTime(this.model.getProcessInfo().getStartTime());
        processBuilder.addAllCommandlineArguments(this.model.getProcessInfo().getArguments());
        processBuilder.setExecutingUser(this.model.getProcessInfo().getUserName());
        processBuilder.setRsbVersion(Version.getInstance().getVersionString());

        // Add host information.
        final Host.Builder host = helloBuilder.getHostBuilder();
        host.setId(this.model.getHostInfo().getId());
        host.setHostname(this.model.getHostInfo().getHostname());
        if (!this.model.getHostInfo().getSoftwareType()
                .equalsIgnoreCase("unkown")) {
            host.setSoftwareType(this.model.getHostInfo().getSoftwareType());
        }
        if (this.model.getHostInfo().getMachineType() != MACHINE_TYPE.UNKNOWN) {
            host.setMachineType(this.model.getHostInfo().getMachineType()
                    .name().toLowerCase());
        }
        // Get build object
        final Hello hello = helloBuilder.build();

        // Construct event.
        final Event helloEvent = new Event();
        final Scope partcipantScope =
                new Scope("/" + participant.getId().toString());
        helloEvent.setScope(this.informer.getScope().concat(partcipantScope));
        helloEvent.setData(hello);
        helloEvent.setType(hello.getClass());
        if (!(query == null)) {
            helloEvent.addCause(query.getId());
        }
        try {
            this.informer.send(helloEvent);
        } catch (final RSBException e) {
            LOG.warning("HELLO event could not be sent: " + helloEvent);
            e.printStackTrace();
        }
    }

    public void sendBye(final ParticipantInfo participant) {

        final Bye.Builder byeBuilder = Bye.newBuilder();
        // byeBuilder.setId(ByteString.copyFromUtf8(participant.getId().toString()));
        byeBuilder
                .setId(ByteString.copyFrom(participant.getId().toByteArray()));
        final Bye bye = byeBuilder.build();
        final Event e = new Event(bye.getClass(), bye);
        e.setScope(new Scope("/__rsb/introspection/participants/"
                + participant.getId()));
        try {
            this.informer.send(e);
        } catch (final RSBException exception) {
            LOG.warning("BYE event could not be send: " + e);
            exception.printStackTrace();
        }
    }

    public void sendPong(final ParticipantInfo participant, final Event query) {
        final rsb.Event pongEvent = query;
        final Scope participantScope = new Scope("/" + participant.getId().toString());
        pongEvent.setScope(this.informer.getScope().concat(participantScope));
        pongEvent.setType(String.class);
        pongEvent.setData(new String("pong"));

        try {
            this.informer.send(pongEvent);
        } catch (final RSBException e) {
            LOG.warning("Pong event could not be sent" + pongEvent);
            e.printStackTrace();
        }
    }

    @Override
    public boolean isActive() {
        if ((this.informer == null) || (this.infoServer == null)
                || (this.listener == null)) {
            return false;
        }
        return this.informer.isActive() && this.infoServer.isActive()
                && this.listener.isActive();
    }

}

/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.introspection;

import java.util.UUID;
import java.util.logging.Level;
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
import rsb.patterns.LocalServer;
import rsb.protocol.introspection.ByeType.Bye;
import rsb.protocol.introspection.HelloType.Hello;
import rsb.protocol.operatingsystem.HostType.Host;
import rsb.protocol.operatingsystem.ProcessType.Process;

import com.google.protobuf.ByteString;

/**
 * Implementation of the introspection protocol using RSB patterns. Cf.
 * http://docs
 * .cor-lab.de//rsb-manual/trunk/html/specification-introspection.html
 *
 * @author swrede
 * @author ssharma
 */
public class ProtocolHandler extends AbstractEventHandler implements
        Activatable {

    static final Scope BASE_SCOPE = new Scope("/__rsb/introspection");

    private static final Logger LOG = Logger.getLogger(ProtocolHandler.class
            .getName());

    private static final Scope PARTICIPANT_SCOPE = BASE_SCOPE.concat(new Scope(
            "/participants/"));

    private final IntrospectionModel model;

    /**
     * Receives introspection queries about participants.
     */
    private Listener queryListener;

    /**
     * Broadcasts introspection information to interested RSB participants.
     */
    private Informer<?> informer;

    /**
     * Server to answer echo requests.
     */
    private LocalServer infoServer;

    private static class EchoCallback extends rsb.patterns.EventCallback {

        @Override
        public Event invoke(final Event request) throws Throwable {
            request.getMetaData().setUserTime("request.send",
                    request.getMetaData().getSendTime());
            request.getMetaData().setUserTime("request.receive",
                    request.getMetaData().getReceiveTime());
            return request;
        }

    }

    /**
     * Creates a new instance operating on the provided introspection model.
     *
     * @param model
     *            the mode, not <code>null</code>
     */
    public ProtocolHandler(final IntrospectionModel model) {
        assert model != null;
        this.model = model;
    }

    @Override
    public void activate() throws RSBException {
        // set up listener and informer pair for basic introspection protocol
        this.queryListener =
                Factory.getInstance().createListener(PARTICIPANT_SCOPE);
        this.queryListener.activate();
        this.informer = Factory.getInstance().createInformer(PARTICIPANT_SCOPE);
        this.informer.activate();

        assert this.model.getHostInfo().getHostId() != null;
        assert this.model.getProcessInfo().getPid() != null;

        // set up server for echo method
        final Scope serverScope =
                BASE_SCOPE.concat(new Scope("/hosts/"
                        + this.model.getHostInfo().getHostId() + "/"
                        + this.model.getProcessInfo().getPid()));
        this.infoServer = Factory.getInstance().createLocalServer(serverScope);
        this.infoServer.activate();
        this.infoServer.addMethod("echo", new EchoCallback());

        try {
            this.queryListener.addHandler(this, false);
        } catch (final InterruptedException e) {
            throw new RSBException(e);
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        if (this.queryListener != null) {
            this.queryListener.deactivate();
        }
        if (this.informer != null) {
            this.informer.deactivate();
        }
        if (this.infoServer != null) {
            this.infoServer.deactivate();
        }
    }

    @Override
    public void handleEvent(final Event query) {
        LOG.log(Level.FINE, "Processing introspection query: {0}",
                new Object[] { query });
        // if empty data field, either SURVEY or REQUEST
        if (query.getData() == null) {
            if (query.getMethod().equals("SURVEY")) {
                handleSurvey(query);
            } else if (query.getMethod().equals("REQUEST")) {
                handleRequest(query);
            } else {
                // Protocol error
                LOG.log(Level.WARNING, "Introspection query not understood, "
                        + "must be either SURVEY or REQUEST: {0}",
                        new Object[] { query });
            }
        } else if (query.getData().toString().equalsIgnoreCase("ping")) {
            // Process Ping
            // TODO check thread safety
            synchronized (this.model.getParticipants()) {
                for (final ParticipantInfo info : this.model.getParticipants()) {
                    this.sendPong(info, query);
                }
            }
        }
    }

    private void handleRequest(final Event event) {
        assert event.getScope().getComponents().size() >= 1;
        final String idString =
                event.getScope().getComponents()
                        .get(event.getScope().getComponents().size() - 1);
        final UUID participantId = UUID.fromString(idString);

        final ParticipantInfo participant =
                this.model.getParticipant(participantId);

        if (participant != null) {
            this.sendHello(participant, event);
        }
    }

    private void handleSurvey(final Event event) {
        // TODO check thread safety
        synchronized (this.model.getParticipants()) {
            for (final ParticipantInfo info : this.model.getParticipants()) {
                this.sendHello(info, event);
            }
        }
    }

    void sendHello(final ParticipantInfo participant) {
        sendHello(participant, null);
    }

    void sendHello(final ParticipantInfo participant, final Event query) {

        final Hello.Builder helloBuilder = Hello.newBuilder();

        // Add participant information.
        helloBuilder.setId(ByteString.copyFrom(participant.getId()
                .toByteArray()));
        if (participant.getParentId() != null) {
            helloBuilder.setParent(ByteString.copyFrom(participant
                    .getParentId().toByteArray()));
        }
        helloBuilder.setKind(participant.getKind());
        if (participant.getDataType() != null) {
            helloBuilder.setType(participant.getDataType().getName());
        }
        helloBuilder.setScope(participant.getScope().toString());

        // Add process information.
        final Process.Builder processBuilder = helloBuilder.getProcessBuilder();
        assert this.model.getProcessInfo().getPid() != null;
        processBuilder.setId(String.valueOf(this.model.getProcessInfo()
                .getPid()));
        assert this.model.getProcessInfo().getProgramName() != null;
        processBuilder.setProgramName(this.model.getProcessInfo()
                .getProgramName());
        assert this.model.getProcessInfo().getStartTime() != null;
        processBuilder.setStartTime(this.model.getProcessInfo().getStartTime());
        if (this.model.getProcessInfo().getArguments() != null) {
            processBuilder.addAllCommandlineArguments(this.model
                    .getProcessInfo().getArguments());
        }
        if (this.model.getProcessInfo().getUserName() != null) {
            processBuilder.setExecutingUser(this.model.getProcessInfo()
                    .getUserName());
        }
        processBuilder.setRsbVersion(Version.getInstance().getVersionString());

        // Add host information.
        final Host.Builder host = helloBuilder.getHostBuilder();
        if (this.model.getHostInfo().getHostId() != null) {
            host.setId(this.model.getHostInfo().getHostId());
        }
        if (this.model.getHostInfo().getHostName() != null) {
            host.setHostname(this.model.getHostInfo().getHostName());
        } else {
            // since host name is required, we need to do something about this
            // case
            LOG.warning("No host name available to insert into Hello message. "
                    + "Using a random one.");
            host.setHostname("<unknown>");
        }
        if (this.model.getHostInfo() != null) {
            host.setSoftwareType(this.model.getHostInfo().getSoftwareType());
        }
        if (this.model.getHostInfo().getMachineType() != null) {
            host.setMachineType(this.model.getHostInfo().getMachineType());
        }
        // Get build object
        final Hello hello = helloBuilder.build();

        // Construct event.
        final Event helloEvent = new Event();
        helloEvent.setScope(PARTICIPANT_SCOPE.concat(new Scope("/"
                + participant.getId())));
        helloEvent.setData(hello);
        helloEvent.setType(hello.getClass());
        if (query != null) {
            helloEvent.addCause(query.getId());
        }
        try {
            this.informer.send(helloEvent);
        } catch (final RSBException e) {
            LOG.log(Level.WARNING, "HELLO event could not be sent.", e);
        }
    }

    public void sendBye(final ParticipantInfo participant) {

        final Bye.Builder byeBuilder = Bye.newBuilder();
        byeBuilder
                .setId(ByteString.copyFrom(participant.getId().toByteArray()));
        final Bye bye = byeBuilder.build();
        final Event event = new Event(bye.getClass(), bye);
        event.setScope(PARTICIPANT_SCOPE.concat(new Scope("/"
                + participant.getId())));
        try {
            this.informer.send(event);
        } catch (final RSBException e) {
            LOG.log(Level.WARNING, "BYE event could not be sent.", e);
        }
    }

    public void sendPong(final ParticipantInfo participant, final Event query) {
        final Event pongEvent = query;
        pongEvent.setScope(PARTICIPANT_SCOPE.concat(new Scope("/"
                + participant.getId())));
        pongEvent.setType(String.class);
        pongEvent.setData("pong");

        try {
            this.informer.send(pongEvent);
        } catch (final RSBException e) {
            LOG.log(Level.WARNING, "Pong event could not be sent", e);
        }
    }

    @Override
    public boolean isActive() {
        if ((this.informer == null) || (this.infoServer == null)
                || (this.queryListener == null)) {
            return false;
        }
        return this.informer.isActive() && this.infoServer.isActive()
                && this.queryListener.isActive();
    }

}

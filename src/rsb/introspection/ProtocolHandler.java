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

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.AbstractEventHandler;
import rsb.Activatable;
import rsb.Event;
import rsb.Factory;
import rsb.Informer;
import rsb.Listener;
import rsb.Participant;
import rsb.ParticipantId;
import rsb.RSBException;
import rsb.Scope;
import rsb.Version;
import rsb.config.ParticipantConfig;
import rsb.introspection.IntrospectionModel.IntrospectionModelObserver;
import rsb.patterns.EventCallback;
import rsb.patterns.LocalServer;
import rsb.protocol.introspection.ByeType.Bye;
import rsb.protocol.introspection.HelloType.Hello;
import rsb.protocol.operatingsystem.HostType.Host;
import rsb.protocol.operatingsystem.ProcessType.Process;
import rsb.util.OsUtilities;

import com.google.protobuf.ByteString;

/**
 * Implementation of the introspection protocol using RSB patterns. Cf.
 * http://docs
 * .cor-lab.de//rsb-manual/trunk/html/specification-introspection.html
 *
 * Activation and deactivation needs to be single-threaded and if not activated,
 * this class must not be actively registered as an
 * {@link IntrospectionModelObserver}.
 *
 * @author swrede
 * @author ssharma
 * @author jwienke
 */
public class ProtocolHandler extends AbstractEventHandler implements
        Activatable, IntrospectionModelObserver {

    private static final Logger LOG = Logger.getLogger(ProtocolHandler.class
            .getName());

    private static final Scope BASE_SCOPE = new Scope("/__rsb/introspection");
    private static final Scope PARTICIPANT_SCOPE = BASE_SCOPE.concat(new Scope(
            "/participants/"));
    private static final Scope HOST_SCOPE = BASE_SCOPE.concat(new Scope(
            "/hosts/"));

    /**
     * Data to send or expect in a ping event.
     */
    private static final String PING_EVENT_DATA = "ping";

    /**
     * Data to send or expect in a pong event.
     */
    private static final String PONG_EVENT_DATA = "pong";

    /**
     * {@link Event#getMethod()} entry to expect on a request for a specific
     * participant.
     */
    private static final String REQUEST_METHOD_NAME = "REQUEST";

    /**
     * {@link Event#getMethod()} entry to expect on a participant survey
     * request.
     */
    private static final String SURVEY_METHOD_NAME = "SURVEY";

    /**
     * Name of the RPC server method for echoing back timing information to a
     * caller.
     */
    private static final String ECHO_RPC_METHOD_NAME = "echo";

    private final IntrospectionModel model;
    private final ProcessInfo processInfo;
    private final HostInfo hostInfo;

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

    private static class EchoCallback extends EventCallback {

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

        final HostInfo info;
        switch (OsUtilities.deriveOsFamily(OsUtilities.getOsName())) {
        case LINUX:
            LOG.fine("Creating Process and CommonHostInfo instances for Linux OS.");
            this.processInfo = new LinuxProcessInfo();
            info = new LinuxHostInfo();
            break;
        default:
            LOG.fine("Creating PortableProcess and PortableHostInfo instances.");
            this.processInfo = new PortableProcessInfo();
            info = new PortableHostInfo();
            break;
        }
        this.hostInfo = new HostIdEnsuringHostInfo(info);
    }

    /**
     * Cleans up a participant, if it is not <code>null</code>, while ignoring
     * all possible exceptions.
     *
     * @param participant
     *            the participant to clean up
     */
    private void safeParticipantCleanup(final Participant participant) {
        if (participant != null) {
            try {
                participant.deactivate();
            } catch (final RSBException e) {
                // ignore this since we can't do anything
            } catch (final InterruptedException e) {
                // ignore this since we can't do anything
            }
        }
    }

    /**
     * Tries to recover the internal state to "not being active" as best as
     * possible after an error while creating or deactivating internal RSB
     * participants.
     */
    private void safeCleanup() {

        safeParticipantCleanup(this.infoServer);
        this.infoServer = null;
        safeParticipantCleanup(this.queryListener);
        this.queryListener = null;
        safeParticipantCleanup(this.informer);
        this.informer = null;

    }

    @Override
    public void activate() throws RSBException {

        try {

            // ensure that introspection participants do not end up in the
            // introspection as well, which would be a recursion
            final ParticipantConfig config =
                    Factory.getInstance().getDefaulParticipantConfig().copy();
            config.setIntrospectionEnabled(false);

            // set up listener and informer pair for basic introspection
            // protocol
            this.queryListener =
                    Factory.getInstance().createListener(PARTICIPANT_SCOPE,
                            config);
            this.queryListener.activate();
            this.informer =
                    Factory.getInstance().createInformer(PARTICIPANT_SCOPE,
                            config);
            this.informer.activate();

            assert this.hostInfo.getHostId() != null;
            assert this.processInfo.getPid() != null;

            // set up server for echo method
            final Scope serverScope =
                    HOST_SCOPE.concat(new Scope(Scope.COMPONENT_SEPARATOR
                            + this.hostInfo.getHostId()
                            + Scope.COMPONENT_SEPARATOR
                            + this.processInfo.getPid()));
            this.infoServer =
                    Factory.getInstance()
                            .createLocalServer(serverScope, config);
            this.infoServer.activate();
            this.infoServer.addMethod(ECHO_RPC_METHOD_NAME, new EchoCallback());

            try {
                this.queryListener.addHandler(this, true);
            } catch (final InterruptedException e) {
                throw new RSBException(e);
            }

        } catch (final RSBException e) {
            // reset state so we can retry activation again later
            safeCleanup();
            throw e;
        }

    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {

        try {
            // ordered correctly so that server and listener actions will still
            // have the informer available for processing
            if (this.infoServer != null) {
                this.infoServer.deactivate();
                this.infoServer.waitForShutdown();
                this.infoServer = null;
            }
            if (this.queryListener != null) {
                this.queryListener.deactivate();
                this.queryListener = null;
            }
            if (this.informer != null) {
                this.informer.deactivate();
                this.informer = null;
            }
        } catch (final RSBException e) {
            safeCleanup();
            throw e;
        } catch (final InterruptedException e) {
            safeCleanup();
            throw e;
        }

    }

    private boolean isSpecificParticipantScope(final Scope scope) {
        return scope != null
                && scope.isSubScopeOf(PARTICIPANT_SCOPE)
                && scope.getComponents().size() == PARTICIPANT_SCOPE
                        .getComponents().size() + 1;
    }

    @Override
    public void handleEvent(final Event query) {
        LOG.log(Level.FINE, "Processing introspection query: {0}",
                new Object[] { query });

        if (query.getScope().equals(PARTICIPANT_SCOPE)
                && query.getData() == null && query.getMethod() != null
                && query.getMethod().equals(SURVEY_METHOD_NAME)) {
            // survey if: on general participant scope & no data & method
            // matches
            handleSurvey(query);
        } else if (isSpecificParticipantScope(query.getScope())
                && query.getData() == null && query.getMethod() != null
                && query.getMethod().equals(REQUEST_METHOD_NAME)) {
            // request if: on specific participant scope & no data & method
            // matches
            handleRequest(query);
        } else if (query.getData() != null
                && query.getData().getClass()
                        .equals(PING_EVENT_DATA.getClass())
                && query.getData().toString().equalsIgnoreCase(PING_EVENT_DATA)) {
            // ping if: data exists & data matches ping data
            // TODO include expected scope
            synchronized (this.model) {
                for (final ParticipantInfo info : this.model.getParticipants()) {
                    this.sendPong(info, query);
                }
            }
        } else if (isSpecificParticipantScope(query.getScope())
                && query.getData() != null
                && query.getData().getClass().equals(Hello.class)) {
            // intentionally ignoring hello and bye events which we do not
            // monitor
            assert true;
        } else if (isSpecificParticipantScope(query.getScope())
                && query.getData() != null
                && query.getData().getClass().equals(Bye.class)) {
            // intentionally ignoring hello and bye events which we do not
            // monitor
            assert true;
        } else {
            // Protocol error
            LOG.log(Level.WARNING, "Introspection event not understood: {0}",
                    new Object[] { query, });
        }

    }

    private void handleRequest(final Event event) {
        assert event != null;
        assert event.getScope().isSubScopeOf(PARTICIPANT_SCOPE);

        try {

            // we expect the last scope component to be the id of the
            // participant this request is targeted at
            final String idString =
                    event.getScope().getComponents()
                            .get(event.getScope().getComponents().size() - 1);
            final ParticipantId participantId = new ParticipantId(idString);

            final ParticipantInfo participant =
                    this.model.getParticipant(participantId);

            if (participant == null) {
                // might be a legal case because requests might come in for
                // participants that have just been removed from the model
                LOG.log(Level.FINE,
                        "Not answering a request for a participant with {0} "
                                + "because it is not known in the model.",
                        new Object[] { participantId });
            } else {
                this.sendHello(participant, event);
            }

        } catch (final IllegalArgumentException e) {
            // participant ID was not parsable
            LOG.log(Level.WARNING, "Cannot handle the request {0} "
                    + "because the last component of the event scope "
                    + "cannot be parsed as a participant id. "
                    + "Ignoring this request.", new Object[] { event });
        }

    }

    private void handleSurvey(final Event event) {
        synchronized (this.model) {
            for (final ParticipantInfo info : this.model.getParticipants()) {
                this.sendHello(info, event);
            }
        }
    }

    private void sendHello(final ParticipantInfo participant) {
        sendHello(participant, null);
    }

    private void
            sendHello(final ParticipantInfo participant, final Event query) {

        final Hello.Builder helloBuilder = Hello.newBuilder();
        helloAddParticipantData(participant, helloBuilder);
        helloAddProcessInformation(this.processInfo, helloBuilder);
        helloAddHostInformation(this.hostInfo, helloBuilder);

        final Hello hello = helloBuilder.build();

        // Construct event.
        final Event helloEvent = new Event();
        helloEvent.setScope(participantScope(participant));
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

    /**
     * Creates a scope to send data messages on that regard a specific
     * participant.
     *
     * @param participant
     *            the participant to create the scope for
     * @return a scope related to the participant, subscope of
     *         {@link #PARTICIPANT_SCOPE}.
     */
    private Scope participantScope(final ParticipantInfo participant) {
        assert participant != null;
        return PARTICIPANT_SCOPE.concat(new Scope(Scope.COMPONENT_SEPARATOR
                + participant.getId()));
    }

    /**
     * Adds host-related information to a hello message.
     *
     * @param hostInfo
     *            host information provider
     * @param helloBuilder
     *            the hello message builder to fill
     */
    private void helloAddHostInformation(final HostInfo hostInfo,
            final Hello.Builder helloBuilder) {
        assert hostInfo != null;
        assert helloBuilder != null;

        final Host.Builder host = helloBuilder.getHostBuilder();
        assert hostInfo.getHostId() != null : "We must have a host id "
                + "since we enforced this with a facade class";
        host.setId(hostInfo.getHostId());
        if (hostInfo.getHostName() != null) {
            host.setHostname(hostInfo.getHostName());
        } else {
            // since host name is required, we need to do something about this
            // case
            LOG.warning("Replacing host name with host ID "
                    + "since the host name is not known.");
            host.setHostname(hostInfo.getHostId());

        }
        if (hostInfo.getSoftwareType() != null) {
            host.setSoftwareType(hostInfo.getSoftwareType());
        }
        if (hostInfo.getMachineType() != null) {
            host.setMachineType(hostInfo.getMachineType());
        }

    }

    /**
     * Adds process-related information to a hello message.
     *
     * @param processInfo
     *            the process information source
     * @param helloBuilder
     *            the builder for the hello message to use
     */
    private void helloAddProcessInformation(final ProcessInfo processInfo,
            final Hello.Builder helloBuilder) {
        assert processInfo != null;
        assert helloBuilder != null;

        final Process.Builder processBuilder = helloBuilder.getProcessBuilder();
        assert processInfo.getPid() != null;
        processBuilder.setId(String.valueOf(processInfo.getPid()));
        assert processInfo.getProgramName() != null;
        processBuilder.setProgramName(processInfo.getProgramName());
        assert processInfo.getStartTime() != null;
        processBuilder.setStartTime(processInfo.getStartTime());
        if (processInfo.getArguments() != null) {
            processBuilder.addAllCommandlineArguments(processInfo
                    .getArguments());
        }
        if (processInfo.getUserName() != null) {
            processBuilder.setExecutingUser(processInfo.getUserName());
        }
        processBuilder.setRsbVersion(Version.getInstance().getVersionString());

    }

    /**
     * Adds participant-related information to a hello message.
     *
     * @param participant
     *            the participant to represent
     * @param helloBuilder
     *            the message builder to use for the new message
     */
    private void helloAddParticipantData(final ParticipantInfo participant,
            final Hello.Builder helloBuilder) {
        assert participant != null;
        assert helloBuilder != null;

        helloBuilder.setId(participantIdAsByteString(participant));
        if (participant.getParentId() != null) {
            helloBuilder.setParent(ByteString.copyFrom(participant
                    .getParentId().toByteArray()));
        }
        helloBuilder.setKind(participant.getKind());
        if (participant.getDataType() != null) {
            helloBuilder.setType(participant.getDataType().getName());
        }
        helloBuilder.setScope(participant.getScope().toString());

    }

    private ByteString participantIdAsByteString(
            final ParticipantInfo participant) {
        return ByteString.copyFrom(participant.getId().toByteArray());
    }

    private void sendBye(final ParticipantInfo participant) {

        final Bye.Builder byeBuilder = Bye.newBuilder();
        byeBuilder.setId(participantIdAsByteString(participant));
        final Bye bye = byeBuilder.build();
        final Event event = new Event(bye.getClass(), bye);
        event.setScope(participantScope(participant));
        try {
            this.informer.send(event);
        } catch (final RSBException e) {
            LOG.log(Level.WARNING, "BYE event could not be sent.", e);
        }
    }

    private void sendPong(final ParticipantInfo participant, final Event query) {
        assert query != null;

        final Event pongEvent = new Event();
        pongEvent.setScope(participantScope(participant));
        pongEvent.setType(String.class);
        pongEvent.setData(PONG_EVENT_DATA);
        pongEvent.addCause(query.getId());

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

    @Override
    public void participantAdded(final ParticipantInfo info) {
        sendHello(info);
    }

    @Override
    public void participantRemoved(final ParticipantInfo info) {
        sendBye(info);
    }

}

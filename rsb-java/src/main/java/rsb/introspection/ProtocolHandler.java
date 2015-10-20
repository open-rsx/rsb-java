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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

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
import rsb.util.os.HostInfo;
import rsb.util.os.HostInfoSelector;
import rsb.util.os.ProcessInfo;
import rsb.util.os.ProcessInfoSelector;

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
// the protocol is quite complex and it is hard to factor out details into
// specific classes
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public class ProtocolHandler extends AbstractEventHandler implements
        Activatable, IntrospectionModelObserver {

    private static final Logger LOG = Logger.getLogger(ProtocolHandler.class
            .getName());

    private final IntrospectionModel model;
    private final ProcessInfo processInfo;
    private final HostInfo hostInfo;
    private final String processDisplayName;

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

    /**
     * Set of actions to check when a new event is received on the introspection
     * scope.
     */
    private final Set<EventAction> eventActions = new HashSet<EventAction>();

    private static class EchoCallback extends EventCallback {

        @Override
        public Event invoke(final Event request) {
            request.getMetaData().setUserTime(
                    ProtocolUtilities.REQUEST_SEND_USER_TIME,
                    request.getMetaData().getSendTime());
            request.getMetaData().setUserTime(
                    ProtocolUtilities.REQUEST_RECEIVE_USER_TIME,
                    request.getMetaData().getReceiveTime());
            return request;
        }

    }

    /**
     * Implementations encapsulate individual reactions to events received from
     * the introspection listener.
     *
     * @author jwienke
     */
    private interface EventAction {

        /**
         * Predicate function to determine whether a certain action applies.
         *
         * @param event
         *            introspection event to test against
         * @return <code>true</code> if this action shall be executed for the
         *         specified event
         */
        boolean matches(Event event);

        /**
         * Actually perform the action.
         *
         * @param event
         *            the event to perform the action for
         */
        void perform(Event event);

    }

    /**
     * Reply to a survey request.
     *
     * @author jwienke
     */
    private class ReplyToSurveyAction implements EventAction {

        @Override
        public boolean matches(final Event event) {
            return event.getScope().equals(ProtocolUtilities.PARTICIPANT_SCOPE)
                    && event.getData() == null
                    && event.getMethod() != null
                    && event.getMethod().equals(
                            ProtocolUtilities.SURVEY_METHOD_NAME);
        }

        @Override
        public void perform(final Event event) {
            handleSurvey(event);
        }

    }

    /**
     * Reply to a request for a specific participant.
     *
     * @author jwienke
     */
    private class ReplyToParticipantRequestAction implements EventAction {

        @Override
        public boolean matches(final Event event) {
            return ProtocolUtilities.isSpecificParticipantScope(event
                    .getScope())
                    && event.getData() == null
                    && event.getMethod() != null
                    && event.getMethod().equals(
                            ProtocolUtilities.REQUEST_METHOD_NAME);
        }

        @Override
        public void perform(final Event event) {
            handleRequest(event);
        }

    }

    /**
     * Sends a pong for all participants in case of a global ping request.
     *
     * @author jwienke
     */
    private class GlobalPongAction implements EventAction {

        @Override
        public boolean matches(final Event event) {
            return event.getScope().equals(ProtocolUtilities.PARTICIPANT_SCOPE)
                    && event.getData() != null
                    && event.getData()
                            .getClass()
                            .equals(ProtocolUtilities.PING_EVENT_DATA
                                    .getClass())
                    && event.getData()
                            .toString()
                            .equalsIgnoreCase(ProtocolUtilities.PING_EVENT_DATA);
        }

        @Override
        public void perform(final Event event) {
            synchronized (ProtocolHandler.this.model) {
                for (final ParticipantInfo info : ProtocolHandler.this.model
                        .getParticipants()) {
                    sendPong(info, event);
                }
            }
        }

    }

    /**
     * Action to intentionally ignore Hello events as we do not monitor external
     * participants.
     *
     * @author jwienke
     */
    private class IgnoreHelloEventAction implements EventAction {

        @Override
        public boolean matches(final Event event) {
            return ProtocolUtilities.isSpecificParticipantScope(event
                    .getScope())
                    && event.getData() != null
                    && event.getData().getClass().equals(Hello.class);
        }

        @Override
        public void perform(final Event event) {
            // do nothing
        }

    }

    /**
     * Action to intentionally ignore Bye events as we do not monitor external
     * participants.
     *
     * @author jwienke
     */
    private class IgnoreByeEventAction implements EventAction {

        @Override
        public boolean matches(final Event event) {
            return ProtocolUtilities.isSpecificParticipantScope(event
                    .getScope())
                    && event.getData() != null
                    && event.getData().getClass().equals(Bye.class);
        }

        @Override
        public void perform(final Event event) {
            // do nothing
        }

    }

    /**
     * Creates a new instance operating on the provided introspection model.
     *
     * @param model
     *            the mode, not <code>null</code>
     * @param processDisplayName
     *            human-readable name of the process this instance operates in,
     *            may be <code>null</code> if not provided
     * @throws LacksOsInformationException
     *             thrown in case required information from the operating system
     *             are not available. This makes the introspection unusable.
     */
    public ProtocolHandler(final IntrospectionModel model,
            final String processDisplayName) {
        assert model != null;
        this.model = model;

        this.processDisplayName = processDisplayName;

        // select the host and process information providers
        try {
            this.hostInfo =
                    new HostIdEnsuringHostInfo(HostInfoSelector.getHostInfo());
        } catch (final IllegalArgumentException e) {
            throw new LacksOsInformationException(
                    "Host information lacks required information.", e);
        }
        this.processInfo = ProcessInfoSelector.getProcessInfo();

        // check whether all required pieces of information for the process.
        // Host information have already been checked by the constructor of
        // HostIdEnsuringHostInfo.
        if (this.processInfo.getPid() == null) {
            throw new LacksOsInformationException(
                    "No PID information available.");
        }
        if (this.processInfo.getProgramName() == null) {
            throw new LacksOsInformationException("No program name available.");
        }
        if (this.processInfo.getStartTime() == null) {
            throw new LacksOsInformationException(
                    "No process start time available.");
        }

        // register known actions to perform for introspection events
        this.eventActions.add(new ReplyToSurveyAction());
        this.eventActions.add(new ReplyToParticipantRequestAction());
        this.eventActions.add(new GlobalPongAction());
        this.eventActions.add(new IgnoreHelloEventAction());
        this.eventActions.add(new IgnoreByeEventAction());

    }

    /**
     * Cleans up a participant, if it is not <code>null</code>, while ignoring
     * all possible exceptions.
     *
     * @param participant
     *            the participant to clean up
     */
    // we don't want to let out anything here
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void safeParticipantCleanup(final Participant participant) {
        if (participant != null) {
            try {
                participant.deactivate();
            } catch (final RSBException e) {
                // ignore this since we can't do anything
            } catch (final InterruptedException e) {
                // pass to external code to preserve interruption state
                // cf. http://www.ibm.com/developerworks/library/j-jtp05236/
                Thread.currentThread().interrupt();
            } catch (final RuntimeException e) {
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
                    Factory.getInstance().getDefaultParticipantConfig().copy();
            config.setIntrospectionEnabled(false);

            // set up listener and informer pair for basic introspection
            // protocol
            this.queryListener =
                    Factory.getInstance().createListener(
                            ProtocolUtilities.PARTICIPANT_SCOPE, config);
            this.queryListener.activate();
            this.informer =
                    Factory.getInstance().createInformer(
                            ProtocolUtilities.PARTICIPANT_SCOPE, config);
            this.informer.activate();

            assert this.hostInfo.getHostId() != null;
            assert this.processInfo.getPid() != null;

            // set up server for echo method
            final Scope serverScope =
                    ProtocolUtilities.HOST_SCOPE.concat(new Scope(
                            Scope.COMPONENT_SEPARATOR
                                    + this.hostInfo.getHostId()
                                    + Scope.COMPONENT_SEPARATOR
                                    + this.processInfo.getPid()));
            this.infoServer =
                    Factory.getInstance()
                            .createLocalServer(serverScope, config);
            this.infoServer.activate();
            this.infoServer.addMethod(ProtocolUtilities.ECHO_RPC_METHOD_NAME,
                    new EchoCallback());

            this.queryListener.addHandler(this, true);

        } catch (final InterruptedException e) {
            // reset state so we can retry activation again later
            safeCleanup();
            // cf. http://www.ibm.com/developerworks/library/j-jtp05236/
            Thread.currentThread().interrupt();
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

    @Override
    public void handleEvent(final Event query) {
        LOG.log(Level.FINE, "Processing introspection query: {0}",
                new Object[] { query });

        for (final EventAction action : this.eventActions) {
            if (action.matches(query)) {
                action.perform(query);
                return;
            }
        }

        LOG.log(Level.WARNING, "Introspection event not understood: {0}",
                new Object[] { query });

    }

    private void handleRequest(final Event event) {
        assert event != null;
        assert event.getScope().isSubScopeOf(
                ProtocolUtilities.PARTICIPANT_SCOPE);

        try {

            final ParticipantId participantId =
                    ProtocolUtilities.participantIdFromScope(event.getScope());

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
        helloAddProcessInformation(this.processInfo, this.processDisplayName,
                helloBuilder);
        helloAddHostInformation(this.hostInfo, helloBuilder);

        final Hello hello = helloBuilder.build();

        // Construct event.
        final Event helloEvent = new Event();
        helloEvent.setScope(ProtocolUtilities.participantScope(participant));
        helloEvent.setData(hello);
        helloEvent.setType(hello.getClass());
        if (query != null) {
            helloEvent.addCause(query.getId());
        }

        try {
            this.informer.publish(helloEvent);
        } catch (final RSBException e) {
            LOG.log(Level.WARNING, "HELLO event could not be sent.", e);
        }

    }

    /**
     * Adds host-related information to a hello message.
     *
     * @param hostInfo
     *            host information provider
     * @param helloBuilder
     *            the hello message builder to fill
     */
    private static void helloAddHostInformation(final HostInfo hostInfo,
            final Hello.Builder helloBuilder) {
        assert hostInfo != null;
        assert helloBuilder != null;

        final Host.Builder host = helloBuilder.getHostBuilder();
        assert hostInfo.getHostId() != null : "We must have a host id "
                + "since we enforced this with a facade class";
        host.setId(hostInfo.getHostId());
        if (hostInfo.getHostName() == null) {
            // since host name is required, we need to do something about this
            // case
            LOG.warning("Replacing host name with host ID "
                    + "since the host name is not known.");
            host.setHostname(hostInfo.getHostId());
        } else {
            host.setHostname(hostInfo.getHostName());
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
     * @param processDisplayName
     *            human-readable name of the process this instance operates in,
     *            may be <code>null</code> if not provided
     * @param helloBuilder
     *            the builder for the hello message to use
     */
    private static void helloAddProcessInformation(
            final ProcessInfo processInfo, final String processDisplayName,
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
        if (processDisplayName != null) {
            processBuilder.setDisplayName(processDisplayName);
        }

    }

    /**
     * Adds participant-related information to a hello message.
     *
     * @param participant
     *            the participant to represent
     * @param helloBuilder
     *            the message builder to use for the new message
     */
    private static void
            helloAddParticipantData(final ParticipantInfo participant,
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
        for (final URI uri : participant.getTransportUris()) {
            helloBuilder.addTransport(uri.toString());
        }

    }

    private static ByteString participantIdAsByteString(
            final ParticipantInfo participant) {
        return ByteString.copyFrom(participant.getId().toByteArray());
    }

    private void sendBye(final ParticipantInfo participant) {

        final Bye.Builder byeBuilder = Bye.newBuilder();
        byeBuilder.setId(participantIdAsByteString(participant));
        final Bye bye = byeBuilder.build();
        final Event event = new Event(bye.getClass(), bye);
        event.setScope(ProtocolUtilities.participantScope(participant));
        try {
            this.informer.publish(event);
        } catch (final RSBException e) {
            LOG.log(Level.WARNING, "BYE event could not be sent.", e);
        }
    }

    private void sendPong(final ParticipantInfo participant, final Event query) {
        assert query != null;

        final Event pongEvent = new Event();
        pongEvent.setScope(ProtocolUtilities.participantScope(participant));
        pongEvent.setType(String.class);
        pongEvent.setData(ProtocolUtilities.PONG_EVENT_DATA);
        pongEvent.addCause(query.getId());

        try {
            this.informer.publish(pongEvent);
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

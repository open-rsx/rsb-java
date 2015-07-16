/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
package rsb;

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.config.TransportConfig;
import rsb.converter.DefaultConverterRepository;
import rsb.eventprocessing.DefaultOutRouteConfigurator;
import rsb.eventprocessing.OutRouteConfigurator;
import rsb.transport.SequenceNumber;
import rsb.transport.TransportRegistry;

/**
 * This class offers a method to publish events to a channel, reaching all
 * participating Listeners. This n:m-communication is one of the basic
 * communication patterns offered by RSB.
 *
 * @author swrede
 * @author rgaertne
 * @author jschaefe
 * @author jwienke
 * @param <DataType>
 *            The type of data sent by this informer
 */
public class Informer<DataType extends Object> extends Participant {

    private static final Logger LOG = Logger
            .getLogger(Informer.class.getName());

    /** Contains the state instance currently being active. */
    // false positive due to use in private state classes
    @SuppressWarnings("PMD.ImmutableField")
    private InformerState<DataType> state;

    /** An atomic uint32 counter object for event sequence numbers. */
    private final SequenceNumber sequenceNumber = new SequenceNumber();

    /** The default data type for this informer. */
    private Class<?> type;

    private final OutRouteConfigurator router;

    /**
     * {@link InformerState} in case the informer is inactive.
     *
     * @author swrede
     */
    protected class InformerStateInactive extends InformerState<DataType> {

        /**
         * Constructor.
         *
         * @param ctx
         *            the host informer
         */
        protected InformerStateInactive(final Informer<DataType> ctx) {
            super(ctx);
        }

        @Override
        protected void activate() throws RSBException {
            this.getInformer().state = new InformerStateActive(
                    this.getInformer());
            LOG.log(Level.FINE,
                    "Informer activated: [Scope={0}, Type={1}]",
                    new Object[] { Informer.this.getScope(),
                            Informer.this.type.getName() });
            try {
                Informer.this.router.activate();
            } catch (final RSBException e) {
                throw new InitializeException(e);
            }
            Informer.super.activate();
        }

    }

    /**
     * {@link InformerState} in case the informer is active.
     *
     * @author swrede
     */
    protected class InformerStateActive extends InformerState<DataType> {

        /**
         * Constructor.
         *
         * @param ctx
         *            the host informer
         */
        protected InformerStateActive(final Informer<DataType> ctx) {
            super(ctx);
        }

        @Override
        protected void deactivate() throws RSBException, InterruptedException {
            Informer.super.deactivate();
            Informer.this.router.deactivate();
            this.getInformer().state = new InformerStateInactive(
                    this.getInformer());
            LOG.log(Level.FINE,
                    "Informer deactivated: [Scope={0}, Type={1}]",
                    new Object[] { Informer.this.getScope(),
                            Informer.this.type.getName() });
        }

        @Override
        protected Event send(final Event event) throws RSBException {

            if (event.getScope() == null) {
                throw new IllegalArgumentException(
                        "Event scope must not be null.");
            }
            if (!Informer.this.getScope().equals(event.getScope())
                    && !Informer.this.getScope().isSuperScopeOf(
                            event.getScope())) {
                throw new IllegalArgumentException(
                        "Event scope not a sub-scope of informer scope.");
            }
            if (!this.getInformer().getTypeInfo()
                    .isAssignableFrom(event.getType())) {
                throw new IllegalArgumentException(
                        "Type of event data does not match nor "
                                + "is a sub-class of the Informer data type.");
            }

            // set participant metadata
            // increment atomic counter
            event.setId(Informer.this.getId(),
                    Informer.this.sequenceNumber.incrementAndGet());

            // send to transport(s)
            Informer.this.router.publishSync(event);

            // return event for local use
            return event;

        }

        @Override
        protected Event send(final DataType data) throws RSBException {
            return this.send(new Event(Informer.this.getScope(), data
                    .getClass(), data));
        }

    }

    /**
     * Creates an informer for a specific data type with a given scope and with
     * a specified config.
     *
     * @param args
     *            arguments used to create the new instance
     * @throws InitializeException
     *             error initializing the informer
     */
    Informer(final InformerCreateArgs args) throws InitializeException {
        super(args);

        if (args.getType() == null) {
            throw new IllegalArgumentException(
                    "Informer type must not be null.");
        }

        this.type = args.getType();

        // TODO this should be passed in from the outside?
        this.router = new DefaultOutRouteConfigurator(getScope());
        for (final TransportConfig transportConfig : getConfig()
                .getEnabledTransports()) {
            this.router.addConnector(TransportRegistry
                    .getDefaultInstance()
                    .getFactory(transportConfig.getName())
                    .createOutConnector(
                            transportConfig.getOptions(),
                            transportConfig.getConverters(
                                    DefaultConverterRepository
                                            .getDefaultConverterRepository())
                                    .getConvertersForSerialization()));
        }
        this.state = new InformerStateInactive(this);
        LOG.fine("New informer instance created: [Scope:" + this.getScope()
                + ",State:Inactive,Type:" + this.type.getName() + "]");

    }

    @Override
    public void activate() throws RSBException {
        synchronized (this) {
            this.state.activate();
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this) {
            this.state.deactivate();
        }
    }

    /**
     * Send an {@link Event} to all subscribed participants.
     *
     * @param event
     *            the event to send
     * @return modified event with set timing information
     * @throws RSBException
     *             error sending event
     * @throws IllegalArgumentException
     *             if the event is not complete or does not match the type or
     *             scope settings of the informer
     * @deprecated Use {@link #publish(Event)} instead to be consistent across
     *             implementations
     */
    @Deprecated
    public Event send(final Event event) throws RSBException {
        return publish(event);
    }

    /**
     * Publish an {@link Event} to all subscribed participants.
     *
     * @param event
     *            the event to send
     * @return modified event with set timing information
     * @throws RSBException
     *             error sending event
     * @throws IllegalArgumentException
     *             if the event is not complete or does not match the type or
     *             scope settings of the informer
     */
    public Event publish(final Event event) throws RSBException {
        synchronized (this) {
            return this.state.send(event);
        }
    }

    /**
     * Send data (of type DataType) to all subscribed participants.
     *
     * @param data
     *            data to send with default setting from the informer
     * @return generated event
     * @throws RSBException
     *             error sending event
     * @deprecated Use {@link #publish(Object)} instead to be consistent across
     *             implementations
     */
    @Deprecated
    public Event send(final DataType data) throws RSBException {
        return publish(data);
    }

    /**
     * Publish data (of type DataType) to all subscribed participants.
     *
     * @param data
     *            data to send with default setting from the informer
     * @return generated event
     * @throws RSBException
     *             error sending event
     */
    public Event publish(final DataType data) throws RSBException {
        synchronized (this) {
            return this.state.send(data);
        }
    }

    /**
     * Returns the class describing the type of data sent by this informer.
     *
     * @return class
     */
    public Class<?> getTypeInfo() {
        return this.type;
    }

    /**
     * Set the class object describing the type of data sent by this informer.
     *
     * @param typeInfo
     *            a {@link Class} instance describing the sent data
     */
    public void setTypeInfo(final Class<?> typeInfo) {
        this.type = typeInfo;
    }

    @Override
    public boolean isActive() {
        return this.state.getClass() == InformerStateActive.class;
    }

    @Override
    public String getKind() {
        return "informer";
    }

    @Override
    public Class<?> getDataType() {
        return this.type;
    }

}

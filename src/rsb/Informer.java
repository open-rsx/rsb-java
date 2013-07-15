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

import java.util.logging.Logger;

import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.DefaultConverterRepository;
import rsb.eventprocessing.DefaultOutRouteConfigurator;
import rsb.eventprocessing.OutRouteConfigurator;
import rsb.transport.SequenceNumber;
import rsb.transport.TransportFactory;
import rsb.transport.TransportRegistry;
import rsb.util.Properties;

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

    private final static Logger LOG = Logger
            .getLogger(Informer.class.getName());

    /** state variable for publisher instance */
    protected InformerState<DataType> state;

    /** atomic uint32 counter object for event sequence numbers */
    protected SequenceNumber sequenceNumber = new SequenceNumber();

    /** converter repository for type mappings */
    protected ConverterSelectionStrategy<?> converter;

    /** default data type for this publisher */
    protected Class<?> type;

    private OutRouteConfigurator router;

    protected class InformerStateInactive extends InformerState<DataType> {

        protected InformerStateInactive(final Informer<DataType> ctx) {
            super(ctx);
            LOG.fine("Informer state activated: [Scope:"
                    + Informer.this.getScope() + ",State:Inactive,Type:"
                    + Informer.this.type.getName() + "]");
        }

        @Override
        protected void activate() throws InitializeException {
            // TODO check where to put the type mangling java type to wire type
            // probably this should be translated in the respective converter
            // itself
            // however: this information is part of the notification! so, that
            // is a problem
            Informer.this.converter = DefaultConverterRepository
                    .getDefaultConverterRepository()
                    .getConvertersForSerialization();
            this.ctx.state = new InformerStateActive(this.ctx);
            LOG.fine("Informer activated: [Scope:" + Informer.this.getScope()
                    + ",Type:" + Informer.this.type.getName() + "]");
            try {
                Informer.this.router.activate();
            } catch (final RSBException e) {
                throw new InitializeException(e);
            }
        }

    }

    protected class InformerStateActive extends InformerState<DataType> {

        protected InformerStateActive(final Informer<DataType> ctx) {
            super(ctx);
            LOG.fine("Informer state activated: [Scope:"
                    + Informer.this.getScope() + ",State:Active,Type:"
                    + Informer.this.type.getName() + "]");
        }

        @Override
        protected void deactivate() throws RSBException {
            Informer.this.router.deactivate();
            this.ctx.state = new InformerStateInactive(this.ctx);
            LOG.fine("Informer deactivated: [Scope:" + Informer.this.getScope()
                    + ",Type:" + Informer.this.type.getName() + "]");
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
            if (!this.ctx.getTypeInfo().isAssignableFrom(event.getType())) {
                throw new IllegalArgumentException(
                        "Type of event data does not match nor is a sub-class of the Informer data type.");
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

    private void initMembers(final Class<?> type, final Properties properties)
            throws InitializeException {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Informer type must not be null.");
        }
        this.type = type;
        // TODO this should be passed in from the outside?
        this.router = new DefaultOutRouteConfigurator(getScope());
        this.router.addConnector(getTransportFactory().createOutConnector(
                properties));
        this.state = new InformerStateInactive(this);
        LOG.fine("New informer instance created: [Scope:" + this.getScope()
                + ",State:Inactive,Type:" + type.getName() + "]");
    }

    Informer(final String scope, final Properties properties)
            throws InitializeException {
        super(scope, TransportRegistry.getDefaultInstance()
                .getFactory("spread"));
        this.initMembers(Object.class, properties);
    }

    Informer(final Scope scope, final Properties properties)
            throws InitializeException {
        super(scope, TransportRegistry.getDefaultInstance()
                .getFactory("spread"));
        this.initMembers(Object.class, properties);
    }

    Informer(final String scope, final Class<?> type,
            final Properties properties) throws InitializeException {
        super(scope, TransportRegistry.getDefaultInstance()
                .getFactory("spread"));
        this.initMembers(type, properties);
    }

    Informer(final Scope scope, final Class<?> type, final Properties properties)
            throws InitializeException {
        super(scope, TransportRegistry.getDefaultInstance()
                .getFactory("spread"));
        this.initMembers(type, properties);
    }

    Informer(final String scope, final TransportFactory tfac,
            final Properties properties) throws InitializeException {
        super(scope, tfac);
        this.initMembers(Object.class, properties);
    }

    Informer(final Scope scope, final TransportFactory tfac,
            final Properties properties) throws InitializeException {
        super(scope, tfac);
        this.initMembers(Object.class, properties);
    }

    Informer(final String scope, final Class<?> type,
            final TransportFactory tfac, final Properties properties)
            throws InitializeException {
        super(scope, tfac);
        this.initMembers(type, properties);
    }

    Informer(final Scope scope, final Class<?> type,
            final TransportFactory tfac, final Properties properties)
            throws InitializeException {
        super(scope, tfac);
        this.initMembers(type, properties);
    }

    @Override
    public void activate() throws InitializeException {
        synchronized (this) {
            this.state.activate();
        }
    }

    @Override
    public void deactivate() throws RSBException {
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
     */
    public Event send(final Event event) throws RSBException {
        synchronized (this) {
            return this.state.send(event);
        }
    }

    /**
     * Send data (of type <T>) to all subscribed participants.
     *
     * @param data
     *            data to send with default setting from the informer
     * @return generated event
     * @throws RSBException
     *             error sending event
     */
    public Event send(final DataType data) throws RSBException {
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

}

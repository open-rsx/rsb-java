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
import rsb.transport.PortConfiguration;
import rsb.transport.SequenceNumber;
import rsb.transport.TransportFactory;

/**
 * This class offers a method to publish events to a channel, reaching all
 * participating Listeners. This n:m-communication is one of the basic
 * communication patterns offered by RSB.
 *
 * @author swrede
 * @author rgaertne
 * @author jschaefe
 * @author jwienke
 */
public class Informer<T extends Object> extends Participant {

	private final static Logger LOG = Logger
			.getLogger(Informer.class.getName());

	/** state variable for publisher instance */
	protected InformerState<T> state;

	/** atomic uint32 counter object for event sequence numbers */
	protected SequenceNumber sequenceNumber = new SequenceNumber();

	/** converter repository for type mappings */
	protected ConverterSelectionStrategy<?> converter;

	/** default data type for this publisher */
	protected Class<?> type;

	protected class InformerStateInactive extends InformerState<T> {

		protected InformerStateInactive(final Informer<T> ctx) {
			super(ctx);
			LOG.fine("Informer state activated: [Scope:" + getScope()
					+ ",State:Inactive,Type:" + type.getName() + "]");
		}

		protected void activate() throws InitializeException {
			// TODO check where to put the type mangling java type to wire type
			// probably this should be translated in the respective converter itself
			// however: this information is part of the notification! so, that is a problem
			converter = DefaultConverterRepository.getDefaultConverterRepository().getConvertersForSerialization();
			getRouter().activate();
			ctx.state = new InformerStateActive(ctx);
			LOG.fine("Informer activated: [Scope:" + getScope() + ",Type:"
					+ type.getName() + "]");
		}

	}

	protected class InformerStateActive extends InformerState<T> {

		protected InformerStateActive(final Informer<T> ctx) {
			super(ctx);
			LOG.fine("Informer state activated: [Scope:" + getScope()
					+ ",State:Active,Type:" + type.getName() + "]");
		}

		protected void deactivate() {
			getRouter().deactivate();
			ctx.state = new InformerStateInactive(ctx);
			LOG.fine("Informer deactivated: [Scope:" + getScope() + ",Type:"
					+ type.getName() + "]");
		}

		protected Event send(final Event event) throws RSBException {

			if (event.getScope()==null) {
				throw new IllegalArgumentException(
						"Event scope must not be null.");
			}
			if (!getScope().equals(event.getScope())) {
				if (!getScope().isSuperScopeOf(event.getScope())) {
					throw new IllegalArgumentException(
							"Event scope not a sub-scope of informer scope.");
				}
			}
			if (!ctx.getTypeInfo().isAssignableFrom(event.getType())) {
			    throw new IllegalArgumentException(
						"Type of event data does not match nor is a sub-class of the Informer data type.");
			}

			// set participant metadata
			// increment atomic counter
			event.setId(getId(), sequenceNumber.incrementAndGet());

			// send to transport(s)
			getRouter().publishSync(event);

			// return event for local use
			return event;

		}

		protected Event send(final T data) throws RSBException {
			final Event event = new Event(getScope(), data.getClass(), (Object) data);
			return send(event);
		}

	}

	private void initMembers(final Class<?> type) {
	    if (type == null) {
	        throw new IllegalArgumentException("Informer type must not be null.");
	    }
		this.type = type;
		state = new InformerStateInactive(this);
		LOG.fine("New informer instance created: [Scope:" + getScope()
				+ ",State:Inactive,Type:" + type.getName() + "]");
	}

	Informer(String scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers(Object.class);
	}

	Informer(final Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers(Object.class);
	}

	Informer(String scope, final Class<?> type) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers(type);
	}

	Informer(final Scope scope, final Class<?> type) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers(type);
	}

	Informer(String scope, final TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers(Object.class);
	}

	Informer(final Scope scope, final TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers(Object.class);
	}

	Informer(String scope, final Class<?> type, final TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers(type);
	}

	Informer(final Scope scope, final Class<?> type, final TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers(type);
	}

	public synchronized void activate() throws InitializeException {
		state.activate();
	}

	public synchronized void deactivate() {
		state.deactivate();
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
	public synchronized Event send(final Event event) throws RSBException {
		return state.send(event);
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
	public synchronized Event send(final T data) throws RSBException {
		return state.send(data);
	}

	/**
	 * Returns the class describing the type of data sent by this informer.
	 *
	 * @return class
	 */
	public Class<?> getTypeInfo() {
		return type;
	}

	/**
	 * Set the class object describing the type of data sent by this informer.
	 */
	public void setTypeInfo(final Class<?> typeInfo) {
		type = typeInfo;
	}

	@Override
	public boolean isActive() {
		return state.getClass() == InformerStateActive.class;
	}

}

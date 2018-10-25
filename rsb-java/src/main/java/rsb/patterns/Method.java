/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.patterns;

import java.util.List;

import rsb.Activatable;
import rsb.Factory;
import rsb.Informer;
import rsb.Listener;
import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.RSBException;

/**
 * Objects of this class are methods which are associated to a local or remote
 * server. Within a server, each method has a unique name.
 *
 * This class is primarily intended as a superclass for local and remote method
 * classes.
 *
 * This class manages the {@link Listener} and {@link Informer} instances used
 * to communicate between methods as well as a state pattern for activating
 * them.
 *
 * @author jmoringe
 * @author swrede
 */
// false positive, lacks methods from Participant
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class Method extends Participant {

    private final Factory factory;
    private Informer<?> informer;
    private Listener listener;
    private State state;

    /**
     * Abstract base class for states of a {@link Method} based on the state
     * pattern.
     *
     * @author jwienke
     */
    private abstract class State extends Activatable.State {
        // pull into own namespace for nice class names
    }

    /**
     * Represents the state of a method that is currently active.
     */
    private class StateActive extends State {

        @Override
        public void deactivate() throws RSBException, InterruptedException {
            Method.super.deactivate();
            // Deactivate informer and listener if necessary.
            if (Method.this.getListener() != null) {
                Method.this.getListener().deactivate();
                Method.this.setListener(null);
            }
            if (Method.this.getInformer() != null) {
                Method.this.getInformer().deactivate();
                Method.this.setInformer(null);
            }
            Method.this.state = new StateTerminal();
        }

        @Override
        public boolean isActive() {
            return true;
        }

    }

    /**
     * Represents the state of a method that is currently inactive.
     */
    private class StateInactive extends State {

        @Override
        public void activate() throws RSBException {
            Method.super.activate();
            Method.this.getInformer().activate();
            Method.this.getListener().activate();
            Method.this.state = new StateActive();
            Method.this.activated();
        }

        @Override
        public boolean isActive() {
            return false;
        }

    }

    /**
     * State which prevents any further state changes.
     *
     * @author jwienke
     */
    private class StateTerminal extends State {

        @Override
        public boolean isActive() {
            return false;
        }

    }

    /**
     * Create a new Method object for the method named @a name.
     *
     * @param args
     *            Arguments used to create a method instances. The last scope
     *            fragment is assumed to be the method name.
     */
    protected Method(final ParticipantCreateArgs<?> args) {
        super(args);
        if (args.getScope().getComponents().isEmpty()) {
            throw new IllegalArgumentException(
                    "Methods must not be on the root scope since "
                            + "they do not have a name then.");
        }
        this.factory = Factory.getInstance();
        this.state = new StateInactive();
    }

    /**
     * Returns the factory instance to use by this method for creating internal
     * participants.
     *
     * @return factory instance
     */
    protected Factory getFactory() {
        return this.factory;
    }

    /**
     * Return the name of this method.
     *
     * @return The name of this method.
     */
    public String getName() {
        final List<String> scopeComponents = this.getScope().getComponents();
        return scopeComponents.get(scopeComponents.size() - 1);
    }

    /**
     * Return the Informer object associated to this method.
     *
     * The Informer object may be created lazily.
     *
     * @return The Informer object.
     */
    public Informer<?> getInformer() {
        return this.informer;
    }

    /**
     * Return the Listener object associated to this method.
     *
     * The Listener object may be created lazily.
     *
     * @return The Listener object.
     */
    public Listener getListener() {
        return this.listener;
    }

    @Override
    public boolean isActive() {
        return this.state.isActive();
    }

    @Override
    public void activate() throws RSBException {
        this.state.activate();
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        this.state.deactivate();
    }

    @Override
    public String toString() {
        return "Method[name=" + this.getName() + "]";
    }

    /**
     * Sets the informer instances used by this method communicate with
     * counterparts.
     *
     * @param informer
     *            the new informer instances
     */
    protected void setInformer(final Informer<?> informer) {
        this.informer = informer;
    }

    /**
     * Sets the listener instance used by this method to communicate with
     * counterparts.
     *
     * @param listener
     *            the new listeners
     */
    protected void setListener(final Listener listener) {
        this.listener = listener;
    }

};

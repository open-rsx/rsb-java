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
package rsb;

import rsb.config.ParticipantConfig;

/**
 * A parameter object used to specify the various parameters that are required
 * to construct new {@link Participant} instances. This class is intended for
 * method chaining. Subclasses exist for each participant type.
 *
 * @author swrede
 * @author jwienke
 * @author jmoringe
 * @param <ConcreteClass>
 *            The specific subclass that needs to be returned in setters for the
 *            method chaining pattern so that subclass setters are still
 *            available after using one of the setters in this class.
 */
// There is no way to instantiate a Participant directly since it is abstract
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
// CHECKSTYLE.OFF: LineLength - no way to format this
public abstract class ParticipantCreateArgs<ConcreteClass extends ParticipantCreateArgs<?>> {

    // CHECKSTYLE.ON: LineLength

    private ParticipantConfig config;
    private Scope scope;
    private Participant parent;

    /**
     * Sets the {@link ParticipantConfig} to be used for the {@link Participant}
     * .
     *
     * @param config
     *            new config to use. Can be <code>null</code> in case the
     *            default configuration shall be used.
     * @return this instance for method chaining.
     */
    @SuppressWarnings("unchecked")
    public ConcreteClass setConfig(final ParticipantConfig config) {
        this.config = config;
        return (ConcreteClass) this;
    }

    /**
     * Sets the {@link Scope} to be used for the {@link Participant}.
     *
     * @param scope
     *            new scope to use. Must not be <code>null</code>.
     * @return this instance for method chaining.
     */
    @SuppressWarnings("unchecked")
    public ConcreteClass setScope(final Scope scope) {
        assert scope != null;
        this.scope = scope;
        return (ConcreteClass) this;
    }

    /**
     * Returns the {@link ParticipantConfig} to be used for the new
     * {@link Participant}.
     *
     * @return the config
     */
    public ParticipantConfig getConfig() {
        return this.config;
    }

    /**
     * Returns the {@link Scope} to be used for the new {@link Participant}.
     *
     * @return the scope
     */
    public Scope getScope() {
        return this.scope;
    }

    /**
     * Returns the parent {@link Participant} of the {@link Participant} to
     * create.
     *
     * @return the parent of the {@link Participant} to create or
     *         <code>null</code> if no parent.
     */
    public Participant getParent() {
        return this.parent;
    }

    /**
     * Sets the parent {@link Participant} of the {@link Participant} to create.
     *
     * @param parent
     *            the parent to set or <code>null</code> for no parent
     * @return this instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public ConcreteClass setParent(final Participant parent) {
        this.parent = parent;
        return (ConcreteClass) this;
    }

}

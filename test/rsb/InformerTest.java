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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.Informer.InformerStateActive;
import rsb.Informer.InformerStateInactive;
import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.transport.TransportFactory;

/**
 * @author swrede
 */
public class InformerTest {

    transient private final Scope defaultScope = new Scope("/informer/example");
    transient private Informer<String> informerString;
    transient private Informer<?> informerGeneric;
    @SuppressWarnings("unused")
    final private ConverterRepository<ByteBuffer> converters = DefaultConverterRepository
            .getDefaultConverterRepository();

    @Before
    public void setUp() throws Throwable {
        this.informerString = new Informer<String>(this.defaultScope,
                String.class);
        this.informerString.activate();
        this.informerGeneric = new Informer<Object>(this.defaultScope);
        this.informerGeneric.activate();
    }

    @After
    public void tearDown() {
        if (this.informerString.isActive()) {
            this.informerString.deactivate();
        }
        if (this.informerGeneric.isActive()) {
            this.informerGeneric.deactivate();
        }
    }

    /**
     * Test method for {@link rsb.Informer#Informer(java.lang.String)}.
     */
    @Test
    public void testInformerString() {
        assertNotNull("Informer is null", this.informerString);
        assertEquals(this.informerString.getScope(), this.defaultScope);
    }

    /**
     * Test method for
     * {@link rsb.Informer#Informer(java.lang.String, rsb.transport.TransportFactory)}
     * .
     */
    @Test
    public void testInformerStringTransportFactory() {
        final Scope scope = new Scope("/x");
        final Informer<String> p = new Informer<String>(scope,
                TransportFactory.getInstance());
        assertNotNull("Informer is null", p);
        assertEquals("Wrong scope", p.getScope(), scope);
    }

    @Test
    public void testInformerStringString() {
        final Scope scope = new Scope("/x");
        final String type = "XMLString";
        final Informer<String> p = new Informer<String>(scope, type.getClass());
        assertNotNull("Informer object is null", p);
        assertEquals(p.getScope(), scope);
        assertEquals(p.getTypeInfo(), type.getClass());
    }

    @Test
    public void testInformerStringStringTransportFactory() {
        final Scope scope = new Scope("/x");
        final String type = "XMLString";
        final Informer<String> p = new Informer<String>(scope, type.getClass(),
                TransportFactory.getInstance());
        assertNotNull(p);
        assertEquals(p.getScope(), scope);
        assertEquals(p.getTypeInfo(), type.getClass());
    }

    /**
     * Test method for {@link rsb.Informer#getScope()}.
     */
    @Test
    public void testGetScope() {
        assertEquals(this.informerString.getScope(), this.defaultScope);
    }

    /**
     * Test method for {@link rsb.Informer#activate()}.
     * 
     * @throws Throwable
     *             any error
     */
    @Test
    public void testActivate() throws Throwable {
        assertTrue(this.informerString.state instanceof InformerStateActive);
    }

    /**
     * Test method for {@link rsb.Informer#deactivate()}.
     * 
     * @throws InitializeException
     */
    @Test
    public void testDeactivate() throws InitializeException {
        assertTrue(this.informerString.state instanceof InformerStateActive);
        this.informerString.deactivate();
        assertTrue(this.informerString.state instanceof InformerStateInactive);
    }

    private void testEvent(final Event e, final Participant participant) {
        assertEquals(String.class, e.getType());
        assertEquals("Hello World!", e.getData());
        assertNotNull(e.getId());
        assertEquals(new Scope("/informer/example"), e.getScope());
        assertEquals(participant.getId(), e.getId().getParticipantId());
    }

    /**
     * Test method for {@link rsb.Informer#send(rsb.Event)}.
     * 
     * @throws Throwable
     *             any error
     */
    @Test
    public void testSendEvent() throws Throwable {
        Event e = this.informerString.send(new Event(this.defaultScope,
                String.class, "Hello World!"));
        this.testEvent(e, this.informerString);
        e = this.informerGeneric.send(new Event(this.defaultScope,
                String.class, "Hello World!"));
        this.testEvent(e, this.informerGeneric);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendWrongType() throws RSBException {
        this.informerString.send(new Event(this.defaultScope, Object.class,
                "not allowed"));
    }

    /**
     * Test method for {@link rsb.Informer#send(rsb.Event)}.
     * 
     * @throws Throwable
     *             any error
     */
    @Test
    public void testSendT() throws Throwable {
        final Event e = this.informerString.send("Hello World!");
        this.testEvent(e, this.informerString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendEventNullScope() throws Throwable {
        final Event e = new Event(this.informerString.getTypeInfo(), "foo");
        e.setScope(null);
        this.informerString.send(e);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendEventWrongScope() throws Throwable {
        this.informerString.send(new Event(new Scope("/blubb"),
                this.informerString.getTypeInfo(), "foo"));
    }

    @Test
    public void testSendEventSubScope() throws Throwable {
        this.informerString.send(new Event(this.defaultScope.concat(new Scope(
                "/blubb")), this.informerString.getTypeInfo(), "foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendEventWrongType() throws Throwable {
        this.informerString.setTypeInfo(String.class);
        this.informerString.send(new Event(this.defaultScope, Boolean.class,
                "foo"));
    }
}

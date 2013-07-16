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
import org.junit.BeforeClass;
import org.junit.Test;

import rsb.Informer.InformerStateActive;
import rsb.Informer.InformerStateInactive;
import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.DefaultConverters;
import rsb.transport.DefaultTransports;
import rsb.transport.TransportRegistry;
import rsb.util.ConfigLoader;
import rsb.util.Properties;

/**
 * @author swrede
 */
public class InformerTest {

    transient private final Scope defaultScope = new Scope("/informer/example");
    transient private Informer<String> stringInformer;
    transient private Informer<?> genericInformer;
    @SuppressWarnings("unused")
    final private ConverterRepository<ByteBuffer> converters = DefaultConverterRepository
            .getDefaultConverterRepository();
    private final Properties properties = new Properties();

    @BeforeClass
    public static void registerConverters() {
        DefaultConverters.register();
        DefaultTransports.register();
    }

    @Before
    public void setUp() throws Throwable {
        final ConfigLoader loader = new ConfigLoader();
        this.properties.reset();
        loader.load(this.properties);

        this.stringInformer = new Informer<String>(this.defaultScope,
                String.class, this.properties);
        this.stringInformer.activate();
        this.genericInformer = new Informer<Object>(this.defaultScope,
                this.properties);
        this.genericInformer.activate();
    }

    @After
    public void tearDown() throws Throwable {
        if (this.stringInformer.isActive()) {
            this.stringInformer.deactivate();
        }
        if (this.genericInformer.isActive()) {
            this.genericInformer.deactivate();
        }
    }

    @Test
    public void informerString() {
        assertNotNull("Informer is null", this.stringInformer);
        assertEquals(this.stringInformer.getScope(), this.defaultScope);
    }

    @Test
    public void informerStringTransportFactory() throws Throwable {
        final Scope scope = new Scope("/x");
        final Informer<String> informer = new Informer<String>(scope,
                TransportRegistry.getDefaultInstance().getFactory("spread"),
                this.properties);
        assertNotNull("Informer is null", informer);
        assertEquals("Wrong scope", informer.getScope(), scope);
    }

    @Test
    public void informerStringString() throws Throwable {
        final Scope scope = new Scope("/x");
        final String type = "XMLString";
        final Informer<String> informer = new Informer<String>(scope,
                type.getClass(), this.properties);
        assertNotNull("Informer object is null", informer);
        assertEquals(informer.getScope(), scope);
        assertEquals(informer.getTypeInfo(), type.getClass());
    }

    @Test
    public void informerStringStringTransportFactory() throws Throwable {
        final Scope scope = new Scope("/x");
        final String type = "XMLString";
        final Informer<String> informer = new Informer<String>(scope,
                type.getClass(), TransportRegistry.getDefaultInstance()
                        .getFactory("spread"), this.properties);
        assertNotNull(informer);
        assertEquals(informer.getScope(), scope);
        assertEquals(informer.getTypeInfo(), type.getClass());
    }

    /**
     * Test method for {@link rsb.Informer#getScope()}.
     */
    @Test
    public void getScope() {
        assertEquals(this.stringInformer.getScope(), this.defaultScope);
    }

    /**
     * Test method for {@link rsb.Informer#activate()}.
     *
     * @throws Throwable
     *             any error
     */
    @Test
    public void activate() throws Throwable {
        assertTrue(this.stringInformer.state instanceof InformerStateActive);
    }

    /**
     * Test method for {@link rsb.Informer#deactivate()}.
     *
     * @throws Throwable
     *             any error
     */
    @Test
    public void deactivate() throws Throwable {
        assertTrue(this.stringInformer.state instanceof InformerStateActive);
        this.stringInformer.deactivate();
        assertTrue(this.stringInformer.state instanceof InformerStateInactive);
    }

    // this is not a junit test case, we know that
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    private void testEvent(final Event event, final Participant participant) {
        assertEquals(String.class, event.getType());
        assertEquals("Hello World!", event.getData());
        assertNotNull(event.getId());
        assertEquals(new Scope("/informer/example"), event.getScope());
        assertEquals(participant.getId(), event.getId().getParticipantId());
    }

    /**
     * Test method for {@link rsb.Informer#send(rsb.Event)}.
     *
     * @throws Throwable
     *             any error
     */
    // junit assertions are handled by method call
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void sendEvent() throws Throwable {
        Event sentEvent = this.stringInformer.send(new Event(this.defaultScope,
                String.class, "Hello World!"));
        this.testEvent(sentEvent, this.stringInformer);
        sentEvent = this.genericInformer.send(new Event(this.defaultScope,
                String.class, "Hello World!"));
        this.testEvent(sentEvent, this.genericInformer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendWrongType() throws RSBException {
        this.stringInformer.send(new Event(this.defaultScope, Object.class,
                "not allowed"));
    }

    /**
     * Test method for {@link rsb.Informer#send(rsb.Event)}.
     *
     * @throws Throwable
     *             any error
     */
    // junit assertions are handled by method call
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void sendT() throws Throwable {
        final Event sentEvent = this.stringInformer.send("Hello World!");
        this.testEvent(sentEvent, this.stringInformer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventNullScope() throws Throwable {
        final Event sentEvent = new Event(this.stringInformer.getTypeInfo(),
                "foo");
        sentEvent.setScope(null);
        this.stringInformer.send(sentEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventWrongScope() throws Throwable {
        this.stringInformer.send(new Event(new Scope("/blubb"),
                this.stringInformer.getTypeInfo(), "foo"));
    }

    // we just want to know that the call does not raise an exception
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void sendEventSubScope() throws Throwable {
        this.stringInformer.send(new Event(this.defaultScope.concat(new Scope(
                "/blubb")), this.stringInformer.getTypeInfo(), "foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventWrongType() throws Throwable {
        this.stringInformer.setTypeInfo(String.class);
        this.stringInformer.send(new Event(this.defaultScope, Boolean.class,
                "foo"));
    }
}

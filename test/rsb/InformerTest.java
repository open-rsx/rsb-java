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
import rsb.config.ParticipantConfig;
import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.DefaultConverters;
import rsb.transport.DefaultTransports;

/**
 * @author swrede
 */
@SuppressWarnings("PMD.TooManyMethods")
public class InformerTest {

    private static final Scope DEFAULT_SCOPE = new Scope("/informer/example");
    private static final String DEFAULT_STRING_PAYLOAD = "Hello World!";
    private Informer<String> stringInformer;
    private Informer<?> genericInformer;
    @SuppressWarnings("unused")
    final private ConverterRepository<ByteBuffer> converters = DefaultConverterRepository
            .getDefaultConverterRepository();
    private ParticipantConfig config;

    @BeforeClass
    public static void registerConverters() {
        DefaultConverters.register();
        DefaultTransports.register();
    }

    @Before
    public void setUp() throws Throwable {

        this.config = Utilities.createParticipantConfig();

        this.stringInformer = new Informer<String>(DEFAULT_SCOPE, String.class,
                this.config);
        this.stringInformer.activate();
        this.genericInformer = new Informer<Object>(DEFAULT_SCOPE, this.config);
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
        assertEquals(this.stringInformer.getScope(), DEFAULT_SCOPE);
    }

    @Test
    public void informerStringConfig() throws Throwable {
        final Scope scope = new Scope("/x");
        final Informer<String> informer = new Informer<String>(scope,
                this.config);
        assertNotNull("Informer is null", informer);
        assertEquals("Wrong scope", informer.getScope(), scope);
    }

    @Test
    public void informerScopeTypeConfig() throws Throwable {
        final Scope scope = new Scope("/x");
        final String type = "XMLString";
        final Informer<String> informer = new Informer<String>(scope,
                type.getClass(), this.config);
        assertNotNull("Informer object is null", informer);
        assertEquals(informer.getScope(), scope);
        assertEquals(informer.getTypeInfo(), type.getClass());
    }

    @Test
    public void informerScopeConfig() throws Throwable {
        final Scope scope = new Scope("/x");
        final String type = "XMLString";
        final Informer<String> informer = new Informer<String>(scope,
                type.getClass(), this.config);
        assertNotNull(informer);
        assertEquals(informer.getScope(), scope);
        assertEquals(informer.getTypeInfo(), type.getClass());
    }

    /**
     * Test method for {@link rsb.Informer#getScope()}.
     */
    @Test
    public void getScope() {
        assertEquals(this.stringInformer.getScope(), DEFAULT_SCOPE);
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
        assertEquals(DEFAULT_STRING_PAYLOAD, event.getData());
        assertNotNull(event.getId());
        assertEquals(DEFAULT_SCOPE, event.getScope());
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
        Event sentEvent = this.stringInformer.send(new Event(DEFAULT_SCOPE,
                String.class, DEFAULT_STRING_PAYLOAD));
        this.testEvent(sentEvent, this.stringInformer);
        sentEvent = this.genericInformer.send(new Event(DEFAULT_SCOPE,
                String.class, DEFAULT_STRING_PAYLOAD));
        this.testEvent(sentEvent, this.genericInformer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendWrongType() throws RSBException {
        this.stringInformer.send(new Event(DEFAULT_SCOPE, Object.class,
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
        final Event sentEvent = this.stringInformer
                .send(DEFAULT_STRING_PAYLOAD);
        this.testEvent(sentEvent, this.stringInformer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventNullScope() throws Throwable {
        final Event sentEvent = new Event(this.stringInformer.getTypeInfo(),
                DEFAULT_STRING_PAYLOAD);
        sentEvent.setScope(null);
        this.stringInformer.send(sentEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventWrongScope() throws Throwable {
        this.stringInformer.send(new Event(new Scope("/blubb"),
                this.stringInformer.getTypeInfo(), DEFAULT_STRING_PAYLOAD));
    }

    // we just want to know that the call does not raise an exception
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void sendEventSubScope() throws Throwable {
        this.stringInformer.send(new Event(DEFAULT_SCOPE.concat(new Scope(
                "/blubb")), this.stringInformer.getTypeInfo(),
                DEFAULT_STRING_PAYLOAD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventWrongType() throws Throwable {
        this.stringInformer.setTypeInfo(String.class);
        this.stringInformer.send(new Event(DEFAULT_SCOPE, Boolean.class,
                DEFAULT_STRING_PAYLOAD));
    }
}

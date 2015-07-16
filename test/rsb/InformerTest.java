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

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rsb.config.ParticipantConfig;
import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.DefaultConverters;
import rsb.transport.DefaultTransports;

/**
 * @author swrede
 */
@SuppressWarnings("PMD.TooManyMethods")
public class InformerTest extends LoggingEnabled {

    private static final Scope DEFAULT_SCOPE = new Scope("/informer/example");
    private static final String DEFAULT_STRING_PAYLOAD = "Hello World!";
    private static final String XML_TYPE = "XMLString";

    private Informer<String> stringInformer;
    private Informer<?> genericInformer;
    @SuppressWarnings("unused")
    private final ConverterRepository<ByteBuffer> converters =
            DefaultConverterRepository.getDefaultConverterRepository();
    private ParticipantConfig config;

    @BeforeClass
    public static void registerConverters() {
        DefaultConverters.register();
        DefaultTransports.register();
    }

    @Before
    public void setUp() throws Throwable {

        this.config = Utilities.createParticipantConfig();

        this.stringInformer =
                new Informer<String>(new InformerCreateArgs()
                        .setScope(DEFAULT_SCOPE).setType(String.class)
                        .setConfig(this.config));
        this.stringInformer.activate();
        this.genericInformer =
                new Informer<Object>(new InformerCreateArgs().setScope(
                        DEFAULT_SCOPE).setConfig(this.config));
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
        final Informer<String> informer =
                new Informer<String>(new InformerCreateArgs().setScope(
                        DEFAULT_SCOPE).setConfig(this.config));
        assertEquals("Wrong scope", informer.getScope(), DEFAULT_SCOPE);
    }

    @Test
    public void informerScopeTypeConfig() throws Throwable {
        final Informer<String> informer =
                new Informer<String>(new InformerCreateArgs()
                        .setScope(DEFAULT_SCOPE).setType(XML_TYPE.getClass())
                        .setConfig(this.config));
        assertNotNull("Informer object is null", informer);
        assertEquals(informer.getScope(), DEFAULT_SCOPE);
        assertEquals(informer.getTypeInfo(), XML_TYPE.getClass());
    }

    @Test
    public void informerScopeConfig() throws Throwable {
        final Scope scope = new Scope("/x");
        final Informer<String> informer =
                new Informer<String>(new InformerCreateArgs().setScope(scope)
                        .setType(XML_TYPE.getClass()).setConfig(this.config));
        assertNotNull(informer);
        assertEquals(informer.getScope(), scope);
        assertEquals(informer.getTypeInfo(), XML_TYPE.getClass());
    }

    /**
     * Test method for {@link rsb.Informer#getScope()}.
     */
    @Test
    public void getScope() {
        assertEquals(this.stringInformer.getScope(), DEFAULT_SCOPE);
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
        Event sentEvent =
                this.stringInformer.publish(new Event(DEFAULT_SCOPE,
                        String.class, DEFAULT_STRING_PAYLOAD));
        this.testEvent(sentEvent, this.stringInformer);
        sentEvent =
                this.genericInformer.publish(new Event(DEFAULT_SCOPE,
                        String.class, DEFAULT_STRING_PAYLOAD));
        this.testEvent(sentEvent, this.genericInformer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendWrongType() throws RSBException {
        this.stringInformer.publish(new Event(DEFAULT_SCOPE, Object.class,
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
        final Event sentEvent =
                this.stringInformer.publish(DEFAULT_STRING_PAYLOAD);
        this.testEvent(sentEvent, this.stringInformer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventNullScope() throws Throwable {
        final Event sentEvent =
                new Event(this.stringInformer.getTypeInfo(),
                        DEFAULT_STRING_PAYLOAD);
        sentEvent.setScope(null);
        this.stringInformer.publish(sentEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventWrongScope() throws Throwable {
        this.stringInformer.publish(new Event(new Scope("/blubb"),
                this.stringInformer.getTypeInfo(), DEFAULT_STRING_PAYLOAD));
    }

    // we just want to know that the call does not raise an exception
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void sendEventSubScope() throws Throwable {
        this.stringInformer.publish(new Event(DEFAULT_SCOPE.concat(new Scope(
                "/bla")), this.stringInformer.getTypeInfo(),
                DEFAULT_STRING_PAYLOAD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendEventWrongType() throws Throwable {
        this.stringInformer.setTypeInfo(String.class);
        this.stringInformer.publish(new Event(DEFAULT_SCOPE, Boolean.class,
                DEFAULT_STRING_PAYLOAD));
    }
}

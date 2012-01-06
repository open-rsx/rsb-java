/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import static org.junit.Assert.*;

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
	final private ConverterRepository<ByteBuffer> converters = DefaultConverterRepository.getDefaultConverterRepository();

	@Before
	public void setUp() throws Throwable {
		informerString = new Informer<String>(defaultScope,String.class);
		informerString.activate();
		informerGeneric = new Informer<Object>(defaultScope);
		informerGeneric.activate();
	}

	@After
	public void tearDown() {
		if (informerString.isActive()) {
			informerString.deactivate();
		}
		if (informerGeneric.isActive()) {
			informerGeneric.deactivate();
		}
	}

	/**
	 * Test method for {@link rsb.Informer#Informer(java.lang.String)}.
	 */
	@Test
	public void testInformerString() {
		assertNotNull("Informer is null",informerString);
		assertEquals(informerString.getScope(), defaultScope);
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
		assertNotNull("Informer is null",p);
		assertEquals("Wrong scope",p.getScope(), scope);
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testInformerStringString() {
		final Scope scope = new Scope("/x");
		final String type = "XMLString";
		Informer<String> p = new Informer<String>(scope, type.getClass());
		assertNotNull("Informer object is null",p);
		assertEquals(p.getScope(), scope);
		assertEquals(p.getTypeInfo(), type.getClass());
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, java.lang.String, rsb.transport.TransportFactory)}
	 * .
	 */
	@Test
	public void testInformerStringStringTransportFactory() {
		final Scope scope = new Scope("/x");
		final String type = "XMLString";
		Informer<String> p = new Informer<String>(scope, type.getClass(),
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
		assertEquals(informerString.getScope(), defaultScope);
	}

	/**
	 * Test method for {@link rsb.Informer#activate()}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testActivate() throws Throwable {
		assertTrue(informerString.state instanceof InformerStateActive);
	}

	/**
	 * Test method for {@link rsb.Informer#deactivate()}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testDeactivate() throws InitializeException {
		assertTrue(informerString.state instanceof InformerStateActive);
		informerString.deactivate();
		assertTrue(informerString.state instanceof InformerStateInactive);
	}

	private void testEvent(Event e, Participant participant) {
		assertEquals(String.class, e.getType());
		assertEquals("Hello World!", e.getData());
		assertNotNull(e.getId());
		assertEquals(new Scope("/informer/example"), e.getScope());
		assertEquals(participant.getId(), e.getId().getParticipantId());
	}

	/**
	 * Test method for {@link rsb.Informer#send(rsb.Event)}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testSendEvent() throws Throwable {
		Event e = informerString.send(new Event(defaultScope, String.class,
				"Hello World!"));
		testEvent(e, informerString);
		e = informerGeneric.send(new Event(defaultScope, String.class,
				"Hello World!"));
		testEvent(e, informerGeneric);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendWrongType() throws RSBException {
		informerString.send(new Event(defaultScope, Object.class,"not allowed"));
	}

	/**
	 * Test method for {@link rsb.Informer#send(rsb.Event)}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testSendT() throws Throwable {
		Event e = informerString.send("Hello World!");
		testEvent(e, informerString);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendEventNullScope() throws Throwable {
		Event e = new Event();
		e.setType(informerString.getTypeInfo());
		e.setScope(null);
		e.setData("foo");
		informerString.send(e);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendEventWrongScope() throws Throwable {
		Event e = new Event();
		e.setType(informerString.getTypeInfo());
		e.setScope(new Scope("/blubb"));
		e.setData("foo");
		informerString.send(e);
	}

	@Test
	public void testSendEventSubScope() throws Throwable {
		Event e = new Event();
		e.setType(informerString.getTypeInfo());
		e.setScope(defaultScope.concat(new Scope("/blubb")));
		e.setData("foo");
		informerString.send(e);
	}

	public void testSendEventNullType() throws Throwable {
		Event e = new Event();
		e.setType(null);
		e.setScope(defaultScope);
		e.setData("foo");
		informerString.send(e);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendEventWrongType() throws Throwable {
		informerString.setTypeInfo(String.class);
		Event e = new Event();
		e.setType(Boolean.class);
		e.setScope(defaultScope);
		e.setData("foo");
		informerString.send(e);
	}
}

package rsb.transport.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;
import rsb.transport.socket.Bus.NotificationReceiver;

/**
 * Tests the roundtrip of data through bus server and client.
 *
 * @author jwienke
 */
public class BusRoundtripTest {

    private ResultWaiter serverResult;
    private ResultWaiter clientResult;
    private ResultWaiter secondClientResult;
    private BusServer server;
    private BusClient client;
    private BusClient secondClient;

    private class ResultWaiter implements NotificationReceiver {

        private Notification received = null;

        @Override
        public void handle(final Notification notification) {
            synchronized (this) {
                this.received = notification;
                this.notifyAll();
            }
        }

        public Notification waitForResult() throws InterruptedException {
            synchronized (this) {

                final long waitTime = 10000;
                final long waitStart = System.currentTimeMillis();
                while (this.received == null
                        && System.currentTimeMillis() < waitStart + waitTime) {
                    this.wait(waitTime);
                }

                return this.received;

            }
        }

    }

    @Before
    public void setUp() throws Throwable {

        this.serverResult = new ResultWaiter();
        this.server = new BusServer(Utilities.getSocketOptions());
        this.server.activate();
        this.server.addNotificationReceiver(this.serverResult);

        this.clientResult = new ResultWaiter();
        this.client = new BusClient(Utilities.getSocketOptions());
        this.client.activate();
        this.client.addNotificationReceiver(this.clientResult);

        this.secondClientResult = new ResultWaiter();
        this.secondClient = new BusClient(Utilities.getSocketOptions());
        this.secondClient.activate();
        this.secondClient.addNotificationReceiver(this.secondClientResult);

    }

    @After
    public void tearDown() throws Throwable {

        if (this.secondClient != null) {
            try {
                this.secondClient.deactivate();
            } catch (final RSBException e) {
                // we can't do anything
            }
            this.secondClient = null;
        }
        if (this.client != null) {
            try {
                this.client.deactivate();
            } catch (final RSBException e) {
                // we can't do anything
            }
            this.client = null;
        }
        if (this.server != null) {
            try {
                this.server.deactivate();
            } catch (final RSBException e) {
                // we can't do anything
            }
            this.server = null;
        }

    }

    private void checkResults(final Notification sent) throws Throwable {

        {
            final Notification serverResult = this.serverResult.waitForResult();
            assertNotNull(
                    "We must have received something after waiting this long",
                    serverResult);
            assertEquals(sent, serverResult);
        }

        {
            final Notification clientResult = this.clientResult.waitForResult();
            assertNotNull(
                    "We must have received something after waiting this long",
                    clientResult);
            assertEquals(sent, clientResult);
        }

        {
            final Notification secondClientResult = this.secondClientResult
                    .waitForResult();
            assertNotNull(
                    "We must have received something after waiting this long",
                    secondClientResult);
            assertEquals(sent, secondClientResult);
        }

    }

    // asserts are delegated to a separate method
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void fromServer() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.server.handleOutgoing(sent);

        checkResults(sent);

    }

    // asserts are delegated to a separate method
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void fromClient() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.client.handleOutgoing(sent);

        checkResults(sent);

    }

    // asserts are delegated to a separate method
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void fromSecondClient() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.secondClient.handleOutgoing(sent);

        checkResults(sent);

    }

}

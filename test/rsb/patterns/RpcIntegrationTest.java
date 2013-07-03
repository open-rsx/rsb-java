package rsb.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import rsb.Event;
import rsb.Factory;
import rsb.RSBException;
import rsb.Scope;

public class RpcIntegrationTest {

    DataInCallback dataInCallback = new DataInCallback();
    EventInCallback eventInCallback = new EventInCallback();
    ReplyDataCallback dataCallback = new ReplyDataCallback();
    ReplyEventCallback eventCallback = new ReplyEventCallback();

    @Before
    public void resetCounters() {
        dataCallback.counter.set(0);
        eventCallback.counter.set(0);
        dataInCallback.counter.set(0);
        eventInCallback.counter.set(0);
    }

    @Test
    public void testCallMethod() throws RSBException, InterruptedException,
            ExecutionException {
        final RemoteServer remote = setupActiveObjects();
        assertNotNull("RemoteServer construction failed", remote);
        String result1 = null;
        String result2 = null;
        List<Future<Event>> resultsEvents = new ArrayList<Future<Event>>();
        List<Future<String>> resultsData = new ArrayList<Future<String>>();
        for (int i = 0; i < 100; i++) {
            result1 = remote.call("callmedc", "testdata");
            Event event = new Event(String.class);
            event.setData("testdata2");
            Event event2 = remote.call("callmeec", event);
            result2 = (String) event2.getData();
            Event event3 = new Event(String.class);
            event3.setData("testdata2");
            resultsEvents.add(remote.callAsync("callmeec", event3));
            Future<String> future = remote.callAsync("callmedc", "testdata");
            resultsData.add(future);
        }
        // TODO make this test nicer, remove sleep...
        Thread.sleep(500);
        // check result of blocking call
        result1.equals("adf");
        assertEquals("Incorrect number of event callback invocations!", 200,
                dataCallback.counter.get());
        assertEquals("Incorrect number of data callback invocations!", 200,
                eventCallback.counter.get());
        assertTrue("Received wrong result from server callback.",
                result1.equals("testdata"));
        assertTrue("Received wrong result from server callback.",
                result2.equals("testdata2"));
        assertEquals("Not all events delivererd!", 100, resultsEvents.size());
        assertEquals("Not all data delivererd!", 100, resultsData.size());
        // check result of async event calls
        for (int i = 0; i < 100; i++) {
            String result = (String) resultsEvents.get(i).get().getData();
            assertTrue("Received wrong result from server callback.",
                    result.equals("testdata2"));
        }
        // check result of async data calls
        for (int i = 0; i < 100; i++) {
            String result = resultsData.get(i).get();
            assertTrue("Received wrong result from server callback.",
                    result.equals("testdata"));
        }
    }

    @SuppressWarnings("unchecked")
    private RemoteServer setupActiveObjects() throws RSBException {
        final LocalServer server = Factory.getInstance().createLocalServer(
                new Scope("/example/server"));
        // TODO new type of callback needed?
        server.addMethod("callmedi", dataInCallback);
        server.addMethod("callmeei", eventInCallback);
        server.addMethod("callmedc", dataCallback);
        server.addMethod("callmeec", eventCallback);

        server.activate();
        final RemoteServer remote = Factory.getInstance().createRemoteServer(
                new Scope("/example/"), 1200);
        assertNotNull("RemoteServer construction failed", remote);
        remote.activate();
        return remote;
    }

    @Test
    public void testCallMethodWithoutParameter() throws RSBException {
        List<Future<Event>> resultsEvents = new ArrayList<Future<Event>>();
        RemoteServer remote = setupActiveObjects();
        for (int i = 0; i < 100; i++) {
            // optional use of reply value is possible
            Event event = remote.call("callmedi");
            // but can also be ignored
            remote.call("callmeei");
            // asynchronous completion
            resultsEvents.add((Future<Event>) remote.callAsync("callmeei"));
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO check result of blocking call
        assertEquals("Incorrect number of data callback invocations!", 100,
                dataInCallback.counter.get());
        assertEquals("Incorrect number of event callback invocations!", 200,
                eventInCallback.counter.get());
        // TODO check result of async calls
    }

}

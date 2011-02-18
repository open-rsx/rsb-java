/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rsb.transport.spread;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import rsb.InitializeException;
import rsb.RSBEvent;
import rsb.RSBException;
import rsb.protocol.AttachmentPB.Attachment;
import rsb.protocol.NotificationPB.Notification;
import rsb.transport.AbstractPort;
import rsb.util.QueueClosedException;
import spread.SpreadException;

/**
 *
 * @author swrede
 */
public class Port extends AbstractPort {

	private final static Logger log = Logger.getLogger(Port.class.getName());
	
    /**
     * Protocol for optimization based on registered filters:
     *   TypeFilter: Some types may be received via special spread groups,
     *               e.g. SystemEvents. Port could join groups for registered types only
     *               and send events of this type to the same groups
     *   IdentityFilter: This depends on whether the component is filtering for it's
     *               own identity, to receive private messages (could use spread's
     *               private groups here, but that would prevent us from intercepting
     *               this communication), or filtering for another components
     *               identity, e.g. a publisher. 
     *   ScopeFilter: Restricts visibility according to the group encoding rules based
     *                on the Scope concept.
     *   XPathFilter: no way to optimize this on the Port
     * 
     */
   // protected static XcfLog log = XcfLog.create("Transport.Spread");
    SpreadWrapper spread = null;
//    UriTranslator translator = new UriTranslator();
//    HashMap<XcfUri, IdentityFilter> identityFilters = new HashMap<XcfUri, IdentityFilter>();
    /* reference counting for groups that may have multiple filters. */
    // this is necessary because filters for the same scope or port may be added
    // multiple times but the spread group is joined only once and left only if
    // all filters for that group have been removed
   // HashMap<String, LinkedList<MTF>> groupReferences = new HashMap<String, LinkedList<MTF>>();

    public Port(SpreadWrapper sw) {
        spread = sw;
    }

    public void activate() throws InitializeException {
        // activate spread connection
        if (!spread.isActivated()) {
            spread.activate();
        }
    }

    public RSBEvent next() throws InterruptedException {
        RSBEvent me = null;
        // repeat until next event was successfully decoded or
        // an exception is thrown
        // TODO add better handling of inactive state
        while (me == null) {
            try {
                DataMessage msg = spread.next();
                log.info("Message received from Spread");
                if (msg == null) {
                    log.info("received null message from port, will be ignored");
                } else {
                    // TODO deserialize notification
                	me = new RSBEvent();
                	me.setData(msg.getData());
                	me.setType("string");
                }
            } catch (QueueClosedException e) {
                log.info("SpreadWrapper's message queues were closed");
                throw new InterruptedException("Port next was interrupted");
            } catch (java.lang.InterruptedException e) {
                // TODO Auto-generated catch block
                throw new InterruptedException("Port next was interrupted");
            }
        }
        return me;
    }

    public void push(RSBEvent e) {
        // TODO refactor this
		Notification.Builder nb = Notification.newBuilder();
		Attachment.Builder ab = Attachment.newBuilder();
		nb.setEid(e.getUuid().toString());
		nb.setTypeId("string");
		nb.setUri(e.getUri());		
		ByteBuffer bb = ByteBuffer.wrap(((String) e.getData()).getBytes()); 
		nb.setData(ab.setBinary(ByteString.copyFrom(bb)).setLength(bb.array().length));	
		nb.setStandalone(true);
		Notification n = nb.build();
        log.info("push called, sending message on port infrastructure, event id: " + e.getUuid());
        // TODO remove data message
        DataMessage dm = new DataMessage(n.toByteArray());
        String[] groups = {"rsb://example/informer"};
        dm.setGroups(groups);
        spread.send(dm);
    }

    private void joinSpreadGroup(String hash) {
        if (spread.isActivated()) {
            // join group 
            try {
                spread.join(hash);

            } catch (SpreadException e) {
                // TODO how to handle this exception
                e.printStackTrace();
            }
        } else {
            log.severe("Couldn't set up network filter, spread inactive.");
        }
    }



    private void leaveSpreadGroup(String hash) {
        if (spread.isActivated()) {
            spread.leave(hash);
        } else {
            log.severe("Couldn't remove group filter, spread inactive.");
        }
    }


    public void deactivate() throws RSBException {
        if (spread.isActivated()) {
        	log.info("deactivating SpreadPort");
            spread.deactivate();
        }
    }

    public String getInfo() {
        // TODO Auto-generated method stub
        return this.getClass().getSimpleName();
    }
}

/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb.transport.spread;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import rsb.InitializeException;
import rsb.RSBEvent;
import rsb.RSBException;
import rsb.protocol.AttachmentPB.Attachment;
import rsb.protocol.NotificationPB.Notification;
import rsb.transport.AbstractConverter;
import rsb.transport.AbstractPort;
import rsb.transport.convert.StringConverter;
import rsb.util.QueueClosedException;
import spread.SpreadException;

/**
 *
 * @author swrede
 */
public class SpreadPort extends AbstractPort {

	private final static Logger log = Logger.getLogger(SpreadPort.class.getName());
	
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

    SpreadWrapper spread = null;
    Map<String, AbstractConverter<String>> converters = new HashMap<String, AbstractConverter<String>>();

    public SpreadPort(SpreadWrapper sw) {
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
                	me = new RSBEvent("string",msg.getData());
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
		// copy-from ByteBuffer seems to be available only with gpb 2.3 version
		//nb.setData(ab.setBinary(ByteString.copyFrom(bb)).setLength(bb.array().length));		
		ab.setBinary(ByteString.copyFrom(bb.array()));		
		ab.setLength(bb.limit());
		nb.setData(ab.build());
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
    
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "SpreadPort";
	}

	public void addConverter(String s, AbstractConverter<String> c) {
		converters.put(s, c);		
	}
}

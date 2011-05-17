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

import rsb.InitializeException;
import rsb.RSBEvent;
import rsb.RSBException;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.protocol.AttachmentPB.Attachment;
import rsb.protocol.NotificationPB.Notification;
import rsb.transport.AbstractConverter;
import rsb.transport.AbstractPort;
import rsb.transport.convert.ByteBufferConverter;
import rsb.util.Holder;
import spread.SpreadException;

import com.google.protobuf.ByteString;

/**
 *
 * @author swrede
 */
public class SpreadPort extends AbstractPort {

	ReceiverTask receiver;

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
    Map<String, AbstractConverter<ByteBuffer>> converters = new HashMap<String, AbstractConverter<ByteBuffer>>();

    public SpreadPort(SpreadWrapper sw) {
        spread = sw;
    }

    public void activate() throws InitializeException {
        receiver = new ReceiverTask(spread,r,converters);
        // activate spread connection
        if (!spread.isActivated()) {
            spread.activate();
        }
        receiver.setPriority(Thread.NORM_PRIORITY+2);
        receiver.setName("ReceiverTask [name="+spread.getName()+",grp="+ spread.getPrivateGroup() +"]");
        receiver.start();
    }

    /* (non-Javadoc)
	 * @see rsb.filter.AbstractFilterObserver#notify(rsb.filter.ScopeFilter, rsb.filter.FilterAction)
	 */
	@Override
	public void notify(ScopeFilter e, FilterAction a) {
		log.info("SpreadPort::notify(ScopeFilter e, FilterAction=" + a.name() +" called");
		switch (a) {
		case ADD:
			// TODO add reference handling from xcf4j
			joinSpreadGroup(e.getUri());
			break;
		case REMOVE:
			// TODO add reference handling from xcf4j
			leaveSpreadGroup(e.getUri());
			break;
		case UPDATE:
			log.info("Update of ScopeFilter requested on SpreadSport");
			break;
		default:
			break;
		}
	}

    public void push(RSBEvent e) {
        // TODO deal with missing converter
	AbstractConverter<ByteBuffer> c = converters.get(e.getType());
		Notification.Builder nb = Notification.newBuilder();
		Attachment.Builder ab = Attachment.newBuilder();
		nb.setId(e.getId().toString());
		nb.setWireSchema(e.getType());
		nb.setScope(e.getUri());
		// copy-from ByteBuffer seems to be available only with gpb 2.3 version
		//nb.setData(ab.setBinary(ByteString.copyFrom(bb)).setLength(bb.array().length));
		Holder<ByteBuffer> bb = c.serialize("string",e.getData());
		ab.setBinary(ByteString.copyFrom(bb.value.array()));
		ab.setLength(bb.value.limit());
		nb.setData(ab.build());
		Notification n = nb.build();
        log.fine("push called, sending message on port infrastructure: [eid=" + e.getId().toString() + "]");
        //log.info("push called, sending message on port infrastructure: " + (String) e.getData());
        // TODO remove data message
        DataMessage dm = new DataMessage();
        try {
			dm.setData(n.toByteArray());
		} catch (SerializeException e1) {
			// TODO think about reasonable error handling
			e1.printStackTrace();
		}
        dm.addGroup(e.getUri());
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
		log.fine("deactivating SpreadPort");
            spread.deactivate();
        }
        try {
			receiver.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "SpreadPort";
	}

	public void addConverter(String s, ByteBufferConverter bbc) {
		converters.put(s, bbc);
	}
}

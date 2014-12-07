/**
 * ============================================================
 *
 * This file is a part of the rsb.git.java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb.introspection;

import org.junit.Test;

import rsb.Factory;
import rsb.Informer;
import rsb.Listener;
import rsb.RSBException;

/**
 * @author swrede
 *
 */
public class ManualIntrospectionTest {

    @Test
    public void simpleNonTest() throws RSBException, InterruptedException {
        final Factory factory = Factory.getInstance();

        if (!Factory.getInstance().getProperties()
                .getProperty("rsb.introspection", "false").asBoolean()) {
            Factory.getInstance().addObserver(
                    new IntrospectionParticipantObserver());
        }

        // regular RSB API usage, example here: listener creation and
        // destruction
        final Listener listener = factory.createListener("/rsbtest");
        listener.activate();
        final Informer<String> informer =
                factory.createInformer("/rsbtest", String.class);
        informer.activate();
        Thread.sleep(1000);
        listener.deactivate();
        informer.deactivate();
    }

}

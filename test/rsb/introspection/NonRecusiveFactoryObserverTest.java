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
import rsb.InitializeException;
import rsb.Listener;
import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantObserver;
import rsb.RSBException;
import rsb.Scope;

/**
 * Development test case for preventing recursive calls to a participant
 * observer. The test case should fail with StackOverflowException if the
 * condition to prevent recursive calls is not correct.
 *
 * @author swrede
 *
 */
public class NonRecusiveFactoryObserverTest {

    class RecursiveFactoryObserver implements ParticipantObserver {

        @Override
        public void created(final Participant participant,
                final ParticipantCreateArgs<?> args) {
            if (participant.getScope().isSubScopeOf(new Scope("/nonrecursive"))) {
                final Factory factory = Factory.getInstance();
                try {
                    factory.createInformer("/recursive/a");
                } catch (final InitializeException e) {
                    e.printStackTrace();
                }
            } else if (participant.getScope().isSubScopeOf(
                    new Scope("/recursive"))) {
                System.out
                        .println("Not creating informer due to recursive notification.");
            }
        }

        @Override
        public void destroyed(final Participant participant) {
            // intentionally left blank

        }
    }

    @Test
    public void test() throws RSBException, InterruptedException {
        final Factory factory = Factory.getInstance();
        factory.addObserver(new LoggingObserver());
        factory.addObserver(new RecursiveFactoryObserver());

        final Listener listener = factory.createListener("/nonrecursive/b");
        listener.activate();
        listener.deactivate();
    }

}

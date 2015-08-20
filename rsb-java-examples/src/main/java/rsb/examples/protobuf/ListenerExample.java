/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010-2012 CoR-Lab, Bielefeld University
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
package rsb.examples.protobuf;

// mark-start::body
import rsb.AbstractDataHandler;
import rsb.Factory;
import rsb.Listener;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.examples.protobuf.ImageMessage.SimpleImage;

public class ListenerExample {

    public static void main(final String[] args) throws Throwable {

        // See RegistrationExample.java.
        final ProtocolBufferConverter<SimpleImage> converter = new ProtocolBufferConverter<SimpleImage>(
                SimpleImage.getDefaultInstance());

        // register converter for SimpleImage's.
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(converter);

        // Create a Listener instance on the specified scope that will
        // receive events and dispatch them asynchronously to the
        // registered handlers.
        final Factory factory = Factory.getInstance();
        final Listener listener = factory.createListener("/example/protobuf");
        listener.activate();

        // Add a DataHandler that is called with the SimpleImage datum
        // contained in received events.
        try {
            listener.addHandler(new AbstractDataHandler<SimpleImage>() {

                @Override
                public void handleEvent(final SimpleImage e) {
                    System.out.println("SimpleImage data received");
                }

            }, true);

            // Wait for events.
            while (true) {
                Thread.sleep(1);
            }
        } finally {
            listener.deactivate();
        }
    }

}
// mark-end::body

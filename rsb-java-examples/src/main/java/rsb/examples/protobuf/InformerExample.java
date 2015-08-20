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
import rsb.Factory;
import rsb.Informer;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.examples.protobuf.ImageMessage.SimpleImage;
import rsb.examples.protobuf.ImageMessage.SimpleImage.Builder;

import com.google.protobuf.ByteString;

public class InformerExample {

    public static void main(final String[] args) throws Throwable {

        // See RegistrationExample.java.
        final ProtocolBufferConverter<SimpleImage> converter = new ProtocolBufferConverter<SimpleImage>(
                SimpleImage.getDefaultInstance());

        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(converter);

        // Create an informer on scope "/example/protobuf" to send
        // SimpleImage data.
        final Factory factory = Factory.getInstance();
        final Informer<SimpleImage> informer = factory
                .createInformer("/example/protobuf");
        informer.activate();

        // Send a SimpleImage datum..
        try {
            final Builder img = SimpleImage.newBuilder();
            img.setHeight(100);
            img.setWidth(100);
            final byte[] bytes = new byte[100 * 100];
            final ByteString bs = ByteString.copyFrom(bytes);
            img.setData(bs);
            informer.publish(img.build());
        } finally {
            informer.deactivate();
        }

    }

}
// mark-end::body

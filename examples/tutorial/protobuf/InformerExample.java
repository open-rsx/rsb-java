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

// mark-start::body
package tutorial.protobuf;

import com.google.protobuf.ByteString;

import rsb.Factory;
import rsb.Informer;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;

import tutorial.protobuf.ImageMessage.SimpleImage;
import tutorial.protobuf.ImageMessage.SimpleImage.Builder;

public class InformerExample {

    public static void main(String[] args) throws Throwable {

        // See RegistrationExample.java.
        ProtocolBufferConverter<SimpleImage> converter
            = new ProtocolBufferConverter<SimpleImage>(SimpleImage.getDefaultInstance());

        DefaultConverterRepository.getDefaultConverterRepository().addConverter(converter);

        // Create an informer on scope "/example/protobuf" to send
        // SimpleImage data.
        Factory factory = Factory.getInstance();
        Informer<SimpleImage> informer = factory.createInformer("/example/protobuf");
        informer.activate();

        // Send a SimpleImage datum..
        try {
            Builder img = SimpleImage.newBuilder();
            img.setHeight(100);
            img.setWidth(100);
            byte[] bytes = new byte[100*100];
            ByteString bs = ByteString.copyFrom(bytes);
            img.setData(bs);
            informer.send(img.build());
        } finally {
            informer.deactivate();
        }

    }

}
// mark-end::body

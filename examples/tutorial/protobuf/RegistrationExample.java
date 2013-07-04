/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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

import rsb.Factory;

import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;

import tutorial.protobuf.ImageMessage.SimpleImage;

public class RegistrationExample {

    public static void main(String[] args) throws Throwable {

        // Instantiate generic ProtocolBufferConverter with
        // SimpleImage exemplar.
        ProtocolBufferConverter<SimpleImage> converter
            = new ProtocolBufferConverter<SimpleImage>(SimpleImage.getDefaultInstance());

        // Register converter for the SimpleImage type.
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(converter);

        // The factory now uses the modified set of converters.
        @SuppressWarnings("unused")
        Factory factory = Factory.getInstance();

    }

}
// mark-end::body

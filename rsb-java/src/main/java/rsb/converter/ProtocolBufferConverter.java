/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
package rsb.converter;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import rsb.util.ByteHelpers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

/**
 * Converter for protocol buffer generated messages.
 *
 * @author jmoringe
 *
 * @param <MessageType>
 *            the protocol buffer type to convert
 */
public class ProtocolBufferConverter<MessageType extends Message> implements
        Converter<ByteBuffer> {

    private static final Logger LOG = Logger
            .getLogger(ProtocolBufferConverter.class.getName());

    private final MessageType defaultInstance;
    private final ConverterSignature signature;

    /**
     * Constructor.
     *
     * @param instance
     *            provide a default instance for the type to convert
     */
    public ProtocolBufferConverter(final MessageType instance) {
        this.defaultInstance = instance;
        LOG.fine("Result of instance.getClass().getName() "
                + instance.getClass().getName());
        this.signature =
                new ConverterSignature(this.getWireSchema(),
                        instance.getClass());
    }

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object obj) throws ConversionException {
        @SuppressWarnings("unchecked")
        final ByteBuffer serialized =
                ByteBuffer.wrap(((MessageType) obj).toByteArray());
        return new WireContents<ByteBuffer>(serialized, this.getWireSchema());
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserData<MessageType> deserialize(final String wireSchema,
            final ByteBuffer buffer) throws ConversionException {
        assert wireSchema.contentEquals(this.getWireSchema());

        MessageType result;
        try {
            result =
                    (MessageType) this.defaultInstance.newBuilderForType()
                            .mergeFrom(ByteHelpers.byteBufferToArray(buffer))
                            .build();
        } catch (final InvalidProtocolBufferException e) {
            throw new ConversionException(
                    "Error deserializing wire data because of a protobuf problem.",
                    e);
        }
        return new UserData<MessageType>(result, result.getClass());
    }

    private String getWireSchema() {
        LOG.fine("Detected wire type: "
                + this.defaultInstance.getDescriptorForType().getFullName());
        return "." + this.defaultInstance.getDescriptorForType().getFullName();
    }

    @Override
    public ConverterSignature getSignature() {
        return this.signature;
    }

}

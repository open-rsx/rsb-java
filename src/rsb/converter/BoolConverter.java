/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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

/**
 * A converter with wire type {@link ByteBuffer} that is capable of handling
 * boolean values.
 * 
 * @author jmoringe
 */
public class BoolConverter implements Converter<ByteBuffer> {

    private final ConverterSignature signature;

    public BoolConverter() {
        this.signature = new ConverterSignature("bool", Boolean.class);
    }

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object data) throws ConversionException {

        try {
            final Boolean value = (Boolean) data;
            final byte[] backing = new byte[1];
            backing[0] = (byte) (value ? 1 : 0);
            final ByteBuffer serialized = ByteBuffer.wrap(backing);
            return new WireContents<ByteBuffer>(serialized,
                    this.signature.getSchema());
        } catch (final ClassCastException e) {
            throw new ConversionException(
                    "Input data for serializing must be boolean values.", e);
        }
    }

    @Override
    public UserData<ByteBuffer> deserialize(final String wireSchema,
            final ByteBuffer bytes) throws ConversionException {

        if (!wireSchema.equals(this.signature.getSchema())) {
            throw new ConversionException("Unexpected wire schema '"
                    + wireSchema + "', expected '" + this.signature.getSchema()
                    + "'.");
        }

        final boolean result = (bytes.get(0) == 1);
        return new UserData<ByteBuffer>(result, Boolean.class);
    }

    @Override
    public ConverterSignature getSignature() {
        return this.signature;
    }
}

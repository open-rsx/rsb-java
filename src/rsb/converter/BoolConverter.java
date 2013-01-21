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
 * A converter with wire type {@link ByteBuffer} that is capable of
 * handling boolean values.
 *
 * @author jmoringe
 */
public class BoolConverter implements Converter<ByteBuffer> {

    private ConverterSignature signature;

    public BoolConverter() {
        signature = new ConverterSignature("bool", Boolean.class);
    }

    @Override
    public WireContents<ByteBuffer> serialize(Class<?> typeInfo, Object data)
        throws ConversionException {

        try {
            Boolean value = (Boolean) data;
            byte[] backing = new byte[1];
            backing[0] = (byte) (value ? 1 : 0);
            ByteBuffer serialized =  ByteBuffer.wrap(backing);
            return new WireContents<ByteBuffer>(serialized, signature.getSchema());
        } catch (ClassCastException e) {
            throw new ConversionException("Input data for serializing must be boolean values.", e);
        }
    }

    @Override
    public UserData<ByteBuffer> deserialize(String wireSchema, ByteBuffer bytes)
        throws ConversionException {

        if (!wireSchema.equals(signature.getSchema())) {
            throw new ConversionException("Unexpected wire schema '"
                                          + wireSchema + "', expected '"
                                          + signature.getSchema() + "'.");
        }

        boolean result = (bytes.get(0) == 1);
        return new UserData<ByteBuffer>(result, Boolean.class);
    }

    @Override
    public ConverterSignature getSignature() {
        return signature;
    }
}

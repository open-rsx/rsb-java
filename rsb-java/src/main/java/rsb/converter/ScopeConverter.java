/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 CoR-Lab, Bielefeld University
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

import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;


import rsb.Scope;

/**
 * A converter for {@link rsb.Scope} objects.
 *
 * @author jmoringe
 */
public class ScopeConverter implements Converter<ByteBuffer> {

    private final ConverterSignature signature;
    private final CachedCharsetCoding coding;

    /**
     * Creates a new converter for encoding {@link rsb.Scope} objects
     * with scope wire-schema.
     */
    public ScopeConverter() {
        this.signature = new ConverterSignature("scope", Scope.class);
        this.coding = new CachedCharsetCoding(Charset.forName("US-ASCII"));
    }

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object obj) throws ConversionException {

        try {

            final Scope scope = (Scope) obj;
            final ByteBuffer serialized =
                    this.coding.getEncoder().encode(CharBuffer.wrap(scope.toString()));
            return new WireContents<ByteBuffer>(serialized,
                    this.signature.getSchema());

        } catch (final ClassCastException e) {
            throw new ConversionException(
                    "Input data for serializing must be scopes.", e);
        } catch (final CharacterCodingException e) {
            throw new ConversionException(
                    "Error serializing scope because of a charset problem.",
                    e);
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

        try {

            final Scope scope =
                    new Scope(this.coding.getDecoder().decode(bytes).toString());
            return new UserData<ByteBuffer>(scope, this.signature.getDataType());

        } catch (final CharacterCodingException e) {
            throw new ConversionException(
                    "Error deserializing wire data because of a charset problem.",
                    e);
        }

    }

    @Override
    public ConverterSignature getSignature() {
        return this.signature;
    }

}

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
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * A converter with wire type {@link ByteBuffer} that is capable of handling
 * strings with different encodings.
 * 
 * @author swrede
 * @author jwienke
 */
public class StringConverter implements Converter<ByteBuffer> {

    private ConverterSignature signature;
    private ThreadLocal<CharsetEncoder> encoder;
    private ThreadLocal<CharsetDecoder> decoder;

    /**
     * Creates a converter for UTF-8 encoding with utf-8-string wire schema.
     */
    public StringConverter() {
        this("UTF-8", "utf-8-string");
    }

    /**
     * Creates a converter that uses the specified encoding for strings.
     * 
     * @param encoding
     *            encoding name for the data
     * @param wireSchema
     *            wire schema of the serialized data
     * @throws IllegalArgumentException
     *             invalid encoding name
     */
    public StringConverter(final String encoding, final String wireSchema) {

        if (!Charset.isSupported(encoding)) {
            throw new IllegalArgumentException("Encoding '" + encoding
                    + "' is not supported.");
        }

        this.init(Charset.forName(encoding), wireSchema);

    }

    /**
     * Creates a converter that uses the specified charset for strings.
     * 
     * @param charset
     *            encoding for the data
     * @param wireSchema
     *            wire schema of the serialized data
     */
    public StringConverter(final Charset charset, final String wireSchema) {
        this.init(charset, wireSchema);
    }

    private void init(final Charset charset, final String wireSchema) {

        // TODO replace by Java class object for type info
        this.signature = new ConverterSignature(wireSchema, String.class);

        this.encoder = new ThreadLocal<CharsetEncoder>() {

            @Override
            protected CharsetEncoder initialValue() {
                final CharsetEncoder encoder = charset.newEncoder();
                encoder.onMalformedInput(CodingErrorAction.REPORT);
                encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
                return encoder;
            }

        };
        this.decoder = new ThreadLocal<CharsetDecoder>() {

            @Override
            protected CharsetDecoder initialValue() {
                final CharsetDecoder decoder = charset.newDecoder();
                decoder.onMalformedInput(CodingErrorAction.REPORT);
                decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
                return decoder;
            }

        };

    }

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object data) throws ConversionException {

        try {

            // TODO handle null values

            final String string = (String) data;

            final ByteBuffer serialized = this.encoder.get().encode(
                    CharBuffer.wrap(string));
            return new WireContents<ByteBuffer>(serialized,
                    this.signature.getSchema());

        } catch (final ClassCastException e) {
            throw new ConversionException(
                    "Input data for serializing must be strings.", e);
        } catch (final CharacterCodingException e) {
            throw new ConversionException(
                    "Error serializing input data because of a charset problem.",
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

            final String string = this.decoder.get().decode(bytes).toString();
            return new UserData<ByteBuffer>(string, String.class);

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

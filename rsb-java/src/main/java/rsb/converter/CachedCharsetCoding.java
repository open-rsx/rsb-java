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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * Lazily creates and caches a {@link java.nio.charset.CharsetEncoder}, {@link
 * java.nio.charset.CharsetDecoder} pair for each calling thread.
 *
 * @author jmoringe
 */
public class CachedCharsetCoding {

    private final ThreadLocal<CharsetEncoder> encoder;
    private final ThreadLocal<CharsetDecoder> decoder;

    /**
     * Creates a per-thread cache for encoder-decoder pairs for the
     * given character set.
     *
     * @param charset
     *            The character set that should be used for en- and
     *            decoding.
     */
    public CachedCharsetCoding(final Charset charset) {
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

    /**
     * Returns the encoder for the current thread (potentially after
     * creating it).
     *
     * @return The encoder.
     */
    public CharsetEncoder getEncoder() {
        return this.encoder.get();
    }

    /**
     * Returns the decoder for the current thread (potentially after
     * creating it).
     *
     * @return The decoder.
     */
    public CharsetDecoder getDecoder() {
        return this.decoder.get();
    }

}

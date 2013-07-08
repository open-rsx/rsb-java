/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

/**
 * An exception indicating that no {@link Converter} is available for the
 * intended operation.
 * 
 * @author swrede
 */
public class NoSuchConverterException extends RuntimeException {

    private static final long serialVersionUID = 4919272894409736639L;

    /**
     * Constructs an exception with empty message and causing exception.
     */
    public NoSuchConverterException() {
        super();
    }

    /**
     * Constructs an exception with explanatory message and causing exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            causing exception
     */
    public NoSuchConverterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an exception with explanatory message.
     * 
     * @param message
     *            the message
     */
    public NoSuchConverterException(final String message) {
        super(message);
    }

    /**
     * Constructs an exception with causing exception.
     * 
     * @param cause
     *            causing exception
     */
    public NoSuchConverterException(final Throwable cause) {
        super(cause);
    }

}

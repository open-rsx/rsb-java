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
package rsb.patterns;

import java.lang.Throwable;

/**
 * Implementations of this interface are used to provide the behavior
 * of exposed methods.
 *
 * @author jmoringe
 */
public interface DataCallback<T, U> {

    /**
     * This method is called to invoke the actual behavior of an
     * exposed method.
     *
     * @param U The argument passed to the associated method by the
     * remote caller.
     * @return A result that should be returned to the remote caller
     * as the result of the calling the method.
     * @throw Throwable Can throw anything.
     */
    public T invoke(U request) throws Throwable;
};

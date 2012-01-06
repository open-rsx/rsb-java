/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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
package rsb;

/**
 * @author swrede
 */
public interface RSBObject {

	/**
	 * Activates all network resources that belong to a specific object.
	 */
	public void activate() throws RSBException;

	/**
	 * Deactivate all network resources that are owned by a specific object in
	 * order to reactivate it.
	 */
	public void deactivate() throws RSBException;
	
	/**
	 * Tells wether this class is currently active or not.
	 * 
	 * @return <code>true</code> if active
	 */
	boolean isActive();

}

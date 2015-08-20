/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb.introspection;

import rsb.util.os.HostInfo;

/**
 * Facade class around a {@link HostInfo} instance that ensures that there is
 * always a host id by replacing it with the host name, if required.
 *
 * @author jwienke
 */
public class HostIdEnsuringHostInfo implements HostInfo {

    private final HostInfo info;

    /**
     * Creates a new instance wrapping the provided {@link HostInfo} object.
     *
     * @param info
     *            the info object to wrap, not <code>null</code> and
     *            {@link HostInfo#getHostName()} must return a value.
     */
    public HostIdEnsuringHostInfo(final HostInfo info) {
        assert info != null;
        if (info.getHostId() == null && info.getHostName() == null) {
            throw new IllegalArgumentException(
                    "Unable to ensure that there is always a host id "
                            + "since the wrapped object does not "
                            + "contain a host name as well.");
        }
        this.info = info;
    }

    @Override
    public String getHostId() {
        if (this.info.getHostId() == null) {
            return this.info.getHostName();
        } else {
            return this.info.getHostId();
        }
    }

    @Override
    public String getHostName() {
        return this.info.getHostName();
    }

    @Override
    public String getSoftwareType() {
        return this.info.getSoftwareType();
    }

    @Override
    public String getMachineType() {
        return this.info.getMachineType();
    }

}

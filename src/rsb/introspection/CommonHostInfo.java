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

import rsb.util.OsUtilities;
import rsb.util.OsUtilities.MachineType;
import rsb.util.OsUtilities.OsFamily;

/**
 * Encapsulates common host info functionality shared between portable and
 * non-portable subclasses. This class contains variables and getters and
 * setters for all required attributes of {@link HostInfo}.
 *
 * @author swrede
 */
public abstract class CommonHostInfo implements HostInfo {

    private String hostId;
    private String hostName;
    private OsFamily softwareType;
    private MachineType machineType;

    /**
     * Creates a new instance and pre-computes values for
     * {@link HostInfo#getSoftwareType()} and {@link HostInfo#getMachineType()}.
     */
    public CommonHostInfo() {
        this.setSoftwareType(OsUtilities.deriveOsFamily(OsUtilities.getOsName()));
        this.setMachineType(OsUtilities.deriveMachineType(OsUtilities
                .getOsArchitecture()));
    }

    @Override
    public String getHostId() {
        return this.hostId;
    }

    /**
     * Sets the unique host id variable.
     *
     * @param hostId
     *            unique id
     */
    protected void setId(final String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Sets the host name variable.
     *
     * @param hostName
     *            the host name
     */
    protected void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    @Override
    public OsFamily getSoftwareType() {
        return this.softwareType;
    }

    /**
     * Sets the operating system family variable.
     *
     * @param softwareType
     *            the operating system family
     */
    protected void setSoftwareType(final OsFamily softwareType) {
        this.softwareType = softwareType;
    }

    @Override
    public MachineType getMachineType() {
        return this.machineType;
    }

    /**
     * Sets the bit architecture variable.
     *
     * @param machineType
     *            the bit architecture
     */
    protected void setMachineType(final MachineType machineType) {
        this.machineType = machineType;
    }

}

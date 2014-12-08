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

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates common process info functionality shared between
 * portable and non-portable subclasses.
 *
 * @author swrede
 */
public abstract class CommonProcessInfo implements ProcessInfo {

    // process id
    protected int pid;
    protected String name;
    protected List<String> arguments = new ArrayList<String>();
    protected String userName;

    // TODO add documentation
    long startTime;

    public CommonProcessInfo() {
        this.userName = readUserName();
    }

    private String readUserName() {
        String userName = System.getProperty("user.name");
        if (userName.isEmpty()) {
            userName = "unknown";
        }
        return userName;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getPid()
     */
    @Override
    public int getPid() {
        return this.pid;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getProgramName()
     */
    @Override
    public String getProgramName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getArguments()
     */
    @Override
    public List<String> getArguments() {
        return this.arguments;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getStartTime()
     */
    @Override
    public long getStartTime() {
        return this.startTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see rsb.introspection.ProcessInfo#getUserName()
     */
    @Override
    public String getUserName() {
        return this.userName;
    }

}

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
 * Encapsulates common process info functionality shared between portable and
 * non-portable subclasses.
 *
 * @author swrede
 */
public abstract class CommonProcessInfo implements ProcessInfo {

    private int pid;
    private String programName;
    private List<String> arguments = new ArrayList<String>();
    private String userName;
    private long startTime;

    /**
     * Creates a new instance and sets a common value for
     * {@link ProcessInfo#getUserName()}.
     */
    public CommonProcessInfo() {
        this.setUserName(readUserName());
    }

    private String readUserName() {
        final String userName = System.getProperty("user.name");
        if (userName.isEmpty()) {
            return "unknown";
        }
        return userName;
    }

    @Override
    public int getPid() {
        return this.pid;
    }

    /**
     * Sets the pid variable.
     *
     * @param pid
     *            the pid
     */
    protected void setPid(final int pid) {
        this.pid = pid;
    }

    @Override
    public String getProgramName() {
        return this.programName;
    }

    /**
     * Sets the program name.
     *
     * @param programName
     *            the program name
     */
    protected void setProgramName(final String programName) {
        this.programName = programName;
    }

    @Override
    public List<String> getArguments() {
        assert this.arguments != null;
        return this.arguments;
    }

    /**
     * Sets the command line arguments.
     *
     * @param arguments
     *            the arguments
     */
    protected void setArguments(final List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Sets the process start time.
     *
     * @param startTime
     *            the process start time in microseconds
     */
    protected void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets the user name variable.
     *
     * @param userName
     *            user name
     */
    protected void setUserName(final String userName) {
        this.userName = userName;
    }

}
